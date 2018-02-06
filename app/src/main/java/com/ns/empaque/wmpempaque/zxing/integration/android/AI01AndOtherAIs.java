package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 */
final class AI01AndOtherAIs extends AI01decoder {

    private static final int HEADER_SIZE = 1 + 1 + 2; //first bit encodes the linkage flag,
    //the second one is the encodation method, and the other two are for the variable length
    AI01AndOtherAIs(BitArray information) {
        super(information);
    }

    @Override
    public String parseInformation() throws NotFoundException, FormatException {
        StringBuilder buff = new StringBuilder();

        buff.append("(01)");
        int initialGtinPosition = buff.length();
        int firstGtinDigit = this.getGeneralDecoder().extractNumericValueFromBitArray(HEADER_SIZE, 4);
        buff.append(firstGtinDigit);

        this.encodeCompressedGtinWithoutAI(buff, HEADER_SIZE + 4, initialGtinPosition);

        return this.getGeneralDecoder().decodeAllCodes(buff, HEADER_SIZE + 44);
    }
}
