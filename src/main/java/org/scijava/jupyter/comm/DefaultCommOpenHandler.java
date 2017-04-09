/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyter.comm;

import com.twosigma.jupyter.KernelFunctionality;
import com.twosigma.jupyter.handler.Handler;
import com.twosigma.jupyter.message.Message;
import com.twosigma.beaker.jupyter.comm.KernelControlCommandListHandler;
import com.twosigma.beaker.jupyter.comm.KernelControlInterrupt;
import com.twosigma.beaker.jupyter.comm.KernelControlSetShellHandler;
import com.twosigma.beaker.jupyter.comm.TargetNamesEnum;
import com.twosigma.beaker.jupyter.handler.CommOpenHandler;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultCommOpenHandler extends CommOpenHandler {

    private final Handler<?>[] KERNEL_CONTROL_CHANNEL_HANDLERS = {
        new KernelControlSetShellHandler(kernel),
        new DefaultCommKernelControlSetShellHandler(kernel),
        new KernelControlInterrupt(kernel),
        new KernelControlCommandListHandler(kernel)
    };

    public DefaultCommOpenHandler(KernelFunctionality kernel) {
        super(kernel);
    }

    @Override
    public Handler<Message>[] getKernelControlChanelHandlers(String targetName) {
        if (TargetNamesEnum.KERNEL_CONTROL_CHANNEL.getTargetName().equalsIgnoreCase(targetName)) {
            return (Handler<Message>[]) KERNEL_CONTROL_CHANNEL_HANDLERS;
        } else {
            return (Handler<Message>[]) new Handler<?>[0];
        }
    }

}
