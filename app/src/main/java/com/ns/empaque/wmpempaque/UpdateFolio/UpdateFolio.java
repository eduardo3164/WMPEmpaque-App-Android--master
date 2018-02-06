package com.ns.empaque.wmpempaque.UpdateFolio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsigPrefolio2Folio.Prefolio;
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
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier.calderon on 27/03/2017.
 */

public class UpdateFolio {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static LinearLayout btnAtras, btnHome, btnScanFolio, btnGoFolio, btnScanPrefolio, btnMergeFolios, btnGoPrefolio;
    private static EditText et_Folio, et_PreFolio;
    private static RadioGroup consolidaMethod;
    private static TextView txtFolio, txtFolioQA, txtGh;
    private static SharedPreferences sharedpreferences;
    private static RecyclerView rvPreFolio;

    private static ArrayList<Prefolio> al_Prefolio;
    private static String GoodFolio = "-1", GHFolio ="-1", idQAFolio = "-1", GHPre = "-1", idQAPre = "-1", fechaPreFolio = "-1", fechaFolio = "-1";
    private static int disperso = 1;

    public UpdateFolio(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
    }

    public static void setView() {

        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_update_folio, null, true);
        config.updateContent(content, v);

        btnAtras = (LinearLayout) v.findViewById(R.id.btnAtras);
        btnHome =  (LinearLayout) v.findViewById(R.id.btnHome);
        btnScanFolio = (LinearLayout) v.findViewById(R.id.btnScanFolio);
        btnGoFolio = (LinearLayout) v.findViewById(R.id.btnGoFolio);
        btnScanPrefolio = (LinearLayout) v.findViewById(R.id.btnScanPrefolio);
        btnMergeFolios = (LinearLayout) v.findViewById(R.id.btnMergeFolios);
        btnGoPrefolio = (LinearLayout) v.findViewById(R.id.btnGoPrefolio);

        et_Folio = (EditText) v.findViewById(R.id.et_Folio);
        et_PreFolio = (EditText) v.findViewById(R.id.et_PreFolio);

        txtFolio = (TextView) v.findViewById(R.id.txtFolio);
        txtFolioQA = (TextView) v.findViewById(R.id.txtFolioQA);
        txtGh = (TextView) v.findViewById(R.id.txtGh);

        rvPreFolio = (RecyclerView) v.findViewById(R.id.rvPreFolio);
        al_Prefolio = new ArrayList<>();

        consolidaMethod = (RadioGroup) v.findViewById(R.id.consolidaMethod);

        /*Regreso las variables*/
        GHPre = "-1";
        idQAPre = "-1";
        fechaPreFolio = "-1";
        GoodFolio = "-1";
        GHFolio ="-1";
        idQAFolio = "-1";
        fechaFolio = "-1";
        disperso = 1;

