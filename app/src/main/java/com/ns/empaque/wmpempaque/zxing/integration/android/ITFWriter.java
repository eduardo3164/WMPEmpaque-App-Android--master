package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.Map;

/**
 * This object renders a ITF code as a {@link BitMatrix}.
 *
 * @author erik.barbara@gmail.com (Erik Barbara)
 */
public final class ITFWriter extends OneDimensionalCodeWriter {

    private static final int[] START_PATTERN = {1, 1, 1, 1};
    private static final int[] END_PATTERN = {3, 1, 1};

    @Override
    public BitMatrix encode(String contents,
                            BarcodeFormat format,
                            int width,
                            int height,
                            Map<EncodeHintType,?> hints) throws WriterException {
        if (format != BarcodeFormat.ITF) {
            throw new IllegalArgumentException("Can only encode ITF, but got " + format);
        }

        return super.encode(contents, format, width, height, hints);
    }

    @Override
    public boolean[] encode(String contents) {
        int length = contents.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("The lenght of the input should be even");
        }
        if (length > 80) {
            throw new IllegalArgumentException(
                    "Requested contents should be less than 80 digits long, but got " + length);
        }
        boolean[] result = new boolean[9 + 9 * length];
        int pos = appendPattern(result, 0, START_PATTERN, true);
        for (int i = 0; i < length; i += 2) {
            int one = Character.digit(contents.charAt(i), 10);
            int two = Character.digit(contents.charAt(i + 1), 10);
            int[] encoding = new int[18];
            for (int j = 0; j < 5; j++) {
                encoding[2 * j] = ITFReader.PATTERNS[one][j];
                encoding[2 * j + 1] = ITFReader.PATTERNS[two][j];
            }
            pos += appendPattern(result, pos, encoding, true);
        }
        appendPattern(result, pos, END_PATTERN, true);

        return result;
    }

}