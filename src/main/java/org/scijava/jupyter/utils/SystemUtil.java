/*-
 * #%L
 * SciJava polyglot kernel for Jupyter.
 * %%
 * Copyright (C) 2017 Hadrien Mary
 * %%
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
 * #L%
 */

package org.scijava.jupyter.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.scijava.log.LogService;

/**
 *
 * @author Hadrien Mary
 */
public class SystemUtil {

    public static String getJavaBinary() {
        String jvm_location;
        if (System.getProperty("os.name").startsWith("Win")) {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
        } else {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
        return jvm_location;
    }

    public static String getImageJClassPaths() {
        String classPaths = "";

        if (System.getProperty("imagej.dir") != null) {

            String classPathSeparator = SystemUtil.getClassPathSeparator();
            classPaths += Paths.get(System.getProperty("imagej.dir"), "jars") + FileSystems.getDefault().getSeparator() + "*" + classPathSeparator;
            classPaths += Paths.get(System.getProperty("imagej.dir"), "jars", "bio-formats") + FileSystems.getDefault().getSeparator() + "*" + classPathSeparator;
            classPaths += Paths.get(System.getProperty("imagej.dir"), "plugins") + FileSystems.getDefault().getSeparator() + "*" + classPathSeparator;
        }

        return classPaths;
    }

    public static void deleteFolderRecursively(Path rootPath, LogService log) {
        if (rootPath.toFile().exists()) {
            try {
                Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    public static String getClassPathSeparator() {
        String classPathSeparator;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            classPathSeparator = ";";
        } else {
            classPathSeparator = ":";
        }
        return classPathSeparator;
    }

}
