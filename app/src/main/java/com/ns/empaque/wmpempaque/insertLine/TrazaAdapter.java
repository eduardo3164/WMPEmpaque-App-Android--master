package com.ns.empaque.wmpempaque.insertLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by javier.calderon on 21/03/2017.
 */

public class TrazaAdapter extends RecyclerView.Adapter {

    private TrazaLineInformation TrazaData;
    ArrayList<TrazaLineInformation> TrazaDataList;

    public TrazaAdapter(TrazaLineInformation TrazaData) {
        this.TrazaData = TrazaData;
    }

    public TrazaAdapter(ArrayList<TrazaLineInformation> FIFOFoliosList) {
        this.TrazaDataList = FIFOFoliosList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrazaAdapter.TrazaViewHolder(new TRAZAView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TrazaData = TrazaDataList.get(position);

        ((TRAZAView)holder.itemView).displayFolio(TrazaData.getFolio());
        ((TRAZAView)holder.itemView).displayBoxesSKU(TrazaData.getBoxes(), TrazaData.getSku());
        ((TRAZAView)holder.itemView).displayDateNameLine(TrazaData.getFechaIngreso(), TrazaData.getNameLine());
        ((TRAZAView)holder.itemView).displayFarm(TrazaData.getPlanta());
    }

    @Override
    public int getItemCount() {
        //return 1;
        return TrazaDataList.size();
    }

    private class TrazaViewHolder extends RecyclerView.ViewHolder {

        public TrazaViewHolder(View itemView) {
            super(itemView);
        }
    }
}

