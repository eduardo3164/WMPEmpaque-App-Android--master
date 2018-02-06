package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.BaseDatos.CasesPrePalletDB;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.R;
import com.ns.empaque.wmpempaque.WMPEmpaque;
import com.ns.empaque.wmpempaque.qrScanner.IntentIntegrator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcalderon on 20/06/2016.
 */
public class AsignarPrepallet {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static FloatingActionButton fabAtras, fabSearchLine, fabAddPrePallet;
    private static ImageView btnSearchLine;
    private static EditText et_locatCode;
    private static TextView linea, tvLinea, palletListTitle;
    private static ListView listViewPrePallet, listViewPallet;

    private static ArrayList<PrePallet> PrePalletList;
    private static ArrayList<Pallet> PalletList;
    //private static ArrayList<cases> CaseList;
    private static listPPAdapter adaptadorPP;
    private static listPalletAdapter adaptadorPallet;

    public static AlertDialog ad2;
    public static String codigoLinea = "";

    public AsignarPrepallet(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
    }

    public static void setView() {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.viewasignprepallet, null, true);
        config.updateContent(content, v);

        //Fab Buttons
        fabAtras = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        //fabSearchLine = (FloatingActionButton) v.findViewById(R.id.fabSearchLine);
        btnSearchLine = (ImageView) v.findViewById(R.id.btnSearchLine);
        fabAddPrePallet = (FloatingActionButton) v.findViewById(R.id.fabAddPP);

        et_locatCode = (EditText) v.findViewById(R.id.et_locatCode);
        linea = (TextView) v.findViewById(R.id.linea);
        tvLinea = (TextView) v.findViewById(R.id.tvLine);
        listViewPrePallet = (ListView) v.findViewById(R.id.prepalletList);


        PrePalletList = new ArrayList<>();
        PalletList = new ArrayList<>();
       // CaseList = new ArrayList<>();

        tvLinea.setVisibility(tvLinea.INVISIBLE);

        //funcionalidad de botón atras
        fabAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        //Funcionalidad para abrir la camara y escanear el codigo QR
        /*fabSearchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });*/

        btnSearchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        //Funcionalidad para agregar prepallets
        fabAddPrePallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddPrePallet addPP = new AddPrePallet(nContext);
                View vw = addPP.getPopUpView();

                FloatingActionButton fabAtrasAddDialog = (FloatingActionButton) vw.findViewById(R.id.fabAtras);

                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
                alertDialog2
                        .setView(vw);

                alertDialog2.setIcon(R.drawable.naturesweet);
                alertDialog2.setCancelable(false);
                final AlertDialog ad2 = alertDialog2.create();
                ad2.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ad2.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                ad2.show();

                fabAtrasAddDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad2.dismiss();
                    }
                });

                addPP.showDialogConfigPrePallet();
            }
        });

        //TextWatcher para agregar busqueda automatica
        et_locatCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isALine(s.toString())) {
                    codigoLinea = s.toString();
                    GetLineInfo(s.toString());
                }
            }
        });

        //Evento para agregar dialog al hacer click al prepallet
        prePalletListEvent();

    }

    private static void prePalletListEvent(){
        listViewPrePallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!PrePalletList.isEmpty()) {
                    PrePallet pp = PrePalletList.get(position);
                    //showPalletDialog(pp);

                    final InsupPrePallet insPp = new InsupPrePallet(pp, nContext, content);
                    View v = insPp.setView();

                    FloatingActionButton fabAtrasDialog = (FloatingActionButton) v.findViewById(R.id.fabAtras);

                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
                    alertDialog2.setView(v);

                    alertDialog2.setIcon(R.drawable.naturesweet);
                    alertDialog2.setCancelable(false);
                    final AlertDialog ad2 = alertDialog2.create();
                    ad2.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    ad2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    ad2.show();

                    /*alertDialog2.setIcon(R.drawable.naturesweet);
                    alertDialog2.setCancelable(false);
                    ad2 = alertDialog2.create();
                    ad2.show();*/

                    fabAtrasDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(insPp.pp.getvPalletID().equalsIgnoreCase("null") || insPp.pp.getvPalletID() == null){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);

                                alertDialog.setTitle("Confirmación");
                                alertDialog.setIcon(R.drawable.naturesweet);
                                alertDialog.setMessage("¿Desea cancelar la operación?\n\nNota: Se perdarán los datos que no han sido guardados.");
                                alertDialog.setCancelable(false);

                                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Log.d("CODIGO LINEA", codigoLinea+" - - - - - - - - - - - - ");
                                        GetLineInfo(codigoLinea);
                                        ad2.dismiss();
                                    }
                                });

                                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else {
                                ad2.dismiss();
                            }
                        }
                    });

                    ad2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        });
    }

    /*private static void showPalletDialog(PrePallet pp) {
        //seleccionamos vista
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.view_dialog_pallet, null);


        TextView idPP = (TextView) dialoglayout.findViewById(R.id.idPP);
        TextView planta = (TextView) dialoglayout.findViewById(R.id.planta);
        TextView linea = (TextView) dialoglayout.findViewById(R.id.linea);
        TextView promotion = (TextView) dialoglayout.findViewById(R.id.promotion);
        TextView size = (TextView) dialoglayout.findViewById(R.id.size);
        TextView sku = (TextView) dialoglayout.findViewById(R.id.sku);
        TextView fecha = (TextView) dialoglayout.findViewById(R.id.fecha);
        TextView cajas = (TextView) dialoglayout.findViewById(R.id.cajas);
        TextView promoDesc = (TextView) dialoglayout.findViewById(R.id.promotionDesc);
        FloatingActionButton fabCancel = (FloatingActionButton) dialoglayout.findViewById(R.id.fabCancel);
        palletListTitle = (TextView) dialoglayout.findViewById(R.id.listPallets);
        listViewPallet = (ListView) dialoglayout.findViewById(R.id.palletList);

        idPP.setText(pp.getIdPrePallet()+"");
        planta.setText(pp.getPlantaName());
        linea.setText(pp.getNameLine());
        promotion.setText(pp.getvPromotion());
        size.setText(pp.getvSize());
        sku.setText(pp.getvSKU());
        fecha.setText(pp.getFullDateCreated());
        cajas.setText(pp.getCajas()+"");
        promoDesc.setText(pp.getPromoDesc());

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(dialoglayout);

        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.show();

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad2.dismiss();
            }
        });

        ad2.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        new AysnTaskgetNotAsignPallets(
                config.rutaWebServerOmar+"/getNotAsignPallets",
                pp.getvSKU(),
                pp.getCajas()+"",
                pp.getvPromotion(),
                pp.getvSize(),
                pp.getIdFarm()+""
        ).execute();
    }*/

    public static boolean isALine(String content){
        if(content.matches("^[0-9A-Za-z]+-[0-9]{1,3}[|][0][.][0][.][0]"))
            return true;

        return false;
    }

    public static void GetLineInfo(String content) {
        //Toast.makeText(nContext, "Si tiene estrutura de linea", Toast.LENGTH_LONG).show();

        com.ns.empaque.wmpempaque.PrePallet.PrePallet pp2 = new com.ns.empaque.wmpempaque.PrePallet.PrePallet(nContext);
        pp2.sincronizar();

        et_locatCode.setText("");
        Log.d("s", content.toString());
        String[] partition = content.toString().split("\\|");
        partition = partition[0].split("-");
        Log.d("partition[1]", partition[1]+"");
        new AysnTaskGetPrePalletFromLine(config.rutaWebServerOmar+"/GetPrePalletFromLine", partition[0], partition[1]).execute();
    }

    public static class AysnTaskGetPrePalletFromLine extends AsyncTask<String, String, String> {

        public String url;
        private ProgressDialog pd;
        private String idLine, QRCode;

        public AysnTaskGetPrePalletFromLine(String url, String QRCode, String idLine){
            this.url = url;
            this.idLine = idLine;
            this.QRCode = QRCode;
            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Cargando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="", step="0";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("QRCode", QRCode));
                params.add(new BasicNameValuePair("idLine", idLine));

                step="2";
                HttpPost httppostreq = new HttpPost(url);
                step="3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step="4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step="5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step="6";
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(String res) {
            // Toast.makeText(GuardarRiego.this, res, Toast.LENGTH_LONG).show();
            Log.d("WebMethod -- >", res);
            PrePalletList.clear();
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            //CaseList.clear();

            /*BaseDatos db = new BaseDatos(nContext);
            db.abrir();
            Cursor c = db.cpdb.getPrePalletsByLine(idLine);


            if(c != null){

                for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                    PrePallet pp = new PrePallet();

                    pp.setIdPrePalletTablet(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLET)));
                    pp.setIdPrePallet(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLETSERVER)));
                    pp.setvPromotion(c.getString(c.getColumnIndex(CasesPrePalletDB.PROMOTIONS)));
                    pp.setPromoDesc(c.getString(c.getColumnIndex(BaseDatos.DESCPROMOTIONS)));
                    pp.setvSize(c.getString(c.getColumnIndex(CasesPrePalletDB.SIZE)));
                    pp.setvSKU(c.getString(c.getColumnIndex(BaseDatos.VSKU)));
                    pp.setIdFarm(c.getInt(c.getColumnIndex(BaseDatos.IDFARM)));
                    pp.setPlantaName(c.getString(c.getColumnIndex(BaseDatos.NOMBREFARM)));
                    pp.setIdLinePackage(c.getInt(c.getColumnIndex(BaseDatos.ID_LINE)));
                    pp.setNameLine(c.getString(c.getColumnIndex(BaseDatos.VNAMELINE)));
                    pp.setdDateCreate(c.getString(c.getColumnIndex(BaseDatos.FECHA_REGISTRO)));
                    pp.setSync(c.getInt(c.getColumnIndex(BaseDatos.SYNC)));
                    pp.setCajas(c.getInt(c.getColumnIndex("cajasRegistradas")));
                    //c.get

                    PrePalletList.add(pp);
                }


            }else{
                Toast.makeText(nContext, "No se encontro preppalet en la tablet", Toast.LENGTH_LONG).show();
            }

            c.close();
            db.cerrar();*/
           // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();

            //  JSONObject json;
            try {
                JSONArray JSONArrayPrePallets = new JSONArray(res);

                //adaptadorPP.notifyDataSetChanged();

                if(JSONArrayPrePallets.length() > 0){
                    String nPlanta = "";

                    for(int  i = 0; i< JSONArrayPrePallets.length(); i++) {

                        ArrayList<Linea> LinesPP = new ArrayList<Linea>();
                        ArrayList<cases> CaseList = new ArrayList<cases>();
                        ArrayList<Folio> FolioList = new ArrayList<>();

                        PrePallet pp = new PrePallet();
                        JSONObject row = JSONArrayPrePallets.getJSONObject(i);

                        Linea l = new Linea();
                        String lineasPP[] = row.getString("idLinePackage").split(",");
                        String lineasGP[] = row.getString("idLineGP").split(",");
                        String nomLinea[] = row.getString("vNameLine").split(", ");

                        for(int j = 0; j < lineasPP.length; j++){
                            l.setIdLinea(Integer.parseInt(lineasPP[j]));
                            l.setIdGPLinea(Integer.parseInt(lineasGP[j]));
                            l.setNombreLinea(nomLinea[j]);
                            l.setActive(true);

                            LinesPP.add(l);
                        }

                        pp.setIdPrePallet(row.getInt("idPrePallet"));
                        pp.setIdPrePalletTablet(row.getInt("idPrePalletTablet"));
                        pp.setIdFarm(row.getInt("idFarm"));
                        //pp.setIdLinePackage(row.getInt("idLinePackage"));
                        pp.setLine(LinesPP);
                        pp.setvPromotion(row.getString("vPromotion"));

                        //pp.setIdGPLine(row.getString("idLineGP"));
                        pp.setWeek(row.getString("Wk"));
                        pp.setDay(row.getString("DY"));
                        pp.setHH(row.getString("HH"));

                        pp.setvSize(row.getString("vSize"));
                        pp.setvSKU(row.getString("vSKU"));
                        pp.setActive(row.getBoolean("bActive"));
                        pp.setdDateCreate(row.getString("createdDate"));
                        pp.setdHourCreated(row.getString("hora"));
                        pp.setFullDateCreated(row.getString("dDateCraete"));
                        pp.setPlantaName(row.getString("plantaName"));
                        pp.setNameLine(row.getString("vNameLine"));
                        pp.setCajas(row.getInt("cajasRegistradas"));
                        pp.setPromoDesc(row.getString("PromoDesc"));
                        pp.setCasesPerPallet(row.getInt("casesPerPallet"));
                        pp.setvPalletCodeEX(row.getString("pallletCode"));
                        pp.setvUnicSessionKey(row.getString("vUnicSesionKey"));
                        pp.setvPalletID(row.getString("vPalletID"));
                        pp.setIdGP(row.getString("idGP").equalsIgnoreCase("null") ? "" : row.getString("idGP"));
                        pp.setSync(1);

                        //Funcionalidad para lista de cajas
                        String b = row.getString("Boxes");
                        String boxes[] = b.split(",");

                        if(!b.equals("null")) {
                            for (int j = 0; j < boxes.length; j++) {
                                if(boxes[j].contains(":")){
                                    String box[] = boxes[j].split(":");

                                    cases Case = new cases();
                                    Case.setCodigoCase(box[0]);
                                    Case.setIdPrePallet(pp.getIdPrePallet());
                                    Case.setActive(true);

                                    /**************************/
                                    Case.setIdCasesDetails(Integer.parseInt(box[1]));
                                    Case.setUuidCasesDetails(box[2]);
                                    Case.setFolio(box[3]);
                                    /**************************/

                                    CaseList.add(Case);
                                }
                            }
                        }

                        pp.setCajasPrePallet(CaseList);
                        //Funcionalidad para lista de folios
                        String f = row.getString("Folios");
                        String folio[] = f.split(",");
                        if(folio.length > 0) {
                            for (int j = 0; j < folio.length; j++) {
                                if(folio[j].contains(":")) {
                                    String casesperfolio[] = folio[j].split(":");
                                    Folio codeFolio = new Folio();
                                    codeFolio.setFolioCode(casesperfolio[0]);
                                    codeFolio.setCajas(Integer.parseInt(casesperfolio[4]));
                                    //codeFolio.setIdProductLog(Integer.parseInt(casesperfolio[2]));
                                    codeFolio.setGreenHouse(casesperfolio[2]);
                                    codeFolio.setCaseCodeHeader(casesperfolio[3]);

                                    /*************************/
                                    codeFolio.setCajasSeleccionadas(Integer.parseInt(casesperfolio[1]));
                                    /*************************/

                                    FolioList.add(codeFolio);
                                }
                            }
                            pp.setFolioPerPallet(FolioList);
                        }

                        PrePalletList.add(pp);

                        nPlanta = row.getString("plantaName");
                    }

                    BaseDatos bd = new BaseDatos(nContext);
                    bd.abrir();
                    linea.setText(bd.cpdb.getNameLine(idLine) + " - " + nPlanta);
                    bd.cerrar();

                    tvLinea.setVisibility(tvLinea.VISIBLE);
                }else{
                    Toast.makeText(nContext, "No se encontro preppalet en el servidor", Toast.LENGTH_LONG).show();

                  /*  tvLinea.setVisibility(tvLinea.INVISIBLE);
                    String[] notFound = new String[1];
                    notFound[0] = "No se encontraron prepallets!";
                    ArrayAdapter<String> notFounAdapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, notFound);
                    listViewPrePallet.setAdapter(notFounAdapter);

                    linea.setText("No se encontraron prepallets para la codificación leida!!");*/

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            if(PrePalletList.size() > 0){
                adaptadorPP = new listPPAdapter(nContext, PrePalletList);

                listViewPrePallet.setAdapter(adaptadorPP);
            } else {
                tvLinea.setVisibility(tvLinea.INVISIBLE);
                String[] notFound = new String[1];
                notFound[0] = "No se encontraron prepallets!";
                ArrayAdapter<String> notFounAdapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, notFound);
                listViewPrePallet.setAdapter(notFounAdapter);

                linea.setText("No se encontraron prepallets para la codificación leida!!");
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

    public static class AysnTaskgetNotAsignPallets extends AsyncTask<String, Integer, String> {
        public String url;
        private ProgressDialog pd;
        private String sku, cajas, promotion, size, farm;

        public AysnTaskgetNotAsignPallets(String url, String sku, String cajas, String promotion, String size, String farm){
            this.url = url;
            this.sku = sku;
            this.cajas = cajas;
            this.promotion = promotion;
            this.size = size;
            this.farm = farm;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Cargando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="", step="0";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sku", sku));
                params.add(new BasicNameValuePair("cajas", cajas));
                params.add(new BasicNameValuePair("promotion", promotion));
                params.add(new BasicNameValuePair("size", size));
                params.add(new BasicNameValuePair("farm", farm));

                step="2";
                HttpPost httppostreq = new HttpPost(url);
                step="3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step="4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step="5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step="6";
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pd.setProgress(values[0]);
            Toast.makeText(nContext, values[0]+" -- ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String res) {
            // Toast.makeText(GuardarRiego.this, res, Toast.LENGTH_LONG).show();
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();

            //  JSONObject json;
            try {

                JSONObject json = new JSONObject(res);
                JSONArray jsonPallet = json.getJSONArray("table1");
                PalletList.clear();
                if(jsonPallet.length() > 0){

                    for(int  i = 0; i< jsonPallet.length(); i++) {

                        Pallet p = new Pallet();
                        JSONObject row = jsonPallet.getJSONObject(i);
                        p.setSku(row.getString("SKU"));
                        p.setPallet(row.getString("Pallet"));
                        p.setCases(row.getInt("Cases"));
                        p.setLibras(row.getDouble("Libras"));
                        p.setRecordDate(row.getString("RecordDate"));
                        p.setIdPackFarm(row.getInt("PackFarm"));
                        p.setNamePackFarm(row.getString("NameFarm"));
                        p.setNameLines(row.getString("Linea"));
                        p.setGreenHouse(row.getString("GreenHouse"));

                        PalletList.add(p);
                        //pd.setMessage("Cargando... Por favor espere!! - "+(i+1)+" / "+jsonPallet.length()+" --> "+p.getPallet());
                    }

                    adaptadorPallet = new listPalletAdapter(nContext, PalletList);

                    listViewPallet.setAdapter(adaptadorPallet);

                    palletListTitle.setText(nContext.getString(R.string.PalletList) +"("+ (PalletList.size()) +")" );

                }else{
                    Toast.makeText(nContext, R.string.notPallets, Toast.LENGTH_LONG).show();
                    new PopUp(nContext, nContext.getString(R.string.notPallets), PopUp.POPUP_INCORRECT).showPopUp();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

}
