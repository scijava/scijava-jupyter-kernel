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

import java.io.IOException;
import java.io.StringReader;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptInfo;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;

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
    Context context;

    private String languageUsed;
    private ScriptLanguage scriptLanguage;
    private ScriptModule module;
    private ScriptInfo info;

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
    public void setShellOptions(String string, String string1) throws IOException {
        log.info("Set shell options : ");
        log.info("\t- " + string);
        log.info("\t- " + string1);
    }

    @Override
    public AutocompleteResult autocomplete(String string, int i) {
        log.info("Autocomplete is not (yet) available.");
        return null;
    }

    @Override
    public void killAllThreads() {
        log.info("Kill all threads.");
        // Ugly !
        System.exit(0);
    }

    @Override
    public void evaluate(SimpleEvaluationObject seo, String string) {
        log.info("Evaluate code\n\n");

        //seo.started();
        //seo.error("test error");
        //seo.update("test update");
        seo.finished("test fini!!!!");
    }

    @Override
    public void startWorker() {
        log.info("Start the worker");
    }

    @Override
    public void exit() {
        log.info("Exiting DefaultEvaluator");
    }

    private void setLanguage(String languageName) {

        if (scriptService.getLanguageByName(languageName) == null) {
            log.error("Script Language for '" + languageName + "' not found.");
            System.exit(1);
        }

        this.languageUsed = languageName;
        this.scriptLanguage = scriptService.getLanguageByName(languageName);

        log.info("Script Language found for '" + this.languageUsed + "'");
    }

    public String getLanguage() {
        return this.languageUsed;
    }

}
