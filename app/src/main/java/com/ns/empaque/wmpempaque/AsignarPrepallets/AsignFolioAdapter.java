package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by jcalderon on 06/07/2016.
 */
public class AsignFolioAdapter extends BaseAdapter {

    private Activity nContext;
    private ArrayList<Folio> Folios;
    private ArrayList<Folio> foliosToAsign;
    private LayoutInflater layoutInflater;
    private String ArrayTemp [];
    private Boolean ArrayBol [];
//
    public AsignFolioAdapter(Activity context, ArrayList<Folio> Folios) {
        this.nContext = context;
        this.Folios = Folios;
        this.foliosToAsign = new ArrayList<>();
        this.layoutInflater = nContext.getLayoutInflater();

        this.ArrayTemp = new String[this.Folios.size()];
        this.ArrayBol = new Boolean[this.Folios.size()];

        for(int i=0; i< this.Folios.size(); i++) {
            Folio f = Folios.get(i);
            this.ArrayBol[i] = false;
            this.ArrayTemp[i] = f.getCajas()+"";
        }

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

        //final ViewHolder holder;
        final Folio f = Folios.get(position);

       /* if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.viewassingfolios, null);
            holder = new ViewHolder();

            holder.cajas  = (TextView) convertView.findViewById(R.id.tv_cajas);
            holder.cajasToEnter =  (EditText) convertView.findViewById(R.id.et_cajas);
            holder.folio = (TextView) convertView.findViewById(R.id.tv_Folio);
            holder.switchFolios = (Switch) convertView.findViewById(R.id.SwfolioAccepted);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }*/

        //final Folio f = Folios.get(position);

        convertView = layoutInflater.inflate(R.layout.viewassingfolios, null);
        View view = convertView;

        TextView folio = (TextView) view.findViewById(R.id.tv_Folio);
        final TextView cajas = (TextView) view.findViewById(R.id.tv_cajas);
        final EditText cajasToEnter = (EditText) view.findViewById(R.id.et_cajas);
        // final TextInputLayout layoutInputBoxes = (TextInputLayout) rowView.findViewById(R.id.layoutInputCajas);

        Switch switchFolios = (Switch) view.findViewById(R.id.SwfolioAccepted);

        folio.setText(f.getFolioCode());
        cajas.setText(f.getCajas() + "");

        cajasToEnter.setText(ArrayTemp[position]);



        switchFolios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*ArrayBol[position] = isChecked;
                if (!isChecked) {

                    Folio f1 = new Folio();

                    f1.setFolioCode(f.getFolioCode());
                    f1.setIdProductLog(f.getIdProductLog());
                    if (foliosToAsign.contains(f1)) {
                        foliosToAsign.remove(f1);
                        //ArrayTemp[position] = "0";

                    }

                    cajasToEnter.setEnabled(ArrayBol[position]);

                    Log.d("CheckChange No Checked", cajasToEnter.getText().toString());

                } else {
                    cajasToEnter.setEnabled(true);
                    cajasToEnter.setText(ArrayTemp[position]);
                    //ArrayTemp[position] = f.getCajas()+"";
                    // ArrayTemp[position] = f.getCajas() + "";
                    Log.d("CheckChange checked", cajasToEnter.getText().toString());
                }*/
            }
        });

        switchFolios.setChecked(ArrayBol[position]);

        cajasToEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*//holder.switchFolios.setChecked(true);
                ArrayTemp[position] = s.toString();
                if (s.toString().compareToIgnoreCase("") == 0) {
                    cajasToEnter.setText("0");
                    cajasToEnter.setSelection(cajasToEnter.getText().length());

                 //   ArrayTemp[position] = s.toString();
                }

                try {
                    int txtCajas = Integer.parseInt(s.toString());

                    if (txtCajas <= 0 || txtCajas > f.getCajas()) {
                        cajasToEnter.setBackgroundColor(nContext.getResources().getColor(R.color.errorColor));
                        Folio f1 = new Folio();

                        f1.setFolioCode(f.getFolioCode());
                        if (foliosToAsign.contains(f1)) {
                            foliosToAsign.remove(f1);
                            // ArrayTemp[position] = "0";
                        }
                       // ArrayTemp[position] = s.toString();
                        //ArrayTemp[position] = f.;

                    } else {
                        cajasToEnter.setBackgroundColor(nContext.getResources().getColor(R.color.blanco));
                        // holder.switchFolios.setChecked(true);
                        //  datos[position] = s.toString();

                        Folio f1 = new Folio();

                        f1.setFolioCode(f.getFolioCode());
                        f1.setCajas(Integer.parseInt(s.toString()));
                        f1.setGreenHouse(f.getGreenHouse());
                        f1.setIdProductLog(f.getIdProductLog());

                        Log.d("folioAdap", f.getFolioCode());
                        Log.d("cajaAdat", s.toString());

                        if (foliosToAsign.contains(f1)) {
                            foliosToAsign.remove(f1);
                            foliosToAsign.add(f1);
                            //  ArrayTemp[position] = f1.getCajas() + "";
                        } else {
                            foliosToAsign.add(f1);
                            // ArrayTemp[position] = f1.getCajas() + "";
                        }

                       // ArrayTemp[position] = s.toString();

                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }

                //holder.switchFolios.setChecked(ArrayTemp[position] == null || ArrayTemp[position].compareToIgnoreCase("0") == 0 ? false : true);*/
            }
        });




        return view;
    }

    public ArrayList<Folio> getDatos(){
        return foliosToAsign;
    }

  /*  public static class ViewHolder {
        TextView cajas;
        EditText cajasToEnter;
        TextView folio;
        Switch switchFolios;
    }*/

}
