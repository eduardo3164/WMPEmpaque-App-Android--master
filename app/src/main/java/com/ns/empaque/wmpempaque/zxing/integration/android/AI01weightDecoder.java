package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 */
abstract class AI01weightDecoder extends AI01decoder {

    AI01weightDecoder(BitArray information) {
        super(information);
    }

    protected final void encodeCompressedWeight(StringBuilder buf, int currentPos, int weightSize) {
        int originalWeightNumeric = this.getGeneralDecoder().extractNumericValueFromBitArray(currentPos, weightSize);
        addWeightCode(buf, originalWeightNumeric);

        int weightNumeric = checkWeight(originalWeightNumeric);

        int currentDivisor = 100000;
        for(int i = 0; i < 5; ++i){
            if (weightNumeric / currentDivisor == 0) {
                buf.append('0');
            }
            currentDivisor /= 10;
        }
        buf.append(weightNumeric);
    }

    protected abstract void addWeightCode(StringBuilder buf, int weight);

    protected abstract int checkWeight(int weight);

}
