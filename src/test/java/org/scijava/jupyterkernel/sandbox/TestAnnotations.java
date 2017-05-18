/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.sandbox;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import net.imagej.ImageJ;
import net.imagej.Main;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.ModulePreprocessor;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;

/**
 *
 * @author Hadrien Mary
 */
public class TestAnnotations {

    public static void main(final String[] args) throws ScriptException {
        ImageJ ij = Main.launch(args);
        
        String code = "#@LogService log\n#@ImageJ ij\nprint('log')\nprint('jjjj')";
        final Reader input = new StringReader(code);

        ScriptInfo info = new ScriptInfo(ij.context(), "dummy.py", input);
        final String path = info.getPath();

        List<? extends PreprocessorPlugin> pre = ij.plugin().createInstancesOfType(PreprocessorPlugin.class);
        List<? extends PostprocessorPlugin> post = ij.plugin().createInstancesOfType(PostprocessorPlugin.class);

        ScriptLanguage scriptLanguage = ij.script().getLanguageByName("jython");
        ScriptEngine scriptEngine = scriptLanguage.getScriptEngine();

        ScriptModule module;
        module = new ScriptModule(info);
        ij.context().inject(module);
        module.setLanguage(scriptLanguage);

        for (final ModulePreprocessor p : pre) {
            p.process(module);
        }

        ij.log().info(module);
        ij.log().info(module.getInputs());
        
        for (final ModuleItem<?> item : info.inputs()) {
            final String name = item.getName();
            ij.log().info(name);
            scriptEngine.put(name, module.getInput(name));
        }
        
        scriptEngine.eval(code);
        
        //ij.module().run(module, true);

    }

}
