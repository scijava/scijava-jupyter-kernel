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
package org.scijava.plot.specification.encoding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EncodingSingleValue {
    
    protected String value;
    protected List<String> allowedValues;
    
    public EncodingSingleValue() {
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) throws Exception {
        if (!this.allowedValues.contains(value)) {
            throw new Exception("'" + value + "' is not allowed. Please choose in this lis: " + this.allowedValues.toString());
        }
        this.value = value;
    }
    
}
