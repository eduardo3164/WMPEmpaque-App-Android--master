package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by jcalderon on 16/11/2016.
 */
public class casesListAdapter extends BaseAdapter {

    private Activity nContext;
    public ArrayList<CaseCode> ccList;
    private LayoutInflater layoutInflater;

    public casesListAdapter(Activity context, ArrayList<CaseCode> ccList){
        this.ccList = ccList;
        this.nContext = context;
    }

    @Override
    public int getCount() {
        return ccList.size();
    }

    @Override
    public Object getItem(int position) {
        return ccList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        layoutInflater = nContext.getLayoutInflater();
        v = layoutInflater.inflate(R.layout.vw_item_caselist, null, true);

        TextView caseCode = (TextView) v.findViewById(R.id.caseCode);
        TextView txtFolio = (TextView) v.findViewById(R.id.txtFolio);
        TextView txtinv = (TextView) v.findViewById(R.id.txtinv);
        TextView txtSKU = (TextView) v.findViewById(R.id.txtSKUIFL);
        TextView txtLinea = (TextView) v.findViewById(R.id.txtLinea);
        //TextView txtSync = (TextView) v.findViewById(R.id.txtSync);
        ImageView btnEraseCaseCode = (ImageView) v.findViewById(R.id.btnEraseCaseCode);

        CaseCode cc = ccList.get(position);

        caseCode.setText(cc.getCode());
        txtFolio.setText(cc.getFolio());
        txtinv.setText(cc.getGreenHouse());
        txtSKU.setText(cc.getSKU());
        txtLinea.setText(cc.getNombreLinea());

        //txtSync.setText(cc.getSync() == 0 ? "No Sincronizado" : "Sincronizado");

        //if(cc.getSync() == 0)
            //txtSync.setTextColor(Color.parseColor("#ffcc0000"));
        //else
            //txtSync.setTextColor(Color.parseColor("#000000"));

        btnEraseCaseCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDatos bd = new BaseDatos(nContext);
                bd.abrir();
                bd.cidb.InactiveRow(ccList.get(position));//Borra Header
                bd.ciddb.InactiveRowsByCaseCode(ccList.get(position));//Borra todos los incrementables que dependen
                bd.cerrar();

                ccList.remove(position);
                notifyDataSetChanged();
            }
        });

        return v;
    }
}
