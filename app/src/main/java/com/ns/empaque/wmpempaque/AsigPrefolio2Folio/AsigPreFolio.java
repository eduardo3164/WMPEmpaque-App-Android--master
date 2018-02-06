package com.ns.empaque.wmpempaque.AsigPrefolio2Folio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by jcalderon on 12/01/2017.
 */

public class AsigPreFolio {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static PreFolioAdapter PFAdapter;

    private static LinearLayout btnAtras, btnHome, btnScanFolio,
            btnScanPrefolio, btnSendWedge, btnGoFolio, btnGoPrefolio;
    private static TextView txtFolio, txtFolioQA;
    private static ListView lv_PreFolios;
    private static String GoodFolio ="";
    private static SharedPreferences sharedpreferences;
    private static EditText et_Folio, et_PreFolio;

    private static ArrayList<Prefolio> al_Prefolio;

    public AsigPreFolio(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
    }

    public static void setView() {

        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_asig_prefolio, null, true);
        config.updateContent(content, v);

        btnAtras = (LinearLayout) v.findViewById(R.id.btnAtras);
        btnHome =  (LinearLayout) v.findViewById(R.id.btnHome);
        btnScanFolio = (LinearLayout) v.findViewById(R.id.btnScanFolio);
        btnScanPrefolio = (LinearLayout) v.findViewById(R.id.btnScanPrefolio);
        btnSendWedge = (LinearLayout) v.findViewById(R.id.btnSendWedge);
        btnGoFolio = (LinearLayout) v.findViewById(R.id.btnGoFolio);
        btnGoPrefolio = (LinearLayout) v.findViewById(R.id.btnGoPrefolio);

        et_Folio = (EditText) v.findViewById(R.id.et_Folio);
        et_PreFolio = (EditText) v.findViewById(R.id.et_PreFolio);

        txtFolio = (TextView) v.findViewById(R.id.txtFolio);
        txtFolioQA = (TextView) v.findViewById(R.id.txtFolioQA);

        lv_PreFolios = (ListView) v.findViewById(R.id.lv_PreFolios);

        al_Prefolio = new ArrayList<>();

