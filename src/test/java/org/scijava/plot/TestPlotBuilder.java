
package org.scijava.plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.plot.builder.PlotBuilder;

public class TestPlotBuilder {

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testSimpleBarChart() {
		List<Map<String, Object>> data = new ArrayList<>();
		Map<String, Object> datum;
		datum = new HashMap<>();
		datum.put("a", "A");
		datum.put("b", 28);
		data.add(datum);
		datum = new HashMap<>();
		datum.put("a", "B");
		datum.put("b", 55);
		data.add(datum);
		datum = new HashMap<>();
		datum.put("a", "C");
		datum.put("b", 43);
		data.add(datum);

		PlotBuilder builder = new PlotBuilder();
		builder = builder.name("bar chart").description("the description");
		builder = builder.data().values(data).build();
		builder = builder.mark().type("bar").build();
		builder = builder.channel("x").field("a").type("ordinal").build();
		builder = builder.channel("y").field("b").type("quantitative").build();

		builder.build();

	}

}
