package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;


/**
 * Created by jcalderon on 25/05/2016.
 */
public class foliosAdapter extends BaseAdapter {

    private Activity nContext;
    private ArrayList<Folio> Folios;
    private String palletID;

    public foliosAdapter(Activity context, ArrayList<Folio> Folios, String vPalletID) {
        this.nContext = context;
        this.Folios = Folios;
        this.palletID = vPalletID;
    }

    @Override
    public int getCount() {
        return Folios.size();
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

        final Folio folio = Folios.get(position);
        LayoutInflater layoutInflater = nContext.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.view_folio_list_adapter, null);

        TextView txtEmbalaje = (TextView) view.findViewById(R.id.txtFolio);
        TextView numBoxes = (TextView) view.findViewById(R.id.et_numBoxes);
        TextView lblLbsPorCaja = (TextView) view.findViewById(R.id.lblLbsPorCaja);
        TextView lblLbsDisponibles = (TextView) view.findViewById(R.id.lblLbsDisponibles);
        TextView inv = (TextView) view.findViewById(R.id.txtInv);
        TextView caseHeader = (TextView) view.findViewById(R.id.txtCodeCaseHeader);

        final EditText txtNumCajas = (EditText) view.findViewById(R.id.txtNumCajas);

        txtEmbalaje.setText(folio.getFolioCode());
        numBoxes.setText(folio.getCajas()+"");
        lblLbsPorCaja.setText(folio.getLbsPorCaja()+"");
        lblLbsDisponibles.setText(folio.getLbsDisponibles()+"");
        inv.setText(folio.getGreenHouse());
        caseHeader.setText(folio.getCaseCodeHeader());
        txtNumCajas.setText(folio.getCajasSeleccionadas()+"");

        if(!palletID.equalsIgnoreCase("null") && palletID.length() > 0){
            txtNumCajas.setEnabled(false);
        }

        txtNumCajas.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                int nCajas = 0;

                try{
                    nCajas = Integer.parseInt(txtNumCajas.getText().toString());
                } catch (NumberFormatException ex){
                    nCajas = 0;
                    txtNumCajas.setText(nCajas + "");
                    txtNumCajas.setSelection(txtNumCajas.getText().length());
                    folio.setCajasSeleccionadas(nCajas);
                }

                if(nCajas < 0) {
                    nCajas = 0;
                    txtNumCajas.setText(nCajas + "");
                    txtNumCajas.setSelection(txtNumCajas.getText().length());
                    folio.setCajasSeleccionadas(nCajas);
                } else {
                    if(nCajas > folio.getCajas()) {
                        nCajas = folio.getCajas();
                        txtNumCajas.setText(nCajas + "");
                        txtNumCajas.setSelection(txtNumCajas.getText().length());
                        folio.setCajasSeleccionadas(nCajas);
                    } else {
                        folio.setCajasSeleccionadas(nCajas);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

}