package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.R;

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
 * Created by jcalderon on 17/11/2016.
 */
public class syncCasesHeaderAndDetails {

    public static String getJSONHeader(Activity nContext){
        String JSONCaseHeader = null;

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        ArrayList<CaseCode> ccList = bd.cidb.getCasesCodeHeaderToSync();
        bd.cerrar();

        if(ccList.size() > 0){
            JSONCaseHeader = "[";

            for(int i = 0; i < ccList.size(); i++){
                CaseCode cc = ccList.get(i);

                        JSONCaseHeader += "{\"vCodeCase\":\"" + cc.getCode() + "\"," +
                                "\"vGreenHouse\":\"" + cc.getGreenHouse() + "\"," +
                                "\"vFolio\":\"" + cc.getFolio() + "\"," +
                                "\"vSize\":\"" + cc.getSize() + "\"," +
                                "\"iLineNumber\":\"" + cc.getIdGPLine() + "\"," +
                                "\"iFarm\":\"" + cc.getFarm() + "\"," +
                                "\"vCompany\":\"" + cc.getCompany() + "\"," +
                                "\"iWeek\":\"" + cc.getWeek() + "\"," +
                                "\"vHour\":\"" + cc.getHour() + "\"," +
                                "\"vDay\":\"" + cc.getDay() + "\"," +
                                "\"vUUID\":\"" + cc.getUUIDHeader() + "\"," +
                                //"\"vSKU\":\"" + cc.getSKU() + "\"," +
                                "\"bActive\":\"" + cc.getActive() + "\"," +
                                "\"CreatedDate\":\"" + cc.getCreatedDate() + "\"," +
                                "\"CreatedUser\":\"" + cc.getCreatedUser() + "\"," +
                                "\"UpdateDate\":" + (cc.getUpdateDate() != null ? "\"" + cc.getUpdateDate() + "\"" : "null,") +
                                "\"UpdateUser\":" + (cc.getUpdateUser() != null ? "\"" + cc.getUpdateUser() + "\"" : "null},");

            }

            JSONCaseHeader = JSONCaseHeader.substring(0, JSONCaseHeader.length() - 1);

            JSONCaseHeader += "]";
        } else {
            CasesList.des();
        }

        if(JSONCaseHeader != null)
            Log.d("JSONCaseHeader -- >", JSONCaseHeader);

        return JSONCaseHeader;
    }

    public static String getJSONDetails(Activity nContext){
        String JSONCaseDetails = null;

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        ArrayList<caseIncrement> ciList = bd.ciddb.getCaseIncrementToSync();
        bd.cerrar();

        if(ciList.size() > 0){
            JSONCaseDetails = "[";

            for(int i = 0; i < ciList.size(); i++){
                caseIncrement ci = ciList.get(i);
                JSONCaseDetails += "{\"vCodeCase\":\"" + ci.getCaseCode()+"\","+
                        "\"vFolio\":\""+ci.getFolio()+"\","+
                        "\"vUUID\":\""+ci.getUUID()+"\","+
                        "\"vCodeCaseHeader\":\""+ci.getCaseCodeHeader()+"\","+
                        "\"vUUIDHeader\":\""+ci.getUUIDHeader()+"\","+
                        //"\"vSKU\":\""+ci.getUUIDHeader()+"\","+
                        //"\"vGTIN\":\""+ci.getUUIDHeader()+"\","+
                        //"\"vDescription\":\""+ci.getUUIDHeader()+"\","+
                        //"\"vUnits\":\""+ci.getUUIDHeader()+"\","+
                        //"\"vOZ\":\""+ci.getUUIDHeader()+"\","+
                        "\"bActive\":\""+ci.getActive()+"\","+
                        "\"CreatedDate\":\""+ci.getCreatedDate()+"\","+
                        "\"CreatedUser\":\""+ci.getCreatedUser()+"\","+
                        "\"UpdateDate\":" + (ci.getUpdateDate() != null ? "\"" + ci.getUpdateDate() + "\"" : "null,") +
                        "\"UpdateUser\":" + (ci.getUpdateUser() != null ? "\"" + ci.getUpdateUser() + "\"" : "null},");
            }

            JSONCaseDetails = JSONCaseDetails.substring(0, JSONCaseDetails.length() - 1);
            JSONCaseDetails += "]";
        } else {
            CasesList.des();
        }

        if(JSONCaseDetails != null)
            Log.d("JSONCaseDetails -- >", JSONCaseDetails);

        return JSONCaseDetails;
    }

    public static class ATSendTable extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url, jsonHeader, jsonDetails;
        final public Activity nContext;
        ProgressDialog pd;

        public ATSendTable(String url, Activity nContext, String JSONCaseHeader, String JSONCaseDetails) {
            this.url = url;
            this.nContext = nContext;
            this.jsonHeader = JSONCaseHeader;
            this.jsonDetails = JSONCaseDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage(R.string.charging+"");
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
                params.add(new BasicNameValuePair("jsonCasesHeader", jsonHeader));
                params.add(new BasicNameValuePair("jsonCasesDetails", jsonDetails));
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
                //jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("iDWebMeth -- >", res);
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();

            try {
                pd.dismiss();
            }catch(Exception e){ }


            try{
            /* *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* */
                JSONObject json = new JSONObject(res);
                JSONArray ch = json.getJSONArray("table1");//casesHeader
                JSONArray cd = json.getJSONArray("table2");//casesDetails

                BaseDatos db = new BaseDatos(nContext);
                db.abrir();

                if(ch.length() > 0){
                    for (int i = 0; i < ch.length(); i++) {
                        JSONObject row = ch.getJSONObject(i);
                        db.cidb.updateSyncUUID(row.getString("UUIDHeader"));
                    }
                }

                if(cd.length() > 0){
                    for(int i = 0; i<cd.length(); i++){
                        JSONObject row = cd.getJSONObject(i);
                        db.ciddb.updateSyncUUID(row.getString("vUUID"));
                    }
                }

                db.cerrar();

                CasesList.reFreshList(nContext);
                CasesList.des();
                CasesList.fillList();

                Toast.makeText(nContext, "Datos sincronizados...", Toast.LENGTH_SHORT).show();
            }catch(Exception ex){
                Log.e("syncCasesHDError", ex.getMessage());
            }
        }
    }
}
