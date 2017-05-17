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

import org.python.core.PyException;
import org.python.util.PythonInterpreter;

/**
 *
 * @author Hadrien Mary
 */
public class TestJython {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws PyException {

        PythonInterpreter interp = new PythonInterpreter();

        Object result = interp.eval(interp.compile("p=999\n555")).__tojava__(Object.class);
        System.out.println(result);

        interp = new PythonInterpreter();

        result = interp.eval(interp.compile("555")).__tojava__(Object.class);
        System.out.println(result);
        
    }
}
