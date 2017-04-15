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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.scijava.Context;
import org.scijava.log.LogService;
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
    private ScriptModule module;
    private ScriptInfo info;

    private Worker worker = null;

    protected String shellId;
    protected String sessionId;

    public DefaultEvaluator(Context context, String shellId, String sessionId, String languageName) {
        context.inject(this);

        this.shellId = shellId;
        this.sessionId = sessionId;

        this.setLanguage(languageName);
        this.initModule();
    }

    private void initModule() {

        // Init ScriptInfo
        this.info = new ScriptInfo(this.context, "dummy.py", new StringReader(""));

        // Init ScriptModule
        this.module = new ScriptModule(this.info);
        this.module.setLanguage(scriptLanguage);
    }

    @Override
    public void setShellOptions(KernelParameters kp) throws IOException {
        log.debug("Set shell options : " + kp);
    }

    @Override
    public AutocompleteResult autocomplete(String code, int i) {        
        List<String> matches = new ArrayList<>();
        matches.add("Autocompletion does not work yet.");
        matches.add("Test Autocompletion 1");
        matches.add("Test Autocompletion 2");
        matches.add("Test Autocompletion 3");
        int startIndex = 0;
        AutocompleteResult ac = new AutocompleteResult(matches, startIndex);
        return ac;
    }

    @Override
    public void killAllThreads() {
        log.debug("Kill All Threads");
        // Ugly !
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
        this.worker = new Worker(this.context, this.module);
    }

    @Override
    public void exit() {
        log.debug("Exiting DefaultEvaluator");
    }

    private void setLanguage(String languageName) {

        if (scriptService.getLanguageByName(languageName) == null) {
            log.error("Script Language for '" + languageName + "' not found.");
            System.exit(1);
        }

        this.languageUsed = languageName;
        this.scriptLanguage = scriptService.getLanguageByName(languageName);

        log.debug("Script Language found for '" + this.languageUsed + "'");
    }

    public String getLanguage() {
        return this.languageUsed;
    }

    public class Worker implements Runnable {

        @Parameter
        private LogService log;

        ScriptModule module;
        ScriptEngine engine;

        SimpleEvaluationObject seo = null;
        String code = null;

        Worker(Context context, ScriptModule module) {
            context.inject(this);
            this.module = module;
            this.engine = this.module.getEngine();
        }

        public void setup(SimpleEvaluationObject seo, String code) {
            this.seo = seo;
            this.code = code;
        }

        @Override
        public void run() {

            this.seo.setOutputHandler();

            try {                
                Object result = this.engine.eval(this.code);

                if (result != null) {
                    this.seo.finished(result);
                } else {
                    this.seo.finished("");
                }

            } catch (ScriptException ex) {
                log.debug("Error : " + ex);
                this.seo.error(ex);
                this.seo.finished("");
            }

            this.seo.clrOutputHandler();
        }
    }

}
