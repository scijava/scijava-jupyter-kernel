package org.scijava.jupyter.service;

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
import java.io.File;
import java.nio.file.Path;
import org.scijava.service.SciJavaService;

/**
 *
 * @author Hadrien Mary
 */
public interface JupyterService extends SciJavaService {

    /* Install kernel */
    void installKernel(String scriptLanguage, String logLevel, String pythonBinaryPath);

    void installKernel(String scriptLanguage, String logLevel, Path pythonBinaryPath);

    void installKernel(String scriptLanguage, String logLevel, File pythonBinaryPath);

    void installKernel(String scriptLanguage, String logLevel, String pythonBinaryPath, boolean installAllKernels);

    void installKernel(String scriptLanguage, String logLevel, Path pythonBinaryPath, boolean installAllKernels);

    void installKernel(String scriptLanguage, String logLevel, File pythonBinaryPath, boolean installAllKernels);

    /* Run kernel */
    void runKernel(String... args);

    void runKernel(String scriptLanguage, String logLevel, String connectionFile);

    void runKernel(String scriptLanguage, String logLevel, File connectionFile);

    void runKernel(String scriptLanguage, String logLevel, Path connectionFile);
}
