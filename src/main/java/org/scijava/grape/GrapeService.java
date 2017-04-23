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

import groovy.grape.GrapeEngine;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.scijava.service.SciJavaService;

/**
 *
 * @author Hadrien Mary
 */
public interface GrapeService extends SciJavaService {

    void addResolver(Map<String, Object> args);

    Map<String, Map<String, List<String>>> enumerateGrapes();

    /**
     * Global flag to ignore checksums. By default it is set to false.
     * @return 
     */
    boolean getDisableChecksums();

    /**
     * This is a static access auto download enabler. It will set the 'autoDownload' value to the
     * passed in arguments map if not already set. If 'autoDownload' is set the value will not be
     * adjusted.
     * <p>
     * This applies to the grab and resolve calls.
     * <p>
     * If it is set to false, only previously downloaded grapes will be used. This may cause failure
     * in the grape call if the library has not yet been downloaded
     * <p>
     * If it is set to true, then any jars not already downloaded will automatically be downloaded.
     * Also, any versions expressed as a range will be checked for new versions and downloaded (with
     * dependencies) if found.
     * <p>
     * By default it is set to true.
     * @return 
     */
    boolean getEnableAutoDownload();

    /**
     * This is a static access kill-switch. All of the static shortcut methods in this class will
     * not work if this property is set to false. By default it is set to true.
     */
    boolean getEnableGrapes();

    GrapeEngine getGrapeEngine();

    void grab(String endorsed);

    void grab(Map<String, Object> dependency);

    void grab(Map<String, Object> args, Map... dependencies);

    Map[] listDependencies(ClassLoader cl);

    URI[] resolve(Map<String, Object> args, Map... dependencies);

    URI[] resolve(Map<String, Object> args, List depsInfo, Map... dependencies);

    /**
     * Set global flag to ignore checksums. By default it is set to false.
     */
    void setDisableChecksums(boolean disableChecksums);

    /**
     * This is a static access auto download enabler. It will set the 'autoDownload' value to the
     * passed in arguments map if not already set. If 'autoDownload' is set the value will not be
     * adjusted.
     * <p>
     * This applies to the grab and resolve calls.
     * <p>
     * If it is set to false, only previously downloaded grapes will be used. This may cause failure
     * in the grape call if the library has not yet been downloaded.
     * <p>
     * If it is set to true, then any jars not already downloaded will automatically be downloaded.
     * Also, any versions expressed as a range will be checked for new versions and downloaded (with
     * dependencies) if found. By default it is set to true.
     */
    void setEnableAutoDownload(boolean enableAutoDownload);

    /**
     * This is a static access kill-switch. All of the static shortcut methods in this class will
     * not work if this property is set to false. By default it is set to true.
     *
     * @param enableGrapes
     */
    void setEnableGrapes(boolean enableGrapes);

}
