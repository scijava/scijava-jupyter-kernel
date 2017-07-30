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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EncodingChannel {

	@JsonProperty
	private String field;

	@JsonProperty
	private String type;

	@JsonProperty
	private String timeUnit;

	@JsonProperty
	private String aggregate;

	@JsonProperty
	private String stack;

	@JsonProperty
	private String sort;

	@JsonProperty
	private String value;

	// TODO
	@JsonProperty
	private String scale;

	// TODO
	@JsonProperty
	private Boolean axis;

	// TODO
	@JsonProperty
	private Boolean legend;

	@JsonProperty
	private EncodingBin bin;

	private final List<String> allowedType = Arrays.asList("quantitative", "temporal", "ordinal", "nominal", "Q", "T",
			"O", "N");

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (!this.allowedType.contains(type)) {
			try {
				throw new Exception(
						"'" + type + "' is not allowed. Please choose in this lis: " + this.allowedType.toString());
			} catch (Exception ex) {
				Logger.getLogger(EncodingChannel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		this.type = type;
	}

	public EncodingBin getBin() {
		return bin;
	}

	public void setBin(EncodingBin bin) {
		this.bin = bin;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public Boolean getAxis() {
		return axis;
	}

	public void setAxis(Boolean axis) {
		this.axis = axis;
	}

	public Boolean getLegend() {
		return legend;
	}

	public void setLegend(Boolean legend) {
		this.legend = legend;
	}
}
