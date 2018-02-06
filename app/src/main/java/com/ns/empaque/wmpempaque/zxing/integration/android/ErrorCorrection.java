package com.ns.empaque.wmpempaque.zxing.integration.android;
/**
 * <p>PDF417 error correction implementation.</p>
 *
 * <p>This <a href="http://en.wikipedia.org/wiki/Reed%E2%80%93Solomon_error_correction#Example">example</a>
 * is quite useful in understanding the algorithm.</p>
 *
 * @author Sean Owen
 * see com.google.zxing.common.reedsolomon.ReedSolomonDecoder
 */
public final class ErrorCorrection {

    private final ModulusGF field;

    public ErrorCorrection() {
        this.field = ModulusGF.PDF417_GF;
    }

    /**
     * @param received received codewords
     * @param numECCodewords number of those codewords used for EC
     * @param erasures location of erasures
     * @return number of errors
     * @throws ChecksumException if errors cannot be corrected, maybe because of too many errors
     */
    public int decode(int[] received,
                      int numECCodewords,
                      int[] erasures) throws ChecksumException {

        ModulusPoly poly = new ModulusPoly(field, received);
        int[] S = new int[numECCodewords];
        boolean error = false;
        for (int i = numECCodewords; i > 0; i--) {
            int eval = poly.evaluateAt(field.exp(i));
            S[numECCodewords - i] = eval;
            if (eval != 0) {
                error = true;
            }
        }

        if (!error) {
            return 0;
        }

        ModulusPoly knownErrors = field.getOne();
        if (erasures != null) {
            for (int erasure : erasures) {
                int b = field.exp(received.length - 1 - erasure);
                // Add (1 - bx) term:
                ModulusPoly term = new ModulusPoly(field, new int[]{field.subtract(0, b), 1});
                knownErrors = knownErrors.multiply(term);
            }
        }

        ModulusPoly syndrome = new ModulusPoly(field, S);
        //syndrome = syndrome.multiply(knownErrors);

        ModulusPoly[] sigmaOmega =
                runEuclideanAlgorithm(field.buildMonomial(numECCodewords, 1), syndrome, numECCodewords);
        ModulusPoly sigma = sigmaOmega[0];
        ModulusPoly omega = sigmaOmega[1];

        //sigma = sigma.multiply(knownErrors);

        int[] errorLocations = findErrorLocations(sigma);
        int[] errorMagnitudes = findErrorMagnitudes(omega, sigma, errorLocations);

        for (int i = 0; i < errorLocations.length; i++) {
            int position = received.length - 1 - field.log(errorLocations[i]);
            if (position < 0) {
                throw ChecksumException.getChecksumInstance();
            }
            received[position] = field.subtract(received[position], errorMagnitudes[i]);
        }
        return errorLocations.length;
    }

    private ModulusPoly[] runEuclideanAlgorithm(ModulusPoly a, ModulusPoly b, int R)
            throws ChecksumException {
        // Assume a's degree is >= b's
        if (a.getDegree() < b.getDegree()) {
            ModulusPoly temp = a;
            a = b;
            b = temp;
        }

        ModulusPoly rLast = a;
        ModulusPoly r = b;
        ModulusPoly tLast = field.getZero();
        ModulusPoly t = field.getOne();

        // Run Euclidean algorithm until r's degree is less than R/2
        while (r.getDegree() >= R / 2) {
            ModulusPoly rLastLast = rLast;
            ModulusPoly tLastLast = tLast;
            rLast = r;
            tLast = t;

            // Divide rLastLast by rLast, with quotient in q and remainder in r
            if (rLast.isZero()) {
                // Oops, Euclidean algorithm already terminated?
                throw ChecksumException.getChecksumInstance();
            }
            r = rLastLast;
            ModulusPoly q = field.getZero();
            int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());
            int dltInverse = field.inverse(denominatorLeadingTerm);
            while (r.getDegree() >= rLast.getDegree() && !r.isZero()) {
                int degreeDiff = r.getDegree() - rLast.getDegree();
                int scale = field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
                q = q.add(field.buildMonomial(degreeDiff, scale));
                r = r.subtract(rLast.multiplyByMonomial(degreeDiff, scale));
            }

            t = q.multiply(tLast).subtract(tLastLast).negative();
        }

        int sigmaTildeAtZero = t.getCoefficient(0);
        if (sigmaTildeAtZero == 0) {
            throw ChecksumException.getChecksumInstance();
        }

