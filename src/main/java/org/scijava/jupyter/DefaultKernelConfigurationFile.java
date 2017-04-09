/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.apache.commons.cli.PosixParser;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultKernelConfigurationFile implements ConfigurationFile {

    @Parameter
    private transient LogService log;

    private File configFile;
    private Config configuration;

    // TODO : move this to the Config class
    private String languageName;
    private String logLevel;

    public DefaultKernelConfigurationFile(final Context context, final String[] args) {
        context.inject(this);
        this.configFile = getConfig(args);
    }

    @Override
    public Config getConfig() throws IOException {
        if (configuration == null) {
            log.info("Parsing the connection file.");
            log.info("Path to kernel config file : " + this.configFile.getAbsolutePath());

            configuration = MessageSerializer.parse(new String(Files.readAllBytes(this.configFile.toPath())), Config.class);

            log.info("Creating signing hmac with : " + configuration.getKey());
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
                
                File configFile = new File(connectionFilePath);
                if (!configFile.exists()) {
                    System.out.println("Kernel configuration not found.");
                    System.exit(1);
                }

                return configFile;
            } catch (ParseException ex) {
                System.out.println("Error parsing kernel options : " + ex.toString());
            }
        } else {
            System.out.println("No parameters passed to the Kernel.");
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
