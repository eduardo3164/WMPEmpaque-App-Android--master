package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.Arrays;
import java.util.Map;

/**
 * <p>The main class which implements QR Code decoding -- as opposed to locating and extracting
 * the QR Code from an image.</p>
 *
 * @author Sean Owen
 */
public final class Decoder {

    private final ReedSolomonDecoder rsDecoder;

    public Decoder() {
        rsDecoder = new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
    }

    private AztecDetectorResult ddata;

    public DecoderResult decode(AztecDetectorResult detectorResult) throws FormatException {
        ddata = detectorResult;
        BitMatrix matrix = detectorResult.getBits();
        boolean[] rawbits = extractBits(matrix);
        boolean[] correctedBits = correctBits(rawbits);
        String result = getEncodedData(correctedBits);
        return new DecoderResult(null, result, null, null);
    }

    private enum Table {
        UPPER,
        LOWER,
        MIXED,
        DIGIT,
        PUNCT,
        BINARY
    }

    private static final String[] UPPER_TABLE = {
            "CTRL_PS", " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS"
    };

    private static final String[] LOWER_TABLE = {
            "CTRL_PS", " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
            "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS"
    };

    private static final String[] MIXED_TABLE = {
            "CTRL_PS", " ", "\1", "\2", "\3", "\4", "\5", "\6", "\7", "\b", "\t", "\n",
            "\13", "\f", "\r", "\33", "\34", "\35", "\36", "\37", "@", "\\", "^", "_",
            "`", "|", "~", "\177", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS"
    };

    private static final String[] PUNCT_TABLE = {
            "", "\r", "\r\n", ". ", ", ", ": ", "!", "\"", "#", "$", "%", "&", "'", "(", ")",
            "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "[", "]", "{", "}", "CTRL_UL"
    };

    private static final String[] DIGIT_TABLE = {
            "CTRL_PS", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", "CTRL_UL", "CTRL_US"
    };


    /**
     * Gets the string encoded in the aztec code bits
     *
     * @return the decoded string
     */
    private static String getEncodedData(boolean[] correctedBits) {
        int endIndex = correctedBits.length;
        Table latchTable = Table.UPPER; // table most recently latched to
        Table shiftTable = Table.UPPER; // table to use for the next read
        StringBuilder result = new StringBuilder(20);
        int index = 0;
        while (index < endIndex) {
            if (shiftTable == Table.BINARY) {
                if (endIndex - index < 5) {
                    break;
                }
                int length = readCode(correctedBits, index, 5);
                index += 5;
                if (length == 0) {
                    if (endIndex - index < 11) {
                        break;
                    }
                    length = readCode(correctedBits, index, 11) + 31;
                    index += 11;
                }
                for (int charCount = 0; charCount < length; charCount++) {
                    if (endIndex - index < 8) {
                        index = endIndex;  // Force outer loop to exit
                        break;
                    }
                    int code = readCode(correctedBits, index, 8);
                    result.append((char) code);
                    index += 8;
                }
                // Go back to whatever mode we had been in
                shiftTable = latchTable;
            } else {
                int size = shiftTable == Table.DIGIT ? 4 : 5;
                if (endIndex - index < size) {
                    break;
                }
                int code = readCode(correctedBits, index, size);
                index += size;
                String str = getCharacter(shiftTable, code);
                if (str.startsWith("CTRL_")) {
                    // Table changes
                    shiftTable = getTable(str.charAt(5));
                    if (str.charAt(6) == 'L') {
                        latchTable = shiftTable;
                    }
                } else {
                    result.append(str);
                    // Go back to whatever mode we had been in
                    shiftTable = latchTable;
                }
            }
        }
        return result.toString();
    }

    /**
     * gets the table corresponding to the char passed
     */
    private static Table getTable(char t) {
        switch (t) {
            case 'L':
                return Table.LOWER;
            case 'P':
                return Table.PUNCT;
            case 'M':
                return Table.MIXED;
            case 'D':
                return Table.DIGIT;
            case 'B':
                return Table.BINARY;
            case 'U':
            default:
                return Table.UPPER;
        }
    }


    private static String getCharacter(Table table, int code) {
        switch (table) {
            case UPPER:
                return UPPER_TABLE[code];
            case LOWER:
                return LOWER_TABLE[code];
            case MIXED:
                return MIXED_TABLE[code];
            case PUNCT:
                return PUNCT_TABLE[code];
            case DIGIT:
                return DIGIT_TABLE[code];
            default:
                // Should not reach here.
                throw new IllegalStateException("Bad table");
        }
    }

