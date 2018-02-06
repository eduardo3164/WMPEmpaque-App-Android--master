package com.ns.empaque.wmpempaque.MermaLinea;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Christopher BA on 24/05/2017.
 */

public class AdaptadorListaMermaRV extends RecyclerView.Adapter {

    private ProductoMermado productoMermado;
    private ProductoMermado[] productoMermadoArray;
    private Activity nContext;

    public AdaptadorListaMermaRV(ProductoMermado productoMermado) {
        this.productoMermado = productoMermado;
    }

    public AdaptadorListaMermaRV(ProductoMermado[] productoMermadoArray, Activity nContext) {
        this.productoMermadoArray = productoMermadoArray;
        this.nContext = nContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MermadoViewHolder(new MermadoView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        productoMermado = productoMermadoArray[position];

        ((MermadoView)holder.itemView).displayInfo(productoMermado);
    }

    @Override
    public int getItemCount() {
        //return 1;
        return productoMermadoArray.length;
    }

    private class MermadoViewHolder extends RecyclerView.ViewHolder {

        public MermadoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
