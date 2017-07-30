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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.scijava.plot.specification.VegaPlot;
import org.scijava.plot.specification.encoding.EncodingChannel;

/**
 *
 * @author hadim
 */
public class ManualTest {

    public static void main(String args[]) throws IOException {

        VegaPlot plot = new VegaPlot();

        plot.setDescription("A simple bar hart with embedded data.");

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
