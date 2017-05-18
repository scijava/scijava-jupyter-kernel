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
package org.scijava.notebook;

import com.twosigma.beaker.mimetype.MIMEContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.imagej.notebook.ImageJNotebookService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;

import net.imglib2.type.numeric.RealType;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.notebook.converter.ouput.HTMLNotebookOutput;
import org.scijava.notebook.converter.ouput.LatexNotebookOutput;
import org.scijava.notebook.converter.ouput.MarkdownNotebookOutput;
import org.scijava.notebook.converter.ouput.NotebookOutput;

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

    @Parameter
    private ImageJNotebookService ijNotebookService;

    /**
     * Use the most appropriate output type according to the input object.
     *
     * @param object
     * @return
     */
    @Override
    public Object display(Object object) {

        if (converService.supports(object, NotebookOutput.class)) {
            return converService.convert(object, NotebookOutput.class);
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

    @Override
    public Object html(String content) {
        if (converService.supports(content, HTMLNotebookOutput.class)) {
            return converService.convert(content, HTMLNotebookOutput.class);
        } else {
            return content;
        }
    }

    @Override
    public Object markdown(String content) {
        if (converService.supports(content, MarkdownNotebookOutput.class)) {
            return converService.convert(content, MarkdownNotebookOutput.class);
        } else {
            return content;
        }
    }

    @Override
    public Object latex(String content) {
        if (converService.supports(content, LatexNotebookOutput.class)) {
            return converService.convert(content, LatexNotebookOutput.class);
        } else {
            return content;
        }
    }

    @Override
    public Object table(List<Map> table) {

        String htmlString = "<table>";

        List<String> headers = new ArrayList<>(table.get(0).keySet());

        // Set column headers
        htmlString += "<tr>";
        for (String header : headers) {
            htmlString += "<th>";
            htmlString += header;
            htmlString += "</th>";
        }
        htmlString += "</tr>";

        // Append the rows
        for (int i = 0; i < table.size(); i++) {
            htmlString += "<tr>";
            for (String header : headers) {
                htmlString += "<td>";
                htmlString += table.get(i).getOrDefault(header, "");
                htmlString += "</td>";
            }
            htmlString += "</tr>";
        }

        htmlString += "</table>";

        return new HTMLNotebookOutput(HTMLNotebookOutput.getMimeType(), htmlString);
    }

    // TODO : those methods are using the net.imagej namespace.
    // Also would it be possible to create a converter for this ?
    // With RandomAccessibleInterval[] or List<RandomAccessibleInterval> as a type ?
    public Object tiles(final int[] gridLayout, final RandomAccessibleInterval... images) {
        RandomAccessibleInterval rai = ijNotebookService.mosaic(gridLayout, images);
        return converService.convert(rai, NotebookOutput.class);
    }

}
