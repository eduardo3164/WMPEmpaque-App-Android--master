package com.ns.empaque.wmpempaque.OnHold;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.MAP.locacion;
import com.ns.empaque.wmpempaque.Modelo.config;
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
 * Created by Christopher BA on 07/03/2017.
 */

public class OnHold {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static LinearLayout lytProductoOnHold;
    private static LinearLayout fbtnAtras, fbtnNuevo;
    private static ImageView btnBuscarLocation, btnScanBuscarOnHold;
    private static Button btnTodosBuscarOnHold;
    private static EditText txtCodigoLocacion, txtBuscarOnHold;
    private static TextView lblCodigoLocacion, lblNombreLocacion;
    private static BaseDatos bdWMP;
    private static locacion loc;
    private static Regex regex[];
    private static ProductoOnHold productoOnHold[];
    private static ProductoOnHold busquedaOnHold[];
    private static int xPosicion, yPosicion, zPosicion;
    private static RecyclerView rvOnHold;

    public OnHold(Activity nContext, RelativeLayout parent) {
        this.nContext = nContext;
        this.content = parent;

        bdWMP = new BaseDatos(nContext);
    }

    public static void setView() {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.view_on_hold, null, true);
        config.updateContent(content, v);

        lytProductoOnHold       = (LinearLayout) v.findViewById(R.id.lytProductoOnHold);
        btnBuscarLocation       = (ImageView) v.findViewById(R.id.btnBuscarLocacion);
        txtCodigoLocacion       = (EditText) v.findViewById(R.id.txtCodigoLocacion);
        //txtCodigoLocacion.setText("ONHOLDZAP|0.0.0");

        lblCodigoLocacion       = (TextView) v.findViewById(R.id.lblCodigoLocacion);
        lblNombreLocacion       = (TextView) v.findViewById(R.id.lblNombreLocacion);

        fbtnNuevo               = (LinearLayout) v.findViewById(R.id.fbtnNuevo);
        fbtnAtras               = (LinearLayout) v.findViewById(R.id.fbtnAtras);

        rvOnHold                = (RecyclerView) v.findViewById(R.id.rvOnHold);

        btnScanBuscarOnHold     = (ImageView) v.findViewById(R.id.btnScanBuscarOnHold);
        txtBuscarOnHold         = (EditText) v.findViewById(R.id.txtBuscarOnHold);
        btnTodosBuscarOnHold    = (Button) v.findViewById(R.id.btnTodosBuscarOnHold);

