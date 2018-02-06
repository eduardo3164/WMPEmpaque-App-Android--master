package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 31/03/2016.
 */
public class PrePalletList {

    private static Activity nContext;
    private static RelativeLayout content;
    public static LayoutInflater inflater;
    public static ListView ListPrePallet;
    public static FloatingActionButton fabAddPrePallet;
    public static prePalletListAdapter adaptadorPrePallet;

    public static String[] idPrepallet;
    public static String idFarmSelected, SKUSelected, lineSelected;

    public static Spinner SPPlanta, SPLine, SPSku;


    public PrePalletList(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
    }


    public static void setView(){

        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.prepalletlist, null, true);
        config.updateContent(content, v);

        ListPrePallet = (ListView) v.findViewById(R.id.prepalletList);
        fabAddPrePallet = (FloatingActionButton) v.findViewById(R.id.fabAddPrePallet);

        BaseDatos db = new BaseDatos(nContext);
        db.abrir();
        String datos[][] = db.cpdb.getPrePallets();
        db.cerrar();

        if(datos.length > 0) {

            final String[]  activo, sync;
            idPrepallet = new String[datos.length];
            activo = new String[datos.length];
            sync = new String[datos.length];
            
            for (int i = 0; i < datos.length; i++) {
                idPrepallet[i] = datos[i][0];
                activo[i] = datos[i][2];
                sync[i] = datos[i][1];
            }

            adaptadorPrePallet = new prePalletListAdapter(nContext,idPrepallet,activo,sync);
            ListPrePallet.setAdapter(adaptadorPrePallet);

            ListPrePallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    BaseDatos bd = new BaseDatos(nContext);
                    bd.abrir();
                    String datos[][] = bd.cpdb.getCasesFromPrepallet(idPrepallet[position]);
                    bd.cerrar();

                   // if(datos.length > 0) {
                        final String cases[] = new String[datos.length];
                        for(int i=0; i<datos.length; i++)
                            cases[i] = datos[i][1];

                        LayoutInflater inflater = nContext.getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.viewshowcasesinprepallet, null);

                        ListView caseslist = (ListView) dialoglayout.findViewById(R.id.listCasesinPrepallet);
                        FloatingActionButton fabEdit = (FloatingActionButton) dialoglayout.findViewById(R.id.fabEdit);

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1,cases);


                        caseslist.setAdapter(arrayAdapter);

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);

                        alertDialog.setView(dialoglayout)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setCancelable(false);
                        final AlertDialog ad = alertDialog.create();
                        ad.show();


                        fabEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ad.dismiss();
                                new Cases(nContext, content, cases, idPrepallet[position]);//edit
                            }
                        });



                   // }
                }
            });

        }else{
            String empty[] = {"No se encontraron prepallets almacenados"};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    nContext,
                    android.R.layout.simple_list_item_1,empty
                     );

            ListPrePallet.setAdapter(arrayAdapter);
        }
        buttonsEvents();
    }

    private static void buttonsEvents() {
        fabAddPrePallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDialogConfigPrePallet();
            }
        });
    }

    private static void showDialogConfigPrePallet(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.viewfirstconfigprepallet, null);

        SPPlanta = (Spinner) dialoglayout.findViewById(R.id.SPPlant);
        SPLine = (Spinner) dialoglayout.findViewById(R.id.SPLine);
        SPSku = (Spinner) dialoglayout.findViewById(R.id.SPSku);



        llenarSpinnerPlanta();
        //llenarSpinnerLine();
        //llenarSpinnerSku();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        // .setMessage("Are you sure you want to delete this entry?")
        alertDialog.setView(dialoglayout)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(idFarmSelected.compareToIgnoreCase("-1") == 0 ||
                                lineSelected.compareToIgnoreCase("-1") == 0 ||
                                SKUSelected.compareToIgnoreCase("-1") == 0){

                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

                            alertDialog2
                                    .setMessage("Intenta de nuevo")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            alertDialog2.setIcon(R.drawable.alerticon);
                            alertDialog2.setTitle("Tiene que seleccionar datos para Planta, Linea y SKU si desea continuar");
                            alertDialog2.setCancelable(false);
                            AlertDialog ad2 = alertDialog2.create();
                            ad2.show();


                        }else{
                            Log.d("farm",idFarmSelected);
                            Log.d("line",lineSelected);
                            Log.d("sku",SKUSelected);
                            new Cases(nContext, content, idFarmSelected, lineSelected, SKUSelected).setView();
                        }
                    }
                });
                           /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
        alertDialog.setIcon(R.drawable.alerticon);
        alertDialog.setTitle("Para continuar con el pre-pallet, elige sus configuraciones");
        alertDialog.setCancelable(false);
        final AlertDialog ad = alertDialog.create();
        ad.show();
    }

    private static void llenarSpinnerPlanta() {

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.obtenerFarms();
        bd.cerrar();

        if(datos.length > 0){

            String []namePlant = new String[datos.length];

            for(int i = 0; i<datos.length; i++)
                namePlant[i] = datos[i][1];

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item, namePlant);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SPPlanta.setAdapter(adapter);

            SPPlanta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idFarmSelected = datos[position][0];
                    llenarSpinnerLinea(datos[position][0]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }else{
            Toast.makeText(nContext, "No hay plantas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

    }

    private static void llenarSpinnerLinea(String idFarm) {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.getLinePackage(idFarm);
        bd.cerrar();

        if(datos.length > 0){

            String []nameLine = new String[datos.length];

            for(int i = 0; i<datos.length; i++)
                nameLine[i] = datos[i][1];


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item, nameLine);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SPLine.setAdapter(adapter);

            SPLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    lineSelected = datos[position][0];
                    llenarSpinnerSKU(datos[position][0]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }else{
            lineSelected = "-1";
            SKUSelected = "-1";

            String []emptyData = new String[1];

            emptyData[0] = "No hay datos";


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item, emptyData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            SPLine.setAdapter(adapter);
            SPSku.setAdapter(adapter);

            Toast.makeText(nContext, "No hay Lineas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

    }

    private static void llenarSpinnerSKU(String idLine) {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.getLineSKU(idLine);
        bd.cerrar();

        if(datos.length > 0){

            String[] skuName = new String [datos.length];

            for(int i = 0; i<datos.length; i++)
                skuName[i] = datos[i][0];

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item, skuName);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SPSku.setAdapter(adapter);

            SPSku.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SKUSelected = datos[position][0];
                    //llenarSpinnerSKU(datos[0][position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }else{

            SKUSelected = "-1";

            String []emptyData = new String[1];

            emptyData[0] = "No hay datos";


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item, emptyData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            SPSku.setAdapter(adapter);

            Toast.makeText(nContext, "No hay Sku's en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

}
