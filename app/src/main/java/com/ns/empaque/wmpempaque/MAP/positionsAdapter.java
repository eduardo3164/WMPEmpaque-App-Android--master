package com.ns.empaque.wmpempaque.MAP;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.R;
import com.ns.empaque.wmpempaque.UbicationByQr.UbicationByQr;
import com.ns.empaque.wmpempaque.qrScanner.IntentIntegrator;

import java.util.ArrayList;


/**
 * Created by jcalderon on 25/05/2016.
 */
public class positionsAdapter extends BaseAdapter {

    private Activity nContext;
    private locacion lc;
    private String[] posiciones;
    private ArrayList<LocacionesOcupadas> busyLoct;
    private int z = 1;

    public positionsAdapter(Activity context, locacion lc, ArrayList<LocacionesOcupadas> busyLoct, int z) {
        this.nContext = context;
        this.z = z;
        this.lc = lc;
        this.busyLoct = busyLoct;
        posiciones = new String[lc.getWidth()*lc.getLenght()*lc.getHeight()];

        if(lc.getWidth() > lc.getLenght() ){
            for (int j = 0, l = 0; j < lc.getWidth(); j++)
                for (int i = 0; i < lc.getLenght(); i++, l++) {
                    posiciones[l] = (j + 1) + "," + (i + 1) + "," + z;
                }
        } else{
            for (int j = 0, l = 0; j < lc.getLenght(); j++)
                for (int i = 0; i < lc.getWidth(); i++, l++) {
                    posiciones[l] = (i + 1) + "," + (j + 1) + "," + z;
                }
        }
    }

    @Override
    public int getCount() {
        return (lc.getWidth()*lc.getLenght());
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
        View view = layoutInflater.inflate(R.layout.position, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView pst = (TextView) view.findViewById(R.id.pos);
        TextView folio = (TextView) view.findViewById(R.id.Folio);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView quality = (TextView) view.findViewById(R.id.Quality);
        TextView product = (TextView) view.findViewById(R.id.product);
        TextView gh = (TextView) view.findViewById(R.id.gh);
        LinearLayout btnPos = (LinearLayout) view.findViewById(R.id.btnPost);

        Boolean bandera = true;

     //   int cols = lc.getWidth();
       // int rows = lc.getLenght();
        final String[] xyz = posiciones[position].split(",");
        if(!busyLoct.isEmpty()) {

            for (int i = 0; i < busyLoct.size(); i++) {
                LocacionesOcupadas lo = busyLoct.get(i);

                if (Integer.parseInt(xyz[0]) == lo.getxPos() &&
                        Integer.parseInt(xyz[1]) == lo.getyPos() &&
                        Integer.parseInt(xyz[2]) == lo.getzPos()) {
                    btnPos.setBackgroundResource(R.drawable.btn_red);
                    folio.setText(lo.getFolio());
                    time.setText(lo.getHoras() + "-24");
                    quality.setText(lo.getCalidad());
                    product.setText(lo.getProducto());
                    gh.setText(lo.getGreenhouse()+" / "+lo.getBoxes()+"");
                    btnPos.setClickable(false);
                    bandera = false;
                }
            }
        }




        name.setText(lc.getCode());
        pst.setText(posiciones[position]);
        if(bandera) {
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* UbicationByQr.positions = xyz;
                    IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                    scanIntegrator.initiateScan();*/

                    showFormasADialog(xyz);

                }
            });
        }
        //Log.d("adapter", "entro");

        return view;
    }

    private void showFormasADialog(final String [] xyz) {
        //seleccionamos vista
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.viewtoscan, null);

        Button btnScan = (Button) dialoglayout.findViewById(R.id.btnScan);
        final EditText etEmbalaje = (EditText) dialoglayout.findViewById(R.id.txtEmbalaje);
        Button btnSendEmbalaje = (Button) dialoglayout.findViewById(R.id.btnSendEmb);



        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(dialoglayout)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog2.setIcon(R.drawable.naturesweet);
       // alertDialog2.setTitle();
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.show();

        ad2.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbicationByQr.positions = xyz;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();

                ad2.dismiss();
            }
        });

        btnSendEmbalaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmbalaje.getText().toString().compareToIgnoreCase("") == 0) {
                    Toast.makeText(nContext, "Debe llenar el campo con algún embalaje", Toast.LENGTH_LONG).show();
                } else {
                    // UbicationByQr ubqr = new UbicationByQr(nContext, w);
                    // String contents = intent.getStringExtra("SCAN_RESULT");
                    String contents = etEmbalaje.getText().toString();
                    contents = contents.replace(" ", "");
                    UbicationByQr.positions = xyz;
                    UbicationByQr ubq = new UbicationByQr(nContext);
                    ubq.addEmbalajeFromMap(contents);
                    ubq.enviarFoliosToServerFromMap(nContext);

                    ad2.dismiss();
                }
            }
        });

    }

}


