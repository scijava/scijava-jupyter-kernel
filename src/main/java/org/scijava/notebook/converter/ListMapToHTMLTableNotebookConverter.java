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
 * Converts a {@code List<Map<K,V>>} in to an HTML table. The first {@code Map}
 * in the list's keys will be used as the headers for this table and dictate the
 * number of columns the table has.
 *
 * @author Alison Walter
 * @param <K> data type of keys
 * @param <V> data type of values
 */
@Plugin(type = Converter.class)
public class ListMapToHTMLTableNotebookConverter<K, V> extends
    HTMLNotebookOutputConverter<List<Map<K, V>>, HTMLTableNotebookOutput>
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<List<Map<K, V>>> getInputType() {
        return (Class) List.class;
    }

    @Override
    public Class<HTMLTableNotebookOutput> getOutputType() {
        return HTMLTableNotebookOutput.class;
    }

    @Override
    public boolean canConvert(final ConversionRequest request) {
        final Object src = request.sourceObject();
        if (src != null && src instanceof List && ((List<?>) src).get(
            0) instanceof Map)
        {
            return super.canConvert(request);
        }
        return false;

    }

    @Override
    public boolean canConvert(final Object src, final Type dest) {
        if (src == null) return false;
        if (src instanceof List && ((List<?>)src).get(0) instanceof Map) {
            final Class<?> srcClass = src.getClass();
            return super.canConvert(srcClass, dest);
        }
        return false;
    }

    @Override
    public boolean canConvert(final Object src, final Class<?> dest) {
        if (src == null) return false;
        if (src instanceof List && ((List<?>)src).get(0) instanceof Map) {
            final Class<?> srcClass = src.getClass();
            return super.canConvert(srcClass, dest);
        }
        return false;
    }

    @Override
    public HTMLTableNotebookOutput convert(final Object object) {
        @SuppressWarnings("unchecked")
        final List<Map<K, V>> table = (List<Map<K, V>>) object;

        String htmlTable = HTMLTableBuilder.startTable();
        final Object[] headers = table.get(0).keySet().toArray();
        final int numCols = headers.length;

        // Add column headers
        for (int i = 0; i < numCols; i++) {
            htmlTable += HTMLTableBuilder.appendHeadings(asHTML(headers[i]),
                i == numCols - 1);
        }

        // Append the rows
        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < numCols; j++) {
                final Map<?, ?> row = table.get(i);
                if (row.containsKey(headers[j])) htmlTable += HTMLTableBuilder
                    .appendData(asHTML(row.get(headers[j])), j == 0,
                        j == numCols - 1);
                else htmlTable += HTMLTableBuilder.appendData("&nbsp;", j == 0,
                    j == numCols - 1);
            }
        }
        htmlTable += HTMLTableBuilder.endTable();

        return new HTMLTableNotebookOutput(HTMLTableBuilder.getTableStyle(
            false) + htmlTable);
    }

}
