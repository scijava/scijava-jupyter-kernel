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

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menu = {
    @Menu(label = "Analyze")
    ,
	@Menu(label = "Jupyter Kernel")
    ,
	@Menu(label = "Run Scijava Kernel (not available yet)")})
public class RunScijavaKernel implements Command {

    @Parameter
    private LogService log;

    @Parameter(required = true, label = "Script Language")
    private String scriptLanguage = "jython";

    @Parameter(required = true, label = "Log Level",
            choices = {"debug", "error", "info", "none"})
    private String logLevel = "info";

    @Override
    public void run() {

        log.info("Not implemented yet because we need the 'connection_file'.");

//        try {
//            
//            List<String> parameters = new ArrayList<>();
//            parameters.add("-language");
//            parameters.add(this.scriptLanguage);
//            parameters.add("-verbose");
//            parameters.add(this.logLevel);
//            parameters.add("-configFile");
//            parameters.add("");
//            
//            DefaultKernel.main(parameters.toArray(new String[parameters.size()]));
//            
//        } catch (InterruptedException | IOException ex) {
//            log.error(ex);
//        }
    }
}
