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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.scijava.jupyterkernel.console.InteractiveConsole;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONArray;

import org.json.JSONObject;
import org.scijava.jupyterkernel.console.ConsoleFactory;
import org.scijava.jupyterkernel.console.ConsoleInputReader;
import org.scijava.jupyterkernel.console.JupyterStreamWriter;
import org.scijava.jupyterkernel.json.messages.T_comm_close;
import org.scijava.jupyterkernel.json.messages.T_comm_info_reply;
import org.scijava.jupyterkernel.json.messages.T_comm_open;
import org.scijava.jupyterkernel.json.messages.T_complete_reply;
import org.scijava.jupyterkernel.json.messages.T_complete_request;
import org.scijava.jupyterkernel.json.messages.T_connect_reply;
import org.scijava.jupyterkernel.json.messages.T_execute_reply;
import org.scijava.jupyterkernel.json.messages.T_execute_reply_err;
import org.scijava.jupyterkernel.json.messages.T_execute_reply_ok;
import org.scijava.jupyterkernel.json.messages.T_execute_request;
import org.scijava.jupyterkernel.json.messages.T_execute_result;
import org.scijava.jupyterkernel.json.messages.T_header;
import org.scijava.jupyterkernel.json.messages.T_history_reply;
import org.scijava.jupyterkernel.json.messages.T_input_request;
import org.scijava.jupyterkernel.json.messages.T_inspect_reply;
import org.scijava.jupyterkernel.json.messages.T_is_complete_reply;
import org.scijava.jupyterkernel.json.messages.T_kernel_info_reply;
import org.scijava.jupyterkernel.json.messages.T_status;
import org.scijava.jupyterkernel.json.messages.T_message;
import org.scijava.jupyterkernel.json.messages.T_shutdown_request;
import org.scijava.jupyterkernel.json.messages.T_stream;

/**
 *
 * @author kay schluehr
 *
 *
 */
public class Kernel extends Thread {

    public static boolean DEBUG = false;

    // TODO: this class is not currently used because I have no model for 
    //       evaluating cells aynchronously. I might take a look on how 
    //       Mathematica does it with multiple kernels but it doesn't look too
    //       promising either
    // Note : this class is used.
    private class ExecuteRequestHandler extends Thread {

        final ArrayDeque<MessageObject> requestMessages = new ArrayDeque<>();

        public synchronized void addMessage(MessageObject message) {
            requestMessages.addLast(message);
        }

        @Override
        public void run() {
            while (!requestMessages.isEmpty()) {
                synchronized (requestMessages) {

                    MessageObject message = requestMessages.pollFirst();
                    T_header parentHeader = (T_header) message.msg.header.clone();

                    // Warn the frontend execution is starting
                    sendKernelStatus("busy", message.msg.header);

                    // Execute the request
                    MessageObject[] responseMessages = execute_request(message);

                    // Send all the response messages
                    for (MessageObject response : responseMessages) {
                        logInfo("Message Sent : " + response.msg.header.msg_type);
                        response.msg.parent_header = parentHeader;
                        response.send();
                        response = null;
                    }

                    // Warn the frontend, that kernel is done
                    sendKernelStatus("idle", message.msg.header);
                }
            }
        }
    }

    ScriptEngineManager manager;
    ScriptEngine engine;
    InteractiveConsole console;
    String kernel;

    JSONObject connectionData;

    int execution_count = 0;

    ConsoleInputReader stdinReader = null;

    boolean shutdown_requested = false;
    boolean restart_requested = false;

    // message templates which can be used as copy constructor arguments
    MessageObject stdinTemplate;
    MessageObject iopubTemplate;

    ExecuteRequestHandler execute_request_handler = new ExecuteRequestHandler();

    public Kernel(String name) {
        kernel = name;
        console = ConsoleFactory.createConsole(name);
    }

    public String getKernel() {
        return kernel;
    }

    public boolean isShutdownRequested() {
        return shutdown_requested;
    }

    public boolean isRestartRequested() {
        return shutdown_requested;
    }

    public void setStdinTemplate(MessageObject messageObject) {
        this.stdinTemplate = messageObject;
    }

    public void setIOPubTemplate(MessageObject messageObject) {
        this.iopubTemplate = messageObject;
    }

