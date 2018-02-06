package com.ns.empaque.wmpempaque.zxing.integration.android;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * DataMatrix ECC 200 data encoder following the algorithm described in ISO/IEC 16022:200(E) in
 * annex S.
 */
public final class HighLevelEncoder {

    static final String[] MODE_NAMES = {"UPPER", "LOWER", "DIGIT", "MIXED", "PUNCT"};

    static final int MODE_UPPER = 0; // 5 bits
    static final int MODE_LOWER = 1; // 5 bits
    static final int MODE_DIGIT = 2; // 4 bits
    static final int MODE_MIXED = 3; // 5 bits
    static final int MODE_PUNCT = 4; // 5 bits

    // The Latch Table shows, for each pair of Modes, the optimal method for
    // getting from one mode to another.  In the worst possible case, this can
    // be up to 14 bits.  In the best possible case, we are already there!
    // The high half-word of each entry gives the number of bits.
    // The low half-word of each entry are the actual bits necessary to change
    static final int[][] LATCH_TABLE = {
            {
                    0,
                    (5 << 16) + 28,              // UPPER -> LOWER
                    (5 << 16) + 30,              // UPPER -> DIGIT
                    (5 << 16) + 29,              // UPPER -> MIXED
                    (10 << 16) + (29 << 5) + 30, // UPPER -> MIXED -> PUNCT
            },
            {
                    (9 << 16) + (30 << 4) + 14,  // LOWER -> DIGIT -> UPPER
                    0,
                    (5 << 16) + 30,              // LOWER -> DIGIT
                    (5 << 16) + 29,              // LOWER -> MIXED
                    (10 << 16) + (29 << 5) + 30, // LOWER -> MIXED -> PUNCT
            },
            {
                    (4 << 16) + 14,              // DIGIT -> UPPER
                    (9 << 16) + (14 << 5) + 28,  // DIGIT -> UPPER -> LOWER
                    0,
                    (9 << 16) + (14 << 5) + 29,  // DIGIT -> UPPER -> MIXED
                    (14 << 16) + (14 << 10) + (29 << 5) + 30,
                    // DIGIT -> UPPER -> MIXED -> PUNCT
            },
            {
                    (5 << 16) + 29,              // MIXED -> UPPER
                    (5 << 16) + 28,              // MIXED -> LOWER
                    (10 << 16) + (29 << 5) + 30, // MIXED -> UPPER -> DIGIT
                    0,
                    (5 << 16) + 30,              // MIXED -> PUNCT
            },
            {
                    (5 << 16) + 31,              // PUNCT -> UPPER
                    (10 << 16) + (31 << 5) + 28, // PUNCT -> UPPER -> LOWER
                    (10 << 16) + (31 << 5) + 30, // PUNCT -> UPPER -> DIGIT
                    (10 << 16) + (31 << 5) + 29, // PUNCT -> UPPER -> MIXED
                    0,
            },
    };

    // A reverse mapping from [mode][char] to the encoding for that character
    // in that mode.  An entry of 0 indicates no mapping exists.
    private static final int[][] CHAR_MAP = new int[5][256];
    static {
        CHAR_MAP[MODE_UPPER][' '] = 1;
        for (int c = 'A'; c <= 'Z'; c++) {
            CHAR_MAP[MODE_UPPER][c] = c - 'A' + 2;
        }
        CHAR_MAP[MODE_LOWER][' '] = 1;
        for (int c = 'a'; c <= 'z'; c++) {
            CHAR_MAP[MODE_LOWER][c] = c - 'a' + 2;
        }
        CHAR_MAP[MODE_DIGIT][' '] = 1;
        for (int c = '0'; c <= '9'; c++) {
            CHAR_MAP[MODE_DIGIT][c] = c - '0' + 2;
        }
        CHAR_MAP[MODE_DIGIT][','] = 12;
        CHAR_MAP[MODE_DIGIT]['.'] = 13;
        int[] mixedTable = {
                '\0', ' ', '\1', '\2', '\3', '\4', '\5', '\6', '\7', '\b', '\t', '\n',
                '\13', '\f', '\r', '\33', '\34', '\35', '\36', '\37', '@', '\\', '^',
                '_', '`', '|', '~', '\177'
        };
        for (int i = 0; i < mixedTable.length; i++) {
            CHAR_MAP[MODE_MIXED][mixedTable[i]] = i;
        }
        int[] punctTable = {
                '\0', '\r', '\0', '\0', '\0', '\0', '!', '\'', '#', '$', '%', '&', '\'',
                '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?',
                '[', ']', '{', '}'
        };
        for (int i = 0; i < punctTable.length; i++) {
            if (punctTable[i] > 0) {
                CHAR_MAP[MODE_PUNCT][punctTable[i]] = i;
            }
        }
    }

