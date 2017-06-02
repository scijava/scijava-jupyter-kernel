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

import java.util.Map;

import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.output.HTMLTableNotebookOutput;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@code Map} to a two column HTML table. The headers for this table
 * are Key and Value.
 *
 * @author Alison Walter
 * @param <K> data type used for the key
 * @param <V> data type used for the values
 */
@Plugin(type = Converter.class)
public class MapToHTMLTableNotebookConverter<K, V> extends
    HTMLNotebookOutputConverter<Map<K, V>, HTMLTableNotebookOutput>
{

    @Parameter
    private ConvertService convertService;

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<Map<K, V>> getInputType() {
        return (Class) Map.class;
    }

    @Override
    public Class<HTMLTableNotebookOutput> getOutputType() {
        return HTMLTableNotebookOutput.class;
    }

    @Override
    public HTMLTableNotebookOutput convert(final Object object) {
        @SuppressWarnings("unchecked")
        final Map<K, V> table = (Map<K, V>) object;

        // Default headings for a map are Key and Value
        String htmlTable = HTMLTableBuilder.startTable();
        htmlTable += HTMLTableBuilder.appendHeadings("Key", false);
        htmlTable += HTMLTableBuilder.appendHeadings("Value", true);

        // Append the rows
        for (final K key : table.keySet()) {
            final String k = asHTML(key);
            final String v = asHTML(table.get(key));
            htmlTable += HTMLTableBuilder.appendData(k, true, false);
            htmlTable += HTMLTableBuilder.appendData(v, false, true);
        }
        htmlTable += HTMLTableBuilder.endTable();

        return new HTMLTableNotebookOutput(HTMLTableBuilder.getTableStyle(
            false) + htmlTable);
    }

}
