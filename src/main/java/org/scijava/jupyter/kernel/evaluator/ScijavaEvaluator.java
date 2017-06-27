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

import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.Classpath;
import com.twosigma.beakerx.kernel.ImportPath;
import com.twosigma.beakerx.kernel.Imports;
import com.twosigma.beakerx.kernel.KernelParameters;
import com.twosigma.beakerx.kernel.PathToJar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.script.AutoCompleter;
import org.scijava.script.AutoCompletionResult;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;
import org.scijava.thread.ThreadService;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaEvaluator implements Evaluator {

	public static final String DEFAULT_LANGUAGE = "groovy";

	@Parameter
	private LogService log;

	@Parameter
	private transient ScriptService scriptService;

	@Parameter
	private ThreadService threadService;

	@Parameter
	Context context;

	private final Map<String, ScriptEngine> scriptEngines;
	private final Map<String, ScriptLanguage> scriptLanguages;
	private final Map<String, AutoCompleter> completers;
	private String languageName;

	protected String shellId;
	protected String sessionId;

	public ScijavaEvaluator(Context context, String shellId, String sessionId) {
		context.inject(this);

		this.shellId = shellId;
		this.sessionId = sessionId;

		this.scriptEngines = new HashMap<>();
		this.scriptLanguages = new HashMap<>();

		this.completers = new HashMap<>();

		this.languageName = DEFAULT_LANGUAGE;
	}

	@Override
	public void setShellOptions(KernelParameters kp) throws IOException {
		log.debug("Set shell options : " + kp);
	}

	@Override
	public AutocompleteResult autocomplete(String code, int index) {

		// Get only the line corresponding to the index.
		List<String> lines = Arrays.asList(code.substring(0, index).split("\n"));
		String line = lines.get(lines.size() - 1);

		// TODO: we need to find a way the language related to the current cell.
		// For now, we are just using the last used language.
		AutoCompleter completer = this.completers.get(this.languageName);
		ScriptEngine scriptEngine = this.scriptEngines.get(this.languageName);

		List<String> matches;
		int startIndex;
		if (completer != null) {
			AutoCompletionResult result = completer.autocomplete(line, index, scriptEngine);

			matches = (List<String>) result.getMatches();
			startIndex = index;

		} else {
			matches = new ArrayList<>();
			startIndex = 0;
		}

		// Reconstruct each matches with the correct index
		List<String> newMatches = new ArrayList<>();
		String newLine;
		for (String match : matches) {
			lines.set(lines.size() - 1, match);
			newLine = lines.stream().collect(Collectors.joining("\n"));
			newMatches.add(newLine.substring(startIndex, newLine.length()));
		}

		return new AutocompleteResult(newMatches, startIndex);
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

		code = this.setLanguage(code);

		Worker worker = new Worker(this.context, this.scriptEngines, this.scriptLanguages);
		worker.setup(seo, code, this.languageName);
		this.threadService.queue(getClass().getName(), worker);
	}

	@Override
	public void exit() {
		log.debug("Exiting DefaultEvaluator");
		// Ugly and not working :-(
		System.exit(0);
	}

	private void addLanguage(String langName) {

		if (scriptService.getLanguageByName(langName) == null) {
			log.error("Script Language for '" + langName + "' not found.");
			System.exit(1);
		}

		if (!this.scriptLanguages.keySet().contains(langName)) {

			Bindings bindings = null;
			if (!this.scriptEngines.isEmpty()) {
				String firstLanguage = this.scriptEngines.keySet().iterator().next();
				bindings = this.scriptEngines.get(firstLanguage).getBindings(ScriptContext.ENGINE_SCOPE);
			}

			log.info("Script Language for '" + langName + "' found.");
			ScriptLanguage scriptLanguage = scriptService.getLanguageByName(langName);
			this.scriptLanguages.put(langName, scriptLanguage);

			ScriptEngine engine = this.scriptLanguages.get(langName).getScriptEngine();
			this.scriptEngines.put(langName, engine);

			AutoCompleter completer = scriptLanguage.getAutoCompleter();
			this.completers.put(languageName, completer);

			// Not implemented yet
			//engine.setBindings(this.bindings, ScriptContext.ENGINE_SCOPE);
			if (bindings != null) {
				this.initBindings(bindings, engine, scriptLanguage);
			}

		}

		log.debug("Script Language found for '" + langName + "'");
	}

	private String setLanguage(String code) {

		if (code.startsWith("#!")) {

			// If code is composed of multiple lines
			if (code.split("\n").length > 1) {
				this.languageName = code.substring(2, code.indexOf("\n")).trim();

				// Return the code string without the first line
				code = code.substring(code.indexOf("\n") + 1);
			} // If only one line
			else {
				this.languageName = code.substring(2).trim();

				code = "";
			}

		}

		this.addLanguage(this.languageName);
		return code;
	}

	private void initBindings(Bindings bindings, ScriptEngine scriptEngine, ScriptLanguage scriptLanguage) {

		Bindings currentBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.keySet().forEach((String key) -> {
			currentBindings.put(key, scriptLanguage.decode(bindings.get(key)));
		});

	}

	@Override
	public void addJarToClasspath(PathToJar ptj) {
		log.debug("addJarToClasspath()");
	}

	@Override
	public Classpath getClasspath() {
		log.debug("addJarToClasspath()");
		return null;
	}

	@Override
	public Imports getImports() {
		log.debug("addJarToClasspath()");
		return null;
	}

	@Override
	public void addImport(ImportPath ip) {
		log.debug("addJarToClasspath()");
	}

	@Override
	public void resetEnvironment() {
		log.debug("addJarToClasspath()");
	}

	@Override
	public void removeImport(ImportPath ip) {
		log.debug("addJarToClasspath()");
	}

}
