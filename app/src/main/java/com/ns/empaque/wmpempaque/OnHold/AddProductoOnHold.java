package com.ns.empaque.wmpempaque.OnHold;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
 * Created by Christopher BA on 17/03/2017.
 */

public class AddProductoOnHold {

    private static Activity nContext;
    private static LayoutInflater inflater;
    private static LinearLayout lytFolioOnHold, lytPalletOnHold;
    private static ImageView btnBuscarProducto;
    private static TextView lblCodigoLocacion, lblNombreLocacion, lblEmbalajesPermitidos;
    private static TextView lblCodigoFolio, lblNombrePlantaF, lblGHF, lblCajasDispF, lblCalidadF, lblFechaArriboF;
    private static TextView lblNombrePlantaP, lblCodigoPallet, lblGHP, lblSKUP, lblCajasDispP, lblCalidadP;
    private static EditText txtCodigoProducto, txtCajasF, txtLbsF, txtComentariosF;
    private static EditText txtCajasP, txtComentariosP;
    private static Spinner cboxDepartamentoF, cboxRazonF;
    private static Spinner cboxDepartamentoP, cboxRazonP;
    private static AdaptadorSpinner adaptadorDepartamentos, adaptadorRazones;
    private static Button btnSendOnHoldF, btnCancelOnHoldF, btnSendOnHoldP,btnCancelOnHoldP;
    private static Departamento arregloDepartamentos[];
    private static Razon arregloRazones[];
    private static locacion loc;
    private static Regex regex[];
    private static int tipoEmbalaje, idProduct, idPlanta, cajasDisponibles, cajasSeleccionadas;
    private static double librasDisponibles, librasSeleccionadas;
    private static int xPosicion, yPosicion, zPosicion;
    private static BaseDatos bdWMP;

    private static final int TIPO_FOLIO = 1;
    private static final int TIPO_PALLET = 2;

    public AddProductoOnHold(Activity nContext, locacion loc, Regex regex[], int x, int y, int z) {
        this.nContext = nContext;
        this.loc = loc;
        this.regex = regex;
        this.xPosicion = x;
        this.yPosicion = y;
        this.zPosicion = z;

        bdWMP = new BaseDatos(nContext);
    }

    public static View getPopUpView(){
        inflater = nContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_producto_on_hold, null, true);

        lblCodigoLocacion       = (TextView) view.findViewById(R.id.lblCodigoLocacion);
        lblNombreLocacion       = (TextView) view.findViewById(R.id.lblNombreLocacion);
        lblEmbalajesPermitidos  = (TextView) view.findViewById(R.id.lblEmbalajesPermitidos);

        txtCodigoProducto       = (EditText) view.findViewById(R.id.txtCodigoProducto);
        txtComentariosF         = (EditText) view.findViewById(R.id.txtComentariosF);
        txtCajasF               = (EditText) view.findViewById(R.id.txtCajasF);
        txtLbsF                 = (EditText) view.findViewById(R.id.txtLbsF);

        lytFolioOnHold          = (LinearLayout) view.findViewById(R.id.lytFolioOnHold);
        lblCodigoFolio          = (TextView) view.findViewById(R.id.lblCodigoFolio);
        lblNombrePlantaF        = (TextView) view.findViewById(R.id.lblNombrePlantaF);
        lblGHF                  = (TextView) view.findViewById(R.id.lblGHF);
        lblCajasDispF           = (TextView) view.findViewById(R.id.lblCajasDispF);
        lblCalidadF             = (TextView) view.findViewById(R.id.lblCalidadF);
        lblFechaArriboF         = (TextView) view.findViewById(R.id.lblFechaArriboF);

        cboxDepartamentoF       = (Spinner) view.findViewById(R.id.spDepartamentoF);
        cboxRazonF              = (Spinner) view.findViewById(R.id.spRazonF);

        txtComentariosP         = (EditText) view.findViewById(R.id.txtComentariosP);
        txtCajasP               = (EditText) view.findViewById(R.id.txtCajasP);

        lytPalletOnHold         = (LinearLayout) view.findViewById(R.id.lytPalletOnHold);
        lblNombrePlantaP        = (TextView) view.findViewById(R.id.lblNombrePlantaP);
        lblCodigoPallet         = (TextView) view.findViewById(R.id.lblCodigoPallet);
        lblSKUP                 = (TextView) view.findViewById(R.id.lblSKUP);
        lblGHP                  = (TextView) view.findViewById(R.id.lblGHP);
        lblCajasDispP           = (TextView) view.findViewById(R.id.lblCajasDispP);
        lblCalidadP             = (TextView) view.findViewById(R.id.lblCalidadP);

