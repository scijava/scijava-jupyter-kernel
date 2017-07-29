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

import java.util.Arrays;

/**
 *
 * @author hadim
 */
public class EncodingTimeUnit extends EncodingSingleValue {

    public EncodingTimeUnit() {
        this.allowedValues = Arrays.asList("year", "yearmonth", "yearmonthday", "yearmonthdate", "yearday", "yeardate", "yearmonthdayhours", "yearmonthdayhoursminutes", "utcyear", "utcyearmonth", "utcyearmonthday", "utcyearmonthdate", "utcyearday", "utcyeardate", "utcyearmonthdayhours", "utcyearmonthdayhoursminutes", "month", "day", "date", "hours", "minutes", "seconds", "milliseconds", "hoursminutes", "hoursminutesseconds", "minutesseconds", "secondsmilliseconds", "utcmonth", "utcday", "utcdate", "utchours", "utcminutes", "utcseconds", "utcmilliseconds", "utchoursminutes", "utchoursminutesseconds", "utcminutesseconds", "utcsecondsmilliseconds");
    }

    public EncodingTimeUnit(String timeUnit) throws Exception {
        this.setValue(timeUnit);
    }

}
