/*
 * Copyright 2017 SciJava.
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
package org.scijava.jupyter.kernel.evaluator;

import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.script.ScriptEngine;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.module.ModuleException;
import org.scijava.module.ModuleItem;
import org.scijava.module.event.ModulePostprocessEvent;
import org.scijava.module.event.ModulePreprocessEvent;
import org.scijava.module.process.ModulePostprocessor;
import org.scijava.module.process.ModulePreprocessor;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;

public class Worker implements Runnable {
    
    @Parameter
    private LogService log;
    
    @Parameter
    private Context context;
    
    @Parameter
    private PluginService pluginService;
    
    @Parameter
    private EventService eventService;
    
    @Parameter
    private ConvertService convertService;
    
    ScriptLanguage scriptLanguage;
    ScriptEngine engine;
    
    SimpleEvaluationObject seo = null;
    String code = null;
    
    Worker(Context context, ScriptEngine engine, ScriptLanguage scriptLanguage) {
        context.inject(this);
        this.engine = engine;
        this.scriptLanguage = scriptLanguage;
    }
    
    public void setup(SimpleEvaluationObject seo, String code) {
        this.seo = seo;
        this.code = code;
    }
    
    @Override
    public void run() {
        
        final Reader input = new StringReader(this.code);
        
        ScriptInfo info = new ScriptInfo(context, "dummy.py", input);
        
        this.seo.setOutputHandler();
        
        try {
            
            ScriptModule module = info.createModule();
            context.inject(module);
            module.setLanguage(this.scriptLanguage);

            // Populate input annotation parameters
            this.preProcess(module);
            for (final ModuleItem<?> item : info.inputs()) {
                final String name = item.getName();
                engine.put(name, module.getInput(name));
            }

            // Execute the code
            Object returnValue = null;
            try {
                returnValue = engine.eval(info.getReader());
                this.seo.finished(returnValue);
                
            } catch (Throwable e) {
                
                if (e instanceof InvocationTargetException) {
                    e = ((InvocationTargetException) e).getTargetException();
                }
                
                if (e instanceof InterruptedException || e instanceof InvocationTargetException || e instanceof ThreadDeath) {
                    this.seo.error("Excecution canceled.");
                } else {
                    this.seo.error(e.getMessage());
                }
            }

            // Populate output annotation parameters
            for (final ModuleItem<?> item : info.outputs()) {
                final String name = item.getName();
                final Object value;
                if ("result".equals(name) && info.isReturnValueAppended()) {
                    // NB: This is the special implicit return value output!
                    value = returnValue;
                } else {
                    value = engine.get(name);
                }
                final Object decoded = scriptLanguage.decode(value);
                final Object typed = convertService.convert(decoded, item.getType());
                module.setOutput(name, typed);
            }
            this.postProcess(module);
            
        } catch (ModuleException ex) {
            log.error(ex);
        }
        
        this.seo.clrOutputHandler();
        
    }
    
    public ModulePreprocessor preProcess(ScriptModule module) {
        List<? extends PreprocessorPlugin> pre = pluginService.createInstancesOfType(PreprocessorPlugin.class);
        
        for (final ModulePreprocessor p : pre) {
            p.process(module);
            if (eventService != null) {
                eventService.publish(new ModulePreprocessEvent(module, p));
            }
            if (p.isCanceled()) {
                return p;
            }
        }
        return null;
    }
    
    public void postProcess(ScriptModule module) {
        List<? extends PostprocessorPlugin> post = pluginService.createInstancesOfType(PostprocessorPlugin.class);
        
        for (final ModulePostprocessor p : post) {
            p.process(module);
            if (eventService != null) {
                eventService.publish(new ModulePostprocessEvent(module, p));
            }
        }
    }
    
}
