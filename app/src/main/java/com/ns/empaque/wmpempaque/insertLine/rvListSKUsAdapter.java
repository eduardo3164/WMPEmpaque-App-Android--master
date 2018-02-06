package com.ns.empaque.wmpempaque.insertLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jcalderon on 31/01/2017.
 */

public class rvListSKUsAdapter extends RecyclerView.Adapter {

    private ArrayList<String[]> skus;

    public rvListSKUsAdapter(ArrayList<String[]> skus){
        this.skus = skus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new skusViewHolder(new rowSKUsView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((rowSKUsView)holder.itemView).setAdapter(new skusAdapter(skus.get(position)));
    }

    @Override
    public int getItemCount() {
        return skus.size();
    }

    private class skusViewHolder extends RecyclerView.ViewHolder {

        public skusViewHolder(View itemView) {
            super(itemView);
        }
    }

}
