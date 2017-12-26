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

import java.util.ArrayList;

import net.imagej.display.ColorTables;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.ColorTable8;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;

import org.scijava.log.LogService;
import org.scijava.notebook.converter.NotebookConverters;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * AWT-driven implementation of {@link ImageJNotebookService}.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultImageJNotebookService extends AbstractService implements
        ImageJNotebookService {

    @Parameter
    private LogService log;

    @Parameter
    private OpService ops;

    @Override
    public <T extends RealType<T>> Object RAIToPNG(
            final RandomAccessibleInterval<T> source, //
            final int xAxis, final int yAxis, final int cAxis, //
            final ValueScaling scaling, final long... pos) {

        final IntervalView<T> image = ops.transform().zeroMinView(source);

        final int w = xAxis >= 0 ? (int) image.dimension(xAxis) : 1;
        final int h = yAxis >= 0 ? (int) image.dimension(yAxis) : 1;
        final int c = cAxis >= 0 ? (int) image.dimension(cAxis) : 1;
        final ARGBScreenImage target = new ARGBScreenImage(w, h);
        final ArrayList<Converter<T, ARGBType>> converters = new ArrayList<>(c);

        final double min, max;
        final boolean full = scaling == ValueScaling.FULL
                || //
                scaling == ValueScaling.AUTO && isNarrowType(source);

        if (full) {
            // scale the intensities based on the full range of the type
            min = image.firstElement().getMinValue();
            max = image.firstElement().getMaxValue();
        } else {
            // scale the intensities based on the sample values
            final IterableInterval<T> ii = ops.transform().flatIterableView(source);
            final Pair<T, T> minMax = ops.stats().minMax(ii);
            min = minMax.getA().getRealDouble();
            max = minMax.getB().getRealDouble();
        }

        for (int i = 0; i < c; i++) {
            final ColorTable8 lut = c == 1
                    ? //
                    ColorTables.GRAYS : ColorTables.getDefaultColorTable(i);
            converters.add(new RealLUTConverter<>(min, max, lut));
        }
        final CompositeXYProjector<T> proj = new CompositeXYProjector<>(image,
                target, converters, cAxis);
        if (pos != null && pos.length > 0) {
            proj.setPosition(pos);
        }
        proj.setComposite(true);
        proj.map();

        // Convert to PNG
        return NotebookConverters.toPNG(target.image());

    }

    @Override
    public <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T>
            mosaic(final int[] gridLayout,
                    @SuppressWarnings("unchecked") final RandomAccessibleInterval<T>... images) {
        // Count the actual number of image dimensions.
        int numDims = 0;
        for (RandomAccessibleInterval<T> image : images) {
            numDims = Math.max(numDims, image.numDimensions());
        }

        // Pad any missing grid dimensions.
        final int[] grid = new int[numDims];
        for (int d = 0; d < numDims; d++) {
            grid[d] = d < gridLayout.length ? gridLayout[d] : 1;
        }

        // Define a buffer for holding multidimensional position indices.
        final int[] pos = new int[numDims];

        // Compute grid box extents (width, height, etc.).
        final long[][] extents = new long[numDims][];
        for (int d = 0; d < numDims; d++) {
            extents[d] = new long[grid[d]];
        }
        for (int i = 0; i < images.length; i++) {
            IntervalIndexer.indexToPosition(i, grid, pos);
            for (int d = 0; d < numDims; d++) {
                if (pos[d] < grid[d]) {
                    extents[d][pos[d]]
                            = //
                            Math.max(extents[d][pos[d]], images[i].dimension(d));
                }
            }
        }

        // Compute grid box offsets.
        final long[][] offsets = new long[numDims][];
        for (int d = 0; d < numDims; d++) {
            offsets[d] = new long[grid[d] + 1];
        }
        for (int d = 0; d < numDims; d++) {
            for (int g = 0; g < grid[d]; g++) {
                offsets[d][g + 1] = offsets[d][g] + extents[d][g];
            }
        }

        // Compute total mosaic dimensions.
        final long[] mosaicDims = new long[numDims];
        for (int d = 0; d < numDims; d++) {
            mosaicDims[d] = offsets[d][offsets[d].length - 1];
        }
        final FinalInterval mosaicBox = new FinalInterval(mosaicDims);

        final Img<T> result
                = //
                ops.create().img(mosaicBox, Util.getTypeFromInterval(images[0]));

        for (int i = 0; i < images.length; i++) {
            IntervalIndexer.indexToPosition(i, grid, pos);

            // Skip images which will not appear on the grid.
            boolean outOfBounds = false;
            for (int d = 0; d < numDims; d++) {
                if (pos[d] >= grid[d]) {
                    outOfBounds = true;
                    break;
                }
            }
            if (outOfBounds) {
                continue;
            }

            // Translate the origin of each image to match its position in the mosaic.
            final long[] offset = new long[numDims];
            for (int d = 0; d < numDims; d++) {
                offset[d] = offsets[d][pos[d]];
            }
            final IntervalView<T> translated = //
                    ops.transform().translateView(ops.transform().zeroMinView(images[i]), offset);

            // Declare that all values outside the interval proper will be 0.
            // If we do not perform this step, we will get an error when querying
            // out-of-bounds coordinates.
            final RandomAccessible<T> extended = ops.transform().extendZeroView(translated);

            // Define the interval of the image to match the size of the mosaic.
            final RandomAccessibleInterval<T> expanded
                    = //
                    ops.transform().intervalView(extended, mosaicBox);

            // Add the full-size zero-padded translated image into the mosaic.
            Inplaces.binary1(ops, Ops.Math.Add.class, result, expanded).mutate1(
                    result, expanded);
        }

        // TODO: Some day, use Views.arrange, Views.tile or Views.combine instead.
        return result;
    }

    // -- Helper methods --
    private <T extends RealType<T>> boolean isNarrowType(
            final RandomAccessibleInterval<T> source) {
        return Util.getTypeFromInterval(source).getBitsPerPixel() <= 8;
    }
}
