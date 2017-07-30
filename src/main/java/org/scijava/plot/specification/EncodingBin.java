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

import java.util.List;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EncodingBin {

	@JsonProperty
	private Integer maxbins;

	@JsonProperty
	private Integer base;

	@JsonProperty
	private Integer step;

	@JsonProperty
	private List<Integer> steps;

	@JsonProperty
	private Integer minstep;

	@JsonProperty
	private List<Integer> divide;

	@JsonProperty
	private List<Integer> extent;

	@JsonProperty
	private Integer nice;

	public Integer getMaxbins() {
		return maxbins;
	}

	public void setMaxbins(Integer maxbins) {
		this.maxbins = maxbins;
	}

	public Integer getBase() {
		return base;
	}

	public void setBase(Integer base) {
		this.base = base;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public List<Integer> getSteps() {
		return steps;
	}

	public void setSteps(List<Integer> steps) {
		this.steps = steps;
	}

	public Integer getMinstep() {
		return minstep;
	}

	public void setMinstep(Integer minstep) {
		this.minstep = minstep;
	}

	public List<Integer> getDivide() {
		return divide;
	}

	public void setDivide(List<Integer> divide) {
		this.divide = divide;
	}

	public List<Integer> getExtent() {
		return extent;
	}

	public void setExtent(List<Integer> extent) {
		this.extent = extent;
	}

	public Integer getNice() {
		return nice;
	}

	public void setNice(Integer nice) {
		this.nice = nice;
	}

}
