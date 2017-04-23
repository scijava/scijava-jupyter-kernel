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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.net.URI;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Facade to GrapeEngine. Kindly stolen from
 * https://github.com/apache/groovy/blob/master/src/main/groovy/grape/Grape.java.
 */
@Plugin(type = Service.class)
public class DefaultGrapeService extends AbstractService implements GrapeService {

    public static final String AUTO_DOWNLOAD_SETTING = "autoDownload";
    public static final String DISABLE_CHECKSUMS_SETTING = "disableChecksums";
    public static final String SYSTEM_PROPERTIES_SETTING = "systemProperties";

    private boolean enableGrapes = Boolean.valueOf(System.getProperty("org.scijava.grape.enable", "true"));
    private boolean enableAutoDownload = Boolean.valueOf(System.getProperty("org.scijava.grape.autoDownload", "true"));
    private boolean disableChecksums = Boolean.valueOf(System.getProperty("org.scijava.grape.disableChecksums", "false"));

    private GrapeEngine grapeEngine = null;

    /**
     * This is a static access kill-switch. All of the static shortcut methods in this class will
     * not work if this property is set to false. By default it is set to true.
     *
     * @return
     */
    @Override
    public boolean getEnableGrapes() {
        return enableGrapes;
    }

    /**
     * This is a static access kill-switch. All of the static shortcut methods in this class will
     * not work if this property is set to false. By default it is set to true.
     *
     * @param enableGrapes
     */
    @Override
    public void setEnableGrapes(boolean enableGrapes) {
        this.enableGrapes = enableGrapes;
    }

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
     */
    @Override
    public boolean getEnableAutoDownload() {
        return enableAutoDownload;
    }

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
     *
     * @param enableAutoDownload
     */
    @Override
    public void setEnableAutoDownload(boolean enableAutoDownload) {
        this.enableAutoDownload = enableAutoDownload;
    }

    /**
     * Global flag to ignore checksums. By default it is set to false.
     */
    @Override
    public boolean getDisableChecksums() {
        return disableChecksums;
    }

    /**
     * Set global flag to ignore checksums. By default it is set to false.
     *
     * @param disableChecksums
     */
    @Override
    public void setDisableChecksums(boolean disableChecksums) {
        this.disableChecksums = disableChecksums;
    }

    @Override
    public GrapeEngine getGrapeEngine() {
        if (this.grapeEngine == null) {
            this.grapeEngine = new GrapeScijava();
        }
        return grapeEngine;
    }

    @Override
    public void grab(String endorsed) {
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                instance.grab(endorsed);
            }
        }
    }

    @Override
    public void grab(Map<String, Object> dependency) {
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                if (!dependency.containsKey(AUTO_DOWNLOAD_SETTING)) {
                    dependency.put(AUTO_DOWNLOAD_SETTING, enableAutoDownload);
                }
                if (!dependency.containsKey(DISABLE_CHECKSUMS_SETTING)) {
                    dependency.put(DISABLE_CHECKSUMS_SETTING, disableChecksums);
                }

                if (!dependency.keySet().contains("classLoader")) {
                    dependency.put("classLoader", this.context().getClass().getClassLoader());
                }

                instance.grab(dependency);
            }
        }
    }

    @Override
    public void grab(Map<String, Object> args, Map... dependencies) {
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                if (!args.containsKey(AUTO_DOWNLOAD_SETTING)) {
                    args.put(AUTO_DOWNLOAD_SETTING, enableAutoDownload);
                }
                if (!args.containsKey(DISABLE_CHECKSUMS_SETTING)) {
                    args.put(DISABLE_CHECKSUMS_SETTING, disableChecksums);
                }

                if (!args.keySet().contains("classLoader")) {
                    args.put("classLoader", this.context().getClass().getClassLoader());
                }

                instance.grab(args, dependencies);
            }
        }
    }

    @Override
    public Map<String, Map<String, List<String>>> enumerateGrapes() {
        Map<String, Map<String, List<String>>> grapes = null;
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                grapes = instance.enumerateGrapes();
            }
        }
        if (grapes == null) {
            return Collections.emptyMap();
        } else {
            return grapes;
        }
    }

    @Override
    public URI[] resolve(Map<String, Object> args, Map... dependencies) {
        return resolve(args, null, dependencies);
    }

    @Override
    public URI[] resolve(Map<String, Object> args, List depsInfo, Map... dependencies) {
        URI[] uris = null;
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                if (!args.containsKey(AUTO_DOWNLOAD_SETTING)) {
                    args.put(AUTO_DOWNLOAD_SETTING, enableAutoDownload);
                }
                if (!args.containsKey(DISABLE_CHECKSUMS_SETTING)) {
                    args.put(DISABLE_CHECKSUMS_SETTING, disableChecksums);
                }
                uris = instance.resolve(args, depsInfo, dependencies);
            }
        }
        if (uris == null) {
            return new URI[0];
        } else {
            return uris;
        }
    }

    @Override
    public Map[] listDependencies(ClassLoader cl) {
        Map[] maps = null;
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                maps = instance.listDependencies(cl);
            }
        }
        if (maps == null) {
            return new Map[0];
        } else {
            return maps;
        }

    }

    @Override
    public void addResolver(Map<String, Object> args) {
        if (enableGrapes) {
            GrapeEngine instance = getGrapeEngine();
            if (instance != null) {
                instance.addResolver(args);
            }
        }
    }
}
