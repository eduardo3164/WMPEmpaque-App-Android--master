package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.nio.charset.Charset;
import java.util.Map;

public final class AztecWriter implements Writer {

    private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

    @Override
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) {
        return encode(contents, format, width, height, null);
    }

    @Override
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType,?> hints) {
        String charset = hints == null ? null : (String) hints.get(EncodeHintType.CHARACTER_SET);
        Number eccPercent = hints == null ? null : (Number) hints.get(EncodeHintType.ERROR_CORRECTION);
        Number layers = hints == null ? null : (Number) hints.get(EncodeHintType.AZTEC_LAYERS);
        return encode(contents,
                format,
                width,
                height,
                charset == null ? DEFAULT_CHARSET : Charset.forName(charset),
                eccPercent == null ? Encoder.DEFAULT_EC_PERCENT : eccPercent.intValue(),
                layers == null ? Encoder.DEFAULT_AZTEC_LAYERS : layers.intValue());
    }

    private static BitMatrix encode(String contents, BarcodeFormat format,
                                    int width, int height,
                                    Charset charset, int eccPercent, int layers) {
        if (format != BarcodeFormat.AZTEC) {
            throw new IllegalArgumentException("Can only encode AZTEC, but got " + format);
        }
        AztecCode aztec = Encoder.encode(contents.getBytes(charset), eccPercent, layers);
        return renderResult(aztec, width, height);
    }

    private static BitMatrix renderResult(AztecCode code, int width, int height) {
        BitMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int outputWidth = Math.max(width, inputWidth);
        int outputHeight = Math.max(height, inputHeight);

        int multiple = Math.min(outputWidth / inputWidth, outputHeight / inputHeight);
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

        BitMatrix output = new BitMatrix(outputWidth, outputHeight);

        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
            // Write the contents of this row of the barcode
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
                if (input.get(inputX, inputY)) {
                    output.setRegion(outputX, outputY, multiple, multiple);
                }
            }
        }
        return output;
    }
}
