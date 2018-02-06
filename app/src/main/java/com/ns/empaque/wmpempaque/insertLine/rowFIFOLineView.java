package com.ns.empaque.wmpempaque.insertLine;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class rowFIFOLineView extends FrameLayout {

    public rowFIFOLineView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.single_row, this);
        ((RecyclerView)findViewById(R.id.recyclerViewHorizontal)).setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    }

    public void setAdapter(FIFOLineAdapter adapter) {
        ((RecyclerView)findViewById(R.id.recyclerViewHorizontal)).setAdapter(adapter);
    }

}
