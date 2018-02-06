package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.Map;

/**
 * Implementation of this interface attempt to read several barcodes from one image.
 *
 * @see Reader
 * @author Sean Owen
 */
public interface MultipleBarcodeReader {

    Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException;

    Result[] decodeMultiple(BinaryBitmap image,
                            Map<DecodeHintType, ?> hints) throws NotFoundException;

}