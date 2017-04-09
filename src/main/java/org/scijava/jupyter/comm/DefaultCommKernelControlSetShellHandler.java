/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyter.comm;

import com.twosigma.jupyter.KernelFunctionality;
import com.twosigma.beaker.jupyter.comm.KernelControlGetDefaultShellHandler;
import org.scijava.jupyter.DefaultVariables;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultCommKernelControlSetShellHandler extends KernelControlGetDefaultShellHandler {

    protected DefaultVariables var = new DefaultVariables();

    public DefaultCommKernelControlSetShellHandler(KernelFunctionality kernel) {
        super(kernel);
    }

    @Override
    public String[] getDefaultImports() {
        return var.getImportsAsArray();
    }

    @Override
    public String[] getDefaultClassPath() {
        return var.getClassPathAsArray();
    }

}
