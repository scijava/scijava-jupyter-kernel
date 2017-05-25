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
import org.scijava.notebook.converter.HTMLTableBuilder;
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
        boolean rowLabels = false;

        // Start table and add extra heading column in case there's row headings
        String htmlTable = HTMLTableBuilder.startTable();
        htmlTable += HTMLTableBuilder.appendRowLabelHeading();

        // Add headings
        for (int i = 0; i < table.getColumnCount(); i++) {
            htmlTable += HTMLTableBuilder.appendHeadings(asHTML(table
                .getColumnHeader(i)), i == table.getColumnCount() - 1);
        }

        // Add data
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getRowHeader(i) != null) rowLabels = true;
            htmlTable += HTMLTableBuilder.appendRowLabelData(table.getRowHeader(
                i));
            for (int j = 0; j < table.getColumnCount(); j++) {
                htmlTable += HTMLTableBuilder.appendData(asHTML(table.get(j,
                    i)), false, j == table.getColumnCount());
            }
        }
        htmlTable += HTMLTableBuilder.endTable();

        return new HTMLTableNotebookOutput(HTMLTableBuilder.getTableStyle(
            rowLabels) + htmlTable);
    }

}
