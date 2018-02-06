package com.ns.empaque.wmpempaque.OnHold;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
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
 * Created by Christopher BA on 21/03/2017.
 */

public class OnHoldView extends FrameLayout {

    /*ENCABEZADO*/
    private TextView lblProductoOnHoldLP;
    private TextView lblFechaOnHoldLP;
    private TextView lblRazonOnHoldLP;
    private TextView lblPlantaOnHoldLP;
    private TextView lblGHOnHoldLP;
    private TextView lblCajasOnHoldLP;
    private LinearLayout btnLiberarFolioLP;
    private LinearLayout btnMermarFolioLP;

    /*LIBERAR PRODUCTO*/
    private Spinner cboxCalidadLP;
    private Spinner cboxRazonLP;
    private AdaptadorSpinner adaptadorSPCalidad;
    private AdaptadorSpinner adaptadorSPRazon;
    private EditText txtComentariosLP;
    private EditText txtCajasLP;
    private EditText txtLibrasLP;
    private Button btnCancelarLP;
    private Button btnAceptarLP;
    private Calidad arregloCalidad[];
    private Razon arregloRazon[];

    /*MERMAR PRODUCTO*/
    private Spinner cboxDepartamentoMP;
    private Spinner cboxRazonMP;
    private Spinner cboxDisposicionMP;
    private AdaptadorSpinner adaptadorSPDepartamentoM;
    private AdaptadorSpinner adaptadorSPRazonM;
    private AdaptadorSpinner adaptadorSPDisposicionM;
    private EditText txtComentariosMP;
    private EditText txtCajasMP;
    private EditText txtLibrasMP;
    private Button btnCancelarMP;
    private Button btnAceptarMP;
    private Departamento arregloDepartamentoM[];
    private Razon arregloRazonM[];
    private Disposicion arregloDisposicionM[];

    private int cajasSeleccionadas;
    private Double librasSeleccionadas;
    private final int TIPO_FOLIO = 1;
    private final int TIPO_PALLET = 2;
    private final int ACCION_LIBERAR = 1;
    private final int ACCION_MERMAR = 2;

    private Activity nContext;
    private BaseDatos bdWMP;
    private AlertDialog alertDialog;