    public void setConnectionData(JSONObject connectionData) {
        this.connectionData = connectionData;
    }

    public void dispatch(MessageObject message) {
        MessageObject[] responseMessages = {};
        message.read();
        T_header header = message.msg.header;
        String msgType = header.msg_type;

        logInfo("Message Received : " + msgType);

        // handle execute_request as a special case
        if (msgType.equals("execute_request")) {

            if (!execute_request_handler.isAlive()) {
                execute_request_handler = new ExecuteRequestHandler();
            }

            execute_request_handler.addMessage(message);
            execute_request_handler.start();

        } else {

            T_header parentHeader = (T_header) header.clone();
            Method[] methods = this.getClass().getMethods();

            for (Method m : methods) {
                if (m.getName().equals(msgType)) {
                    try {
                        responseMessages = (MessageObject[]) m.invoke(this, message);
                        break;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            if (responseMessages.length == 0) {
                throw new RuntimeException("Message handler not implemented for message type '" + msgType + "'");
            }
            // now send all messages in sequence 
            for (MessageObject response : responseMessages) {
                response.msg.parent_header = parentHeader;
                response.send();
            }
        }
    }

    private void setStdin(MessageObject message) {
        if (stdinReader == null) {
            T_input_request stdin = new T_input_request();
            MessageObject stdinMsg = new MessageObject(stdinTemplate);
            stdinMsg.msg = new T_message();
            stdinMsg.msg.header = (T_header) message.msg.header.clone();
            stdinMsg.msg.header.msg_type = "input_request";
            stdinMsg.msg.parent_header = (T_header) message.msg.header;
            stdinMsg.msg.content = stdin;
            console.setStdinReader(new ConsoleInputReader(stdinMsg));
        }
    }

    private void setStreamWriter(MessageObject message) {
        T_stream stdout = new T_stream();
        MessageObject stdoutMsg = new MessageObject(iopubTemplate);
        stdoutMsg.msg = new T_message();
        stdoutMsg.msg.header = (T_header) message.msg.header.clone();
        stdoutMsg.msg.header.msg_type = "stream";
        stdoutMsg.msg.parent_header = (T_header) message.msg.header.clone();
        stdoutMsg.msg.content = stdout;
        console.setStreamWriter(new JupyterStreamWriter(stdoutMsg));
    }

    public MessageObject[] comm_open(MessageObject message) {
        // TODO: figure out if you can do anything with comms
        //       Currently the reply to a comm_open will just be comm_close.
        T_comm_open commOpen = (T_comm_open) message.msg.content;
        T_comm_close commClose = new T_comm_close();
        commClose.comm_id = commOpen.comm_id;
        message.msg.content = commClose;
        message.msg.header.msg_type = "comm_close";
        return new MessageObject[]{message};
    }

    public MessageObject[] kernel_info_request(MessageObject message) {
        // TODO: this is odd. A 'kernel interrupt' in the notebook just leads to a 
        //       kernel_info_request message. 

        if (this.execute_request_handler.isAlive()) {
            this.execute_request_handler.interrupt();
        }

        T_kernel_info_reply content = console.getKernelInfo();
        content.banner += "The kernel is the Java JSR223 kernel from https://github.com/hadim/jupyter-kernel-jsr223.\n";
        content.banner += "It is still an experimental project so please report any issue you might have.";

        message.msg.content = content;
        message.msg.header.msg_type = "kernel_info_reply";
        return new MessageObject[]{message};
    }

    public MessageObject[] execute_request(MessageObject message) {
        T_execute_request request = (T_execute_request) message.msg.content;
        if (request.store_history) {
            execution_count++;
            console.setCellNumber(execution_count);
        }

        String code = request.code;

        setStreamWriter(message);
        setStdin(message);

        // TODO: what can be done with obj? Check an expression oriented language
        //       such as Clojure if obj must be formatted.
        Object obj = console.eval(code);

        String res = console.readAndClearStdout();
        String err = console.readAndClearStderr();

        if (obj != null) {
            res = obj.toString();
        }

        // just return a simple reply message
        T_execute_reply reply = new T_execute_reply();
        reply.execution_count = execution_count;
        message.msg.content = reply;
        message.msg.header.msg_type = "execute_reply";

        if (res.length() == 0 && err.length() == 0) {
            reply.status = "ok";
            reply.setAnswer(new T_execute_reply_ok());
            return new MessageObject[]{message};
        } else {
            T_execute_result result = new T_execute_result();

            // TODO: what is responded when history disabled?        
            result.execution_count = execution_count;
            String mimetype = console.getMIMEType();
            if (!err.isEmpty()) {
                reply.status = "error";
                reply.setAnswer(new T_execute_reply_err());
                result.data.put(mimetype, err);
            } else {
                reply.status = "ok";
                reply.setAnswer(new T_execute_reply_ok());
                result.data.put(mimetype, res);
            }

            MessageObject iopubMsg = new MessageObject(iopubTemplate);

            iopubMsg.msg = new T_message();
            iopubMsg.msg.header = (T_header) message.msg.header.clone();
            iopubMsg.msg.header.msg_type = "execute_result";
            iopubMsg.msg.content = result;

            return new MessageObject[]{message, iopubMsg};
        }
    }

    public MessageObject[] shutdown_request(MessageObject message) {
        T_shutdown_request request = (T_shutdown_request) message.msg.content;
        shutdown_requested = true;
        restart_requested = request.restart;
        message.msg.header.msg_type = "shutdown_reply";
        return new MessageObject[]{message};
    }

    public MessageObject[] complete_request(MessageObject message) {
        T_complete_request request = (T_complete_request) message.msg.content;
        String[] matches = console.completion(request.code, request.cursor_pos);
        T_complete_reply reply = new T_complete_reply();
        reply.matches = new JSONArray(matches);
        reply.cursor_start = console.getCompletionCursorPosition();
        reply.cursor_end = request.cursor_pos;
        message.msg.content = reply;
        message.msg.header.msg_type = "complete_reply";
        return new MessageObject[]{message};
    }

    public MessageObject[] connect_request(MessageObject message) {
        message.msg.header.msg_type = "connect_reply";
        T_connect_reply reply = new T_connect_reply();
        reply.hb_port = connectionData.getInt("hb_port");
        reply.iopub_port = connectionData.getInt("iopub_port");
        reply.shell_port = connectionData.getInt("shell_port");
        reply.stdin_port = connectionData.getInt("stdin_port");
        message.msg.content = reply;
        return new MessageObject[]{message};
    }

    // TODO: the history implementation of the notebook client
    //       does fine. I guess this implementation would be for
    //       other clients.
    public MessageObject[] history_request(MessageObject message) {
        message.msg.header.msg_type = "history_reply";
        message.msg.content = new T_history_reply();
        return new MessageObject[]{message};
    }

    // TODO: is there any notebook application logic connected 
    //       to this message? 
    public MessageObject[] inspect_request(MessageObject message) {
        message.msg.header.msg_type = "inspect_reply";
        message.msg.content = new T_inspect_reply();
        return new MessageObject[]{message};
    }

    public MessageObject[] input_reply(MessageObject message) {

        return new MessageObject[]{};
    }

    public MessageObject[] is_complete_request(MessageObject message) {
        message.msg.header.msg_type = "is_complete_reply";
        T_is_complete_reply reply = new T_is_complete_reply();
        reply.status = "complete";
        message.msg.content = reply;
        return new MessageObject[]{message};
    }

    public MessageObject[] comm_info_request(MessageObject message) {
        message.msg.header.msg_type = "comm_info_reply";
        T_comm_info_reply reply = new T_comm_info_reply();
        message.msg.content = reply;
        return new MessageObject[]{message};
    }

    private void sendKernelStatus(String status, T_header header) {
        MessageObject statusMessage = new MessageObject(iopubTemplate);

        statusMessage.msg.parent_header = header;
        statusMessage.msg.header = (T_header) header.clone();
        statusMessage.msg.header.msg_type = "status";

        T_status statusContent = new T_status();
        statusContent.execution_state = status;

        statusMessage.msg.content = statusContent;
        statusMessage.send();
        statusMessage = null;
    }

    private static void logInfo(String message) {
        if (DEBUG) {
            Logger.getLogger(Kernel.class.getName()).log(Level.INFO, message);
        }
    }
}
