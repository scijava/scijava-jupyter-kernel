/* 
 * Copyright 2017 Hadrien Mary.
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
package org.scijava.jupyter.kernel.handler;

import static com.twosigma.beaker.jupyter.msg.JupyterMessages.KERNEL_INFO_REPLY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.twosigma.jupyter.KernelFunctionality;
import com.twosigma.jupyter.handler.KernelHandler;
import com.twosigma.jupyter.message.Header;
import com.twosigma.jupyter.message.Message;
import java.util.List;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaKernelInfoHandler extends KernelHandler<Message> {

    private final ScriptLanguage scriptLanguage;

    public ScijavaKernelInfoHandler(KernelFunctionality kernel, ScriptLanguage scriptLanguage) {
        super(kernel);
        this.scriptLanguage = scriptLanguage;
    }

    @Override
    public void handle(Message message) {

        Message reply = new Message();

        HashMap<String, Serializable> map = new HashMap<>(6);
        map.put("protocol_version", "5.0");
        map.put("implementation", this.scriptLanguage.getEngineName());
        map.put("implementation_version", this.scriptLanguage.getEngineVersion());

        HashMap<String, Serializable> map1 = new HashMap<>(7);
        map1.put("name", this.scriptLanguage.getLanguageName());
        map1.put("version", this.scriptLanguage.getLanguageVersion());
        map1.put("mimetype", this.scriptLanguage.getMimeTypes().toString());
        map1.put("file_extension", this.scriptLanguage.getExtensions().toString());
        map1.put("pygments_lexer", this.scriptLanguage.getLanguageName());
        map1.put("codemirror_mode", this.scriptLanguage.getLanguageName());
        map1.put("nbconverter_exporter", "");

        map.put("language_info", map1);
        String banner = "SciJava Jupyter Kernel v" +  getClass().getPackage().getSpecificationVersion() + " | ";
        banner += "Language : " + this.scriptLanguage.getLanguageName();
        banner += " " + this.scriptLanguage.getLanguageVersion() + "\n";
        map.put("banner", banner);
        map.put("beakerx", true);

        List<String> helpLinks = new ArrayList<>();
        helpLinks.add("https://imagej.net/Jupyter");
        helpLinks.add("https://github.com/hadim/scijava-jupyter-kernel");
        map.put("help_links", (Serializable) helpLinks);

        reply.setContent(map);
        reply.setHeader(new Header(KERNEL_INFO_REPLY, message.getHeader().getSession()));
        reply.setParentHeader(message.getHeader());
        reply.setIdentities(message.getIdentities());
        send(reply);
    }

}
