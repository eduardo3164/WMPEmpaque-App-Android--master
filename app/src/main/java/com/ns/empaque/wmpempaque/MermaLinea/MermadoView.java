package com.ns.empaque.wmpempaque.MermaLinea;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.Modelo.config;
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
 * Created by Christopher BA on 24/05/2017.
 */

public class MermadoView extends FrameLayout {

    private Activity nContext;

    public MermadoView(Context context) {
        super(context);
        this.nContext = (Activity) context;

        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.adaptador_lista_mermado, this);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    public void displayInfo(final ProductoMermado productoMermado) {
        final TextView lblLibrasMermadas        = (TextView) findViewById(R.id.lblLibrasMermadas);
        final TextView lblPlantaMermado         = (TextView) findViewById(R.id.lblPlantaMermado);
        final TextView lblLineaMermado          = (TextView) findViewById(R.id.lblLineaMermado);
        final TextView lblFechaMerma            = (TextView) findViewById(R.id.lblFechaMerma);
        final TextView lblDisposicionMermado    = (TextView) findViewById(R.id.lblDisposicionMermado);
        final TextView lblDepartamentoMermado   = (TextView) findViewById(R.id.lblDepartamentoMermado);
        final TextView lblRazonMermado          = (TextView) findViewById(R.id.lblRazonMermado);
        final LinearLayout btnBorrarMerma       = (LinearLayout) findViewById(R.id.btnBorrarMerma);

        lblLibrasMermadas.setText(productoMermado.getLibrasMermado() + " lbs");
        lblPlantaMermado.setText(productoMermado.getnPlanta() + "");
        lblLineaMermado.setText(productoMermado.getnLinea() + "");
        lblFechaMerma.setText(productoMermado.getFechaMermado() + "");
        lblDisposicionMermado.setText(productoMermado.getnDisposicion() + "");
        lblDepartamentoMermado.setText(productoMermado.getnDepartamento() + "");
        lblRazonMermado.setText(productoMermado.getnRazon() + "");

        btnBorrarMerma.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoBorrarMerma(productoMermado.getIdProductoMermado());
                Toast.makeText(nContext, productoMermado.getIdProductoMermado()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarDialogoBorrarMerma(final int idProductoMermado){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle("Mensaje");
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage("¿Desea borrar la merma?");
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String vUser = "DBTAPP";
                new AsyncTaskBorrarProductoMermado(config.rutaWebServerOmar + "/insDelMermaLinea", idProductoMermado, vUser).execute();
            }
        });

        alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void mostrarDialogo(String titulo, String mensaje){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(mensaje);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public class AsyncTaskBorrarProductoMermado extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private int idProductoMermado;
        private String mUser;

        public AsyncTaskBorrarProductoMermado(String url, int idProductoMermado, String mUser) {
            this.mURL = url;
            this.idProductoMermado = idProductoMermado;
            this.mUser = mUser;

            mProgressDialog = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Cargando... Por favor espere!");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "";
            try {
                Log.d("IDPRODUCTOMERMADO", idProductoMermado + "");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("accion", "3"));
                params.add(new BasicNameValuePair("idProductoMermado", idProductoMermado + ""));
                params.add(new BasicNameValuePair("idLinea", "0"));
                params.add(new BasicNameValuePair("idDepartamento", "0"));
                params.add(new BasicNameValuePair("idRazon", "0"));
                params.add(new BasicNameValuePair("vDisposicion", " "));
                params.add(new BasicNameValuePair("dLibras", "0"));
                params.add(new BasicNameValuePair("vComentarios", " "));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("xPosicion", "0"));
                params.add(new BasicNameValuePair("yPosicion", "0"));
                params.add(new BasicNameValuePair("zPosicion", "0"));
                params.add(new BasicNameValuePair("idPlanta", "0"));

                HttpPost httppostreq = new HttpPost(mURL);
                httppostreq.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpresponse = Client.execute(httppostreq);
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
            } catch (Exception t) {
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. " + t.getMessage();
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);

            try{
                JSONObject jsonObject = new JSONObject(res);
                JSONArray JSONArrayRespuesta = jsonObject.getJSONArray("table1");
                JSONObject rowJSON;
                int indicador = 0;

                if(res.contains("indicador")){
                    for(int i = 0; i < JSONArrayRespuesta.length(); i++) {
                        rowJSON = JSONArrayRespuesta.getJSONObject(i);
                        indicador = rowJSON.getInt("indicador");
                    }

                    if(indicador == 1) {
                        mostrarDialogo("Mensaje", "La merma se borró de forma correcta");
                    }
                    else
                        mostrarDialogo("Error al borrar merma en línea", "Error al registrar merma");
                } else {
                    mostrarDialogo("Error al borrar merma en línea", "Error al registrar merma");
                }

                MermaLinea.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }
}
