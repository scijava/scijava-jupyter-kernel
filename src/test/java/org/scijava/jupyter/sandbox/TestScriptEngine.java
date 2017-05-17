/*-
 * #%L
 * SciJava polyglot kernel for Jupyter.
 * %%
 * Copyright (C) 2017 Hadrien Mary
 * %%
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
 * #L%
 */

package org.scijava.jupyter.sandbox;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.scijava.Context;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;

/**
 *
 * @author Hadrien Mary
 */
public class TestScriptEngine {

    public static void main(String[] args) throws ScriptException {
        // Only for testing purpose

        Context context = new Context();
        ScriptService scriptService = context.getService(ScriptService.class);
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName("python");
        ScriptEngine engine = scriptLanguage.getScriptEngine();

        Object result = engine.eval("p=999\n555");
        System.out.println(result);

        scriptService = context.getService(ScriptService.class);
        scriptLanguage = scriptService.getLanguageByName("python");
        engine = scriptLanguage.getScriptEngine();
        
        result = engine.eval("555");
        System.out.println(result);

        context.dispose();
    }

}
