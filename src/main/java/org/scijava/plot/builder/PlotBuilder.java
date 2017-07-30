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
package org.scijava.plot.builder;

import org.scijava.plot.specification.VegaPlot;

/**
 *
 * @author hadim
 */
public class PlotBuilder {

	private final VegaPlot plot = new VegaPlot();

	public VegaPlot build() {
		// TODO: Check if plot contains all the required fields
		return plot;
	}

	public VegaPlot bld() {
		return build();
	}

	public PlotBuilder schema(String schema) {
		plot.setSchema(schema);
		return this;
	}

	public PlotBuilder background(String background) {
		plot.setBackground(background);
		return this;
	}

	public PlotBuilder padding(Integer padding) {
		plot.setPadding(padding);
		return this;
	}

	public PlotBuilder autoResize(Boolean autoResize) {
		plot.setAutoResize(autoResize);
		return this;
	}

	public PlotBuilder name(String name) {
		plot.setName(name);
		return this;
	}

	public PlotBuilder description(String description) {
		plot.setDescription(description);
		return this;
	}

	public PlotBuilder width(Integer width) {
		plot.setWidth(width);
		return this;
	}

	public PlotBuilder height(Integer height) {
		plot.setHeight(height);
		return this;
	}

}
