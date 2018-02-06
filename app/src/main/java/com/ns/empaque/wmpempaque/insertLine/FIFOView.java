package com.ns.empaque.wmpempaque.insertLine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class FIFOView  extends FrameLayout {


    public FIFOView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vw_fifo_line, this);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    public void displayFolio(String text) {
        ((TextView)findViewById(R.id.tv_Folio)).setText(text);
    }

    public void displayPlace(String text) {
        ((TextView)findViewById(R.id.tv_Place)).setText(text);
    }

    public void displayBoxes(String text) {
        ((TextView)findViewById(R.id.tv_Boxes)).setText(text+" Cajas");
    }

    public void displayQADate(String text) {
        ((TextView)findViewById(R.id.tv_QADate)).setText(text);
    }


}
