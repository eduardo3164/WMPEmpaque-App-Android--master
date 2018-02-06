package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 */
final class CurrentParsingState {

    private int position;
    private State encoding;

    private enum State {
        NUMERIC,
        ALPHA,
        ISO_IEC_646
    }

    CurrentParsingState() {
        this.position = 0;
        this.encoding = State.NUMERIC;
    }

    int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
    }

    void incrementPosition(int delta) {
        position += delta;
    }

    boolean isAlpha(){
        return this.encoding == State.ALPHA;
    }

    boolean isNumeric(){
        return this.encoding == State.NUMERIC;
    }

    boolean isIsoIec646(){
        return this.encoding == State.ISO_IEC_646;
    }

    void setNumeric() {
        this.encoding = State.NUMERIC;
    }

    void setAlpha() {
        this.encoding = State.ALPHA;
    }

    void setIsoIec646() {
        this.encoding = State.ISO_IEC_646;
    }
}

