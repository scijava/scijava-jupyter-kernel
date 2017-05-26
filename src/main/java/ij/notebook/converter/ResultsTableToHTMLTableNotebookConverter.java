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

package ij.notebook.converter;

import net.imagej.table.GenericTable;

import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.notebook.converter.HTMLNotebookOutputConverter;
import org.scijava.notebook.converter.output.HTMLTableNotebookOutput;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.measure.ResultsTable;

/**
 * Converts a {@link ResultsTable} to an HTML table.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class ResultsTableToHTMLTableNotebookConverter extends
    HTMLNotebookOutputConverter<ResultsTable, HTMLTableNotebookOutput>
{

    @Parameter
    private ConvertService convertService;

    @Override
    public Class<HTMLTableNotebookOutput> getOutputType() {
        return HTMLTableNotebookOutput.class;
    }

    @Override
    public Class<ResultsTable> getInputType() {
        return ResultsTable.class;
    }

    @Override
    public HTMLTableNotebookOutput convert(final Object object) {
        GenericTable t = convertService.convert((ResultsTable)object, GenericTable.class);
        return convertService.convert(t, HTMLTableNotebookOutput.class);
    }

}