    public OnHoldView(Context context) {
        super(context);
        this.nContext = (Activity) context;

        bdWMP = new BaseDatos(nContext);

        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.adaptador_lista_on_hold, this);
        setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    public void displayInfo(final ProductoOnHold productoOnHold) {
        final TextView lblProductoOnHold      = (TextView) findViewById(R.id.lblProductoOnHold);
        final TextView lblFechaOnHoldF        = (TextView) findViewById(R.id.lblFechaOnHoldF);
        final TextView lblRazonOnHoldF        = (TextView) findViewById(R.id.lblRazonOnHoldF);
        final TextView lblPlantaOnHoldF       = (TextView) findViewById(R.id.lblPlantaOnHoldF);
        final TextView lblGHOnHoldF           = (TextView) findViewById(R.id.lblGHOnHoldF);
        final TextView lblCajasOnHoldF        = (TextView) findViewById(R.id.lblCajasOnHoldF);

        final LinearLayout btnLiberarFolio    = (LinearLayout) findViewById(R.id.btnLiberarFolio);
        final LinearLayout btnMermarFolio     = (LinearLayout) findViewById(R.id.btnMermarFolio);

        lblProductoOnHold.setText(productoOnHold.getvProducto());
        lblFechaOnHoldF.setText(productoOnHold.getFechaOnHold());
        lblRazonOnHoldF.setText(productoOnHold.getnRazon());
        lblPlantaOnHoldF.setText(productoOnHold.getnPlanta());
        lblGHOnHoldF.setText(productoOnHold.getGH());
        lblCajasOnHoldF.setText(productoOnHold.getCajasOnHold() + "/" + productoOnHold.getLibrasOnHold());

        btnLiberarFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoLiberarProducto(productoOnHold);
            }
        });

        btnMermarFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoMermarProducto(productoOnHold);
            }
        });
    }

    public void mostrarDialogoLiberarProducto(final ProductoOnHold producto){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.adaptador_liberar, null);

        lblProductoOnHoldLP     = (TextView) dialoglayout.findViewById(R.id.lblProductoOnHold);
        lblFechaOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblFechaOnHoldF);
        lblRazonOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblRazonOnHoldF);
        lblPlantaOnHoldLP       = (TextView) dialoglayout.findViewById(R.id.lblPlantaOnHoldF);
        lblGHOnHoldLP           = (TextView) dialoglayout.findViewById(R.id.lblGHOnHoldF);
        lblCajasOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblCajasOnHoldF);

        btnLiberarFolioLP       = (LinearLayout) dialoglayout.findViewById(R.id.lytContenedorLiberar);
        btnMermarFolioLP        = (LinearLayout) dialoglayout.findViewById(R.id.lytContenedorMermar);

        cboxCalidadLP           = (Spinner) dialoglayout.findViewById(R.id.spCalidadLP);
        cboxRazonLP             = (Spinner) dialoglayout.findViewById(R.id.spRazonLP);
        txtCajasLP              = (EditText) dialoglayout.findViewById(R.id.txtCajasLP);
        txtLibrasLP             = (EditText) dialoglayout.findViewById(R.id.txtLibrasLP);
        txtComentariosLP        = (EditText) dialoglayout.findViewById(R.id.txtComentariosLP);

        btnAceptarLP            = (Button) dialoglayout.findViewById(R.id.btnAceptarLP);
        btnCancelarLP           = (Button) dialoglayout.findViewById(R.id.btnCancelarLP);

        btnLiberarFolioLP.setVisibility(View.GONE);
        btnMermarFolioLP.setVisibility(View.GONE);

        lblProductoOnHoldLP.setText(producto.getvProducto());
        lblFechaOnHoldLP.setText(producto.getFechaOnHold());
        lblRazonOnHoldLP.setText(producto.getnRazon());
        lblPlantaOnHoldLP.setText(producto.getnPlanta());
        lblGHOnHoldLP.setText(producto.getGH());
        lblCajasOnHoldLP.setText(producto.getCajasOnHold() + "/" + producto.getLibrasOnHold());
        txtCajasLP.setText(producto.getCajasOnHold()+"");
        txtLibrasLP.setText(producto.getLibrasOnHold()+"");

        llenarSpinnerCalidad();
        llenarSpinnerRazonesLiberar();

        final AlertDialog.Builder ad = new AlertDialog.Builder(nContext);
        ad.setView(dialoglayout);
        ad.setIcon(R.drawable.alerticon);
        ad.setTitle("Liberar Producto de On Hold");
        ad.setCancelable(false);

        alertDialog = ad.create();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialog.show();

        btnAceptarLP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(validarNumeroCajas(txtCajasLP.getText().toString()) && validarCantidadCajas(producto.getCajasOnHold()) && validarNumeroLibras(txtLibrasLP.getText().toString()) && validarCantidadLibras(producto.getLibrasOnHold())){
                    int tCajas = Integer.parseInt(txtCajasLP.getText().toString());
                    double dLibras = Double.parseDouble(txtLibrasLP.getText().toString());

                    if((tCajas == producto.getCajasOnHold() && dLibras != producto.getLibrasOnHold()) || (dLibras == producto.getLibrasOnHold() && tCajas != producto.getCajasOnHold())){
                        mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                    } else {
//                        if(!txtComentariosLP.getText().toString().isEmpty()){
                            mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_out_on_hold), ACCION_LIBERAR, producto);
