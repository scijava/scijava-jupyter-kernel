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
package org.scijava.jupyter.kernel.comm;

import com.twosigma.beakerx.kernel.KernelFunctionality;
import com.twosigma.beakerx.kernel.comm.KernelControlGetDefaultShellHandler;
import org.scijava.jupyter.kernel.configuration.ScijavaVariables;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaCommKernelControlSetShellHandler extends KernelControlGetDefaultShellHandler {

	protected ScijavaVariables var = new ScijavaVariables();

	public ScijavaCommKernelControlSetShellHandler(KernelFunctionality kernel) {
		super(kernel);
	}

	@Override
	public String[] getDefaultImports() {
		return var.getImportsAsArray();
	}

	@Override
	public String[] getDefaultClassPath() {
		return var.getClassPathAsArray();
	}

}
