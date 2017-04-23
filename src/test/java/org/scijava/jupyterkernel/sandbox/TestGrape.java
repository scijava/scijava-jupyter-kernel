/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.sandbox;

import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.scijava.Context;
import org.scijava.grape.GrapeService;
import org.scijava.log.LogService;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;

/**
 *
 * @author Hadrien Mary
 */
public class TestGrape {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Context context = new Context();

        // Get services
        LogService log = context.getService(LogService.class);
        GrapeService grape = context.getService(GrapeService.class);
        ScriptService scriptService = context.getService(ScriptService.class);
        
        // Init scripting
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName("groovy");
        ScriptEngine engine = scriptLanguage.getScriptEngine();

        Map<String, Object> paramsGrab = new HashMap<String, Object>() {
            {
                put("group", "org.springframework");
                put("module", "spring-orm");
                put("version", "3.2.5.RELEASE");
            }
        };

        // Grab the required dependency.
        grape.grab(paramsGrab);

        // Now execute some groovy code and import the "newly injected" class.
        try {
            engine.eval("import org.springframework.jdbc.core.JdbcTemplate; println JdbcTemplate");
        } catch (ScriptException ex) {
            log.info(ex);
        }

        context.dispose();
    }

}
