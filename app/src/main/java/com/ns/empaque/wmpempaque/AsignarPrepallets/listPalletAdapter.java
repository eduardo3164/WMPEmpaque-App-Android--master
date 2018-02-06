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
public class listPalletAdapter extends BaseAdapter {

    private Activity nContext;
    private ArrayList<Pallet> pallets;

    public listPalletAdapter(Activity context, ArrayList<Pallet> pallets) {
        this.nContext = context;
        this.pallets = pallets;
    }

    @Override
    public int getCount() {
        return pallets.size();
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

        LayoutInflater layoutInflater = nContext.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.view_pallet_list, null);

        TextView pallet = (TextView) view.findViewById(R.id.pallet);
        TextView planta = (TextView) view.findViewById(R.id.planta);
        TextView libras = (TextView) view.findViewById(R.id.libras);
        TextView cajas = (TextView) view.findViewById(R.id.cajas);
        TextView gh = (TextView) view.findViewById(R.id.gh);
        TextView sku = (TextView) view.findViewById(R.id.sku);
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        TextView linea = (TextView) view.findViewById(R.id.linea);

        Pallet p = pallets.get(position);

        pallet.setText(p.getPallet());
        planta.setText(p.getNamePackFarm());
        libras.setText(p.getLibras()+"");
        cajas.setText(p.getCases()+"");
        gh.setText(p.getGreenHouse());
        sku.setText(p.getSku());
        fecha.setText(p.getRecordDate());
        linea.setText(p.getNameLines());


        return view;
    }

}


