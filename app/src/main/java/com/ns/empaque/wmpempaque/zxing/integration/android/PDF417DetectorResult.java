package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.List;

/**
 * @author Guenther Grau
 */
public final class PDF417DetectorResult {

    private final BitMatrix bits;
    private final List<ResultPoint[]> points;

    public PDF417DetectorResult(BitMatrix bits, List<ResultPoint[]> points) {
        this.bits = bits;
        this.points = points;
    }

    public BitMatrix getBits() {
        return bits;
    }

    public List<ResultPoint[]> getPoints() {
        return points;
    }

}
