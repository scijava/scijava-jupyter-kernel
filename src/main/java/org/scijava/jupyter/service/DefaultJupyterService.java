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

package org.scijava.jupyter.service;


import com.twosigma.beakerx.kernel.KernelRunner;
import com.twosigma.beakerx.kernel.KernelSocketsFactoryImpl;
import static com.twosigma.beakerx.kernel.Utils.uuid;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.jupyter.kernel.ScijavaKernel;
import org.scijava.jupyter.kernel.configuration.ScijavaKernelConfigurationFile;
import org.scijava.jupyter.kernel.evaluator.ScijavaEvaluator;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author Hadrien Mary
 */
@Plugin(type = Service.class)
public class DefaultJupyterService extends AbstractService implements JupyterService {

    @Parameter
    private transient LogService log;

    @Parameter
    private transient Context context;

    @Parameter
    private transient CommandService command;

    /* Run kernel */
    @Override
    public void runKernel(String... args) {
        Map<String, Object> parameters = parseArgumentsRun(args);
        // TODO : Ensure parameters contains the appropriate keys.

        runKernel((String) parameters.get("logLevel"),
                (String) parameters.get("connectionFile"));
    }

    @Override
    public void runKernel(String logLevel, String connectionFile) {
        runKernel(logLevel, Paths.get(connectionFile));
    }

    @Override
    public void runKernel(String logLevel, File connectionFile) {
        runKernel(logLevel, connectionFile.toPath());
    }

    @Override
    public void runKernel(String logLevel, Path connectionFile) {

        KernelRunner.run(() -> {
            String id = uuid();

            // Setup configuration
            ScijavaKernelConfigurationFile config = new ScijavaKernelConfigurationFile(this.context,
                    logLevel,
                    connectionFile);

            // Setup the socket
            KernelSocketsFactoryImpl kernelSocketsFactory = new KernelSocketsFactoryImpl(config);

            // Setup the evaluator
            ScijavaEvaluator evaluator = new ScijavaEvaluator(context, id, id);

            // Launch the kernel
            return new ScijavaKernel(context, id, evaluator, config, kernelSocketsFactory);
        });
    }

    /* Helpers private method */
    private Map<String, Object> parseArgumentsRun(final String... args) {
        if (args.length > 0) {
            try {

                Options options = new Options();
                options.addOption("connectionFile", true, "Connection File Path");
                options.addOption("verbose", true, "Verbose Mode");

                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(options, args);

                Map<String, Object> parameters = new HashMap<>();

                parameters.put("connectionFile", cmd.getOptionValue("connectionFile"));
                parameters.put("logLevel", cmd.getOptionValue("verbose"));

                return parameters;

            } catch (ParseException ex) {
                log.error("Error parsing arguments : " + ex.toString());
            }
        } else {
            log.error("No parameters passed to the Scijava kernel.");
        }
        return null;
    }

}
