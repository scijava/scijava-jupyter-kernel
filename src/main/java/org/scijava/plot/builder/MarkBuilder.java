package org.scijava.plot.builder;

import org.scijava.plot.specification.VegaMark;

public class MarkBuilder {

	private final PlotBuilder plotBuilder;
	private final VegaMark mark = new VegaMark();

	public MarkBuilder(PlotBuilder plotBuilder) {
		this.plotBuilder = plotBuilder;
	}

	public PlotBuilder build() {
		this.plotBuilder.getPlot().setMark(mark);
		return this.plotBuilder;
	}

	public PlotBuilder bld() {
		return this.build();
	}

	public MarkBuilder type(String type) {
		mark.setType(type);
		return this;
	}

	public MarkBuilder role(String role) {
		mark.setRole(role);
		return this;
	}

	public MarkBuilder filled(Boolean filled) {
		mark.setFilled(filled);
		return this;
	}

	public MarkBuilder orient(String orient) {
		mark.setOrient(orient);
		return this;
	}

	public MarkBuilder interpolate(String interpolate) throws Exception {
		mark.setInterpolate(interpolate);
		return this;
	}

	public MarkBuilder tension(String tension) {
		mark.setTension(tension);
		return this;
	}

}
