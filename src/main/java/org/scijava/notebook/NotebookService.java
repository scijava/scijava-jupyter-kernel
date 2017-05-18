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

import java.util.List;
import java.util.Map;

import org.scijava.notebook.converter.ouput.HTMLNotebookOutput;
import org.scijava.notebook.converter.ouput.HTMLTableNotebookOutput;
import org.scijava.notebook.converter.ouput.ImageNotebookOutput;
import org.scijava.notebook.converter.ouput.JPGImageNotebookOutput;
import org.scijava.notebook.converter.ouput.JSONNotebookOutput;
import org.scijava.notebook.converter.ouput.LatexNotebookOutput;
import org.scijava.notebook.converter.ouput.MarkdownNotebookOutput;
import org.scijava.notebook.converter.ouput.NotebookOutput;
import org.scijava.notebook.converter.ouput.PNGImageNotebookOutput;
import org.scijava.notebook.converter.ouput.PlainNotebookOutput;
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
    
    Object html(String content);
    
    Object markdown(String content);
    
    Object latex(String content);
    
    Object table(List<Map<?, ?>> table);
}
