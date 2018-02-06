package com.ns.empaque.wmpempaque.AsigPrefolio2Folio;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by jcalderon on 13/01/2017.
 */

public class PreFolioAdapter extends BaseAdapter{

    private Activity nContext;
    private ArrayList<Prefolio> al_preFolio;

    private LayoutInflater layoutInflater;

    public PreFolioAdapter(Activity context, ArrayList<Prefolio> al_preFolio) {
        this.nContext = context;
        this.al_preFolio = al_preFolio;
        this.layoutInflater = nContext.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return al_preFolio.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Prefolio pf = al_preFolio.get(position);

        convertView = layoutInflater.inflate(R.layout.vw_adapter_prefolio, null);
        View view = convertView;

        TextView txtPrefolio = (TextView) view.findViewById(R.id.txt_PreFolio);
        TextView txtGreenHouse = (TextView) view.findViewById(R.id.txt_Invernadero);
        TextView txtPeso = (TextView) view.findViewById(R.id.txt_Peso);
        TextView txtCalidad = (TextView) view.findViewById(R.id.txt_QA);
        TextView txtCajas = (TextView) view.findViewById(R.id.txt_cajas);
        LinearLayout btnErase = (LinearLayout) view.findViewById(R.id.btnErasePreFolio);

        txtPrefolio.setText(pf.getvPrefolio());
        txtGreenHouse.setText(pf.getvGreenHouse());
        txtPeso.setText(pf.getPeso()+"");
        txtCalidad.setText(pf.getQAName());
        txtCajas.setText(pf.getCajas()+"");

        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al_preFolio.remove(pf);

                notifyDataSetChanged();
            }
        });



        return view;
    }
}
