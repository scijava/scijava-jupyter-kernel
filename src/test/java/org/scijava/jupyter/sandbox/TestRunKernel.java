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

/**
 *
 * @author Hadrien Mary
 */
public class TestRunKernel {

    public static void main(final String[] args) {

        // Warning : if run from your IDE the classpath won't be set to your Fiji installation
        Context context = new Context();
        JupyterService jupyter = context.service(JupyterService.class);
        jupyter.runKernel("jython", "info", "");
        context.dispose();
    }

}
