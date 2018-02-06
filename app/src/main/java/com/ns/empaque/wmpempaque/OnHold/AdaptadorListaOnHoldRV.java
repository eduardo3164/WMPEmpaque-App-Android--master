package com.ns.empaque.wmpempaque.OnHold;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ns.empaque.wmpempaque.insertLine.FIFOLineAdapter;
import com.ns.empaque.wmpempaque.insertLine.FIFOView;

import java.util.ArrayList;

/**
 * Created by Christopher BA on 21/03/2017.
 */

public class AdaptadorListaOnHoldRV extends RecyclerView.Adapter {

    private ProductoOnHold productoOnHold;
    private ProductoOnHold[] productoOnHoldArray;
    private Activity nContext;

    public AdaptadorListaOnHoldRV(ProductoOnHold productoOnHold) {
        this.productoOnHold = productoOnHold;
    }

    public AdaptadorListaOnHoldRV(ProductoOnHold[] productoOnHoldArray, Activity nContext) {
        this.productoOnHoldArray = productoOnHoldArray;
        this.nContext = nContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OnHoldiewHolder(new OnHoldView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        productoOnHold = productoOnHoldArray[position];

        ((OnHoldView)holder.itemView).displayInfo(productoOnHold);
    }

    @Override
    public int getItemCount() {
        //return 1;
        return productoOnHoldArray.length;
    }

    private class OnHoldiewHolder extends RecyclerView.ViewHolder {

        public OnHoldiewHolder(View itemView) {
            super(itemView);
        }
    }
}
