package com.ns.empaque.wmpempaque.UpdateFolio;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.ns.empaque.wmpempaque.AsigPrefolio2Folio.Prefolio;

import java.util.ArrayList;

/**
 * Created by javier.calderon on 28/03/2017.
 */

public class prefolioAdapter extends RecyclerView.Adapter  {

    private Prefolio prefolioData;
    ArrayList<Prefolio> prefolioDataList;

    public prefolioAdapter(Prefolio prefolioData) {
        this.prefolioData = prefolioData;
    }

    public prefolioAdapter(ArrayList<Prefolio> prefolioDataList) {
        this.prefolioDataList = prefolioDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new prefolioAdapter.preFolioViewHolder(new preFolioRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        prefolioData = prefolioDataList.get(position);
        ((preFolioRowView)holder.itemView).displayRow(prefolioData, prefolioDataList, this, position);

    }

    @Override
    public int getItemCount() {
        return prefolioDataList.size();
    }

    private class preFolioViewHolder extends RecyclerView.ViewHolder {
        public preFolioViewHolder(View itemView) {
            super(itemView);
        }
    }
}
