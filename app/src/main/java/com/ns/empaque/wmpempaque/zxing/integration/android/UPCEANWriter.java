package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * <p>Encapsulates functionality and implementation that is common to UPC and EAN families
 * of one-dimensional barcodes.</p>
 *
 * @author aripollak@gmail.com (Ari Pollak)
 * @author dsbnatut@gmail.com (Kazuki Nishiura)
 */
public abstract class UPCEANWriter extends OneDimensionalCodeWriter {

    @Override
    public int getDefaultMargin() {
        // Use a different default more appropriate for UPC/EAN
        return UPCEANReader.START_END_PATTERN.length;
    }

}