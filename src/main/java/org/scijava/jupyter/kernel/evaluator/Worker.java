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

package org.scijava.jupyter.kernel.evaluator;

import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.module.ModuleRunner;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;
import org.scijava.util.ClassUtils;

public class Worker implements Runnable {

    @Parameter
    private LogService log;

    @Parameter
    private Context context;

    @Parameter
    private PluginService pluginService;

    @Parameter
    private ConvertService convertService;

    private final Map<String, ScriptEngine> scriptEngines;
    private final Map<String, ScriptLanguage> scriptLanguages;
    private String languageName;

    SimpleEvaluationObject seo = null;
    String code = null;

    Worker(Context context, Map<String, ScriptEngine> scriptEngines, Map<String, ScriptLanguage> scriptLanguages) {
        context.inject(this);
        this.scriptEngines = scriptEngines;
        this.scriptLanguages = scriptLanguages;
    }

    public void setup(SimpleEvaluationObject seo, String code, String languageName) {
        this.seo = seo;
        this.code = code;
        this.languageName = languageName;
    }

    @Override
    public void run() {

        ScriptLanguage scriptLanguage = this.scriptLanguages.get(this.languageName);
        ScriptEngine scriptEngine = this.scriptEngines.get(this.languageName);

        final Reader input = new StringReader(this.code);
        final ScriptInfo info = new ScriptInfo(context, "dummy.py", input);
        this.seo.setOutputHandler();

        try {
            // create the ScriptModule instance
            final ScriptModule module = info.createModule();
            context.inject(module);
            module.setLanguage(scriptLanguage);

            // HACK: Inject our cached script engine instance, rather
            // than letting the ScriptModule instance create its own.
            final Field f = ClassUtils.getField(ScriptModule.class, "scriptEngine");
            ClassUtils.setValue(f, module, scriptEngine);

            // execute the code
            // NB: We do preprocessing, but not postprocessing, so
            // that the outputs do not get displayed by any rogue UI.
            // We will handle it ourselves here in the notebook!
            final List<PreprocessorPlugin> pre = pluginService.createInstancesOfType(PreprocessorPlugin.class);
            final ModuleRunner runner = new ModuleRunner(context, module, pre, null);
            runner.run();

            final Object returnValue = module.getOutput(ScriptModule.RETURN_VALUE);
            this.seo.finished(returnValue == null ? "null" : returnValue);

            this.syncBindings(scriptEngine, scriptLanguage);
        }
        catch (final ThreadDeath ex) {
            seo.error("Execution canceled");
            log.error(ex);
        }
        catch (final Throwable t) {
            seo.error(t.getMessage());
            log.error(t);
        }

        this.seo.clrOutputHandler();
        this.seo.executeCodeCallback();
    }

    private void syncBindings(ScriptEngine scriptEngine, ScriptLanguage scriptLanguage) {

        Bindings currentBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        this.scriptEngines.forEach((String name, ScriptEngine engine) -> {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            currentBindings.keySet().forEach((String key) -> {
                bindings.put(key, scriptLanguage.decode(currentBindings.get(key)));
            });
        });

    }

}
