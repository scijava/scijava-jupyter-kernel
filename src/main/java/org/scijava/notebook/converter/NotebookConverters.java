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

package org.scijava.notebook.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 * Utility class for converters.
 *
 * @author Alison Walter
 */
public class NotebookConverters {

    @Parameter
    private static LogService log;

    // TODO: Move this further upstream, possible in SciJava AWT?
    public static String toPNG(BufferedImage bi) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "PNG", os);
        } catch (IOException ex) {
            log.error(ex);
        }
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }
}
