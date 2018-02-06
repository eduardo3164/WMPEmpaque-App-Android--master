package com.ns.empaque.wmpempaque.zxing.integration.android;


import java.util.Map;

/**
 * This object renders an EAN8 code as a {@link BitMatrix}.
 *
 * @author aripollak@gmail.com (Ari Pollak)
 */
public final class EAN8Writer extends UPCEANWriter {

    private static final int CODE_WIDTH = 3 + // start guard
            (7 * 4) + // left bars
            5 + // middle guard
            (7 * 4) + // right bars
            3; // end guard

    @Override
    public BitMatrix encode(String contents,
                            BarcodeFormat format,
                            int width,
                            int height,
                            Map<EncodeHintType,?> hints) throws WriterException {
        if (format != BarcodeFormat.EAN_8) {
            throw new IllegalArgumentException("Can only encode EAN_8, but got "
                    + format);
        }

        return super.encode(contents, format, width, height, hints);
    }

    /**
     * @return a byte array of horizontal pixels (false = white, true = black)
     */
    @Override
    public boolean[] encode(String contents) {
        if (contents.length() != 8) {
            throw new IllegalArgumentException(
                    "Requested contents should be 8 digits long, but got " + contents.length());
        }

        boolean[] result = new boolean[CODE_WIDTH];
        int pos = 0;

        pos += appendPattern(result, pos, UPCEANReader.START_END_PATTERN, true);

        for (int i = 0; i <= 3; i++) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], false);
        }

        pos += appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, false);

        for (int i = 4; i <= 7; i++) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], true);
        }
        appendPattern(result, pos, UPCEANReader.START_END_PATTERN, true);

        return result;
    }

}