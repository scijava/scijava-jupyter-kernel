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

import org.scijava.jupyterkernel.console.deprecated.ClojureConsole;
import org.scijava.jupyterkernel.console.deprecated.GroovyConsole;
import org.scijava.jupyterkernel.console.deprecated.JythonConsole;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.python.util.PythonInterpreter;
import org.scijava.Context;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;

/**
 *
 * @author kay schluehr
 */
public class ConsoleFactory {

    public static InteractiveConsole createConsole(String name, Context context) throws Exception {

        ScriptService scriptService = context.getService(ScriptService.class);
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName(name);

        if (scriptLanguage == null) {
            throw new Exception("ScriptLanguage for " + name + " not found.");
        }

        InteractiveConsole console = new InteractiveConsole(scriptLanguage);
        return console;
    }

    public static void main(String[] args) throws ScriptException {
        // Only for testing purpose

        Context context = new Context();
        ScriptService scriptService = context.getService(ScriptService.class);
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName("Groovy");
        ScriptEngine engine = scriptLanguage.getScriptEngine();

        engine.eval("println 'Hello'");

        context.dispose();
    }

}
