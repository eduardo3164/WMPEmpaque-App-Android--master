package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 28/04/2016.
 */
public class assignFolioAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private String[] code, boxes, idProductLog;
    private String[][] data;

   // private ArrayList<HashMap<String, String>> folios;


    public assignFolioAdapter(Activity context, String[] code, String[] boxes, String[] idProductLog) {
        super(context, R.layout.viewassingfolios, code);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.code= code;
        this.boxes = boxes;
        this.idProductLog = idProductLog;

        data = new String[code.length][3];
        //datos = new String[code.length];
      //  folios = new ArrayList<HashMap<String,String>>();
    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.viewassingfolios, null, true);

        TextView folio = (TextView) rowView.findViewById(R.id.tv_Folio);
        final TextView cajas = (TextView) rowView.findViewById(R.id.tv_cajas);
        final EditText cajasToEnter = (EditText) rowView.findViewById(R.id.et_cajas);
       // final TextInputLayout layoutInputBoxes = (TextInputLayout) rowView.findViewById(R.id.layoutInputCajas);

        Switch switchFolios = (Switch) rowView.findViewById(R.id.SwfolioAccepted);

        switchFolios.setChecked(true);

        switchFolios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    cajasToEnter.setEnabled(false);
                    /*if(folios.size() > 0)
                    {
                        HashMap<String, String> folioMap = new HashMap<String, String>();
                        folioMap.put("folio", code[position]);
                        folioMap.put("idProductLog", idProductLog[position]);
                        folios.remove(folioMap);
                    }*/
                    //layoutInputBoxes.setError("Folio no asignado");

                    data[position][0] = "0";
                    data[position][1] = "0";
                    data[position][2] = "0";

                }else{
                    cajasToEnter.setEnabled(true);

                    data[position][0] = cajasToEnter.getText().toString();
                    data[position][1] = code[position];
                    data[position][2] = idProductLog[position];

                    /*HashMap<String, String> folioMap = new HashMap<String, String>();
                    folioMap.put("folio", code[position]);
                    folioMap.put("cajas", cajasToEnter.getText().toString());
                    folioMap.put("idProductLog", idProductLog[position]);
                    folios.add(folioMap);*/
                    //layoutInputBoxes.setError(null);


                }
            }
        });

        folio.setText(code[position]);
        cajas.setText(boxes[position]);

        cajasToEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().compareToIgnoreCase("") == 0) {
                    cajasToEnter.setText("0");
                    cajasToEnter.setSelection(cajasToEnter.getText().length());
                }

                try {
                    int txtCajas = Integer.parseInt(s.toString());

                    if (txtCajas <= 0 || txtCajas > Integer.parseInt(boxes[position])) {
                        cajasToEnter.setBackgroundColor(context.getResources().getColor(R.color.errorColor));
                     //   datos[position] = "0";
                        data[position][0] = "0";
                        data[position][1] = "0";
                        data[position][2] = "0";
                    } else {
                        cajasToEnter.setBackgroundColor(context.getResources().getColor(R.color.blanco));
                      //  datos[position] = s.toString();

                        data[position][0] = s.toString();
                        data[position][1] = code[position];
                        data[position][2] = idProductLog[position];

                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
        });
        cajasToEnter.setText(boxes[position]);

        return rowView;
    };


    public String[][] getDatos(){
        return data;
    }


}