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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.jupyter.utils.JupyterUtil;
import org.scijava.jupyter.utils.ProcessUtil;
import org.scijava.jupyter.utils.SystemUtil;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;

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

    @Parameter(type = ItemIO.OUTPUT)
    private String message;

    @Override
    public void run() {

        if (!this.pythonBinaryPath.isFile()) {
            log.error(this.pythonBinaryPath + " does not exist.");
        }

        String[] cmd = null;
        String sourceCode = null;
        Map<String, String> results = null;

        // Check binary is a valid Python executable
        cmd = new String[]{pythonBinaryPath.toString(), "--version"};
        results = ProcessUtil.executeProcess(cmd, log);
        if (!results.get("output").contains("Python")) {
            log.error(this.pythonBinaryPath + " does not seem to be a valid Python executable.");
            log.error("Output : " + results.get("output"));
            log.error("Error : " + results.get("error"));
            return;
        }
        log.info("Python found.");

        // Check Jupyter is installed
        sourceCode = "import jupyter";
        results = ProcessUtil.executePythonCode(this.pythonBinaryPath, sourceCode, log);
        if (results.get("error").contains("ModuleNotFoundError")) {
            log.error("Jupyter does not seems to be installed.");
            log.error("Output : " + results.get("output"));
            log.error("Error : " + results.get("error"));
            return;
        }
        log.info("Jupyter found.");

        // Check the language is available
        if (scriptService.getLanguageByName(this.scriptLanguage) == null) {
            log.error("Script Language for '" + this.scriptLanguage + "' not found.");
            return;
        }
        log.info("Language '" + this.scriptLanguage + "' found.");

        // Create the new kernel
        Path kernelDir = Paths.get(System.getProperty("java.io.tmpdir"), "scijava-" + this.scriptLanguage);
        SystemUtil.deleteFolderRecursively(kernelDir, log);
        kernelDir.toFile().mkdir();

        try {
            // Copy the logo
            Files.copy(this.getClass().getResourceAsStream("/logo-64x64.png"), Paths.get(kernelDir.toString(), "logo-64x64.png"));
            Files.copy(this.getClass().getResourceAsStream("/logo-32x32.png"), Paths.get(kernelDir.toString(), "logo-32x32.png"));
        } catch (IOException ex) {
            log.error(ex);
            return;
        }

        // Generate the kernel.json file
        String JSONString = JupyterUtil.createKernelJSON(this.scriptLanguage, this.logLevel);
        Path kernelJSONPath = Paths.get(kernelDir.toString(), "kernel.json");
        try (FileWriter file = new FileWriter(kernelJSONPath.toFile())) {
            file.write(JSONString);
            log.info("kernel.json file : \n" + JSONString);
        } catch (IOException ex) {
            log.error(ex);
            return;
        }
        log.info("Kernel generated.");

        // Install the new kernel
        sourceCode = "from jupyter_client.kernelspec import KernelSpecManager\n";
        sourceCode += "KernelSpecManager().install_kernel_spec(\"" + kernelDir.toAbsolutePath().toString() + "\", user=True, replace=True)\n";
        results = ProcessUtil.executePythonCode(this.pythonBinaryPath, sourceCode, log);
        if (results.get("output").toLowerCase().contains("error") || results.get("error").toLowerCase().contains("error")) {
            log.error("New kernel installation failed.");
            log.error("Output : " + results.get("output"));
            log.error("Error : " + results.get("error"));
            return;
        }
        log.info("Kernel installed.");

        // Clean temp dir
        log.info("Clean temporary files.");
        SystemUtil.deleteFolderRecursively(kernelDir, log);

        this.message = "The kernel '" + "scijava-" + this.scriptLanguage + "' has been correctly installed.";
    }

}
