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

package net.imagej.notebook.converter;

import net.imagej.notebook.ImageJNotebookService;
import net.imagej.notebook.ImageJNotebookService.ValueScaling;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.convert.Converter;
import org.scijava.notebook.converter.NotebookOutputConverter;
import org.scijava.notebook.converter.output.PNGImageNotebookOutput;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class)
public class RAIToPNGNotebookConverter<T extends RealType<T>>
        extends NotebookOutputConverter<RandomAccessibleInterval<T>, PNGImageNotebookOutput> {

    @Parameter
    private ImageJNotebookService ijnb;

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<RandomAccessibleInterval<T>> getInputType() {
        return (Class) RandomAccessibleInterval.class;
    }

    @Override
    public Class<PNGImageNotebookOutput> getOutputType() {
        return PNGImageNotebookOutput.class;
    }

    @Override
    public PNGImageNotebookOutput convert(Object object) {

        RandomAccessibleInterval<T> source = (RandomAccessibleInterval<T>) object;

        // NB: Assume <=3 samples in the 3rd dimension means channels. Of course,
        // we have no metadata with a vanilla RAI, but this is a best guess;
        // 3rd dimensions with >3 samples are probably something like Z or time.
        final int cAxis = source.numDimensions() > 2 && source.dimension(2) <= 3 ? 2 : -1;

        String base64Image = (String) ijnb.RAIToPNG(source, 0, 1, cAxis, ValueScaling.AUTO);

        return new PNGImageNotebookOutput(base64Image);
    }

}
