/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.sandbox;

import groovy.lang.GroovyClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.codehaus.groovy.tools.LoaderConfiguration;
import org.codehaus.groovy.tools.RootLoader;
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

        LogService log = context.getService(LogService.class);
        GrapeService grape = context.getService(GrapeService.class);

        ScriptService scriptService = context.getService(ScriptService.class);
        ScriptLanguage scriptLanguage = scriptService.getLanguageByName("groovy");
        ScriptEngine engine = scriptLanguage.getScriptEngine();

        ClassLoader classLoader = new GroovyClassLoader();

//        Map<String, Object> paramsResolver = new HashMap<String, Object>() {
//            {
//                put("name", "restlet");
//                put("root", "http://maven.restlet.org");
//                put("classLoader", new GroovyClassLoader());
//            }
//        };
//        grape.resolve(paramsResolver);

        Map<String, Object> paramsGrab = new HashMap<String, Object>() {
            {
                put("group", "org.springframework");
                put("module", "spring-orm");
                put("version", "3.2.5.RELEASE");
                put("classLoader", context.getClass().getClassLoader());
            }
        };

        grape.grab(paramsGrab);
        try {
            log.info(classLoader.loadClass("java.lang.String"));
            log.info(classLoader.loadClass("org.springframework.jdbc.core.JdbcTemplate"));
        } catch (ClassNotFoundException ex) {
            log.info(ex);
        }

        try {
            engine.eval("import java.lang.String");
            engine.eval("import org.springframework.jdbc.core.JdbcTemplate; println JdbcTemplate");
        } catch (ScriptException ex) {
            log.info(ex);
        }

        context.dispose();
    }

}
