package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jcalderon on 29/03/2016.
 */
public class Cases {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static ListView CasesList;
    private static EditText etCases;
    private static Button btnGo, btnAsignarFolios;
    private static FloatingActionButton btnSendBoxes, btnAtras;
    private static TextView tvLine, tvFarm, tvSKU;
    private static Spinner spSize, spPromo;

    private static int posicionSize, posicionPromo;
    private static boolean editBandera= false;
    private static String idPrePallet, idFarm, idLine, sku, nombreFarm, nombreLine, Promotion, size;
    private static casesAdapter adaptadorCajas;
    private static ArrayList<HashMap<String, String>> cajas, foliosList;

    private static String[] idProductLog, idSize, codeSize, idPromo, desc;


    public Cases(Activity nContext, RelativeLayout parent, String idFarm, String idLine, String sku){
        this.nContext = nContext;
        this.content = parent;
        this.idFarm = idFarm;
        this.idLine = idLine;
        this.sku = sku;
        this.editBandera = false;//inserta

        //Obtenemos nombre de la farm
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
            String[][] datos = bd.obtenerFarm(idFarm);
            nombreFarm = datos[0][1];
            datos = bd.getInfoLinePackage(idLine);
            nombreLine = datos[0][1];
        bd.cerrar();

    }

    public Cases(Activity nContext, RelativeLayout parent, String[] cases, String idPrePallet){
        this.nContext = nContext;
        this.content = parent;
        this.idPrePallet = idPrePallet;
        this.editBandera = true;//Edita

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String[][] datos = bd.cpdb.getPrePallet(idPrePallet);
        bd.cerrar();
        if(datos.length > 0) {

            this.idFarm = datos[0][4];
            this.idLine = datos[0][5];
            this.sku = datos[0][2];

            nombreFarm = datos[0][1];
            nombreLine = datos[0][3];
            sku = datos[0][2];

            Promotion = datos[0][6];
            size = datos[0][8];

        }else{
            nombreFarm = "";
            nombreLine = "";
            sku = "";
        }
        setView();
        llenaLista(cases);
    }

    public static void llenaLista(String [] cases){
        for (int i=0; i<cases.length; i++)
            insertCase(cases[i]);
    }

    public static void setView(){

        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.casesprepallet, null, true);
        config.updateContent(content, v);

        CasesList = (ListView) v.findViewById(R.id.casesList);
        etCases = (EditText) v.findViewById(R.id.et_Cases);
        btnGo = (Button) v.findViewById(R.id.btnGo);
        btnSendBoxes = (FloatingActionButton) v.findViewById(R.id.fabSendCases);
        btnAtras  = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        btnAsignarFolios = (Button) v.findViewById(R.id.btnSaveFolios);
        spSize = (Spinner) v.findViewById(R.id.spSize);
        spPromo = (Spinner) v.findViewById(R.id.spPromo);

        tvLine = (TextView) v.findViewById(R.id.tvLine);
        tvFarm = (TextView) v.findViewById(R.id.tvPlant);
        tvSKU = (TextView) v.findViewById(R.id.tvSku);

        tvLine.setText(nombreLine);
        tvFarm.setText(nombreFarm);
        tvSKU.setText(sku);
        //etCases.requestFocus();

        llenarSizeSpinner();
        llenarPromoSpinner();

        cajas = new ArrayList<HashMap<String,String>>();
        foliosList =  new ArrayList<HashMap<String,String>>();
        adaptadorCajas = new casesAdapter(nContext,cajas, editBandera);

        CasesList.setAdapter(adaptadorCajas);
        CasesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //CasesList.smoothScrollToPosition(0);



        etCases.addTextChangedListener(new MyTextWatcher());
        buttonsEvents();

        spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionSize = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spPromo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionPromo = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(editBandera) {
           // Toast.makeText(nContext, " Falta llenar Spinner de size ", Toast.LENGTH_LONG).show();
            spSize.setSelection(getIndex(spSize, size));
            spPromo.setSelection(getIndexPromo());
        }
    }

    private static int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++)
            if (spinner.getItemAtPosition(i).equals(myString))
                index = i;
        return index;
    }

    private static int getIndexPromo() {
        int index = 0;
        for (int i = 0; i < idPromo.length; i++)
            if (idPromo[i].equals(Promotion))
                index = i;
        return index;
    }

    private static void llenarSizeSpinner() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos [][] = bd.getSizeCode();
        bd.cerrar();

        if(datos.length > 0){


            codeSize = new String[datos.length];
            idSize = new String[datos.length];

            for(int i = 0; i<datos.length; i++){
                idSize[i] = datos[i][0];
                codeSize[i] = datos[i][1];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item,codeSize );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);
        }

    }

    private static void llenarPromoSpinner() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos [][] = bd.getPromotions();
        bd.cerrar();

        if(datos.length > 0){
           // String[]

            idPromo = new String[datos.length];
            desc = new String[datos.length];

            for(int i = 0; i<datos.length; i++){
                idPromo[i] = datos[i][0];
                desc[i] = datos[i][1];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    nContext, android.R.layout.simple_spinner_item,desc );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPromo.setAdapter(adapter);
        }

    }

    private static void buttonsEvents() {

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cases = etCases.getText().toString();
                cases = cases.replace("\n", "");
                cases = cases.replace("\t", "");
                cases = cases.replace("\r", "");
                insertCase(cases);
            }
        });

        btnSendBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editBandera) {
                    guardarPrePallet();
                } else {
                    updatePrePallet();
                }
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PrePalletList(nContext, content).setView();
            }
        });
        btnAsignarFolios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // showFormasADialog();
            }
        });

        btnAsignarFolios.setVisibility(btnAsignarFolios.INVISIBLE);

    }

    private static void guardarPrePallet(){

        if(posicionSize != 0) {

            //Creo un objeto prepallet y asigno todo
            PrePallet pp = new PrePallet(nContext);
            pp.setBoxes(cajas);
            pp.setActive("1");
            pp.setFarm(idFarm);
            //pp.setLine(idLine);
            pp.setSKU(sku);
            pp.setFolios(foliosList);
            pp.setSize(codeSize[posicionSize]);
            pp.setPromotion(idPromo[posicionPromo]);

            //guardo el prepallet
            if (pp.savePrePallet() == 1) {
                Toast.makeText(nContext, "PrePallet creado correctamente", Toast.LENGTH_LONG).show();
                cajas.clear();
                adaptadorCajas.notifyDataSetChanged();
                PrePalletList.setView();
            } else {
                Toast.makeText(nContext, "Error al crear el PrePallet", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(nContext, "Seleccione un tamanio", Toast.LENGTH_LONG).show();
        }

    }

    private static void updatePrePallet(){

        if(posicionSize != 0) {
            //Creo un objeto prepallet y asigno todo
            PrePallet pp = new PrePallet(nContext);
            pp.setBoxes(cajas);
            pp.setActive("1");
            pp.setFarm(idFarm);
            //pp.setLine(idLine);
            pp.setSKU(sku);
            pp.setFolios(foliosList);
            pp.setIdPrePallet(idPrePallet);
            pp.setSize(codeSize[posicionSize]);
            pp.setPromotion(idPromo[posicionPromo]);

            if(pp.updatePrePallet() > 0){
                Toast.makeText(nContext, "PrePallet actualizado correctamente", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

                alertDialog2
                        .setMessage("¿Desea seguir editando el Pre-Pallet?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cajas.clear();
                                adaptadorCajas.notifyDataSetChanged();
                                PrePalletList.setView();

                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog2.setIcon(R.drawable.alerticon);
                alertDialog2.setTitle("Pre-Pallet actualizado correctamente");
                alertDialog2.setCancelable(false);
                AlertDialog ad2 = alertDialog2.create();
                ad2.show();
            }else{
                Toast.makeText(nContext, "Error al actualizar el PrePallet", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(nContext, "Seleccione un tamanio", Toast.LENGTH_LONG).show();
        }
    }

    private static void showFormasADialog() {
        new asyncTaskGetFoliosInLine(config.rutaWebServerOmar+"/getFoliosFromLine", idLine).execute();
    }

    private static void showFormasADialog(final String[] codigo, final String[] boxes) {
        //seleccionamos vista
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.viewsetfoliostoprepallet, null);

        ListView lv_assignFolios = (ListView) dialoglayout.findViewById(R.id.lv_forms);

        final assignFolioAdapter adaptador = new assignFolioAdapter(nContext, codigo, boxes, idProductLog);

        lv_assignFolios.setAdapter(adaptador);


        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(dialoglayout)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Guardar", "Guardar Folios seleccionados");
                        //datos = adaptador.getDatos();
                        //asignarCajasAfolios(codigo, boxes);
                        String[][] datos = adaptador.getDatos();

                        for(int i=0; i<datos.length; i++){
                            //if(!datos[0][1].matches("0")) {
                                HashMap<String, String> folioMap = new HashMap<String, String>();
                                folioMap.put("cajas", difCajasFolio(datos[i][1], Integer.parseInt(datos[i][0])) +"" );
                                folioMap.put("folio", datos[i][1]);
                                folioMap.put("idProductLog", datos[i][2]);
                                foliosList.add(folioMap);
                           // }
                        }

                        HashMap<String, String> folioMap = new HashMap<String, String>();
                        folioMap.put("cajas", "0");
                        folioMap.put("folio", "0");
                        folioMap.put("idProductLog","0");
                        foliosList.remove(folioMap);

                        for(int i=0; i<foliosList.size(); i++ ){
                            HashMap<String, String> item = foliosList.get(i);
                            Log.d("folio", item.get("folio"));
                            Log.d("cajas", item.get("cajas"));
                            Log.d("idProductLog", item.get("idProductLog"));
                        }
                    }
                });

        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setTitle("Asignar folios a Prepallet");
        alertDialog2.setCancelable(false);
        AlertDialog ad2 = alertDialog2.create();
        ad2.show();

        ad2.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

  /*  private static void asignarCajasAfolios(String[] code, String[] totalBoxes) {
        for(int i = 0; i<datos.length; i++) {
            if( Integer.parseInt(datos[i]) <= Integer.parseInt(totalBoxes[i]) && Integer.parseInt(datos[i]) > 0 ) {
                HashMap<String, String> folio = new HashMap<String, String>();
                folio.put("code", code[i]);
                folio.put("boxes", datos[i]);

                foliosList.add(folio);

                Log.d("folio | caja",code[i] + " | "+datos[i] );
            }
        }
    }*/

    public static void insertCase(String Case){

        if(config.validaString(Case, nContext) == 3) {
            HashMap<String, String> cases = new HashMap<String, String>();
            cases.put("case", Case);

            if(!cajas.contains(cases)) {
                cajas.add(0, cases);
                adaptadorCajas.notifyDataSetChanged();

                // Toast.makeText(nContext, Case, Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(nContext, "Ese case ya ha sido agregado a la lista", Toast.LENGTH_LONG).show();
                CasesList.setSelection(cajas.indexOf(cases));
            }
        }else
            Toast.makeText(nContext, "No es un case", Toast.LENGTH_LONG).show();

        etCases.setText("");
    }

    private static class MyTextWatcher implements TextWatcher {
        private String mText="";
        public MyTextWatcher() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do something with position:
            String oj = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)","");
            if(s.toString().contains("\n") == true){
                etCases.setText(mText);
                insertCase(oj);
            }else{
                mText = "";
            }

        }
    }

    private static class asyncTaskGetFoliosInLine extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String idLine;
        ProgressDialog pd;

        public asyncTaskGetFoliosInLine(String url, String idLine) {
            this.url = url;
            this.idLine = idLine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage("Cargando...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {

            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLine", idLine));
                params.add(new BasicNameValuePair("sku", sku));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));


                HttpParams httpParameters = new BasicHttpParams();

                int timeoutConnection = 5000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                int timeoutSocket = 7000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                final HttpClient Client = new DefaultHttpClient(httpParameters);

                step = "4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step = "5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step = "6";
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "" + t.getMessage() + " -- step: " + step;

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(String res) {

            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            } catch (Exception e) {
                Log.e("Error", "" + e.getMessage());
            }

            try {
                JSONObject json = new JSONObject(res);
                JSONArray folios = json.getJSONArray("table1");

                if (folios.length() > 0) {
                    String[] codigo, boxes;

                    codigo = new String[folios.length()];
                    boxes = new String[folios.length()];
                    idProductLog = new String[folios.length()];

                    for (int i = 0; i < folios.length(); i++) {
                        JSONObject row = folios.getJSONObject(i);
                        idProductLog[i] = row.getString("idProductLog");
                        boxes[i] = row.getString("cajas");
                        codigo[i] = row.getString("vFolio");
                    }

                    showFormasADialog(codigo, boxes);

                } else {
                    Toast.makeText(nContext, "No hay Folios disponibles para la linea seleccionada", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet", Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos", e.getMessage());
            }

        }
    }

    public static int difCajasFolio(String folio, int cajas){
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        int Boxes = bd.cpdb.difBoxesByFolio(folio);
        bd.cerrar();


        int sumatoria = (cajas-Boxes);

        if(sumatoria > 0)
            return sumatoria;
        else
            return 0;
    }

}