    /**
     * <p>Performs RS error correction on an array of bits.</p>
     *
     * @return the corrected array
     * @throws FormatException if the input contains too many errors
     */
    private boolean[] correctBits(boolean[] rawbits) throws FormatException {
        GenericGF gf;
        int codewordSize;

        if (ddata.getNbLayers() <= 2) {
            codewordSize = 6;
            gf = GenericGF.AZTEC_DATA_6;
        } else if (ddata.getNbLayers() <= 8) {
            codewordSize = 8;
            gf = GenericGF.AZTEC_DATA_8;
        } else if (ddata.getNbLayers() <= 22) {
            codewordSize = 10;
            gf = GenericGF.AZTEC_DATA_10;
        } else {
            codewordSize = 12;
            gf = GenericGF.AZTEC_DATA_12;
        }

        int numDataCodewords = ddata.getNbDatablocks();
        int numCodewords = rawbits.length / codewordSize;
        if (numCodewords < numDataCodewords) {
            throw FormatException.getFormatInstance();
        }
        int offset = rawbits.length % codewordSize;
        int numECCodewords = numCodewords - numDataCodewords;

        int[] dataWords = new int[numCodewords];
        for (int i = 0; i < numCodewords; i++, offset += codewordSize) {
            dataWords[i] = readCode(rawbits, offset, codewordSize);
        }

        try {
            ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(gf);
            rsDecoder.decode(dataWords, numECCodewords);
        } catch (ReedSolomonException ex) {
            throw FormatException.getFormatInstance(ex);
        }

        // Now perform the unstuffing operation.
        // First, count how many bits are going to be thrown out as stuffing
        int mask = (1 << codewordSize) - 1;
        int stuffedBits = 0;
        for (int i = 0; i < numDataCodewords; i++) {
            int dataWord = dataWords[i];
            if (dataWord == 0 || dataWord == mask) {
                throw FormatException.getFormatInstance();
            } else if (dataWord == 1 || dataWord == mask - 1) {
                stuffedBits++;
            }
        }
        // Now, actually unpack the bits and remove the stuffing
        boolean[] correctedBits = new boolean[numDataCodewords * codewordSize - stuffedBits];
        int index = 0;
        for (int i = 0; i < numDataCodewords; i++) {
            int dataWord = dataWords[i];
            if (dataWord == 1 || dataWord == mask - 1) {
                // next codewordSize-1 bits are all zeros or all ones
                Arrays.fill(correctedBits, index, index + codewordSize - 1, dataWord > 1);
                index += codewordSize - 1;
            } else {
                for (int bit = codewordSize - 1; bit >= 0; --bit) {
                    correctedBits[index++] = (dataWord & (1 << bit)) != 0;
                }
            }
        }
        return correctedBits;
    }

