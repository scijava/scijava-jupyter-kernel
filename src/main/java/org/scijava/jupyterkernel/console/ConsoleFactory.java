/*
 * Copyright 2016 kay schluehr.
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
package org.scijava.jupyterkernel.console;

import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptService;

/**
 *
 * @author kay schluehr
 */
public class ConsoleFactory {
    
    @Parameter
	public ScriptService scriptService;

    private ConsoleFactory(Context context) {
        context.inject(this);
    }
    
    public static InteractiveConsole createConsole(String name) {
        switch (name) {
            case "python":
                return new JythonConsole();
            case "clojure":
                return new ClojureConsole();
            default:
                return new InteractiveConsole(name);
        }
    }

    public static void main(String[] args) {
        System.out.println("sss");
        
        Context context = new Context();
        
        ConsoleFactory test = new ConsoleFactory(context);
        System.out.println(test.scriptService.getLanguages());
    }

    
}
