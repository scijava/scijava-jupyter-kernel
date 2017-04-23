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

import groovy.grape.GrapeIvy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ReflectionUtils;

/**
 * I had to extend GrapeIvy to use any CLassLoader (not only GroovyClassLoader).
 *
 * @author Hadrien Mary
 */
public class GrapeScijava extends GrapeIvy {

    Map<String, List<String>> exclusiveGrabArgs = new HashMap<String, List<String>>() {
        {
            put("group", Arrays.asList("groupId", "organisation", "organization", "org"));
            put("groupId", Arrays.asList("group", "organisation", "organization", "org"));
            put("organisation", Arrays.asList("group", "groupId", "organization", "org"));
            put("organization", Arrays.asList("group", "groupId", "organisation", "org"));
            put("org", Arrays.asList("group", "groupId", "organisation", "organization"));
            put("module", Arrays.asList("artifactId", "artifact"));
            put("artifactId", Arrays.asList("module", "artifact"));
            put("artifact", Arrays.asList("module", "artifactId"));
            put("version", Arrays.asList("revision", "rev"));
            put("revision", Arrays.asList("version", "rev"));
            put("rev", Arrays.asList("version", "revision"));
            put("conf", Arrays.asList("scope", "configuration"));
            put("scope", Arrays.asList("conf", "configuration"));
            put("configuration", Arrays.asList("conf", "scope"));

        }
    };

    @Override
    public ClassLoader chooseClassLoader(Map args) {
        ClassLoader loader = (ClassLoader) args.get("classLoader");

        if (this.isValidTargetClassLoader(loader)) {
            if (args.get("refObject") == null) {
                if (!args.keySet().contains("calleeDepth")) {
                    loader = ReflectionUtils.getCallingClass((int) args.get("calleeDepth")).getClassLoader();
                } else {
                    loader = ReflectionUtils.getCallingClass(1).getClassLoader();
                }
            }

            while (loader != null && !this.isValidTargetClassLoader(loader)) {
                loader = loader.getParent();
            }
            //if (!isValidTargetClassLoader(loader)) {
            //    loader = Thread.currentThread().contextClassLoader
            //}
            //if (!isValidTargetClassLoader(loader)) {
            //    loader = GrapeIvy.class.classLoader
            //}
            if (!isValidTargetClassLoader(loader)) {
                throw new RuntimeException("No suitable ClassLoader found for grab");
            }
        }
        return loader;
    }

    private boolean isValidTargetClassLoader(ClassLoader loader) {
        return true;
    }

    private boolean isValidTargetClassLoaderClass(Class loaderClass) {
        return true;
    }
}
