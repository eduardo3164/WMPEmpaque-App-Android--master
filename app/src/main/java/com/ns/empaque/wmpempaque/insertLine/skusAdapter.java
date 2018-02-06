package com.ns.empaque.wmpempaque.insertLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class skusAdapter extends RecyclerView.Adapter {

    private String[] skus;

    public skusAdapter(String[] skus) {
        this.skus = skus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new skuViewHolder(new skusView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((skusView)holder.itemView).displayItem(skus[position]);
    }

    @Override
    public int getItemCount() {
        return skus.length;
    }

    private class skuViewHolder extends RecyclerView.ViewHolder {

        public skuViewHolder(View itemView) {
            super(itemView);
        }
    }
}


