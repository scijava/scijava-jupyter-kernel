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

package org.scijava.notebook.converter.ouput;

import com.twosigma.beaker.mimetype.MIMEContainer;

/**
 *
 * @author hadim
 */
public class LatexNotebookOutput extends NotebookOutput {

    public static MIMEContainer.MIME getMimeType() {
        return MIMEContainer.MIME.TEXT_LATEX;
    }

    public LatexNotebookOutput(MIME mimeTypeObj, String content) {
        super(mimeTypeObj, content);
    }

}
