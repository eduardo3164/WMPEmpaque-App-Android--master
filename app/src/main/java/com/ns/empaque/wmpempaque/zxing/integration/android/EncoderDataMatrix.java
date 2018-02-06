package com.ns.empaque.wmpempaque.zxing.integration.android;

interface EncoderDataMatrix {

    int getEncodingMode();

    void encode(EncoderContext context);

}