package com.ns.empaque.wmpempaque.zxing.integration.android;



/**
 * <p>Encapsulates a finder pattern, which are the three square patterns found in
 * the corners of QR Codes. It also encapsulates a count of similar finder patterns,
 * as a convenience to the finder's bookkeeping.</p>
 *
 * @author Sean Owen
 */
public final class FinderPattern extends ResultPoint {

    private final float estimatedModuleSize;
    private final int count;


    private final int value;
    private final int[] startEnd;
    private final ResultPoint[] resultPoints;

    FinderPattern(float posX, float posY, float estimatedModuleSize) {
        this(posX, posY, estimatedModuleSize, 1);
    }

    private FinderPattern(float posX, float posY, float estimatedModuleSize, int count) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
        this.count = count;

        this.value = 0;
        this.startEnd = new int[]{0};
        this.resultPoints = new ResultPoint[]{ new ResultPoint(0,0),new ResultPoint(0,0)};

    }

    public FinderPattern(int value, int[] startEnd, int start, int end, int rowNumber) {
        super(start,end);
        this.value = value;
        this.startEnd = startEnd;

        this.estimatedModuleSize = 0;
        this.count = 0;

        this.resultPoints = new ResultPoint[] {
                new ResultPoint((float) start, (float) rowNumber),
                new ResultPoint((float) end, (float) rowNumber),
        };
    }


    public int getValue() {
        return value;
    }

    public int[] getStartEnd() {
        return startEnd;
    }

    public ResultPoint[] getResultPoints() {
        return resultPoints;
    }


    public float getEstimatedModuleSize() {
        return estimatedModuleSize;
    }

    int getCount() {
        return count;
    }





  /*
  void incrementCount() {
    this.count++;
  }
   */

    /**
     * <p>Determines if this finder pattern "about equals" a finder pattern at the stated
     * position and size -- meaning, it is at nearly the same center with nearly the same size.</p>
     */
    boolean aboutEquals(float moduleSize, float i, float j) {
        if (Math.abs(i - getY()) <= moduleSize && Math.abs(j - getX()) <= moduleSize) {
            float moduleSizeDiff = Math.abs(moduleSize - estimatedModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= estimatedModuleSize;
        }
        return false;
    }

    /**
     * Combines this object's current estimate of a finder pattern position and module size
     * with a new estimate. It returns a new {@code FinderPattern} containing a weighted average
     * based on count.
     */
    FinderPattern combineEstimate(float i, float j, float newModuleSize) {
        int combinedCount = count + 1;
        float combinedX = (count * getX() + j) / combinedCount;
        float combinedY = (count * getY() + i) / combinedCount;
        float combinedModuleSize = (count * estimatedModuleSize + newModuleSize) / combinedCount;
        return new FinderPattern(combinedX, combinedY, combinedModuleSize, combinedCount);
    }



}
