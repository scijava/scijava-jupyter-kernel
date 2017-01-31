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

import java.util.ArrayDeque;
import java.io.StringWriter;
import org.scijava.jupyterkernel.json.messages.T_stream;
import org.scijava.jupyterkernel.kernel.MessageObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author kay schluehr
 *
 * Used to stream data to stdout during code execution in 'exec' mode.
 *
 * NOTE: 'single' mode execution should produce output in an Out[] cell. Since
 * data from stdout won't be streamed the result of print() actions will be
 * collected and returned in the end.
 */
public class JupyterStreamWriter extends StringWriter {

    MessageObject message;
    Timer timer = new Timer();

    public JupyterStreamWriter(MessageObject stream) {
        super();
        this.message = stream;
        streaming();
    }
 
    public void stopStreaming() {
        timer.cancel();
        // one last shot
        StringBuffer sb = getBuffer();
        synchronized (sb) {
            if (sb.length() > 0) {
                String S = sb.toString();
                flush();
                sb.delete(0, sb.length());
                ((T_stream) message.msg.content).text = S;
                message.send();
            }
        }
    }

    private void streaming() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StringBuffer sb = getBuffer();
                synchronized (sb) {
                    if (sb.length() > 0) {
                        String S = sb.toString();
                        flush();
                        sb.delete(0, sb.length());
                        ((T_stream) message.msg.content).text = S;
                        message.send();
                    }
                }
            }
        }, 250, 250);
    }
}
