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

import org.scijava.convert.AbstractConverter;
import org.scijava.log.LogService;
import org.scijava.notebook.converter.output.NotebookOutput;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
public abstract class NotebookOutputConverter<I, O extends NotebookOutput>
        extends AbstractConverter<I, O> {

    @Parameter
    private LogService log;

    @Override
    public <T> T convert(final Object src, final Class<T> dest) {
        if (src == null) {
            throw new IllegalArgumentException("Null input");
        }

        if (!this.getInputType().isInstance(src)) {
            throw new IllegalArgumentException("Expected input of type "
                    + getInputType().getSimpleName() + ", but got "
                    + src.getClass().getSimpleName());
        }

        return (T) this.convert(src);
    }

    public abstract O convert(Object object);

}
