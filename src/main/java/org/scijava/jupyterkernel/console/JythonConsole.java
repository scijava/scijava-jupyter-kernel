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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Compilable;
import javax.script.ScriptException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.XML;
import org.scijava.jupyterkernel.json.messages.T_kernel_info_reply;
import org.scijava.jupyterkernel.json.messages.T_language_info;

/**
 *
 * @author kay schluehr
 */
public class JythonConsole extends InteractiveConsole {

    private final String compoundStmt = "(def|class|with|for|if|while|@|try|finally|except|else)(\n|.)*";

    public JythonConsole() {
        super("python");
        setDisplayhook();
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

    private void setDisplayhook() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResource("start.py").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sourceCode = sb.toString();
            try {
                engine.eval(sourceCode);
            } catch (ScriptException ex) {
                Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String readPrintExpr() {
        try {
            return (String) engine.eval("sys.displayhook.read()");
        } catch (ScriptException ex) {
            Logger.getLogger(JythonConsole.class
                    .getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    @Override
    public String getMIMEType() {
        if (ex != null) {
            return "text/html";
        }
        try {
            return (String) engine.eval("sys.displayhook.mimetype");
        } catch (ScriptException ex) {
            Logger.getLogger(JythonConsole.class
                    .getName()).log(Level.SEVERE, null, ex);
            return "text/plain";
        }
    }

    @Override
    public Object eval(String codeString) {
        ex = null;
        try {
            if (codeString.indexOf('\n') == -1) {                                
                engine.eval(String.format("exec(compile('''%s''', '<string>', 'single'), locals())", codeString));
                stopStreaming();
                return readPrintExpr();
            } else {
                // check that code compiles at all but don't evaluated it yet
                String c = String.format("__code = compile('''%s''', '<string>', 'exec')", codeString);
                engine.eval(c);

                // let's split the code string into two parts, a firstBlock which will be executed
                // in exec mode and a lastBlock which is executed in single mode. If the lastBlock
                // can be compiled in exec but not in single mode, just evaluate the complete code
                // in exec mode, otherwise execute the last block in single mode to produce expected
                // output
                String firstBlock;
                String lastBlock;
                int n = codeString.lastIndexOf('\n');
                while (true) {
                    firstBlock = n == -1 ? "" : codeString.substring(0, n);
                    lastBlock = codeString.substring(n + 1);

                    if (lastBlock.isEmpty() || lastBlock.matches("^(#|\\s)(\n|.)*")) {
                        // 'single' code shouldn't start with whithespace characters
                        n = codeString.lastIndexOf('\n', n - 1);
                        continue;
                    } else if (lastBlock.matches(compoundStmt)) {
                        // compound statement should be executed in 'exec' mode
                        // to support stdout streaming
                        break;
                    }
                    try {
                        if (!firstBlock.isEmpty()) {
                            ((Compilable) engine).compile(firstBlock);
                            ((Compilable) engine).compile(lastBlock);
                        }
                    } catch (ScriptException e1) {
                        // won't compile
                        n = codeString.lastIndexOf('\n', n - 1);
                        continue;
                    }
                    // o.k. we know now that both of the blocks compile but can we 
                    // compile lastBlock as a 'single' interactive statement?
                    if (!firstBlock.isEmpty()) {
                        engine.eval(String.format("exec(compile('''%s''', '<string>', 'exec'), locals())", firstBlock));
                    }
                    String code = "try:\n"
                            + "    __code = compile('''%s''', '<string>', 'single')\n"
                            + "except SyntaxError:\n"
                            + "    __code = compile('''%s''', '<string>', 'exec')\n"
                            + "exec(__code, locals())\n"
                            + "del(__code)";
                    code = String.format(code, lastBlock, lastBlock);
                                        
                    engine.eval(code);
                    stopStreaming();
                    return readPrintExpr();
                }

                // so only the whole codeString will execute. We already compiled it
                String code = "exec(__code, locals())\n"
                        + "del(__code)";
                engine.eval(code);
                stopStreaming();
                return readPrintExpr();
            }
        } catch (ScriptException e2) {
            stopStreaming();
            ex = e2;
            setErrorMessage();
        }
        return null;
    }

    @Override
    public String[] completion(String source, int cursor_position) {
        int n, n1 = cursor_position;
        while (true) {
            n = source.lastIndexOf('\n', n1);
            if (n >= 0 && n1 == n) {
                n1--;
            } else {
                break;
            }
        }
        completionCursorPosition = -1;
        source = source.substring(Math.max(0, n), cursor_position);
        if (source.endsWith(".")) {
            source = source.substring(0, source.length() - 1);
            cursor_position--;
        }
        // compile regex for dotted name, which matches the end of the line
        Pattern p = Pattern.compile("\\w+(?:\\.\\w+)*$");
        Matcher m = p.matcher(source);
        if (m.find()) {
            String dotted_name = m.group();
            completionCursorPosition = cursor_position - dotted_name.length();
            try {
                List res = (List) engine.eval(String.format("['%s.'+s for s in dir(%s)]", dotted_name, dotted_name));
                String[] completionList = new String[res.size()];
                res.toArray(completionList);
                return completionList;
            } catch (ScriptException ex) {
                // Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new String[]{};
    }

    @Override
    public T_kernel_info_reply getKernelInfo() {
        String version = "";
        T_kernel_info_reply kernelInfoReply = new T_kernel_info_reply();
        kernelInfoReply.implementation = "python";
        try {
            engine.eval("import sys");
            version = (String) engine.eval("'.'.join(map(str, sys.version_info[:3]))");
        } catch (ScriptException ex) {
            Logger.getLogger(JythonConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        kernelInfoReply.implementation_version = version;
        T_language_info languageInfo = new T_language_info();
        languageInfo.file_extension = ".py";
        languageInfo.name = "python";
        languageInfo.mimetype = "text/x-python";
        languageInfo.name = "python";
        if (version.startsWith("2")) {
            languageInfo.pygments_lexer = "python";
        } else {
            languageInfo.pygments_lexer = "py3";
        }
        languageInfo.version = version;
        kernelInfoReply.language_info = languageInfo;
        return kernelInfoReply;
    }
}