    // A map showing the available shift codes.  (The shifts to BINARY are not
    // shown
    static final int[][] SHIFT_TABLE = new int[6][6]; // mode shift codes, per table
    static {
        for (int[] table : SHIFT_TABLE) {
            Arrays.fill(table, -1);
        }
        SHIFT_TABLE[MODE_UPPER][MODE_PUNCT] = 0;

        SHIFT_TABLE[MODE_LOWER][MODE_PUNCT] = 0;
        SHIFT_TABLE[MODE_LOWER][MODE_UPPER] = 28;

        SHIFT_TABLE[MODE_MIXED][MODE_PUNCT] = 0;

        SHIFT_TABLE[MODE_DIGIT][MODE_PUNCT] = 0;
        SHIFT_TABLE[MODE_DIGIT][MODE_UPPER] = 15;
    }

    private  byte[] text = null;

    public HighLevelEncoder(byte[] text) {
        this.text = text;
    }

    /**
     * @return text represented by this encoder encoded as a {@link BitArray}
     */
    public BitArray encode() {
        Collection<State> states = Collections.singletonList(State.INITIAL_STATE);
        for (int index = 0; index < text.length; index++) {
            int pairCode;
            int nextChar = index + 1 < text.length ? text[index + 1] : 0;
            switch (text[index]) {
                case '\r':
                    pairCode = nextChar == '\n' ? 2 : 0;
                    break;
                case '.' :
                    pairCode = nextChar == ' '  ? 3 : 0;
                    break;
                case ',' :
                    pairCode = nextChar == ' ' ? 4 : 0;
                    break;
                case ':' :
                    pairCode = nextChar == ' ' ? 5 : 0;
                    break;
                default:
                    pairCode = 0;
            }
            if (pairCode > 0) {
                // We have one of the four special PUNCT pairs.  Treat them specially.
                // Get a new set of states for the two new characters.
                states = updateStateListForPair(states, index, pairCode);
                index++;
            } else {
                // Get a new set of states for the new character.
                states = updateStateListForChar(states, index);
            }
        }
        // We are left with a set of states.  Find the shortest one.
        State minState = Collections.min(states, new Comparator<State>() {
            @Override
            public int compare(State a, State b) {
                return a.getBitCount() - b.getBitCount();
            }
        });
        // Convert it to a bit array, and return.
        return minState.toBitArray(text);
    }

    // We update a set of states for a new character by updating each state
    // for the new character, merging the results, and then removing the
    // non-optimal states.
    private Collection<State> updateStateListForChar(Iterable<State> states, int index) {
        Collection<State> result = new LinkedList<>();
        for (State state : states) {
            updateStateForChar(state, index, result);
        }
        return simplifyStates(result);
    }

