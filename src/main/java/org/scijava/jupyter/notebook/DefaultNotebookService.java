/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2017 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.scijava.jupyter.notebook;

import com.twosigma.beaker.mimetype.MIMEContainer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jupyter.Displayer;
import jupyter.Displayers;
import org.scijava.jupyter.notebook.displayer.ListDisplayer;
import org.scijava.jupyter.notebook.displayer.StringDisplayer;
import org.scijava.log.LogService;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * AWT-driven implementation of {@link NotebookService}.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultNotebookService extends AbstractService implements
        NotebookService {

    @Parameter
    private LogService log;

    public DefaultNotebookService() {
        Displayers.register(String.class, (Displayer) StringDisplayer.get());
        Displayers.register(List.class, (Displayer) ListDisplayer.get());
    }

    /**
     * Display a content with a given mimetype.
     *
     * @param mimetype
     * @param content
     * @return
     */
    @Override
    public Object displayMimetype(String mimetype, String content) {

        MIMEContainer.MIME mimeTypeObj = Arrays.asList(MIMEContainer.MIME.values()).stream().
                filter(m -> m.getMime().equals(mimetype)).
                findFirst().orElse(null);

        if (mimeTypeObj == null) {
            log.error("The mimetype '" + mimetype + "' is not supported");
            return null;
        } else {
            return new MIMEContainer(mimeTypeObj, content);
        }

    }

    /**
     * Try to display the object according to its type.
     *
     * @param object
     * @return
     */
    @Override
    public Object displayAuto(Object object) {

        Map<String, String> richResult = Displayers.display(object);

        // Only take the first one since Beakerx can only send one for now.
        String mimetype = (String) richResult.keySet().toArray()[0];
        String content = richResult.get(mimetype);

        return displayMimetype(mimetype, content);
    }

}
