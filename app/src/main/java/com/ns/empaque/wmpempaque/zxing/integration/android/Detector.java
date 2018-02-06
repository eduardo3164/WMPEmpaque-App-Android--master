package com.ns.empaque.wmpempaque.zxing.integration.android;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Encapsulates logic that can detect a QR Code in an image, even if the QR Code
 * is rotated or skewed, or partially obscured.</p>
 *
 * @author Sean Owen
 */
public class Detector {

    private final BitMatrix image;
    private boolean compact;
    private int nbLayers;
    private int nbDataBlocks;
    private int nbCenterLayers;
    private int shift;
    private ResultPointCallback resultPointCallback;


    private static final int[] INDEXES_START_PATTERN = {0, 4, 1, 5};
    private static final int[] INDEXES_STOP_PATTERN = {6, 2, 7, 3};
    private static final float MAX_AVG_VARIANCE = 0.42f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.8f;

    // B S B S B S B S Bar/Space pattern
    // 11111111 0 1 0 1 0 1 000
    private static final int[] START_PATTERN = {8, 1, 1, 1, 1, 1, 1, 3};
    // 1111111 0 1 000 1 0 1 00 1
    private static final int[] STOP_PATTERN = {7, 1, 1, 3, 1, 1, 1, 2, 1};
    private static final int MAX_PIXEL_DRIFT = 3;
    private static final int MAX_PATTERN_DRIFT = 5;
    // if we set the value too low, then we don't detect the correct height of the bar if the start patterns are damaged.
    // if we set the value too high, then we might detect the start pattern from a neighbor barcode.
    private static final int SKIPPED_ROW_COUNT_MAX = 25;
    // A PDF471 barcode should have at least 3 rows, with each row being >= 3 times the module width. Therefore it should be at least
    // 9 pixels tall. To be conservative, we use about half the size to ensure we don't miss it.
    private static final int BARCODE_MIN_HEIGHT = 10;

    private static final int ROW_STEP = 5;

    public Detector(BitMatrix image) {
        this.image = image;
        this.compact = false;
        this.nbLayers = 0;
        this.nbDataBlocks=0;
        this.nbCenterLayers=0;
        this.shift=0;
    }

    /**
     * <p>Detects a PDF417 Code in an image. Only checks 0 and 180 degree rotations.</p>
     *
     * @param image barcode image to decode
     * @param hints optional hints to detector
     * @param multiple if true, then the image is searched for multiple codes. If false, then at most one code will
     * be found and returned
     * @return {@link PDF417DetectorResult} encapsulating results of detecting a PDF417 code
     * @throws NotFoundException if no PDF417 Code can be found
     */
    public static PDF417DetectorResult detect(BinaryBitmap image, Map<DecodeHintType,?> hints, boolean multiple)
            throws NotFoundException {
        // TODO detection improvement, tryHarder could try several different luminance thresholds/blackpoints or even
        // different binarizers
        //boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);

        BitMatrix bitMatrix = image.getBlackMatrix();

        List<ResultPoint[]> barcodeCoordinates = detect(multiple, bitMatrix);
        if (barcodeCoordinates.isEmpty()) {
            bitMatrix = bitMatrix.clone();
            bitMatrix.rotate180();
            barcodeCoordinates = detect(multiple, bitMatrix);
        }
        return new PDF417DetectorResult(bitMatrix, barcodeCoordinates);
    }

    private static void copyToResult(ResultPoint[] result, ResultPoint[] tmpResult, int[] destinationIndexes) {
        for (int i = 0; i < destinationIndexes.length; i++) {
            result[destinationIndexes[i]] = tmpResult[i];
        }
    }

    private static ResultPoint[] findRowsWithPattern(BitMatrix matrix,
                                                     int height,
                                                     int width,
                                                     int startRow,
                                                     int startColumn,
                                                     int[] pattern) {
        ResultPoint[] result = new ResultPoint[4];
        boolean found = false;
        int[] counters = new int[pattern.length];
        for (; startRow < height; startRow += ROW_STEP) {
            int[] loc = findGuardPattern(matrix, startColumn, startRow, width, false, pattern, counters);
            if (loc != null) {
                while (startRow > 0) {
                    int[] previousRowLoc = findGuardPattern(matrix, startColumn, --startRow, width, false, pattern, counters);
                    if (previousRowLoc != null) {
                        loc = previousRowLoc;
                    } else {
                        startRow++;
                        break;
                    }
                }
                result[0] = new ResultPoint(loc[0], startRow);
                result[1] = new ResultPoint(loc[1], startRow);
                found = true;
                break;
            }
        }
        int stopRow = startRow + 1;
        // Last row of the current symbol that contains pattern
        if (found) {
            int skippedRowCount = 0;
            int[] previousRowLoc = {(int) result[0].getX(), (int) result[1].getX()};
            for (; stopRow < height; stopRow++) {
                int[] loc = findGuardPattern(matrix, previousRowLoc[0], stopRow, width, false, pattern, counters);
                // a found pattern is only considered to belong to the same barcode if the start and end positions
                // don't differ too much. Pattern drift should be not bigger than two for consecutive rows. With
                // a higher number of skipped rows drift could be larger. To keep it simple for now, we allow a slightly
                // larger drift and don't check for skipped rows.
                if (loc != null &&
                        Math.abs(previousRowLoc[0] - loc[0]) < MAX_PATTERN_DRIFT &&
                        Math.abs(previousRowLoc[1] - loc[1]) < MAX_PATTERN_DRIFT) {
                    previousRowLoc = loc;
                    skippedRowCount = 0;
                } else {
                    if (skippedRowCount > SKIPPED_ROW_COUNT_MAX) {
                        break;
                    } else {
                        skippedRowCount++;
                    }
                }
            }
            stopRow -= skippedRowCount + 1;
            result[2] = new ResultPoint(previousRowLoc[0], stopRow);
            result[3] = new ResultPoint(previousRowLoc[1], stopRow);
        }
        if (stopRow - startRow < BARCODE_MIN_HEIGHT) {
            for (int i = 0; i < result.length; i++) {
                result[i] = null;
            }
        }
        return result;
    }


