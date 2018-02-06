package com.ns.empaque.wmpempaque.insertLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jcalderon on 01/02/2017.
 */

public class FIFOLineAdapter extends RecyclerView.Adapter {

    private String[] FIFOFolios;
    ArrayList<String[]> FIFOFoliosList;

    public FIFOLineAdapter(String[] FIFOFolios) {
        this.FIFOFolios = FIFOFolios;
    }

    public FIFOLineAdapter(ArrayList<String[]> FIFOFoliosList) {
        this.FIFOFoliosList = FIFOFoliosList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FIFOLineAdapter.FIFOViewHolder(new FIFOView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FIFOFolios = FIFOFoliosList.get(position);

        ((FIFOView)holder.itemView).displayFolio(FIFOFolios[0]);
        ((FIFOView)holder.itemView).displayPlace(FIFOFolios[1]);
        ((FIFOView)holder.itemView).displayBoxes(FIFOFolios[2]);
        ((FIFOView)holder.itemView).displayQADate(FIFOFolios[3]);
    }

    @Override
    public int getItemCount() {
        //return 1;
       return FIFOFoliosList.size();
    }

    private class FIFOViewHolder extends RecyclerView.ViewHolder {

        public FIFOViewHolder(View itemView) {
            super(itemView);
        }
    }
}
