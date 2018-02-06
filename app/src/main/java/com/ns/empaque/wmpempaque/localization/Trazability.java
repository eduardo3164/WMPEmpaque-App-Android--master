package com.ns.empaque.wmpempaque.localization;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.R;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcalderon on 04/01/2016.
 */
public class Trazability {
    public static LayoutInflater inflater;
    public static Activity nContext;
    public RelativeLayout content;
    public static LinearLayout contentTrazabilidad;
    public static TextView tvCode, tvKindCode;
    public static FloatingActionButton fabSearch;

    public Trazability(Activity c, RelativeLayout content){
        this.nContext = c;
        this.content = content;
    }

    public void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.trazability, null, true);
        config.updateContent(content, v);

        /*Asignacion de componentes xml a objetos en java*/
        tvCode = (TextView) v.findViewById(R.id.tvData);
        tvKindCode = (TextView) v.findViewById(R.id.tvKindCode);
        fabSearch = (FloatingActionButton) v.findViewById(R.id.fabLookfor);
        contentTrazabilidad = (LinearLayout) v.findViewById(R.id.contentTrazabilidad);

        accionBtnSearch();
    }

    private void accionBtnSearch() {
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });
    }

    public static void consultaInformacionFolio(String folio){
        //cargarVistaInfoFolio();

        new taskInformationFolioPallet(folio,1).execute();
    }

    public static void cargarVistaInfoFolio(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.viewfolioinfo, null, true);
        config.updateContent(contentTrazabilidad, v);
    }

    public static void consultaInfoPallet( String pallet){
        cargarVistaInfoPallet();
    }

    private static void cargarVistaInfoPallet() {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.viewpalletinfo, null, true);
        config.updateContent(contentTrazabilidad, v);
    }

    private static class taskInformationFolioPallet extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        private String embalaje;
        private int type;

        public taskInformationFolioPallet(String embalaje, int type) {
            this.embalaje = embalaje;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(type==1?"Folio":"pallet", embalaje));
                step = "2";
                HttpPost httppostreq = new HttpPost(config.rutaWebServerOmar+"/Get_FolioInfo");
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            if(type==1)
                cargarVistaInfoFolio();
            else
                cargarVistaInfoPallet();

        }
    }

}
