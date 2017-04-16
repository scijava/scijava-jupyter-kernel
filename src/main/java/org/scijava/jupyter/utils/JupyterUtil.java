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
package org.scijava.jupyter.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Hadrien Mary
 */
public class JupyterUtil {

    public static String createKernelJSON(String scriptLanguage, String logLevel) {
        JSONObject root = new JSONObject();
        root.put("language", scriptLanguage);
        root.put("display_name", "Scijava - " + Character.toUpperCase(scriptLanguage.charAt(0)) + scriptLanguage.substring(1));

        JSONArray argv = new JSONArray();
        argv.add(SystemUtil.getJavaBinary());
        argv.add("-classpath");
        argv.add(SystemUtil.getClassPaths());
        argv.add("org.scijava.jupyter.kernel.DefaultKernel");
        argv.add("-language");
        argv.add(scriptLanguage);
        argv.add("-verbose");
        argv.add(logLevel);
        argv.add("-configFile");
        argv.add("{connection_file}");
        root.put("argv", argv);

        return root.toJSONString();
    }
}
