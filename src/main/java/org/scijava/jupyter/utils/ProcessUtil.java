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

package org.scijava.jupyter.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.scijava.log.LogService;

/**
 *
 * @author Hadrien Mary
 */
public class ProcessUtil {

    public static Map<String, String> executePythonCode(File pythonBinaryPath, String sourceCode, LogService log) {

        Map<String, String> results = null;

        try {
            File tempFile = File.createTempFile("scijava-script", ".py");
            Files.write(tempFile.toPath(), sourceCode.getBytes());

            String[] cmd = new String[]{pythonBinaryPath.toString(), tempFile.toString()};

            results = ProcessUtil.executeProcess(cmd, log);
        } catch (IOException ex) {
            log.error(ex);
        }

        return results;
    }

    public static Map<String, String> executeProcess(String[] cmd, LogService log) {

        Map<String, String> results = new HashMap<>();

        try {

            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();

            BufferedReader outputStream = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output = outputStream.lines().collect(Collectors.joining("\n"));

            BufferedReader errorStream = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String error = errorStream.lines().collect(Collectors.joining("\n"));

            results.put("output", output);
            results.put("error", error);

        } catch (IOException | InterruptedException ex) {
            log.error(ex);
        }

        return results;
    }

}
