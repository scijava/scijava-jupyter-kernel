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

import org.apache.commons.lang3.StringEscapeUtils;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.output.HTMLNotebookOutput;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class)
public class ObjectToHTMLNotebookConverter extends
    NotebookOutputConverter<Object, HTMLNotebookOutput>
{

    @Override
    public Class<Object> getInputType() {
        return Object.class;
    }

    @Override
    public Class<HTMLNotebookOutput> getOutputType() {
        return HTMLNotebookOutput.class;
    }

    @Override
    public HTMLNotebookOutput convert(final Object object) {
        final String escaped = StringEscapeUtils.escapeHtml4(object.toString());
        // Add in zero width space character (&#8203;) before @, ., $, _, and #
        final String wordBreaks = escaped.replaceAll("([@.$_#])", "&#8203;$1");
        return new HTMLNotebookOutput(wordBreaks);
    }

}
