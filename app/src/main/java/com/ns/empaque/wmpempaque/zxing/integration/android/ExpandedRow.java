package com.ns.empaque.wmpempaque.zxing.integration.android;

import java.util.ArrayList;
import java.util.List;

/**
 * One row of an RSS Expanded Stacked symbol, consisting of 1+ expanded pairs.
 */
final class ExpandedRow {

    private final List<ExpandedPair> pairs;
    private final int rowNumber;
    /** Did this row of the image have to be reversed (mirrored) to recognize the pairs? */
    private final boolean wasReversed;

    ExpandedRow(List<ExpandedPair> pairs, int rowNumber, boolean wasReversed) {
        this.pairs = new ArrayList<>(pairs);
        this.rowNumber = rowNumber;
        this.wasReversed = wasReversed;
    }

    List<ExpandedPair> getPairs() {
        return this.pairs;
    }

    int getRowNumber() {
        return this.rowNumber;
    }

    boolean isReversed() {
        return this.wasReversed;
    }

    boolean isEquivalent(List<ExpandedPair> otherPairs) {
        return this.pairs.equals(otherPairs);
    }

    @Override
    public String toString() {
        return "{ " + pairs + " }";
    }

    /**
     * Two rows are equal if they contain the same pairs in the same order.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpandedRow)) {
            return false;
        }
        ExpandedRow that = (ExpandedRow) o;
        return this.pairs.equals(that.getPairs()) && wasReversed == that.wasReversed;
    }

    @Override
    public int hashCode() {
        return pairs.hashCode() ^ Boolean.valueOf(wasReversed).hashCode();
    }

}
