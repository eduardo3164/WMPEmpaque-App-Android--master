package com.ns.empaque.wmpempaque.UbicationByQr;

/**
 * Created by jcalderon on 09/05/2016.
 */

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class FIFOlineAdapter extends BaseAdapter {

    private Activity nContext;
    private int[] positions;
    private JSONArray items;

    public FIFOlineAdapter(Activity context, JSONArray folios) {
        this.nContext = context;
        this.items = folios;

        positions = new int[items.length()];
    }


    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater layoutInflater = nContext.getLayoutInflater();
            //View view = layoutInflater.inflate(R.layout.viewadapterfifoline, null);
            convertView = layoutInflater.inflate(R.layout.viewadapterfifoline, null);
            holder = new ViewHolder();

            holder.vfolio = (TextView) convertView.findViewById(R.id.txtFolio);
            holder.number = (TextView) convertView.findViewById(R.id.txtNumero);
            holder.location = (TextView) convertView.findViewById(R.id.locat);
            holder.boex = (TextView) convertView.findViewById(R.id.boxes);


            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        /*TextView vfolio = (TextView) view.findViewById(R.id.txtFolio);
        TextView number = (TextView) view.findViewById(R.id.txtNumero);
        TextView location = (TextView) view.findViewById(R.id.locat);
        TextView boex = (TextView) view.findViewById(R.id.boxes);
        */
        try{
            JSONObject row = items.getJSONObject(position);
            if(position > 0) {
               JSONObject rowAnterior = items.getJSONObject(position-1);
                String vFolio = rowAnterior.getString("vFolio");

                if(vFolio.compareToIgnoreCase(row.getString("vFolio")) == 0){
                    holder.number.setText( positions[position-1]+"" );
                    positions[position] = positions[position-1];
                }else{
                    positions[position] = positions[position-1] + 1;
                    holder.number.setText( positions[position]+"");

                }
            }else{
                positions[position] = (position+1);
                holder.number.setText(positions[position]+"");
            }
            holder.vfolio.setText(row.getString("vFolio"));
            holder.location.setText(row.getString("locacion") + " ("+row.getString("iXPosition")+", "+row.getString("iYPosition")+", "+row.getString("iZPosition")+")");
            holder.boex.setText(row.getString("Cajas"));
        }catch(Exception ex){
            Log.e("ErrorAdapter",ex.getMessage());

        }


        return convertView;
    }

    static class ViewHolder {
        TextView vfolio;
        TextView number;
        TextView location;
        TextView boex;
    }



}