    /**
     * @param matrix row of black/white values to search
     * @param column x position to start search
     * @param row y position to start search
     * @param width the number of pixels to search on this row
     * @param pattern pattern of counts of number of black and white pixels that are
     *                 being searched for as a pattern
     * @param counters array of counters, as long as pattern, to re-use
     * @return start/end horizontal offset of guard pattern, as an array of two ints.
     */
    private static int[] findGuardPattern(BitMatrix matrix,
                                          int column,
                                          int row,
                                          int width,
                                          boolean whiteFirst,
                                          int[] pattern,
                                          int[] counters) {
        Arrays.fill(counters, 0, counters.length, 0);
        int patternLength = pattern.length;
        boolean isWhite = whiteFirst;
        int patternStart = column;
        int pixelDrift = 0;

        // if there are black pixels left of the current pixel shift to the left, but only for MAX_PIXEL_DRIFT pixels
        while (matrix.get(patternStart, row) && patternStart > 0 && pixelDrift++ < MAX_PIXEL_DRIFT) {
            patternStart--;
        }
        int x = patternStart;
        int counterPosition = 0;
        for (; x < width; x++) {
            boolean pixel = matrix.get(x, row);
            if (pixel ^ isWhite) {
                counters[counterPosition]++;
            } else {
                if (counterPosition == patternLength - 1) {
                    if (patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE) < MAX_AVG_VARIANCE) {
                        return new int[] {patternStart, x};
                    }
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        if (counterPosition == patternLength - 1) {
            if (patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE) < MAX_AVG_VARIANCE) {
                return new int[] {patternStart, x - 1};
            }
        }
        return null;
    }

    /**
     * Determines how closely a set of observed counts of runs of black/white
     * values matches a given target pattern. This is reported as the ratio of
     * the total variance from the expected pattern proportions across all
     * pattern elements, to the length of the pattern.
     *
     * @param counters observed counters
     * @param pattern expected pattern
     * @param maxIndividualVariance The most any counter can differ before we give up
     * @return ratio of total variance between counters and pattern compared to total pattern size
     */
    private static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
        int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;
        for (int i = 0; i < numCounters; i++) {
            total += counters[i];
            patternLength += pattern[i];
        }
        if (total < patternLength) {
            // If we don't even have one pixel per unit of bar width, assume this
            // is too small to reliably match, so fail:
            return Float.POSITIVE_INFINITY;
        }
        // We're going to fake floating-point math in integers. We just need to use more bits.
        // Scale up patternLength so that intermediate values below like scaledCounter will have
        // more "significant digits".
        float unitBarWidth = (float) total / patternLength;
        maxIndividualVariance *= unitBarWidth;

        float totalVariance = 0.0f;
        for (int x = 0; x < numCounters; x++) {
            int counter = counters[x];
            float scaledPattern = pattern[x] * unitBarWidth;
            float variance = counter > scaledPattern ? counter - scaledPattern : scaledPattern - counter;
            if (variance > maxIndividualVariance) {
                return Float.POSITIVE_INFINITY;
            }
            totalVariance += variance;
        }
        return totalVariance / total;
    }

    /**
     * Locate the vertices and the codewords area of a black blob using the Start
     * and Stop patterns as locators.
     *
     * @param matrix the scanned barcode image.
     * @return an array containing the vertices:
     *           vertices[0] x, y top left barcode
     *           vertices[1] x, y bottom left barcode
     *           vertices[2] x, y top right barcode
     *           vertices[3] x, y bottom right barcode
     *           vertices[4] x, y top left codeword area
     *           vertices[5] x, y bottom left codeword area
     *           vertices[6] x, y top right codeword area
     *           vertices[7] x, y bottom right codeword area
     */
    private static ResultPoint[] findVertices(BitMatrix matrix, int startRow, int startColumn) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();

        ResultPoint[] result = new ResultPoint[8];
        copyToResult(result, findRowsWithPattern(matrix, height, width, startRow, startColumn, START_PATTERN),
                INDEXES_START_PATTERN);

