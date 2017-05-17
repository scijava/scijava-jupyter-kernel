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
import org.scijava.service.SciJavaService;

/**
 * Interface for services which provide handy methods for working with
 * scientific notebook software.
 *
 * @author Curtis Rueden
 */
public interface NotebookService extends SciJavaService {

    /**
     * Converts the input object to a notebook-appropriate data type.
     *
     * @param object The object to convert to a notebook-friendly result.
     * @return A result which is displays well in a notebook.
     */
    public Object display(Object object);

    /**
     * Displays content as a given MIME type.
     *
     * @param mimetype The desired MIME type of the result.
     * @param content The content to display.
     * @return A result of the specified MIME type.
     */
    public Object displayMimetype(String mimetype, String content);
    
    public Object html(String content);
    
    public Object markdown(String content);
    
    public Object latex(String content);
    
    public Object table(List<Map<?, ?>> table);
}
