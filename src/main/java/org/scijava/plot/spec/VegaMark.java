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
package org.scijava.plot.spec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VegaMark {

    @JsonProperty
    private String type;

    @JsonProperty
    private String role;

    @JsonProperty
    private boolean filled = true;

    @JsonProperty
    private String orient;

    @JsonProperty
    private String interpolate;

    @JsonProperty
    private String tension;

    public VegaMark() {
        this.interpolate = "";

    }

    private final List<String> allowedInterpolate = Arrays.asList(
            "",
            "linear",
            "linear-closed",
            "step",
            "step-before",
            "step-after",
            "basis",
            "basis-open",
            "basis-closed",
            "cardinal",
            "cardinal-open",
            "cardinal-closed",
            "bundle",
            "monotone");

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public String getOrient() {
        return orient;
    }

    public void setOrient(String orient) {
        this.orient = orient;
    }

    public String getInterpolate() {
        return interpolate;
    }

    public void setInterpolate(String interpolate) throws Exception {
        if (!this.allowedInterpolate.contains(interpolate)) {
            throw new Exception("'" + interpolate + "' is not allowed. Please choose in this lis: "
                    + this.allowedInterpolate.toString());
        }
        this.interpolate = interpolate;
    }

    public String getTension() {
        return tension;
    }

    public void setTension(String tension) {
        this.tension = tension;
    }
}
