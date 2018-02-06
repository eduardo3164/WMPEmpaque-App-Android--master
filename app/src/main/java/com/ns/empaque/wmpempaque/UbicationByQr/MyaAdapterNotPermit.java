package com.ns.empaque.wmpempaque.UbicationByQr;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jcalderon on 11/03/2016.
 */
public class MyaAdapterNotPermit extends ArrayAdapter {

    public Activity nContext;
    private String[] folios;
    private JSONArray notPermits;

    public MyaAdapterNotPermit(Activity nContext, String[] folios, JSONArray notPermits) {
        super(nContext, R.layout.viewnotpermitfoliosinline,folios);
        this.folios = folios;
        this.notPermits = notPermits;
        this.nContext = nContext;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = nContext.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.viewnotpermitfoliosinline, null);
        Log.d("position", position + "");
        TextView folioTV = (TextView) convertView.findViewById(R.id.tv_folio);
        TextView skuTV = (TextView) convertView.findViewById(R.id.tv_sku);
        TextView boxesTV = (TextView) convertView.findViewById(R.id.tv_boxes);
        TextView totalBoxesTV =  (TextView) convertView.findViewById(R.id.tv_totalBoxes);

        try {
            JSONObject row = notPermits.getJSONObject(position);
            folioTV.setText(folios[position]);
            skuTV.setText(row.getString("sku"));
            boxesTV.setText(row.getString("cajas"));
            totalBoxesTV.setText(row.getString("cajasTotales"));
        }catch(Exception e){

        }

        return convertView;
    }

}
