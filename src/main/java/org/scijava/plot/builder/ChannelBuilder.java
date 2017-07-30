package org.scijava.plot.builder;

import java.util.List;

import org.scijava.plot.specification.EncodingBin;
import org.scijava.plot.specification.EncodingChannel;
import org.scijava.plot.specification.VegaEncoding;

public class ChannelBuilder {

	private final PlotBuilder plotBuilder;
	private final String channelName;
	private final EncodingBin bin = new EncodingBin();
	private final EncodingChannel channel = new EncodingChannel();

	public ChannelBuilder(PlotBuilder plotBuilder, String channelName) {
		this.plotBuilder = plotBuilder;
		this.channelName = channelName;
	}

	public PlotBuilder build() {
		VegaEncoding encoding = this.plotBuilder.getPlot().getEncoding();
		if (channelName == "x")
			encoding.setX(channel);
		else if (channelName == "y")
			encoding.setY(channel);
		else if (channelName == "row")
			encoding.setRow(channel);
		else if (channelName == "column")
			encoding.setColumn(channel);
		else if (channelName == "color")
			encoding.setColor(channel);
		else if (channelName == "order")
			encoding.setOrder(channel);
		else if (channelName == "shape")
			encoding.setShape(channel);
		else if (channelName == "size")
			encoding.setSize(channel);
		else if (channelName == "opacity")
			encoding.setOpacity(channel);
		else if (channelName == "text")
			encoding.setText(channel);
		else if (channelName == "tooltip")
			encoding.setTooltip(channel);
		else if (channelName == "detail")
			encoding.setDetail(channel);
		return this.plotBuilder;
	}

	public PlotBuilder bld() {
		return this.build();
	}

	public ChannelBuilder field(String field) {
		channel.setField(field);
		return this;
	}

	public ChannelBuilder type(String type) {
		channel.setType(type);
		return this;
	}

	public ChannelBuilder timeUnit(String timeUnit) {
		channel.setTimeUnit(timeUnit);
		return this;
	}

	public ChannelBuilder aggregate(String aggregate) {
		channel.setAggregate(aggregate);
		return this;
	}

	public ChannelBuilder stack(String stack) {
		channel.setStack(stack);
		return this;
	}

	public ChannelBuilder sort(String sort) {
		channel.setSort(sort);
		return this;
	}

	public ChannelBuilder value(String value) {
		channel.setValue(value);
		return this;
	}

	public ChannelBuilder scale(String scale) {
		channel.setScale(scale);
		return this;
	}

	public ChannelBuilder axis(Boolean axis) {
		channel.setAxis(axis);
		return this;
	}

	public ChannelBuilder legend(Boolean legend) {
		channel.setLegend(legend);
		return this;
	}

	public ChannelBuilder bin(String key, Object value) {

		if (key == "maxbins")
			bin.setMaxbins((Integer) value);
		else if (key == "base")
			bin.setBase((Integer) value);
		else if (key == "step")
			bin.setStep((Integer) value);
		else if (key == "steps")
			bin.setSteps((List<Integer>) value);
		else if (key == "minstep")
			bin.setMinstep((Integer) value);
		else if (key == "divide")
			bin.setDivide((List<Integer>) value);
		else if (key == "extent")
			bin.setExtent((List<Integer>) value);
		else if (key == "nice")
			bin.setNice((Integer) value);
		channel.setBin(bin);

		return this;
	}

}
