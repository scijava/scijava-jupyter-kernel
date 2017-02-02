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
package org.scijava.jupyterkernel.console.deprecated;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import org.json.XML;
import org.scijava.jupyterkernel.console.InteractiveConsole;
import org.scijava.jupyterkernel.json.messages.T_kernel_info_reply;
import org.scijava.jupyterkernel.json.messages.T_language_info;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author kay schluehr
 */
public class ClojureConsole extends InteractiveConsole {

    public ClojureConsole(ScriptLanguage scriptLanguage) {
        super(scriptLanguage);
    }

    @Override
    public String getMIMEType() {
        if (ex != null) {
            return "text/html";
        }
        return "text/plain";
    }

    @Override
    protected void setErrorMessage() {
        StringWriter sw = new StringWriter();
        ex.getCause().printStackTrace(new PrintWriter(sw));
        String err = sw.toString();
        String[] tb = err.split("\n\n");
        String traceback;
        if (tb.length <= 2) {
            traceback = XML.escape(tb[0]);
        } else {
            traceback = XML.escape(tb[0] + "\n" + tb[1]);
        }
        //traceback = traceback.replaceAll("\n", "<br>");
        stderrWriter.write("<pre><font color=\"red\">" + traceback + "</font></pre>");
    }

    /**
     *
     * @param codeString source code which is evaluated by the ScriptEngine
     * @return result of the evaluation
     *
     */
    @Override
    public Object eval(String codeString) {
        Object res = null;
        ex = null;
        try {
            res = engine.eval(codeString);
        } catch (ScriptException e) {
            ex = e;
            setErrorMessage();
        }
        stopStreaming();
        return res;
    }

    @Override
    public T_kernel_info_reply getKernelInfo() {
        String version = "";
        T_kernel_info_reply kernelInfoReply = new T_kernel_info_reply();
        kernelInfoReply.implementation = "clojure";
        try {
            version = (String) engine.eval("(clojure-version)");
        } catch (ScriptException ex) {
            Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        kernelInfoReply.implementation_version = version;
        T_language_info languageInfo = new T_language_info();
        languageInfo.file_extension = ".clj";
        languageInfo.name = "clojure";
        languageInfo.mimetype = "text/x-clojure";
        languageInfo.pygments_lexer = "clojure";
        languageInfo.version = version;
        kernelInfoReply.language_info = languageInfo;
        
        kernelInfoReply.banner = "Clojure " + version + "\n";
        
        return kernelInfoReply;
    }

}
