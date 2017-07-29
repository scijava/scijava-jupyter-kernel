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
package org.scijava.plot.specification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.scijava.plot.specification.encoding.EncodingChannel;
import org.scijava.plot.specification.encoding.VegaEncoding;

/**
 * A plot represents a Vega-Lite visualization. It contains the top-level
 * specifications defined here: https://vega.github.io/vega-lite/docs/spec.html
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VegaPlot {

    public static String SCHEMA = "https://vega.github.io/schema/vega-lite/v2.json";

    @JsonProperty
    private String schema = SCHEMA;

    @JsonProperty
    private String background;

    @JsonProperty
    private Integer padding;

    @JsonProperty
    private Boolean autoResize;

    @JsonProperty
    private VegaConfig config;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private Integer width;

    @JsonProperty
    private Integer height;

    @JsonProperty
    private VegaData data;

    @JsonProperty
    private VegaTransforms transform;

    @JsonProperty
    private VegaSelection selection;

    @JsonProperty
    private VegaMark mark;

    @JsonProperty
    private VegaEncoding encoding;

    public VegaPlot() {
        this.data = new VegaData();
        this.mark = new VegaMark();
        this.encoding = new VegaEncoding();
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public Boolean getAutoResize() {
        return autoResize;
    }

    public void setAutoResize(Boolean autoResize) {
        this.autoResize = autoResize;
    }

    public VegaConfig getConfig() {
        return config;
    }

    public void setConfig(VegaConfig config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public VegaData getData() {
        return data;
    }

    public void setData(VegaData data) {
        this.data = data;
    }

    public VegaTransforms getTransform() {
        return transform;
    }

    public void setTransform(VegaTransforms transform) {
        this.transform = transform;
    }

    public VegaSelection getSelection() {
        return selection;
    }

    public void setSelection(VegaSelection selection) {
        this.selection = selection;
    }

    public VegaMark getMark() {
        return mark;
    }

    public void setMark(VegaMark mark) {
        this.mark = mark;
    }

    public VegaEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(VegaEncoding encoding) {
        this.encoding = encoding;
    }

    public static void main(String args[]) throws IOException {

        VegaPlot plot = new VegaPlot();

        plot.setDescription("A simple bar chart with embedded data.");

        plot.getMark().setType("bar");

        EncodingChannel x = new EncodingChannel();
        x.setField("a");
        x.setType("ordinal");
        plot.getEncoding().setX(x);

        EncodingChannel y = new EncodingChannel();
        y.setField("b");
        y.setType("quantitative");
        plot.getEncoding().setY(y);

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
        datum = new HashMap<>();
        datum.put("a", "D");
        datum.put("b", 91);
        data.add(datum);
        datum = new HashMap<>();
        datum.put("a", "E");
        datum.put("b", 81);
        data.add(datum);
        datum = new HashMap<>();
        datum.put("a", "F");
        datum.put("b", 53);
        data.add(datum);
        datum = new HashMap<>();
        datum.put("a", "G");
        datum.put("b", 19);
        data.add(datum);
        datum = new HashMap<>();
        datum.put("a", "H");
        datum.put("b", 87);
        data.add(datum);
        datum = new HashMap<>();
        datum.put("a", "I");
        datum.put("b", 52);
        data.add(datum);

        plot.getData().setValues(data);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("/home/hadim/outputfile.json"), plot);

        plot = mapper.readValue(new File("/home/hadim/outputfile.json"), VegaPlot.class);

        System.out.println(mapper.writeValueAsString(plot));

    }

}