    // Return a set of states that represent the possible ways of updating this
    // state for the next character.  The resulting set of states are added to
    // the "result" list.
    private void updateStateForChar(State state, int index, Collection<State> result) {
        char ch = (char) (text[index] & 0xFF);
        boolean charInCurrentTable = CHAR_MAP[state.getMode()][ch] > 0;
        State stateNoBinary = null;
        for (int mode = 0; mode <= MODE_PUNCT; mode++) {
            int charInMode = CHAR_MAP[mode][ch];
            if (charInMode > 0) {
                if (stateNoBinary == null) {
                    // Only create stateNoBinary the first time it's required.
                    stateNoBinary = state.endBinaryShift(index);
                }
                // Try generating the character by latching to its mode
                if (!charInCurrentTable || mode == state.getMode() || mode == MODE_DIGIT) {
                    // If the character is in the current table, we don't want to latch to
                    // any other mode except possibly digit (which uses only 4 bits).  Any
                    // other latch would be equally successful *after* this character, and
                    // so wouldn't save any bits.
                    State latchState = stateNoBinary.latchAndAppend(mode, charInMode);
                    result.add(latchState);
                }
                // Try generating the character by switching to its mode.
                if (!charInCurrentTable && SHIFT_TABLE[state.getMode()][mode] >= 0) {
                    // It never makes sense to temporarily shift to another mode if the
                    // character exists in the current mode.  That can never save bits.
                    State shiftState = stateNoBinary.shiftAndAppend(mode, charInMode);
                    result.add(shiftState);
                }
            }
        }
        if (state.getBinaryShiftByteCount() > 0 || CHAR_MAP[state.getMode()][ch] == 0) {
            // It's never worthwhile to go into binary shift mode if you're not already
            // in binary shift mode, and the character exists in your current mode.
            // That can never save bits over just outputting the char in the current mode.
            State binaryState = state.addBinaryShiftChar(index);
            result.add(binaryState);
        }
    }

    private static Collection<State> updateStateListForPair(Iterable<State> states, int index, int pairCode) {
        Collection<State> result = new LinkedList<>();
        for (State state : states) {
            updateStateForPair(state, index, pairCode, result);
        }
        return simplifyStates(result);
    }

    private static Collection<State> simplifyStates(Iterable<State> states) {
        List<State> result = new LinkedList<>();
        for (State newState : states) {
            boolean add = true;
            for (Iterator<State> iterator = result.iterator(); iterator.hasNext();) {
                State oldState = iterator.next();
                if (oldState.isBetterThanOrEqualTo(newState)) {
                    add = false;
                    break;
                }
                if (newState.isBetterThanOrEqualTo(oldState)) {
                    iterator.remove();
                }
            }
            if (add) {
                result.add(newState);
            }
        }
        return result;
    }

    private static void updateStateForPair(State state, int index, int pairCode, Collection<State> result) {
        State stateNoBinary = state.endBinaryShift(index);
        // Possibility 1.  Latch to MODE_PUNCT, and then append this code
        result.add(stateNoBinary.latchAndAppend(MODE_PUNCT, pairCode));
        if (state.getMode() != MODE_PUNCT) {
            // Possibility 2.  Shift to MODE_PUNCT, and then append this code.
            // Every state except MODE_PUNCT (handled above) can shift
            result.add(stateNoBinary.shiftAndAppend(MODE_PUNCT, pairCode));
        }
        if (pairCode == 3 || pairCode == 4) {
            // both characters are in DIGITS.  Sometimes better to just add two digits
            State digitState = stateNoBinary
                    .latchAndAppend(MODE_DIGIT, 16 - pairCode)  // period or comma in DIGIT
                    .latchAndAppend(MODE_DIGIT, 1);             // space in DIGIT
            result.add(digitState);
        }
        if (state.getBinaryShiftByteCount() > 0) {
            // It only makes sense to do the characters as binary if we're already
            // in binary mode.
            State binaryState = state.addBinaryShiftChar(index).addBinaryShiftChar(index + 1);
            result.add(binaryState);
        }
    }

