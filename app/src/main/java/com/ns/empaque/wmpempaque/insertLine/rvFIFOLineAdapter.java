package com.ns.empaque.wmpempaque.insertLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class rvFIFOLineAdapter  extends RecyclerView.Adapter {

    private ArrayList<String[]> fifoFolios;

    public rvFIFOLineAdapter(ArrayList<String[]> fifoFolios){
        this.fifoFolios = fifoFolios;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FIFOViewHolder(new rowFIFOLineView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((rowFIFOLineView)holder.itemView).setAdapter(new FIFOLineAdapter(fifoFolios.get(position)));
    }

    @Override
    public int getItemCount() {
        return fifoFolios.size();
    }

    private class FIFOViewHolder extends RecyclerView.ViewHolder {

        public FIFOViewHolder(View itemView) {
            super(itemView);
        }
    }
}
