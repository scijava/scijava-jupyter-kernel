/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2017 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imagej.notebook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import javax.imageio.ImageIO;

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
import net.imglib2.view.MixedTransformView;
import org.scijava.log.LogService;

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
    public <T extends RealType<T>> Object display(
            final RandomAccessibleInterval<T> source, //
            final int xAxis, final int yAxis, final int cAxis, //
            final ValueScaling scaling, final long... pos) {
        
        final IntervalView<T> image = ops.transform().zeroMin(source);

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
            final IterableInterval<T> ii = ops.transform().flatIterable(source);
            final Pair<T, T> minMax = ops.stats().minMax(ii);
            min = minMax.getA().getRealDouble();
            max = minMax.getB().getRealDouble();
        }

        for (int i = 0; i < c; i++) {
            final ColorTable8 lut = c == 1
                    ? //
                    ColorTables.GRAYS : ColorTables.getDefaultColorTable(i);
            converters.add(new RealLUTConverter<T>(min, max, lut));
        }
        final CompositeXYProjector<T> proj = new CompositeXYProjector<>(image,
                target, converters, cAxis);
        if (pos != null && pos.length > 0) {
            proj.setPosition(pos);
        }
        proj.setComposite(true);
        proj.map();

        // Convert to PNG
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(target.image(), "PNG", os);
        } catch (IOException ex) {
            log.error(ex);
        }
        return Base64.getEncoder().encodeToString(os.toByteArray());

    }

    @Override
    public <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T>
            mosaic(final int[] gridLayout,
                    @SuppressWarnings("unchecked") final RandomAccessibleInterval<T>... images) {
        // Count the actual number of image dimensions.
        int numDims = 0;
        for (int i = 0; i < images.length; i++) {
            numDims = Math.max(numDims, images[i].numDimensions());
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
            final MixedTransformView<T> translated
                    = //
                    ops.transform().translate(ops.transform().zeroMin(images[i]), offset);

            // Unfortunately, this operation loses the "Interval" from the RAI:
            // translated objects are RAs, not RAIs.
            // So, we readd the bounds to match the newly translated coordinates.
            // NB: The max bound is _inclusive_, so we must subtract 1.
            final long[] max = new long[numDims];
            for (int d = 0; d < numDims; d++) {
                max[d] = offset[d] + images[i].dimension(d) - 1;
            }
            final FinalInterval bounds = new FinalInterval(offset, max);
            final RandomAccessibleInterval<T> bounded
                    = //
                    ops.transform().interval(translated, bounds);

            // Declare that all values outside the interval proper will be 0.
            // If we do not perform this step, we will get an error when querying
            // out-of-bounds coordinates.
            final RandomAccessible<T> extended = ops.transform().extendZero(bounded);

            // Define the interval of the image to match the size of the mosaic.
            final RandomAccessibleInterval<T> expanded
                    = //
                    ops.transform().interval(extended, mosaicBox);

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