    /**
     * Reads a code of given length and at given index in an array of bits
     */
    private static int readCode(boolean[] rawbits, int startIndex, int length) {
        int res = 0;
        for (int i = startIndex; i < startIndex + length; i++) {
            res <<= 1;
            if (rawbits[i]) {
                res |= 0x01;
            }
        }
        return res;
    }


    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : 112) + 16 * layers) * layers;
    }

    boolean[] extractBits(BitMatrix matrix) {
        boolean compact = ddata.isCompact();
        int layers = ddata.getNbLayers();
        int baseMatrixSize = compact ? 11 + layers * 4 : 14 + layers * 4; // not including alignment lines
        int[] alignmentMap = new int[baseMatrixSize];
        boolean[] rawbits = new boolean[totalBitsInLayer(layers, compact)];

        if (compact) {
            for (int i = 0; i < alignmentMap.length; i++) {
                alignmentMap[i] = i;
            }
        } else {
            int matrixSize = baseMatrixSize + 1 + 2 * ((baseMatrixSize / 2 - 1) / 15);
            int origCenter = baseMatrixSize / 2;
            int center = matrixSize / 2;
            for (int i = 0; i < origCenter; i++) {
                int newOffset = i + i / 15;
                alignmentMap[origCenter - i - 1] = center - newOffset - 1;
                alignmentMap[origCenter + i] = center + newOffset + 1;
            }
        }
        for (int i = 0, rowOffset = 0; i < layers; i++) {
            int rowSize = compact ? (layers - i) * 4 + 9 : (layers - i) * 4 + 12;
            // The top-left most point of this layer is <low, low> (not including alignment lines)
            int low = i * 2;
            // The bottom-right most point of this layer is <high, high> (not including alignment lines)
            int high = baseMatrixSize - 1 - low;
            // We pull bits from the two 2 x rowSize columns and two rowSize x 2 rows
            for (int j = 0; j < rowSize; j++) {
                int columnOffset = j * 2;
                for (int k = 0; k < 2; k++) {
                    // left column
                    rawbits[rowOffset + columnOffset + k] =
                            matrix.get(alignmentMap[low + k], alignmentMap[low + j]);
                    // bottom row
                    rawbits[rowOffset + 2 * rowSize + columnOffset + k] =
                            matrix.get(alignmentMap[low + j], alignmentMap[high - k]);
                    // right column
                    rawbits[rowOffset + 4 * rowSize + columnOffset + k] =
                            matrix.get(alignmentMap[high - k], alignmentMap[high - j]);
                    // top row
                    rawbits[rowOffset + 6 * rowSize + columnOffset + k] =
                            matrix.get(alignmentMap[high - j], alignmentMap[low + k]);
                }
            }
            rowOffset += rowSize * 8;
        }
        return rawbits;
    }

    public DecoderResult decode(boolean[][] image) throws ChecksumException, FormatException {
        return decode(image, null);
    }

    /**
     * <p>Convenience method that can decode a QR Code represented as a 2D array of booleans.
     * "true" is taken to mean a black module.</p>
     *
     * @param image booleans representing white/black QR Code modules
     * @param hints decoding hints that should be used to influence decoding
     * @return text and bytes encoded within the QR Code
     * @throws FormatException if the QR Code cannot be decoded
     * @throws ChecksumException if error correction fails
     */
    public DecoderResult decode(boolean[][] image, Map<DecodeHintType,?> hints)
            throws ChecksumException, FormatException {
        int dimension = image.length;
        BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (image[i][j]) {
                    bits.set(j, i);
                }
            }
        }
        return decode(bits, hints);
    }

    public DecoderResult decode(BitMatrix bits) throws ChecksumException, FormatException {
        return decode(bits, null);
    }

    /**
     * <p>Decodes a QR Code represented as a {@link BitMatrix}. A 1 or "true" is taken to mean a black module.</p>
     *
     * @param bits booleans representing white/black QR Code modules
     * @param hints decoding hints that should be used to influence decoding
     * @return text and bytes encoded within the QR Code
     * @throws FormatException if the QR Code cannot be decoded
     * @throws ChecksumException if error correction fails
     */
    public DecoderResult decode(BitMatrix bits, Map<DecodeHintType,?> hints)
            throws FormatException, ChecksumException {

        // Construct a parser and read version, error-correction level
        BitMatrixParser parser = new BitMatrixParser(bits);
        FormatException fe = null;
        ChecksumException ce = null;
        try {
            return decode(parser, hints);
        } catch (FormatException e) {
            fe = e;
        } catch (ChecksumException e) {
            ce = e;
        }

        try {

            // Revert the bit matrix
            parser.remask();

            // Will be attempting a mirrored reading of the version and format info.
            parser.setMirror(true);

            // Preemptively read the version.
            parser.readVersion();

            // Preemptively read the format information.
            parser.readFormatInformation();

      /*
       * Since we're here, this means we have successfully detected some kind
       * of version and format information when mirrored. This is a good sign,
       * that the QR code may be mirrored, and we should try once more with a
       * mirrored content.
       */
            // Prepare for a mirrored reading.
            parser.mirror();

            DecoderResult result = decode(parser, hints);

            // Success! Notify the caller that the code was mirrored.
            result.setOther(new QRCodeDecoderMetaData(true));

            return result;

        } catch (FormatException | ChecksumException e) {
            // Throw the exception from the original reading
            if (fe != null) {
                throw fe;
            }
            if (ce != null) {
                throw ce;
            }
            throw e;

        }
    }

    private DecoderResult decode(BitMatrixParser parser, Map<DecodeHintType,?> hints)
            throws FormatException, ChecksumException {
        Version version = parser.readVersion();
        ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();

        // Read codewords
        byte[] codewords = parser.readCodewords();
        // Separate into data blocks
        DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version, ecLevel);

        // Count total number of data bytes
        int totalBytes = 0;
        for (DataBlock dataBlock : dataBlocks) {
            totalBytes += dataBlock.getNumDataCodewords();
        }
        byte[] resultBytes = new byte[totalBytes];
        int resultOffset = 0;

        // Error-correct and copy data blocks together into a stream of bytes
        for (DataBlock dataBlock : dataBlocks) {
            byte[] codewordBytes = dataBlock.getCodewords();
            int numDataCodewords = dataBlock.getNumDataCodewords();
            correctErrors(codewordBytes, numDataCodewords);
            for (int i = 0; i < numDataCodewords; i++) {
                resultBytes[resultOffset++] = codewordBytes[i];
            }
        }

        // Decode the contents of that stream of bytes
        return DecodedBitStreamParser.decode(resultBytes, version, ecLevel, hints);
    }

    /**
     * <p>Given data and error-correction codewords received, possibly corrupted by errors, attempts to
     * correct the errors in-place using Reed-Solomon error correction.</p>
     *
     * @param codewordBytes data and error correction codewords
     * @param numDataCodewords number of codewords that are data bytes
     * @throws ChecksumException if error correction fails
     */
    private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException {
        int numCodewords = codewordBytes.length;
        // First read into an array of ints
        int[] codewordsInts = new int[numCodewords];
        for (int i = 0; i < numCodewords; i++) {
            codewordsInts[i] = codewordBytes[i] & 0xFF;
        }
        int numECCodewords = codewordBytes.length - numDataCodewords;
        try {
            rsDecoder.decode(codewordsInts, numECCodewords);
        } catch (ReedSolomonException ignored) {
            throw ChecksumException.getChecksumInstance();
        }
        // Copy back into array of bytes -- only need to worry about the bytes that were data
        // We don't care about errors in the error-correction codewords
        for (int i = 0; i < numDataCodewords; i++) {
            codewordBytes[i] = (byte) codewordsInts[i];
        }
    }

}
