/*
 * Copyright 2016 kay schluehr.
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
package org.scijava.jupyterkernel.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 *
 * @author kay schluehr
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONField {
    String type() default "Simple";
    
    /**
     *
     * Version semantics
     * 
     *      N.M+    -- all versions greater than or equal to N.M
     *      N.M-    -- deprecated in version N.M but allowed for all versions
     *                 lower than N.M ( but greater or equal to 5.0 ).
     * 
     * 
     * @return 
     */
    String version() default "5.0+";  // minimum version
}
