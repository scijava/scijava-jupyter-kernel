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

package org.scijava.plot.converter;

import groovy.json.JsonSlurper;
import org.scijava.Priority;
import org.scijava.notebook.converter.*;
import org.scijava.convert.Converter;
import org.scijava.plot.PlotService;
import org.scijava.plot.specification.VegaPlot;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class, priority = Priority.HIGH_PRIORITY)
public class VegaPlotConverter
        extends NotebookOutputConverter<VegaPlot, VegaPlotOutput> {
    
    @Parameter
    private PlotService plt;

    @Override
    public Class<VegaPlot> getInputType() {
        return VegaPlot.class;
    }

    @Override
    public Class<VegaPlotOutput> getOutputType() {
        return VegaPlotOutput.class;
    }

    @Override
    public VegaPlotOutput convert(Object plot) {
        String jsonString = plt.toJson((VegaPlot) plot);
        JsonSlurper jsonSlurper = new JsonSlurper();
        Object json = jsonSlurper.parseText(jsonString);
        return new VegaPlotOutput(json);
    }

}
