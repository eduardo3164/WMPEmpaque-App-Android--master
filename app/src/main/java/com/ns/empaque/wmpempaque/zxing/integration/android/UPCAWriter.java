package com.ns.empaque.wmpempaque.zxing.integration.android;
import java.util.Map;

/**
 * This object renders a UPC-A code as a {@link BitMatrix}.
 *
 * @author qwandor@google.com (Andrew Walbran)
 */
public final class UPCAWriter implements Writer {

    private final EAN13Writer subWriter = new EAN13Writer();

    @Override
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height)
            throws WriterException {
        return encode(contents, format, width, height, null);
    }

    @Override
    public BitMatrix encode(String contents,
                            BarcodeFormat format,
                            int width,
                            int height,
                            Map<EncodeHintType,?> hints) throws WriterException {
        if (format != BarcodeFormat.UPC_A) {
            throw new IllegalArgumentException("Can only encode UPC-A, but got " + format);
        }
        return subWriter.encode(preencode(contents), BarcodeFormat.EAN_13, width, height, hints);
    }

    /**
     * Transform a UPC-A code into the equivalent EAN-13 code, and add a check digit if it is not
     * already present.
     */
    private static String preencode(String contents) {
        int length = contents.length();
        if (length == 11) {
            // No check digit present, calculate it and add it
            int sum = 0;
            for (int i = 0; i < 11; ++i) {
                sum += (contents.charAt(i) - '0') * (i % 2 == 0 ? 3 : 1);
            }
            contents += (1000 - sum) % 10;
        } else if (length != 12) {
            throw new IllegalArgumentException(
                    "Requested contents should be 11 or 12 digits long, but got " + contents.length());
        }
        return '0' + contents;
    }
}
