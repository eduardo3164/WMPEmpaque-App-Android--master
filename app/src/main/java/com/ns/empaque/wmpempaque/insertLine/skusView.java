package com.ns.empaque.wmpempaque.insertLine;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class skusView extends FrameLayout {

    public skusView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.skus_view, this);
    }

    public void displayItem(String text) {
        ((TextView)findViewById(R.id.skuTextView)).setText(text);
    }
}
