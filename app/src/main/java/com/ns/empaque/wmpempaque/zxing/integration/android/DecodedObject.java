package com.ns.empaque.wmpempaque.zxing.integration.android;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 */
abstract class DecodedObject {

    private final int newPosition;

    DecodedObject(int newPosition){
        this.newPosition = newPosition;
    }

    final int getNewPosition() {
        return this.newPosition;
    }

}