        if (result[4] != null) {
            startColumn = (int) result[4].getX();
            startRow = (int) result[4].getY();
        }
        copyToResult(result, findRowsWithPattern(matrix, height, width, startRow, startColumn, STOP_PATTERN),
                INDEXES_STOP_PATTERN);
        return result;
    }

    /**
     * Detects PDF417 codes in an image. Only checks 0 degree rotation
     * @param multiple if true, then the image is searched for multiple codes. If false, then at most one code will
     * be found and returned
     * @param bitMatrix bit matrix to detect barcodes in
     * @return List of ResultPoint arrays containing the coordinates of found barcodes
     */
    private static List<ResultPoint[]> detect(boolean multiple, BitMatrix bitMatrix) {
        List<ResultPoint[]> barcodeCoordinates = new ArrayList<>();
        int row = 0;
        int column = 0;
        boolean foundBarcodeInRow = false;
        while (row < bitMatrix.getHeight()) {
            ResultPoint[] vertices = findVertices(bitMatrix, row, column);

            if (vertices[0] == null && vertices[3] == null) {
                if (!foundBarcodeInRow) {
                    // we didn't find any barcode so that's the end of searching
                    break;
                }
                // we didn't find a barcode starting at the given column and row. Try again from the first column and slightly
                // below the lowest barcode we found so far.
                foundBarcodeInRow = false;
                column = 0;
                for (ResultPoint[] barcodeCoordinate : barcodeCoordinates) {
                    if (barcodeCoordinate[1] != null) {
                        row = (int) Math.max(row, barcodeCoordinate[1].getY());
                    }
                    if (barcodeCoordinate[3] != null) {
                        row = Math.max(row, (int) barcodeCoordinate[3].getY());
                    }
                }
                row += ROW_STEP;
                continue;
            }
            foundBarcodeInRow = true;
            barcodeCoordinates.add(vertices);
            if (!multiple) {
                break;
            }
            // if we didn't find a right row indicator column, then continue the search for the next barcode after the
            // start pattern of the barcode just found.
            if (vertices[2] != null) {
                column = (int) vertices[2].getX();
                row = (int) vertices[2].getY();
            } else {
                column = (int) vertices[4].getX();
                row = (int) vertices[4].getY();
            }
        }
        return barcodeCoordinates;
    }

    protected final BitMatrix getImage() {
        return image;
    }

    protected final ResultPointCallback getResultPointCallback() {
        return resultPointCallback;
    }

    /**
     * <p>Detects a QR Code in an image.</p>
     *
     * @return {@link DetectorResult} encapsulating results of detecting a QR Code
     * @throws NotFoundException if QR Code cannot be found
     * @throws FormatException if a QR Code cannot be decoded
     */
    public DetectorResult detect() throws NotFoundException, FormatException {
        return detect(null);
    }

    /**
     * <p>Detects a QR Code in an image.</p>
     *
     * @param hints optional hints to detector
     * @return {@link DetectorResult} encapsulating results of detecting a QR Code
     * @throws NotFoundException if QR Code cannot be found
     * @throws FormatException if a QR Code cannot be decoded
     */
    public final DetectorResult detect(Map<DecodeHintType,?> hints) throws NotFoundException, FormatException {

        resultPointCallback = hints == null ? null :
                (ResultPointCallback) hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);

        FinderPatternFinder finder = new FinderPatternFinder(image, resultPointCallback);
        FinderPatternInfo info = finder.find(hints);

        return processFinderPatternInfo(info);
    }

    protected final DetectorResult processFinderPatternInfo(FinderPatternInfo info)
            throws NotFoundException, FormatException {

        FinderPattern topLeft = info.getTopLeft();
        FinderPattern topRight = info.getTopRight();
        FinderPattern bottomLeft = info.getBottomLeft();

        float moduleSize = calculateModuleSize(topLeft, topRight, bottomLeft);
        if (moduleSize < 1.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        int dimension = computeDimension(topLeft, topRight, bottomLeft, moduleSize);
        Version provisionalVersion = Version.getProvisionalVersionForDimension(dimension);
        int modulesBetweenFPCenters = provisionalVersion.getDimensionForVersion() - 7;

        AlignmentPattern alignmentPattern = null;
        // Anything above version 1 has an alignment pattern
        if (provisionalVersion.getAlignmentPatternCenters().length > 0) {

            // Guess where a "bottom right" finder pattern would have been
            float bottomRightX = topRight.getX() - topLeft.getX() + bottomLeft.getX();
            float bottomRightY = topRight.getY() - topLeft.getY() + bottomLeft.getY();

            // Estimate that alignment pattern is closer by 3 modules
            // from "bottom right" to known top left location
            float correctionToTopLeft = 1.0f - 3.0f / (float) modulesBetweenFPCenters;
            int estAlignmentX = (int) (topLeft.getX() + correctionToTopLeft * (bottomRightX - topLeft.getX()));
            int estAlignmentY = (int) (topLeft.getY() + correctionToTopLeft * (bottomRightY - topLeft.getY()));

            // Kind of arbitrary -- expand search radius before giving up
            for (int i = 4; i <= 16; i <<= 1) {
                try {
                    alignmentPattern = findAlignmentInRegion(moduleSize,
                            estAlignmentX,
                            estAlignmentY,
                            (float) i);
                    break;
                } catch (NotFoundException re) {
                    // try next round
                }
            }
            // If we didn't find alignment pattern... well try anyway without it
        }

        PerspectiveTransform transform =
                createTransform(topLeft, topRight, bottomLeft, alignmentPattern, dimension);

        BitMatrix bits = sampleGrid(image, transform, dimension);

        ResultPoint[] points;
        if (alignmentPattern == null) {
            points = new ResultPoint[]{bottomLeft, topLeft, topRight};
        } else {
            points = new ResultPoint[]{bottomLeft, topLeft, topRight, alignmentPattern};
        }
        return new DetectorResult(bits, points);
    }

    private static PerspectiveTransform createTransform(ResultPoint topLeft,
                                                        ResultPoint topRight,
                                                        ResultPoint bottomLeft,
                                                        ResultPoint alignmentPattern,
                                                        int dimension) {
        float dimMinusThree = (float) dimension - 3.5f;
        float bottomRightX;
        float bottomRightY;
        float sourceBottomRightX;
        float sourceBottomRightY;
        if (alignmentPattern != null) {
            bottomRightX = alignmentPattern.getX();
            bottomRightY = alignmentPattern.getY();
            sourceBottomRightX = dimMinusThree - 3.0f;
            sourceBottomRightY = sourceBottomRightX;
        } else {
            // Don't have an alignment pattern, just make up the bottom-right point
            bottomRightX = (topRight.getX() - topLeft.getX()) + bottomLeft.getX();
            bottomRightY = (topRight.getY() - topLeft.getY()) + bottomLeft.getY();
            sourceBottomRightX = dimMinusThree;
            sourceBottomRightY = dimMinusThree;
        }

        return PerspectiveTransform.quadrilateralToQuadrilateral(
                3.5f,
                3.5f,
                dimMinusThree,
                3.5f,
                sourceBottomRightX,
                sourceBottomRightY,
                3.5f,
                dimMinusThree,
                topLeft.getX(),
                topLeft.getY(),
                topRight.getX(),
                topRight.getY(),
                bottomRightX,
                bottomRightY,
                bottomLeft.getX(),
                bottomLeft.getY());
    }

    private static BitMatrix sampleGrid(BitMatrix image,
                                        PerspectiveTransform transform,
                                        int dimension) throws NotFoundException {

        GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, dimension, transform);
    }

    /**
     * <p>Computes the dimension (number of modules on a size) of the QR Code based on the position
     * of the finder patterns and estimated module size.</p>
     */
    private static int computeDimension(ResultPoint topLeft,
                                        ResultPoint topRight,
                                        ResultPoint bottomLeft,
                                        float moduleSize) throws NotFoundException {
        int tltrCentersDimension = MathUtils.round(ResultPoint.distance(topLeft, topRight) / moduleSize);
        int tlblCentersDimension = MathUtils.round(ResultPoint.distance(topLeft, bottomLeft) / moduleSize);
        int dimension = ((tltrCentersDimension + tlblCentersDimension) / 2) + 7;
        switch (dimension & 0x03) { // mod 4
            case 0:
                dimension++;
                break;
            // 1? do nothing
            case 2:
                dimension--;
                break;
            case 3:
                throw NotFoundException.getNotFoundInstance();
        }
        return dimension;
    }

    /**
     * <p>Computes an average estimated module size based on estimated derived from the positions
     * of the three finder patterns.</p>
     *
     * @param topLeft detected top-left finder pattern center
     * @param topRight detected top-right finder pattern center
     * @param bottomLeft detected bottom-left finder pattern center
     * @return estimated module size
     */
    protected final float calculateModuleSize(ResultPoint topLeft,
                                              ResultPoint topRight,
                                              ResultPoint bottomLeft) {
        // Take the average
        return (calculateModuleSizeOneWay(topLeft, topRight) +
                calculateModuleSizeOneWay(topLeft, bottomLeft)) / 2.0f;
    }

    /**
     * <p>Estimates module size based on two finder patterns -- it uses
     * {@link #sizeOfBlackWhiteBlackRunBothWays(int, int, int, int)} to figure the
     * width of each, measuring along the axis between their centers.</p>
     */
    private float calculateModuleSizeOneWay(ResultPoint pattern, ResultPoint otherPattern) {
        float moduleSizeEst1 = sizeOfBlackWhiteBlackRunBothWays((int) pattern.getX(),
                (int) pattern.getY(),
                (int) otherPattern.getX(),
                (int) otherPattern.getY());
        float moduleSizeEst2 = sizeOfBlackWhiteBlackRunBothWays((int) otherPattern.getX(),
                (int) otherPattern.getY(),
                (int) pattern.getX(),
                (int) pattern.getY());
        if (Float.isNaN(moduleSizeEst1)) {
            return moduleSizeEst2 / 7.0f;
        }
        if (Float.isNaN(moduleSizeEst2)) {
            return moduleSizeEst1 / 7.0f;
        }
        // Average them, and divide by 7 since we've counted the width of 3 black modules,
        // and 1 white and 1 black module on either side. Ergo, divide sum by 14.
        return (moduleSizeEst1 + moduleSizeEst2) / 14.0f;
    }

    /**
     * See {@link #sizeOfBlackWhiteBlackRun(int, int, int, int)}; computes the total width of
     * a finder pattern by looking for a black-white-black run from the center in the direction
     * of another point (another finder pattern center), and in the opposite direction too.
     */
    private float sizeOfBlackWhiteBlackRunBothWays(int fromX, int fromY, int toX, int toY) {

        float result = sizeOfBlackWhiteBlackRun(fromX, fromY, toX, toY);

        // Now count other way -- don't run off image though of course
        float scale = 1.0f;
        int otherToX = fromX - (toX - fromX);
        if (otherToX < 0) {
            scale = (float) fromX / (float) (fromX - otherToX);
            otherToX = 0;
        } else if (otherToX >= image.getWidth()) {
            scale = (float) (image.getWidth() - 1 - fromX) / (float) (otherToX - fromX);
            otherToX = image.getWidth() - 1;
        }
        int otherToY = (int) (fromY - (toY - fromY) * scale);

        scale = 1.0f;
        if (otherToY < 0) {
            scale = (float) fromY / (float) (fromY - otherToY);
            otherToY = 0;
        } else if (otherToY >= image.getHeight()) {
            scale = (float) (image.getHeight() - 1 - fromY) / (float) (otherToY - fromY);
            otherToY = image.getHeight() - 1;
        }
        otherToX = (int) (fromX + (otherToX - fromX) * scale);

        result += sizeOfBlackWhiteBlackRun(fromX, fromY, otherToX, otherToY);

        // Middle pixel is double-counted this way; subtract 1
        return result - 1.0f;
    }

    /**
     * <p>This method traces a line from a point in the image, in the direction towards another point.
     * It begins in a black region, and keeps going until it finds white, then black, then white again.
     * It reports the distance from the start to this point.</p>
     *
     * <p>This is used when figuring out how wide a finder pattern is, when the finder pattern
     * may be skewed or rotated.</p>
     */
    private float sizeOfBlackWhiteBlackRun(int fromX, int fromY, int toX, int toY) {
        // Mild variant of Bresenham's algorithm;
        // see http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        if (steep) {
            int temp = fromX;
            fromX = fromY;
            fromY = temp;
            temp = toX;
            toX = toY;
            toY = temp;
        }

        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = -dx / 2;
        int xstep = fromX < toX ? 1 : -1;
        int ystep = fromY < toY ? 1 : -1;

        // In black pixels, looking for white, first or second time.
        int state = 0;
        // Loop up until x == toX, but not beyond
        int xLimit = toX + xstep;
        for (int x = fromX, y = fromY; x != xLimit; x += xstep) {
            int realX = steep ? y : x;
            int realY = steep ? x : y;

            // Does current pixel mean we have moved white to black or vice versa?
            // Scanning black in state 0,2 and white in state 1, so if we find the wrong
            // color, advance to next state or end if we are in state 2 already
            if ((state == 1) == image.get(realX, realY)) {
                if (state == 2) {
                    return MathUtils.distance(x, y, fromX, fromY);
                }
                state++;
            }

            error += dy;
            if (error > 0) {
                if (y == toY) {
                    break;
                }
                y += ystep;
                error -= dx;
            }
        }
        // Found black-white-black; give the benefit of the doubt that the next pixel outside the image
        // is "white" so this last point at (toX+xStep,toY) is the right ending. This is really a
        // small approximation; (toX+xStep,toY+yStep) might be really correct. Ignore this.
        if (state == 2) {
            return MathUtils.distance(toX + xstep, toY, fromX, fromY);
        }
        // else we didn't find even black-white-black; no estimate is really possible
        return Float.NaN;
    }

    /**
     * <p>Attempts to locate an alignment pattern in a limited region of the image, which is
     * guessed to contain it. This method uses {@link AlignmentPattern}.</p>
     *
     * @param overallEstModuleSize estimated module size so far
     * @param estAlignmentX x coordinate of center of area probably containing alignment pattern
     * @param estAlignmentY y coordinate of above
     * @param allowanceFactor number of pixels in all directions to search from the center
     * @return {@link AlignmentPattern} if found, or null otherwise
     * @throws NotFoundException if an unexpected error occurs during detection
     */
    protected final AlignmentPattern findAlignmentInRegion(float overallEstModuleSize,
                                                           int estAlignmentX,
                                                           int estAlignmentY,
                                                           float allowanceFactor)
            throws NotFoundException {
        // Look for an alignment pattern (3 modules in size) around where it
        // should be
        int allowance = (int) (allowanceFactor * overallEstModuleSize);
        int alignmentAreaLeftX = Math.max(0, estAlignmentX - allowance);
        int alignmentAreaRightX = Math.min(image.getWidth() - 1, estAlignmentX + allowance);
        if (alignmentAreaRightX - alignmentAreaLeftX < overallEstModuleSize * 3) {
            throw NotFoundException.getNotFoundInstance();
        }

        int alignmentAreaTopY = Math.max(0, estAlignmentY - allowance);
        int alignmentAreaBottomY = Math.min(image.getHeight() - 1, estAlignmentY + allowance);
        if (alignmentAreaBottomY - alignmentAreaTopY < overallEstModuleSize * 3) {
            throw NotFoundException.getNotFoundInstance();
        }

        AlignmentPatternFinder alignmentFinder =
                new AlignmentPatternFinder(
                        image,
                        alignmentAreaLeftX,
                        alignmentAreaTopY,
                        alignmentAreaRightX - alignmentAreaLeftX,
                        alignmentAreaBottomY - alignmentAreaTopY,
                        overallEstModuleSize,
                        resultPointCallback);
        return alignmentFinder.find();
    }




    /**
     * Gets the coordinate of the first point with a different color in the given direction
     */
    private Point getFirstDifferent(Point init, boolean color, int dx, int dy) {
        int x = init.getX() + dx;
        int y = init.getY() + dy;

        while (isValid(x, y) && image.get(x, y) == color) {
            x += dx;
            y += dy;
        }

        x -= dx;
        y -= dy;

        while (isValid(x, y) && image.get(x, y) == color) {
            x += dx;
        }
        x -= dx;

        while (isValid(x, y) && image.get(x, y) == color) {
            y += dy;
        }
        y -= dy;

        return new Point(x, y);
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < image.getWidth() && y > 0 && y < image.getHeight();
    }

    private boolean isValid(ResultPoint point) {
        int x = MathUtils.round(point.getX());
        int y = MathUtils.round(point.getY());
        return isValid(x, y);
    }





    /**
     * Finds a candidate center point of an Aztec code from an image
     *
     * @return the center point
     */
    private Point getMatrixCenter() {

        ResultPoint pointA;
        ResultPoint pointB;
        ResultPoint pointC;
        ResultPoint pointD;

        //Get a white rectangle that can be the border of the matrix in center bull's eye or
        try {

            ResultPoint[] cornerPoints = new WhiteRectangleDetector(image).detect();
            pointA = cornerPoints[0];
            pointB = cornerPoints[1];
            pointC = cornerPoints[2];
            pointD = cornerPoints[3];

        } catch (NotFoundException e) {

            // This exception can be in case the initial rectangle is white
            // In that case, surely in the bull's eye, we try to expand the rectangle.
            int cx = image.getWidth() / 2;
            int cy = image.getHeight() / 2;
            pointA = getFirstDifferent(new Point(cx + 7, cy - 7), false, 1, -1).toResultPoint();
            pointB = getFirstDifferent(new Point(cx + 7, cy + 7), false, 1, 1).toResultPoint();
            pointC = getFirstDifferent(new Point(cx - 7, cy + 7), false, -1, 1).toResultPoint();
            pointD = getFirstDifferent(new Point(cx - 7, cy - 7), false, -1, -1).toResultPoint();

        }



        //Compute the center of the rectangle
        int cx = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0f);
        int cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0f);

        // Redetermine the white rectangle starting from previously computed center.
        // This will ensure that we end up with a white rectangle in center bull's eye
        // in order to compute a more accurate center.
        try {
            ResultPoint[] cornerPoints = new WhiteRectangleDetector(image, 15, cx, cy).detect();
            pointA = cornerPoints[0];
            pointB = cornerPoints[1];
            pointC = cornerPoints[2];
            pointD = cornerPoints[3];
        } catch (NotFoundException e) {
            // This exception can be in case the initial rectangle is white
            // In that case we try to expand the rectangle.
            pointA = getFirstDifferent(new Point(cx + 7, cy - 7), false, 1, -1).toResultPoint();
            pointB = getFirstDifferent(new Point(cx + 7, cy + 7), false, 1, 1).toResultPoint();
            pointC = getFirstDifferent(new Point(cx - 7, cy + 7), false, -1, 1).toResultPoint();
            pointD = getFirstDifferent(new Point(cx - 7, cy - 7), false, -1, -1).toResultPoint();
        }

        // Recompute the center of the rectangle
        cx = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0f);
        cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0f);

        return new Point(cx, cy);
    }


    /**
     * Creates a BitMatrix by sampling the provided image.
     * topLeft, topRight, bottomRight, and bottomLeft are the centers of the squares on the
     * diagonal just outside the bull's eye.
     */
    private BitMatrix sampleGrid(BitMatrix image,
                                 ResultPoint topLeft,
                                 ResultPoint topRight,
                                 ResultPoint bottomRight,
                                 ResultPoint bottomLeft) throws NotFoundException {

        GridSampler sampler = GridSampler.getInstance();
        int dimension = getDimension();

        float low = dimension / 2.0f - nbCenterLayers;
        float high = dimension / 2.0f + nbCenterLayers;

        return sampler.sampleGrid(image,
                dimension,
                dimension,
                low, low,   // topleft
                high, low,  // topright
                high, high, // bottomright
                low, high,  // bottomleft
                topLeft.getX(), topLeft.getY(),
                topRight.getX(), topRight.getY(),
                bottomRight.getX(), bottomRight.getY(),
                bottomLeft.getX(), bottomLeft.getY());
    }

    private static float distance(Point a, Point b) {
        return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private static float distance(ResultPoint a, ResultPoint b) {
        return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
    }



    /**
     * Samples a line.
     *
     * @param p1   start point (inclusive)
     * @param p2   end point (exclusive)
     * @param size number of bits
     * @return the array of bits as an int (first bit is high-order bit of result)
     */
    private int sampleLine(ResultPoint p1, ResultPoint p2, int size) {
        int result = 0;

        float d = distance(p1, p2);
        float moduleSize = d / size;
        float px = p1.getX();
        float py = p1.getY();
        float dx = moduleSize * (p2.getX() - p1.getX()) / d;
        float dy = moduleSize * (p2.getY() - p1.getY()) / d;
        for (int i = 0; i < size; i++) {
            if (image.get(MathUtils.round(px + i * dx), MathUtils.round(py + i * dy))) {
                result |= 1 << (size - i - 1);
            }
        }
        return result;
    }

    private static final int[] EXPECTED_CORNER_BITS = {
            0xee0,  // 07340  XXX .XX X.. ...
            0x1dc,  // 00734  ... XXX .XX X..
            0x83b,  // 04073  X.. ... XXX .XX
            0x707,  // 03407 .XX X.. ... XXX
    };

    private static int getRotation(int[] sides, int length) throws NotFoundException {
        // In a normal pattern, we expect to See
        //   **    .*             D       A
        //   *      *
        //
        //   .      *
        //   ..    ..             C       B
        //
        // Grab the 3 bits from each of the sides the form the locator pattern and concatenate
        // into a 12-bit integer.  Start with the bit at A
        int cornerBits = 0;
        for (int side : sides) {
            // XX......X where X's are orientation marks
            int t = ((side >> (length - 2)) << 1) + (side & 1);
            cornerBits = (cornerBits << 3) + t;
        }
        // Mov the bottom bit to the top, so that the three bits of the locator pattern at A are
        // together.  cornerBits is now:
        //  3 orientation bits at A || 3 orientation bits at B || ... || 3 orientation bits at D
        cornerBits = ((cornerBits & 1) << 11) + (cornerBits >> 1);
        // The result shift indicates which element of BullsEyeCorners[] goes into the top-left
        // corner. Since the four rotation values have a Hamming distance of 8, we
        // can easily tolerate two errors.
        for (int shift = 0; shift < 4; shift++) {
            if (Integer.bitCount(cornerBits ^ EXPECTED_CORNER_BITS[shift]) <= 2) {
                return shift;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /**
     * Extracts the number of data layers and data blocks from the layer around the bull's eye.
     *
     * @param bullsEyeCorners the array of bull's eye corners
     * @throws NotFoundException in case of too many errors or invalid parameters
     */
    private void extractParameters(ResultPoint[] bullsEyeCorners) throws NotFoundException {
        if (!isValid(bullsEyeCorners[0]) || !isValid(bullsEyeCorners[1]) ||
                !isValid(bullsEyeCorners[2]) || !isValid(bullsEyeCorners[3])) {
            throw NotFoundException.getNotFoundInstance();
        }
        int length = 2 * nbCenterLayers;
        // Get the bits around the bull's eye
        int[] sides = {
                sampleLine(bullsEyeCorners[0], bullsEyeCorners[1], length), // Right side
                sampleLine(bullsEyeCorners[1], bullsEyeCorners[2], length), // Bottom
                sampleLine(bullsEyeCorners[2], bullsEyeCorners[3], length), // Left side
                sampleLine(bullsEyeCorners[3], bullsEyeCorners[0], length)  // Top
        };

        // bullsEyeCorners[shift] is the corner of the bulls'eye that has three
        // orientation marks.
        // sides[shift] is the row/column that goes from the corner with three
        // orientation marks to the corner with two.
        shift = getRotation(sides, length);

        // Flatten the parameter bits into a single 28- or 40-bit long
        long parameterData = 0;
        for (int i = 0; i < 4; i++) {
            int side = sides[(shift + i) % 4];
            if (compact) {
                // Each side of the form ..XXXXXXX. where Xs are parameter data
                parameterData <<= 7;
                parameterData += (side >> 1) & 0x7F;
            } else {
                // Each side of the form ..XXXXX.XXXXX. where Xs are parameter data
                parameterData <<= 10;
                parameterData += ((side >> 2) & (0x1f << 5)) + ((side >> 1) & 0x1F);
            }
        }

        // Corrects parameter data using RS.  Returns just the data portion
        // without the error correction.
        int correctedData = getCorrectedParameterData(parameterData, compact);

        if (compact) {
            // 8 bits:  2 bits layers and 6 bits data blocks
            nbLayers = (correctedData >> 6) + 1;
            nbDataBlocks = (correctedData & 0x3F) + 1;
        } else {
            // 16 bits:  5 bits layers and 11 bits data blocks
            nbLayers = (correctedData >> 11) + 1;
            nbDataBlocks = (correctedData & 0x7FF) + 1;
        }
    }

    /**
     * Corrects the parameter bits using Reed-Solomon algorithm.
     *
     * @param parameterData parameter bits
     * @param compact true if this is a compact Aztec code
     * @throws NotFoundException if the array contains too many errors
     */
    private static int getCorrectedParameterData(long parameterData, boolean compact) throws NotFoundException {
        int numCodewords;
        int numDataCodewords;

        if (compact) {
            numCodewords = 7;
            numDataCodewords = 2;
        } else {
            numCodewords = 10;
            numDataCodewords = 4;
        }

        int numECCodewords = numCodewords - numDataCodewords;
        int[] parameterWords = new int[numCodewords];
        for (int i = numCodewords - 1; i >= 0; --i) {
            parameterWords[i] = (int) parameterData & 0xF;
            parameterData >>= 4;
        }
        try {
            ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GenericGF.AZTEC_PARAM);
            rsDecoder.decode(parameterWords, numECCodewords);
        } catch (ReedSolomonException ignored) {
            throw NotFoundException.getNotFoundInstance();
        }
        // Toss the error correction.  Just return the data as an integer
        int result = 0;
        for (int i = 0; i < numDataCodewords; i++) {
            result = (result << 4) + parameterWords[i];
        }
        return result;
    }

    /**
     * Detects an Aztec Code in an image.
     *
     * @param isMirror if true, image is a mirror-image of original
     * @return {@link AztecDetectorResult} encapsulating results of detecting an Aztec Code
     * @throws NotFoundException if no Aztec Code can be found
     */
    public AztecDetectorResult detect(boolean isMirror) throws NotFoundException {

        // 1. Get the center of the aztec matrix
        Point pCenter = getMatrixCenter();

        // 2. Get the center points of the four diagonal points just outside the bull's eye
        //  [topRight, bottomRight, bottomLeft, topLeft]
        ResultPoint[] bullsEyeCorners = getBullsEyeCorners(pCenter);

        if (isMirror) {
            ResultPoint temp = bullsEyeCorners[0];
            bullsEyeCorners[0] = bullsEyeCorners[2];
            bullsEyeCorners[2] = temp;
        }

        // 3. Get the size of the matrix and other parameters from the bull's eye
        extractParameters(bullsEyeCorners);

        // 4. Sample the grid
        BitMatrix bits = sampleGrid(image,
                bullsEyeCorners[shift % 4],
                bullsEyeCorners[(shift + 1) % 4],
                bullsEyeCorners[(shift + 2) % 4],
                bullsEyeCorners[(shift + 3) % 4]);

        // 5. Get the corners of the matrix.
        ResultPoint[] corners = getMatrixCornerPoints(bullsEyeCorners);

        return new AztecDetectorResult(bits, corners, compact, nbDataBlocks, nbLayers);
    }

    /**
     * Gets the Aztec code corners from the bull's eye corners and the parameters.
     *
     * @param bullsEyeCorners the array of bull's eye corners
     * @return the array of aztec code corners
     */
    private ResultPoint[] getMatrixCornerPoints(ResultPoint[] bullsEyeCorners) {
        return expandSquare(bullsEyeCorners, 2 * nbCenterLayers, getDimension());
    }

    /**
     * Finds the corners of a bull-eye centered on the passed point.
     * This returns the centers of the diagonal points just outside the bull's eye
     * Returns [topRight, bottomRight, bottomLeft, topLeft]
     *
     * @param pCenter Center point
     * @return The corners of the bull-eye
     * @throws NotFoundException If no valid bull-eye can be found
     */
    private ResultPoint[] getBullsEyeCorners(Point pCenter) throws NotFoundException {

        Point pina = pCenter;
        Point pinb = pCenter;
        Point pinc = pCenter;
        Point pind = pCenter;

        boolean color = true;

        for (nbCenterLayers = 1; nbCenterLayers < 9; nbCenterLayers++) {
            Point pouta = getFirstDifferent(pina, color, 1, -1);
            Point poutb = getFirstDifferent(pinb, color, 1, 1);
            Point poutc = getFirstDifferent(pinc, color, -1, 1);
            Point poutd = getFirstDifferent(pind, color, -1, -1);

            //d      a
            //
            //c      b

            if (nbCenterLayers > 2) {
                float q = distance(poutd, pouta) * nbCenterLayers / (distance(pind, pina) * (nbCenterLayers + 2));
                if (q < 0.75 || q > 1.25 || !isWhiteOrBlackRectangle(pouta, poutb, poutc, poutd)) {
                    break;
                }
            }

            pina = pouta;
            pinb = poutb;
            pinc = poutc;
            pind = poutd;

            color = !color;
        }

        if (nbCenterLayers != 5 && nbCenterLayers != 7) {
            throw NotFoundException.getNotFoundInstance();
        }

        compact = nbCenterLayers == 5;

        // Expand the square by .5 pixel in each direction so that we're on the border
        // between the white square and the black square
        ResultPoint pinax = new ResultPoint(pina.getX() + 0.5f, pina.getY() - 0.5f);
        ResultPoint pinbx = new ResultPoint(pinb.getX() + 0.5f, pinb.getY() + 0.5f);
        ResultPoint pincx = new ResultPoint(pinc.getX() - 0.5f, pinc.getY() + 0.5f);
        ResultPoint pindx = new ResultPoint(pind.getX() - 0.5f, pind.getY() - 0.5f);

        // Expand the square so that its corners are the centers of the points
        // just outside the bull's eye.
        return expandSquare(new ResultPoint[]{pinax, pinbx, pincx, pindx},
                2 * nbCenterLayers - 3,
                2 * nbCenterLayers);
    }

    /**
     * Expand the square represented by the corner points by pushing out equally in all directions
     *
     * @param cornerPoints the corners of the square, which has the bull's eye at its center
     * @param oldSide the original length of the side of the square in the target bit matrix
     * @param newSide the new length of the size of the square in the target bit matrix
     * @return the corners of the expanded square
     */
    private static ResultPoint[] expandSquare(ResultPoint[] cornerPoints, float oldSide, float newSide) {
        float ratio = newSide / (2 * oldSide);
        float dx = cornerPoints[0].getX() - cornerPoints[2].getX();
        float dy = cornerPoints[0].getY() - cornerPoints[2].getY();
        float centerx = (cornerPoints[0].getX() + cornerPoints[2].getX()) / 2.0f;
        float centery = (cornerPoints[0].getY() + cornerPoints[2].getY()) / 2.0f;

        ResultPoint result0 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
        ResultPoint result2 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);

        dx = cornerPoints[1].getX() - cornerPoints[3].getX();
        dy = cornerPoints[1].getY() - cornerPoints[3].getY();
        centerx = (cornerPoints[1].getX() + cornerPoints[3].getX()) / 2.0f;
        centery = (cornerPoints[1].getY() + cornerPoints[3].getY()) / 2.0f;
        ResultPoint result1 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
        ResultPoint result3 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);

        return new ResultPoint[]{result0, result1, result2, result3};
    }

    /**
     * Gets the color of a segment
     *
     * @return 1 if segment more than 90% black, -1 if segment is more than 90% white, 0 else
     */
    private int getColor(Point p1, Point p2) {
        float d = distance(p1, p2);
        float dx = (p2.getX() - p1.getX()) / d;
        float dy = (p2.getY() - p1.getY()) / d;
        int error = 0;

        float px = p1.getX();
        float py = p1.getY();

        boolean colorModel = image.get(p1.getX(), p1.getY());

        for (int i = 0; i < d; i++) {
            px += dx;
            py += dy;
            if (image.get(MathUtils.round(px), MathUtils.round(py)) != colorModel) {
                error++;
            }
        }

        float errRatio = error / d;

        if (errRatio > 0.1f && errRatio < 0.9f) {
            return 0;
        }

        return (errRatio <= 0.1f) == colorModel ? 1 : -1;
    }

    /**
     * @return true if the border of the rectangle passed in parameter is compound of white points only
     *         or black points only
     */
    private boolean isWhiteOrBlackRectangle(Point p1,
                                            Point p2,
                                            Point p3,
                                            Point p4) {

        int corr = 3;

        p1 = new Point(p1.getX() - corr, p1.getY() + corr);
        p2 = new Point(p2.getX() - corr, p2.getY() - corr);
        p3 = new Point(p3.getX() + corr, p3.getY() - corr);
        p4 = new Point(p4.getX() + corr, p4.getY() + corr);

        int cInit = getColor(p4, p1);

        if (cInit == 0) {
            return false;
        }

        int c = getColor(p1, p2);

        if (c != cInit) {
            return false;
        }

        c = getColor(p2, p3);

        if (c != cInit) {
            return false;
        }

        c = getColor(p3, p4);

        return c == cInit;

    }


    private int getDimension() {
        if (compact) {
            return 4 * nbLayers + 11;
        }
        if (nbLayers <= 4) {
            return 4 * nbLayers + 15;
        }
        return 4 * nbLayers + 2 * ((nbLayers - 4) / 8 + 1) + 15;
    }

    static final class Point {
        private final int x;
        private final int y;

        ResultPoint toResultPoint() {
            return new ResultPoint(getX(), getY());
        }

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "<" + x + ' ' + y + '>';
        }
    }



}
