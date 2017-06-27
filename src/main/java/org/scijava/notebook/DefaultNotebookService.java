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

import com.twosigma.beakerx.mimetype.MIMEContainer;
import java.util.Arrays;

import net.imagej.notebook.ImageJNotebookService;
import net.imglib2.RandomAccessibleInterval;

import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.notebook.converter.output.NotebookOutput;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * AWT-driven implementation of {@link NotebookService}.
 *
 * @author Curtis Rueden
 * @author Hadrien Mary
 */
@Plugin(type = Service.class)
public class DefaultNotebookService extends AbstractService implements
        NotebookService {

    @Parameter
    private LogService log;

    @Parameter
    private ConvertService convertService;

    @Parameter
    private ImageJNotebookService ijNotebookService;

    @Override
    public Object display(final Object object,
        final Class<? extends NotebookOutput> outputType)
    {
        if (convertService.supports(object, outputType)) {
            return convertService.convert(object, outputType);
        }
        return object;
    }

    @Override
    public Object displayMimetype(String mimetype, String content) {

        MIMEContainer.MIME mimeTypeObj = Arrays.asList(MIMEContainer.MIME.values()).stream().
                filter(m -> m.getMime().equals(mimetype)).
                findFirst().orElse(null);

        if (mimeTypeObj == null) {
            log.warn("The mimetype '" + mimetype + "' is not supported");
            return content;
        }
        return new MIMEContainer(mimeTypeObj, content);

    }

    // TODO : those methods are using the net.imagej namespace.
    // Also would it be possible to create a converter for this ?
    // With RandomAccessibleInterval[] or List<RandomAccessibleInterval> as a type ?
    public Object tiles(final int[] gridLayout, final RandomAccessibleInterval... images) {
        RandomAccessibleInterval rai = ijNotebookService.mosaic(gridLayout, images);
        return convertService.convert(rai, NotebookOutput.class);
    }

}
