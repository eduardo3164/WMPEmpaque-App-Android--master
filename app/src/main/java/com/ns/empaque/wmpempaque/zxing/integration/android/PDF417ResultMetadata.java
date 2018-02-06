package com.ns.empaque.wmpempaque.zxing.integration.android;
/**
 * @author Guenther Grau
 */
public final class PDF417ResultMetadata {

    private int segmentIndex;
    private String fileId;
    private int[] optionalData;
    private boolean lastSegment;

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int[] getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(int[] optionalData) {
        this.optionalData = optionalData;
    }

    public boolean isLastSegment() {
        return lastSegment;
    }

    public void setLastSegment(boolean lastSegment) {
        this.lastSegment = lastSegment;
    }

}
