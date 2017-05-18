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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.ouput.HTMLNotebookOutput;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@code List<Map<K,V>>} in to an HTML table. The first {@code Map}
 * in the list's keys will be used as the headers for this table and dictate
 * the number of columns the table has.
 *
 * @author Alison Walter
 *
 * @param <K> data type of keys
 * @param <V> data type of values
 */
@Plugin(type = Converter.class, priority = Priority.LOW_PRIORITY)
public class ListMapToHTMLTable<K, V> extends
    NotebookOutputConverter<List<Map<K, V>>, HTMLNotebookOutput>
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<List<Map<K, V>>> getInputType() {
        return (Class) List.class;
    }

    @Override
    public Class<HTMLNotebookOutput> getOutputType() {
        return HTMLNotebookOutput.class;
    }

    @Override
    public HTMLNotebookOutput convert(final Object object) {
        @SuppressWarnings("unchecked")
        final List<Map<K, V>> table = (List<Map<K, V>>) object;

        // Style for the HTML table
        final String style = "<style>" +
            "table.converted {color: #333; font-family: Helvetica, Arial, sans-serif; border-collapse: collapse; border-spacing: 0;}" +
            "table.converted td, table.converted th {border: 1px solid #C9C7C7;}" +
            "table.converted th {background: #626262; color: #FFFFFF; font-weight: bold; text-align: left;}" +
            "table.converted td {text-align: left;}" +
            "table.converted tr:nth-child(even) {background: #F3F3F3;}" +
            "table.converted tr:nth-child(odd) {background: #FFFFFF;}" +
            "table.converted tbody tr:hover {background: #BDF4B5;}" +
            "</style>";

        String htmlString = "<table class=\"converted\"><thead>";

        final List<?> headers = new ArrayList<>(table.get(0).keySet());

        // Set column headers
        htmlString += "<tr>";
        for (final Object header : headers) {
            htmlString += "<th>";
            htmlString += StringEscapeUtils.escapeHtml4(header.toString());
            htmlString += "</th>";
        }
        htmlString += "</tr></thead><tbody>";

        // Append the rows
        for (int i = 0; i < table.size(); i++) {
            htmlString += "<tr>";
            for (final Object header : headers) {
                htmlString += "<td>";
                final Map<?, ?> row = table.get(i);
                if (row.containsKey(header)) htmlString += StringEscapeUtils
                    .escapeHtml4(row.get(header).toString());
                htmlString += "</td>";
            }
            htmlString += "</tr>";
        }

        htmlString += "</tbody></table>";

        final String styledTable = style + htmlString;

        return new HTMLNotebookOutput(HTMLNotebookOutput.getMimeType(),
            styledTable);
    }

}
