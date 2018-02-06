package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>QR Codes can encode text as bits in one of several modes, and can use multiple modes
 * in one QR Code. This class decodes the bits back into text.</p>
 *
 * <p>See ISO 18004:2006, 6.4.3 - 6.4.7</p>
 *
 * @author Sean Owen
 */
final class DecodedBitStreamParser {

    /**
     * See ISO 18004:2006, 6.4.4 Table 5
     */
    private static final char[] ALPHANUMERIC_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            ' ', '$', '%', '*', '+', '-', '.', '/', ':'
    };
    private static final int GB2312_SUBSET = 1;

    private DecodedBitStreamParser() {
    }

    private enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    private enum Mode2 {
        TERMINATOR(new int[]{0, 0, 0}, 0x00), // Not really a mode...
        NUMERIC(new int[]{10, 12, 14}, 0x01),
        ALPHANUMERIC(new int[]{9, 11, 13}, 0x02),
        STRUCTURED_APPEND(new int[]{0, 0, 0}, 0x03), // Not supported
        BYTE(new int[]{8, 16, 16}, 0x04),
        ECI(new int[]{0, 0, 0}, 0x07), // character counts don't apply
        KANJI(new int[]{8, 10, 12}, 0x08),
        FNC1_FIRST_POSITION(new int[]{0, 0, 0}, 0x05),
        FNC1_SECOND_POSITION(new int[]{0, 0, 0}, 0x09),
        /** See GBT 18284-2000; "Hanzi" is a transliteration of this mode name. */
        HANZI(new int[]{8, 10, 12}, 0x0D);

        private final int[] characterCountBitsForVersions;
        private final int bits;

        Mode2(int[] characterCountBitsForVersions, int bits) {
            this.characterCountBitsForVersions = characterCountBitsForVersions;
            this.bits = bits;
        }

        /**
         * @param bits four bits encoding a QR Code data mode
         * @return Mode encoded by these bits
         * @throws IllegalArgumentException if bits do not correspond to a known mode
         */
        public static Mode2 forBits(int bits) {
            switch (bits) {
                case 0x0:
                    return TERMINATOR;
                case 0x1:
                    return NUMERIC;
                case 0x2:
                    return ALPHANUMERIC;
                case 0x3:
                    return STRUCTURED_APPEND;
                case 0x4:
                    return BYTE;
                case 0x5:
                    return FNC1_FIRST_POSITION;
                case 0x7:
                    return ECI;
                case 0x8:
                    return KANJI;
                case 0x9:
                    return FNC1_SECOND_POSITION;
                case 0xD:
                    // 0xD is defined in GBT 18284-2000, may not be supported in foreign country
                    return HANZI;
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * @param version version in question
         * @return number of bits used, in this QR Code symbol {@link Version}, to encode the
         *         count of characters that will follow encoded in this Mode
         */
        public int getCharacterCountBits(Version version) {
            int number = version.getVersionNumber();
            int offset;
            if (number <= 9) {
                offset = 0;
            } else if (number <= 26) {
                offset = 1;
            } else {
                offset = 2;
            }
            return characterCountBitsForVersions[offset];
        }

        public int getBits() {
            return bits;
        }

    }





    private static final int TEXT_COMPACTION_MODE_LATCH = 900;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final int ECI_USER_DEFINED = 925;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_CHARSET = 927;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int MAX_NUMERIC_CODEWORDS = 15;

    private static final int PL = 25;
    private static final int LL = 27;
    private static final int AS = 27;
    private static final int ML = 28;
    private static final int AL = 28;
    private static final int PS = 29;
    private static final int PAL = 29;

    private static final char[] PUNCT_CHARS = {
            ';', '<', '>', '@', '[', '\\', ']', '_', '`', '~', '!',
            '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '"', '|', '*',
            '(', ')', '?', '{', '}', '\''};

    private static final char[] MIXED_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&',
            '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*',
            '=', '^'};

    private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");

    /**
     * Text Compaction mode (see 5.4.1.5) permits all printable ASCII characters to be
     * encoded, i.e. values 32 - 126 inclusive in accordance with ISO/IEC 646 (IRV), as
     * well as selected control characters.
     *
     * @param codewords The array of codewords (data + error)
     * @param codeIndex The current index into the codeword array.
     * @param result    The decoded data is appended to the result.
     * @return The next index into the codeword array.
     */
    private static int textCompaction(int[] codewords, int codeIndex, StringBuilder result) {
        // 2 character per codeword
        int[] textCompactionData = new int[(codewords[0] - codeIndex) * 2];
        // Used to hold the byte compaction value if there is a mode shift
        int[] byteCompactionData = new int[(codewords[0] - codeIndex) * 2];

        int index = 0;
        boolean end = false;
        while ((codeIndex < codewords[0]) && !end) {
            int code = codewords[codeIndex++];
            if (code < TEXT_COMPACTION_MODE_LATCH) {
                textCompactionData[index] = code / 30;
                textCompactionData[index + 1] = code % 30;
                index += 2;
            } else {
                switch (code) {
                    case TEXT_COMPACTION_MODE_LATCH:
                        // reinitialize text compaction mode to alpha sub mode
                        textCompactionData[index++] = TEXT_COMPACTION_MODE_LATCH;
                        break;
                    case BYTE_COMPACTION_MODE_LATCH:
                    case BYTE_COMPACTION_MODE_LATCH_6:
                    case NUMERIC_COMPACTION_MODE_LATCH:
                    case BEGIN_MACRO_PDF417_CONTROL_BLOCK:
                    case BEGIN_MACRO_PDF417_OPTIONAL_FIELD:
                    case MACRO_PDF417_TERMINATOR:
                        codeIndex--;
                        end = true;
                        break;
                    case MODE_SHIFT_TO_BYTE_COMPACTION_MODE:
                        // The Mode Shift codeword 913 shall cause a temporary
                        // switch from Text Compaction mode to Byte Compaction mode.
                        // This switch shall be in effect for only the next codeword,
                        // after which the mode shall revert to the prevailing sub-mode
                        // of the Text Compaction mode. Codeword 913 is only available
                        // in Text Compaction mode; its use is described in 5.4.2.4.
                        textCompactionData[index] = MODE_SHIFT_TO_BYTE_COMPACTION_MODE;
                        code = codewords[codeIndex++];
                        byteCompactionData[index] = code;
                        index++;
                        break;
                }
            }
        }
        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return codeIndex;
    }

    /**
     * The Text Compaction mode includes all the printable ASCII characters
     * (i.e. values from 32 to 126) and three ASCII control characters: HT or tab
     * (ASCII value 9), LF or line feed (ASCII value 10), and CR or carriage
     * return (ASCII value 13). The Text Compaction mode also includes various latch
     * and shift characters which are used exclusively within the mode. The Text
     * Compaction mode encodes up to 2 characters per codeword. The compaction rules
     * for converting data into PDF417 codewords are defined in 5.4.2.2. The sub-mode
     * switches are defined in 5.4.2.3.
     *
     * @param textCompactionData The text compaction data.
     * @param byteCompactionData The byte compaction data if there
     *                           was a mode shift.
     * @param length             The size of the text compaction and byte compaction data.
     * @param result             The decoded data is appended to the result.
     */
    private static void decodeTextCompaction(int[] textCompactionData,
                                             int[] byteCompactionData,
                                             int length,
                                             StringBuilder result) {
        // Beginning from an initial state of the Alpha sub-mode
        // The default compaction mode for PDF417 in effect at the start of each symbol shall always be Text
        // Compaction mode Alpha sub-mode (uppercase alphabetic). A latch codeword from another mode to the Text
        // Compaction mode shall always switch to the Text Compaction Alpha sub-mode.
        Mode subMode = Mode.ALPHA;
        Mode priorToShiftMode = Mode.ALPHA;
        int i = 0;
        while (i < length) {
            int subModeCh = textCompactionData[i];
            char ch = 0;
            switch (subMode) {
                case ALPHA:
                    // Alpha (uppercase alphabetic)
                    if (subModeCh < 26) {
                        // Upper case Alpha Character
                        ch = (char) ('A' + subModeCh);
                    } else {
                        if (subModeCh == 26) {
                            ch = ' ';
                        } else if (subModeCh == LL) {
                            subMode = Mode.LOWER;
                        } else if (subModeCh == ML) {
                            subMode = Mode.MIXED;
                        } else if (subModeCh == PS) {
                            // Shift to punctuation
                            priorToShiftMode = subMode;
                            subMode = Mode.PUNCT_SHIFT;
                        } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                            result.append((char) byteCompactionData[i]);
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;

                case LOWER:
                    // Lower (lowercase alphabetic)
                    if (subModeCh < 26) {
                        ch = (char) ('a' + subModeCh);
                    } else {
                        if (subModeCh == 26) {
                            ch = ' ';
                        } else if (subModeCh == AS) {
                            // Shift to alpha
                            priorToShiftMode = subMode;
                            subMode = Mode.ALPHA_SHIFT;
                        } else if (subModeCh == ML) {
                            subMode = Mode.MIXED;
                        } else if (subModeCh == PS) {
                            // Shift to punctuation
                            priorToShiftMode = subMode;
                            subMode = Mode.PUNCT_SHIFT;
                        } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                            // TODO Does this need to use the current character encoding? See other occurrences below
                            result.append((char) byteCompactionData[i]);
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;

                case MIXED:
                    // Mixed (numeric and some punctuation)
                    if (subModeCh < PL) {
                        ch = MIXED_CHARS[subModeCh];
                    } else {
                        if (subModeCh == PL) {
                            subMode = Mode.PUNCT;
                        } else if (subModeCh == 26) {
                            ch = ' ';
                        } else if (subModeCh == LL) {
                            subMode = Mode.LOWER;
                        } else if (subModeCh == AL) {
                            subMode = Mode.ALPHA;
                        } else if (subModeCh == PS) {
                            // Shift to punctuation
                            priorToShiftMode = subMode;
                            subMode = Mode.PUNCT_SHIFT;
                        } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                            result.append((char) byteCompactionData[i]);
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;

                case PUNCT:
                    // Punctuation
                    if (subModeCh < PAL) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else {
                        if (subModeCh == PAL) {
                            subMode = Mode.ALPHA;
                        } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                            result.append((char) byteCompactionData[i]);
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;

                case ALPHA_SHIFT:
                    // Restore sub-mode
                    subMode = priorToShiftMode;
                    if (subModeCh < 26) {
                        ch = (char) ('A' + subModeCh);
                    } else {
                        if (subModeCh == 26) {
                            ch = ' ';
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;

                case PUNCT_SHIFT:
                    // Restore sub-mode
                    subMode = priorToShiftMode;
                    if (subModeCh < PAL) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else {
                        if (subModeCh == PAL) {
                            subMode = Mode.ALPHA;
                        } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                            // PS before Shift-to-Byte is used as a padding character,
                            // see 5.4.2.4 of the specification
                            result.append((char) byteCompactionData[i]);
                        } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                            subMode = Mode.ALPHA;
                        }
                    }
                    break;
            }
            if (ch != 0) {
                // Append decoded character to result
                result.append(ch);
            }
            i++;
        }
    }

    /**
     * Byte Compaction mode (see 5.4.3) permits all 256 possible 8-bit byte values to be encoded.
     * This includes all ASCII characters value 0 to 127 inclusive and provides for international
     * character set support.
     *
     * @param mode      The byte compaction mode i.e. 901 or 924
     * @param codewords The array of codewords (data + error)
     * @param encoding  Currently active character encoding
     * @param codeIndex The current index into the codeword array.
     * @param result    The decoded data is appended to the result.
     * @return The next index into the codeword array.
     */
    private static int byteCompaction(int mode,
                                      int[] codewords,
                                      Charset encoding,
                                      int codeIndex,
                                      StringBuilder result) {
        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        if (mode == BYTE_COMPACTION_MODE_LATCH) {
            // Total number of Byte Compaction characters to be encoded
            // is not a multiple of 6
            int count = 0;
            long value = 0;
            int[] byteCompactedCodewords = new int[6];
            boolean end = false;
            int nextCode = codewords[codeIndex++];
            while ((codeIndex < codewords[0]) && !end) {
                byteCompactedCodewords[count++] = nextCode;
                // Base 900
                value = 900 * value + nextCode;
                nextCode = codewords[codeIndex++];
                // perhaps it should be ok to check only nextCode >= TEXT_COMPACTION_MODE_LATCH
                if (nextCode == TEXT_COMPACTION_MODE_LATCH ||
                        nextCode == BYTE_COMPACTION_MODE_LATCH ||
                        nextCode == NUMERIC_COMPACTION_MODE_LATCH ||
                        nextCode == BYTE_COMPACTION_MODE_LATCH_6 ||
                        nextCode == BEGIN_MACRO_PDF417_CONTROL_BLOCK ||
                        nextCode == BEGIN_MACRO_PDF417_OPTIONAL_FIELD ||
                        nextCode == MACRO_PDF417_TERMINATOR) {
                    codeIndex--;
                    end = true;
                } else {
                    if ((count % 5 == 0) && (count > 0)) {
                        // Decode every 5 codewords
                        // Convert to Base 256
                        for (int j = 0; j < 6; ++j) {
                            decodedBytes.write((byte) (value >> (8 * (5 - j))));
                        }
                        value = 0;
                        count = 0;
                    }
                }
            }

            // if the end of all codewords is reached the last codeword needs to be added
            if (codeIndex == codewords[0] && nextCode < TEXT_COMPACTION_MODE_LATCH) {
                byteCompactedCodewords[count++] = nextCode;
            }

            // If Byte Compaction mode is invoked with codeword 901,
            // the last group of codewords is interpreted directly
            // as one byte per codeword, without compaction.
            for (int i = 0; i < count; i++) {
                decodedBytes.write((byte) byteCompactedCodewords[i]);
            }

        } else if (mode == BYTE_COMPACTION_MODE_LATCH_6) {
            // Total number of Byte Compaction characters to be encoded
            // is an integer multiple of 6
            int count = 0;
            long value = 0;
            boolean end = false;
            while (codeIndex < codewords[0] && !end) {
                int code = codewords[codeIndex++];
                if (code < TEXT_COMPACTION_MODE_LATCH) {
                    count++;
                    // Base 900
                    value = 900 * value + code;
                } else {
                    if (code == TEXT_COMPACTION_MODE_LATCH ||
                            code == BYTE_COMPACTION_MODE_LATCH ||
                            code == NUMERIC_COMPACTION_MODE_LATCH ||
                            code == BYTE_COMPACTION_MODE_LATCH_6 ||
                            code == BEGIN_MACRO_PDF417_CONTROL_BLOCK ||
                            code == BEGIN_MACRO_PDF417_OPTIONAL_FIELD ||
                            code == MACRO_PDF417_TERMINATOR) {
                        codeIndex--;
                        end = true;
                    }
                }
                if ((count % 5 == 0) && (count > 0)) {
                    // Decode every 5 codewords
                    // Convert to Base 256
                    for (int j = 0; j < 6; ++j) {
                        decodedBytes.write((byte) (value >> (8 * (5 - j))));
                    }
                    value = 0;
                    count = 0;
                }
            }
        }
        result.append(new String(decodedBytes.toByteArray(), encoding));
        return codeIndex;
    }

    /**
     * Numeric Compaction mode (see 5.4.4) permits efficient encoding of numeric data strings.
     *
     * @param codewords The array of codewords (data + error)
     * @param codeIndex The current index into the codeword array.
     * @param result    The decoded data is appended to the result.
     * @return The next index into the codeword array.
     */
    private static int numericCompaction(int[] codewords, int codeIndex, StringBuilder result) throws FormatException {
        int count = 0;
        boolean end = false;

        int[] numericCodewords = new int[MAX_NUMERIC_CODEWORDS];

        while (codeIndex < codewords[0] && !end) {
            int code = codewords[codeIndex++];
            if (codeIndex == codewords[0]) {
                end = true;
            }
            if (code < TEXT_COMPACTION_MODE_LATCH) {
                numericCodewords[count] = code;
                count++;
            } else {
                if (code == TEXT_COMPACTION_MODE_LATCH ||
                        code == BYTE_COMPACTION_MODE_LATCH ||
                        code == BYTE_COMPACTION_MODE_LATCH_6 ||
                        code == BEGIN_MACRO_PDF417_CONTROL_BLOCK ||
                        code == BEGIN_MACRO_PDF417_OPTIONAL_FIELD ||
                        code == MACRO_PDF417_TERMINATOR) {
                    codeIndex--;
                    end = true;
                }
            }
            if (count % MAX_NUMERIC_CODEWORDS == 0 ||
                    code == NUMERIC_COMPACTION_MODE_LATCH ||
                    end) {
                // Re-invoking Numeric Compaction mode (by using codeword 902
                // while in Numeric Compaction mode) serves  to terminate the
                // current Numeric Compaction mode grouping as described in 5.4.4.2,
                // and then to start a new one grouping.
                if (count > 0) {
                    String s = decodeBase900toBase10(numericCodewords, count);
                    result.append(s);
                    count = 0;
                }
            }
        }
        return codeIndex;
    }

    /**
     * Table containing values for the exponent of 900.
     * This is used in the numeric compaction decode algorithm.
     */
    private static final BigInteger[] EXP900;
    static {
        EXP900 = new BigInteger[16];
        EXP900[0] = BigInteger.ONE;
        BigInteger nineHundred = BigInteger.valueOf(900);
        EXP900[1] = nineHundred;
        for (int i = 2; i < EXP900.length; i++) {
            EXP900[i] = EXP900[i - 1].multiply(nineHundred);
        }
    }


    private static String decodeBase900toBase10(int[] codewords, int count) throws FormatException {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < count; i++) {
            result = result.add(EXP900[count - i - 1].multiply(BigInteger.valueOf(codewords[i])));
        }
        String resultString = result.toString();
        if (resultString.charAt(0) != '1') {
            throw FormatException.getFormatInstance();
        }
        return resultString.substring(1);
    }

    static DecoderResult decode(int[] codewords, String ecLevel) throws FormatException {
        StringBuilder result = new StringBuilder(codewords.length * 2);
        Charset encoding = DEFAULT_ENCODING;
        // Get compaction mode
        int codeIndex = 1;
        int code = codewords[codeIndex++];
        PDF417ResultMetadata resultMetadata = new PDF417ResultMetadata();
        while (codeIndex < codewords[0]) {
            switch (code) {
                case TEXT_COMPACTION_MODE_LATCH:
                    codeIndex = textCompaction(codewords, codeIndex, result);
                    break;
                case BYTE_COMPACTION_MODE_LATCH:
                case BYTE_COMPACTION_MODE_LATCH_6:
                    codeIndex = byteCompaction(code, codewords, encoding, codeIndex, result);
                    break;
                case MODE_SHIFT_TO_BYTE_COMPACTION_MODE:
                    result.append((char) codewords[codeIndex++]);
                    break;
                case NUMERIC_COMPACTION_MODE_LATCH:
                    codeIndex = numericCompaction(codewords, codeIndex, result);
                    break;
                case ECI_CHARSET:
                    CharacterSetECI charsetECI =
                            CharacterSetECI.getCharacterSetECIByValue(codewords[codeIndex++]);
                    encoding = Charset.forName(charsetECI.name());
                    break;
                case ECI_GENERAL_PURPOSE:
                    // Can't do anything with generic ECI; skip its 2 characters
                    codeIndex += 2;
                    break;
                case ECI_USER_DEFINED:
                    // Can't do anything with user ECI; skip its 1 character
                    codeIndex ++;
                    break;
                case BEGIN_MACRO_PDF417_CONTROL_BLOCK:
                    codeIndex = decodeMacroBlock(codewords, codeIndex, resultMetadata);
                    break;
                case BEGIN_MACRO_PDF417_OPTIONAL_FIELD:
                case MACRO_PDF417_TERMINATOR:
                    // Should not see these outside a macro block
                    throw FormatException.getFormatInstance();
                default:
                    // Default to text compaction. During testing numerous barcodes
                    // appeared to be missing the starting mode. In these cases defaulting
                    // to text compaction seems to work.
                    codeIndex--;
                    codeIndex = textCompaction(codewords, codeIndex, result);
                    break;
            }
            if (codeIndex < codewords.length) {
                code = codewords[codeIndex++];
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (result.length() == 0) {
            throw FormatException.getFormatInstance();
        }
        DecoderResult decoderResult = new DecoderResult(null, result.toString(), null, ecLevel);
        decoderResult.setOther(resultMetadata);
        return decoderResult;
    }

    private static final int NUMBER_OF_SEQUENCE_CODEWORDS = 2;

    private static int decodeMacroBlock(int[] codewords, int codeIndex, PDF417ResultMetadata resultMetadata)
            throws FormatException {
        if (codeIndex + NUMBER_OF_SEQUENCE_CODEWORDS > codewords[0]) {
            // we must have at least two bytes left for the segment index
            throw FormatException.getFormatInstance();
        }
        int[] segmentIndexArray = new int[NUMBER_OF_SEQUENCE_CODEWORDS];
        for (int i = 0; i < NUMBER_OF_SEQUENCE_CODEWORDS; i++, codeIndex++) {
            segmentIndexArray[i] = codewords[codeIndex];
        }
        resultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(segmentIndexArray,
                NUMBER_OF_SEQUENCE_CODEWORDS)));

        StringBuilder fileId = new StringBuilder();
        codeIndex = textCompaction(codewords, codeIndex, fileId);
        resultMetadata.setFileId(fileId.toString());

        if (codewords[codeIndex] == BEGIN_MACRO_PDF417_OPTIONAL_FIELD) {
            codeIndex++;
            int[] additionalOptionCodeWords = new int[codewords[0] - codeIndex];
            int additionalOptionCodeWordsIndex = 0;

            boolean end = false;
            while ((codeIndex < codewords[0]) && !end) {
                int code = codewords[codeIndex++];
                if (code < TEXT_COMPACTION_MODE_LATCH) {
                    additionalOptionCodeWords[additionalOptionCodeWordsIndex++] = code;
                } else {
                    switch (code) {
                        case MACRO_PDF417_TERMINATOR:
                            resultMetadata.setLastSegment(true);
                            codeIndex++;
                            end = true;
                            break;
                        default:
                            throw FormatException.getFormatInstance();
                    }
                }
            }

            resultMetadata.setOptionalData(Arrays.copyOf(additionalOptionCodeWords, additionalOptionCodeWordsIndex));
        } else if (codewords[codeIndex] == MACRO_PDF417_TERMINATOR) {
            resultMetadata.setLastSegment(true);
            codeIndex++;
        }

        return codeIndex;
    }

    static DecoderResult decode(byte[] bytes,
                                Version version,
                                ErrorCorrectionLevel ecLevel,
                                Map<DecodeHintType,?> hints) throws FormatException {
        BitSource bits = new BitSource(bytes);
        StringBuilder result = new StringBuilder(50);
        List<byte[]> byteSegments = new ArrayList<>(1);
        int symbolSequence = -1;
        int parityData = -1;

        try {
            CharacterSetECI currentCharacterSetECI = null;
            boolean fc1InEffect = false;
            Mode2 mode;
            do {
                // While still another segment to read...
                if (bits.available() < 4) {
                    // OK, assume we're done. Really, a TERMINATOR mode should have been recorded here
                    mode = Mode2.TERMINATOR;
                } else {
                    mode = Mode2.forBits(bits.readBits(4)); // mode is encoded by 4 bits
                }
                if (mode != Mode2.TERMINATOR) {
                    if (mode == Mode2.FNC1_FIRST_POSITION || mode == Mode2.FNC1_SECOND_POSITION) {
                        // We do little with FNC1 except alter the parsed result a bit according to the spec
                        fc1InEffect = true;
                    } else if (mode == Mode2.STRUCTURED_APPEND) {
                        if (bits.available() < 16) {
                            throw FormatException.getFormatInstance();
                        }
                        // sequence number and parity is added later to the result metadata
                        // Read next 8 bits (symbol sequence #) and 8 bits (parity data), then continue
                        symbolSequence = bits.readBits(8);
                        parityData = bits.readBits(8);
                    } else if (mode == Mode2.ECI) {
                        // Count doesn't apply to ECI
                        int value = parseECIValue(bits);
                        currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(value);
                        if (currentCharacterSetECI == null) {
                            throw FormatException.getFormatInstance();
                        }
                    } else {
                        // First handle Hanzi mode which does not start with character count
                        if (mode == Mode2.HANZI) {
                            //chinese mode contains a sub set indicator right after mode indicator
                            int subset = bits.readBits(4);
                            int countHanzi = bits.readBits(mode.getCharacterCountBits(version));
                            if (subset == GB2312_SUBSET) {
                                decodeHanziSegment(bits, result, countHanzi);
                            }
                        } else {
                            // "Normal" QR code modes:
                            // How many characters will follow, encoded in this mode?
                            int count = bits.readBits(mode.getCharacterCountBits(version));
                            if (mode == Mode2.NUMERIC) {
                                decodeNumericSegment(bits, result, count);
                            } else if (mode == Mode2.ALPHANUMERIC) {
                                decodeAlphanumericSegment(bits, result, count, fc1InEffect);
                            } else if (mode == Mode2.BYTE) {
                                decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments, hints);
                            } else if (mode == Mode2.KANJI) {
                                decodeKanjiSegment(bits, result, count);
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                        }
                    }
                }
            } while (mode != Mode2.TERMINATOR);
        } catch (IllegalArgumentException iae) {
            // from readBits() calls
            throw FormatException.getFormatInstance();
        }

        return new DecoderResult(bytes,
                result.toString(),
                byteSegments.isEmpty() ? null : byteSegments,
                ecLevel == null ? null : ecLevel.toString(),
                symbolSequence,
                parityData);
    }

    /**
     * See specification GBT 18284-2000
     */
    private static void decodeHanziSegment(BitSource bits,
                                           StringBuilder result,
                                           int count) throws FormatException {
        // Don't crash trying to read more bits than we have available.
        if (count * 13 > bits.available()) {
            throw FormatException.getFormatInstance();
        }

        // Each character will require 2 bytes. Read the characters as 2-byte pairs
        // and decode as GB2312 afterwards
        byte[] buffer = new byte[2 * count];
        int offset = 0;
        while (count > 0) {
            // Each 13 bits encodes a 2-byte character
            int twoBytes = bits.readBits(13);
            int assembledTwoBytes = ((twoBytes / 0x060) << 8) | (twoBytes % 0x060);
            if (assembledTwoBytes < 0x003BF) {
                // In the 0xA1A1 to 0xAAFE range
                assembledTwoBytes += 0x0A1A1;
            } else {
                // In the 0xB0A1 to 0xFAFE range
                assembledTwoBytes += 0x0A6A1;
            }
            buffer[offset] = (byte) ((assembledTwoBytes >> 8) & 0xFF);
            buffer[offset + 1] = (byte) (assembledTwoBytes & 0xFF);
            offset += 2;
            count--;
        }

        try {
            result.append(new String(buffer, StringUtils.GB2312));
        } catch (UnsupportedEncodingException ignored) {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeKanjiSegment(BitSource bits,
                                           StringBuilder result,
                                           int count) throws FormatException {
        // Don't crash trying to read more bits than we have available.
        if (count * 13 > bits.available()) {
            throw FormatException.getFormatInstance();
        }

        // Each character will require 2 bytes. Read the characters as 2-byte pairs
        // and decode as Shift_JIS afterwards
        byte[] buffer = new byte[2 * count];
        int offset = 0;
        while (count > 0) {
            // Each 13 bits encodes a 2-byte character
            int twoBytes = bits.readBits(13);
            int assembledTwoBytes = ((twoBytes / 0x0C0) << 8) | (twoBytes % 0x0C0);
            if (assembledTwoBytes < 0x01F00) {
                // In the 0x8140 to 0x9FFC range
                assembledTwoBytes += 0x08140;
            } else {
                // In the 0xE040 to 0xEBBF range
                assembledTwoBytes += 0x0C140;
            }
            buffer[offset] = (byte) (assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte) assembledTwoBytes;
            offset += 2;
            count--;
        }
        // Shift_JIS may not be supported in some environments:
        try {
            result.append(new String(buffer, StringUtils.SHIFT_JIS));
        } catch (UnsupportedEncodingException ignored) {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeByteSegment(BitSource bits,
                                          StringBuilder result,
                                          int count,
                                          CharacterSetECI currentCharacterSetECI,
                                          Collection<byte[]> byteSegments,
                                          Map<DecodeHintType,?> hints) throws FormatException {
        // Don't crash trying to read more bits than we have available.
        if (8 * count > bits.available()) {
            throw FormatException.getFormatInstance();
        }

        byte[] readBytes = new byte[count];
        for (int i = 0; i < count; i++) {
            readBytes[i] = (byte) bits.readBits(8);
        }
        String encoding;
        if (currentCharacterSetECI == null) {
            // The spec isn't clear on this mode; see
            // section 6.4.5: t does not say which encoding to assuming
            // upon decoding. I have seen ISO-8859-1 used as well as
            // Shift_JIS -- without anything like an ECI designator to
            // give a hint.
            encoding = StringUtils.guessEncoding(readBytes, hints);
        } else {
            encoding = currentCharacterSetECI.name();
        }
        try {
            result.append(new String(readBytes, encoding));
        } catch (UnsupportedEncodingException ignored) {
            throw FormatException.getFormatInstance();
        }
        byteSegments.add(readBytes);
    }

    private static char toAlphaNumericChar(int value) throws FormatException {
        if (value >= ALPHANUMERIC_CHARS.length) {
            throw FormatException.getFormatInstance();
        }
        return ALPHANUMERIC_CHARS[value];
    }

    private static void decodeAlphanumericSegment(BitSource bits,
                                                  StringBuilder result,
                                                  int count,
                                                  boolean fc1InEffect) throws FormatException {
        // Read two characters at a time
        int start = result.length();
        while (count > 1) {
            if (bits.available() < 11) {
                throw FormatException.getFormatInstance();
            }
            int nextTwoCharsBits = bits.readBits(11);
            result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
            result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
            count -= 2;
        }
        if (count == 1) {
            // special case: one character left
            if (bits.available() < 6) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(bits.readBits(6)));
        }
        // See section 6.4.8.1, 6.4.8.2
        if (fc1InEffect) {
            // We need to massage the result a bit if in an FNC1 mode:
            for (int i = start; i < result.length(); i++) {
                if (result.charAt(i) == '%') {
                    if (i < result.length() - 1 && result.charAt(i + 1) == '%') {
                        // %% is rendered as %
                        result.deleteCharAt(i + 1);
                    } else {
                        // In alpha mode, % should be converted to FNC1 separator 0x1D
                        result.setCharAt(i, (char) 0x1D);
                    }
                }
            }
        }
    }

    private static void decodeNumericSegment(BitSource bits,
                                             StringBuilder result,
                                             int count) throws FormatException {
        // Read three digits at a time
        while (count >= 3) {
            // Each 10 bits encodes three digits
            if (bits.available() < 10) {
                throw FormatException.getFormatInstance();
            }
            int threeDigitsBits = bits.readBits(10);
            if (threeDigitsBits >= 1000) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(threeDigitsBits / 100));
            result.append(toAlphaNumericChar((threeDigitsBits / 10) % 10));
            result.append(toAlphaNumericChar(threeDigitsBits % 10));
            count -= 3;
        }
        if (count == 2) {
            // Two digits left over to read, encoded in 7 bits
            if (bits.available() < 7) {
                throw FormatException.getFormatInstance();
            }
            int twoDigitsBits = bits.readBits(7);
            if (twoDigitsBits >= 100) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(twoDigitsBits / 10));
            result.append(toAlphaNumericChar(twoDigitsBits % 10));
        } else if (count == 1) {
            // One digit left over to read
            if (bits.available() < 4) {
                throw FormatException.getFormatInstance();
            }
            int digitBits = bits.readBits(4);
            if (digitBits >= 10) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(digitBits));
        }
    }

    private static int parseECIValue(BitSource bits) throws FormatException {
        int firstByte = bits.readBits(8);
        if ((firstByte & 0x80) == 0) {
            // just one byte
            return firstByte & 0x7F;
        }
        if ((firstByte & 0xC0) == 0x80) {
            // two bytes
            int secondByte = bits.readBits(8);
            return ((firstByte & 0x3F) << 8) | secondByte;
        }
        if ((firstByte & 0xE0) == 0xC0) {
            // three bytes
            int secondThirdBytes = bits.readBits(16);
            return ((firstByte & 0x1F) << 16) | secondThirdBytes;
        }
        throw FormatException.getFormatInstance();
    }

}