    /**
     * Padding character
     */
    private static final char PAD = 129;
    /**
     * mode latch to C40 encodation mode
     */
    static final char LATCH_TO_C40 = 230;
    /**
     * mode latch to Base 256 encodation mode
     */
    static final char LATCH_TO_BASE256 = 231;
    /**
     * FNC1 Codeword
     */
    //private static final char FNC1 = 232;
    /**
     * Structured Append Codeword
     */
    //private static final char STRUCTURED_APPEND = 233;
    /**
     * Reader Programming
     */
    //private static final char READER_PROGRAMMING = 234;
    /**
     * Upper Shift
     */
    static final char UPPER_SHIFT = 235;
    /**
     * 05 Macro
     */
    private static final char MACRO_05 = 236;
    /**
     * 06 Macro
     */
    private static final char MACRO_06 = 237;
    /**
     * mode latch to ANSI X.12 encodation mode
     */
    static final char LATCH_TO_ANSIX12 = 238;
    /**
     * mode latch to Text encodation mode
     */
    static final char LATCH_TO_TEXT = 239;
    /**
     * mode latch to EDIFACT encodation mode
     */
    static final char LATCH_TO_EDIFACT = 240;
    /**
     * ECI character (Extended Channel Interpretation)
     */
    //private static final char ECI = 241;

    /**
     * Unlatch from C40 encodation
     */
    static final char C40_UNLATCH = 254;
    /**
     * Unlatch from X12 encodation
     */
    static final char X12_UNLATCH = 254;

    /**
     * 05 Macro header
     */
    private static final String MACRO_05_HEADER = "[)>\u001E05\u001D";
    /**
     * 06 Macro header
     */
    private static final String MACRO_06_HEADER = "[)>\u001E06\u001D";
    /**
     * Macro trailer
     */
    private static final String MACRO_TRAILER = "\u001E\u0004";

    static final int ASCII_ENCODATION = 0;
    static final int C40_ENCODATION = 1;
    static final int TEXT_ENCODATION = 2;
    static final int X12_ENCODATION = 3;
    static final int EDIFACT_ENCODATION = 4;
    static final int BASE256_ENCODATION = 5;

    private HighLevelEncoder() {
    }

  /*
   * Converts the message to a byte array using the default encoding (cp437) as defined by the
   * specification
   *
   * @param msg the message
   * @return the byte array of the message
   */

  /*
  public static byte[] getBytesForMessage(String msg) {
    return msg.getBytes(Charset.forName("cp437")); //See 4.4.3 and annex B of ISO/IEC 15438:2001(E)
  }
   */

