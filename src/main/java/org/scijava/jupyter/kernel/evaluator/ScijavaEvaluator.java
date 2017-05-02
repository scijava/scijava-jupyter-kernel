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
package org.scijava.jupyter.kernel.evaluator;

import com.twosigma.beaker.autocomplete.AutocompleteResult;
import com.twosigma.beaker.evaluator.Evaluator;
import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;
import com.twosigma.jupyter.KernelParameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;
import org.scijava.thread.ThreadService;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaEvaluator implements Evaluator {

    @Parameter
    private LogService log;

    @Parameter
    private transient ScriptService scriptService;

    @Parameter
    private ThreadService threadService;

    @Parameter
    Context context;

    private Map<String, ScriptEngine> scriptEngines;
    private Map<String, ScriptLanguage> scriptLanguages;

    protected String shellId;
    protected String sessionId;

    public ScijavaEvaluator(Context context, String shellId, String sessionId, String languageName) {
        context.inject(this);

        this.shellId = shellId;
        this.sessionId = sessionId;

        this.scriptEngines = new HashMap<>();
        this.scriptLanguages = new HashMap<>();
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
    public void startWorker() {
        // Nothing to do
    }

    @Override
    public void evaluate(SimpleEvaluationObject seo, String code) {

        String languageName = "groovy";
        this.addLanguage(languageName);

        Worker worker = new Worker(this.context,
                this.scriptEngines.get(languageName),
                this.scriptLanguages.get(languageName));

        worker.setup(seo, code);
        this.threadService.queue(worker);
    }

    @Override
    public void exit() {
        log.debug("Exiting DefaultEvaluator");
        // Ugly and not working :-(
        System.exit(0);
    }

    private void addLanguage(String languageName) {

        if (scriptService.getLanguageByName(languageName) == null) {
            log.error("Script Language for '" + languageName + "' not found.");
            System.exit(1);
        }

        if (!this.scriptLanguages.keySet().contains(languageName)) {
            this.scriptLanguages.put(languageName, scriptService.getLanguageByName(languageName));
        }
        
        if (!this.scriptEngines.keySet().contains(languageName)) {
            this.scriptEngines.put(languageName, this.scriptLanguages.get(languageName).getScriptEngine());
        }

        log.debug("Script Language found for '" + languageName + "'");
    }

}
