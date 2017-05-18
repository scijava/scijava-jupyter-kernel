/*
 * Copyright 2017 SciJava.
 *
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
 */
package net.imagej.notebook.converter;

import net.imagej.table.Table;
import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.NotebookOutputConverter;
import org.scijava.notebook.converter.output.HTMLNotebookOutput;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class, priority = Priority.LOW_PRIORITY)
public class TableToHTMLNotebookConverter<O extends Table>
        extends NotebookOutputConverter<O, HTMLNotebookOutput> {

    @Override
    public Class getInputType() {
        return Table.class;
    }

    @Override
    public Class getOutputType() {
        return HTMLNotebookOutput.class;
    }

    @Override
    public HTMLNotebookOutput convert(Object object) {

        Table table = (Table) object;

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
                htmlString += table.get(j, i);
                htmlString += "</td>";
            }
            htmlString += "</tr>";
        }

        htmlString += "</table>";

        return new HTMLNotebookOutput(HTMLNotebookOutput.getMimeType(), htmlString);
    }

}