    private static char randomize253State(char ch, int codewordPosition) {
        int pseudoRandom = ((149 * codewordPosition) % 253) + 1;
        int tempVariable = ch + pseudoRandom;
        return tempVariable <= 254 ? (char) tempVariable : (char) (tempVariable - 254);
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm described in annex P
     * of ISO/IEC 16022:2000(E).
     *
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 255)
     */
    public static String encodeHighLevel(String msg) {
        return encodeHighLevel(msg, SymbolShapeHint.FORCE_NONE, null, null);
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm described in annex P
     * of ISO/IEC 16022:2000(E).
     *
     * @param msg     the message
     * @param shape   requested shape. May be {@code SymbolShapeHint.FORCE_NONE},
     *                {@code SymbolShapeHint.FORCE_SQUARE} or {@code SymbolShapeHint.FORCE_RECTANGLE}.
     * @param minSize the minimum symbol size constraint or null for no constraint
     * @param maxSize the maximum symbol size constraint or null for no constraint
     * @return the encoded message (the char values range from 0 to 255)
     */
    public static String encodeHighLevel(String msg,
                                         SymbolShapeHint shape,
                                         Dimension minSize,
                                         Dimension maxSize) {
        //the codewords 0..255 are encoded as Unicode characters
        EncoderDataMatrix[] encoders = {
                new ASCIIEncoder(), new C40Encoder(), new TextEncoder(),
                new X12Encoder(), new EdifactEncoder(),  new Base256Encoder()
        };

        EncoderContext context = new EncoderContext(msg);
        context.setSymbolShape(shape);
        context.setSizeConstraints(minSize, maxSize);

        if (msg.startsWith(MACRO_05_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_05);
            context.setSkipAtEnd(2);
            context.pos += MACRO_05_HEADER.length();
        } else if (msg.startsWith(MACRO_06_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_06);
            context.setSkipAtEnd(2);
            context.pos += MACRO_06_HEADER.length();
        }

        int encodingMode = ASCII_ENCODATION; //Default mode
        while (context.hasMoreCharacters()) {
            encoders[encodingMode].encode(context);
            if (context.getNewEncoding() >= 0) {
                encodingMode = context.getNewEncoding();
                context.resetEncoderSignal();
            }
        }
        int len = context.getCodewordCount();
        context.updateSymbolInfo();
        int capacity = context.getSymbolInfo().getDataCapacity();
        if (len < capacity) {
            if (encodingMode != ASCII_ENCODATION && encodingMode != BASE256_ENCODATION) {
                context.writeCodeword('\u00fe'); //Unlatch (254)
            }
        }
        //Padding
        StringBuilder codewords = context.getCodewords();
        if (codewords.length() < capacity) {
            codewords.append(PAD);
        }
        while (codewords.length() < capacity) {
            codewords.append(randomize253State(PAD, codewords.length() + 1));
        }

        return context.getCodewords().toString();
    }

    static int lookAheadTest(CharSequence msg, int startpos, int currentMode) {
        if (startpos >= msg.length()) {
            return currentMode;
        }
        float[] charCounts;
        //step J
        if (currentMode == ASCII_ENCODATION) {
            charCounts = new float[]{0, 1, 1, 1, 1, 1.25f};
        } else {
            charCounts = new float[]{1, 2, 2, 2, 2, 2.25f};
            charCounts[currentMode] = 0;
        }

        int charsProcessed = 0;
        while (true) {
            //step K
            if ((startpos + charsProcessed) == msg.length()) {
                int min = Integer.MAX_VALUE;
                byte[] mins = new byte[6];
                int[] intCharCounts = new int[6];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                int minCount = getMinimumCount(mins);

                if (intCharCounts[ASCII_ENCODATION] == min) {
                    return ASCII_ENCODATION;
                }
                if (minCount == 1 && mins[BASE256_ENCODATION] > 0) {
                    return BASE256_ENCODATION;
                }
                if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                }
                if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                }
                if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                }
                return C40_ENCODATION;
            }

            char c = msg.charAt(startpos + charsProcessed);
            charsProcessed++;

