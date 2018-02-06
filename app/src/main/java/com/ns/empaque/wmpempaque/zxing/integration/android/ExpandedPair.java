package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 */
final class ExpandedPair {

    private final boolean mayBeLast;
    private final DataCharacter leftChar;
    private final DataCharacter rightChar;
    private final FinderPattern finderPattern;

    ExpandedPair(DataCharacter leftChar,
                 DataCharacter rightChar,
                 FinderPattern finderPattern,
                 boolean mayBeLast) {
        this.leftChar = leftChar;
        this.rightChar = rightChar;
        this.finderPattern = finderPattern;
        this.mayBeLast = mayBeLast;
    }

    boolean mayBeLast(){
        return this.mayBeLast;
    }

    DataCharacter getLeftChar() {
        return this.leftChar;
    }

    DataCharacter getRightChar() {
        return this.rightChar;
    }

    FinderPattern getFinderPattern() {
        return this.finderPattern;
    }

    public boolean mustBeLast() {
        return this.rightChar == null;
    }

    @Override
    public String toString() {
        return
                "[ " + leftChar + " , " + rightChar + " : " +
                        (finderPattern == null ? "null" : finderPattern.getValue()) + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpandedPair)) {
            return false;
        }
        ExpandedPair that = (ExpandedPair) o;
        return
                equalsOrNull(leftChar, that.leftChar) &&
                        equalsOrNull(rightChar, that.rightChar) &&
                        equalsOrNull(finderPattern, that.finderPattern);
    }

    private static boolean equalsOrNull(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return hashNotNull(leftChar) ^ hashNotNull(rightChar) ^ hashNotNull(finderPattern);
    }

    private static int hashNotNull(Object o) {
        return o == null ? 0 : o.hashCode();
    }

}