        int inverse = field.inverse(sigmaTildeAtZero);
        ModulusPoly sigma = t.multiply(inverse);
        ModulusPoly omega = r.multiply(inverse);
        return new ModulusPoly[]{sigma, omega};
    }

    private static String createECCBlock(CharSequence codewords, int numECWords) {
        return createECCBlock(codewords, 0, codewords.length(), numECWords);
    }

    /**
     * Lookup table which factors to use for which number of error correction codewords.
     * See FACTORS.
     */
    private static final int[] FACTOR_SETS
            = {5, 7, 10, 11, 12, 14, 18, 20, 24, 28, 36, 42, 48, 56, 62, 68};

    /**
     * Precomputed polynomial factors for ECC 200.
     */
    private static final int[][] FACTORS = {
            {228, 48, 15, 111, 62},
            {23, 68, 144, 134, 240, 92, 254},
            {28, 24, 185, 166, 223, 248, 116, 255, 110, 61},
            {175, 138, 205, 12, 194, 168, 39, 245, 60, 97, 120},
            {41, 153, 158, 91, 61, 42, 142, 213, 97, 178, 100, 242},
            {156, 97, 192, 252, 95, 9, 157, 119, 138, 45, 18, 186, 83, 185},
            {83, 195, 100, 39, 188, 75, 66, 61, 241, 213, 109, 129, 94, 254, 225, 48, 90, 188},
            {15, 195, 244, 9, 233, 71, 168, 2, 188, 160, 153, 145, 253, 79, 108, 82, 27, 174, 186, 172},
            {52, 190, 88, 205, 109, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172,
                    254, 124, 12, 181, 184, 96, 50, 193},
            {211, 231, 43, 97, 71, 96, 103, 174, 37, 151, 170, 53, 75, 34, 249, 121,
                    17, 138, 110, 213, 141, 136, 120, 151, 233, 168, 93, 255},
            {245, 127, 242, 218, 130, 250, 162, 181, 102, 120, 84, 179, 220, 251, 80, 182,
                    229, 18, 2, 4, 68, 33, 101, 137, 95, 119, 115, 44, 175, 184, 59, 25,
                    225, 98, 81, 112},
            {77, 193, 137, 31, 19, 38, 22, 153, 247, 105, 122, 2, 245, 133, 242, 8,
                    175, 95, 100, 9, 167, 105, 214, 111, 57, 121, 21, 1, 253, 57, 54, 101,
                    248, 202, 69, 50, 150, 177, 226, 5, 9, 5},
            {245, 132, 172, 223, 96, 32, 117, 22, 238, 133, 238, 231, 205, 188, 237, 87,
                    191, 106, 16, 147, 118, 23, 37, 90, 170, 205, 131, 88, 120, 100, 66, 138,
                    186, 240, 82, 44, 176, 87, 187, 147, 160, 175, 69, 213, 92, 253, 225, 19},
            {175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235,
                    150, 159, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, 117, 203, 29, 232,
                    144, 238, 22, 150, 201, 117, 62, 207, 164, 13, 137, 245, 127, 67, 247, 28,
                    155, 43, 203, 107, 233, 53, 143, 46},
            {242, 93, 169, 50, 144, 210, 39, 118, 202, 188, 201, 189, 143, 108, 196, 37,
                    185, 112, 134, 230, 245, 63, 197, 190, 250, 106, 185, 221, 175, 64, 114, 71,
                    161, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, 163, 31,
                    176, 170, 4, 107, 232, 7, 94, 166, 224, 124, 86, 47, 11, 204},
            {220, 228, 173, 89, 251, 149, 159, 56, 89, 33, 147, 244, 154, 36, 73, 127,
                    213, 136, 248, 180, 234, 197, 158, 177, 68, 122, 93, 213, 15, 160, 227, 236,
                    66, 139, 153, 185, 202, 167, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239,
                    181, 241, 59, 52, 172, 25, 49, 232, 211, 189, 64, 54, 108, 153, 132, 63,
                    96, 103, 82, 186}};


    private static String createECCBlock(CharSequence codewords, int start, int len, int numECWords) {
        int table = -1;
        for (int i = 0; i < FACTOR_SETS.length; i++) {
            if (FACTOR_SETS[i] == numECWords) {
                table = i;
                break;
            }
        }
        if (table < 0) {
            throw new IllegalArgumentException(
                    "Illegal number of error correction codewords specified: " + numECWords);
        }
        int[] poly = FACTORS[table];
        char[] ecc = new char[numECWords];
        for (int i = 0; i < numECWords; i++) {
            ecc[i] = 0;
        }
        for (int i = start; i < start + len; i++) {
            int m = ecc[numECWords - 1] ^ codewords.charAt(i);
            for (int k = numECWords - 1; k > 0; k--) {
                if (m != 0 && poly[k] != 0) {
                    ecc[k] = (char) (ecc[k - 1] ^ ALOG[(LOG[m] + LOG[poly[k]]) % 255]);
                } else {
                    ecc[k] = ecc[k - 1];
                }
            }
            if (m != 0 && poly[0] != 0) {
                ecc[0] = (char) ALOG[(LOG[m] + LOG[poly[0]]) % 255];
            } else {
                ecc[0] = 0;
            }
        }
        char[] eccReversed = new char[numECWords];
        for (int i = 0; i < numECWords; i++) {
            eccReversed[i] = ecc[numECWords - i - 1];
        }
        return String.valueOf(eccReversed);
    }

    private static final int MODULO_VALUE = 0x12D;
    private static final int[] LOG;
    private static final int[] ALOG;
    static {
        //Create log and antilog table
        LOG = new int[256];
        ALOG = new int[255];

        int p = 1;
        for (int i = 0; i < 255; i++) {
            ALOG[i] = p;
            LOG[p] = i;
            p *= 2;
            if (p >= 256) {
                p ^= MODULO_VALUE;
            }
        }
    }
    /**
     * Creates the ECC200 error correction for an encoded message.
     *
     * @param codewords  the codewords
     * @param symbolInfo information about the symbol to be encoded
     * @return the codewords with interleaved error correction.
     */
    public static String encodeECC200(String codewords, SymbolInfo symbolInfo) {
        if (codewords.length() != symbolInfo.getDataCapacity()) {
            throw new IllegalArgumentException(
                    "The number of codewords does not match the selected symbol");
        }
        StringBuilder sb = new StringBuilder(symbolInfo.getDataCapacity() + symbolInfo.getErrorCodewords());
        sb.append(codewords);
        int blockCount = symbolInfo.getInterleavedBlockCount();
        if (blockCount == 1) {
            String ecc = createECCBlock(codewords, symbolInfo.getErrorCodewords());
            sb.append(ecc);
        } else {
            sb.setLength(sb.capacity());
            int[] dataSizes = new int[blockCount];
            int[] errorSizes = new int[blockCount];
            int[] startPos = new int[blockCount];
            for (int i = 0; i < blockCount; i++) {
                dataSizes[i] = symbolInfo.getDataLengthForInterleavedBlock(i + 1);
                errorSizes[i] = symbolInfo.getErrorLengthForInterleavedBlock(i + 1);
                startPos[i] = 0;
                if (i > 0) {
                    startPos[i] = startPos[i - 1] + dataSizes[i];
                }
            }
            for (int block = 0; block < blockCount; block++) {
                StringBuilder temp = new StringBuilder(dataSizes[block]);
                for (int d = block; d < symbolInfo.getDataCapacity(); d += blockCount) {
                    temp.append(codewords.charAt(d));
                }
                String ecc = createECCBlock(temp.toString(), errorSizes[block]);
                int pos = 0;
                for (int e = block; e < errorSizes[block] * blockCount; e += blockCount) {
                    sb.setCharAt(symbolInfo.getDataCapacity() + e, ecc.charAt(pos++));
                }
            }
        }
        return sb.toString();

    }

    private int[] findErrorLocations(ModulusPoly errorLocator) throws ChecksumException {
        // This is a direct application of Chien's search
        int numErrors = errorLocator.getDegree();
        int[] result = new int[numErrors];
        int e = 0;
        for (int i = 1; i < field.getSize() && e < numErrors; i++) {
            if (errorLocator.evaluateAt(i) == 0) {
                result[e] = field.inverse(i);
                e++;
            }
        }
        if (e != numErrors) {
            throw ChecksumException.getChecksumInstance();
        }
        return result;
    }

    private int[] findErrorMagnitudes(ModulusPoly errorEvaluator,
                                      ModulusPoly errorLocator,
                                      int[] errorLocations) {
        int errorLocatorDegree = errorLocator.getDegree();
        int[] formalDerivativeCoefficients = new int[errorLocatorDegree];
        for (int i = 1; i <= errorLocatorDegree; i++) {
            formalDerivativeCoefficients[errorLocatorDegree - i] =
                    field.multiply(i, errorLocator.getCoefficient(i));
        }
        ModulusPoly formalDerivative = new ModulusPoly(field, formalDerivativeCoefficients);

        // This is directly applying Forney's Formula
        int s = errorLocations.length;
        int[] result = new int[s];
        for (int i = 0; i < s; i++) {
            int xiInverse = field.inverse(errorLocations[i]);
            int numerator = field.subtract(0, errorEvaluator.evaluateAt(xiInverse));
            int denominator = field.inverse(formalDerivative.evaluateAt(xiInverse));
            result[i] = field.multiply(numerator, denominator);
        }
        return result;
    }
}
