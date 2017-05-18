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

package org.scijava.jupyter.sandbox;

import org.scijava.Context;
import org.scijava.jupyter.service.JupyterService;
import org.scijava.script.ScriptService;

/**
 *
 * @author Hadrien Mary
 */
public class TestInstallKernel {

    public static void main(String... args) {

        String pythonBinaryPath = "/home/hadim/local/conda/bin/python";

        Context context = new Context();
        JupyterService jupyter = context.service(JupyterService.class);
        ScriptService scriptService = context.service(ScriptService.class);
        
        //jupyter.installKernel("groovy", "info", pythonBinaryPath);
        
        System.out.println(scriptService.getLanguages());
        
        context.dispose();

    }

}
