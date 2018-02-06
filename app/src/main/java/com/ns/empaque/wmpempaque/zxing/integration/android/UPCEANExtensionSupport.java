package com.ns.empaque.wmpempaque.zxing.integration.android;

final class UPCEANExtensionSupport {

    private static final int[] EXTENSION_START_PATTERN = {1,1,2};

    private final UPCEANExtension2Support twoSupport = new UPCEANExtension2Support();
    private final UPCEANExtension5Support fiveSupport = new UPCEANExtension5Support();

    Result decodeRow(int rowNumber, BitArray row, int rowOffset) throws NotFoundException {
        int[] extensionStartRange = UPCEANReader.findGuardPattern(row, rowOffset, false, EXTENSION_START_PATTERN);
        try {
            return fiveSupport.decodeRow(rowNumber, row, extensionStartRange);
        } catch (ReaderException ignored) {
            return twoSupport.decodeRow(rowNumber, row, extensionStartRange);
        }
    }

}
