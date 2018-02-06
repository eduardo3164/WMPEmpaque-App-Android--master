package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PrinterConection.pConnect;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcalderon on 29/06/2016.
 */

public class InsupPrePallet {

    public static PrePallet pp;
    private static Activity nContext;
    private static Context contexto;
    private static LayoutInflater inflater;

    private static casesAdapter caseAdap;
    private static foliosAdapter folioAdap;

    private static EditText et_Cases;
    private static TextView titleCaajasList;
    private static ImageView btnSendToGP;
    public static ArrayList<cases> NewCasesList;
    public static ArrayList<Folio> NewFoliosList;

    private static TextView lblTituloIdPallet, lblIdPallet, lblPrintPallet, lblSendGPP, lblInvoice, lblTituloInvoice;
    private static LinearLayout btnPrintPallet, btnConfigScan;

    private static Button btnGo, btnSaveCajas;
    private static LinearLayout btnScanCase;
    private static BaseDatos bdPrePallet;
    private static double lbsPorCase;
    private static boolean errorSave = false;
    private static SharedPreferences sharedpreferences;

    public InsupPrePallet(PrePallet pp, Activity nContext, RelativeLayout content) {
        this.pp = pp;
        this.nContext = nContext;
        this.contexto = nContext;

        bdPrePallet = new BaseDatos(nContext);
        bdPrePallet.abrir();

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);

        boolean existeTablaCasesPrePallet = bdPrePallet.existeTablaCasesPrePallet();
        boolean existeTablaAsigPallet = bdPrePallet.existeTablaAsigPallet();
        boolean existeTablaPalletsInGP = bdPrePallet.existeTablaPalletsInGP();

        if (!existeTablaCasesPrePallet)
            bdPrePallet.crearTablaCasesPrePallet();

        if (!existeTablaAsigPallet)
            bdPrePallet.crearTablaAsigPallet();

        if (!existeTablaPalletsInGP)
            bdPrePallet.crearTablaPalletsInGP();

        lbsPorCase = bdPrePallet.obtenerLibrasPorCajaSKU(pp.getvSKU());

