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
import java.io.BufferedReader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.CompiledScript;
import javax.script.Compilable;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import org.scijava.jupyterkernel.json.messages.T_kernel_info_reply;

public class InteractiveConsole {

    ScriptEngine engine;
    ScriptException ex;

    ArrayList<CompiledScript> compiledChunks = new ArrayList<>();

    StringWriter stdoutWriter = new StringWriter();
    StringWriter stderrWriter = new StringWriter();

    int cellnum = 0;
    int completionCursorPosition = -1;

    public InteractiveConsole() {
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

    public InteractiveConsole(String kernel) {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName(kernel);
        if (engine == null) {
            getClasses();
            throw new RuntimeException("ScriptEngine not found. Please check your classpath.");
        }
        engine.getContext().setWriter(stdoutWriter);
        engine.getContext().setErrorWriter(stderrWriter);
    }

    public void setStdinReader(ConsoleInputReader reader) {
        this.engine.getContext().setReader(new BufferedReader(reader));
    }

    public void setStreamWriter(JupyterStreamWriter streamWriter) {
        this.engine.getContext().setWriter(streamWriter);
    }

    public void stopStreaming() {
        JupyterStreamWriter streamWriter = ((JupyterStreamWriter) this.engine.getContext().getWriter());
        if (streamWriter != null) {
            streamWriter.stopStreaming();            
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
        ex.printStackTrace(new PrintWriter(stderrWriter));
    }

    public String[] getTraceback() {
        return null;
    }

    /**
     *
     * @param codeString source code which is evaluted by the ScriptEngine
     * @return result of the evaluation
     *
     */
    public Object eval(String codeString) {
        CompiledScript compiledScript;
        ex = null;
        try {
            compiledScript = ((Compilable) engine).compile(codeString);
            return compiledScript.eval();
        } catch (ScriptException e) {
            ex = e;
            setErrorMessage();
        }
        return null;
    }

    public String readAndClearStdout() {
        String S = stdoutWriter.toString();
        stdoutWriter.flush();
        StringBuffer sb = stdoutWriter.getBuffer();
        sb.delete(0, sb.length());
        return S;
    }

    public String readAndClearStderr() {
        String S = stderrWriter.toString();
        stderrWriter.flush();
        StringBuffer sb = stderrWriter.getBuffer();
        sb.delete(0, sb.length());
        return S;
    }

    // language specific -- not implemented here
    public String[] completion(String source, int cursor_position) {
        return new String[]{};
    }

    public T_kernel_info_reply getKernelInfo() {
        return new T_kernel_info_reply();
    }
}
