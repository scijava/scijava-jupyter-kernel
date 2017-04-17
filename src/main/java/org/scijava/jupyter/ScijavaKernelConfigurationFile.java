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
package org.scijava.jupyter;

import com.twosigma.jupyter.Config;
import com.twosigma.jupyter.ConfigurationFile;
import com.twosigma.jupyter.message.MessageSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaKernelConfigurationFile implements ConfigurationFile {

    @Parameter
    private transient LogService log;

    private File configFile;
    private Config configuration;

    // TODO : move this to the Config class
    private String languageName;
    private String logLevel;

    public ScijavaKernelConfigurationFile(final Context context, final String[] args) {
        context.inject(this);
        this.configFile = getConfig(args);
    }

    @Override
    public Config getConfig() {
        if (configuration == null) {
            try {
                configuration = MessageSerializer.parse(new String(Files.readAllBytes(this.configFile.toPath())), Config.class);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        return configuration;
    }

    private File getConfig(final String[] args) {
        if (args.length > 0) {
            try {
                String connectionFilePath;

                Options options = new Options();
                options.addOption("configFile", true, "Connection File Path");
                options.addOption("language", true, "Language Name");
                options.addOption("verbose", true, "Verbose Mode");

                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(options, args);

                connectionFilePath = cmd.getOptionValue("configFile");
                this.languageName = cmd.getOptionValue("language");
                this.logLevel = cmd.getOptionValue("verbose");
                
                File localConfigFile = new File(connectionFilePath);
                if (!localConfigFile.exists()) {
                    log.error("Kernel configuration not found.");
                    System.exit(1);
                }

                return localConfigFile;
                
            } catch (ParseException ex) {
                log.error("Error parsing kernel options : " + ex.toString());
            }
        } else {
            log.error("No parameters passed to the Kernel.");
            System.exit(1);
        }

        return null;
    }

    public String getLanguageName() {
        return this.languageName;
    }
    
    public String getLogLevel() {
        return this.logLevel;
    }

}
