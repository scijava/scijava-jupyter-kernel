/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.scijava.Context;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;

/**
 *
 * @author Hadrien Mary
 */
public class TestScriptEngine {

    public static void main(String[] args) throws ScriptException {
        // Only for testing purpose

        Context context = new Context();
        ScriptService scriptService = context.getService(ScriptService.class);
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName("python");
        ScriptEngine engine = scriptLanguage.getScriptEngine();

        engine.eval("print('Hello')");
        Object result = (Object) engine.eval("9+3");
        
        System.out.println(result);

        context.dispose();
    }

}
