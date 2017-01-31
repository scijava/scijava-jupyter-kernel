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

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.lang.StringBuilder;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.scijava.jupyterkernel.json.messages.T_JSON;
import org.scijava.jupyterkernel.json.messages.T_header;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZFrame;
import org.scijava.jupyterkernel.json.messages.T_message;
import org.scijava.jupyterkernel.util.UUID;

/**
 *
 * @author kay schluehr
 *
 * The MessageObject bundles a message together with a socket. This allows to
 * use a socket for a request/reply pair without handling a registry of messages
 *
 */
public class MessageObject {
    
    final String[] supportedProtocolVersions = {"5.0"};
    final String delimiter  = "<IDS|MSG>";
    final byte[] bDelimiter = delimiter.getBytes();

    /* Expresses message parts according to the wire protocol
     *
     * http://jupyter-client.readthedocs.org/en/latest/messaging.html#the-wire-protocol
     * 
     b'u-u-i-d',         # zmq identity(ies)
     b'<IDS|MSG>',       # delimiter
     b'baddad42',        # HMAC signature
     b'{header}',        # serialized header dict
     b'{parent_header}', # serialized parent header dict
     b'{metadata}',      # serialized metadata dict
     b'{content}',       # serialized content dict
     b'blob',            # extra raw data buffer(s)
     */
    class MessageParts {

        public static final int UUID = 0;
        public static final int DELIM = 1;
        public static final int HMAC = 2;
        public static final int HEADER = 3;
        public static final int PARENT = 4;
        public static final int METADATA = 5;
        public static final int CONTENT = 6;
        public static final int BLOB = 7;
    }        

    public T_message msg;
    // The socket from which the message was received.
    public Socket socket;
    // zmq uuid
    byte[] uuid;
    // HMAC key
    byte[] key;
    
    ZMsg zmsg;

    public MessageObject(ZMsg zmsg, Socket socket, byte[] key) {
        this.socket = socket;
        this.key = key;
        this.zmsg = zmsg;
        this.msg = new T_message();
    }
    
    public MessageObject(MessageObject other) {
        this.socket = other.socket;
        this.key = other.key;
        this.zmsg = other.zmsg;
        this.msg = (T_message)other.msg.clone();        
    }
    
    private void checkAllowedProtocolVersion(String protocol)
    {
        for(String version: supportedProtocolVersions)
        {
            if(version.equals(protocol))
                return;
        }
        throw new RuntimeException("[jupyter-kernel] Protocol version "+protocol+"not supported by this kernel");
    }
    
        
    private byte[] computeSignature(byte[] header, 
                                    byte[] parent, 
                                    byte[] meta, 
                                    byte[] content) {
        byte[][] data = {header, parent, meta, content};
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            for (int i = 0; i < 4; i++) {
                mac.update(data[i]);
            }
            return mac.doFinal();

        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void mildlySecureMACCompare(byte[] mac1, byte[] mac2) {
        boolean hmacValid = true;
        if (mac1.length != mac2.length) {
            hmacValid = false;
        } else {
            // use full loop and don't break at error
            for (int i = 0; i < mac1.length; i++) {
                if (mac1[i] != mac2[i]) {
                    hmacValid = false;
                }
            }
        }
        if (!hmacValid) {
            throw new RuntimeException("[jupyter-kernel.jar] HMAC verification failed");
        }
    }

    public void read() {
        try {
            ZFrame[] zframes = new ZFrame[zmsg.size()];
            zmsg.toArray(zframes);
            if (zmsg.size() < 7) {
                throw new RuntimeException("[jupyter-kernel.jar] Message incomplete. Didn't receive required message parts");
            }
            uuid = zframes[MessageParts.UUID].getData();
            String delim = new String(zframes[MessageParts.DELIM].getData(), 
                                      StandardCharsets.UTF_8);
            if (!delim.equals(delimiter)) {
                throw new RuntimeException("[jupyter-kernel.jar] Incorrectly formatted message. Delimiter <IDS|MSG> not found");
            }                        
            byte[] header = zframes[MessageParts.HEADER].getData();
            byte[] parent = zframes[MessageParts.PARENT].getData();
            byte[] meta = zframes[MessageParts.METADATA].getData();
            byte[] content = zframes[MessageParts.CONTENT].getData();

            byte[] digest = computeSignature(header, parent, meta, content);
            byte[] hmac = zframes[MessageParts.HMAC].getData();
            // hmac is an UTF-8 string and has to be converted into a byte array first
            hmac = DatatypeConverter.parseHexBinary(new String(hmac));
            
            mildlySecureMACCompare(digest, hmac);
            
            JSONObject jsonHeader = new JSONObject(new String(header, StandardCharsets.UTF_8));
            if(null == T_JSON.message_protocol_version)
            {
                String protocolVersion = (String)jsonHeader.get("version");
                checkAllowedProtocolVersion(protocolVersion);
                // set protocol version for protocol specific serialization / deserialization
                T_JSON.setProtocolVersion(protocolVersion);
            }
            msg.header = (T_header)T_JSON.fromJSON("T_header", jsonHeader);                    
            msg.parent_header = (T_header)T_JSON.fromJSON("T_header", 
                    new JSONObject(new String(parent, StandardCharsets.UTF_8)));
            msg.metadata = new JSONObject(new String(meta, StandardCharsets.UTF_8));
            msg.content = T_JSON.fromJSON("T_"+msg.header.msg_type, 
                    new JSONObject(new String(content, StandardCharsets.UTF_8)));
            
        } finally {
            zmsg.destroy();
        }
    }
    
    public void send() {  
        msg.header.msg_id = UUID.newID();
        JSONObject jsonMsg = msg.toJSON();        
        ZMsg newZmsg = new ZMsg();
        newZmsg.add(uuid);
        newZmsg.add(bDelimiter);
        byte[] header  = jsonMsg.getJSONObject("header").toString().getBytes();
        byte[] parent  = jsonMsg.getJSONObject("parent_header").toString().getBytes();        
        byte[] meta    = jsonMsg.getJSONObject("metadata").toString().getBytes();
        byte[] content = jsonMsg.getJSONObject("content").toString().getBytes();
        byte[] digest  = computeSignature(header, parent, meta, content);
        digest = DatatypeConverter.printHexBinary(digest).toLowerCase().getBytes();
        newZmsg.add(digest);
        newZmsg.add(header);
        newZmsg.add(parent);
        newZmsg.add(meta);
        newZmsg.add(content);        
        newZmsg.send(socket);                
    }

}
