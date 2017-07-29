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
package org.scijava.plot;

import org.scijava.plot.spec.VegaPlot;
import org.scijava.plot.spec.encoding.EncodingChannel;
import org.scijava.service.SciJavaService;

/**
 *
 * @author hadim
 */
public interface PlotService extends SciJavaService {
    
    public VegaPlot newPlot();
    public VegaPlot newPlot(String name);
    public EncodingChannel newChannel();
    public EncodingChannel newChannel(String field, String type);
    public void writeJson(VegaPlot plot, String filePath);
    public VegaPlot readJson(String jsonString);
    public String toJson(VegaPlot plot);
    
}
