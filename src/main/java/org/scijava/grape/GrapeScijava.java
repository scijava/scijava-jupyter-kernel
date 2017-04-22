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
package org.scijava.grape;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
class GrapeScijava implements GrapeEngine {

    @Parameter
    LogService log;

    public GrapeScijava(Context context) {
        context.inject(this);
    }

    @Override
    public Object grab(String endorsedModule) {
        log.info("grab(String endorsedModule)");
        return null;
    }

    @Override
    public Object grab(Map args) {
        log.info("grab(Map args)");
        return null;
    }

    @Override
    public Object grab(Map args, Map... dependencies) {
        log.info("grab(Map args, Map... dependencies)");
        return null;
    }

    @Override
    public Map<String, Map<String, List<String>>> enumerateGrapes() {
        log.info("enumerateGrapes()");
        return null;
    }

    @Override
    public URI[] resolve(Map args, Map... dependencies) {
        log.info("resolve(Map args, Map... dependencies)");
        return null;
    }

    @Override
    public URI[] resolve(Map args, List depsInfo, Map... dependencies) {
        log.info("resolve(Map args, List depsInfo, Map... dependencies)");
        return null;
    }

    @Override
    public Map[] listDependencies(ClassLoader classLoader) {
        log.info("listDependencies(ClassLoader classLoader)");
        return null;
    }

    @Override
    public void addResolver(Map<String, Object> args) {
        log.info("addResolver(Map<String, Object> args)");
    }

}
