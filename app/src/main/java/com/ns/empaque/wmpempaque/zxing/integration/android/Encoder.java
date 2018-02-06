package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
public final class Encoder {

    public static final int DEFAULT_EC_PERCENT = 33; // default minimal percentage of error check words
    public static final int DEFAULT_AZTEC_LAYERS = 0;
    private static final int MAX_NB_BITS = 32;
    private static final int MAX_NB_BITS_COMPACT = 4;

    private static final int[] WORD_SIZE = {
            4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            12, 12, 12, 12, 12, 12, 12, 12, 12, 12
    };

    // The original table is defined in the table 5 of JISX0510:2004 (p.19).
    private static final int[] ALPHANUMERIC_TABLE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x00-0x0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x10-0x1f
            36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43,  // 0x20-0x2f
            0,   1,  2,  3,  4,  5,  6,  7,  8,  9, 44, -1, -1, -1, -1, -1,  // 0x30-0x3f
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 0x40-0x4f
            25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1,  // 0x50-0x5f
    };
    /**
     * Encodes the given binary content as an Aztec symbol
     *
     * @param data input data string
     * @return Aztec symbol matrix with metadata
     */
    public static AztecCode encode(byte[] data) {
        return encode(data, DEFAULT_EC_PERCENT, DEFAULT_AZTEC_LAYERS);
    }

    static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

    private static int[] bitsToWords(BitArray stuffedBits, int wordSize, int totalWords) {
        int[] message = new int[totalWords];
        int i;
        int n;
        for (i = 0, n = stuffedBits.getSize() / wordSize; i < n; i++) {
            int value = 0;
            for (int j = 0; j < wordSize; j++) {
                value |= stuffedBits.get(i * wordSize + j) ? (1 << wordSize - j - 1) : 0;
            }
            message[i] = value;
        }
        return message;
    }

    private static GenericGF getGF(int wordSize) {
        switch (wordSize) {
            case 4:
                return GenericGF.AZTEC_PARAM;
            case 6:
                return GenericGF.AZTEC_DATA_6;
            case 8:
                return GenericGF.AZTEC_DATA_8;
            case 10:
                return GenericGF.AZTEC_DATA_10;
            case 12:
                return GenericGF.AZTEC_DATA_12;
            default:
                throw new IllegalArgumentException("Unsupported word size " + wordSize);
        }
    }

    private static BitArray generateCheckWords(BitArray bitArray, int totalBits, int wordSize) {
        // bitArray is guaranteed to be a multiple of the wordSize, so no padding needed
        int messageSizeInWords = bitArray.getSize() / wordSize;
        ReedSolomonEncoder rs = new ReedSolomonEncoder(getGF(wordSize));
        int totalWords = totalBits / wordSize;
        int[] messageWords = bitsToWords(bitArray, wordSize, totalWords);
        rs.encode(messageWords, totalWords - messageSizeInWords);
        int startPad = totalBits % wordSize;
        BitArray messageBits = new BitArray();
        messageBits.appendBits(0, startPad);
        for (int messageWord : messageWords) {
            messageBits.appendBits(messageWord, wordSize);
        }
        return messageBits;
    }

    static BitArray stuffBits(BitArray bits, int wordSize) {
        BitArray out = new BitArray();

        int n = bits.getSize();
        int mask = (1 << wordSize) - 2;
        for (int i = 0; i < n; i += wordSize) {
            int word = 0;
            for (int j = 0; j < wordSize; j++) {
                if (i + j >= n || bits.get(i + j)) {
                    word |= 1 << (wordSize - 1 - j);
                }
            }
            if ((word & mask) == mask) {
                out.appendBits(word & mask, wordSize);
                i--;
            } else if ((word & mask) == 0) {
                out.appendBits(word | 1, wordSize);
                i--;
            } else {
                out.appendBits(word, wordSize);
            }
        }
        return out;
    }

    static BitArray generateModeMessage(boolean compact, int layers, int messageSizeInWords) {
        BitArray modeMessage = new BitArray();
        if (compact) {
            modeMessage.appendBits(layers - 1, 2);
            modeMessage.appendBits(messageSizeInWords - 1, 6);
            modeMessage = generateCheckWords(modeMessage, 28, 4);
        } else {
            modeMessage.appendBits(layers - 1, 5);
            modeMessage.appendBits(messageSizeInWords - 1, 11);
            modeMessage = generateCheckWords(modeMessage, 40, 4);
        }
        return modeMessage;
    }

    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : 112) + 16 * layers) * layers;
    }

    /**
     * Encodes the given binary content as an Aztec symbol
     *
     * @param data input data string
     * @param minECCPercent minimal percentage of error check words (According to ISO/IEC 24778:2008,
     *                      a minimum of 23% + 3 words is recommended)
     * @param userSpecifiedLayers if non-zero, a user-specified value for the number of layers
     * @return Aztec symbol matrix with metadata
     */
    public static AztecCode encode(byte[] data, int minECCPercent, int userSpecifiedLayers) {
        // High-level encode
        BitArray bits = new HighLevelEncoder(data).encode();

        // stuff bits and choose symbol size
        int eccBits = bits.getSize() * minECCPercent / 100 + 11;
        int totalSizeBits = bits.getSize() + eccBits;
        boolean compact;
        int layers;
        int totalBitsInLayer;
        int wordSize;
        BitArray stuffedBits;
        if (userSpecifiedLayers != DEFAULT_AZTEC_LAYERS) {
            compact = userSpecifiedLayers < 0;
            layers = Math.abs(userSpecifiedLayers);
            if (layers > (compact ? MAX_NB_BITS_COMPACT : MAX_NB_BITS)) {
                throw new IllegalArgumentException(
                        String.format("Illegal value %s for layers", userSpecifiedLayers));
            }
            totalBitsInLayer = totalBitsInLayer(layers, compact);
            wordSize = WORD_SIZE[layers];
            int usableBitsInLayers = totalBitsInLayer - (totalBitsInLayer % wordSize);
            stuffedBits = stuffBits(bits, wordSize);
            if (stuffedBits.getSize() + eccBits > usableBitsInLayers) {
                throw new IllegalArgumentException("Data to large for user specified layer");
            }
            if (compact && stuffedBits.getSize() > wordSize * 64) {
                // Compact format only allows 64 data words, though C4 can hold more words than that
                throw new IllegalArgumentException("Data to large for user specified layer");
            }
        } else {
            wordSize = 0;
            stuffedBits = null;
            // We look at the possible table sizes in the order Compact1, Compact2, Compact3,
            // Compact4, Normal4,...  Normal(i) for i < 4 isn't typically used since Compact(i+1)
            // is the same size, but has more data.
            for (int i = 0; ; i++) {
                if (i > MAX_NB_BITS) {
                    throw new IllegalArgumentException("Data too large for an Aztec code");
                }
                compact = i <= 3;
                layers = compact ? i + 1 : i;
                totalBitsInLayer = totalBitsInLayer(layers, compact);
                if (totalSizeBits > totalBitsInLayer) {
                    continue;
                }
                // [Re]stuff the bits if this is the first opportunity, or if the
                // wordSize has changed
                if (wordSize != WORD_SIZE[layers]) {
                    wordSize = WORD_SIZE[layers];
                    stuffedBits = stuffBits(bits, wordSize);
                }
                int usableBitsInLayers = totalBitsInLayer - (totalBitsInLayer % wordSize);
                if (compact && stuffedBits.getSize() > wordSize * 64) {
                    // Compact format only allows 64 data words, though C4 can hold more words than that
                    continue;
                }
                if (stuffedBits.getSize() + eccBits <= usableBitsInLayers) {
                    break;
                }
            }
        }
        BitArray messageBits = generateCheckWords(stuffedBits, totalBitsInLayer, wordSize);

        // generate mode message
        int messageSizeInWords = stuffedBits.getSize() / wordSize;
        BitArray modeMessage = generateModeMessage(compact, layers, messageSizeInWords);

        // allocate symbol
        int baseMatrixSize = compact ? 11 + layers * 4 : 14 + layers * 4; // not including alignment lines
        int[] alignmentMap = new int[baseMatrixSize];
        int matrixSize;
        if (compact) {
            // no alignment marks in compact mode, alignmentMap is a no-op
            matrixSize = baseMatrixSize;
            for (int i = 0; i < alignmentMap.length; i++) {
                alignmentMap[i] = i;
            }
        } else {
            matrixSize = baseMatrixSize + 1 + 2 * ((baseMatrixSize / 2 - 1) / 15);
            int origCenter = baseMatrixSize / 2;
            int center = matrixSize / 2;
            for (int i = 0; i < origCenter; i++) {
                int newOffset = i + i / 15;
                alignmentMap[origCenter - i - 1] = center - newOffset - 1;
                alignmentMap[origCenter + i] = center + newOffset + 1;
            }
        }
        BitMatrix matrix = new BitMatrix(matrixSize);

        // draw data bits
        for (int i = 0, rowOffset = 0; i < layers; i++) {
            int rowSize = compact ? (layers - i) * 4 + 9 : (layers - i) * 4 + 12;
            for (int j = 0; j < rowSize; j++) {
                int columnOffset = j * 2;
                for (int k = 0; k < 2; k++) {
                    if (messageBits.get(rowOffset + columnOffset + k)) {
                        matrix.set(alignmentMap[i * 2 + k], alignmentMap[i * 2 + j]);
                    }
                    if (messageBits.get(rowOffset + rowSize * 2 + columnOffset + k)) {
                        matrix.set(alignmentMap[i * 2 + j], alignmentMap[baseMatrixSize - 1 - i * 2 - k]);
                    }
                    if (messageBits.get(rowOffset + rowSize * 4 + columnOffset + k)) {
                        matrix.set(alignmentMap[baseMatrixSize - 1 - i * 2 - k], alignmentMap[baseMatrixSize - 1 - i * 2 - j]);
                    }
                    if (messageBits.get(rowOffset + rowSize * 6 + columnOffset + k)) {
                        matrix.set(alignmentMap[baseMatrixSize - 1 - i * 2 - j], alignmentMap[i * 2 + k]);
                    }
                }
            }
            rowOffset += rowSize * 8;
        }

        // draw mode message
        drawModeMessage(matrix, compact, matrixSize, modeMessage);

        // draw alignment marks
        if (compact) {
            drawBullsEye(matrix, matrixSize / 2, 5);
        } else {
            drawBullsEye(matrix, matrixSize / 2, 7);
            for (int i = 0, j = 0; i < baseMatrixSize / 2 - 1; i += 15, j += 16) {
                for (int k = (matrixSize / 2) & 1; k < matrixSize; k += 2) {
                    matrix.set(matrixSize / 2 - j, k);
                    matrix.set(matrixSize / 2 + j, k);
                    matrix.set(k, matrixSize / 2 - j);
                    matrix.set(k, matrixSize / 2 + j);
                }
            }
        }

        AztecCode aztec = new AztecCode();
        aztec.setCompact(compact);
        aztec.setSize(matrixSize);
        aztec.setLayers(layers);
        aztec.setCodeWords(messageSizeInWords);
        aztec.setMatrix(matrix);
        return aztec;
    }

    private static void drawBullsEye(BitMatrix matrix, int center, int size) {
        for (int i = 0; i < size; i += 2) {
            for (int j = center - i; j <= center + i; j++) {
                matrix.set(j, center - i);
                matrix.set(j, center + i);
                matrix.set(center - i, j);
                matrix.set(center + i, j);
            }
        }
        matrix.set(center - size, center - size);
        matrix.set(center - size + 1, center - size);
        matrix.set(center - size, center - size + 1);
        matrix.set(center + size, center - size);
        matrix.set(center + size, center - size + 1);
        matrix.set(center + size, center + size - 1);
    }

    private static void drawModeMessage(BitMatrix matrix, boolean compact, int matrixSize, BitArray modeMessage) {
        int center = matrixSize / 2;
        if (compact) {
            for (int i = 0; i < 7; i++) {
                int offset = center - 3 + i;
                if (modeMessage.get(i)) {
                    matrix.set(offset, center - 5);
                }
                if (modeMessage.get(i + 7)) {
                    matrix.set(center + 5, offset);
                }
                if (modeMessage.get(20 - i)) {
                    matrix.set(offset, center + 5);
                }
                if (modeMessage.get(27 - i)) {
                    matrix.set(center - 5, offset);
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                int offset = center - 5 + i + i / 5;
                if (modeMessage.get(i)) {
                    matrix.set(offset, center - 7);
                }
                if (modeMessage.get(i + 10)) {
                    matrix.set(center + 7, offset);
                }
                if (modeMessage.get(29 - i)) {
                    matrix.set(offset, center + 7);
                }
                if (modeMessage.get(39 - i)) {
                    matrix.set(center - 7, offset);
                }
            }
        }
    }

    private Encoder() {
    }

    // The mask penalty calculation is complicated.  See Table 21 of JISX0510:2004 (p.45) for details.
    // Basically it applies four rules and summate all penalties.
    private static int calculateMaskPenalty(ByteMatrix matrix) {
        return MaskUtil.applyMaskPenaltyRule1(matrix)
                + MaskUtil.applyMaskPenaltyRule2(matrix)
                + MaskUtil.applyMaskPenaltyRule3(matrix)
                + MaskUtil.applyMaskPenaltyRule4(matrix);
    }

    /**
     * @param content text to encode
     * @param ecLevel error correction level to use
     * @return {@link QRCode} representing the encoded QR code
     * @throws WriterException if encoding can't succeed, because of for example invalid content
     *   or configuration
     */
    public static QRCode encode(String content, ErrorCorrectionLevel ecLevel) throws WriterException {
        return encode(content, ecLevel, null);
    }

    public static QRCode encode(String content,
                                ErrorCorrectionLevel ecLevel,
                                Map<EncodeHintType,?> hints) throws WriterException {

        // Determine what character encoding has been specified by the caller, if any
        String encoding = hints == null ? null : (String) hints.get(EncodeHintType.CHARACTER_SET);
        if (encoding == null) {
            encoding = DEFAULT_BYTE_MODE_ENCODING;
        }

        // Pick an encoding mode appropriate for the content. Note that this will not attempt to use
        // multiple modes / segments even if that were more efficient. Twould be nice.
        Mode mode = chooseMode(content, encoding);

        // This will store the header information, like mode and
        // length, as well as "header" segments like an ECI segment.
        BitArray headerBits = new BitArray();

        // Append ECI segment if applicable
        if (mode == Mode.BYTE && !DEFAULT_BYTE_MODE_ENCODING.equals(encoding)) {
            CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
            if (eci != null) {
                appendECI(eci, headerBits);
            }
        }

        // (With ECI in place,) Write the mode marker
        appendModeInfo(mode, headerBits);

        // Collect data within the main segment, separately, to count its size if needed. Don't add it to
        // main payload yet.
        BitArray dataBits = new BitArray();
        appendBytes(content, mode, dataBits, encoding);

        // Hard part: need to know version to know how many bits length takes. But need to know how many
        // bits it takes to know version. First we take a guess at version by assuming version will be
        // the minimum, 1:

        int provisionalBitsNeeded = headerBits.getSize()
                + mode.getCharacterCountBits(Version.getVersionForNumber(1))
                + dataBits.getSize();
        Version provisionalVersion = chooseVersion(provisionalBitsNeeded, ecLevel);

        // Use that guess to calculate the right version. I am still not sure this works in 100% of cases.

        int bitsNeeded = headerBits.getSize()
                + mode.getCharacterCountBits(provisionalVersion)
                + dataBits.getSize();
        Version version = chooseVersion(bitsNeeded, ecLevel);

        BitArray headerAndDataBits = new BitArray();
        headerAndDataBits.appendBitArray(headerBits);
        // Find "length" of main segment and write it
        int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
        appendLengthInfo(numLetters, version, mode, headerAndDataBits);
        // Put data together into the overall payload
        headerAndDataBits.appendBitArray(dataBits);

        Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
        int numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords();

        // Terminate the bits properly.
        terminateBits(numDataBytes, headerAndDataBits);

        // Interleave data bits with error correction code.
        BitArray finalBits = interleaveWithECBytes(headerAndDataBits,
                version.getTotalCodewords(),
                numDataBytes,
                ecBlocks.getNumBlocks());

        QRCode qrCode = new QRCode();

        qrCode.setECLevel(ecLevel);
        qrCode.setMode(mode);
        qrCode.setVersion(version);

        //  Choose the mask pattern and set to "qrCode".
        int dimension = version.getDimensionForVersion();
        ByteMatrix matrix = new ByteMatrix(dimension, dimension);
        int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
        qrCode.setMaskPattern(maskPattern);

        // Build the matrix and set it to "qrCode".
        MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
        qrCode.setMatrix(matrix);

        return qrCode;
    }

    /**
     * @return the code point of the table used in alphanumeric mode or
     *  -1 if there is no corresponding code in the table.
     */
    static int getAlphanumericCode(int code) {
        if (code < ALPHANUMERIC_TABLE.length) {
            return ALPHANUMERIC_TABLE[code];
        }
        return -1;
    }

    public static Mode chooseMode(String content) {
        return chooseMode(content, null);
    }

    /**
     * Choose the best mode by examining the content. Note that 'encoding' is used as a hint;
     * if it is Shift_JIS, and the input is only double-byte Kanji, then we return {@link Mode#KANJI}.
     */
    private static Mode chooseMode(String content, String encoding) {
        if ("Shift_JIS".equals(encoding)) {
            // Choose Kanji mode if all input are double-byte characters
            return isOnlyDoubleByteKanji(content) ? Mode.KANJI : Mode.BYTE;
        }
        boolean hasNumeric = false;
        boolean hasAlphanumeric = false;
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                hasNumeric = true;
            } else if (getAlphanumericCode(c) != -1) {
                hasAlphanumeric = true;
            } else {
                return Mode.BYTE;
            }
        }
        if (hasAlphanumeric) {
            return Mode.ALPHANUMERIC;
        }
        if (hasNumeric) {
            return Mode.NUMERIC;
        }
        return Mode.BYTE;
    }

    private static boolean isOnlyDoubleByteKanji(String content) {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException ignored) {
            return false;
        }
        int length = bytes.length;
        if (length % 2 != 0) {
            return false;
        }
        for (int i = 0; i < length; i += 2) {
            int byte1 = bytes[i] & 0xFF;
            if ((byte1 < 0x81 || byte1 > 0x9F) && (byte1 < 0xE0 || byte1 > 0xEB)) {
                return false;
            }
        }
        return true;
    }

    private static int chooseMaskPattern(BitArray bits,
                                         ErrorCorrectionLevel ecLevel,
                                         Version version,
                                         ByteMatrix matrix) throws WriterException {

        int minPenalty = Integer.MAX_VALUE;  // Lower penalty is better.
        int bestMaskPattern = -1;
        // We try all mask patterns to choose the best one.
        for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
            MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
            int penalty = calculateMaskPenalty(matrix);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                bestMaskPattern = maskPattern;
            }
        }
        return bestMaskPattern;
    }

    private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) throws WriterException {
        // In the following comments, we use numbers of Version 7-H.
        for (int versionNum = 1; versionNum <= 40; versionNum++) {
            Version version = Version.getVersionForNumber(versionNum);
            // numBytes = 196
            int numBytes = version.getTotalCodewords();
            // getNumECBytes = 130
            Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
            int numEcBytes = ecBlocks.getTotalECCodewords();
            // getNumDataBytes = 196 - 130 = 66
            int numDataBytes = numBytes - numEcBytes;
            int totalInputBytes = (numInputBits + 7) / 8;
            if (numDataBytes >= totalInputBytes) {
                return version;
            }
        }
        throw new WriterException("Data too big");
    }

    /**
     * Terminate bits as described in 8.4.8 and 8.4.9 of JISX0510:2004 (p.24).
     */
    static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
        int capacity = numDataBytes * 8;
        if (bits.getSize() > capacity) {
            throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " +
                    capacity);
        }
        for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
            bits.appendBit(false);
        }
        // Append termination bits. See 8.4.8 of JISX0510:2004 (p.24) for details.
        // If the last byte isn't 8-bit aligned, we'll add padding bits.
        int numBitsInLastByte = bits.getSize() & 0x07;
        if (numBitsInLastByte > 0) {
            for (int i = numBitsInLastByte; i < 8; i++) {
                bits.appendBit(false);
            }
        }
        // If we have more space, we'll fill the space with padding patterns defined in 8.4.9 (p.24).
        int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
        for (int i = 0; i < numPaddingBytes; ++i) {
            bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);
        }
        if (bits.getSize() != capacity) {
            throw new WriterException("Bits size does not equal capacity");
        }
    }

    /**
     * Get number of data bytes and number of error correction bytes for block id "blockID". Store
     * the result in "numDataBytesInBlock", and "numECBytesInBlock". See table 12 in 8.5.1 of
     * JISX0510:2004 (p.30)
     */
    static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes,
                                                       int numDataBytes,
                                                       int numRSBlocks,
                                                       int blockID,
                                                       int[] numDataBytesInBlock,
                                                       int[] numECBytesInBlock) throws WriterException {
        if (blockID >= numRSBlocks) {
            throw new WriterException("Block ID too large");
        }
        // numRsBlocksInGroup2 = 196 % 5 = 1
        int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
        // numRsBlocksInGroup1 = 5 - 1 = 4
        int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
        // numTotalBytesInGroup1 = 196 / 5 = 39
        int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
        // numTotalBytesInGroup2 = 39 + 1 = 40
        int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
        // numDataBytesInGroup1 = 66 / 5 = 13
        int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
        // numDataBytesInGroup2 = 13 + 1 = 14
        int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
        // numEcBytesInGroup1 = 39 - 13 = 26
        int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
        // numEcBytesInGroup2 = 40 - 14 = 26
        int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
        // Sanity checks.
        // 26 = 26
        if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new WriterException("EC bytes mismatch");
        }
        // 5 = 4 + 1.
        if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
            throw new WriterException("RS blocks mismatch");
        }
        // 196 = (13 + 26) * 4 + (14 + 26) * 1
        if (numTotalBytes !=
                ((numDataBytesInGroup1 + numEcBytesInGroup1) *
                        numRsBlocksInGroup1) +
                        ((numDataBytesInGroup2 + numEcBytesInGroup2) *
                                numRsBlocksInGroup2)) {
            throw new WriterException("Total bytes mismatch");
        }

        if (blockID < numRsBlocksInGroup1) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
        } else {
            numDataBytesInBlock[0] = numDataBytesInGroup2;
            numECBytesInBlock[0] = numEcBytesInGroup2;
        }
    }

    /**
     * Interleave "bits" with corresponding error correction bytes. On success, store the result in
     * "result". The interleave rule is complicated. See 8.6 of JISX0510:2004 (p.37) for details.
     */
    static BitArray interleaveWithECBytes(BitArray bits,
                                          int numTotalBytes,
                                          int numDataBytes,
                                          int numRSBlocks) throws WriterException {

        // "bits" must have "getNumDataBytes" bytes of data.
        if (bits.getSizeInBytes() != numDataBytes) {
            throw new WriterException("Number of bits and data bytes does not match");
        }

        // Step 1.  Divide data bytes into blocks and generate error correction bytes for them. We'll
        // store the divided data bytes blocks and error correction bytes blocks into "blocks".
        int dataBytesOffset = 0;
        int maxNumDataBytes = 0;
        int maxNumEcBytes = 0;

        // Since, we know the number of reedsolmon blocks, we can initialize the vector with the number.
        Collection<BlockPair> blocks = new ArrayList<>(numRSBlocks);

        for (int i = 0; i < numRSBlocks; ++i) {
            int[] numDataBytesInBlock = new int[1];
            int[] numEcBytesInBlock = new int[1];
            getNumDataBytesAndNumECBytesForBlockID(
                    numTotalBytes, numDataBytes, numRSBlocks, i,
                    numDataBytesInBlock, numEcBytesInBlock);

            int size = numDataBytesInBlock[0];
            byte[] dataBytes = new byte[size];
            bits.toBytes(8*dataBytesOffset, dataBytes, 0, size);
            byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
            blocks.add(new BlockPair(dataBytes, ecBytes));

            maxNumDataBytes = Math.max(maxNumDataBytes, size);
            maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
            dataBytesOffset += numDataBytesInBlock[0];
        }
        if (numDataBytes != dataBytesOffset) {
            throw new WriterException("Data bytes does not match offset");
        }

        BitArray result = new BitArray();

        // First, place data blocks.
        for (int i = 0; i < maxNumDataBytes; ++i) {
            for (BlockPair block : blocks) {
                byte[] dataBytes = block.getDataBytes();
                if (i < dataBytes.length) {
                    result.appendBits(dataBytes[i], 8);
                }
            }
        }
        // Then, place error correction blocks.
        for (int i = 0; i < maxNumEcBytes; ++i) {
            for (BlockPair block : blocks) {
                byte[] ecBytes = block.getErrorCorrectionBytes();
                if (i < ecBytes.length) {
                    result.appendBits(ecBytes[i], 8);
                }
            }
        }
        if (numTotalBytes != result.getSizeInBytes()) {  // Should be same.
            throw new WriterException("Interleaving error: " + numTotalBytes + " and " +
                    result.getSizeInBytes() + " differ.");
        }

        return result;
    }

    static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
        int numDataBytes = dataBytes.length;
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for (int i = 0; i < numDataBytes; i++) {
            toEncode[i] = dataBytes[i] & 0xFF;
        }
        new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

        byte[] ecBytes = new byte[numEcBytesInBlock];
        for (int i = 0; i < numEcBytesInBlock; i++) {
            ecBytes[i] = (byte) toEncode[numDataBytes + i];
        }
        return ecBytes;
    }

    /**
     * Append mode info. On success, store the result in "bits".
     */
    static void appendModeInfo(Mode mode, BitArray bits) {
        bits.appendBits(mode.getBits(), 4);
    }


    /**
     * Append length info. On success, store the result in "bits".
     */
    static void appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) throws WriterException {
        int numBits = mode.getCharacterCountBits(version);
        if (numLetters >= (1 << numBits)) {
            throw new WriterException(numLetters + " is bigger than " + ((1 << numBits) - 1));
        }
        bits.appendBits(numLetters, numBits);
    }

    /**
     * Append "bytes" in "mode" mode (encoding) into "bits". On success, store the result in "bits".
     */
    static void appendBytes(String content,
                            Mode mode,
                            BitArray bits,
                            String encoding) throws WriterException {
        switch (mode) {
            case NUMERIC:
                appendNumericBytes(content, bits);
                break;
            case ALPHANUMERIC:
                appendAlphanumericBytes(content, bits);
                break;
            case BYTE:
                append8BitBytes(content, bits, encoding);
                break;
            case KANJI:
                appendKanjiBytes(content, bits);
                break;
            default:
                throw new WriterException("Invalid mode: " + mode);
        }
    }

    static void appendNumericBytes(CharSequence content, BitArray bits) {
        int length = content.length();
        int i = 0;
        while (i < length) {
            int num1 = content.charAt(i) - '0';
            if (i + 2 < length) {
                // Encode three numeric letters in ten bits.
                int num2 = content.charAt(i + 1) - '0';
                int num3 = content.charAt(i + 2) - '0';
                bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
                i += 3;
            } else if (i + 1 < length) {
                // Encode two numeric letters in seven bits.
                int num2 = content.charAt(i + 1) - '0';
                bits.appendBits(num1 * 10 + num2, 7);
                i += 2;
            } else {
                // Encode one numeric letter in four bits.
                bits.appendBits(num1, 4);
                i++;
            }
        }
    }

    static void appendAlphanumericBytes(CharSequence content, BitArray bits) throws WriterException {
        int length = content.length();
        int i = 0;
        while (i < length) {
            int code1 = getAlphanumericCode(content.charAt(i));
            if (code1 == -1) {
                throw new WriterException();
            }
            if (i + 1 < length) {
                int code2 = getAlphanumericCode(content.charAt(i + 1));
                if (code2 == -1) {
                    throw new WriterException();
                }
                // Encode two alphanumeric letters in 11 bits.
                bits.appendBits(code1 * 45 + code2, 11);
                i += 2;
            } else {
                // Encode one alphanumeric letter in six bits.
                bits.appendBits(code1, 6);
                i++;
            }
        }
    }

    static void append8BitBytes(String content, BitArray bits, String encoding)
            throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes(encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new WriterException(uee);
        }
        for (byte b : bytes) {
            bits.appendBits(b, 8);
        }
    }

    static void appendKanjiBytes(String content, BitArray bits) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException uee) {
            throw new WriterException(uee);
        }
        int length = bytes.length;
        for (int i = 0; i < length; i += 2) {
            int byte1 = bytes[i] & 0xFF;
            int byte2 = bytes[i + 1] & 0xFF;
            int code = (byte1 << 8) | byte2;
            int subtracted = -1;
            if (code >= 0x8140 && code <= 0x9ffc) {
                subtracted = code - 0x8140;
            } else if (code >= 0xe040 && code <= 0xebbf) {
                subtracted = code - 0xc140;
            }
            if (subtracted == -1) {
                throw new WriterException("Invalid byte sequence");
            }
            int encoded = ((subtracted >> 8) * 0xc0) + (subtracted & 0xff);
            bits.appendBits(encoded, 13);
        }
    }

    private static void appendECI(CharacterSetECI eci, BitArray bits) {
        bits.appendBits(Mode.ECI.getBits(), 4);
        // This is correct for values up to 127, which is all we need now.
        bits.appendBits(eci.getValue(), 8);
    }

}
