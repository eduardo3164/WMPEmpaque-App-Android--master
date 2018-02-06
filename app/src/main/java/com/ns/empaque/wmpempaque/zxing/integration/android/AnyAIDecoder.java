package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 */
final class AnyAIDecoder extends AbstractExpandedDecoder {

    private static final int HEADER_SIZE = 2 + 1 + 2;

    AnyAIDecoder(BitArray information) {
        super(information);
    }

    @Override
    public String parseInformation() throws NotFoundException, FormatException {
        StringBuilder buf = new StringBuilder();
        return this.getGeneralDecoder().decodeAllCodes(buf, HEADER_SIZE);
    }
}