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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EncodingBin {

    @JsonProperty
    private int maxbins;

    @JsonProperty
    private int base;

    @JsonProperty
    private int step;

    @JsonProperty
    private int[] steps;

    @JsonProperty
    private int minstep;

    @JsonProperty
    private int[] divide;

    @JsonProperty
    private int[] extent;

    @JsonProperty
    private int nice;

    public int getMaxbins() {
        return maxbins;
    }

    public void setMaxbins(int maxbins) {
        this.maxbins = maxbins;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int[] getSteps() {
        return steps;
    }

    public void setSteps(int[] steps) {
        this.steps = steps;
    }

    public int getMinstep() {
        return minstep;
    }

    public void setMinstep(int minstep) {
        this.minstep = minstep;
    }

    public int[] getDivide() {
        return divide;
    }

    public void setDivide(int[] divide) {
        this.divide = divide;
    }

    public int[] getExtent() {
        return extent;
    }

    public void setExtent(int[] extent) {
        this.extent = extent;
    }

    public int getNice() {
        return nice;
    }

    public void setNice(int nice) {
        this.nice = nice;
    }

}
