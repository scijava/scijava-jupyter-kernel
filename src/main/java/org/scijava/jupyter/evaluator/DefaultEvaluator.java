/* 
 * Copyright 2017 Hadrien Mary.
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
package org.scijava.jupyter.evaluator;

import com.twosigma.beaker.autocomplete.AutocompleteResult;
import com.twosigma.beaker.evaluator.Evaluator;
import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;
import com.twosigma.jupyter.KernelParameters;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;

import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.module.ModuleException;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;
import org.scijava.thread.ThreadService;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultEvaluator implements Evaluator {

    @Parameter
    private LogService log;

    @Parameter
    private transient ScriptService scriptService;

    @Parameter
    private ThreadService threadService;

    @Parameter
    Context context;

    private String languageUsed;
    private ScriptLanguage scriptLanguage;
    private ScriptEngine scriptEngine;

    private Worker worker = null;

    protected String shellId;
    protected String sessionId;

    public DefaultEvaluator(Context context, String shellId, String sessionId, String languageName) {
        context.inject(this);

        this.shellId = shellId;
        this.sessionId = sessionId;

        this.setLanguage(languageName);
    }

    @Override
    public void setShellOptions(KernelParameters kp) throws IOException {
        log.debug("Set shell options : " + kp);
    }

    @Override
    public AutocompleteResult autocomplete(String code, int i) {
        List<String> matches = new ArrayList<>();
        matches.add("Autocompletion does not work yet.");
        int startIndex = 0;
        AutocompleteResult ac = new AutocompleteResult(matches, startIndex);
        return ac;
    }

    @Override
    public void killAllThreads() {
        log.debug("Kill All Threads");
        // Ugly and not working :-(
        System.exit(0);
    }

    @Override
    public void evaluate(SimpleEvaluationObject seo, String code) {
        this.worker.setup(seo, code);
        this.threadService.run(this.worker);
    }

    @Override
    public void startWorker() {
        log.debug("Start worker");
        this.worker = new Worker(this.context, this.scriptEngine, this.scriptLanguage);
    }

    @Override
    public void exit() {
        log.debug("Exiting DefaultEvaluator");
        // Ugly and not working :-(
        System.exit(0);
    }

    private void setLanguage(String languageName) {

        if (scriptService.getLanguageByName(languageName) == null) {
            log.error("Script Language for '" + languageName + "' not found.");
            System.exit(1);
        }

        this.languageUsed = languageName;
        this.scriptLanguage = scriptService.getLanguageByName(languageName);
        this.scriptEngine = this.scriptLanguage.getScriptEngine();

        log.debug("Script Language found for '" + this.languageUsed + "'");
    }

    public ScriptLanguage getScriptLanguage() {
        return this.scriptLanguage;
    }

    public String getLanguage() {
        return this.languageUsed;
    }

    public class Worker implements Runnable {

        @Parameter
        private LogService log;

        @Parameter
        private Context context;

        @Parameter
        private ConvertService conversionService;

        ScriptEngine engine;
        ScriptLanguage scriptLanguage;

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
            final String path = info.getPath();

            this.seo.setOutputHandler();

            try {

                ScriptModule module = info.createModule();
                context.inject(module);

                module.setLanguage(scriptLanguage);
                this.engine.put(ScriptEngine.FILENAME, path);
                this.engine.put(ScriptModule.class.getName(), module);

                // Populate input annotation values
                for (final ModuleItem<?> item : info.inputs()) {
                    final String name = item.getName();
                    this.engine.put(name, module.getInput(name));
                }

                // Execute the code
                Object returnValue = null;
                try {
                    returnValue = this.engine.eval(info.getReader());
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

                // Populate output annotation values
                for (final ModuleItem<?> item : info.outputs()) {
                    final String name = item.getName();
                    final Object value;
                    if ("result".equals(name) && info.isReturnValueAppended()) {
                        // NB: This is the special implicit return value output!
                        value = returnValue;
                    } else {
                        value = this.engine.get(name);
                    }
                    //final Object decoded = this.engine.decode(value);
                    //final Object typed = conversionService.convert(decoded, item.getType());
                    //module.setOutput(name, typed);
                }

            } catch (ModuleException ex) {
                log.error(ex);
            }

            this.seo.clrOutputHandler();

        }

    }
}
