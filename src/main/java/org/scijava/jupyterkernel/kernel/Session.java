/*
 * Copyright 2016 kay schluehr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scijava.jupyterkernel.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;
import org.zeromq.ZMsg;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLineParser;

/**
 *
 * @author kay schluehr
 */
public class Session extends Thread {

    public static boolean _DEBUG_ = false;

    private class SocketType {

        public static final int CONTROL = 0;
        public static final int DELIM = 1;
        public static final int HMAC = 2;
        public static final int HEADER = 3;
        public static final int PARENT = 4;
        public static final int METADATA = 5;
        public static final int CONTENT = 6;
        public static final int BLOB = 7;
    }

    // battery of sockets        
    Socket Control;
    Socket Heartbeat;
    Socket IOPub;
    Socket Shell;
    Socket Stdin;
    ZContext ctx;
    ZMQ.Poller sockets;

    // HMAC key 
    // NOTE: this doesn't even pretend to be secure, but then the connection
    //       file which is transmitted in plaintext isn't either. 
    byte[] key = null;

    // JSONObject which stores data from Jupyter connection file
    JSONObject connectionData;

    // Used kernel
    Kernel kernel;

    boolean restart_kernel_requested = false;

    public Session(JSONObject connectionData, Kernel kernel) throws InvalidKeyException,
            UnsupportedEncodingException {
        this.connectionData = connectionData;
        this.kernel = kernel;
        getKey();
    }

    void closeSockets() {
        try {
            if (Shell != null) {
                Shell.close();
            }
            if (Control != null) {
                Control.close();
            }
            if (IOPub != null) {
                IOPub.close();
            }
            if (Stdin != null) {
                Stdin.close();
            }
            if (Heartbeat != null) {
                Heartbeat.close();
            }
            ctx.close();
        } catch (Exception e) {
        }
    }

    private byte[] getKey() throws InvalidKeyException, UnsupportedEncodingException {
        if (key == null) {
            String sKey = (String) connectionData.get("key");
            /*
             if(sKey.length()!=32)
             { 
             throw new InvalidKeyException(sKey);
             } 
             */
            key = sKey.getBytes();
            return key;
        }
        return key;
    }

    boolean createSockets() {
        ctx = new ZContext();
        String ip = (String) connectionData.get("ip");
        String transport = (String) connectionData.get("transport");
        try {
            // http://jupyter-client.readthedocs.org/en/latest/messaging.html#heartbeat-for-kernels 
            //
            //     Clients send ping messages on a REQ socket, which are echoed right back from the Kernel’s 
            //     REP socket. These are simple bytestrings, not full JSON messages described above.
            Heartbeat = ctx.createSocket(ZMQ.REP);
            Heartbeat.bind(String.format("%s://%s:%s",
                    transport, ip, connectionData.get("hb_port")
            ));

            // http://jupyter-client.readthedocs.org/en/latest/messaging.html#introduction
            // Shell: this single ROUTER socket allows multiple incoming connections from frontends, and 
            //        this is the socket where requests for code execution, object information, prompts, etc. 
            //        are made to the kernel by any frontend. 
            String shellAddress = String.format("%s://%s:%s",
                    transport, ip, connectionData.get("shell_port"));
            Shell = ctx.createSocket(ZMQ.ROUTER);
            Shell.bind(shellAddress);

            // Control: This channel is identical to Shell, but operates on a separate socket, to allow 
            //          important messages to avoid queueing behind execution requests (e.g. shutdown or abort).
            Control = ctx.createSocket(ZMQ.ROUTER);
            Control.bind(String.format("%s://%s:%s",
                    transport, ip, connectionData.get("control_port")
            ));

            // IOPub: this socket is the ‘broadcast channel’ where the kernel publishes all side effects 
            //        (stdout, stderr, etc.) as well as the requests coming from any client over the shell socket 
            //        send its own requests on the stdin socket. 
            IOPub = ctx.createSocket(ZMQ.PUB);
            IOPub.bind(String.format("%s://%s:%s",
                    transport, ip, connectionData.get("iopub_port")
            ));

            Stdin = ctx.createSocket(ZMQ.ROUTER);
            Stdin.connect(String.format("%s://%s:%s",
                    transport, ip, connectionData.get("stdin_port")
            ));
        } catch (ZMQException e) {
            closeSockets();
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, e);
        }

