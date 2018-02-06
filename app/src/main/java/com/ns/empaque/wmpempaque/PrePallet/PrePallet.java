package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.AddPrePallet;
import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jcalderon on 27/04/2016.
 */
public class PrePallet {

    private static ArrayList<HashMap<String, String>> boxes, folios;
    private static String Farm, /*Line, */SKU, Active, idPrePallet, size, promotion;
    private static int desgrane;
    private static ArrayList<Linea> Lines;
    private static Activity nContext;

    public static String getSize() {
        return size;
    }

    public static void setSize(String size) {
        PrePallet.size = size;
    }

    public static String getPromotion() {
        return promotion;
    }

    public static void setPromotion(String promotion) {
        PrePallet.promotion = promotion;
    }

    public static ArrayList<HashMap<String, String>> getFolios() {
        return folios;
    }

    public static void setFolios(ArrayList<HashMap<String, String>> folios) {
        PrePallet.folios = folios;
    }

    public PrePallet(Activity nContext){
        setnContext(nContext);
    }

    public static String getActive() {
        return Active;
    }

    public static void setActive(String active) {
        Active = active;
    }

    public static ArrayList<HashMap<String, String>> getBoxes() {
        return boxes;
    }

    public static void setBoxes(ArrayList<HashMap<String, String>> boxes) {
        PrePallet.boxes = boxes;
    }

    public static String getFarm() {
        return Farm;
    }

    public static void setFarm(String farm) {
        Farm = farm;
    }

    public static String getIdPrePallet() {
        return idPrePallet;
    }

    public static void setIdPrePallet(String idPrePallet) {
        PrePallet.idPrePallet = idPrePallet;
    }

    public static int getDesgrane(){
        return desgrane;
    }

    public static void setDesgrane(int desgrane) {
        PrePallet.desgrane = desgrane;
    }

    public static ArrayList<Linea> getLines() {
        return Lines;
    }

    public static void setLine(ArrayList<Linea> lines) {
        Lines = lines;
    }

    /*public static String getLine() {
        return Line;
    }

    public static void setLine(String line) {
        Line = line;
    }*/

    public static Activity getnContext() {
        return nContext;
    }

    public void setnContext(Activity nContext) {
        this.nContext = nContext;
    }

    public static String getSKU() {
        return SKU;
    }

    public static void setSKU(String SKU) {
        PrePallet.SKU = SKU;
    }

    public int savePrePallet(){

        if(//boxes.size() > 0 &&
                Farm != null &&
                Lines != null &&
                SKU != null &&
                Active != null){

            BaseDatos bd = new BaseDatos(nContext);
            bd.abrir();
            if (bd.cpdb.insertPrePallet(getFarm(), getLines(), getSKU(), getActive(), getSize(), getPromotion(), getDesgrane()) > 0) {
                bd.cerrar();
              return 1;
            } else {
                bd.cerrar();
               return 0;
            }
        }else
            return 0;
    }

    public int updatePrePallet() {

        Log.d("Farm", getFarm() + "");


        if(//getBoxes().size() > 0 &&
                getFarm() != null &&
                getLines() != null &&
                getSKU() != null &&
                getActive() != null){
            BaseDatos bd = new BaseDatos(nContext);
            bd.abrir();
            if(bd.cpdb.updatePrePalletr(getBoxes(), getFolios(), getFarm(), getLines(), getSKU(), getActive(), getIdPrePallet(), getSize(), getPromotion(), getDesgrane()) > 0){
                bd.cerrar();
                return 1;
            }else{
                bd.cerrar();
                return 0;
            }
        }else
            return 0;
    }

