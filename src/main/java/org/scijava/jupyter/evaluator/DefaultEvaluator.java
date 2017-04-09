/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyter.evaluator;

import com.twosigma.beaker.autocomplete.AutocompleteResult;
import com.twosigma.beaker.evaluator.Evaluator;
import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;
import java.io.IOException;

/**
 *
 * @author Hadrien Mary
 */
public class DefaultEvaluator implements Evaluator {

    public DefaultEvaluator(String id, String id0) {
    }

    @Override
    public void setShellOptions(String string, String string1) throws IOException {
    }

    @Override
    public AutocompleteResult autocomplete(String string, int i) {
        return null;
    }

    @Override
    public void killAllThreads() {
    }

    @Override
    public void evaluate(SimpleEvaluationObject seo, String string) {
    }

    @Override
    public void startWorker() {
    }

    @Override
    public void exit() {
    }

}
