package org.scijava.plot.builder;

import java.util.List;
import java.util.Map;

import org.scijava.plot.specification.VegaData;

public class SubPlotBuilder {

	private final PlotBuilder plotBuilder;
	
	public SubPlotBuilder(PlotBuilder plotBuilder) {
		this.plotBuilder = plotBuilder;
	}
	
	public PlotBuilder bld() {
		return this.build();
	}

}
