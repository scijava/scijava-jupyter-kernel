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
package org.scijava.jupyterkernel.console;

/**
 *
 * @author kay schluehr
 *
 */

import javax.script.ScriptException;
import javax.script.CompiledScript;

import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import org.json.XML;
import org.scijava.Context;

import org.scijava.jupyterkernel.json.messages.T_kernel_info_reply;
import org.scijava.jupyterkernel.json.messages.T_language_info;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;

public class InteractiveConsole {

    protected ScriptException exception;

    protected ArrayList<CompiledScript> compiledChunks = new ArrayList<>();

    protected int cellnum = 0;
    protected int completionCursorPosition = -1;

    private final Context context;
    private final ScriptLanguage scriptLanguage;
    private ScriptModule module = null;
    private ScriptInfo info;

    private JupyterStreamWriter streamWriter;
    private StringWriter errorWriter;

    public InteractiveConsole(ScriptLanguage scriptLanguage, Context context) {
        this.context = context;
        this.scriptLanguage = scriptLanguage;

        this.initModule();
    }

    private void initModule() {

        this.setScriptInfo("");
        this.module = new ScriptModule(this.info);
        context.inject(module);
        this.module.setLanguage(scriptLanguage);

        this.module.setOutputWriter(new StringWriter());

        this.errorWriter = new StringWriter();
        this.module.setErrorWriter(this.errorWriter);
    }

    public void setScriptInfo(String script) {
        this.info = new ScriptInfo(context, "dummy.py", new StringReader(""));
        if (this.module != null) {
            this.module.setInfo(this.info);
        }
    }

    private void getClasses() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        String strClassPath = System.getProperty("java.class.path");

        System.out.println("Classpath is " + strClassPath);
    }

    public void setStdinReader(ConsoleInputReader reader) {
        this.info = new ScriptInfo(this.context, "dummy.py", reader);
        this.module.setInfo(this.info);
    }

    public void setStreamWriter(JupyterStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
        this.module.setOutputWriter(streamWriter);
    }

    public void stopStreaming() {
        if (this.streamWriter != null) {
            this.streamWriter.stopStreaming();
        }
    }

    public String getMIMEType() {
        return "text/plain";
    }

    public void setCellNumber(int cell) {
        cellnum = cell;
    }

    // used to handle complete_request message
    public int getCompletionCursorPosition() {
        return this.completionCursorPosition;
    }

    protected void setErrorMessage() {
        StringWriter sw = new StringWriter();
        if (this.exception != null) {
            this.exception.getCause().printStackTrace(new PrintWriter(sw));

            String err = sw.toString();
            String[] tb = err.split("\n\n");
            String traceback;

            if (tb.length <= 2) {
                traceback = XML.escape(tb[0]);
            } else {
                traceback = XML.escape(tb[0] + "\n" + tb[1]);
            }
            //traceback = traceback.replaceAll("\n", "<br>");
            //stderrWriter.write("<pre><font color=\"red\">" + traceback + "</font></pre>");

            // For now only return plain text traceback. 
            // TODO : find a way to send both html and text answer.
            this.errorWriter.write(traceback);
        }
    }

    public String[] getTraceback() {
        return null;
    }

    public Object eval(String codeString) {
        this.exception = null;

        this.setScriptInfo(codeString);
        this.module.run();
        this.setErrorMessage();
        this.stopStreaming();

        return this.module.getReturnValue();
    }

    public String readAndClearStdout() {
        String S = this.streamWriter.toString();
        this.streamWriter.flush();
        StringBuffer sb = this.streamWriter.getBuffer();
        sb.delete(0, sb.length());
        return S;
    }

    public String readAndClearStderr() {
        String S = this.errorWriter.toString();
        this.errorWriter.flush();
        StringBuffer sb = this.errorWriter.getBuffer();
        sb.delete(0, sb.length());
        return S;
    }

    // language specific -- not implemented here
    public String[] completion(String source, int cursor_position) {
        return new String[]{};
    }

    public T_kernel_info_reply getKernelInfo() {

        T_kernel_info_reply kernelInfoReply = new T_kernel_info_reply();

        String lowerCaseName = this.scriptLanguage.getLanguageName().toLowerCase();
        String version = this.scriptLanguage.getLanguageVersion();
        kernelInfoReply.implementation = lowerCaseName;
        kernelInfoReply.implementation_version = version;

        T_language_info languageInfo = new T_language_info();
        languageInfo.file_extension = this.scriptLanguage.getExtensions().toString();
        languageInfo.name = lowerCaseName;
        languageInfo.mimetype = this.scriptLanguage.getMimeTypes().toString();
        languageInfo.pygments_lexer = lowerCaseName;
        languageInfo.version = version;
        kernelInfoReply.language_info = languageInfo;

        kernelInfoReply.banner = this.scriptLanguage.getLanguageName() + " " + version + "\n";
        kernelInfoReply.banner += "The kernel is the SciJava JSR223 kernel from https://github.com/hadim/scijava-jupyter-kernel.\n";
        kernelInfoReply.banner += "It is still an experimental project so please report any issue you might have.";

        return kernelInfoReply;
    }
}