        bdPrePallet.cerrar();
    }

    public static View setView() {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.view_insup_prepallet, null, true);

        TextView sync = (TextView) v.findViewById(R.id.sync);

        if (pp.getSync() == 0)
            sync.setVisibility(sync.VISIBLE);
        else
            sync.setVisibility(sync.INVISIBLE);

        TextView idPP = (TextView) v.findViewById(R.id.idPP);
        TextView planta = (TextView) v.findViewById(R.id.planta);
        TextView linea = (TextView) v.findViewById(R.id.linea);
        TextView promotion = (TextView) v.findViewById(R.id.promotion);
        TextView size = (TextView) v.findViewById(R.id.size);
        TextView sku = (TextView) v.findViewById(R.id.sku);
        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        TextView cajas = (TextView) v.findViewById(R.id.cajas);
        TextView promoDesc = (TextView) v.findViewById(R.id.promotionDesc);
        TextView casesPerPallet = (TextView) v.findViewById(R.id.casesPerPallet);
        ListView lv_Cases = (ListView) v.findViewById(R.id.casesList);
        ListView foliosListView = (ListView) v.findViewById(R.id.foliosList);
        titleCaajasList = (TextView) v.findViewById(R.id.titleBoxesList);
        et_Cases = (EditText) v.findViewById(R.id.et_Cases);
        btnSaveCajas = (Button) v.findViewById(R.id.btnSaveCajas);
        btnScanCase = (LinearLayout) v.findViewById(R.id.btnScanCase);

        lblTituloIdPallet = (TextView) v.findViewById(R.id.lblTituloIdPallet);
        lblIdPallet = (TextView) v.findViewById(R.id.lblIdPallet);
        lblInvoice = (TextView) v.findViewById(R.id.lblInvoicePP);
        lblTituloInvoice = (TextView) v.findViewById(R.id.lblTituloInvoicePP);
        lblPrintPallet = (TextView) v.findViewById(R.id.lblPrintPallet);
        btnPrintPallet = (LinearLayout) v.findViewById(R.id.btnPrintPallet);
        btnConfigScan = (LinearLayout) v.findViewById(R.id.btnConfigScan);
        btnSendToGP = (ImageView) v.findViewById(R.id.btnSendGP);
        lblSendGPP = (TextView) v.findViewById(R.id.lblSendGPP);
        btnGo = (Button) v.findViewById(R.id.btnGo);

        btnPrintPallet.setVisibility(View.INVISIBLE);
        btnSendToGP.setVisibility(View.INVISIBLE);
        lblSendGPP.setVisibility(View.INVISIBLE);

        if(pp.getCajasPrePallet().size() > 0){
            btnSendToGP.setVisibility(View.VISIBLE);
            lblSendGPP.setVisibility(View.VISIBLE);
        }

        validacionGP();

        NewCasesList = new ArrayList<>();
        NewFoliosList = new ArrayList<>();

        Log.d("SEPARATOR", "====================================");
        Log.d("SIZE CASES 1", NewCasesList.size() + "");
        Log.d("SIZE FOLIO 1", NewFoliosList.size() + "");
        Log.d("SEPARATOR", "====================================");
        Log.d("SEPARATOR", "====================================");
        Log.d("SIZE CASES 2", pp.getCajasPrePallet().size() + "");
        Log.d("SIZE FOLIO 2", pp.getFolioPerPallet().size() + "");
        Log.d("SEPARATOR", "====================================");

        nContext.registerForContextMenu(foliosListView);

        titleCaajasList.setText("Lista de cajas (" + (pp.getCajasPrePallet() == null ? "0" : pp.getCajasPrePallet().size()) + ")");
        idPP.setText(pp.getIdPrePallet() + "");
        planta.setText(pp.getPlantaName());
        linea.setText(pp.getNameLine());

        promotion.setText(pp.getvPromotion());
        size.setText(pp.getvSize());
        sku.setText(pp.getvSKU());
        fecha.setText(pp.getFullDateCreated());
        cajas.setText(pp.getCajas() + "");
        promoDesc.setText(pp.getPromoDesc());
        casesPerPallet.setText(pp.getCasesPerPallet() + "");

        if (pp.getCajasPrePallet() != null) {
            caseAdap = new casesAdapter(nContext, pp.getCajasPrePallet(), titleCaajasList, pp.getvPalletID());
            lv_Cases.setAdapter(caseAdap);
        }

        if (pp.getFolioPerPallet() != null) {
            folioAdap = new foliosAdapter(nContext, pp.getFolioPerPallet(), pp.getvPalletID());
            foliosListView.setAdapter(folioAdap);
        }

        eventos();

        et_Cases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().contains("\n")) {
                    final String strCase = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)", "");

                    et_Cases.setText("");

                    nContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new searchCaseInServer(config.rutaWebServerOmar + "/searchCaseInServer", strCase, pp.getIdPrePallet()).execute();
                        }
                    });
                }
            }
        });

        btnScanCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WMPEmpaque.caseAsignCasePrepallet = 1;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });


        btnSendToGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToGP();
            }
        });

        btnPrintPallet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(WMPEmpaque.impresoraConfigurada()){
                    mostrarDialogoImpresion();
                } else {
                    WMPEmpaque.mostrarDialogo("Configure impresora e intentelo nuevamente");
                }
            }
        });

        btnConfigScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configurarScanner();
            }
        });

        btnSaveCajas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pp.getCajasPrePallet().size() > pp.getCasesPerPallet()){
                            int cajasEccedentes = pp.getCajasPrePallet().size() - pp.getCasesPerPallet();
                            mostrarDialogoGuardar(cajasEccedentes);
                        } else{
                            mostrarDialogoGuardar(0);
                        }
                    }
                });
            }
        });

        return v;
    }

    private static void eventos() {
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strCase = et_Cases.getText().toString();

                et_Cases.setText("");

                if (!strCase.isEmpty()) {
                    nContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new searchCaseInServer(config.rutaWebServerOmar + "/searchCaseInServer", strCase, pp.getIdPrePallet()).execute();
                        }
                    });
                }
            }
        });
    }

    public static void validacionGP() {
        //Log.d("IDGP", "AA"+pp.getIdGP()+"AA");
        Log.d("PAID", pp.getvPalletID());

        if(!pp.getvPalletID().equalsIgnoreCase("null") && pp.getvPalletID().length() > 0) {
            lblIdPallet.setText(pp.getvPalletID());
            lblInvoice.setText(pp.getIdGP().trim());
            lblTituloIdPallet.setVisibility(View.VISIBLE);
            btnPrintPallet.setVisibility(View.VISIBLE);
            lblTituloInvoice.setVisibility(View.VISIBLE);
            lblInvoice.setVisibility(View.VISIBLE);
            btnConfigScan.setVisibility(View.INVISIBLE);
            btnSendToGP.setVisibility(View.INVISIBLE);
            lblSendGPP.setVisibility(View.INVISIBLE);
            btnScanCase.setVisibility(View.INVISIBLE);
            btnSaveCajas.setVisibility(View.INVISIBLE);
            et_Cases.setVisibility(View.INVISIBLE);
            btnGo.setVisibility(View.INVISIBLE);
        }
    }

    public static void searCasesInServer(final String strCase){
        if (!strCase.isEmpty()) {
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new searchCaseInServer(config.rutaWebServerOmar + "/searchCaseInServer", strCase, pp.getIdPrePallet()).execute();
                }
            });
        }
    }

    public static void guardarDatos(){
        Log.d("SEPARATOR", "====================================");
        Log.d("SIZE CASES 2", pp.getCajasPrePallet().size()+"");
        Log.d("SIZE FOLIO 2", pp.getFolioPerPallet().size()+"");
        Log.d("SEPARATOR", "====================================");

        bdPrePallet.abrir();

        bdPrePallet.borrarCasesPrePallet(pp.getIdPrePallet());
        bdPrePallet.borrarAsignacionPrePallet(pp.getIdPrePallet());

        for(int i = 0; i < pp.getCajasPrePallet().size(); i++){
            cases c = pp.getCajasPrePallet().get(i);
            bdPrePallet.insertarCasesPrePallet(pp.getIdPrePallet(), c.getIdCasesDetails(), c.getCodigoCase(), config.obtenerMACAddress(nContext), pp.getvUnicSessionKey(), c.getUuidCasesDetails());
        }

        bdPrePallet.selectCasesPrePallet();

        for(int i = 0; i < pp.getFolioPerPallet().size(); i++){
            Folio f = pp.getFolioPerPallet().get(i);
            bdPrePallet.insertarAsignacionPrePallet(pp.getIdPrePallet(), pp.getvPalletCodeEX(), f.getFolioCode(), f.getCajasSeleccionadas(), config.obtenerMACAddress(nContext), "0", 0);
        }

        bdPrePallet.selectAsignacionPrePallet();
        bdPrePallet.cerrar();
    }

    public static String generarJSONCases(){
        bdPrePallet.abrir();
        String [][] datosCasesCasesPrePallet = bdPrePallet.selectCasesPrePalletToSync();
        bdPrePallet.cerrar();

        String CasesPrePalletJSON = null;

        if(datosCasesCasesPrePallet.length > 0) {
            CasesPrePalletJSON = "[";

            for (int i = 0; i < datosCasesCasesPrePallet.length; i++) {
                CasesPrePalletJSON    += "{\"idPrePallet\":"        + datosCasesCasesPrePallet[i][1] + ","
                                        + "\"idCaseDetails\":"      + datosCasesCasesPrePallet[i][2] + ","
                                        + "\"vCodeCase\":\""        + datosCasesCasesPrePallet[i][3] + "\","
                                        + "\"bActive\":"            + datosCasesCasesPrePallet[i][4] + ","
                                        + "\"dDateCreate\":\""      + datosCasesCasesPrePallet[i][5] + "\","
                                        + "\"vUserCreate\":\""      + datosCasesCasesPrePallet[i][6] + "\","
                                        + "\"dDateUpdate\":\""      + datosCasesCasesPrePallet[i][7] + "\","
                                        + "\"vUserUpdate\":\""      + datosCasesCasesPrePallet[i][8] + "\","
                                        + "\"vIdTabletMac\":\""     + datosCasesCasesPrePallet[i][9] + "\","
                                        + "\"vUUIDPP\":\""          + datosCasesCasesPrePallet[i][10] + "\","
                                        + "\"vUUIDCD\":\""          + datosCasesCasesPrePallet[i][11] + "\","
                                        + "\"vUUID\":\""            + datosCasesCasesPrePallet[i][12] + "\"},";
            }

            CasesPrePalletJSON = CasesPrePalletJSON.substring(0, CasesPrePalletJSON.length() - 1);
            CasesPrePalletJSON += "]";
        }

        if(CasesPrePalletJSON != null)
            Log.d("PrePallet -- >", CasesPrePalletJSON);

        return CasesPrePalletJSON;
    }

    public static String generarJSONFolios(){
        bdPrePallet.abrir();
        String [][] datosAsingFoliosPrePallet = bdPrePallet.selectAsignacionPrePalletToSync();
        bdPrePallet.cerrar();

        String FoliosPrePalletJSON = null;

        if(datosAsingFoliosPrePallet.length > 0) {
            FoliosPrePalletJSON = "[";

            for (int i = 0; i < datosAsingFoliosPrePallet.length; i++) {
                FoliosPrePalletJSON     += "{\"idPrePallet\":"          + datosAsingFoliosPrePallet[i][1] + ","
                                        + "\"vFolio\":\""               + datosAsingFoliosPrePallet[i][2] + "\","
                                        + "\"iCases\":"                 + datosAsingFoliosPrePallet[i][3] + ","
                                        + "\"idLine\":"                 + datosAsingFoliosPrePallet[i][4] + ","
                                        + "\"vIdTabletMac\":\""         + datosAsingFoliosPrePallet[i][5] + "\","
                                        + "\"vUnicSesionKey\":\""       + datosAsingFoliosPrePallet[i][6] + "\"},";
            }

            FoliosPrePalletJSON = FoliosPrePalletJSON.substring(0, FoliosPrePalletJSON.length() - 1);
            FoliosPrePalletJSON += "]";
        }

        if(FoliosPrePalletJSON != null)
            Log.d("PrePallet -- >", FoliosPrePalletJSON);

        return FoliosPrePalletJSON;
    }

    private static void sendToGP(){
        if(pp.getFolioPerPallet().size() > 0) {
            mostrarDialogoEnviarGP();
        } else {
            Toast.makeText(nContext, "Primero inserte folios al PrePallet.", Toast.LENGTH_LONG).show();
        }

        /*String idGP[] = {"null", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String Pall[] = {"DS493161E112006", "DS493161E112005", "DS493161E112004", "DS493161E112003", "DS493161E112002", "DS493161E112001", "DM497121F022001"};

        int iGP = (int) (Math.random() * 10);
        int iPa = (int) (Math.random() * 6);

        Log.d("iGP", iGP+"");
        Log.d("iPa", iPa+"");

        String res = "{ 'table1':[ { 'idGP':'" + idGP[iGP] + "', 'Pallet':'" + Pall[iPa] + "' } ] }";
        String msj = "";

        Log.d("JSON", res);

        if(pp.getFolioPerPallet().size() > 0){
            try{

                JSONObject json = new JSONObject(res);
                JSONArray pallet = json.getJSONArray("table1");

                if(pallet.length() > 0) {
                    JSONObject row = pallet.getJSONObject(0);

                    if(row.getInt("idGP") > 0){
                        pp.setIdGP(row.getString("idGP").equalsIgnoreCase("null") ? "" : row.getString("idGP"));
                        pp.setvPalletID(row.getString("Pallet"));

                        validacionGP();

                        bdPrePallet.abrir();
                        bdPrePallet.insertarPalletsInGP(pp.getIdPrePallet(), pp.getvPalletID(), pp.getIdGP());
                        bdPrePallet.cerrar();

                        msj = "Inserción correcta en GP";
                    } else {
                        msj = "Error al insertar en GP";
                    }

                    mostrarDialogo(msj);
                }
            } catch (JSONException ex){
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(nContext, "Primero inserte folios al PrePallet.",Toast.LENGTH_LONG).show();
        }*/
    }

    private static void mostrarDialogoGuardar(int num){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle("Confirmación");
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setCancelable(false);

        if(num != 0)
            alertDialog2.setMessage("Se han agregado " + num + " cases más de los que acepta el pallet.\n\n¿Desea continuar?");
        else
            alertDialog2.setMessage("¿Está seguro que desea guardar los datos?");

        alertDialog2.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new AysnTaskSentToServer(config.rutaWebServerOmar + "/insup_CasesFaPrePallet").execute();
                dialog.dismiss();
            }
        });

        alertDialog2.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private static void mostrarDialogoEnviarGP(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle("Confirmación");
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage("¿Está seguro que desea enviar los datos a GP?");
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new InsertToGP("http://192.168.167.191/Empaque/WebServices/Pallets/WSPallet.asmx/InsertPrePallet").execute();
                //new InsertToGP("http://192.168.167.179:7894/WMPEmpaqueWS/Pallets/WSPallet.asmx/InsertPrePallet").execute();
                dialog.dismiss();
            }
        });

        alertDialog2.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private static void mostrarDialogo(String titulo, String msj){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);

        alertDialog2.setTitle(titulo);
        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setMessage(msj);
        alertDialog2.setCancelable(false);

        alertDialog2.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    public static void reproducirSonido(){
//        MediaPlayer mp;
//
//        if(sharedpreferences.getInt("totalPrint", 0) == 0)
//            mp = MediaPlayer.create(nContext, R.raw.caseerror);
//        else
//            mp = MediaPlayer.create(nContext, R.raw.error);
//
//        mp.start();
    }

    private static void mostrarDialogoImpresion(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.view_print_prepallet, null);

        TextView lblPalletID = (TextView) dialoglayout.findViewById(R.id.lblPalletIDPPP);
        TextView lblSKU = (TextView) dialoglayout.findViewById(R.id.lblSKUPPP);
        final EditText txtTotal = (EditText) dialoglayout.findViewById(R.id.txtTotalPPP);

        lblPalletID.setText(pp.getvPalletID());
        lblSKU.setText(pp.getvSKU());
        txtTotal.setText(sharedpreferences.getInt("totalPrint", 0) + "");

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
                    editor.putInt("totalPrint", nPrint);
                    editor.commit();

                    pConnect print = new pConnect(nContext, lblPrintPallet);
                    print.printPP(nPrint, pp.getvPalletID(), pp.getvSKU());
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

    public static void configurarScanner(){
        View dialoglayout = inflater.inflate(R.layout.dialogo_impresoras, null, true);

        final TextView lblScanMode = (TextView) dialoglayout.findViewById(R.id.lblImpresoraActual);
        TextView lblScanActual = (TextView) dialoglayout.findViewById(R.id.lblTituloImpresoraActual);
        TextView lblScanLista = (TextView) dialoglayout.findViewById(R.id.lblTitulolistaImpresoras);
        ListView listaModos = (ListView) dialoglayout.findViewById(R.id.listaImpresoras);

        lblScanActual.setText("Modo de escaneo actual:");
        lblScanLista.setText("Seleccione modo de escaneo");

        final String modos[] = {"1.- Uno por uno.",
                                "2.- Case leído hacia atrás.",
                                "3.- Mismo encabezado del case."};

        ArrayAdapter<String> adaptadorLista = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_multiple_choice, modos);
        listaModos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaModos.setAdapter(adaptadorLista);

        listaModos.setItemChecked((sharedpreferences.getInt("scanMode", 1) - 1), true);
        lblScanMode.setText(modos[(sharedpreferences.getInt("scanMode", 1) - 1)]);

        listaModos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lblScanMode.setText(modos[i]);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("scanMode", (i + 1));
                editor.commit();
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        alertDialog.setView(dialoglayout);
        alertDialog.setIcon(R.drawable.qr_scan_icon);
        alertDialog.setTitle("Configuración de scanner");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog ad = alertDialog.create();
        ad.show();
    }

    public static boolean insertCase(String strCase, int idCaseDetails, String uuidCD, String folio){
        boolean insercionCorrecta = true;

        if (config.validaString(strCase, nContext) == 3) {
            if (pp.getCajasPrePallet() != null) {
                cases Case = new cases();
                Case.setCodigoCase(strCase);
                Case.setIdPrePallet(pp.getIdPrePallet());
                Case.setActive(true);
                Case.setIdCasesDetails(idCaseDetails);
                Case.setUuidCasesDetails(uuidCD);
                Case.setFolio(folio);

                if (!pp.getCajasPrePallet().contains(Case)) {
                    pp.getCajasPrePallet().add(0, Case);
                    caseAdap.notifyDataSetChanged();
                    titleCaajasList.setText("Lista de cajas (" + (pp.getCajasPrePallet().size()) + ")");

                    NewCasesList.add(0, Case);
                } else {
                    insercionCorrecta = false;
                    //Toast.makeText(nContext, "Ese case ya ha sido agregado a la lista", Toast.LENGTH_LONG).show();
                }

            } else {
                cases Case = new cases();
                Case.setCodigoCase(strCase);
                Case.setIdPrePallet(pp.getIdPrePallet());
                Case.setActive(true);

                Case.setIdCasesDetails(idCaseDetails);
                Case.setUuidCasesDetails(uuidCD);
                Case.setFolio(folio);

                pp.getCajasPrePallet().add(0, Case);
                caseAdap.notifyDataSetChanged();
                titleCaajasList.setText("Lista de cajas (" + (pp.getCajasPrePallet().size()) + ")");

                NewCasesList.add(0, Case);
            }
        } else {
            insercionCorrecta = false;
            Toast.makeText(nContext, "No es un case: " + strCase, Toast.LENGTH_LONG).show();
        }

        return insercionCorrecta;
    }

    public static void insertFolio(String folioCode, int cajas, double lbsPorCaja, String greenHouse, String caseHeader){
        int totalCasesAgregados = 0;

        for(int i = 0; i < pp.getCajasPrePallet().size(); i++){
            if(pp.getCajasPrePallet().get(i).getFolio().equalsIgnoreCase(folioCode))
                totalCasesAgregados++;
        }

        int cajasUtilizadas = Math.round((float)((lbsPorCase * totalCasesAgregados) / lbsPorCaja));

        Log.d("LBS POR CASE", lbsPorCase+"");
        Log.d("CAJAS UTILIZADAS", ((lbsPorCase * totalCasesAgregados) / lbsPorCaja)+"");
        Log.d("CAJAS UTILIZADAS RND", cajasUtilizadas+"");

        if(pp.getFolioPerPallet() != null) {
            Folio folio = new Folio();

            folio.setFolioCode(folioCode);
            folio.setCajas(cajas);
            folio.setLbsPorCaja(lbsPorCaja);
            folio.setLbsDisponibles((lbsPorCaja * cajas));
            folio.setGreenHouse(greenHouse);
            folio.setCaseCodeHeader(caseHeader);
            folio.setCajasSeleccionadas(cajasUtilizadas);

            if (!pp.getFolioPerPallet().contains(folio)) {
                pp.getFolioPerPallet().add(0, folio);
                folioAdap.notifyDataSetChanged();

                //NewFoliosList.add(0, folio);
            } else {
                pp.getFolioPerPallet().get(pp.getFolioPerPallet().indexOf(folio)).setCajasSeleccionadas(cajasUtilizadas);
                folioAdap.notifyDataSetChanged();

                //NewFoliosList.get(NewFoliosList.indexOf(folio)).setCajasSeleccionadas(cajasUtilizadas);
            }
        } else {
            Folio folio = new Folio();

            folio.setFolioCode(folioCode);
            folio.setCajas(cajas);
            folio.setLbsPorCaja(lbsPorCaja);
            folio.setLbsDisponibles((lbsPorCaja * cajas));
            folio.setGreenHouse(greenHouse);
            folio.setCaseCodeHeader(caseHeader);
            folio.setCajasSeleccionadas(cajasUtilizadas);

            pp.getFolioPerPallet().add(0, folio);
            folioAdap.notifyDataSetChanged();

            NewFoliosList.add(0, folio);
        }
    }

    public static class InsertToGP extends AsyncTask<String, String, String> {

        private String url;
        private ProgressDialog pd;
        private int connect;

        public InsertToGP(String url){
            this.url = url;
            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setIndeterminate(true);
            pd.setMessage("Cargando... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "";
            connect = 0;

            Log.d("idPrePallet", pp.getIdPrePallet() + "");

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idPrepallet", pp.getIdPrePallet()+""));
                connect = 2;
                HttpPost httppostreq = new HttpPost(url);
                connect = 3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                connect = 4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                connect = 5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                connect = 6;
            } catch (Exception ex) {
                ex.printStackTrace();
                //jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            String msj = "";

            if(connect == 6){
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray pallet = json.getJSONArray("Result");

                    if(pallet.length() > 0) {
                        JSONObject row = pallet.getJSONObject(0);

                        if(!res.contains("Error")){
                            try{
                                if(row.getString("IdPalletGP").length() > 0){
                                    //pp.setIdGP(row.getString("idGP").equalsIgnoreCase("null") ? 0 : Integer.parseInt(row.getString("idGP")));
                                    pp.setIdGP(row.getString("IdPalletGP"));
                                    pp.setvPalletID(row.getString("PalletID"));

                                    validacionGP();

                                    bdPrePallet.abrir();
                                    bdPrePallet.insertarPalletsInGP(pp.getIdPrePallet(), pp.getvPalletID(), pp.getIdGP());
                                    bdPrePallet.cerrar();

                                    /*bdPrePallet.abrir();
                                    bdPrePallet.cpdb.actualizaPPAfterSendGP(pp.getvUnicSessionKey(), pp.getIdGP(), pp.getvPalletID());
                                    bdPrePallet.cerrar();*/

                                    msj = "Inserción correcta en GP";
                                } else {
                                    msj = "Error al insertar en GP";
                                }
                            } catch (JSONException ex){
                                ex.printStackTrace();
                                msj = "Error al insertar en GP";
                            }

                            mostrarDialogo("Mensaje", msj);
                        } else {
                            mostrarDialogo("Error al insertar en GP", row.getString("Error"));
                        }
                    }
                } catch (JSONException ex){
                    ex.printStackTrace();
                    //msj = "Error al insertar en GP";
                }
            } else {
                Toast.makeText(nContext, "No hay conexión de red - No puede insertar a GP...", Toast.LENGTH_LONG).show();
            }

            pd.dismiss();
        }
    }

    private static class searchCaseInServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url, c;
        public int scanMode;
        public int mIDPP;
        public ProgressDialog pd;

        public searchCaseInServer(String url, String c, int idPP) {
            this.url = url;
            this.c = c;
            this.scanMode = sharedpreferences.getInt("scanMode", 1);
            this.mIDPP = idPP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(scanMode != 1){
                pd = new ProgressDialog(nContext);
                pd.setIndeterminate(true);
                pd.setMessage("Cargando...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }
        }

        @Override
        protected String doInBackground(String... args) {
            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("casecode", c));
                params.add(new BasicNameValuePair("idPP", mIDPP+""));
                params.add(new BasicNameValuePair("scanMode", scanMode+""));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
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
                t.printStackTrace();
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("iDWebMeth -- >", res);

            if(!res.contains("error")){
                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray respuesta = json.getJSONArray("table1");

                    if(respuesta.length() > 0) {
                        for(int i = 0; i < respuesta.length(); i++) {
                            JSONObject row = respuesta.getJSONObject(i);

                            boolean insercionCorrecta = insertCase(row.getString("vCodeCase"), row.getInt("idCaseDetails"), row.getString("vUUIDCD"), row.getString("vFolio"));

                            if(insercionCorrecta){
                                insertFolio(row.getString("vFolio"), row.getInt("CasesAvailable"), row.getDouble("lbsXBox"), row.getString("vGreenHouse"), row.getString("vCodeCaseHeader"));
                            }
                        }
                    } else {
                        Toast.makeText(nContext, "Sin resultados para el case ingresado", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con la conexión a internet", Toast.LENGTH_LONG).show();
                    Log.e("Error recibir datos", e.getMessage());
                    new searchCaseInServer(config.rutaWebServerOmar + "/searchCaseInServer", c, pp.getIdPrePallet()).execute();
                }
            } else {
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray respuesta = json.getJSONArray("table1");

                    int error = 0;

                    for(int i = 0; i < respuesta.length(); i++) {
                        JSONObject row = respuesta.getJSONObject(i);
                        error = row.getInt("error");
                    }

                    if(error == 1) {
                        mostrarDialogo("Mensaje", "Sin resultados para el case " + c);
                        reproducirSonido();
                    } else if(error == 2){
                        mostrarDialogo("Mensaje", "El SKU del Case NO conicide con el SKU del PrePallet");
                        reproducirSonido();
                    } else if(error == 3){
                        mostrarDialogo("Mensaje", "La Línea del Case NO conicide con la Línea del PrePallet");
                        reproducirSonido();
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            if(scanMode != 1)
                pd.dismiss();
        }
    }

    private static class AysnTaskSentToServer extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialogData;
        public String URL;
        private String casesJSON;
        private String foliosJSON;

        public AysnTaskSentToServer(String url) {
            this.URL = url;

            progressDialogData = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogData.setIndeterminate(true);
            progressDialogData.setCanceledOnTouchOutside(false);
            progressDialogData.setMessage("Por favor espere...");
            progressDialogData.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";

            guardarDatos();
            casesJSON = generarJSONCases();
            foliosJSON = generarJSONFolios();

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("jsonCP", casesJSON));
                params.add(new BasicNameValuePair("jsonFA", foliosJSON));
                params.add(new BasicNameValuePair("idPrePallet", pp.getIdPrePallet()+""));
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
                errorSave = true;
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
                t.printStackTrace();
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
                JSONArray resultadoFoliosJSON;
                JSONObject row;

                resultadoCasesJSON = json.optJSONArray("table1");
                resultadoFoliosJSON = json.optJSONArray("table2");

                bdPrePallet.abrir();

                for (int i = 0; i < resultadoCasesJSON.length(); i++) {
                    row = resultadoCasesJSON.getJSONObject(i);
                    bdPrePallet.cambiarEdoSyncCasesPrePallet(row.getString("vUUID").trim());
                }

                for (int i = 0; i < resultadoFoliosJSON.length(); i++) {
                    row = resultadoFoliosJSON.getJSONObject(i);
                    bdPrePallet.cambiarEdoSyncAsigFoliosPrePallet(row.getString("vUUID").trim());
                }

                bdPrePallet.cerrar();
            } catch (JSONException e) {
                errorSave = true;
                e.printStackTrace();
                //Toast.makeText(nContext, nContext.getResources().getString(R.string.success_sync), Toast.LENGTH_LONG).show();
            }

            progressDialogData.dismiss();

            if(!errorSave){
                AsignarPrepallet.ad2.dismiss();
                AsignarPrepallet.GetLineInfo(AsignarPrepallet.codigoLinea);
                mostrarDialogo("Mensaje", "Los datos del PrePallet se guardaron correctamente");
                //btnSendToGP.setVisibility(View.VISIBLE);
                //pp.getCajasPrePallet().clear();
                //pp.getFolioPerPallet().clear();
                //pp.getCajasPrePallet().removeAll(pp.getCajasPrePallet());
                //pp.getFolioPerPallet().removeAll(pp.getFolioPerPallet());
            } else {
                mostrarDialogo("Mensaje", "Error al guardar PrePallet");
                //Toast.makeText(nContext, "Error al guardar", Toast.LENGTH_LONG).show();
            }
        }
    }
}