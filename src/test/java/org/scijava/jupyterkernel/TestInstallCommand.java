/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.imagej.ImageJ;
import net.imagej.Main;
import org.scijava.jupyter.commands.InstallScijavaKernel;

/**
 *
 * @author Hadrien Mary
 */
public class TestInstallCommand {

    public static void main(String... args) {

        File pythonBinaryPath = new File("/home/hadim/local/conda/bin/python");

        ImageJ ij = Main.launch(args);

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("pythonBinaryPath", pythonBinaryPath);
        inputMap.put("scriptLanguage", "groovy");
        inputMap.put("logLevel", "info");

        ij.command().run(InstallScijavaKernel.class, true, inputMap);

    }

}
