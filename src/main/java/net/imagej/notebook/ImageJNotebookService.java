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

package net.imagej.notebook;

import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imagej.axis.Axes;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * Interface for services which provide handy methods for working with scientific notebook software
 * (e.g.,
 * <a href="http://beakernotebook.com/">Beaker Notebook</a>).
 *
 * @author Curtis Rueden
 */
public interface ImageJNotebookService extends ImageJService {

    /**
     * Strategy to use for scaling the image intensity values.
     */
    enum ValueScaling {
        /**
         * Scales the RAIToPNG according to a "best effort": "narrow" types with few sample values
 (e.g., {@code bit}, {@code uint2}, {@code uint4} and {@code uint8}) are scaled according
         * to the {@code FULL} strategy, whereas "wide" types with many possible values (e.g.,
         * {@code uint16}, {@code float32} and {@code float64}) are scaled according to the
         * {@code DATA} strategy.
         * <p>
         * That rationale is that people are accustomed to seeing narrow image types rendered across
         * the full range, whereas wide image types typically do not empass the entire range of the
         * type and rendering them as such results in image which appear all or mostly black or
         * gray.
         * </p>
         */
        AUTO,
        /**
         * Scales the RAIToPNG to match the bounds of the data type. For example, {@code uint8} will
         * be scaled to 0-255, regardless of the actual data values.
         */
        FULL,
        /**
         * Scales the RAIToPNG to match the actual min and max values of the data. For example, a
         * {@code uint16} dataset with sample values ranging between 139 and 3156 will map 139 to
         * minimum intensity and 3156 to maximum intensity.
         */
        DATA
    }

    /**
     * Converts the given image to a form renderable by scientific notebooks.
     *
     * @param source The image to render.
     * @return an object that the notebook knows how to draw onscreen.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default Object display(final Dataset source) {
        return RAIToPNG((Img) source, //
                source.dimensionIndex(Axes.X), //
                source.dimensionIndex(Axes.Y), //
                source.dimensionIndex(Axes.CHANNEL), ValueScaling.AUTO);
    }

    /**
     * Converts the given image to a form renderable by scientific notebooks.
     *
     * @param <T>
     * @param source The image to render.
     * @return an object that the notebook knows how to draw onscreen.
     */
    default <T extends RealType<T>> Object display(
            final RandomAccessibleInterval<T> source) {
        // NB: Assume <=3 samples in the 3rd dimension means channels. Of course,
        // we have no metadata with a vanilla RAI, but this is a best guess;
        // 3rd dimensions with >3 samples are probably something like Z or time.
        final int cAxis
                = //
                source.numDimensions() > 2 && source.dimension(2) <= 3 ? 2 : -1;

        return RAIToPNG(source, 0, 1, cAxis, ValueScaling.AUTO);
    }

    /**
     * Converts the given image to a form renderable by scientific notebooks.
     *
     * @param <T>
     * @param source The image to render.
     * @param xAxis The image dimension to use for the X axis.
     * @param yAxis The image dimension to use for the Y axis.
     * @param cAxis The image dimension to use for compositing multiple channels, or -1 for no
     * compositing.
     * @param scaling Value scaling strategy; see {@link ValueScaling}.
     * @param pos Dimensional position of the image. Passing null or the empty array will RAIToPNG
 the default (typically the first) position.
     * @return an object that the notebook knows how to draw onscreen.
     */
    <T extends RealType<T>> Object RAIToPNG(RandomAccessibleInterval<T> source,
            int xAxis, int yAxis, int cAxis, ValueScaling scaling, long... pos);

    /**
     * Organizes the given list of images into an N-dimensional mosaic.
     * <p>
     * For example, passing a grid layout of {2, 2} with four images {A, B, C, D} will result in
     * them being laid out along the first two axes (let's call them X and Y) in a 2 x 2 grid:
     * </p>
     *
     * <pre>
     * AB
     * CD
     * </pre>
     * <p>
     * The images do not need to be of equal size; images will be padded along each dimension as
     * needed so that everything lines up in a grid. In the example above, if A and C have different
     * widths, then the first column will be as wide as the wider of the two. Same for the second
     * column with images B and D. If A and B have different heights, than the first row will be as
     * tall as the taller of the two. And same for the second row with images C and D.
     * </p>
     * <p>
     * Normally, the number of grid cells (i.e., the product of the grid dimensions) should match
     * the given number of images. However, the algorithm handles a mismatch in either direction. If
     * the number of grid cells is less than the number of images, than the excess images are
     * discarded&mdash;i.e., they will not appear anywhere in the mosaic. On the other hand, if the
     * number of grid cells exceeds the given number of images, then some grid cells will be empty.
     * The cells are filled along the first dimension fastest, so e.g. a grid layout of {2, 3, 2}
     * will fill as follows: 000, 100, 010, 110, 020, 120, 001, 101, 011, 111, 021, 121.
     * </p>
     *
     * @param <T>
     * @param gridLayout Dimensions of the grid.
     * @param images Images to combine into the mosaic.
     * @return A single mosaic image, laid out as specified.
     */
    <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T> mosaic(
            final int[] gridLayout,
            @SuppressWarnings("unchecked") final RandomAccessibleInterval<T>... images);

}
