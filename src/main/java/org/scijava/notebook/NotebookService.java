/*-
 * #%L
 * SciJava polyglot kernel for Jupyter.
 * %%
 * Copyright (C) 2017 Hadrien Mary
 * %%
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
 * #L%
 */
package org.scijava.notebook;

import groovy.json.JsonSlurper;
import org.scijava.notebook.converter.output.HTMLNotebookOutput;
import org.scijava.notebook.converter.output.HTMLTableNotebookOutput;
import org.scijava.notebook.converter.output.ImageNotebookOutput;
import org.scijava.notebook.converter.output.JPGImageNotebookOutput;
import org.scijava.notebook.converter.output.JSONNotebookOutput;
import org.scijava.notebook.converter.output.LatexNotebookOutput;
import org.scijava.notebook.converter.output.MarkdownNotebookOutput;
import org.scijava.notebook.converter.output.NotebookOutput;
import org.scijava.notebook.converter.output.PNGImageNotebookOutput;
import org.scijava.notebook.converter.output.PlainNotebookOutput;
import org.scijava.service.SciJavaService;

/**
 * Interface for services which provide handy methods for working with
 * scientific notebook software.
 *
 * @author Curtis Rueden
 * @author Hadrien Mary
 */
public interface NotebookService extends SciJavaService {

    /**
     * Convenience enum to make it slightly easier to call {@link #display} with
     * a known finite set of options.
     */
    enum OutputType {

        HTML(HTMLNotebookOutput.class),
        IMAGE(ImageNotebookOutput.class),
        JPEG_IMAGE(JPGImageNotebookOutput.class),
        JSON(JSONNotebookOutput.class),
        LATEX(LatexNotebookOutput.class),
        MARKDOWN(MarkdownNotebookOutput.class),
        PLAIN(PlainNotebookOutput.class),
        PNG_IMAGE(PNGImageNotebookOutput.class),
        TABLE(HTMLTableNotebookOutput.class);

        private final Class<? extends NotebookOutput> outputType;

        OutputType(Class<? extends NotebookOutput> outputType) {
            this.outputType = outputType;
        }

        public Class<? extends NotebookOutput> outputType() {
            return outputType;
        }
    }

    /**
     * Converts the input object to a notebook-appropriate data type.
     *
     * @param object The object to convert to a notebook-friendly result.
     * @return A result which is displays well in a notebook.
     */
    default Object display(final Object object) {
        return display(object, NotebookOutput.class);
    }

    /**
     * Converts the input object to notebook-appropriate data of the specified
     * type.
     *
     * @param object The object to convert to a notebook-friendly result.
     * @param outputType The type of notebook-appropriate data.
     * @return A result which is displays well in a notebook.
     */
    default Object display(final Object object, final OutputType outputType) {
        return display(object, outputType.outputType());
    }

    /**
     * Converts the input object to notebook-appropriate data of the specified
     * type.
     *
     * @param object The object to convert to a notebook-friendly result.
     * @param outputType The type of notebook-appropriate data.
     * @return A result which is displays well in a notebook.
     */
    Object display(Object object, Class<? extends NotebookOutput> outputType);

    /**
     * Displays content as a given MIME type.
     *
     * @param mimetype The desired MIME type of the result.
     * @param content The content to display.
     * @return A result of the specified MIME type.
     */
    Object displayMimetype(String mimetype, String content);

    Object displayMimetype(String mimetype, Object content);

    default Object html(String content) {
        return display(content, HTMLNotebookOutput.class);
    }

    default Object markdown(String content) {
        return display(content, MarkdownNotebookOutput.class);
    }

    default Object latex(String content) {
        return display(content, LatexNotebookOutput.class);
    }

    default Object table(Object table) {
        return display(table, HTMLTableNotebookOutput.class);
    }

    default Object vega(String content) {
        JsonSlurper jsonSlurper = new JsonSlurper();
        Object json = jsonSlurper.parseText(content);
        return displayMimetype("application/vnd.vega.v2+json", json);
    }

    default Object vegalite(String content) {
        JsonSlurper jsonSlurper = new JsonSlurper();
        Object json = jsonSlurper.parseText(content);
        return displayMimetype("application/vnd.vegalite.v1+json", json);
    }
}
