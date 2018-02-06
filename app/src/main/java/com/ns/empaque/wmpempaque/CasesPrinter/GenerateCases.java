package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

/**
 * Created by jcalderon on 10/11/2016.
 */

public class GenerateCases {
    private static Activity nContext;
    private static ArrayList<caseIncrement> ciList;
    private static ArrayList<CaseCode> ccList;
    private static CaseCode ccp;
    private static LayoutInflater inflater;
    private static TextView codeCase, lblTotalCasesGenerados, txtStatus;
    private static LinearLayout btnGenerate, btn_printEverthng, btnPrintCalidad;
    private static Button btnGenerateIncrement;
    private static EditText txtTotalIncrement;
    private static ArrayList<caseIncrement> caseIncrementList;
    private static caseIncremetnAdapter adapterIncrement;
    private static ListView lv_consecutivoCases;
    private static int iStatus;
    private static int idCaseHeader;
    private static int totalCasesAGenerar;
    private static BoxesFolioInLine folioInLine;
    private static double totalLbs;
    private static ProgressDialog pDialog;
    private static SharedPreferences sharedpreferences;

    public GenerateCases(Activity nContext, CaseCode ccp, ArrayList<CaseCode> ccList, ArrayList<caseIncrement> ciList, int iStatus, int idCaseHeader, BoxesFolioInLine folioInLine){
        this.nContext = nContext;
        this.ccList = ccList;
        this.ciList = ciList;
        this.iStatus = iStatus;
        this.ccp = ccp;
        this.idCaseHeader = idCaseHeader;
        this.folioInLine = folioInLine;

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
    }