        eventsBTN();
    }

    private static void eventsBTN() {
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            new AlertDialog.Builder(nContext)
                .setTitle("Salir?")
                .setMessage("Seguro que deseas salir de este modulo?")
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        content.removeAllViewsInLayout();
                        config.backContent(content);
                        WMPEmpaque.tipoApp = 0;
                        WMPEmpaque.setAvisos(nContext);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.naturesweet)
                .show();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(nContext)
                        .setTitle("Salir?")
                        .setMessage("Seguro que deseas salir de este modulo?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                content.removeAllViewsInLayout();
                                config.backContent(content);
                                WMPEmpaque.tipoApp = 0;
                                WMPEmpaque.setAvisos(nContext);

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.naturesweet)
                        .show();
            }
        });

        btnScanFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WMPEmpaque.prefolio = 0;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        btnScanPrefolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WMPEmpaque.prefolio = 1;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();

            }
        });

        btnGoFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.validaString(et_Folio.getText().toString().trim(), nContext) == 1) {//Si es folio
                    checkFolio(et_Folio.getText().toString().trim());
                } else {
                    Toast.makeText(nContext, "El codigo no parece un folio", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnGoPrefolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (config.validaString(et_Folio.getText().toString().trim(), nContext) == 1) {//Si es folio
                    checkPreFolio(et_PreFolio.getText().toString().trim());
                } else {
                    Toast.makeText(nContext, "El codigo no parece un Pre-folio", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnMergeFolios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCorrect()){

                    new AlertDialog.Builder(nContext)
                            .setTitle("Seguro?")
                            .setMessage("Estas seguro de asignar estos folios de Preharvest (Cajas : "+getSumCajas()+", Peso: "+getSumPeso()+") a un folio Normal ("+GoodFolio+")?")
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String jsonPrefolios = getJsonPrefolios();

                                    if(jsonPrefolios.compareTo("") == 0){
                                        new PopUp(nContext, "Hubo un error en obtener los prefolios. \n Intente de nuevo por favor. \n Si el problema persiste contacte al administrador del sistema.", "Error al obtener prefolios", PopUp.POPUP_INCORRECT).showPopUp();
                                    }else{
                                        Toast.makeText(nContext, "Combinando Folios", Toast.LENGTH_SHORT).show();
                                        // new PopUp(nContext, "Folio : "+GoodFolio+"\n"+"jsonPrefolios: "+jsonPrefolios, "ShowUp Data", PopUp.POPUP_OK).showPopUp();
                                        //new ATInsertWedgeProduct(config.rutaWebServerOmar + "/insertWedgeProduct", GoodFolio, sharedpreferences.getString("username", "jcalderon"), jsonPrefolios ).execute();
                                        mergePrefoliosWithFA(jsonPrefolios);
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.naturesweet)
                            .show();

                }else{
                    Toast.makeText(nContext, "Necesitas leer un Folio y leer al menos un folio de Preharvest", Toast.LENGTH_SHORT).show();
                    new PopUp(nContext,"Necesitas leer un Folio y leer al menos un folio de Preharvest", "Necesitas leer un folio y prefolio", PopUp.POPUP_INCORRECT).showPopUp();
                }
            }
        });


        consolidaMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch(i) {
                    case R.id.disperso:
                        disperso = 1;
                        break;
                    case R.id.noDisperso:
                        disperso = 0;
                        break;
                }
            }
        });
    }

    public static String getJsonPrefolios() {
        String jsonPrefolios="[";
        if(al_Prefolio.size() > 0){
            for(int i=0; i < al_Prefolio.size(); i++) {
                Prefolio pf = new Prefolio();
                pf = al_Prefolio.get(i);

                jsonPrefolios += "{\"vFolio\":\"" + pf.getvPrefolio()+ "\"},";
            }

            jsonPrefolios = jsonPrefolios.substring(0, jsonPrefolios.length() - 1);
            jsonPrefolios += "]";
            Log.d("Prefolios -- >", jsonPrefolios);
        }else{
            jsonPrefolios = "";
        }

        return jsonPrefolios;
    }

    public static double getSumPeso(){
        double Peso = 0.0;
        if(al_Prefolio.size() > 0){
            for(int i=0; i < al_Prefolio.size(); i++) {
                Prefolio pf = new Prefolio();
                pf = al_Prefolio.get(i);

                Peso += pf.getPeso();
            }
        }else{
            Peso = 0.0;
        }

        return Peso;
    }

    public static double getSumCajas(){
        double Caja = 0.0;
        if(al_Prefolio.size() > 0){
            for(int i=0; i < al_Prefolio.size(); i++) {
                Prefolio pf = new Prefolio();
                pf = al_Prefolio.get(i);

                Caja += pf.getCajas();
            }
        }else{
            Caja = 0.0;
        }

        return Caja;
    }

    private static boolean isCorrect(){
        if(GoodFolio.compareTo("") == 0 || al_Prefolio.isEmpty())
            return false;

        return true;
    }

    public static void checkFolio(String folio){
        new ATCheckFolio(config.rutaWebServerOmar + "/checkFolioConsolidacion", folio).execute();
        Toast.makeText(nContext, folio, Toast.LENGTH_SHORT).show();
    }

    public static void checkPreFolio(String preFolio){
        new ATCheckPreFolio(config.rutaWebServerOmar + "/checkPreFolioConsolidacion", preFolio).execute();
    }

    public static void mergePrefoliosWithFA(String jsonPrefolios){
        //new  ATMergeWedgeProduct(config.rutaWebServerOmar + "/mergeFolioConsolidacion", jsonPrefolios).execute();
        new  ATMergeWedgeProduct(config.rutaWebServerOmar + "/mergeFolioConsolidacionDispercion", jsonPrefolios).execute();


    }

    public static class ATMergeWedgeProduct extends AsyncTask<String, String, String>{

        public String url, jsonPrefolios;
        private ProgressDialog pd;
        private int step = 0;

        public ATMergeWedgeProduct(String url, String jsonPrefolios){
            this.url = url;
            this.jsonPrefolios = jsonPrefolios;
            pd = new ProgressDialog(nContext);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Combinando Folios... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vFolio", GoodFolio));
                params.add(new BasicNameValuePair("dispercion", disperso+""));
                params.add(new BasicNameValuePair("userName", sharedpreferences.getString("username", "DBTAPP")));
                params.add(new BasicNameValuePair("jsonPrefolios", jsonPrefolios));

                step=2;
                HttpPost httppostreq = new HttpPost(url);
                step=3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(nContext, s, Toast.LENGTH_SHORT).show();
            try{
                pd.dismiss();
            }catch(Exception e){

            }

            Log.d("wedMethod", s);

            try {
                JSONObject json = new JSONObject(s);
                JSONArray jsIndicator = json.getJSONArray("table1");
                if(jsIndicator.length() > 0) {
                    JSONObject rowIndicator = jsIndicator.getJSONObject(0);

                    if (rowIndicator.getInt("indicator") == 1) {
                       /* al_Prefolio.clear();
                        rvPreFolio.setLayoutManager(new LinearLayoutManager(nContext));
                        rvPreFolio.setAdapter(new prefolioAdapter(al_Prefolio));*/
                        Toast.makeText(nContext, "Combinacion realizada con exito!", Toast.LENGTH_SHORT).show();
                        new PopUp(nContext, "Combinación realizada con exito", "Operación exitosa", PopUp.POPUP_OK).showPopUp();
                    }else{
                        Toast.makeText(nContext, "Algo fue mal con la conbinación de los folios", Toast.LENGTH_SHORT).show();
                        JSONArray jsMsg = json.getJSONArray("table2");

                        JSONObject rowMsg = jsMsg.getJSONObject(0);

                        new PopUp(nContext, rowMsg.getString("msg"), "No fue posible insertar estos Pre-Folios", PopUp.POPUP_INFORMATION).showPopUp();

                    }
                }else{
                    Toast.makeText(nContext, "Algo fue mal con la conbinación de los folios", Toast.LENGTH_SHORT).show();
                    al_Prefolio.clear();
                    rvPreFolio.setLayoutManager(new LinearLayoutManager(nContext));
                    rvPreFolio.setAdapter(new prefolioAdapter(al_Prefolio));
                }

            }catch(Exception e){
                Toast.makeText(nContext, "Algo fue mal con la conbinación de los folios", Toast.LENGTH_SHORT).show();
                al_Prefolio.clear();
                rvPreFolio.setLayoutManager(new LinearLayoutManager(nContext));
                rvPreFolio.setAdapter(new prefolioAdapter(al_Prefolio));
            }

        }
    }

    public static class ATCheckFolio extends AsyncTask<String, Integer, String> {
        public String url, folio;
        private ProgressDialog pd;
        private int step = 0;

        public ATCheckFolio(String url, String folio){
            this.url = url;
            this.folio = folio;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Validando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vFolio", this.folio));
                params.add(new BasicNameValuePair("GH", GHPre));
                params.add(new BasicNameValuePair("idQA", idQAPre));
                params.add(new BasicNameValuePair("datetime", fechaPreFolio));

                step=2;
                HttpPost httppostreq = new HttpPost(url);
                step=3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }


        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();

            try {

                JSONObject json = new JSONObject(res);
                JSONArray jsIndicator = json.getJSONArray("table1");

                if(jsIndicator.length() > 0){
                    JSONObject rowIndicator = jsIndicator.getJSONObject(0);

                    if(rowIndicator.getInt("indicator") == 1){
                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0){
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);

                            //Si aun no se leen prefolios
                            if(GHPre.compareToIgnoreCase("-1") == 0) {
                                txtFolio.setText(rowInfo.getString("Folio"));
                                txtFolioQA.setText(rowInfo.getString("Calidad"));
                                txtGh.setText(rowInfo.getString("GH"));

                                GoodFolio = rowInfo.getString("Folio");
                                idQAFolio = rowInfo.getString("idQA");
                                GHFolio = rowInfo.getString("GH");
                                fechaFolio = rowInfo.getString("fecha");

                                JSONArray jsPrefolios = json.getJSONArray("table3");

                                if(jsPrefolios.length() > 0){
                                    showPrefolio(jsPrefolios);
                                }


                            }else{//si ya se leyeron prefolios
                                String Mensaje = "";
                                if(GHPre.compareToIgnoreCase(rowInfo.getString("GH")) != 0){
                                   Mensaje += "El folio leido debe de ser del mismo invernadero que los prefolios. \n\n";
                                }

                                if(idQAPre.compareToIgnoreCase(rowInfo.getString("idQA")) != 0){
                                    Mensaje += "El folio leido debe de ser de la misma calidad que los prefolios. \n\n";
                                }

                                if(Mensaje.compareToIgnoreCase("") == 0){
                                    txtFolio.setText(rowInfo.getString("Folio"));
                                    txtFolioQA.setText(rowInfo.getString("Calidad"));
                                    txtGh.setText(rowInfo.getString("GH"));

                                    GoodFolio = rowInfo.getString("Folio");
                                    idQAFolio = rowInfo.getString("idQA");
                                    GHFolio = rowInfo.getString("GH");
                                    fechaFolio = rowInfo.getString("fecha");

                                    JSONArray jsPrefolios = json.getJSONArray("table3");

                                    if(jsPrefolios.length() > 0){
                                        showPrefolio(jsPrefolios);
                                    }

                                }else {
                                    new PopUp(nContext, Mensaje, "Folio no permitido", PopUp.POPUP_INFORMATION).showPopUp();
                                }

                            }

                        }else{
                            new PopUp(nContext, "No hay información para el folio", "Folio no permitido", PopUp.POPUP_INFORMATION).showPopUp();
                        }

                    }else{
                        Toast.makeText(nContext, "Folio no permitido", Toast.LENGTH_LONG).show();

                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0){
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);
                            String txtMessage = "";
                            txtMessage += rowInfo.getString("msg");
                            new PopUp(nContext, txtMessage, "Folio no permitido", PopUp.POPUP_INFORMATION).showPopUp();
                        }

                    }

                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            if(step < 6){
                new PopUp(nContext, "El servicio Web no fue alcanzado. \n Revisa tu conexión a Internet", "Error de Conexion", PopUp.POPUP_INCORRECT).showPopUp();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }

        private void showPrefolio(JSONArray jsPreFolio) {
            try {
                al_Prefolio.clear();
                String Mensaje = "";
                for (int i = 0; i < jsPreFolio.length(); i++) {

                    JSONObject rowPF = jsPreFolio.getJSONObject(i);
                    Prefolio pf = new Prefolio();

                    pf.setvPrefolio(rowPF.getString("PreFolio"));
                    pf.setvGreenHouse(rowPF.getString("GreenHouse"));
                    pf.setPeso(rowPF.getDouble("Peso"));
                    pf.setFechaCreacion(rowPF.getString("FechaCreacion"));
                    pf.setQAName(rowPF.getString("QAName"));
                    pf.setCajas(rowPF.getDouble("Cajas"));
                    pf.setIdQA(rowPF.getInt("idQA"));
                    pf.setSecciones(rowPF.getString("secciones"));

                    al_Prefolio.add(pf);
                }

                rvPreFolio.setLayoutManager(new LinearLayoutManager(nContext));
                rvPreFolio.setAdapter(new prefolioAdapter(al_Prefolio));
            }catch(Exception e){

            }
        }
    }

    public static class ATCheckPreFolio extends AsyncTask<String, Integer, String> {
        public String url, preFolio;
        private ProgressDialog pd;
        private int step=0;

        public ATCheckPreFolio(String url, String preFolio){
            this.url = url;
            this.preFolio = preFolio;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Validando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();


            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vFolio", this.preFolio));
                params.add(new BasicNameValuePair("GH", GHFolio));
                params.add(new BasicNameValuePair("idQA", idQAFolio));
                params.add(new BasicNameValuePair("datetime", fechaFolio));

                step=2;
                HttpPost httppostreq = new HttpPost(url);

                step=3;
                UrlEncodedFormEntity p = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httppostreq.setEntity(p);


                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }


        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();

            try {

                JSONObject json = new JSONObject(res);

                JSONArray jsIndicator = json.getJSONArray("table1");

                if(jsIndicator.length() > 0) {
                    JSONObject rowIndicator = jsIndicator.getJSONObject(0);

                    if (rowIndicator.getInt("indicator") == 1) {

                        JSONArray jsPreFolio = json.getJSONArray("table2");

                        if (jsPreFolio.length() > 0) {
                            al_Prefolio.clear();
                            String Mensaje = "";
                            for (int i = 0; i < jsPreFolio.length(); i++) {
                                JSONObject rowPF = jsPreFolio.getJSONObject(i);

                                if (GHFolio.compareToIgnoreCase("-1") == 0) {
                                    Prefolio pf = new Prefolio();

                                    pf.setvPrefolio(rowPF.getString("PreFolio"));
                                    pf.setvGreenHouse(rowPF.getString("GreenHouse"));
                                    pf.setPeso(rowPF.getDouble("Peso"));
                                    pf.setFechaCreacion(rowPF.getString("FechaCreacion"));
                                    pf.setQAName(rowPF.getString("QAName"));
                                    pf.setCajas(rowPF.getDouble("Cajas"));
                                    pf.setIdQA(rowPF.getInt("idQA"));
                                    pf.setSecciones(rowPF.getString("secciones"));

                                    al_Prefolio.add(pf);

                                    GHPre = rowPF.getString("GreenHouse");
                                    idQAPre = rowPF.getString("idQA");
                                    fechaPreFolio = rowPF.getString("FechaCreacion");
                                } else {
                                    if (GHFolio.compareToIgnoreCase(rowPF.getString("GreenHouse")) == 0) {
                                        if (idQAFolio.compareToIgnoreCase(rowPF.getString("idQA")) == 0) {

                                            Prefolio pf = new Prefolio();

                                            pf.setvPrefolio(rowPF.getString("PreFolio"));
                                            pf.setvGreenHouse(rowPF.getString("GreenHouse"));
                                            pf.setPeso(rowPF.getDouble("Peso"));
                                            pf.setFechaCreacion(rowPF.getString("FechaCreacion"));
                                            pf.setQAName(rowPF.getString("QAName"));
                                            pf.setCajas(rowPF.getDouble("Cajas"));
                                            pf.setIdQA(rowPF.getInt("idQA"));
                                            pf.setSecciones(rowPF.getString("secciones"));

                                            al_Prefolio.add(pf);

                                            GHPre = rowPF.getString("GreenHouse");
                                            idQAPre = rowPF.getString("idQA");
                                            fechaPreFolio = rowPF.getString("FechaCreacion");

                                        } else {
                                            if (Mensaje.compareToIgnoreCase("") == 0)
                                                Mensaje = "El Pre-folio " + rowPF.getString("PreFolio");
                                            else
                                                Mensaje = " No Tiene la misma calidad que el folio al que se quiere asignar\n\n";
                                        }
                                    } else {
                                        if (Mensaje.compareToIgnoreCase("") == 0)
                                            Mensaje = "El Pre-folio " + rowPF.getString("PreFolio");
                                        else
                                            Mensaje = " No pertenece al mismo invernadero que el folio al que se quiere asignar \n";
                                    }
                                }


                                //txtFolioQA.setText(rowPF.getString("QAName"));

                            }

                            if (Mensaje.compareToIgnoreCase("") != 0) {
                                new PopUp(nContext, Mensaje, "Folio (s) No permitido (s)", PopUp.POPUP_INFORMATION).showPopUp();
                                GHPre = "-1";
                                idQAPre = "-1";
                                fechaPreFolio = "-1";
                            }

                            rvPreFolio.setLayoutManager(new LinearLayoutManager(nContext));
                            rvPreFolio.setAdapter(new prefolioAdapter(al_Prefolio));
                            /*
                            PFAdapter = new PreFolioAdapter(nContext, al_Prefolio);
                            lv_PreFolios.setAdapter(PFAdapter);
                            */
                        } else {
                            new PopUp(nContext, "El folio leído no es de PreHarvest o ya ha sido asignado a otro folio. \n \nSi el folio si es de Preharvest, por favor contacte a la persona que lo cosecho para que sincronice su tableta. \n\nRecuerda hay que darle calidad al folio para poderlo asignar a un folio nuevo  \n\n Si el problema persisrte, contacte al administrador del sistema.", "Folio no detectado en preHarvest", PopUp.POPUP_INCORRECT).showPopUp();
                        }
                    } else { //indicator = 0
                        Toast.makeText(nContext, "Pre-folio no permitido", Toast.LENGTH_LONG).show();

                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0){
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);
                            String txtMessage = "";
                            txtMessage += rowInfo.getString("msg");
                            new PopUp(nContext, txtMessage, "Pre-folio no permitido", PopUp.POPUP_INFORMATION).showPopUp();
                        }

                    }
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            if(step < 6){
                new PopUp(nContext, "No se alcanzó el servicio de empaque. \n\nPor favor apague y prende el WIFI de su tableta.\n\n Si el problema persiste por favor contacto al soporte de su planta.", "Error de conexión a internet",PopUp.POPUP_INCORRECT).showPopUp();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

}
