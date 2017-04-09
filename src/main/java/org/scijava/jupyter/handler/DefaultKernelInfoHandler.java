/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyter.handler;

import static com.twosigma.beaker.jupyter.msg.JupyterMessages.KERNEL_INFO_REPLY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.twosigma.jupyter.KernelFunctionality;
import com.twosigma.jupyter.handler.KernelHandler;
import com.twosigma.jupyter.message.Header;
import com.twosigma.jupyter.message.Message;
import java.util.List;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultKernelInfoHandler extends KernelHandler<Message> {

    public DefaultKernelInfoHandler(KernelFunctionality kernel) {
        super(kernel);
    }

    @Override
    public void handle(Message message) {

        Message reply = new Message();

        HashMap<String, Serializable> map = new HashMap<>(6);
        map.put("protocol_version", "5.0");
        map.put("implementation", "groovy");
        map.put("implementation_version", "1.0.0");

        HashMap<String, Serializable> map1 = new HashMap<>(7);
        map1.put("name", "Jython");
        map1.put("version", "2.7");
        map1.put("mimetype", "");
        map1.put("file_extension", ".py");
        map1.put("pygments_lexer", "python");
        map1.put("codemirror_mode", "python");
        map1.put("nbconverter_exporter", "");
        
        map.put("language_info", map1);
        map.put("banner", "SciJava Jupyter Kernel v0.1.0");
        map.put("beakerx", true);
        
        List<String> helpLinks = new ArrayList<>();
        helpLinks.add("http://imagej.net/Jupyter");
        helpLinks.add("https://github.com/hadim/scijava-jupyter-kernel");
        map.put("help_links", (Serializable) helpLinks);

        reply.setContent(map);
        reply.setHeader(new Header(KERNEL_INFO_REPLY, message.getHeader().getSession()));
        reply.setParentHeader(message.getHeader());
        reply.setIdentities(message.getIdentities());
        send(reply);
    }

}
