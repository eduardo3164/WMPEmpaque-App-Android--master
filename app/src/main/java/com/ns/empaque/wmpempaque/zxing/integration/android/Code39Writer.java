package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.Map;

/**
 * This object renders a CODE39 code as a {@link BitMatrix}.
 *
 * @author erik.barbara@gmail.com (Erik Barbara)
 */
public final class Code39Writer extends OneDimensionalCodeWriter {

    @Override
    public BitMatrix encode(String contents,
                            BarcodeFormat format,
                            int width,
                            int height,
                            Map<EncodeHintType,?> hints) throws WriterException {
        if (format != BarcodeFormat.CODE_39) {
            throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override
    public boolean[] encode(String contents) {
        int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException(
                    "Requested contents should be less than 80 digits long, but got " + length);
        }

        int[] widths = new int[9];
        int codeWidth = 24 + 1 + length;
        for (int i = 0; i < length; i++) {
            int indexInString = Code39Reader.ALPHABET_STRING.indexOf(contents.charAt(i));
            if (indexInString < 0) {
                throw new IllegalArgumentException("Bad contents: " + contents);
            }
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            for (int width : widths) {
                codeWidth += width;
            }
        }
        boolean[] result = new boolean[codeWidth];
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        int pos = appendPattern(result, 0, widths, true);
        int[] narrowWhite = {1};
        pos += appendPattern(result, pos, narrowWhite, false);
        //append next character to byte matrix
        for (int i = 0; i < length; i++) {
            int indexInString = Code39Reader.ALPHABET_STRING.indexOf(contents.charAt(i));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            pos += appendPattern(result, pos, widths, true);
            pos += appendPattern(result, pos, narrowWhite, false);
        }
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        appendPattern(result, pos, widths, true);
        return result;
    }

    private static void toIntArray(int a, int[] toReturn) {
        for (int i = 0; i < 9; i++) {
            int temp = a & (1 << (8 - i));
            toReturn[i] = temp == 0 ? 1 : 2;
        }
    }

}