//                        } else {
//                            mostrarDialogo("Mensaje", "El campo de comentarios no debe estar vacío");
//                        }
                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                }
            }
        });

        btnCancelarLP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                alertDialog.dismiss();
                //OnHold.recargarLista();
            }
        });
    }

    public void mostrarDialogoMermarProducto(final ProductoOnHold producto){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.adaptador_mermar, null);

        lblProductoOnHoldLP     = (TextView) dialoglayout.findViewById(R.id.lblProductoOnHold);
        lblFechaOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblFechaOnHoldF);
        lblRazonOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblRazonOnHoldF);
        lblPlantaOnHoldLP       = (TextView) dialoglayout.findViewById(R.id.lblPlantaOnHoldF);
        lblGHOnHoldLP           = (TextView) dialoglayout.findViewById(R.id.lblGHOnHoldF);
        lblCajasOnHoldLP        = (TextView) dialoglayout.findViewById(R.id.lblCajasOnHoldF);

        btnLiberarFolioLP       = (LinearLayout) dialoglayout.findViewById(R.id.lytContenedorLiberar);
        btnMermarFolioLP        = (LinearLayout) dialoglayout.findViewById(R.id.lytContenedorMermar);

        cboxDepartamentoMP      = (Spinner) dialoglayout.findViewById(R.id.spDepartamentoMP);
        cboxRazonMP             = (Spinner) dialoglayout.findViewById(R.id.spRazonMP);
        cboxDisposicionMP       = (Spinner) dialoglayout.findViewById(R.id.spDisposicionMP);
        txtCajasMP              = (EditText) dialoglayout.findViewById(R.id.txtCajasMP);
        txtLibrasMP             = (EditText) dialoglayout.findViewById(R.id.txtLibrasMP);
        txtComentariosMP        = (EditText) dialoglayout.findViewById(R.id.txtComentariosMP);

        btnAceptarMP            = (Button) dialoglayout.findViewById(R.id.btnAceptarMP);
        btnCancelarMP           = (Button) dialoglayout.findViewById(R.id.btnCancelarMP);

        btnLiberarFolioLP.setVisibility(View.GONE);
        btnMermarFolioLP.setVisibility(View.GONE);

        lblProductoOnHoldLP.setText(producto.getvProducto());
        lblFechaOnHoldLP.setText(producto.getFechaOnHold());
        lblRazonOnHoldLP.setText(producto.getnRazon());
        lblPlantaOnHoldLP.setText(producto.getnPlanta());
        lblGHOnHoldLP.setText(producto.getGH());
        lblCajasOnHoldLP.setText(producto.getCajasOnHold() + "/" + producto.getLibrasOnHold());
        txtCajasMP.setText(producto.getCajasOnHold()+"");
        txtLibrasMP.setText(producto.getLibrasOnHold()+"");

        llenarSpinnerDepartamentos(producto.getIdPlanta());
        llenarSpinnerRazonesMerma(arregloDepartamentoM[cboxDepartamentoMP.getSelectedItemPosition()].getIdDepartamento());
        llenarSpinnerDisposicion();

        final AlertDialog.Builder ad = new AlertDialog.Builder(nContext);
        ad.setView(dialoglayout);
        ad.setIcon(R.drawable.alerticon);
        ad.setTitle("Mermar Producto de On Hold");
        ad.setCancelable(false);

        alertDialog = ad.create();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialog.show();

        cboxDepartamentoMP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                llenarSpinnerRazonesMerma(arregloDepartamentoM[cboxDepartamentoMP.getSelectedItemPosition()].getIdDepartamento());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnAceptarMP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(validarNumeroCajas(txtCajasMP.getText().toString()) && validarCantidadCajas(producto.getCajasOnHold()) && validarNumeroLibras(txtLibrasMP.getText().toString()) && validarCantidadLibras(producto.getLibrasOnHold())){
                    int tCajas = Integer.parseInt(txtCajasMP.getText().toString());
                    double dLibras = Double.parseDouble(txtLibrasMP.getText().toString());

                    if((tCajas == producto.getCajasOnHold() && dLibras != producto.getLibrasOnHold()) || (dLibras == producto.getLibrasOnHold() && tCajas != producto.getCajasOnHold())){
                        mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                    } else {
//                      if(!txtComentariosMP.getText().toString().isEmpty()){
                            mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_waste_on_hold), ACCION_MERMAR, producto);