        sockets = new ZMQ.Poller(4);
        sockets.register(Control, ZMQ.Poller.POLLIN);
        sockets.register(Heartbeat, ZMQ.Poller.POLLIN);
        sockets.register(Shell, ZMQ.Poller.POLLIN);
        sockets.register(Stdin, ZMQ.Poller.POLLIN);

        Logger.getLogger(Session.class.getName()).log(Level.INFO, "Sockets have been correctly created.");

        return true;
    }

    public static JSONObject readConnectionFile(String connectionFilePath) throws FileNotFoundException, IOException {
        if (Session._DEBUG_) {
            System.out.println("ConnectionFilePath: " + connectionFilePath);
        }
        File connectionFile = new File(connectionFilePath);
        FileInputStream fis = new FileInputStream(connectionFile);
        byte[] content = new byte[(int) connectionFile.length()];
        try {
            fis.read(content);
        } finally {
            fis.close();
        }

        // Parse connection file content into JSON object
        JSONObject connectionInfo = new JSONObject(new String(content, "UTF-8"));
        Logger.getLogger(Session.class.getName()).log(Level.INFO, "Reading connection file :\n" + connectionInfo.toString(2));
        return connectionInfo;
    }

    @Override
    public void run() {
        MessageObject msg;
        if (!createSockets()) {
            return;
        }
        try {
            kernel.setStdinTemplate(new MessageObject(null, Stdin, key));
            kernel.setIOPubTemplate(new MessageObject(null, IOPub, key));
            kernel.setConnectionData(connectionData);

            Logger.getLogger(Session.class.getName()).log(Level.INFO, "Jupyter Java Kernel has started.", kernel.getKernel());

            while (!this.isInterrupted()) {
                byte[] message;
                sockets.poll();
                if (sockets.pollin(0)) {
                    msg = new MessageObject(ZMsg.recvMsg(Control), Control, key);
                    kernel.dispatch(msg);
                }
                if (sockets.pollin(1)) {
                    message = Heartbeat.recv(0);
                    Heartbeat.send(message);
                }
                if (sockets.pollin(2)) {
                    msg = new MessageObject(ZMsg.recvMsg(Shell), Shell, key);
                    kernel.dispatch(msg);
                }
                if (sockets.pollin(3)) {
                    msg = new MessageObject(ZMsg.recvMsg(Stdin), Stdin, key);
                    kernel.dispatch(msg);
                }
                if (kernel.isShutdownRequested()) {
                    restart_kernel_requested = kernel.isRestartRequested();
                    break;
                }
            }
        } finally {
            closeSockets();
        }
    }

    static void runKernel(String[] args) throws FileNotFoundException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IOException {
        
        if (args.length > 0) {
            String connectionFilePath;
            String kernelName;
            Options options = new Options();
            options.addOption("f", true, "connection file path");
            options.addOption("k", true, "kernel name");
            CommandLineParser parser = new PosixParser();
            
            try {
                CommandLine cmd = parser.parse(options, args);
                connectionFilePath = cmd.getOptionValue("f");
                kernelName = cmd.getOptionValue("k");
            } catch (ParseException e) {
                e.printStackTrace(System.out);
                return;
            }

            JSONObject connectionData = readConnectionFile(connectionFilePath);
            if (Session._DEBUG_) {
                System.out.println("Connection File\n------------------------------------");
                System.out.println(connectionData.toString(4));
            }

            Session session = new Session(connectionData, new Kernel(kernelName));

            try {
                session.start();
                session.join();
            } catch (InterruptedException e1) {
            }
        }
    }

    public static void runKernelDebug() throws FileNotFoundException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IOException {
        Session._DEBUG_ = true;
        ZContext ctx = new ZContext();
        Socket channel = ctx.createSocket(ZMQ.REP);
        channel.bind("tcp://127.0.0.1:2222");
        byte[] msg = channel.recv();
        String sArgs = new String(msg, StandardCharsets.UTF_8);
        String[] newArgs = sArgs.split(" ");
        channel.send("ok");
        runKernel(newArgs);
    }

    public static void main(String[] args) throws FileNotFoundException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IOException {
        if (args == null || args.length == 0) {
            // Used for debugging the kernel. 
            // Start this application in your IDE first. 
            runKernelDebug();
        } else {
            if (Session._DEBUG_) {
                System.out.println("BEGIN ARGS");
                for (String arg : args) {
                    System.out.println("   " + arg);
                }
                System.out.println("END ARGS");
            }
            runKernel(args);
        }
    }
}