    public static void sincronizar() {
        BaseDatos bd = new BaseDatos(getnContext());
        bd.abrir();
        String[][] datosPrepallet = bd.cpdb.getPrePalletsToSync();
        String[][] lineasPrepallet = bd.cpdb.getLinesPrePalletsToSync();

        bd.cerrar();


        if (datosPrepallet.length > 0) {
            String resultsPrePallet = "[";

            for (int i = 0; i < datosPrepallet.length; i++) {
                resultsPrePallet += "{\"idPrePalletServer\":" + (datosPrepallet[i][0] != null ? "\"" + datosPrepallet[i][0] + "\"" : null) +
                                    ", \"idPrePalletTablet\":\"" + datosPrepallet[i][1] +
                                    "\", \"idFarm\":\"" + datosPrepallet[i][2] +
                                    "\", \"idLinePackage\":\"" + datosPrepallet[i][3] +
                                    "\", \"vSKU\":\"" + datosPrepallet[i][4] +
                                    "\", \"bActive\":\"" + datosPrepallet[i][5] +
                                    "\", \"dDateCraete\":\"" + datosPrepallet[i][6] +
                                    "\", \"dDateUpdate\":" + (datosPrepallet[i][7] != null ? "\"" + datosPrepallet[i][7] + "\"" : null) +
                                    ", \"vUserCreate\":" + (datosPrepallet[i][8] != null ? "\"" + datosPrepallet[i][8] + "\"" : null) +
                                    ", \"vUserUpdate\":" + (datosPrepallet[i][9] != null ? "\"" + datosPrepallet[i][9] + "\"" : null) +
                                    ", \"vPromotion\":\"" + datosPrepallet[i][10] +
                                    "\", \"rDesgrane\":" + datosPrepallet[i][13] +
                                    ", \"vSize\":\"" + datosPrepallet[i][11] +
                                    "\", \"vIdTabletMac\":\"" + datosPrepallet[i][14] +
                                    "\", \"vUnicSesionKey\":\"" + datosPrepallet[i][12] +
                                    "\"},";
            }

            resultsPrePallet = resultsPrePallet.substring(0, resultsPrePallet.length() - 1);
            resultsPrePallet += "]";

            Log.d("PrePallet -- >", resultsPrePallet);

            String resultsLinesPrePallet = "[";

            for (int i = 0; i < lineasPrepallet.length; i++) {
                resultsLinesPrePallet += "{\"idPrePalletTablet\":"  + lineasPrepallet[i][0] + "," +
                                          "\"vUUIDPrePallet\":\""   + lineasPrepallet[i][1] + "\"," +
                                          "\"idLinea\":"            + lineasPrepallet[i][2] + "," +
                                          "\"vUUID\":\""            + lineasPrepallet[i][3] + "\"" +
                                          "},";
            }

            resultsLinesPrePallet = resultsLinesPrePallet.substring(0, resultsLinesPrePallet.length() - 1);
            resultsLinesPrePallet += "]";

            Log.d("LinesPrePallet -- >", resultsLinesPrePallet);

            new AysnTaskPrePallet(config.rutaWebServerOmar + "/insupPrePallet", resultsPrePallet, resultsLinesPrePallet).execute();
        }
    }

    private static class AysnTaskPrePallet extends AsyncTask<String, String, String> {
        private ProgressDialog pd;
        public String url;
        private String JSONPrePallet;
        private String JSONPrePalletLines;

