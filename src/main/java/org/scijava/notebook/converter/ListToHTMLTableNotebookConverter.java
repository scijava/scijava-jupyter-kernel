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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.scijava.convert.ConversionRequest;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.output.HTMLTableNotebookOutput;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@code List<?>} to an html table.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class ListToHTMLTableNotebookConverter extends
    HTMLNotebookOutputConverter<List<?>, HTMLTableNotebookOutput>
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<List<?>> getInputType() {
        return (Class) List.class;
    }

    @Override
    public Class<HTMLTableNotebookOutput> getOutputType() {
        return HTMLTableNotebookOutput.class;
    }

    @Override
    public boolean canConvert(final ConversionRequest request) {
        final Object src = request.sourceObject();
        if (src != null && src instanceof List && !(((List<?>) src).get(
            0) instanceof Map))
        {
            return super.canConvert(request);
        }
        return false;

    }

    @Override
    public boolean canConvert(final Object src, final Type dest) {
        if (src == null) return false;
        if (src instanceof List && !(((List<?>) src).get(0) instanceof Map)) {
            final Class<?> srcClass = src.getClass();
            return super.canConvert(srcClass, dest);
        }
        return false;
    }

    @Override
    public boolean canConvert(final Object src, final Class<?> dest) {
        if (src == null) return false;
        if (src instanceof List && !(((List<?>) src).get(0) instanceof Map)) {
            final Class<?> srcClass = src.getClass();
            return super.canConvert(srcClass, dest);
        }
        return false;
    }

    @Override
    public HTMLTableNotebookOutput convert(final Object object) {
        final List<?> list = (List<?>) object;

        String htmlTable = "<table class=\"converted\"><tbody>";

        // Append the rows
        for (int i = 0; i < list.size(); i++) {
            final String data = list.get(i) == null ? "&nbsp;" : asHTML(list
                .get(i));
            htmlTable += HTMLTableBuilder.appendData(data, true, true);
        }
        htmlTable += HTMLTableBuilder.endTable();

        return new HTMLTableNotebookOutput(HTMLTableBuilder.getTableStyle(
            false) + htmlTable);
    }

}
