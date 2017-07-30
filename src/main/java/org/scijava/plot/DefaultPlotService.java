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
package org.scijava.plot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scijava.notebook.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scijava.plot.builder.PlotBuilder;

import org.scijava.plot.specification.VegaPlot;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * AWT-driven implementation of {@link NotebookService}.
 *
 * @author Curtis Rueden
 * @author Hadrien Mary
 */
@Plugin(type = Service.class)
public class DefaultPlotService extends AbstractService implements
        PlotService {

    @Override
    public PlotBuilder builder() {
        return new PlotBuilder();
    }

    @Override
    public void writeJson(VegaPlot plot, String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(filePath), plot);
        } catch (IOException ex) {
            Logger.getLogger(DefaultPlotService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toJson(VegaPlot plot) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(plot);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DefaultPlotService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public VegaPlot readJson(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(filePath), VegaPlot.class);
        } catch (IOException ex) {
            Logger.getLogger(DefaultPlotService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
