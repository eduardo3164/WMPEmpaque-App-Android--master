package com.ns.empaque.wmpempaque.MermaLinea;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.MAP.locacion;
import com.ns.empaque.wmpempaque.Modelo.ItemClickSupport;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.OnHold.AdaptadorSpinner;
import com.ns.empaque.wmpempaque.OnHold.Departamento;
import com.ns.empaque.wmpempaque.OnHold.Disposicion;
import com.ns.empaque.wmpempaque.OnHold.Razon;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Christopher BA on 24/05/2017.
 */

public class MermaLinea {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static LinearLayout lytProductoMermado;
    private static LinearLayout fbtnAtras, fbtnNuevo;
    private static ImageView btnBuscarLinea;
    private static EditText txtCodigoLinea;
    private static TextView lblPlantaM, lblCodigoLinea, lblNombreLinea;
    private static BaseDatos bdWMP;
    private static locacion loc;
    private static Linea lineaSeleccionada;
    private static ProductoMermado productoMermado[];
    private static ProductoMermado productoMermadoSeleccionado;
    private static int xPosicion, yPosicion, zPosicion;
    private static RecyclerView rvMerma;

//    DIALOG MERMA
    public AlertDialog dialogAddMerma;

    private ArrayList<Integer> listaDepartamentosSRC;
    private ArrayList<Integer> listaRazonesSRC;
    private TextView lblCodigoLineaDM, lblNombreLineaDM, lblNombrePlantaDM;
    private EditText txtFechaDM, txtLibrasDM, txtComentariosDM;
    private Spinner cboxDepartamentoDM, cboxRazonDM, cboxDisposicionDM;
    private AdaptadorSpinner adaptadorDepartamentosDM, adaptadorRazonesDM, adaptadorDisposicionDM;
    private Button btnSendMermaDM, btnCancelMermaDM;
    private Departamento arregloDepartamentosDM[];
    private Razon arregloRazonesDM[];
    private Disposicion arregloDisposicionesDM[];
    private float librasSeleccionadas;
    public static SharedPreferences sharedPreferences;

    public MermaLinea(Activity nContext, RelativeLayout parent) {
        this.nContext = nContext;
        this.content = parent;

        bdWMP = new BaseDatos(nContext);
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
    }

    public void setView() {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.view_merma_linea, null, true);
        config.updateContent(content, v);

        lytProductoMermado      = (LinearLayout) v.findViewById(R.id.lytProductoMermado);
        btnBuscarLinea          = (ImageView) v.findViewById(R.id.btnBuscarLinea);
        txtCodigoLinea          = (EditText) v.findViewById(R.id.txtCodigoLinea);

//        txtCodigoLinea.setText("ZLINEA-11|0.0.0");

        lblPlantaM              = (TextView) v.findViewById(R.id.lblPlantaM);
        lblCodigoLinea          = (TextView) v.findViewById(R.id.lblCodigoLinea);
        lblNombreLinea          = (TextView) v.findViewById(R.id.lblNombreLinea);

        fbtnNuevo               = (LinearLayout) v.findViewById(R.id.fbtnNuevo);
        fbtnAtras               = (LinearLayout) v.findViewById(R.id.fbtnAtras);

        rvMerma                 = (RecyclerView) v.findViewById(R.id.rvMerma);

