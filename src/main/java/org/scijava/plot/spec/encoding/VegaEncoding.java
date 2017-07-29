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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author hadim
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VegaEncoding {

    @JsonProperty
    private EncodingChannel x;

    @JsonProperty
    private EncodingChannel y;

    @JsonProperty
    private EncodingChannel row;

    @JsonProperty
    private EncodingChannel column;

    @JsonProperty
    private EncodingChannel color;

    @JsonProperty
    private EncodingChannel order;

    @JsonProperty
    private EncodingChannel shape;

    @JsonProperty
    private EncodingChannel size;

    @JsonProperty
    private EncodingChannel opacity;

    @JsonProperty
    private EncodingChannel text;

    @JsonProperty
    private EncodingChannel tooltip;

    @JsonProperty
    private EncodingChannel detail;

    public EncodingChannel getX() {
        if (x == null) {
            this.x = new EncodingChannel();
        }
        return x;
    }

    public void setX(EncodingChannel x) {
        this.x = x;
    }

    public EncodingChannel getY() {
        if (y == null) {
            this.y = new EncodingChannel();
        }
        return y;
    }

    public void setY(EncodingChannel y) {
        this.y = y;
    }

    public EncodingChannel getRow() {
        if (row == null) {
            this.row = new EncodingChannel();
        }
        return row;
    }

    public void setRow(EncodingChannel row) {
        this.row = row;
    }

    public EncodingChannel getColumn() {
        if (column == null) {
            this.column = new EncodingChannel();
        }
        return column;
    }

    public void setColumn(EncodingChannel column) {
        this.column = column;
    }

    public EncodingChannel getColor() {
        if (color == null) {
            this.color = new EncodingChannel();
        }
        return color;
    }

    public void setColor(EncodingChannel color) {
        this.color = color;
    }

    public EncodingChannel getOrder() {
        if (order == null) {
            this.order = new EncodingChannel();
        }
        return order;
    }

    public void setOrder(EncodingChannel order) {
        this.order = order;
    }

    public EncodingChannel getShape() {
        if (shape == null) {
            this.shape = new EncodingChannel();
        }
        return shape;
    }

    public void setShape(EncodingChannel shape) {
        this.shape = shape;
    }

    public EncodingChannel getSize() {
        if (size == null) {
            this.size = new EncodingChannel();
        }
        return size;
    }

    public void setSize(EncodingChannel size) {
        this.size = size;
    }

    public EncodingChannel getOpacity() {
        if (opacity == null) {
            this.opacity = new EncodingChannel();
        }
        return opacity;
    }

    public void setOpacity(EncodingChannel opacity) {
        this.opacity = opacity;
    }

    public EncodingChannel getText() {
        if (tooltip == null) {
            this.tooltip = new EncodingChannel();
        }
        return tooltip;
    }

    public void setText(EncodingChannel text) {
        this.text = text;
    }

    public EncodingChannel getTooltip() {
        if (tooltip == null) {
            this.tooltip = new EncodingChannel();
        }
        return tooltip;
    }

    public void setTooltip(EncodingChannel tooltip) {
        this.tooltip = tooltip;
    }

    public EncodingChannel getDetail() {
        if (detail == null) {
            this.detail = new EncodingChannel();
        }
        return detail;
    }

    public void setDetail(EncodingChannel detail) {
        this.detail = detail;
    }

}
