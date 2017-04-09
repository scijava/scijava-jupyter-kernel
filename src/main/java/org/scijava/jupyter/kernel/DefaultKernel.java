/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyter.kernel;

import com.twosigma.beaker.evaluator.Evaluator;

import com.twosigma.beaker.jupyter.handler.CommOpenHandler;
import com.twosigma.jupyter.KernelRunner;
import com.twosigma.jupyter.handler.KernelHandler;
import com.twosigma.jupyter.message.Message;
import com.twosigma.jupyter.Kernel;

import java.io.IOException;

import static com.twosigma.beaker.jupyter.Utils.uuid;
import java.util.logging.Level;
import org.scijava.Context;
import org.scijava.jupyter.DefaultKernelConfigurationFile;
import org.scijava.jupyter.comm.DefaultCommOpenHandler;
import org.scijava.jupyter.evaluator.DefaultEvaluator;
import org.scijava.jupyter.handler.DefaultKernelInfoHandler;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultKernel extends Kernel {

    // Scijava context
    Context context;

    @Parameter
    private transient LogService log;

    public DefaultKernel(final Context context, final String id, final Evaluator evaluator, DefaultKernelConfigurationFile config) {
        super(id, evaluator, config);
        this.context = context;
        this.context.inject(this);

        log.info("Default Kernel started.");
        log.info("Language used : " + config.getLanguageName());
    }

    @Override
    public CommOpenHandler getCommOpenHandler(Kernel kernel) {
        return new DefaultCommOpenHandler(kernel);
    }

    @Override
    public KernelHandler<Message> getKernelInfoHandler(Kernel kernel) {
        return new DefaultKernelInfoHandler(kernel);
    }

    private void setLogLevel(String logLevel) {
        switch (logLevel) {
            case "debug":
                this.log.setLevel(LogService.DEBUG);
                break;
            case "error":
                this.log.setLevel(LogService.ERROR);
                break;
            case "info":
                this.log.setLevel(LogService.INFO);
                break;
            case "none":
                this.log.setLevel(LogService.NONE);
                break;
            default:
                this.log.setLevel(LogService.INFO);
                break;
        }
    }

    public static void main(final String[] args) throws InterruptedException, IOException {

        KernelRunner.run(() -> {
            Context context = new Context();
            String id = uuid();
            DefaultKernelConfigurationFile config = new DefaultKernelConfigurationFile(context, args);
            Evaluator evaluator = new DefaultEvaluator(context, id, id, config.getLanguageName());
            return new DefaultKernel(context, id, evaluator, config);
        });
    }
}