        eventos();
    }

    public static void eventos() {
        fbtnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loc != null) {
                    agregarProductoOnHold();
                } else {
                    mostrarDialogo("Mensaje", "Necesita escanear una locación");
                }
            }
        });

        fbtnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        btnBuscarLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMPEmpaque.scanOnHold = 1;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        txtCodigoLocacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    String str = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)", "");
                    buscarLocacion(str);
                    txtCodigoLocacion.setText("");
                }
            }
        });

        btnScanBuscarOnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMPEmpaque.scanOnHold = 3;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        txtBuscarOnHold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if (s.toString().contains("\n")) {
                    String str = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)", "");
                    buscarEnOnHold(str);
                    //txtBuscarOnHold.setText("");
                //}
            }
        });

        btnTodosBuscarOnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarTodos();
            }
        });
    }

    public static void buscarLocacion(String str) {
        String locacion[] = str.split("\\|");
        Log.d("STR", str);

        if(locacion.length > 1){
            Log.d("LOCACION 0", locacion[0]);
            Log.d("LOCACION 1", locacion[1]);

            String posiciones[] = locacion[1].split("\\.");

            if(posiciones.length >= 3){
                boolean error = false;
                Log.d("POSICION 0", posiciones[0]);
                Log.d("POSICION 1", posiciones[1]);
                Log.d("POSICION 2", posiciones[2]);

                try{
                    xPosicion = Integer.parseInt(posiciones[0]);
                    yPosicion = Integer.parseInt(posiciones[1]);
                    zPosicion = Integer.parseInt(posiciones[2]);
                } catch(Exception ex){
                    error = true;
                }

                if(!error){
                    bdWMP.abrir();
                    loc = bdWMP.getLocationLineByCode(locacion[0], 2);
                    bdWMP.cerrar();

                    if (loc.getDescription() != null) {
                        nContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AsyncTaskGetProductoOnHold(config.rutaWebServerOmar + "/getProductoOnHold", loc.getIdLocation()+"", loc.getCode()).execute();
                            }
                        });
                    } else {
                        mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_on_hold));
                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_on_hold));
                }
            } else {
                mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_on_hold));
            }
        } else {
            mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_on_hold));
        }
    }

    public static void recargarLista(){
        buscarLocacion(loc.getCode() + "|" + xPosicion + "." + yPosicion + "." + zPosicion);
    }

    public static void agregarProductoOnHold(){
        AddProductoOnHold objProd = new AddProductoOnHold(nContext, loc, regex, xPosicion, yPosicion, zPosicion);
        View vw = objProd.getPopUpView();

        FloatingActionButton fabAtrasAddDialog = (FloatingActionButton) vw.findViewById(R.id.fabAtras);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        alertDialog.setView(vw);

        alertDialog.setIcon(R.drawable.naturesweet);
        alertDialog.setCancelable(false);

        final AlertDialog ad = alertDialog.create();

        ad.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ad.show();

        fabAtrasAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buscarLocacion(loc.getCode() + "|" + xPosicion + "." + yPosicion + "." + zPosicion);
                ad.dismiss();
            }
        });
    }

    public static void buscarEnOnHold(String str){
        ArrayList<ProductoOnHold> listaOnHold = new ArrayList<>();

        for(int i = 0; i < productoOnHold.length; i++){
            if(productoOnHold[i].getvProducto().contains(str)){
                listaOnHold.add(productoOnHold[i]);
            }
        }

        busquedaOnHold = new ProductoOnHold[listaOnHold.size()];

        for(int i = 0; i < listaOnHold.size(); i++){
            busquedaOnHold[i] = listaOnHold.get(i);
        }

        rvOnHold.setLayoutManager(new LinearLayoutManager(nContext));
        rvOnHold.setAdapter(new AdaptadorListaOnHoldRV(busquedaOnHold, nContext));
    }

    public static void mostrarTodos(){
        rvOnHold.setLayoutManager(new LinearLayoutManager(nContext));
        rvOnHold.setAdapter(new AdaptadorListaOnHoldRV(productoOnHold, nContext));
    }

    public static void mostrarDialogo(String titulo, String mensaje){
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

    public static class AsyncTaskGetProductoOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private String mIdLocacion;
        private String mCodigoLocacion;

        public AsyncTaskGetProductoOnHold(String url, String idLocacion, String codigoLocacion) {
            this.mURL = url;
            this.mIdLocacion = idLocacion;
            this.mCodigoLocacion = codigoLocacion;

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
                Log.d("LOCACION", mIdLocacion+"");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", mIdLocacion));

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

            try {
                JSONObject jsonObject = new JSONObject(res);
                JSONArray JSONArrayProducto = jsonObject.getJSONArray("table1");
                JSONObject rowJSON;

                productoOnHold = new ProductoOnHold[JSONArrayProducto.length()];

                for(int i = 0; i < JSONArrayProducto.length(); i++) {
                    rowJSON = JSONArrayProducto.getJSONObject(i);

                    productoOnHold[i] = new ProductoOnHold();

                    productoOnHold[i].setIdProductoOnHold(rowJSON.getInt("idProductoOnHold"));
                    productoOnHold[i].setIdProduct(rowJSON.getInt("idProduct"));
                    productoOnHold[i].setIdProductLog(rowJSON.getInt("idProductLog"));
                    productoOnHold[i].setvProducto(rowJSON.getString("vProducto"));
                    productoOnHold[i].setFechaOnHold(rowJSON.getString("fechaOnHold"));
                    productoOnHold[i].setIdRazon(rowJSON.getInt("idRazon"));
                    productoOnHold[i].setnRazon(rowJSON.getString("nRazon"));
                    productoOnHold[i].setIdDepartamento(rowJSON.getInt("idDepartamento"));
                    productoOnHold[i].setnDepartamento(rowJSON.getString("nDepartamento"));
                    productoOnHold[i].setIdPlanta(rowJSON.getInt("idPlanta"));
                    productoOnHold[i].setnPlanta(rowJSON.getString("nPlanta"));
                    productoOnHold[i].setGH(rowJSON.getString("GH"));
                    productoOnHold[i].setCajasOnHold(rowJSON.getInt("cajasOnHold"));
                    productoOnHold[i].setLibrasOnHold(rowJSON.getDouble("librasOnHold"));
                    productoOnHold[i].setvComentarios(rowJSON.getString("vComentarios"));
                    productoOnHold[i].setIdLocation(rowJSON.getInt("idLocation"));
                    productoOnHold[i].setTipoProducto(rowJSON.getInt("tipoProducto"));
                }

                lytProductoOnHold.setVisibility(View.VISIBLE);
                lblCodigoLocacion.setText(loc.getCode());
                lblNombreLocacion.setText(loc.getDescription());

                bdWMP.abrir();
                regex = bdWMP.getRegexByCode(mCodigoLocacion, 2);
                bdWMP.cerrar();

                rvOnHold.setLayoutManager(new LinearLayoutManager(nContext));
                rvOnHold.setAdapter(new AdaptadorListaOnHoldRV(productoOnHold, nContext));

                if(productoOnHold.length == 0){
                    mostrarDialogo("Mensaje", nContext.getString(R.string.no_product_in_on_hold));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }
}