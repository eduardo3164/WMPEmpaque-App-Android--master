package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;


/**
 * Created by jcalderon on 25/05/2016.
 */
public class listPPAdapter extends BaseAdapter {

    private Activity nContext;
    private ArrayList<PrePallet> prepallets;
    private LayoutInflater layoutInflater;

    public listPPAdapter(Activity context, ArrayList<PrePallet> prepallets) {
        this.nContext = context;
        this.prepallets = prepallets;
        this.layoutInflater = nContext.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return prepallets.size();
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
        //ViewHolder holder;

        //if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.viewprepalletlist, null);
            //holder = new ViewHolder();

            final PrePallet pp = prepallets.get(position);

            final TextView idPP = (TextView) convertView.findViewById(R.id.idPP);
            final TextView lblTituloIdPallet = (TextView) convertView.findViewById(R.id.lblTituloIdPallet);
            final TextView lblIdPallet = (TextView) convertView.findViewById(R.id.lblIdPallet);
            final TextView planta = (TextView) convertView.findViewById(R.id.planta);
            final TextView linea = (TextView) convertView.findViewById(R.id.linea);
            final TextView promotion = (TextView) convertView.findViewById(R.id.promotion);
            final TextView size = (TextView) convertView.findViewById(R.id.size);
            final TextView sku = (TextView) convertView.findViewById(R.id.sku);
            final TextView fecha = (TextView) convertView.findViewById(R.id.fecha);
            final TextView cajas = (TextView) convertView.findViewById(R.id.cajas);
            final TextView promoDesc = (TextView) convertView.findViewById(R.id.promotionDesc);
            final TextView casesPerPallet = (TextView) convertView.findViewById(R.id.casesPerPallet);
            final TextView sync = (TextView) convertView.findViewById(R.id.sync);

            //convertView.setTag(holder);

        //}else{
         //   holder = (ViewHolder) convertView.getTag();
        //}

        //View view = layoutInflater.inflate(R.layout.viewprepalletlist, null);

        //TextView sync = (TextView) view.findViewById(R.id.sync);

        if(pp.getSync() == 0)
            sync.setVisibility(View.VISIBLE);
        else
            sync.setVisibility(View.INVISIBLE);

      /*  TextView idPP = (TextView) view.findViewById(R.id.idPP);
        TextView planta = (TextView) view.findViewById(R.id.planta);
        TextView linea = (TextView) view.findViewById(R.id.linea);
        TextView promotion = (TextView) view.findViewById(R.id.promotion);
        TextView size = (TextView) view.findViewById(R.id.size);
        TextView sku = (TextView) view.findViewById(R.id.sku);
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        TextView cajas = (TextView) view.findViewById(R.id.cajas);
        TextView promoDesc = (TextView) view.findViewById(R.id.promotionDesc);
        TextView casesPerPallet = (TextView) view.findViewById(R.id.casesPerPallet);*/

        idPP.setText(pp.getIdPrePallet()+"");
        planta.setText(pp.getPlantaName());
        linea.setText(pp.getNameLine());
        promotion.setText(pp.getvPromotion());
        size.setText(pp.getvSize());
        sku.setText(pp.getvSKU());
        fecha.setText(pp.getFullDateCreated());
        cajas.setText(pp.getCajas()+"");
        promoDesc.setText(pp.getPromoDesc());
        casesPerPallet.setText(pp.getCasesPerPallet()+"");

        if(!pp.getvPalletID().equalsIgnoreCase("null") && pp.getvPalletID().length() > 0) {
            lblTituloIdPallet.setVisibility(View.VISIBLE);
            lblIdPallet.setText(pp.getvPalletID());
        }

        return convertView;
    }

    /*static class ViewHolder {
        TextView idPP;
        TextView lblTituloIdPallet;
        TextView lblIdPallet;
        TextView planta;
        TextView linea;
        TextView promotion;
        TextView size;
        TextView sku;
        TextView fecha;
        TextView cajas;
        TextView promoDesc;
        TextView casesPerPallet;
        TextView sync;
    }*/
}