        eventos();
    }

    public void eventos() {
        ItemClickSupport.addTo(rvMerma).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                productoMermadoSeleccionado = productoMermado[position];
                mostrarDialogoMermar();
            }
        });

        fbtnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loc != null) {
                    mostrarDialogoMermar();
                } else {
                    mostrarDialogo("Mensaje", "Necesita escanear una línea");
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

        btnBuscarLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMPEmpaque.scanOnHold = 1;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        txtCodigoLinea.addTextChangedListener(new TextWatcher() {
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
                    txtCodigoLinea.setText("");
                }
            }
        });
    }

    public static void buscarLocacion(String str) {
        String locacion[] = str.split("\\|");
        Log.d("STR", str);

        if(locacion.length > 1){
            Log.d("LOCACION 0", locacion[0]);
            Log.d("LOCACION 1", locacion[1]);

            final String datosLinea[] = locacion[0].split("-");

            Log.d("DATOSLINEA 0", datosLinea[0]);
            Log.d("DATOSLINEA 1", datosLinea[1]);

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
                    loc = bdWMP.getLocationLineByCode(datosLinea[0], 6);
                    bdWMP.cerrar();

                    if (loc.getDescription() != null) {
                        nContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AsyncTaskGetProductoMermado(config.rutaWebServerOmar + "/getProductoMermado", datosLinea[1]).execute();
                            }
                        });
                    } else {
                        mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_line));
                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_line));
                }
            } else {
                mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_line));
            }
        } else {
            mostrarDialogo("Mensaje", nContext.getString(R.string.no_code_line));
        }
    }

    public static void recargarLista(){
        buscarLocacion(loc.getCode() + "-" + lineaSeleccionada.getIdLinea() + "|" + xPosicion + "." + yPosicion + "." + zPosicion);
    }

    public void mostrarDialogoPassword(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_password_admin, null);

        final TextView txtContrasena = (TextView) dialoglayout.findViewById(R.id.txtPasswordDialog);
        Button btnAceptar = (Button) dialoglayout.findViewById(R.id.btnAceptarPassword);
        Button btnCancelar = (Button) dialoglayout.findViewById(R.id.btnCancelPassword);

        final AlertDialog.Builder ad = new AlertDialog.Builder(nContext);
        ad.setView(dialoglayout);
        ad.setCancelable(false);

        final AlertDialog dialogContrasena = ad.create();
        dialogContrasena.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogContrasena.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtContrasena.getText().toString().equals(sharedPreferences.getString("vPassword", "123"))){
                    sharedPreferences.edit().putString("lastPassword", config.obtenerFechaHora()).commit();

                    dialogContrasena.dismiss();
                    Calendar calendario = Calendar.getInstance();
                    DatePickerDialog fromDatePickerDialog = new DatePickerDialog(nContext, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txtFechaDM.setText(year + "-" + formatear2Digitos(monthOfYear + 1) + "-" + formatear2Digitos(dayOfMonth));
                        }
                    }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

                    fromDatePickerDialog.show();
                } else {
                    Toast.makeText(nContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogContrasena.dismiss();
            }
        });
    }

    private String formatear2Digitos(int num){
        if(num < 10) return "0" + num;
        else  return num + "";
    }

    public void mostrarDialogoMermar(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.add_producto_merma, null);

        lblCodigoLineaDM          = (TextView) dialoglayout.findViewById(R.id.lblCodigoLinea);
        lblNombreLineaDM          = (TextView) dialoglayout.findViewById(R.id.lblNombreLinea);
        lblNombrePlantaDM         = (TextView) dialoglayout.findViewById(R.id.lblNombrePlanta);
        txtFechaDM                = (EditText) dialoglayout.findViewById(R.id.txtFechaMerma);

        txtComentariosDM          = (EditText) dialoglayout.findViewById(R.id.txtComentariosM);
        txtLibrasDM               = (EditText) dialoglayout.findViewById(R.id.txtLibrasM);

        cboxDepartamentoDM        = (Spinner) dialoglayout.findViewById(R.id.spDepartamentoM);
        cboxRazonDM               = (Spinner) dialoglayout.findViewById(R.id.spRazonM);
        cboxDisposicionDM         = (Spinner) dialoglayout.findViewById(R.id.spDisposicionM);

        btnSendMermaDM            = (Button) dialoglayout.findViewById(R.id.btnSendMerma);
        btnCancelMermaDM          = (Button) dialoglayout.findViewById(R.id.btnCancelMerma);

        cargarDatosLocacion();

        final AlertDialog.Builder ad = new AlertDialog.Builder(nContext);
        ad.setView(dialoglayout);
        ad.setIcon(R.drawable.alerticon);
        ad.setTitle("Mermar Producto en Línea");
        ad.setCancelable(false);

        dialogAddMerma = ad.create();
        dialogAddMerma.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogAddMerma.show();

        txtFechaDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bdWMP.abrir();
                boolean solicitarContrasena = bdWMP.solicitarContrasena(config.obtenerFechaHora(), sharedPreferences.getString("lastPassword", "2017-01-01 00:00:00"), sharedPreferences.getInt("iSleepPassword", 15));
                bdWMP.cerrar();

                if(solicitarContrasena){
                    mostrarDialogoPassword();
                } else {
                    Calendar calendario = Calendar.getInstance();
                    DatePickerDialog fromDatePickerDialog = new DatePickerDialog(nContext, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txtFechaDM.setText(year + "-" + formatear2Digitos(monthOfYear + 1) + "-" + formatear2Digitos(dayOfMonth));
                        }
                    }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

                    fromDatePickerDialog.show();
                }
            }
        });

        cboxDepartamentoDM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                config.actualizarSharedPreferencesInt(nContext, "idDepartamento", listaDepartamentosSRC.get(i));
                cargarDatosSpinnerRazones(arregloDepartamentosDM[cboxDepartamentoDM.getSelectedItemPosition()].getIdDepartamento());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        cboxRazonDM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                config.actualizarSharedPreferencesInt(nContext, "idRazon", listaRazonesSRC.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnSendMermaDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarNumero(txtLibrasDM.getText().toString()) && validarCantidadLibras()){
//                    if(!txtComentariosM.getText().toString().isEmpty()){
                    if(productoMermadoSeleccionado != null)
                        mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_edit_product_waste));
                    else
                        mostrarDialogoConfirmacion("Mensaje", nContext.getString(R.string.confirm_product_waste));