//                      } else {
//                          mostrarDialogo("Mensaje", "El campo de comentarios no debe estar vacío");
//                      }
                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                }
            }
        });

        btnCancelarMP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                alertDialog.dismiss();
                //OnHold.recargarLista();
            }
        });
    }

    public boolean validarNumeroCajas(String numero){
        cajasSeleccionadas = 0;

        try{
            cajasSeleccionadas = Integer.parseInt(numero);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

    public boolean validarNumeroLibras(String numero){
        librasSeleccionadas = 0.0;

        try{
            librasSeleccionadas = Double.parseDouble(numero);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

    public boolean validarCantidadCajas(int cajasDisponibles){
        if(cajasSeleccionadas > 0 && cajasSeleccionadas <= cajasDisponibles)
            return true;
        else
            return false;
    }

    public boolean validarCantidadLibras(double librasDisponibles){
        if(librasSeleccionadas > 0 && librasSeleccionadas <= librasDisponibles)
            return true;
        else
            return false;
    }

    private void llenarSpinnerCalidad() {
        String nombreCalidad[];
        bdWMP.abrir();
        arregloCalidad = bdWMP.getTiposCalidad();
        bdWMP.cerrar();

        nombreCalidad = new String[arregloCalidad.length];

        for(int i = 0; i < nombreCalidad.length; i++){
            nombreCalidad[i] = arregloCalidad[i].getNombreCalidad();
        }

        adaptadorSPCalidad = new AdaptadorSpinner(nContext, nombreCalidad);
        cboxCalidadLP.setAdapter(adaptadorSPCalidad);
    }

    private void llenarSpinnerRazonesLiberar() {
        String nombreRazones[];
        bdWMP.abrir();
        arregloRazon = bdWMP.getRazonesParaLiberar();
        bdWMP.cerrar();

        nombreRazones = new String[arregloRazon.length];

        for(int i = 0; i < nombreRazones.length; i++){
            nombreRazones[i] = arregloRazon[i].getNombreRazon();
        }

        adaptadorSPRazon = new AdaptadorSpinner(nContext, nombreRazones);
        cboxRazonLP.setAdapter(adaptadorSPRazon);
    }

    public void llenarSpinnerDepartamentos(int idPlanta){
        bdWMP.abrir();
        arregloDepartamentoM = bdWMP.getDepartamentosPorPlanta(idPlanta);
        bdWMP.cerrar();

        String listaDepartamentos[] = new String[arregloDepartamentoM.length];

        for(int i = 0; i < arregloDepartamentoM.length; i++){
            listaDepartamentos[i] = arregloDepartamentoM[i].getNombreDepartamento();
        }

        adaptadorSPDepartamentoM = new AdaptadorSpinner(nContext, listaDepartamentos);
        cboxDepartamentoMP.setAdapter(adaptadorSPDepartamentoM);
    }

    public void llenarSpinnerRazonesMerma(int idDepartamento){
        bdWMP.abrir();
        arregloRazonM = bdWMP.getRazonesPorDepartamento(idDepartamento, 0);
        bdWMP.cerrar();

        String listaRazones[] = new String[arregloRazonM.length];

        for(int i = 0; i < arregloRazonM.length; i++){
            listaRazones[i] = arregloRazonM[i].getNombreRazon();
        }

        adaptadorSPRazonM = new AdaptadorSpinner(nContext, listaRazones);
        cboxRazonMP.setAdapter(adaptadorSPRazonM);
    }

    public void llenarSpinnerDisposicion(){
        bdWMP.abrir();
        arregloDisposicionM = bdWMP.getDispocisionesMerma();
        bdWMP.cerrar();

        String listaDisposicion[] = new String[arregloDisposicionM.length];

        for(int i = 0; i < arregloDisposicionM.length; i++){
            listaDisposicion[i] = arregloDisposicionM[i].getNombreDisposicion();
        }

        adaptadorSPDisposicionM = new AdaptadorSpinner(nContext, listaDisposicion);
        cboxDisposicionMP.setAdapter(adaptadorSPDisposicionM);
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

    public void mostrarDialogoConfirmacion(String titulo, String mensaje, final int accion, final ProductoOnHold producto){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(mensaje);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(accion == ACCION_LIBERAR){
                    liberarProductoOnHold(producto);
                } else if(accion == ACCION_MERMAR){
                    mermarProductoOnHold(producto);
                }
            }
        });

        alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void liberarProductoOnHold(final ProductoOnHold producto){
        final int idCalidad = arregloCalidad[cboxCalidadLP.getSelectedItemPosition()].getIdCalidad();
        final int idReason = arregloRazon[cboxRazonLP.getSelectedItemPosition()].getIdRazon();
        final String vComments = txtComentariosLP.getText().toString();
        final String vUser = "DBTAPP";

        if(producto.getTipoProducto() == TIPO_FOLIO) {
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //new AsyncTaskInsDelFolioOnHold(config.rutaWebServerOmar + "/insDelFolioOnHold", producto, idCalidad, idReason, cajasSeleccionadas, librasSeleccionadas, vComments, vUser).execute();
                    new AsyncTaskInsDelFolioOnHold(config.rutaWebServerOmar + "/insDelFolioOnHoldWithLbs", producto, idCalidad, idReason, cajasSeleccionadas, librasSeleccionadas, vComments, vUser).execute();
                }
            });
        } else if(producto.getTipoProducto() == TIPO_PALLET){
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AsyncTaskInsDelPalletOnHold(config.rutaWebServerOmar + "/insDelPalletOnHold", producto, idCalidad, idReason, cajasSeleccionadas, vComments, vUser).execute();
                }
            });
        }
    }

    public void mermarProductoOnHold(final ProductoOnHold producto){
        final int idDepartament = arregloDepartamentoM[cboxDepartamentoMP.getSelectedItemPosition()].getIdDepartamento();
        final int idReason = arregloRazonM[cboxRazonMP.getSelectedItemPosition()].getIdRazon();
        final String vDisposition = arregloDisposicionM[cboxDisposicionMP.getSelectedItemPosition()].getIdDisposicion();
        final String vComments = txtComentariosMP.getText().toString();
        final String vUser = "DBTAPP";

//        if(producto.getTipoProducto() == TIPO_FOLIO) {
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //new AsyncTaskMermarProductoOnHold(config.rutaWebServerOmar + "/mermarProductoOnHold", producto, idDepartament, idReason, vDisposition, cajasSeleccionadas, vComments, vUser).execute();
                    new AsyncTaskMermarProductoOnHold(config.rutaWebServerOmar + "/mermarProductoOnHoldWithLbs", producto, idDepartament, idReason, vDisposition, cajasSeleccionadas, librasSeleccionadas, vComments, vUser).execute();
                }
            });