        public AysnTaskPrePallet(String url, String jsonPP, String jsonPPL) {
            this.url = url;
            this.JSONPrePallet = jsonPP;
            this.JSONPrePalletLines = jsonPPL;

            pd = new ProgressDialog(getnContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setIndeterminate(true);
            pd.setMessage("Sync... Please wait!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("JSONPrePallet", JSONPrePallet));
                params.add(new BasicNameValuePair("JSONPrePalletLines", JSONPrePalletLines));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step = "4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step = "5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step = "6";
            } catch (Exception t) {
                // just end the background thread
                Log.e("ErrorConexion", t.getMessage());
                jsoncadena = "" + t.getMessage() + " -- step: " + step;

            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("iDWebMeth -- >", res);
            JSONObject json = null;

            BaseDatos db = new BaseDatos(nContext);

            try {
                json = new JSONObject(res);
                JSONArray resultadoPP;
                JSONArray resultadoPPL;
                JSONObject row;

                resultadoPP = json.optJSONArray("table1");
                resultadoPPL = json.optJSONArray("table2");

                db.abrir();

                for (int i = 0; i < resultadoPP.length(); i++) {
                    row = resultadoPP.getJSONObject(i);
                    db.cpdb.actualizaPPAfterSync(row.getInt("idPrePalletServer"), row.getString("vUnicSesionKey"));
                }

                for (int i = 0; i < resultadoPPL.length(); i++) {
                    row = resultadoPPL.getJSONObject(i);
                    db.cpdb.actualizaPPLAfterSync(row.getString("vUUID"));
                }

                db.cerrar();
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(nContext, nContext.getResources().getString(R.string.success_sync), Toast.LENGTH_LONG).show();
            }

            AddPrePallet.prePalletSincronizado();

            pd.dismiss();
        }
    }

    /*private static class AysnTaskPrePallet extends AsyncTask<String, String, String> {
        private ProgressDialog pd;
        public String url;
        private String JSONPrePallet;
        private String JSONPrePalletLines;
        private int type;

        public AysnTaskPrePallet(String url, String jsonStr, int type) {
            this.url = url;
            this.jsonStr = jsonStr;
            this.type = type;
            pd = new ProgressDialog(getnContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Sync... Please wait!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("json", jsonStr));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step = "4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step = "5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step = "6";
            } catch (Exception t) {
                // just end the background thread
                Log.e("ErrorConexion", t.getMessage());
                jsoncadena = "" + t.getMessage() + " -- step: " + step;

            }

            Log.d("iDWebMeth -- >", jsoncadena);

            //JSONObject json;
            JSONArray IDS;
            try {
                BaseDatos db = new BaseDatos(nContext);
                db.abrir();
                IDS = new JSONArray(jsoncadena);
                for(int i =0; i<IDS.length(); i++){
                    JSONObject row = IDS.getJSONObject(i);
                    if(type==1) {
                        //db.cpdb.actualizaPrePalletIDSync(row.getString("vUnicSesionKey"), row.getString("idPrePalletServer"));
                        db.cpdb.actualizaPPAfterSync(row.getString("vUnicSesionKey"));
                    }
                    if(type==2)
                        db.cpdb.actualizaPrePalletCasesIDSync(row.getString("idCaseTablet"), row.getString("idCasesPrePalletServer"));
                    if(type==3)
                        db.cpdb.actualizaFoliosIDSync(row.getString("vFolio"), row.getString("idPrePalletTablet"));
                }
                db.cerrar();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ErrorSyncPrePallet", e.getMessage());
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(nContext, "Error en la syncronización", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            //   Log.d("iDWebMeth -- >", res);

            if(type==1) {
                BaseDatos bd = new BaseDatos(getnContext());
                bd.abrir();
                String[][] datosCasesPrePallet = bd.cpdb.getCasesPrePalletsToSync();
                //String[][] datosFoliosPrePallet = bd.cpdb.getFoliosPrePalletsToSync();
                bd.cerrar();

                if (datosCasesPrePallet.length > 0) {
                    String resultsCasePrePallet = "[";
                    for (int i = 0; i < datosCasesPrePallet.length; i++)
                        resultsCasePrePallet += "{\"idCaseServer\":" + (datosCasesPrePallet[i][0] != null ? "\"" + datosCasesPrePallet[i][0] + "\"" : null) +
                                ", \"idCase\":\"" + datosCasesPrePallet[i][1] +
                                "\", \"idPrePalletServer\":\"" + datosCasesPrePallet[i][4] +
                                "\", \"idPrePallet\":\"" + datosCasesPrePallet[i][3] +
                                "\", \"vCodeCase\":\"" + datosCasesPrePallet[i][2] +
                                "\", \"bActive\":\"" + datosCasesPrePallet[i][5] +
                                "\", \"fechaRegistro\":\"" + datosCasesPrePallet[i][6] +
                                "\", \"fechaActualizacion\":" + (datosCasesPrePallet[i][7] != null ? "\"" + datosCasesPrePallet[i][7] + "\"" : null) +
                                ", \"userCreated\":" + (datosCasesPrePallet[i][8] != null ? "\"" + datosCasesPrePallet[i][8] + "\"" : null) +
                                ", \"userUpdated\":" + (datosCasesPrePallet[i][9] != null ? "\"" + datosCasesPrePallet[i][9] + "\"" : null) +
                                ", \"idTablet\":\"" + datosCasesPrePallet[i][10] +
                                "\", \"unicSesionKey\":\"" + datosCasesPrePallet[i][11] + "\"},";

                    resultsCasePrePallet = resultsCasePrePallet.substring(0, resultsCasePrePallet.length() - 1);
                    resultsCasePrePallet += "]";
                    //Toast.makeText(nContext, resultsFenologia,Toast.LENGTH_LONG).show();
                    Log.d("CasePrePallet -- >", resultsCasePrePallet);
                    new AysnTaskPrePallet(config.rutaWebServerOmar + "/insupCajasPrePallet", resultsCasePrePallet, 2).execute();
                }


               /* if(datosFoliosPrePallet.length > 0){
                    String resultsFolioPrePallet = "[";
                    for (int i = 0; i < datosFoliosPrePallet.length; i++)
                        resultsFolioPrePallet += "{\"vCodeFolio\":" + (datosFoliosPrePallet[i][0] != null ? "\"" + datosFoliosPrePallet[i][0] + "\"" : null) +
                                ", \"iCajas\":\"" + datosFoliosPrePallet[i][1] +
                                "\", \"idPrePallet\":\"" + datosFoliosPrePallet[i][2] +
                                "\", \"idPrePalletServer\":\"" + datosFoliosPrePallet[i][3] +
                                "\", \"idProductLog\":\"" + datosFoliosPrePallet[i][4] +
                                "\", \"Pallet\":" +null+
                                ", \"bActive\":\"" + datosFoliosPrePallet[i][5] +
                                "\", \"fechaRegistro\":\"" + datosFoliosPrePallet[i][6] +
                                "\", \"fechaActualizacion\":" + (datosFoliosPrePallet[i][7] != null ? "\"" + datosFoliosPrePallet[i][7] + "\"" : null) +
                                ", \"userCreated\":" + (datosFoliosPrePallet[i][8] != null ? "\"" + datosFoliosPrePallet[i][8] + "\"" : null) +
                                ", \"userUpdated\":" + (datosFoliosPrePallet[i][9] != null ? "\"" + datosFoliosPrePallet[i][9] + "\"" : null) +
                                ", \"idTablet\":\"" + datosFoliosPrePallet[i][10] +
                                "\", \"unicSesionKey\":\"" + datosFoliosPrePallet[i][11] + "\"},";

                    resultsFolioPrePallet = resultsFolioPrePallet.substring(0, resultsFolioPrePallet.length() - 1);
                    resultsFolioPrePallet += "]";
                    //Toast.makeText(nContext, resultsFenologia,Toast.LENGTH_LONG).show();
                    new AysnTaskPrePallet(config.rutaWebServerOmar + "/insupFoliosPrePallet", resultsFolioPrePallet, 3).execute();
                    Log.d("FolioPrePallet -- >", resultsFolioPrePallet);
                }*/
           /* }

            try {
                pd.dismiss();
            }catch(Exception ex){
                Log.e("ErrorSyncPrePallet", ex.getMessage());
            }
        }
    }*/
}
