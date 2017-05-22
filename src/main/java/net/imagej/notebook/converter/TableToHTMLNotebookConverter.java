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

package net.imagej.notebook.converter;

import net.imagej.table.Column;
import net.imagej.table.Table;

import org.scijava.convert.Converter;
import org.scijava.notebook.converter.HTMLNotebookOutputConverter;
import org.scijava.notebook.converter.output.HTMLTableNotebookOutput;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class)
public class TableToHTMLNotebookConverter<C extends Column<? extends T>, T>
    extends HTMLNotebookOutputConverter<Table<C, T>, HTMLTableNotebookOutput>
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<Table<C, T>> getInputType() {
        return (Class) Table.class;
    }

    @Override
    public Class<HTMLTableNotebookOutput> getOutputType() {
        return HTMLTableNotebookOutput.class;
    }

    @Override
    public HTMLTableNotebookOutput convert(final Object object) {

        @SuppressWarnings("unchecked")
        final Table<C, T> table = (Table<C, T>) object;

        String htmlString = "<table>";

        // Set column headers
        htmlString += "<tr>";
        for (int i = 0; i < table.getColumnCount(); i++) {
            htmlString += "<th>";
            htmlString += table.getColumnHeader(i);
            htmlString += "</th>";
        }
        htmlString += "</tr>";

        // Append the rows
        for (int i = 0; i < table.getRowCount(); i++) {
            htmlString += "<tr>";
            for (int j = 0; j < table.getColumnCount(); j++) {
                htmlString += "<td>";
                htmlString += asHTML(table.get(j, i));
                htmlString += "</td>";
            }
            htmlString += "</tr>";
        }

        htmlString += "</table>";

        return new HTMLTableNotebookOutput(htmlString);
    }

}