        eventsBTN();
    }

    private static void eventsBTN() {
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
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
            public void onClick(View v) {
                if (config.validaString(et_PreFolio.getText().toString().trim(), nContext) == 1) {//Si es folio
                    checkPreFolio(et_PreFolio.getText().toString().trim());
                } else {
                    Toast.makeText(nContext, "El codigo no parece un Pre-folio", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSendWedge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isCorrect()){
                    new AlertDialog.Builder(nContext)
                            .setTitle("Seguro?")
                            .setMessage("Estas seguro de asignar estos folios de Preharvest a un folio Normal ("+GoodFolio+"). Al presionar continuar ya no podrás realizar modificaciones.")
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String jsonPrefolios = getJsonPrefolios();

                                    if(jsonPrefolios.compareTo("") == 0){
                                        new PopUp(nContext, "Hubo un error en obtener los prefolios. \n Intente de nuevo por favor. \n Si el problema persiste contacte al administrador del sistema.", "Error al obtener prefolios", PopUp.POPUP_INCORRECT).showPopUp();
                                    }else{
                                        Toast.makeText(nContext, "Sending Folio to Wedge Production", Toast.LENGTH_SHORT).show();
                                        // new PopUp(nContext, "Folio : "+GoodFolio+"\n"+"jsonPrefolios: "+jsonPrefolios, "ShowUp Data", PopUp.POPUP_OK).showPopUp();
                                        new ATInsertWedgeProduct(config.rutaWebServerOmar + "/insertWedgeProduct", GoodFolio, sharedpreferences.getString("username", "jcalderon"), jsonPrefolios ).execute();
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

    }

    private static boolean isCorrect(){
        if(GoodFolio.compareTo("") == 0 || al_Prefolio.isEmpty())
            return false;

        return true;
    }

    public static void checkFolio(String folio){
        new ATCheckFolio(config.rutaWebServerOmar + "/checkFolio", folio).execute();
    }

    public static void checkPreFolio(String preFolio){
        new ATCheckPreFolio(config.rutaWebServerOmar + "/checkPreFolio", preFolio).execute();
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
                JSONArray jsFolio = json.getJSONArray("table1");

                if(jsFolio.length() > 0){
                    JSONObject rowIndicator = jsFolio.getJSONObject(0);

                    if(rowIndicator.getInt("indicator") == 1){
                        txtFolio.setText(rowIndicator.getString("folio"));
                        GoodFolio = rowIndicator.getString("folio");
                    }else{
                        Toast.makeText(nContext, "Este folio ya existe en Cosecha o en Arribo", Toast.LENGTH_LONG).show();

                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0){
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);
                            String txtMessage = "";
                            txtMessage += rowInfo.getString("msg");
                            new PopUp(nContext, txtMessage, "Este folio ya existe en Cosecha o en Arribo", PopUp.POPUP_INFORMATION).showPopUp();
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
                JSONArray jsPreFolio = json.getJSONArray("table1");

                if(jsPreFolio.length() > 0) {
                    al_Prefolio.clear();
                    for(int i = 0; i<jsPreFolio.length(); i++) {
                        JSONObject rowPF = jsPreFolio.getJSONObject(i);

                        Prefolio pf = new Prefolio();

                        pf.setvPrefolio(rowPF.getString("PreFolio"));
                        pf.setvGreenHouse(rowPF.getString("GreenHouse"));
                        pf.setPeso(rowPF.getDouble("Peso"));
                        pf.setFechaCreacion(rowPF.getString("FechaCreacion"));
                        pf.setQAName(rowPF.getString("QAName"));
                        pf.setCajas(rowPF.getDouble("Cajas"));

                        al_Prefolio.add(pf);

                        txtFolioQA.setText(rowPF.getString("QAName"));

                    }


                    PFAdapter = new PreFolioAdapter(nContext, al_Prefolio);
                    lv_PreFolios.setAdapter(PFAdapter);

                }else{
                    new PopUp(nContext, "El folio leído no es de PreHarvest o ya ha sido asignado a otro folio. \n \nSi el folio si es de Preharvest, por favor contacte a la persona que lo cosecho para que sincronice su tableta. \n\nRecuerda hay que darle calidad al folio para poderlo asignar a un folio nuevo  \n\n Si el problema persisrte, contacte al administrador del sistema.", "Folio no detectado en preHarvest",PopUp.POPUP_INCORRECT).showPopUp();
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

    public static class ATInsertWedgeProduct extends AsyncTask<String, Integer, String> {
        public String url, vFolio, user, json;
        private ProgressDialog pd;
        private int step=0;

        public ATInsertWedgeProduct(String url, String vFolio, String user, String json){
            this.url = url;
            this.vFolio = vFolio;
            this.user = user;
            this.json = json;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Insertando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vFolio", this.vFolio));
                params.add(new BasicNameValuePair("userName", this.user));
                params.add(new BasicNameValuePair("jsonPrefolios", this.json));

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
                JSONArray jsFolio = json.getJSONArray("table1");

                if(jsFolio.length() > 0){
                    JSONObject rowIndicator = jsFolio.getJSONObject(0);

                    if(rowIndicator.getInt("indicator") == 1){

                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0) {
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);

                            new PopUp(nContext, "El folio " + rowInfo.getString("Folio") + " fue insertado correctamente", "Folio insertado correctamente", PopUp.POPUP_OK).showPopUp();
                            //limpiamos preFolios y folio
                            al_Prefolio.clear();
                            PFAdapter.notifyDataSetChanged();
                            GoodFolio = "";
                            txtFolio.setText("---");
                            txtFolioQA.setText("---");

                        }else{
                            new PopUp(nContext, "Algo fue mal con la inserción del folio, por favor vuelta a intentar...", "Error en la inserción",PopUp.POPUP_INCORRECT).showPopUp();
                        }

                    }else{
                        Toast.makeText(nContext, "Este folio ya existe en Cosecha o en Arribo", Toast.LENGTH_LONG).show();

                        JSONArray jsFolioInfo = json.getJSONArray("table2");

                        if(jsFolioInfo.length() > 0){
                            JSONObject rowInfo = jsFolioInfo.getJSONObject(0);
                            String txtMessage = "";
                            txtMessage += rowInfo.getString("msg");
                            new PopUp(nContext, txtMessage, "Este folio ya existe en Cosecha o en Arribo", PopUp.POPUP_INFORMATION).showPopUp();
                        }


                    }

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