//        } else if(producto.getTipoProducto() == TIPO_PALLET){
//            nContext.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    new AsyncTaskMermarPalletOnHold(config.rutaWebServerOmar + "/mermarProductoOnHold", producto, idDepartament, idReason, idDisposition, cajasSeleccionadas, vComments, vUser).execute();
//                }
//            });
//        }
    }

    public class AsyncTaskInsDelFolioOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private ProductoOnHold mProducto;
        private int mIdCalidad;
        private int mIdReason;
        private int mCases;
        private double mLbs;
        private String mComments;
        private String mUser;

        public AsyncTaskInsDelFolioOnHold(String url, ProductoOnHold producto, int idCalidad, int idReason, int iCases, double dLbs, String vComments, String vUser) {
            this.mURL = url;
            this.mProducto = producto;
            this.mIdCalidad = idCalidad;
            this.mIdReason = idReason;
            this.mCases = iCases;
            this.mLbs = dLbs;
            this.mComments = vComments;
            this.mUser = vUser;

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("accion", "2"));
                params.add(new BasicNameValuePair("idOnHold", mProducto.getIdProductoOnHold()+""));
                params.add(new BasicNameValuePair("idProduct", mProducto.getIdProduct()+""));
                params.add(new BasicNameValuePair("idDepartment", "0"));
                params.add(new BasicNameValuePair("idQuality", mIdCalidad+""));
                params.add(new BasicNameValuePair("idReason", mIdReason+""));
                params.add(new BasicNameValuePair("vFolio", mProducto.getvProducto()));
                params.add(new BasicNameValuePair("iCases", mCases+""));
                params.add(new BasicNameValuePair("dLbs", mLbs+""));
                params.add(new BasicNameValuePair("vComments", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("idLocacion", mProducto.getIdLocation()+""));
                params.add(new BasicNameValuePair("xPosition", "0"));
                params.add(new BasicNameValuePair("yPosition", "0"));
                params.add(new BasicNameValuePair("zPosition", "0"));
                params.add(new BasicNameValuePair("idPlanta", mProducto.getIdPlanta()+""));

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
                String error = "";

                if(res.contains("idError")){
                    for(int i = 0; i < JSONArrayRespuesta.length(); i++) {
                        rowJSON = JSONArrayRespuesta.getJSONObject(i);
                        error = rowJSON.getString("description");
                    }

                    mostrarDialogo("Error al liberar producto", error);
                } else if(res.contains("indicador")){
                    mostrarDialogo("Mensaje", "Liberación correcta");
                } else {
                    mostrarDialogo("Error al liberar producto", "Error al liberar producto");
                }

                alertDialog.dismiss();
                OnHold.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }

    public class AsyncTaskInsDelPalletOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private ProductoOnHold mProducto;
        private int mIdCalidad;
        private int mIdReason;
        private int mCases;
        private String mComments;
        private String mUser;

        public AsyncTaskInsDelPalletOnHold(String url, ProductoOnHold producto, int idCalidad, int idReason, int iCases, String vComments, String vUser) {
            this.mURL = url;
            this.mProducto = producto;
            this.mIdCalidad = idCalidad;
            this.mIdReason = idReason;
            this.mCases = iCases;
            this.mComments = vComments;
            this.mUser = vUser;

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("accion", "2"));
                params.add(new BasicNameValuePair("idOnHold", mProducto.getIdProductoOnHold()+""));
                params.add(new BasicNameValuePair("idProductPallet", mProducto.getIdProduct()+""));
                params.add(new BasicNameValuePair("idDepartment", "0"));
                params.add(new BasicNameValuePair("idQuality", mIdCalidad+""));
                params.add(new BasicNameValuePair("idReason", mIdReason+""));
                params.add(new BasicNameValuePair("vPallet", mProducto.getvProducto()));
                params.add(new BasicNameValuePair("iCases", mCases+""));
                params.add(new BasicNameValuePair("vComments", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("idLocacion", mProducto.getIdLocation()+""));
                params.add(new BasicNameValuePair("xPosition", "0"));
                params.add(new BasicNameValuePair("yPosition", "0"));
                params.add(new BasicNameValuePair("zPosition", "0"));
                params.add(new BasicNameValuePair("idPlanta", mProducto.getIdPlanta()+""));

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
                String error = "";

                if(res.contains("idError")){
                    for(int i = 0; i < JSONArrayRespuesta.length(); i++) {
                        rowJSON = JSONArrayRespuesta.getJSONObject(i);
                        error = rowJSON.getString("description");
                    }

                    mostrarDialogo("Error al liberar producto", error);
                } else if(res.contains("indicador")){
                    mostrarDialogo("Mensaje", "Liberación correcta");
                } else {
                    mostrarDialogo("Error al liberar producto", "Error al liberar producto");
                }

                alertDialog.dismiss();
                OnHold.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }

    public class AsyncTaskMermarProductoOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private ProductoOnHold mProducto;
        private int mIdDepartment;
        private int mIdReason;
        private String mDisposition;
        private int mCases;
        private double mLbs;
        private String mComments;
        private String mUser;

        public AsyncTaskMermarProductoOnHold(String url, ProductoOnHold producto, int idDepartament, int idReason, String vDisposition, int iCases, double dLbs, String vComments, String vUser) {
            this.mURL = url;
            this.mProducto = producto;
            this.mIdDepartment = idDepartament;
            this.mIdReason = idReason;
            this.mDisposition = vDisposition;
            this.mCases = iCases;
            this.mLbs = dLbs;
            this.mComments = vComments;
            this.mUser = vUser;

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tipoProducto", mProducto.getTipoProducto()+""));
                params.add(new BasicNameValuePair("idOnHold", mProducto.getIdProductoOnHold()+""));
                params.add(new BasicNameValuePair("idProduct", mProducto.getIdProduct()+""));
                params.add(new BasicNameValuePair("idDepartment", mIdDepartment+""));
                params.add(new BasicNameValuePair("idReason", mIdReason+""));
                params.add(new BasicNameValuePair("vDisposition", mDisposition));
                params.add(new BasicNameValuePair("vProduct", mProducto.getvProducto()));
                params.add(new BasicNameValuePair("iCases", mCases+""));
                params.add(new BasicNameValuePair("dLbs", mLbs+""));
                params.add(new BasicNameValuePair("vComments", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("idLocation", mProducto.getIdLocation()+""));
                params.add(new BasicNameValuePair("xPosition", "0"));
                params.add(new BasicNameValuePair("yPosition", "0"));
                params.add(new BasicNameValuePair("zPosition", "0"));

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
                String error = "";

                if(res.contains("idError")){
                    for(int i = 0; i < JSONArrayRespuesta.length(); i++) {
                        rowJSON = JSONArrayRespuesta.getJSONObject(i);
                        error = rowJSON.getString("description");
                    }

                    mostrarDialogo("Error al mermar producto", error);
                } else if(res.contains("indicador")){
                    mostrarDialogo("Mensaje", "Merma correcta");
                } else {
                    mostrarDialogo("Error al mermar producto", "Error al mermar producto");
                }

                alertDialog.dismiss();
                OnHold.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }
}
