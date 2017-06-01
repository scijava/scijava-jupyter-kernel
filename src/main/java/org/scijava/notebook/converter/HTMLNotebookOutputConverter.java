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

package org.scijava.notebook.converter;

import org.scijava.convert.ConvertService;
import org.scijava.notebook.converter.output.HTMLFriendlyNotebookOutput;
import org.scijava.notebook.converter.output.HTMLNotebookOutput;
import org.scijava.plugin.Parameter;

/**
 * Base class for converters to {@link HTMLNotebookOutput} and subclasses.
 *
 * @author Curtis Rueden
 */
public abstract class HTMLNotebookOutputConverter<I, O extends HTMLNotebookOutput>
    extends NotebookOutputConverter<I, O>
{

    @Parameter
    private ConvertService convertService;

    /** Gets an HTML string representing the given object. */
    protected String asHTML(final Object o) {
        final HTMLFriendlyNotebookOutput converted = convertService.convert(o, HTMLFriendlyNotebookOutput.class);
        return converted == null ? "&lt;none&gt;" : converted.toHTML();
    }

}
