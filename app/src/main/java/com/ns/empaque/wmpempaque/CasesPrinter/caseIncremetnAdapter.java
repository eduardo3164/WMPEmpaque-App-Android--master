package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Desgrane.BoxesFolioInLine;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.PrinterConection.pConnect;
import com.ns.empaque.wmpempaque.R;
import com.ns.empaque.wmpempaque.WMPEmpaque;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jcalderon on 14/11/2016.
 */
public class caseIncremetnAdapter extends BaseAdapter {

    private Activity nContext;
    public ArrayList<caseIncrement> ciList;
    private LayoutInflater layoutInflater;
    private SimpleDateFormat formatoFecha;
    private Date fechaActual;

    public caseIncremetnAdapter(Activity context, ArrayList<caseIncrement> ciList){
        this.ciList = ciList;
        this.nContext = context;

        formatoFecha = new SimpleDateFormat("yyMMdd");
        fechaActual = new Date();
    }

    @Override
    public int getCount() {
        return ciList.size();
    }

    @Override
    public Object getItem(int position) {
        return ciList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        layoutInflater = nContext.getLayoutInflater();
        v = layoutInflater.inflate(R.layout.vw_case_increment, null, true);

        TextView txt_Case = (TextView) v.findViewById(R.id.txt_Case);
        ImageView btn_eraseIncrement = (ImageView) v.findViewById(R.id.btn_eraseIncrement);
        ImageView btnPrint = (ImageView) v.findViewById(R.id.btnPrint);
        final TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);

        final caseIncrement ci = ciList.get(position);

        txt_Case.setText(ci.getCaseCode()+"/"+ci.getFolio());

        btn_eraseIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AyncTaskBorrarCaseIncrement(config.rutaWebServerOmar+"/inactiveCaseIncrement", ciList.get(position).getCaseCode(), position).execute();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SKU", ci.getSKU());

                if(WMPEmpaque.impresoraConfigurada()){
                    ArrayList<caseIncrement> l = new ArrayList<caseIncrement>();
                    l.add(ci);

                    pConnect print = new pConnect(nContext, txtStatus);
                    print.print(l, false);
                } else {
                    WMPEmpaque.mostrarDialogo("Configure impresora e intentelo nuevamente");
                }
            }
        });

        return v;
    }

    private class AyncTaskBorrarCaseIncrement extends AsyncTask<String, String, String> {

        private String mURL;
        private String mCaseIncrement;
        private int mPosicion;

        public AyncTaskBorrarCaseIncrement(String url, String caseInc, int pos) {
            this.mURL = url;
            this.mCaseIncrement = caseInc;
            this.mPosicion = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            String jsoncadena = "";
            Log.d("CaseIncrement", mCaseIncrement);

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("caseInc", mCaseIncrement));

                HttpPost httppostreq = new HttpPost(mURL);
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 50000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 50000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                final HttpClient Client = new DefaultHttpClient(httpParameters);
                HttpResponse httpresponse = Client.execute(httppostreq);
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute (String res) {
            Log.d("generateVoice -- >", res);

            try {
                JSONObject objJSON = new JSONObject(res);
                JSONArray respuestaJSON = objJSON.getJSONArray("Result");

                int respuesta;

                for (int i = 0; i < respuestaJSON.length(); i++) {
                    JSONObject row = respuestaJSON.getJSONObject(i);

                    respuesta = row.getInt("respuesta");

                    if(respuesta == 0){
                        //Borrar de la DB local.
                        BaseDatos db = new BaseDatos(nContext);
                        db.abrir();
                        db.ciddb.InactiveRow(ciList.get(mPosicion));
                        db.cerrar();

                        ciList.remove(mPosicion);
                        notifyDataSetChanged();
                        GenerateCases.cambiarContadorCases();
                    }
                    else
                        Toast.makeText(nContext, "Intentalo nuevamente", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