            //step L
            if (isDigit(c)) {
                charCounts[ASCII_ENCODATION] += 0.5;
            } else if (isExtendedASCII(c)) {
                charCounts[ASCII_ENCODATION] = (int) Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION] += 2;
            } else {
                charCounts[ASCII_ENCODATION] = (int) Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION]++;
            }

            //step M
            if (isNativeC40(c)) {
                charCounts[C40_ENCODATION] += 2.0f / 3.0f;
            } else if (isExtendedASCII(c)) {
                charCounts[C40_ENCODATION] += 8.0f / 3.0f;
            } else {
                charCounts[C40_ENCODATION] += 4.0f / 3.0f;
            }

            //step N
            if (isNativeText(c)) {
                charCounts[TEXT_ENCODATION] += 2.0f / 3.0f;
            } else if (isExtendedASCII(c)) {
                charCounts[TEXT_ENCODATION] += 8.0f / 3.0f;
            } else {
                charCounts[TEXT_ENCODATION] += 4.0f / 3.0f;
            }

            //step O
            if (isNativeX12(c)) {
                charCounts[X12_ENCODATION] += 2.0f / 3.0f;
            } else if (isExtendedASCII(c)) {
                charCounts[X12_ENCODATION] += 13.0f / 3.0f;
            } else {
                charCounts[X12_ENCODATION] += 10.0f / 3.0f;
            }

            //step P
            if (isNativeEDIFACT(c)) {
                charCounts[EDIFACT_ENCODATION] += 3.0f / 4.0f;
            } else if (isExtendedASCII(c)) {
                charCounts[EDIFACT_ENCODATION] += 17.0f / 4.0f;
            } else {
                charCounts[EDIFACT_ENCODATION] += 13.0f / 4.0f;
            }

            // step Q
            if (isSpecialB256(c)) {
                charCounts[BASE256_ENCODATION] += 4;
            } else {
                charCounts[BASE256_ENCODATION]++;
            }

            //step R
            if (charsProcessed >= 4) {
                int[] intCharCounts = new int[6];
                byte[] mins = new byte[6];
                findMinimums(charCounts, intCharCounts, Integer.MAX_VALUE, mins);
                int minCount = getMinimumCount(mins);

                if (intCharCounts[ASCII_ENCODATION] < intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] < intCharCounts[C40_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] < intCharCounts[TEXT_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] < intCharCounts[X12_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] < intCharCounts[EDIFACT_ENCODATION]) {
                    return ASCII_ENCODATION;
                }
                if (intCharCounts[BASE256_ENCODATION] < intCharCounts[ASCII_ENCODATION]
                        || (mins[C40_ENCODATION] + mins[TEXT_ENCODATION] + mins[X12_ENCODATION] + mins[EDIFACT_ENCODATION]) == 0) {
                    return BASE256_ENCODATION;
                }
                if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                }
                if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                }
                if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                }
                if (intCharCounts[C40_ENCODATION] + 1 < intCharCounts[ASCII_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[EDIFACT_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[TEXT_ENCODATION]) {
                    if (intCharCounts[C40_ENCODATION] < intCharCounts[X12_ENCODATION]) {
                        return C40_ENCODATION;
                    }
                    if (intCharCounts[C40_ENCODATION] == intCharCounts[X12_ENCODATION]) {
                        int p = startpos + charsProcessed + 1;
                        while (p < msg.length()) {
                            char tc = msg.charAt(p);
                            if (isX12TermSep(tc)) {
                                return X12_ENCODATION;
                            }
                            if (!isNativeX12(tc)) {
                                break;
                            }
                            p++;
                        }
                        return C40_ENCODATION;
                    }
                }
            }
        }
    }

    private static int findMinimums(float[] charCounts, int[] intCharCounts, int min, byte[] mins) {
        Arrays.fill(mins, (byte) 0);
        for (int i = 0; i < 6; i++) {
            intCharCounts[i] = (int) Math.ceil(charCounts[i]);
            int current = intCharCounts[i];
            if (min > current) {
                min = current;
                Arrays.fill(mins, (byte) 0);
            }
            if (min == current) {
                mins[i]++;

            }
        }
        return min;
    }

    private static int getMinimumCount(byte[] mins) {
        int minCount = 0;
        for (int i = 0; i < 6; i++) {
            minCount += mins[i];
        }
        return minCount;
    }

    static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isExtendedASCII(char ch) {
        return ch >= 128 && ch <= 255;
    }

    private static boolean isNativeC40(char ch) {
        return (ch == ' ') || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isNativeText(char ch) {
        return (ch == ' ') || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isNativeX12(char ch) {
        return isX12TermSep(ch) || (ch == ' ') || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isX12TermSep(char ch) {
        return (ch == '\r') //CR
                || (ch == '*')
                || (ch == '>');
    }

    private static boolean isNativeEDIFACT(char ch) {
        return ch >= ' ' && ch <= '^';
    }

    private static boolean isSpecialB256(char ch) {
        return false; //TODO NOT IMPLEMENTED YET!!!
    }

    /**
     * Determines the number of consecutive characters that are encodable using numeric compaction.
     *
     * @param msg      the message
     * @param startpos the start position within the message
     * @return the requested character count
     */
    public static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    static void illegalCharacter(char c) {
        String hex = Integer.toHexString(c);
        hex = "0000".substring(0, 4 - hex.length()) + hex;
        throw new IllegalArgumentException("Illegal character: " + c + " (0x" + hex + ')');
    }

}
