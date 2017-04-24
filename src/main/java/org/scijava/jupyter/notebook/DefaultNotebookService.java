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
import java.util.Map;
import net.imagej.Dataset;
import org.scijava.convert.ConvertService;
import org.scijava.jupyter.notebook.converter.ouput.JSONNotebookOutput;
import org.scijava.jupyter.notebook.converter.ouput.PNGImageNotebookOutput;
import org.scijava.jupyter.notebook.converter.ouput.PlainNotebookOutput;
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

    @Parameter
    private ConvertService converService;

    /**
     * Use the most appropriate output type according to the input object.
     *
     * @param object
     * @return
     */
    @Override
    public Object display(Object object) {

        if (object.getClass() == String.class) {
            return converService.convert(object, PlainNotebookOutput.class);

        } else if (object.getClass() == Map.class) {
            return converService.convert(object, JSONNotebookOutput.class);

        } else if (Dataset.class.isAssignableFrom(object.getClass())) {
            return converService.convert(object, PNGImageNotebookOutput.class);

        } else {
            return object;
        }
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
            log.warn("The mimetype '" + mimetype + "' is not supported");
            return content;
        } else {
            return new MIMEContainer(mimeTypeObj, content);
        }

    }

}