        cboxDepartamentoP       = (Spinner) view.findViewById(R.id.spDepartamentoP);
        cboxRazonP              = (Spinner) view.findViewById(R.id.spRazonP);

        btnSendOnHoldF          = (Button) view.findViewById(R.id.btnSendOnHoldF);
        btnCancelOnHoldF        = (Button) view.findViewById(R.id.btnCancelOnHoldF);
        btnSendOnHoldP          = (Button) view.findViewById(R.id.btnSendOnHoldP);
        btnCancelOnHoldP          = (Button) view.findViewById(R.id.btnCancelOnHoldP);
        btnBuscarProducto       = (ImageView) view.findViewById(R.id.btnBuscarProducto);

        cargarDatosLocacion();
        eventos();

        return view;
    }

    public static void cargarDatosLocacion(){
        String regexAcum = "\n\n" + nContext.getString(R.string.none);
        lblCodigoLocacion.setText(loc.getCode());
        lblNombreLocacion.setText(loc.getDescription());

        if(regex.length > 0){
            regexAcum = "\n";

            for(int i = 0; i < regex.length; i++)
                regexAcum += "\n - " + regex[i].getNombreEmbalaje();
        }

        lblEmbalajesPermitidos.setText(nContext.getString(R.string.embalajes_on_location) + regexAcum);
    }

    public static void eventos(){
        txtCodigoProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")){
                    String str = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)", "");
                    buscarProducto(str);
                    txtCodigoProducto.setText("");
                }
            }
        });

        btnBuscarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMPEmpaque.scanOnHold = 2;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        cboxDepartamentoF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarDatosSpinnerRazones(arregloDepartamentos[cboxDepartamentoF.getSelectedItemPosition()].getIdDepartamento(), TIPO_FOLIO);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnSendOnHoldF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarNumeroCajas(txtCajasF.getText().toString()) && validarNumeroLibras(txtLbsF.getText().toString()) && validarCantidadCajas() && validarCantidadLibras()){
                    int tCajas = Integer.parseInt(txtCajasF.getText().toString());
                    double dLibras = Double.parseDouble(txtLbsF.getText().toString());

                    if((tCajas == cajasDisponibles && dLibras != librasDisponibles) || (dLibras == librasDisponibles && tCajas != cajasDisponibles)) {
                        mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                    } else {
//                        if(!txtComentariosF.getText().toString().isEmpty()){
                            mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_in_on_hold), TIPO_FOLIO);
