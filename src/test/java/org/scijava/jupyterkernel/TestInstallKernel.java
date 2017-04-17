/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel;

import org.scijava.Context;
import org.scijava.jupyter.service.JupyterService;

/**
 *
 * @author Hadrien Mary
 */
public class TestInstallKernel {

    public static void main(String... args) {

        String pythonBinaryPath = "/home/hadim/local/conda/bin/python";

        Context context = new Context();
        JupyterService jupyter = context.service(JupyterService.class);
        
        jupyter.installKernel("groovy", "info", pythonBinaryPath);
        
        context.dispose();

    }

}
