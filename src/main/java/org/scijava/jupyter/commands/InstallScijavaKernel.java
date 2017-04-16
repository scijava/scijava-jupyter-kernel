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
package org.scijava.jupyter.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;
import org.scijava.util.ArrayUtils;

@Plugin(type = Command.class, menu = {
    @Menu(label = "Analyze")
    ,
	@Menu(label = "Jupyter Kernel")
    ,
	@Menu(label = "Install Scijava Kernel")})
public class InstallScijavaKernel implements Command {

    @Parameter
    private LogService log;

    @Parameter
    private transient ScriptService scriptService;

    @Parameter(required = true, label = "Python binary")
    private File pythonBinaryPath;

    @Parameter(required = true, label = "Script Language")
    private String scriptLanguage = "jython";

    @Parameter(required = true, label = "Log Level",
            choices = {"debug", "error", "info", "none"})
    private String logLevel = "info";

    @Override
    public void run() {

        if (!this.pythonBinaryPath.isFile()) {
            log.error(this.pythonBinaryPath + " does not exist.");
        }

        Process proc = null;
        BufferedReader stdInput = null;
        String s = null;
        String output = null;

        // Check binary is a valid Python executable
        try {
            proc = Runtime.getRuntime().exec(this.pythonBinaryPath + " --version");
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            s = null;
            output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            if (!output.contains("Python")) {
                log.error(this.pythonBinaryPath + " does not seem to be a valid Python executable.");
                return;
            }
            proc.getOutputStream().flush();
        } catch (IOException ex) {
            log.error(ex);
            return;
        }
        log.info("Python found.");

        // Check Jupyter is installed
        try {
            proc = Runtime.getRuntime().exec(this.pythonBinaryPath + " -c \"import jupyter\"");
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            s = null;
            output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            if (output.contains("ModuleNotFoundError")) {
                log.error("Jupyter does not seems to be installed.");
                return;
            }
            proc.getOutputStream().flush();
        } catch (IOException ex) {
            log.error(ex);
            return;
        }
        log.info("Jupyter found.");

        // Check the language is available
        if (scriptService.getLanguageByName(this.scriptLanguage) == null) {
            log.error("Script Language for '" + this.scriptLanguage + "' not found.");
            return;
        }
        log.info("Language '" + this.scriptLanguage + "' found.");

        // Try to remove old similar kernel
        try {
            Runtime.getRuntime().exec(this.pythonBinaryPath + " -m jupyter kernelspec remove -f scijava-" + this.scriptLanguage);
        } catch (IOException ex) {
            log.error(ex);
            return;
        }

        // Create the new kernel
        Path kernelDir = Paths.get(System.getProperty("java.io.tmpdir"), "scijava-" + this.scriptLanguage);
        kernelDir.toFile().delete();
        kernelDir.toFile().mkdir();
        Path kernelJSONPath = Paths.get(kernelDir.toString(), "kernel.json");

        JSONObject root = new JSONObject();
        root.put("language", this.scriptLanguage);
        root.put("display_name", "Scijava - " + this.scriptLanguage);

        JSONArray argv = new JSONArray();
        argv.add(this.getJavaBinary());
        argv.add("-classpath");
        argv.add(this.getClassPaths());
        argv.add("org.scijava.jupyter.kernel.DefaultKernel");
        argv.add("-language");
        argv.add(this.scriptLanguage);
        argv.add("-verbose");
        argv.add(this.logLevel);
        argv.add("-configFile");
        argv.add("{connection_file}");
        root.put("argv", argv);

        try (FileWriter file = new FileWriter(kernelJSONPath.toFile())) {
            file.write(root.toJSONString());
            log.info("kernel.json file : \n" + root.toJSONString());
        } catch (IOException ex) {
            log.error(ex);
        }

        log.info("Temporary kernel files created in " + kernelDir.toString());

        // Install the new kernel
        try {
            String[] cmd = new String[]{this.pythonBinaryPath.getAbsolutePath(), "-m", "jupyter", "kernelspec", "install", "--user", kernelDir.toString()};
            proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();

            stdInput = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            s = null;
            output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            if (output.toLowerCase().contains("error")) {
                log.error("Error during kernel installation.");
                return;
            }

        } catch (IOException ex) {
            log.error(ex);
            return;
        } catch (InterruptedException ex) {
            Logger.getLogger(InstallScijavaKernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Kernel installed.");

        // Clean temp dir
        log.info("Clean temporary files.");
        kernelJSONPath.toFile().delete();
        kernelDir.toFile().delete();
    }

    public String getJavaBinary() {
        String jvm_location;
        if (System.getProperty("os.name").startsWith("Win")) {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
        } else {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
        return jvm_location;
    }

    public String getClassPaths() {
        String classPaths = "";

        classPaths += Paths.get(System.getProperty("imagej.dir"), "jars", "*") + ":";
        classPaths += Paths.get(System.getProperty("imagej.dir"), "jars", "bio-formats", "*") + ":";
        classPaths += Paths.get(System.getProperty("imagej.dir"), "plugins", "*") + ":";

        return classPaths;
    }

}
