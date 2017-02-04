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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import org.scijava.jupyterkernel.kernel.MessageObject;

/**
 *
 * @author kay schluehr
 */
public class ConsoleInputReader extends Reader {

    MessageObject requestInputMessage;
    CharArrayReader userInput = null;
    
    public ConsoleInputReader(MessageObject message)
    {
        super();
        requestInputMessage = message;
    }
            

    public void reply_input(String userInput) {
        this.userInput = new CharArrayReader(userInput.toCharArray());

        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public int read(char[] cbuff, int offs, int len) throws IOException {
        // delegate input to notebook client and wait for a response
        
        // TODO: input() or raw_input() don't seem to work. The input_request 
        //       message has no effect
        // TODO: why is allow_stdin = true in execute_request if that functionality isn't supported?
        // requestInputMessage.send();
        throw new UnsupportedFunction("Reading from stdin is not supported by the notebook.");
        /*
        if(userInput == null)
        {
            requestInputMessage.send();
            synchronized(lock)
            {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConsoleInputReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        int res = userInput.read(cbuff, offs, len);
        if (res == -1) {
            userInput = null;
        }
        return res;
                */
    }

    @Override
    public void close() throws IOException {
        
    }
}