//                    } else {
//                        mostrarDialogo("Mensaje", "El campo de comentarios no debe estar vacío");
//                    }
                } else {
                    mostrarDialogo("Mensaje", nContext.getString(R.string.incorrect_number_pounds));
                }
            }
        });

        btnCancelMermaDM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(productoMermadoSeleccionado != null)
                    productoMermadoSeleccionado = null;

                dialogAddMerma.dismiss();
            }
        });
    }

    public void cargarDatosLocacion(){
        bdWMP.abrir();
        lblNombrePlantaDM.setText(bdWMP.obtenerNombrePlanta(loc.getFarm()));
        lblCodigoLineaDM.setText(loc.getCode() + "-" + lineaSeleccionada.getIdLinea());
        lblNombreLineaDM.setText(lineaSeleccionada.getNombreLinea());
        txtFechaDM.setFocusable(false);
        bdWMP.cerrar();

        if(productoMermadoSeleccionado != null){
            txtFechaDM.setText(productoMermadoSeleccionado.getFechaMermado());
            txtLibrasDM.setText(productoMermadoSeleccionado.getLibrasMermado() + "");
            txtComentariosDM.setText(productoMermadoSeleccionado.getvComentarios());
            txtLibrasDM.setEnabled(false);
            btnSendMermaDM.setText("Guardar");
        } else {
            txtFechaDM.setText(config.obtenerFecha());
            txtLibrasDM.setText("");
            txtComentariosDM.setText("");
            txtLibrasDM.setEnabled(true);
        }

        cargarDatosSpinnerDepartamentos(loc.getFarm());
        cargarDatosSpinnerDisposicion();
    }

    public void cargarDatosSpinnerDepartamentos(int idPlanta){
        bdWMP.abrir();
        arregloDepartamentosDM = bdWMP.getDepartamentoPorPlanta(idPlanta, 3);
        bdWMP.cerrar();

        if(arregloDepartamentosDM.length > 0){
            String listaDepartamentosDM[] = new String[arregloDepartamentosDM.length];
            listaDepartamentosSRC = new ArrayList<Integer>();

            for(int i = 0; i < arregloDepartamentosDM.length; i++){
                listaDepartamentosDM[i] = arregloDepartamentosDM[i].getNombreDepartamento();
                listaDepartamentosSRC.add(arregloDepartamentosDM[i].getIdDepartamento());
            }

            adaptadorDepartamentosDM = new AdaptadorSpinner(nContext, listaDepartamentosDM);
            cboxDepartamentoDM.setAdapter(adaptadorDepartamentosDM);

            if(productoMermadoSeleccionado != null)
                cboxDepartamentoDM.setSelection(listaDepartamentosSRC.indexOf(productoMermadoSeleccionado.getIdDepartamento()));
            else
                cboxDepartamentoDM.setSelection(listaDepartamentosSRC.indexOf(sharedPreferences.getInt("idDepartamento", listaDepartamentosSRC.get(0))));
        } else {
            Toast.makeText(nContext, "No hay departamentos en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    public void cargarDatosSpinnerRazones(int idDepartamento){
        bdWMP.abrir();
        arregloRazonesDM = bdWMP.getRazonesPorDepartamento(idDepartamento, 0);
        bdWMP.cerrar();

        if(arregloRazonesDM.length > 0){
            String listaRazonesDM[] = new String[arregloRazonesDM.length];
            listaRazonesSRC = new ArrayList<Integer>();

            for(int i = 0; i < arregloRazonesDM.length; i++){
                listaRazonesDM[i] = arregloRazonesDM[i].getNombreRazon();
                listaRazonesSRC.add(arregloRazonesDM[i].getIdRazon());
            }

            adaptadorRazonesDM = new AdaptadorSpinner(nContext, listaRazonesDM);
            cboxRazonDM.setAdapter(adaptadorRazonesDM);

            if(productoMermadoSeleccionado != null)
                cboxRazonDM.setSelection(listaRazonesSRC.indexOf(productoMermadoSeleccionado.getIdRazon()));
            else
                cboxRazonDM.setSelection(listaRazonesSRC.indexOf(sharedPreferences.getInt("idRazon", listaRazonesSRC.get(0))));
        } else {
            Toast.makeText(nContext, "No hay razones en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    public void cargarDatosSpinnerDisposicion(){
        bdWMP.abrir();
        arregloDisposicionesDM = bdWMP.getDispocisionesMerma();
        bdWMP.cerrar();

        String listaDisposicionDM[] = new String[arregloDisposicionesDM.length];
        ArrayList<String> listaDisposicionSRC = new ArrayList<String>();

        for(int i = 0; i < arregloDisposicionesDM.length; i++){
            listaDisposicionDM[i] = arregloDisposicionesDM[i].getNombreDisposicion();
            listaDisposicionSRC.add(arregloDisposicionesDM[i].getNombreDisposicion());
        }

        adaptadorDisposicionDM = new AdaptadorSpinner(nContext, listaDisposicionDM);
        cboxDisposicionDM.setAdapter(adaptadorDisposicionDM);

        if(productoMermadoSeleccionado != null)
        cboxDisposicionDM.setSelection(listaDisposicionSRC.indexOf(productoMermadoSeleccionado.getnDisposicion()));
    }

    public boolean validarNumero(String numero){
        librasSeleccionadas = 0;

        try{
            librasSeleccionadas = Float.parseFloat(numero);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

    public boolean validarCantidadLibras(){
        if(librasSeleccionadas > 0)
            return true;
        else
            return false;
    }

    public void enviarProductoAMerma(){
        final int idLinea = lineaSeleccionada.getIdLinea();
        final int idDepartment = arregloDepartamentosDM[cboxDepartamentoDM.getSelectedItemPosition()].getIdDepartamento();
        final int idReason = arregloRazonesDM[cboxRazonDM.getSelectedItemPosition()].getIdRazon();
        final String vDisposition = arregloDisposicionesDM[cboxDisposicionDM.getSelectedItemPosition()].getNombreDisposicion();
        final String vComments = txtComentariosDM.getText().toString();
        final int idPlanta = loc.getFarm();
        final String vUser = "DBTAPP";
        final String dFecha = txtFechaDM.getText() + " " + config.obtenerHora();

        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskSentMerma(config.rutaWebServerOmar + "/insDelMermaLineaWithDate", idLinea, idDepartment, idReason, vDisposition, librasSeleccionadas, vComments, vUser, xPosicion, yPosicion, zPosicion, idPlanta, dFecha).execute();
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

    public void mostrarDialogoConfirmacion(String titulo, String mensaje){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(mensaje);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enviarProductoAMerma();
            }
        });

        alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static class AsyncTaskGetProductoMermado extends AsyncTask<String, String, String> {

        public String mURL;
        private ProgressDialog mProgressDialog;
        private String idLinea;

        public AsyncTaskGetProductoMermado(String url, String idLinea) {
            this.mURL = url;
            this.idLinea = idLinea;

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
                Log.d("LINEA", idLinea + "");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLinea", idLinea));

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

                productoMermado = new ProductoMermado[JSONArrayProducto.length()];

                for(int i = 0; i < JSONArrayProducto.length(); i++) {
                    rowJSON = JSONArrayProducto.getJSONObject(i);

                    productoMermado[i] = new ProductoMermado();

                    productoMermado[i].setIdProductoMermado(rowJSON.getInt("idProductoMermado"));
                    productoMermado[i].setIdLinea(rowJSON.getInt("idLinea"));
                    productoMermado[i].setnLinea(rowJSON.getString("nLinea"));
                    productoMermado[i].setIdDepartamento(rowJSON.getInt("idDepartamento"));
                    productoMermado[i].setnDepartamento(rowJSON.getString("nDepartamento"));
                    productoMermado[i].setIdRazon(rowJSON.getInt("idRazon"));
                    productoMermado[i].setnRazon(rowJSON.getString("nRazon"));
                    productoMermado[i].setnDisposicion(rowJSON.getString("nDisposicion"));
                    productoMermado[i].setIdPlanta(rowJSON.getInt("idPlanta"));
                    productoMermado[i].setnPlanta(rowJSON.getString("nPlanta"));
                    productoMermado[i].setLibrasMermado(Float.parseFloat(rowJSON.getString("dLibras")));
                    productoMermado[i].setvComentarios(rowJSON.getString("vComentarios"));
                    productoMermado[i].setFechaMermado(rowJSON.getString("fechaMermado"));
                }

                bdWMP.abrir();
                lineaSeleccionada = bdWMP.getLinea(idLinea);

                lytProductoMermado.setVisibility(View.VISIBLE);
                lblPlantaM.setText(bdWMP.obtenerNombrePlanta(loc.getFarm()));
                lblCodigoLinea.setText(loc.getCode() + "-" + idLinea);
                lblNombreLinea.setText(lineaSeleccionada.getNombreLinea());
                bdWMP.cerrar();

                rvMerma.setLayoutManager(new LinearLayoutManager(nContext));
                rvMerma.setAdapter(new AdaptadorListaMermaRV(productoMermado, nContext));

                if(productoMermado.length == 0){
                    mostrarDialogo("Mensaje", nContext.getString(R.string.no_product_wasted));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarDialogo("Mensaje", nContext.getString(R.string.notConex));
            }

            mProgressDialog.dismiss();
        }
    }

    public class AsyncTaskSentMerma extends AsyncTask<String, String, String> {

        private ProgressDialog mProgressDialog;
        public String mURL;
        private int mIdLinea;
        private int mIdDepartment;
        private int mIdReason;
        private String mDisposition;
        private float mLibras;
        private String mComments;
        private String mUser;
        private int mXPosition;
        private int mYPosition;
        private int mZPosition;
        private int mIdPlanta;
        private String mFecha;

        public AsyncTaskSentMerma(String url, int idLinea, int idDepartment, int idReason, String vDisposition, float dLibras, String vComments, String vUser, int xPosition, int yPosition, int zPosition, int idPlanta, String dFecha) {
            this.mURL = url;
            this.mIdLinea = idLinea;
            this.mIdDepartment = idDepartment;
            this.mIdReason = idReason;
            this.mDisposition = vDisposition;
            this.mLibras = dLibras;
            this.mComments = vComments;
            this.mUser = vUser;

            this.mXPosition = xPosition;
            this.mYPosition = yPosition;
            this.mZPosition = zPosition;
            this.mIdPlanta = idPlanta;
            this.mFecha = dFecha;

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

                if(productoMermadoSeleccionado != null) {
                    params.add(new BasicNameValuePair("accion", "2"));
                    params.add(new BasicNameValuePair("idProductoMermado", productoMermadoSeleccionado.getIdProductoMermado()+""));
                } else {
                    params.add(new BasicNameValuePair("accion", "1"));
                    params.add(new BasicNameValuePair("idProductoMermado", "0"));
                }

                params.add(new BasicNameValuePair("idLinea", mIdLinea+""));
                params.add(new BasicNameValuePair("idDepartamento", mIdDepartment+""));
                params.add(new BasicNameValuePair("idRazon", mIdReason+""));
                params.add(new BasicNameValuePair("vDisposicion", mDisposition+""));
                params.add(new BasicNameValuePair("dLibras", mLibras+""));
                params.add(new BasicNameValuePair("vComentarios", mComments+""));
                params.add(new BasicNameValuePair("vUser", mUser+""));
                params.add(new BasicNameValuePair("xPosicion", mXPosition+""));
                params.add(new BasicNameValuePair("yPosicion", mYPosition+""));
                params.add(new BasicNameValuePair("zPosicion", mZPosition+""));
                params.add(new BasicNameValuePair("idPlanta", mIdPlanta+""));
                params.add(new BasicNameValuePair("dFecha", mFecha+""));

                HttpPost httppostreq = new HttpPost(mURL);
                httppostreq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

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
                        dialogAddMerma.dismiss();
                        productoMermadoSeleccionado = null;
                        mostrarDialogo("Mensaje", "Inserción correcta");
                    }
                    else
                        mostrarDialogo("Mensaje", "Error al registrar merma");
                } else {
                    mostrarDialogo("Mensaje", "Error al registrar merma");
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