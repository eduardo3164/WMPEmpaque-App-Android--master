package com.ns.empaque.wmpempaque.Desgrane;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ns.empaque.wmpempaque.R.layout.position;

/*
 * Created by Héctor on 02/11/2016.
 */
public class adaptadorBoxesInFolio extends BaseAdapter {

    public Activity nContext;
    public ArrayList<BoxesFolioInLine> bfl;
    private LayoutInflater layoutInflater;

    //private static TextView lineName, plantName, boxesInPallet, avblBoxes, EnterLineTime;//cajasInLine, folio, idProductLog;

    public adaptadorBoxesInFolio(Activity context, ArrayList<BoxesFolioInLine> bfl){
        this.nContext = context;
        this.bfl = bfl;
    }

    @Override
    public int getCount() {
        return bfl.size();
    }

    @Override
    public Object getItem(int position) {
        return bfl.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        View v;
        layoutInflater = nContext.getLayoutInflater();
        v = layoutInflater.inflate(R.layout.item_desgrane_view, null, true);



        final TextView folio = (TextView) v.findViewById(R.id.txtFolio);
        final TextView lineName = (TextView) v.findViewById(R.id.lineName);
        final TextView plantName =(TextView) v.findViewById(R.id.plantName);
        final TextView cajasInLine =(TextView) v.findViewById(R.id.cajasInLine);
        final TextView boxesInPallet =(TextView) v.findViewById(R.id.boxesInPallet);
        final TextView avblBoxes =(TextView) v.findViewById(R.id.avblBoxes);
        final TextView EnterLineTime =(TextView) v.findViewById(R.id.EnterLineTime);
        final TextView idProductLog = (TextView) v.findViewById(R.id.idProduct);

        //BoxesFolioInLine b = bfl.get(position);

        folio.setText(bfl.get(position).getvFolio());
        lineName.setText(bfl.get(position).getLineName());
        plantName.setText(bfl.get(position).getFarmName());
        cajasInLine.setText(bfl.get(position).getBoxesInLine()+"");
        boxesInPallet.setText(bfl.get(position).getBoxesAssignToPallet()+"");
        avblBoxes.setText(bfl.get(position).getBoxesAvailable()+"");
        EnterLineTime.setText(bfl.get(position).getFechaEnterLine());
        idProductLog.setText(bfl.get(position).getIdProductLog()+"");

        final Button btnDesgrane = (Button)v.findViewById(R.id.btnDesgrane);
        final ImageButton btnDelete = (ImageButton)v.findViewById(R.id.btnDelete);
        final EditText txtCajas = (EditText)v.findViewById(R.id.et_cajas_desgrane);
        txtCajas.setText(bfl.get(position).getCajasDesgranadas());





        txtCajas.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bfl.get(position).setCajasDesgranadas(txtCajas.getText().toString());
            }
        });

        btnDesgrane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int cajas = Integer.parseInt(avblBoxes.getText().toString());
                int a = Integer.parseInt(txtCajas.getText().toString());

                avblBoxes.setText((cajas-a)+"");*/
                AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
                AlertDialog dialog;
                if(txtCajas.getText().toString().equals(""))
                {
                    //Toast.makeText(nContext, "Ingresa el Número de Cajas a Desgranar", Toast.LENGTH_LONG).show();
                    builder.setMessage("Ingresa el número de cajas a desgranar").setTitle("Error");
                    dialog = builder.create();
                    dialog.show();

                }else {
                    int a = Integer.parseInt(txtCajas.getText().toString());
                    if(a <=0)
                    {
                        builder.setMessage("Ingresa un Número Valido").setTitle("Error");
                        dialog = builder.create();
                        dialog.show();
                    }else if (a > Integer.parseInt(cajasInLine.getText().toString()) || a<=0) {
                        builder.setMessage("El número de cajas a desgranar NO debe ser mayor a las disponibles").setTitle("Error");
                        dialog = builder.create();
                        dialog.show();

                    } else {//registrarDesgrane
                        new AsyncTaskDesgrane(config.rutaWebServerOmar + "/registrarDesgrane", nContext, txtCajas.getText().toString(),folio.getText().toString(), idProductLog.getText().toString(), EnterLineTime.getText().toString(), position).execute();
                        //Desgrane.refreshList((folio.getText().toString()));
                    }
                }


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(nContext, "BORRADO", Toast.LENGTH_LONG).show();
                Desgrane.removeItem(position);
            }
        });

        return v;
    }

    private class AsyncTaskDesgrane extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private String mURL;
        private String cajas;
        private String folio;
        private String idProduct;
        private String fecha;
        private Activity nContext;
        private int posicion;

        public AsyncTaskDesgrane(String url, Activity nContext, String cajas, String folio, String idProduct, String fecha, int pos) {
            this.mURL = url;
            this.nContext = nContext;
            this.cajas = cajas;
            this.folio = folio;
            this.idProduct = idProduct;
            this.fecha = fecha;
            this.posicion = pos;

            //this.mPass = pass;

            pDialog = new ProgressDialog(this.nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.setTitle("Desgranando");
            pDialog.setMessage("Cargando... Por favor espere");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String jsoncadena = "", step = "0";

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("cajas", cajas));
                params.add(new BasicNameValuePair("vFolio",folio));
                params.add(new BasicNameValuePair("idProduct", idProduct));
                params.add(new BasicNameValuePair("fecha", fecha));
                //params.add(new BasicNameValuePair("password", mPass));
                step = "2";
                HttpPost httppostreq = new HttpPost(mURL);
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
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);
            adaptadorBoxesInFolio adaptadorBoxesInFolio;

            boolean error = false;

            try
            {
                JSONObject objJSON = new JSONObject(res);
                JSONArray respuestaDT = objJSON.getJSONArray("table1");
                for (int i = 0; i < respuestaDT.length(); i++) {
                    JSONObject row = respuestaDT.getJSONObject(i);
                    String ress = row.getString("respuesta");

                    int x = Integer.parseInt(ress);
                    if(x == 1)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
                        AlertDialog dialog;
                        builder.setMessage("El Desgrane Fue Realizado con Éxito").setTitle("Éxito");
                        dialog = builder.create();
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
                        AlertDialog dialog;
                        builder.setMessage("El Desgrane NO se Realizó").setTitle("Error");
                        dialog = builder.create();
                        dialog.show();
                    }
                    //Log.d("fecha" + (i+1), row.getString("fecha"));
                    //Log.d("Planta " + (i+1), row.getString("Planta"));
                }



            }catch (Exception e) {
                error = true;
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos", e.getMessage());
            }

            if(!error){

                Desgrane.removeItem(posicion);
                Desgrane.refreshList(folio);

            }

            /*try {
                JSONObject objJSON = new JSONObject(res);
                JSONArray respuestaDT = objJSON.getJSONArray("table1");

                if(respuestaDT.length() > 0) {
                    for (int i = 0; i < respuestaDT.length(); i++) {
                        JSONObject row = respuestaDT.getJSONObject(i);

                        //Log.d("fecha" + (i+1), row.getString("fecha"));
                        //Log.d("Planta " + (i+1), row.getString("Planta"));
                    }
                } else {
                    Toast.makeText(nContext, "Vacio", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos", e.getMessage());
            }*/


            pDialog.dismiss();
        }

    }
}
