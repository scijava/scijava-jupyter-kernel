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

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.imagej.ImageJ;

import org.scijava.module.ModuleItem;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;

/**
 *
 * @author Hadrien Mary
 */
public class TestGroovy {

    public static void main(String[] args) throws ScriptException {
        ImageJ ij = new ImageJ();
        ij.launch(args);

        String code = "";
        //code += "@Grab('org.springframework:spring-orm:3.2.5.RELEASE')\n";
        code += "import org.springframework.jdbc.core.JdbcTemplate\nprintln JdbcTemplate\n";
        code += "println 'test'";
        final Reader input = new StringReader(code);

        ScriptInfo info = new ScriptInfo(ij.context(), "dummy.py", input);
        final String path = info.getPath();

        List<? extends PreprocessorPlugin> pre = ij.plugin().createInstancesOfType(PreprocessorPlugin.class);

        ScriptLanguage scriptLanguage = ij.script().getLanguageByName("groovy");
        info.setLanguage(scriptLanguage);
        ScriptEngine scriptEngine = scriptLanguage.getScriptEngine();

        ScriptModule module;
        module = new ScriptModule(info);
        ij.context().inject(module);

        pre.forEach((p) -> {
            p.process(module);
        });
        
        for (final ModuleItem<?> item : info.inputs()) {
            final String name = item.getName();
            scriptEngine.put(name, module.getInput(name));
        }
        
        scriptEngine.eval(code);
        
        ij.context().dispose();
        
    }

}