//                        } else {
//                            mostrarDialogo("Mensaje", "El campo de comentarios no debe estar vacío");
//                        }
                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                }
            }
        });

        btnCancelOnHoldF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytFolioOnHold.setVisibility(View.GONE);
            }
        });

        cboxDepartamentoP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarDatosSpinnerRazones(arregloDepartamentos[cboxDepartamentoP.getSelectedItemPosition()].getIdDepartamento(), TIPO_PALLET);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnSendOnHoldP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarNumeroCajas(txtCajasP.getText().toString()) && validarCantidadCajas()){
                    mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_in_on_hold), TIPO_PALLET);
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number));
                }
            }
        });

        btnCancelOnHoldP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytPalletOnHold.setVisibility(View.GONE);
            }
        });
    }

    public static void buscarProducto(final String str) {
        tipoEmbalaje = obtenerTipoEmbalajePorRegex(str);
        Log.d("EMBALAJE", tipoEmbalaje+"");

        if(tipoEmbalaje != 0){
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AsyncTaskGetProductoOnHold(config.rutaWebServerOmar + "/getProductInfo", tipoEmbalaje, str, loc.getFarm()).execute();
                }
            });
        } else {
            mostrarDialogo("Mensaje", nContext.getString(R.string.embalaje_not_allowed));
        }
    }

    public static int obtenerTipoEmbalajePorRegex(String str){
        int tipoEmbalaje = 0;

        for (int i = 0; i < regex.length; i++){
            if(str.matches(regex[i].getRegex()))
                tipoEmbalaje = regex[i].getIdEmbalaje();
        }

        return tipoEmbalaje;
    }

    public static void cargarDatosSpinnerDepartamentos(int idPlanta, int opc){
        bdWMP.abrir();
        arregloDepartamentos = bdWMP.getDepartamentosPorPlanta(idPlanta);
        bdWMP.cerrar();

        String listaDepartamentos[] = new String[arregloDepartamentos.length];

        for(int i = 0; i < arregloDepartamentos.length; i++){
            listaDepartamentos[i] = arregloDepartamentos[i].getNombreDepartamento();
        }

        adaptadorDepartamentos = new AdaptadorSpinner(nContext, listaDepartamentos);

        if(opc == TIPO_FOLIO) {
            cboxDepartamentoF.setAdapter(adaptadorDepartamentos);
        } else if(opc == TIPO_PALLET){
            cboxDepartamentoP.setAdapter(adaptadorDepartamentos);
        }
    }

    public static void cargarDatosSpinnerRazones(int idDepartamento, int opc){
        bdWMP.abrir();
        arregloRazones = bdWMP.getRazonesPorDepartamento(idDepartamento, 0);
        bdWMP.cerrar();

        String listaRazones[] = new String[arregloRazones.length];

        for(int i = 0; i < arregloRazones.length; i++){
            listaRazones[i] = arregloRazones[i].getNombreRazon();
        }

        adaptadorRazones = new AdaptadorSpinner(nContext, listaRazones);

        if(opc == TIPO_FOLIO) {
            cboxRazonF.setAdapter(adaptadorRazones);
        } else if(opc == TIPO_PALLET){
            cboxRazonP.setAdapter(adaptadorRazones);
        }
    }

    public static boolean validarNumeroCajas(String numero){
        cajasSeleccionadas = 0;

        try{
            cajasSeleccionadas = Integer.parseInt(numero);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

    public static boolean validarNumeroLibras(String numero){
        librasSeleccionadas = 0;

        try{
            librasSeleccionadas = Double.parseDouble(numero);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

    public static boolean validarCantidadCajas(){
        if(cajasSeleccionadas > 0 && cajasSeleccionadas <= cajasDisponibles)
            return true;
        else
            return false;
    }

    public static boolean validarCantidadLibras(){
        if(librasSeleccionadas > 0 && librasSeleccionadas <= librasDisponibles)
            return true;
        else
            return false;
    }

    public static void enviarFolioOnHold(){
        final int idDepartment = arregloDepartamentos[cboxDepartamentoF.getSelectedItemPosition()].getIdDepartamento();
        final int idReason = arregloRazones[cboxRazonF.getSelectedItemPosition()].getIdRazon();
        final String vFolio = lblCodigoFolio.getText().toString();
        final String vComments = txtComentariosF.getText().toString();
        final String vUser = "DBTAPP";
        final int idLocacion = loc.getIdLocation();

        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //new AsyncTaskInsDelFolioOnHold(config.rutaWebServerOmar + "/insDelFolioOnHold", idProduct, idDepartment, idReason, vFolio, cajasSeleccionadas, librasSeleccionadas, vComments, vUser, idLocacion, xPosicion, yPosicion, zPosicion, idPlanta).execute();
                new AsyncTaskInsDelFolioOnHold(config.rutaWebServerOmar + "/insDelFolioOnHoldWithLbs", idProduct, idDepartment, idReason, vFolio, cajasSeleccionadas, librasSeleccionadas, vComments, vUser, idLocacion, xPosicion, yPosicion, zPosicion, idPlanta).execute();
            }
        });
    }

    public static void enviarPalletOnHold(){
        final int idDepartment = arregloDepartamentos[cboxDepartamentoP.getSelectedItemPosition()].getIdDepartamento();
        final int idReason = arregloRazones[cboxRazonP.getSelectedItemPosition()].getIdRazon();
        final String vPallet = lblCodigoPallet.getText().toString();
        final String vComments = txtComentariosP.getText().toString();
        final String vUser = "DBTAPP";
        final int idLocacion = loc.getIdLocation();

        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskInsDelPalletOnHold(config.rutaWebServerOmar + "/insDelPalletOnHold", idProduct, idDepartment, idReason, vPallet, cajasSeleccionadas, vComments, vUser, idLocacion, xPosicion, yPosicion, zPosicion).execute();
            }
        });
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

    public static void mostrarDialogoConfirmacion(String titulo, String mensaje, final int opc){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(mensaje);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(opc == TIPO_FOLIO) {
                    enviarFolioOnHold();
                } else if(opc == TIPO_PALLET) {
                    enviarPalletOnHold();
                }
            }
        });

        alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static class AsyncTaskGetProductoOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private String mCodigoProducto;
        private int mTipoProducto;
        private int mIdPlanta;

        public AsyncTaskGetProductoOnHold(String url, int tipoProducto, String codigoProducto, int idPlanta) {
            this.mURL = url;
            this.mTipoProducto = tipoProducto;
            this.mCodigoProducto = codigoProducto;
            this.mIdPlanta = idPlanta;

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
                params.add(new BasicNameValuePair("tipoProducto", mTipoProducto+""));
                params.add(new BasicNameValuePair("codigoProducto", mCodigoProducto));
                params.add(new BasicNameValuePair("idPlanta", mIdPlanta+""));

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
                String error = "";

                if(res.contains("idError")){
                    for(int  i = 0; i < JSONArrayProducto.length(); i++) {
                        rowJSON = JSONArrayProducto.getJSONObject(i);
                        error = rowJSON.getString("description");
                    }

                    mostrarDialogo("Mensaje", error);
                } else {
                    if(mTipoProducto == TIPO_FOLIO){
                        for(int  i = 0; i < JSONArrayProducto.length(); i++) {
                            rowJSON = JSONArrayProducto.getJSONObject(i);

                            idPlanta = rowJSON.getInt("idPlanta");
                            cajasDisponibles = rowJSON.getInt("cajasDisp");
                            librasDisponibles = rowJSON.getDouble("lbsDisp");
                            idProduct = rowJSON.getInt("idProduct");

                            lblNombrePlantaF.setText(rowJSON.getString("nPlanta"));
                            lblCodigoFolio.setText(rowJSON.getString("vFolio"));
                            lblGHF.setText(rowJSON.getString("GH"));
                            lblCajasDispF.setText(cajasDisponibles + " / " + librasDisponibles);
                            lblCalidadF.setText(rowJSON.getString("calidad"));
                            lblFechaArriboF.setText(rowJSON.getString("fechaArribo") + " " + rowJSON.getString("horaArribo"));
                            txtCajasF.setText(cajasDisponibles+"");
                            txtLbsF.setText(librasDisponibles+"");
                        }

                        lytFolioOnHold.setVisibility(View.VISIBLE);

                        cargarDatosSpinnerDepartamentos(idPlanta, TIPO_FOLIO);
                        cargarDatosSpinnerRazones(arregloDepartamentos[cboxDepartamentoF.getSelectedItemPosition()].getIdDepartamento(), TIPO_FOLIO);
                    } else if(mTipoProducto == TIPO_PALLET){
                        for(int  i = 0; i < JSONArrayProducto.length(); i++) {
                            rowJSON = JSONArrayProducto.getJSONObject(i);

                            idPlanta = rowJSON.getInt("idPlanta");
                            cajasDisponibles = rowJSON.getInt("cajasDisp");
                            idProduct = rowJSON.getInt("idProductPallet");

                            lblNombrePlantaP.setText(rowJSON.getString("nPlanta"));
                            lblCodigoPallet.setText(rowJSON.getString("vPallet"));
                            lblGHP.setText(rowJSON.getString("GH"));
                            lblSKUP.setText(rowJSON.getString("SKU"));
                            lblCajasDispP.setText(cajasDisponibles+"");
                            lblCalidadP.setText(rowJSON.getString("calidad"));
                            txtCajasP.setText(cajasDisponibles+"");
                        }

                        lytPalletOnHold.setVisibility(View.VISIBLE);

                        cargarDatosSpinnerDepartamentos(idPlanta, TIPO_PALLET);
                        cargarDatosSpinnerRazones(arregloDepartamentos[cboxDepartamentoP.getSelectedItemPosition()].getIdDepartamento(), TIPO_PALLET);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }

    public static class AsyncTaskInsDelFolioOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private int mIdProduct;
        private int mIdDepartment;
        private int mIdReason;
        private String mFolio;
        private int mCases;
        private double mLbs;
        private String mComments;
        private String mUser;
        private int mIdLocacion;
        private int mXPosition;
        private int mYPosition;
        private int mZPosition;
        private int mIdPlanta;

        public AsyncTaskInsDelFolioOnHold(String url, int idProduct, int idDepartment, int idReason, String vFolio, int iCases, double dLbs, String vComments, String vUser, int idLocacion, int xPosition, int yPosition, int zPosition, int idPlanta) {
            this.mURL = url;
            this.mIdProduct = idProduct;
            this.mIdDepartment = idDepartment;
            this.mIdReason = idReason;
            this.mFolio = vFolio;
            this.mCases = iCases;
            this.mLbs = dLbs;
            this.mComments = vComments;
            this.mUser = vUser;

            this.mIdLocacion = idLocacion;
            this.mXPosition = xPosition;
            this.mYPosition = yPosition;
            this.mZPosition = zPosition;
            this.mIdPlanta = idPlanta;

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
                params.add(new BasicNameValuePair("accion", "1"));
                params.add(new BasicNameValuePair("idOnHold", "0"));
                params.add(new BasicNameValuePair("idProduct", mIdProduct+""));
                params.add(new BasicNameValuePair("idDepartment", mIdDepartment+""));
                params.add(new BasicNameValuePair("idQuality", "0"));
                params.add(new BasicNameValuePair("idReason", mIdReason+""));
                params.add(new BasicNameValuePair("vFolio", mFolio+""));
                params.add(new BasicNameValuePair("iCases", mCases+""));
                params.add(new BasicNameValuePair("dLbs", mLbs+""));
                params.add(new BasicNameValuePair("vComments", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("idLocacion", mIdLocacion+""));
                params.add(new BasicNameValuePair("xPosition", mXPosition+""));
                params.add(new BasicNameValuePair("yPosition", mYPosition+""));
                params.add(new BasicNameValuePair("zPosition", mZPosition+""));
                params.add(new BasicNameValuePair("idPlanta", mIdPlanta+""));

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

                    mostrarDialogo("Error al ingresar producto a On Hold", error);
                } else if(res.contains("indicador")){
                    mostrarDialogo("Mensaje", "Inserción correcta");
                } else {
                    mostrarDialogo("Error al ingresar producto a On Hold", "Error al ingresar producto a On Hold");
                }

                lytFolioOnHold.setVisibility(View.GONE);
                OnHold.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }

    public static class AsyncTaskInsDelPalletOnHold extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private int mIdProduct;
        private int mIdDepartment;
        private int mIdReason;
        private String mPallet;
        private int mCases;
        private String mComments;
        private String mUser;
        private int mIdLocacion;
        private int mXPosition;
        private int mYPosition;
        private int mZPosition;

        public AsyncTaskInsDelPalletOnHold(String url, int idProduct, int idDepartment, int idReason, String vPallet, int iCases, String vComments, String vUser, int idLocacion, int xPosition, int yPosition, int zPosition) {
            this.mURL = url;
            this.mIdProduct = idProduct;
            this.mIdDepartment = idDepartment;
            this.mIdReason = idReason;
            this.mPallet = vPallet;
            this.mCases = iCases;
            this.mComments = vComments;
            this.mUser = vUser;

            this.mIdLocacion = idLocacion;
            this.mXPosition = xPosition;
            this.mYPosition = yPosition;
            this.mZPosition = zPosition;

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
                params.add(new BasicNameValuePair("accion", "1"));
                params.add(new BasicNameValuePair("idOnHold", "0"));
                params.add(new BasicNameValuePair("idProductPallet", mIdProduct+""));
                params.add(new BasicNameValuePair("idDepartment", mIdDepartment+""));
                params.add(new BasicNameValuePair("idQuality", "0"));
                params.add(new BasicNameValuePair("idReason", mIdReason+""));
                params.add(new BasicNameValuePair("vPallet", mPallet+""));
                params.add(new BasicNameValuePair("iCases", mCases+""));
                params.add(new BasicNameValuePair("vComments", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("idLocacion", mIdLocacion+""));
                params.add(new BasicNameValuePair("xPosition", mXPosition+""));
                params.add(new BasicNameValuePair("yPosition", mYPosition+""));
                params.add(new BasicNameValuePair("zPosition", mZPosition+""));

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

                    mostrarDialogo("Error al ingresar producto a On Hold", error);
                } else if(res.contains("indicador")){
                    mostrarDialogo("Mensaje", "Inserción correcta");
                } else {
                    mostrarDialogo("Error al ingresar producto a On Hold", "Error al ingresar producto a On Hold");
                }

                lytPalletOnHold.setVisibility(View.GONE);
                OnHold.recargarLista();
            } catch(Exception ex){
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }
}
