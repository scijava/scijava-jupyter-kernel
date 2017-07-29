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
package org.scijava.plot.spec.encoding;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;

/**
 *
 * @author hadim
 */
public class EncodingAggregate extends EncodingSingleValue {

    public EncodingAggregate() {

        this.allowedValues = Arrays.asList("count",
                "valid",
                "missing",
                "distinct",
                "sum",
                "mean",
                "average",
                "variance",
                "variancep",
                "stdev",
                "stdevp",
                "median",
                "q1",
                "q3",
                "modeskew",
                "min",
                "max"
        );
    }

    public EncodingAggregate(String timeUnit) throws Exception {
        this.setValue(timeUnit);
    }

}
