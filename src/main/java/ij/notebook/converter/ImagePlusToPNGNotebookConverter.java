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

package ij.notebook.converter;

import org.scijava.convert.Converter;
import org.scijava.log.LogService;
import org.scijava.notebook.converter.NotebookConverters;
import org.scijava.notebook.converter.NotebookOutputConverter;
import org.scijava.notebook.converter.output.PNGImageNotebookOutput;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;

/**
 * Converts an {@link ImagePlus} to a PNG.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class ImagePlusToPNGNotebookConverter extends
    NotebookOutputConverter<ImagePlus, PNGImageNotebookOutput>
{
    @Parameter
    private LogService log;

    @Override
    public Class<ImagePlus> getInputType() {
        return ImagePlus.class;
    }

    @Override
    public Class<PNGImageNotebookOutput> getOutputType() {
        return PNGImageNotebookOutput.class;
    }

    @Override
    public PNGImageNotebookOutput convert(final Object object) {
        final ImagePlus imgPlus = (ImagePlus) object;
        final String base64Image = NotebookConverters.toPNG(imgPlus.getBufferedImage());
        return new PNGImageNotebookOutput(base64Image);
    }
}