    public static View getPopUpView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.w_popup_printcases, null, true);

        if(iStatus == 0 || !(ccList.contains(ccp)) ) {
            BaseDatos db = new BaseDatos(nContext);
            db.abrir();
            db.cidb.inserCaseIncrementHeader(ccp);
            db.cerrar();
        }

        /*BaseDatos db = new BaseDatos(nContext);
        db.abrir();
        double lbsCaja = db.obtenerLibrasPorCajaSKU(ccp.getSKU());
        db.cerrar();*/

        totalLbs = (folioInLine.getLbsXBox() * folioInLine.getBoxesAvailable());
        totalCasesAGenerar = Math.round((float)(totalLbs / folioInLine.getLbsPorSKU()));

        lblTotalCasesGenerados = (TextView) v.findViewById(R.id.lblContadorEtiquetas);
        codeCase = (TextView) v.findViewById(R.id.codeCase);
        btnGenerate = (LinearLayout) v.findViewById(R.id.btnGenerate);
        btnGenerateIncrement = (Button) v.findViewById(R.id.btnGenerateIncrement);
        txtTotalIncrement = (EditText) v.findViewById(R.id.txtTotalIncrement);
        btnPrintCalidad = (LinearLayout) v.findViewById(R.id.btnPrintCalidad);
        btn_printEverthng = (LinearLayout) v.findViewById(R.id.btn_printEverthng);
        lv_consecutivoCases = (ListView) v.findViewById(R.id.lv_consecutivoCases);
        txtStatus = (TextView) v.findViewById(R.id.txtStatus);

        if(ciList.isEmpty())
            caseIncrementList = new ArrayList<>();
        else
            caseIncrementList = ciList;

        adapterIncrement = new caseIncremetnAdapter(nContext, caseIncrementList);
        lv_consecutivoCases.setAdapter(adapterIncrement);

        //Escribimos el caseCode en la parte superior.
        lblTotalCasesGenerados.setText(caseIncrementList.size()+"");
        codeCase.setText(ccp.getCode() + " - " + ccp.getSKU());

        int totalCases = totalCasesAGenerar - folioInLine.getCasesGenerados();

        if(totalCases > 0)
            txtTotalIncrement.setText(totalCases + "");

        ButtonsEvents(ccp.getUUIDHeader());

        return v;
    }

    private static void ButtonsEvents(final String UUIDHeader) {
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarIncrementable(UUIDHeader, 1);
            }
        });

        btnGenerateIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                int total = 0;

                try{
                    total = Integer.parseInt(txtTotalIncrement.getText().toString());
                } catch(Exception ex){
                    error = true;
                }

                if(txtTotalIncrement.getText().toString().isEmpty() || error){
                    new PopUp(nContext, "Digite el número de cases incrementables que desea generar", "Digite un número", PopUp.POPUP_INCORRECT).showPopUp();
                } else {
                    generarIncrementable(UUIDHeader, total);
                }
            }
        });

        btnPrintCalidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(caseIncrementList.size() > 0) {
                    if(WMPEmpaque.impresoraConfigurada()){
                        mostrarDialogoImpresion();
                    } else{
                        WMPEmpaque.mostrarDialogo("Configure impresora e intentelo nuevamente");
                    }
                } else {
                    new PopUp(nContext, "No tienes Cases para imprimir, primero genera la lista por favor.", PopUp.POPUP_INFORMATION).showPopUp();
                }
            }
        });

        btn_printEverthng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(caseIncrementList.size() > 0) {
                    if(WMPEmpaque.impresoraConfigurada()){
                        pConnect printEverything = new pConnect(nContext, txtStatus);
                        printEverything.print(caseIncrementList, false);
                    } else{
                        WMPEmpaque.mostrarDialogo("Configure impresora e intentelo nuevamente");
                    }
                }else{
                    new PopUp(nContext, "No tienes Cases para imprimir, primero genera la lista por favor.", PopUp.POPUP_INFORMATION).showPopUp();
                }
            }
        });
    }

    public static void casesAGenerar(){
        int totalCases = totalCasesAGenerar - folioInLine.getCasesGenerados();

        if(totalCases > 0)
            txtTotalIncrement.setText(totalCases + "");
        else
            mostrarDialogo("Mensaje", "Ya han sido generados todos los cases posibles para este folio");
    }

    private static void mostrarDialogo(String titulo, String msj){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(msj);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static void cambiarContadorCases(){
        lblTotalCasesGenerados.setText(caseIncrementList.size()+"");
    }

    private static void mostrarDialogoImpresion(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.view_print_prepallet, null);

        TextView lblTituloPalletID = (TextView) dialoglayout.findViewById(R.id.lblTituloPalletIDPPP);
        TextView lblPalletID = (TextView) dialoglayout.findViewById(R.id.lblPalletIDPPP);
        TextView lblTituloSKU = (TextView) dialoglayout.findViewById(R.id.lblTituloSKUPPP);
        TextView lblSKU = (TextView) dialoglayout.findViewById(R.id.lblSKUPPP);
        final EditText txtTotal = (EditText) dialoglayout.findViewById(R.id.txtTotalPPP);

        lblTituloPalletID.setText("Case: ");
        lblPalletID.setText(caseIncrementList.get(0).getCaseCodeHeader());
        lblTituloSKU.setText("SKU: ");
        lblSKU.setText(caseIncrementList.get(0).getSKU());

        txtTotal.setText(sharedpreferences.getInt("totalPrintCalidad", 0) + "");

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        alertDialog.setView(dialoglayout);
        alertDialog.setIcon(R.drawable.iprint);
        alertDialog.setTitle("Impresión de etiquetas");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean isNumber = true;
                int nPrint = 0;

                try{
                    nPrint = Integer.parseInt(txtTotal.getText().toString());
                } catch(NumberFormatException ex){
                    isNumber = false;
                    nPrint = 0;
                }

                if(isNumber){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("totalPrintCalidad", nPrint);
                    editor.commit();

                    ArrayList<caseIncrement> caseCalidad = new ArrayList<caseIncrement>();

                    for(int i = 0; i < nPrint; i++)
                        caseCalidad.add(caseIncrementList.get(0));

                    pConnect printEverything = new pConnect(nContext, txtStatus);
                    printEverything.print(caseCalidad, true);
                } else {
                    Toast.makeText(nContext, "Número de impresiones incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog ad = alertDialog.create();
        ad.show();
    }

    public static caseIncrement generarCaseIncrement(String UUIDHeader){
        caseIncrement ci = new caseIncrement();

        ci.setCaseCode("");
        ci.setFolio(ccp.getFolio());
        ci.setSKU(ccp.getSKU());
        ci.setUUID(UUID.randomUUID().toString());
        ci.setCaseCodeHeader(ccp.getCode());
        ci.setUUIDHeader(UUIDHeader);
        ci.setActive(1);
        ci.setSync(1);

        return ci;
    }

    public static void generarIncrementable(final String UUIDHeader, final int casesAGenerar){
        if((caseIncrementList.size() + casesAGenerar) > totalCasesAGenerar){
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

            alertDialog2.setTitle("Mensaje");
            alertDialog2.setIcon(R.drawable.naturesweet);
            alertDialog2.setMessage("Se generarán más cases de los que el folio permite\n\n¿Desea continuar?");
            alertDialog2.setCancelable(false);

            alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    pDialog = new ProgressDialog(nContext);
                    pDialog.setIndeterminate(true);
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.setMessage("Por favor espere...");
                    pDialog.show();

                    for (int i = 0; i < casesAGenerar; i++) {
                        final int index = (i + 1);
                        nContext.runOnUiThread(new Runnable() {
                            public void run() {
                                new AysnTaskGenerarCaseDetails(config.rutaWebServerOmar + "/getLastCase", UUIDHeader, index, casesAGenerar).execute();
                            }
                        });
                    }
                }
            }).show();
        } else {
            pDialog = new ProgressDialog(nContext);
            pDialog.setIndeterminate(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setMessage("Por favor espere...");
            pDialog.show();

            for (int i = 0; i < casesAGenerar; i++) {
                final int index = (i + 1);
                nContext.runOnUiThread(new Runnable() {
                    public void run() {
                        new AysnTaskGenerarCaseDetails(config.rutaWebServerOmar + "/getLastCase", UUIDHeader, index, casesAGenerar).execute();
                    }
                });
            }
        }
    }

    private static class AysnTaskGenerarCaseDetails extends AsyncTask<String, String, String> {

        public caseIncrement incrementCase;
        private String caseDetailsJSON;
        public String URL;
        public int index;
        public int totalCases;

        public AysnTaskGenerarCaseDetails(String url, String UUIDHeader, int index, int total) {
            this.URL = url;
            this.index = index;
            this.totalCases = total;

            incrementCase = generarCaseIncrement(UUIDHeader);

            caseDetailsJSON = "[{\"vCodeCase\":\""          + incrementCase.getCaseCode() + "\"," +
                                "\"vFolio\":\""             + incrementCase.getFolio() + "\"," +
                                "\"vSKU\":\""               + incrementCase.getSKU() + "\"," +
                                "\"vUUID\":\""              + incrementCase.getUUID() + "\"," +
                                "\"vCodeCaseHeader\":\""    + incrementCase.getCaseCodeHeader() + "\"," +
                                "\"vUUIDHEADER\":\""        + incrementCase.getUUIDHeader() + "\"," +
                                "\"bActive\":\""            + incrementCase.getActive() + "\"," +
                                "\"dCreatedDate\":\""       + config.obtenerFechaHora() + "\"," +
                                "\"vUserCreated\":\""       + "" + "\"," +
                                "\"dUpdatedDate\":\""       + config.obtenerFechaHora() + "\"," +
                                "\"vUserUpdate\":\""        + "" + "\"}]";

            Log.d("caseDetailsJSON", caseDetailsJSON);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";

            Log.d("idCaseHeader", idCaseHeader+"");

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("caseDetailsJSON", caseDetailsJSON));
                params.add(new BasicNameValuePair("idCaseHeader", idCaseHeader+""));
                step = "2";
                HttpPost httppostreq = new HttpPost(URL);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            JSONObject json;
            Log.d("JSON", res);

            try {
                json = new JSONObject(res);
                JSONArray resultadoCasesJSON;
                JSONObject row;

                resultadoCasesJSON = json.optJSONArray("table1");

                for (int i = 0; i < resultadoCasesJSON.length(); i++) {
                    row = resultadoCasesJSON.getJSONObject(i);

                    incrementCase.setCaseCodeHeader(row.getString("vCodeCaseHeader"));
                    incrementCase.setCaseCode(row.getString("vCodeCase"));
                    incrementCase.setFolio(row.getString("vFolio"));
                    incrementCase.setSKU(row.getString("vSKU"));
                    incrementCase.setUUID(row.getString("vUUID"));
                    incrementCase.setUUIDHeader(row.getString("vUUIDHEADER"));
                    incrementCase.setCreatedDate(row.getString("dCreatedDate"));
                    incrementCase.setCreatedUser(row.getString("vUserCreated"));
                    incrementCase.setUpdateDate(row.getString("dUpdatedDate"));
                    incrementCase.setUpdateUser(row.getString("vUserUpdate"));
                    incrementCase.setActive(row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                    incrementCase.setSKU(ccp.getSKU());

                    caseIncrementList.add(0, incrementCase);
                    adapterIncrement.notifyDataSetChanged();

                    lblTotalCasesGenerados.setText(caseIncrementList.size()+"");

                    BaseDatos db = new BaseDatos(nContext);
                    db.abrir();
                    db.ciddb.inserCaseIncrementDetails(incrementCase);
                    db.cerrar();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(nContext, "No hay conexión con el web service. Revise su conexión a Internet", Toast.LENGTH_LONG).show();
            }

            if(index == totalCases) {
                nContext.runOnUiThread(new Runnable() {
                    public void run() {
                        pDialog.dismiss();
                    }
                });
            }
        }
    }
}
