package com.ns.empaque.wmpempaque.UbicationByQr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by jcalderon on 05/04/2016.
 */
public class myAsyncTaskDisponibles extends AsyncTask<String, String, String> {

    //private ProgressDialog pDialog;
    public String url, idProductLog;
    private Context nContext;
    ProgressDialog pd;

    public myAsyncTaskDisponibles(Context nContext, String url, String idProductLog) {
        this.url = url;
        this.idProductLog = idProductLog;
        this.nContext = nContext;

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
        final HttpClient Client = new DefaultHttpClient();
        String jsoncadena = "", step = "0";
        try {
            step = "1";
           // List<NameValuePair> params = new ArrayList<NameValuePair>();
           // params.add(new BasicNameValuePair("accion", accion));
           // params.add(new BasicNameValuePair("QRCode", QRCode));
            step = "2";
            HttpPost httppostreq = new HttpPost(url);
            step = "3";
           // httppostreq.setEntity(new UrlEncodedFormEntity(params));
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

    }
}
