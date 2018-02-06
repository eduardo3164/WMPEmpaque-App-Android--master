package com.ns.empaque.wmpempaque.UpdateFolio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.AsigPrefolio2Folio.Prefolio;
import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by javier.calderon on 28/03/2017.
 */

public class preFolioRowView  extends FrameLayout {


    public preFolioRowView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vw_row_prefolio, this);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    public void displayRow(Prefolio prefolioData, final ArrayList<Prefolio> prefolioDataList, final prefolioAdapter adp, final int i){
        ((TextView)findViewById(R.id.tv_Folio)).setText(prefolioData.getvPrefolio().toString());
        ((TextView)findViewById(R.id.tv_QA)).setText(prefolioData.getQAName().toString());
        ((TextView)findViewById(R.id.tv_Boxes)).setText(prefolioData.getCajas()+"");
        ((TextView)findViewById(R.id.tv_Weight)).setText(prefolioData.getPeso()+"");
        ((TextView)findViewById(R.id.tv_QADate)).setText(prefolioData.getFechaCreacion().toString());
        ((TextView)findViewById(R.id.tv_inv)).setText(prefolioData.getvGreenHouse().toString());
        ((TextView)findViewById(R.id.tv_secciones)).setText(prefolioData.getSecciones().toString());


        ((LinearLayout)findViewById(R.id.btnErasePrefolio)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                prefolioDataList.remove(i);
                adp.notifyDataSetChanged();
            }
        });

    }

}
