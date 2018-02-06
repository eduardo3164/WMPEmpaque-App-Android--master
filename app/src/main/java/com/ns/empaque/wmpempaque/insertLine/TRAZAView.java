package com.ns.empaque.wmpempaque.insertLine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by javier.calderon on 21/03/2017.
 */

public class TRAZAView extends FrameLayout {




    public TRAZAView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vw_trazalist, this);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    public void displayFolio(String folio) {
        ((TextView)findViewById(R.id.tv_Folio)).setText(folio);
    }

    public void displayBoxesSKU(String boxes, String Sku) {
        ((TextView)findViewById(R.id.tv_Boxes)).setText(boxes+" Cajas");
        ((TextView)findViewById(R.id.tv_SKU)).setText(Sku);
    }

    public void displayFarm(String farm) {
        ((TextView)findViewById(R.id.tv_Place)).setText(farm);
    }

    public void displayDateNameLine(String lineDate, String nameLine) {
        ((TextView)findViewById(R.id.tv_lineDate)).setText(lineDate);
        ((TextView)findViewById(R.id.tv_nameLine)).setText(nameLine);
    }

}

