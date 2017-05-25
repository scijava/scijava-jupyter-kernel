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

package org.scijava.jupyter.kernel;

import org.scijava.Priority;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UserInterface;
import org.scijava.ui.viewer.AbstractDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

/**
 * A dummy {@link DisplayViewer} implementation so that outputs are never shown during
 * module postprocessing.
 *
 * @author Curtis Rueden
 */
@Plugin(type = DisplayViewer.class, priority = Priority.VERY_HIGH_PRIORITY)
public class DummyDisplayViewer extends AbstractDisplayViewer<Object> {

    @Override
    public boolean isCompatible(UserInterface ui) {
        return true;
    }

    @Override
    public boolean canView(Display<?> d) {
        return true;
    }


}
