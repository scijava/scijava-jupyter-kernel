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

package org.scijava.jupyter.kernel.configuration;



import com.twosigma.beakerx.kernel.Config;
import com.twosigma.beakerx.kernel.ConfigurationFile;
import com.twosigma.beakerx.message.MessageSerializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private final File configFile;
    private Config configuration;

    private final String logLevel;

    public ScijavaKernelConfigurationFile(Context context, String logLevel, Path connectionFile) {
        context.inject(this);
        this.configFile = connectionFile.toFile();
        this.logLevel = logLevel;
    }

    @Override
    public Config getConfig() {
        if (configuration == null) {
            try {
                configuration = MessageSerializer.parse(new String(Files.readAllBytes(this.configFile.toPath())), Config.class);
            } catch (IOException ex) {
                log.error("Issue loading connection file : " + ex);
            }
        }
        return configuration;
    }

    public String getLogLevel() {
        return this.logLevel;
    }

}
