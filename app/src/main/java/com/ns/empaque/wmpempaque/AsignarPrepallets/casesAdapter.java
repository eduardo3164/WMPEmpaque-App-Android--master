package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;


/**
 * Created by jcalderon on 25/05/2016.
 */
public class casesAdapter extends BaseAdapter {

    private Activity nContext;
    private ArrayList<cases> Cases;
    private TextView titleCaajasList;
    private String palletID;

    public casesAdapter(Activity context, ArrayList<cases> Cases, TextView titleCaajasList, String vPalletID) {
        this.nContext = context;
        this.Cases = Cases;
        this.titleCaajasList = titleCaajasList;
        this.palletID = vPalletID;
    }

    @Override
    public int getCount() {
        return Cases.size();
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
        final cases Case = Cases.get(position);
        LayoutInflater layoutInflater = nContext.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.view_case_adapter, null);

        TextView txtEmbalaje = (TextView) view.findViewById(R.id.txtEmbalaje);
        ImageView btnEliminar = (ImageView) view.findViewById(R.id.btn_Eliminar);

        if(!palletID.equalsIgnoreCase("null") && palletID.length() > 0){
            btnEliminar.setVisibility(View.INVISIBLE);
        }

        txtEmbalaje.setText(Case.getCodigoCase());

        btnEliminar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               cases Case = Cases.get(position);

               String strFolio = Case.getFolio();
               Folio folio = new Folio();
               folio.setFolioCode(strFolio);

               int contCases = 0;

               for(int i = 0; i < InsupPrePallet.pp.getCajasPrePallet().size(); i++) {
                   if(Case.equalsFolio(InsupPrePallet.pp.getCajasPrePallet().get(i))) {
                       contCases++;
                       Log.d("ENTRA", contCases+"");
                   }
               }

               if(contCases == 1)
                   InsupPrePallet.pp.getFolioPerPallet().remove(InsupPrePallet.pp.getFolioPerPallet().indexOf(folio));

               Cases.remove(position);

               notifyDataSetChanged();
               titleCaajasList.setText("Lista de cajas ("+(Cases.size() )+")");

               BaseDatos bd = new BaseDatos(nContext);
               bd.abrir();
               String datos[][] = bd.cpdb.buscaCase(Case.getCodigoCase());

               if(datos.length > 0)
                   bd.cpdb.eraseCase(Case.getCodigoCase());

               bd.cerrar();

               Case.setActive(false);
               InsupPrePallet.NewCasesList.add(0,Case);
           }
        });

        return view;
    }

}


