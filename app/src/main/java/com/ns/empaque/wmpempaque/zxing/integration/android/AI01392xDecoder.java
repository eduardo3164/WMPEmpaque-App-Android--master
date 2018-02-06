package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 */
final class AI01392xDecoder extends AI01decoder {

    private static final int HEADER_SIZE = 5 + 1 + 2;
    private static final int LAST_DIGIT_SIZE = 2;

    AI01392xDecoder(BitArray information) {
        super(information);
    }

    @Override
    public String parseInformation() throws NotFoundException, FormatException {
        if (this.getInformation().getSize() < HEADER_SIZE + GTIN_SIZE) {
            throw NotFoundException.getNotFoundInstance();
        }

        StringBuilder buf = new StringBuilder();

        encodeCompressedGtin(buf, HEADER_SIZE);

        int lastAIdigit =
                this.getGeneralDecoder().extractNumericValueFromBitArray(HEADER_SIZE + GTIN_SIZE, LAST_DIGIT_SIZE);
        buf.append("(392");
        buf.append(lastAIdigit);
        buf.append(')');

        DecodedInformation decodedInformation =
                this.getGeneralDecoder().decodeGeneralPurposeField(HEADER_SIZE + GTIN_SIZE + LAST_DIGIT_SIZE, null);
        buf.append(decodedInformation.getNewString());

        return buf.toString();
    }

}
