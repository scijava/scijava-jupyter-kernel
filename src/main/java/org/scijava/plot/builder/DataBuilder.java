package org.scijava.plot.builder;

import java.util.List;
import java.util.Map;

import org.scijava.plot.specification.VegaData;

public class DataBuilder {

	private final PlotBuilder plotBuilder;
	private final VegaData data = new VegaData();

	public DataBuilder(PlotBuilder plotBuilder) {
		this.plotBuilder = plotBuilder;
	}

	public PlotBuilder build() {
		this.plotBuilder.getPlot().setData(data);
		return this.plotBuilder;
	}

	public PlotBuilder bld() {
		return this.build();
	}

	public DataBuilder values(List<Map<String, Object>> values) {
		data.setValues(values);
		return this;
	}

	public DataBuilder format(String format) {
		data.setFormat(format);
		return this;
	}

	public DataBuilder url(String url) {
		data.setUrl(url);
		return this;
	}

	public DataBuilder type(String type) {
		data.setType(type);
		return this;
	}

	public DataBuilder parse(String parse) {
		data.setParse(parse);
		return this;
	}

	public DataBuilder property(String property) {
		data.setProperty(property);
		return this;
	}

	public DataBuilder name(String name) {
		data.setName(name);
		return this;
	}

}
