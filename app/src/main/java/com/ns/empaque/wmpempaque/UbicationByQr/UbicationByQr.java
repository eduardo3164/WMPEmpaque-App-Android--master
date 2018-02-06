package com.ns.empaque.wmpempaque.UbicationByQr;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.MAP.Mapeo;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by jcalderon on 06/01/2016.
 */
public class UbicationByQr {
    public static Activity nContext;
    public static RelativeLayout content;
    public static LayoutInflater inflater;
    public static FloatingActionButton fabSearch, fabAdd, fabSend, fabAtras;
    public static String idLocation; //
    public static int letAdd=1, areRead=0, readLoc=0, alreadyConfirmed=0,
            idTypeLocation=-1, conCatalogosVista, busyLocation = 0; //variable que controla si se leen embalajes o no
    public static LinearLayout viewInfoLocation, lineInfoView, embalajes, layoutFormAinLine;
    public static JSONObject rowDatosLocat;
    public static JSONArray datosPermitidos, foliosFIFO;

    public static String[][] datosLocations, dataToSend, dataToSendMerma, camposCatalogID, camposCatalogName, skuPerFolio;
    public static ListView listEmbalajes, fifoList;
    public static Button btnSendLocat;
    public static EditText et_locatCode;
    public static Dialog dialog, dialogInLine;

    public static EditText etData;
    /*Variables para guardar lo mermado y las cajas recibidas por folio*/
    public static String[] cajasRealesRecibidas, cantidadMerma,
            razonMermaArray, comments, cajasCosecha, folios, nombreCampoPermitido,
            tipoDatoCampoPermitido, positions, nombreCampoMerma, tipoDatoCampoMerma,
            mermaReasonsName, mermaReasonsID, alimentadorName, alimentadorID, skuProduct, SKUinLine, pallets;


    public static String jsonFoliosInfo, idFarmLocation, idProductLogSelected, SKU;
    private static View viewNotALine, viewIsALine;


    public static int embalajeType;

    //Adaptador para pedir los campos
    public static GridViewAdapter gridAdapter;
    private static FIFOlineAdapter adaptFIFOLine;

    public static boolean allowSeveralRegisters = false, banderaSKUnotInLine = false;//bandera que controla si la locación permite uno o varios embalajes
    public static boolean banderaMerma[]; //array de Banderas para boton merma
    /*Variables para mandar al server al final*/
   /* public static int[] numCajasToPass;
    public static String[] folios;*/

    /*******************************************/
    public static Session session = null;

    /*Adaptador de la lista, para poner los
    folios o pallets leidos que se pasaran
    a otra locación
    */
    public static MyListAdapter myListAdapter;

    //lista para los folios y para la posicion de los folios con merma
    public static ArrayList<HashMap<String, String>> items, itemsMerma, itemsEmbalaje;

    public static TextView idLocat, tipoLocat, contentLocat,
            posiciones, qrCode, nameLocat, locatType, farm, width,
            height, lenght, lineName, skuLine;


    public UbicationByQr(Activity c){
        this.nContext = c;
        items = new ArrayList<HashMap<String,String>>();
        itemsMerma = new ArrayList<HashMap<String,String>>();
    }

    public UbicationByQr(Activity c, RelativeLayout content){
        this.nContext = c;
        this.content = content;

        items = new ArrayList<HashMap<String,String>>();
        itemsMerma = new ArrayList<HashMap<String,String>>();


    }

    public static String[] devolverItems(){
        String[] itemsitos = new String[items.size()];

        for(int i=0; i<items.size(); i++) {
            HashMap<String, String> item = items.get(i);
            itemsitos[i] = item.get("embalaje");
        }

        return itemsitos;
    }

    public static void ingresarItems(String[] itemsitos){
        for(int i=0; i<itemsitos.length; i++) {
            HashMap<String, String>  folioMap = new HashMap<String, String>();
            folioMap.put("embalaje",itemsitos[i]);
            items.add(folioMap);
        }

        myListAdapter.notifyDataSetChanged();

    }

    public static void verifyAllEmbalajes(){

        for(int p = 0; p<items.size(); p++){
            HashMap<String, String>  embalaje = items.get(p);

            HashMap<String, String>  itemEmb = new HashMap<String, String>();
            itemEmb.put("idEmbalaje", config.validaEmbalaje(embalaje.get("embalaje"), nContext));

            if(!itemsEmbalaje.contains(itemEmb)){
                items.remove(embalaje);
            }

        }

        if(allowSeveralRegisters == false)
            if(items.size() > 1)
                for(int i = 1; i<items.size(); i++)
                    items.remove(i);


        myListAdapter.notifyDataSetChanged();

    }

    public static void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.ubication_by_qr, null, true);
        config.updateContent(content, v);
        idLocation = "-1";

        /*Asignacion de componentes xml a objetos en java*/
        fabAtras = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        fabSearch = (FloatingActionButton) v.findViewById(R.id.fabLookfor);
        fabAdd =  (FloatingActionButton) v.findViewById(R.id.fabAdd);
        fabSend = (FloatingActionButton) v.findViewById(R.id.fabSendFolios);

        embalajes = (LinearLayout) v.findViewById(R.id.viewBb);
        lineInfoView = (LinearLayout) v.findViewById(R.id.infoLineView);
        lineName = (TextView) v.findViewById(R.id.lineName);
        skuLine = (TextView) v.findViewById(R.id.SkuLines);

        lineInfoView.setVisibility(View.INVISIBLE);

        fabSend.setVisibility(View.INVISIBLE);
        //fabAdd.setVisibility(View.INVISIBLE);

        viewInfoLocation = (LinearLayout) v.findViewById(R.id.viewInfoLocation);
        viewInfoLocation.setVisibility(View.INVISIBLE);

        btnSendLocat = (Button) v.findViewById(R.id.btnSendLocat);
        et_locatCode = (EditText) v.findViewById(R.id.et_locatCode);

        fabAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        et_locatCode.addTextChangedListener(new TextWatcher() {
            private String mText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String oj = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)", "");
                if (s.toString().contains("\n") == true) {
                    et_locatCode.setText(mText);
                    sendQRCodeToServer(oj);
                } else {
                    mText = "";
                }
            }
        });

        btnSendLocat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_locatCode.getText().toString().compareToIgnoreCase("") == 0)
                    Toast.makeText(nContext, "El campo de texto debe contener el codigo de la locación.",Toast.LENGTH_LONG).show();
                else{
                    sendQRCodeToServer(et_locatCode.getText().toString());
                }
            }
        });

        idLocat = (TextView) v.findViewById(R.id.idLocat);
        tipoLocat = (TextView) v.findViewById(R.id.tipoLocat);
        contentLocat = (TextView) v.findViewById(R.id.contentLocat);
        posiciones = (TextView) v.findViewById(R.id.posiciones);
        qrCode = (TextView) v.findViewById(R.id.QRCode);
        nameLocat = (TextView) v.findViewById(R.id.locatName);
        locatType = (TextView) v.findViewById(R.id.locationType);
        farm = (TextView) v.findViewById(R.id.farm);
        width = (TextView) v.findViewById(R.id.width);
        height = (TextView) v.findViewById(R.id.height);
        lenght =  (TextView) v.findViewById(R.id.lenght);

        //qrCode, nameLocat, locatType, farm, width, height, lenght;

        //Inflamos las vistas que se usaran en caso de que sea linea o en caso de no
        inflater = nContext.getLayoutInflater();
        viewNotALine = inflater.inflate(R.layout.viewnotaline, null, true);
        viewIsALine = inflater.inflate(R.layout.viewisaline, null, true);

        embalajes.removeAllViews();
        embalajes.addView(viewNotALine);

        listEmbalajes = (ListView) viewNotALine.findViewById(R.id.listViewEmbalaje);

        Button btnFormasA = (Button) viewNotALine.findViewById(R.id.btnGoFormasA);
        final EditText etAForm = (EditText) viewNotALine.findViewById(R.id.etAForm);

        etAForm.addTextChangedListener(new TextWatcher() {
            private String mText="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String oj = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)","");
                if(s.toString().contains("\n") == true){
                    etAForm.setText(mText);
                    addEmbalaje(oj);
                    etAForm.setText("");
                }else{
                    mText = "";
                }
            }
        });

       // embalajes.removeAllViews();
       // embalajes.addView(viewNotALine);

        eventsInCaseLine();

       // items = new ArrayList<HashMap<String,String>>();
        //itemsMerma = new ArrayList<HashMap<String,String>>();

        btnFormasA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etAForm.getText().toString().compareToIgnoreCase("") == 0)
                    Toast.makeText(nContext, "El campo de texto debe contener el codigo QR de la forma A.",Toast.LENGTH_LONG).show();
                else{
                    addEmbalaje(etAForm.getText().toString());
                }
            }
        });

        myListAdapter = new MyListAdapter(nContext, items);
        listEmbalajes.setAdapter(myListAdapter);

        accionsFloatingBtn();
    }



    private static void eventsInCaseLine() {

        final EditText txtSKU = (EditText) viewIsALine.findViewById(R.id.txtSKU);
        final Button btnGoFIFO = (Button) viewIsALine.findViewById(R.id.btnGoFIFO);
        fifoList = (ListView) viewIsALine.findViewById(R.id.fifoListView);
        final TextInputLayout inputLayout = (TextInputLayout) viewIsALine.findViewById(R.id.input_layout_SKU);
        layoutFormAinLine = (LinearLayout) viewIsALine.findViewById(R.id.layoutFormAinLine);
        inputLayout.setErrorEnabled(true);
        layoutFormAinLine.setVisibility(layoutFormAinLine.INVISIBLE);

        //inputLayout.setSoundEffectsEnabled(true);
       // inputLayout.setError("Este SKU no Existe");

        Button btnFormasAinLine = (Button) viewIsALine.findViewById(R.id.btnGoFormasA);
        final EditText etAForminLine = (EditText) viewIsALine.findViewById(R.id.etAForm);

        btnFormasAinLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etAForminLine.getText().toString().compareToIgnoreCase("") == 0)
                    Toast.makeText(nContext, "El campo de texto debe contener el codigo QR de la forma A.",Toast.LENGTH_LONG).show();
                else{
                    addEmbalaje(etAForminLine.getText().toString());
                }
            }
        });

        etAForminLine.addTextChangedListener(new TextWatcher() {
            private String mText="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String oj = s.toString().replaceAll("(\\r|\\n|\\t|\n|\t|\r)","");
                if(s.toString().contains("\n") == true){
                    etAForminLine.setText(mText);
                    addEmbalaje(oj);
                    etAForminLine.setText("");
                }else{
                    mText = "";
                }
            }
        });

        txtSKU.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnGoFIFO.setEnabled(true);
                if(verifySKUinLine(s.toString())){
                    Log.d("SKU","SKU in Line");
                    inputLayout.setError(null);
                    banderaSKUnotInLine = false;
                }else{
                    Log.d("SKU", "SKU not in line");
                    banderaSKUnotInLine = true;
                   // inputLayout.setError("El SKU no existe en la linea");

                    if(!verifySKUinItemMaster(s.toString())) {
                        inputLayout.setError("El SKU no existe.");
                        Log.d("SKU", "SKU not exists");
                        btnGoFIFO.setEnabled(false);
                        fifoList.setVisibility(fifoList.INVISIBLE);
                        fabAdd.setVisibility(fabAdd.INVISIBLE);
                        layoutFormAinLine.setVisibility(layoutFormAinLine.INVISIBLE);
                    }
                    else {
                        inputLayout.setError("El SKU no esta en la linea");
                    }
                }
            }
        });


        btnGoFIFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SKU = txtSKU.getText().toString();

                if (banderaSKUnotInLine) {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
                    alertDialog2
                            .setMessage("EL SKU insertado no lo tiene configurado la linea. ¿Desea Continuar?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //idLocation se guarda la locación
                                    new getFIFOinLine(config.rutaWebServerOmar + "/getFIFOinLine", idLocation, SKU).execute();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog2.setIcon(R.drawable.alerticon);
                    alertDialog2.setTitle("SKU no pertenece a linea");
                    alertDialog2.setCancelable(false);
                    AlertDialog ad2 = alertDialog2.create();
                    ad2.show();
                } else {
                    new getFIFOinLine(config.rutaWebServerOmar + "/getFIFOinLine", idLocation, SKU).execute();
                }

            }
        });



    }

    public static boolean verifySKUinLine(String SKU){

        List<String> T = Arrays.asList(SKUinLine);

        if(T.contains(SKU)) {
            Log.d("skuContain","true");
            return true;
        }

        Log.d("skuContain", "false");

        return false;
    }

    public static boolean verifySKUinItemMaster(String SKU){
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String[] datos = bd.getItemsMaster();
        bd.cerrar();

        List<String> T = Arrays.asList(datos);

        if(T.contains(SKU)) {
            Log.d("skuContain","true");
            return true;
        }

        Log.d("skuContain", "false");

        return false;
    }

    private static void accionsFloatingBtn() {
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
                readLoc = 1;
                letAdd = 1;
                //readLoc = 1;
                //nContext.startActivity(new Intent(nContext, ScanQR.class));
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readLoc = 0;
                letAdd= 1;
                if(!allowSeveralRegisters) {
                    if (items.size() > 0)
                        Toast.makeText(nContext, "Un solo registro permitido a esta locación", Toast.LENGTH_LONG).show();
                    else {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                        scanIntegrator.initiateScan();
                    }
                }
                else{
                    IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                    scanIntegrator.initiateScan();
                }
            }
        });

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToSend = new String[0][];
                dataToSendMerma = new String[0][];
                banderaMerma = new boolean[0];
                itemsMerma.clear();

                if(busyLocation == 1){
                    Toast.makeText(nContext, "La locación esta ocupada, No puede insertar folios", Toast.LENGTH_LONG).show();
                }else{
                    enviarFoliosToServer(nContext);
                }
            }
        });
    }

    public static void sendQRCodeToServer(String contents) {
        try {
            String qrCode[] = contents.split("\\|");
            positions = qrCode[1].split("\\.");

            Log.d("qrcode", qrCode[0] + "a");
            Log.d("contents", contents);
            Log.d("positions", qrCode[1]);

            if( (positions.length !=3) || (qrCode.length!=2) ) {
                Toast.makeText(nContext, "El codigo leido no es locación", Toast.LENGTH_LONG).show();
                et_locatCode.setText("");
                viewInfoLocation.setVisibility(View.INVISIBLE);
                //letAdd = 0;
                //readLoc = 1;
                //fabAdd.setVisibility(View.INVISIBLE);
                idLocation = "-1";

                items.clear();
                myListAdapter.notifyDataSetChanged();
            }
            else
                new sendQRCodeLocationToServer(config.rutaWebServerOmar + "/sendQRCodeLocation", "1", qrCode[0], nContext).execute();
        }catch(Exception e){
            Toast.makeText(nContext, "El codigo leido no es una locación",Toast.LENGTH_LONG).show();
            et_locatCode.setText("");
            viewInfoLocation.setVisibility(View.INVISIBLE);
            //letAdd = 0;
           // readLoc = 1;

           // fabAdd.setVisibility(View.INVISIBLE);
            idLocation = "-1";

            items.clear();
            myListAdapter.notifyDataSetChanged();
        }
    }

    public static void addEmbalajeFromMap( String content ) {


                HashMap<String, String> folioMap = new HashMap<String, String>();
                folioMap.put("embalaje", content);


                HashMap<String, String> itemEmb = new HashMap<String, String>();
                itemEmb.put("idEmbalaje", config.validaEmbalaje(content, nContext));

                if (itemsEmbalaje != null) {
                    if (itemsEmbalaje.contains(itemEmb))
                        items.add(folioMap);
                    else
                        Toast.makeText(nContext, "La locación no permite introducir ese tipo de embalajes", Toast.LENGTH_LONG).show();
                } else {
                    items.add(folioMap);
                }


              //  myListAdapter.notifyDataSetChanged();
            //    Log.d("addEm -->", content);
            //    areRead = 1;
                //fabSend.setVisibility(View.VISIBLE);




    }

    public static void addEmbalaje( String content ) {

        if(itemsEmbalaje == null){
            HashMap<String, String> folioMap = new HashMap<String, String>();
            folioMap.put("embalaje", content);
            if (!items.contains(folioMap)) {
                items.add(folioMap);
            } else
                Toast.makeText(nContext, "Ya existe un elemento igual", Toast.LENGTH_LONG).show();


            myListAdapter.notifyDataSetChanged();
            Log.d("addEm -->", content);
            areRead = 1;
            fabSend.setVisibility(View.VISIBLE);
        }else {
            if (idTypeLocation != 6) {
                HashMap<String, String> folioMap = new HashMap<String, String>();
                folioMap.put("embalaje", content);

                if (!allowSeveralRegisters)
                    if (items.size() > 0)
                        Toast.makeText(nContext, "Un solo registro permitido a esta locación", Toast.LENGTH_LONG).show();
                    else {
                        HashMap<String, String> itemEmb = new HashMap<String, String>();
                        itemEmb.put("idEmbalaje", config.validaEmbalaje(content, nContext));

                        if (itemsEmbalaje != null) {
                            if (itemsEmbalaje.contains(itemEmb))
                                items.add(folioMap);
                            else
                                Toast.makeText(nContext, "La locación no permite introducir ese tipo de embalajes", Toast.LENGTH_LONG).show();
                        } else {
                            items.add(folioMap);
                        }
                    }
                else if (!items.contains(folioMap)) {
                    HashMap<String, String> itemEmb = new HashMap<String, String>();
                    itemEmb.put("idEmbalaje", config.validaEmbalaje(content, nContext));

                    if (itemsEmbalaje.contains(itemEmb))
                        items.add(folioMap);
                    else
                        Toast.makeText(nContext, "La locación no permite introducir ese tipo de embalajes", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(nContext, "Ya existe un elemento igual", Toast.LENGTH_LONG).show();


                myListAdapter.notifyDataSetChanged();
                Log.d("addEm -->", content);
                areRead = 1;
                fabSend.setVisibility(View.VISIBLE);
            } else {
                if (lookForFolioinJSONArray(content)) {
                    items.clear();
                    String contents[] = new String[1];
                    contents[0] = content;

                    ingresarItems(contents);

                    enviarFoliosToServer(nContext);
                } else {
                    Toast.makeText(nContext, "Este folio no es el primero en la listo FIFO (First In First Out)", Toast.LENGTH_LONG).show();

                    items.clear();
                    String contents[] = new String[1];
                    contents[0] = content;

                    ingresarItems(contents);

                    enviarFoliosToServer(nContext);
                }

                items.clear();
            }
        }
    }

    private static boolean lookForFolioinJSONArray(String content){
        try {
           // for (int i = 0; i < foliosFIFO.length(); i++) {
                JSONObject row = foliosFIFO.getJSONObject(0);
                if(content.compareToIgnoreCase(row.getString("vFolio")) == 0)
                    return true;
           // }
        }catch(Exception ex){
            Log.e("Error", ex.getMessage());
        }

        return false;
    }



    public static void enviarFoliosToServer(Activity nContext){
        if(items.size() > 0) {
            String[] foliosQR = UbicationByQr.devolverItems();
            String resultsJSON = "[";
            try {
                for (int i = 0; i < foliosQR.length; i++)
                    resultsJSON += "{\"folioQR\":\"" + foliosQR[i] + "\"},";

                resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
                resultsJSON += "]";

                embalajeType = Integer.parseInt(config.validaEmbalaje(foliosQR[0], nContext)) ;

                //si es pallet
                if(embalajeType == 2){
                    new sendQRPalletToServer(config.rutaWebServerOmar + "/sendPalletToServer", resultsJSON, UbicationByQr.rowDatosLocat.getString("idQR"), nContext).execute();
                }else {
                    new sendQRFoliosToServer(config.rutaWebServerOmar + "/sendFoliosToServer", resultsJSON, UbicationByQr.rowDatosLocat.getString("idQR"), nContext).execute();
                }

            } catch (Exception e) {
                Toast.makeText(nContext, "No se pudo enviar folios al webservice", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(nContext, "Lea al menos un embalaje", Toast.LENGTH_LONG).show();
        }
    }

    public static void enviarFoliosToServerFromMap(Activity nContext){
        if(items.size() > 0) {
            String[] foliosQR = UbicationByQr.devolverItems();
            String resultsJSON = "[";
            try {
                for (int i = 0; i < foliosQR.length; i++)
                    resultsJSON += "{\"folioQR\":\"" + foliosQR[i] + "\"},";

                resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
                resultsJSON += "]";

                embalajeType = Integer.parseInt(config.validaEmbalaje(foliosQR[0], nContext)) ;

                //si es pallet
                if(embalajeType == 2){
                    new sendQRPalletToServerMap(config.rutaWebServerOmar + "/sendPalletToServer", resultsJSON, UbicationByQr.rowDatosLocat.getString("idQR"), nContext).execute();
                }else {
                    new sendQRFoliosToServerMap(config.rutaWebServerOmar + "/sendFoliosToServer", resultsJSON, idLocation, nContext).execute();
                }

            } catch (Exception e) {
                Toast.makeText(nContext, "No se pudo enviar folios al webservice", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(nContext, "Lea al menos un embalaje", Toast.LENGTH_LONG).show();
        }
    }



    private static class sendQRPalletToServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat;
        private Context nContext;
        ProgressDialog pd;

        public sendQRPalletToServer(String url, String json, String idLocat, Context nContext) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("json", json));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {

                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                JSONArray disponibles = json.getJSONArray("table4");

                if(disponibles.length() > 0){
                    showDialogDisponiblesPallet(disponibles);
                }else
                    showDialogPallet(buenos, malos, dataOut, 1);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRPalletToServerMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat;
        private Context nContext;
        ProgressDialog pd;

        public sendQRPalletToServerMap(String url, String json, String idLocat, Context nContext) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("json", json));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {

                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                JSONArray disponibles = json.getJSONArray("table4");

                if(disponibles.length() > 0){
                    showDialogDisponiblesPalletMap(disponibles);
                }else
                    showDialogPalletMap(buenos, malos, dataOut, 1);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRFoliosToServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat;
        private Context nContext;
        ProgressDialog pd;

        public sendQRFoliosToServer(String url, String json, String idLocat, Context nContext) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.nContext = nContext;

            Log.d("web", url+" - "+idLocat);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("json", json));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                JSONArray disponibles = json.getJSONArray("table4");

                if(disponibles.length() > 0){
                    showDialogDisponibles(disponibles);
                }else
                    showDialogFolios(buenos, malos, dataOut, 1);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRFoliosToServerMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat;
        private Context nContext;
        ProgressDialog pd;

        public sendQRFoliosToServerMap(String url, String json, String idLocat, Context nContext) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.nContext = nContext;

            Log.d("web", url+" - "+idLocat);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("json", json));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                JSONArray disponibles = json.getJSONArray("table4");

                if(disponibles.length() > 0){
                    showDialogDisponiblesMap(disponibles);
                }else
                    showDialogFoliosMap(buenos, malos, dataOut, 1);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }



    private static class sendQRFoliosDisponiblesToServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat, productLog;
        ProgressDialog pd;

        public sendQRFoliosDisponiblesToServer(String url, String idLocat, String idProductLog) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.productLog = idProductLog;
            idProductLogSelected = idProductLog;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", productLog));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                //JSONArray disponibles = json.getJSONArray("table4");

                showDialogFolios(buenos, malos, dataOut, 2);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRFoliosDisponiblesToServerMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat, productLog;
        ProgressDialog pd;

        public sendQRFoliosDisponiblesToServerMap(String url, String idLocat, String idProductLog) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.productLog = idProductLog;
            idProductLogSelected = idProductLog;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", productLog));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                //JSONArray disponibles = json.getJSONArray("table4");

                showDialogFoliosMap(buenos, malos, dataOut, 2);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRPalletsDisponiblesToServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat, productLog;
        ProgressDialog pd;

        public sendQRPalletsDisponiblesToServer(String url, String idLocat, String idProductLog) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.productLog = idProductLog;
            idProductLogSelected = idProductLog;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", productLog));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                //JSONArray disponibles = json.getJSONArray("table4");

                showDialogPallet(buenos, malos, dataOut, 2);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }

    private static class sendQRPalletsDisponiblesToServerMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String json, idLocat, productLog;
        ProgressDialog pd;

        public sendQRPalletsDisponiblesToServerMap(String url, String idLocat, String idProductLog) {
            this.url = url;
            this.json = json;
            this.idLocat = idLocat;
            this.productLog = idProductLog;
            idProductLogSelected = idProductLog;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", productLog));
                params.add(new BasicNameValuePair("idLocation", idLocat));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {
                JSONObject json     = new JSONObject(res);
                JSONArray buenos    = json.getJSONArray("table1");
                JSONArray malos     = json.getJSONArray("table2");
                final JSONArray dataOut   = json.getJSONArray("table3");
                //JSONArray disponibles = json.getJSONArray("table4");

                showDialogPalletMap(buenos, malos, dataOut, 2);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage());
            }

        }


    }



    public static void showDialogDisponibles(JSONArray disponibles) {
        final String[] idProductLog, x, y, z, idLocation, nameLocation, totalBoxes;

        idProductLog = new String[disponibles.length()];
        x = new String[disponibles.length()];
        y = new String[disponibles.length()];
        z = new String[disponibles.length()];
        idLocation = new String[disponibles.length()];
        nameLocation = new String[disponibles.length()];
        totalBoxes = new String[disponibles.length()];
        try {
            for (int i = 0; i < disponibles.length(); i++) {
                JSONObject row = disponibles.getJSONObject(i);
                idProductLog[i] = row.getString("id_ProductLog");
                x[i] = row.getString("iXPosition");
                y[i] = row.getString("iYPosition");
                z[i] = row.getString("iZPosition");
                idLocation[i] = row.getString("id_Location");
                nameLocation[i] = row.getString("vNameLocation");
                totalBoxes[i] = row.getString("cajas");
            }


            LayoutInflater inflater = nContext.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.viewdisponibleslist, null);

            ListView disponiblesList = (ListView) dialoglayout.findViewById(R.id.listfoliosdisponibles);
            disponiblesAdapter adapter = new disponiblesAdapter(nContext,x,y,z,nameLocation, totalBoxes);
            disponiblesList.setAdapter(adapter);




            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
            alertDialog.setTitle("Embalaje en mas de una locación");
            // .setMessage("Are you sure you want to delete this entry?")
            alertDialog.setView(dialoglayout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setCancelable(false);
            final AlertDialog dialog = alertDialog.create();
            dialog.show();


            disponiblesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  /*myAsyncTaskDisponibles asTD = new myAsyncTaskDisponibles(nContext, config.rutaWebServerJavier+"/sendDisponibles", idProductLog[position]);
                    asTD.execute();*/
                    try {
                        new sendQRFoliosDisponiblesToServer(config.rutaWebServerOmar + "/sendFoliosDisponiblesToServer",
                                UbicationByQr.rowDatosLocat.getString("idQR"), idProductLog[position]).execute();

                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                    Toast.makeText(nContext, "idProductLog: " + idProductLog[position], Toast.LENGTH_LONG).show();

                }
            });

        }catch(Exception e){
            Log.d("Error",e.getMessage());
        }
    }

    public static void showDialogDisponiblesMap(JSONArray disponibles) {
        final String[] idProductLog, x, y, z, idLocation, nameLocation, totalBoxes;

        idProductLog = new String[disponibles.length()];
        x = new String[disponibles.length()];
        y = new String[disponibles.length()];
        z = new String[disponibles.length()];
        idLocation = new String[disponibles.length()];
        nameLocation = new String[disponibles.length()];
        totalBoxes = new String[disponibles.length()];
        try {
            for (int i = 0; i < disponibles.length(); i++) {
                JSONObject row = disponibles.getJSONObject(i);
                idProductLog[i] = row.getString("id_ProductLog");
                x[i] = row.getString("iXPosition");
                y[i] = row.getString("iYPosition");
                z[i] = row.getString("iZPosition");
                idLocation[i] = row.getString("id_Location");
                nameLocation[i] = row.getString("vNameLocation");
                totalBoxes[i] = row.getString("cajas");
            }


            LayoutInflater inflater = nContext.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.viewdisponibleslist, null);

            ListView disponiblesList = (ListView) dialoglayout.findViewById(R.id.listfoliosdisponibles);
            disponiblesAdapter adapter = new disponiblesAdapter(nContext,x,y,z,nameLocation, totalBoxes);
            disponiblesList.setAdapter(adapter);




            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
            alertDialog.setTitle("Embalaje en mas de una locación");
            // .setMessage("Are you sure you want to delete this entry?")
            alertDialog.setView(dialoglayout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setCancelable(false);
            final AlertDialog dialog = alertDialog.create();
            dialog.show();


            disponiblesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  /*myAsyncTaskDisponibles asTD = new myAsyncTaskDisponibles(nContext, config.rutaWebServerJavier+"/sendDisponibles", idProductLog[position]);
                    asTD.execute();*/
                    try {
                        new sendQRFoliosDisponiblesToServerMap(config.rutaWebServerOmar + "/sendFoliosDisponiblesToServer",
                                UbicationByQr.idLocation, idProductLog[position]).execute();

                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                    Toast.makeText(nContext, "idProductLog: " + idProductLog[position], Toast.LENGTH_LONG).show();

                }
            });

        }catch(Exception e){
            Log.d("Error",e.getMessage());
        }
    }

    public static void showDialogDisponiblesPallet(JSONArray disponibles) {
        final String[] idProductLog, x, y, z, idLocation, nameLocation, totalBoxes;

        idProductLog = new String[disponibles.length()];
        x = new String[disponibles.length()];
        y = new String[disponibles.length()];
        z = new String[disponibles.length()];
        idLocation = new String[disponibles.length()];
        nameLocation = new String[disponibles.length()];
        totalBoxes = new String[disponibles.length()];
        try {
            for (int i = 0; i < disponibles.length(); i++) {
                JSONObject row = disponibles.getJSONObject(i);
                idProductLog[i] = row.getString("id_ProductLog");
                x[i] = row.getString("iXPosition");
                y[i] = row.getString("iYPosition");
                z[i] = row.getString("iZPosition");
                idLocation[i] = row.getString("id_Location");
                nameLocation[i] = row.getString("vNameLocation");
                totalBoxes[i] = row.getString("cajas");
            }


            LayoutInflater inflater = nContext.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.viewdisponibleslist, null);

            ListView disponiblesList = (ListView) dialoglayout.findViewById(R.id.listfoliosdisponibles);
            disponiblesAdapter adapter = new disponiblesAdapter(nContext,x,y,z,nameLocation, totalBoxes);
            disponiblesList.setAdapter(adapter);




            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
            alertDialog.setTitle("Embalaje en mas de una locación");
            // .setMessage("Are you sure you want to delete this entry?")
            alertDialog.setView(dialoglayout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setCancelable(false);
            final AlertDialog dialog = alertDialog.create();
            dialog.show();


            disponiblesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  /*myAsyncTaskDisponibles asTD = new myAsyncTaskDisponibles(nContext, config.rutaWebServerJavier+"/sendDisponibles", idProductLog[position]);
                    asTD.execute();*/
                    try {
                        new sendQRPalletsDisponiblesToServer(config.rutaWebServerOmar + "/sendPalletsDisponiblesToServer",
                                UbicationByQr.rowDatosLocat.getString("idQR"), idProductLog[position]).execute();

                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                    Toast.makeText(nContext, "idProductLog: " + idProductLog[position], Toast.LENGTH_LONG).show();

                }
            });

        }catch(Exception e){
            Log.d("Error",e.getMessage());
        }
    }

    public static void showDialogDisponiblesPalletMap(JSONArray disponibles) {
        final String[] idProductLog, x, y, z, idLocation, nameLocation, totalBoxes;

        idProductLog = new String[disponibles.length()];
        x = new String[disponibles.length()];
        y = new String[disponibles.length()];
        z = new String[disponibles.length()];
        idLocation = new String[disponibles.length()];
        nameLocation = new String[disponibles.length()];
        totalBoxes = new String[disponibles.length()];
        try {
            for (int i = 0; i < disponibles.length(); i++) {
                JSONObject row = disponibles.getJSONObject(i);
                idProductLog[i] = row.getString("id_ProductLog");
                x[i] = row.getString("iXPosition");
                y[i] = row.getString("iYPosition");
                z[i] = row.getString("iZPosition");
                idLocation[i] = row.getString("id_Location");
                nameLocation[i] = row.getString("vNameLocation");
                totalBoxes[i] = row.getString("cajas");
            }


            LayoutInflater inflater = nContext.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.viewdisponibleslist, null);

            ListView disponiblesList = (ListView) dialoglayout.findViewById(R.id.listfoliosdisponibles);
            disponiblesAdapter adapter = new disponiblesAdapter(nContext,x,y,z,nameLocation, totalBoxes);
            disponiblesList.setAdapter(adapter);




            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
            alertDialog.setTitle("Embalaje en mas de una locación");
            // .setMessage("Are you sure you want to delete this entry?")
            alertDialog.setView(dialoglayout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setCancelable(false);
            final AlertDialog dialog = alertDialog.create();
            dialog.show();


            disponiblesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  /*myAsyncTaskDisponibles asTD = new myAsyncTaskDisponibles(nContext, config.rutaWebServerJavier+"/sendDisponibles", idProductLog[position]);
                    asTD.execute();*/
                    try {
                        new sendQRPalletsDisponiblesToServerMap(config.rutaWebServerOmar + "/sendPalletsDisponiblesToServer",
                                UbicationByQr.rowDatosLocat.getString("idQR"), idProductLog[position]).execute();

                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                    Toast.makeText(nContext, "idProductLog: " + idProductLog[position], Toast.LENGTH_LONG).show();

                }
            });

        }catch(Exception e){
            Log.d("Error",e.getMessage());
        }
    }



    private static void showDialogPallet(JSONArray buenos, JSONArray malos,final JSONArray dataOut, final int Action) {
        try {
            pallets = new String[buenos.length()];

            String malosQR[] = new String[malos.length()];

            if (buenos.length() > 0) {
                // String idProductClass[] = new String[buenos.length()];
                for (int i = 0; i < buenos.length(); i++) {
                    JSONObject row = buenos.getJSONObject(i);
                    pallets[i] = row.getString("Pallet");
                }
            }

            Log.d("step1", "step1");
            for (int i = 0; i < malos.length(); i++) {
                JSONObject row = malos.getJSONObject(i);
                malosQR[i] = row.getString("QR");
            }

            Log.d("step1", "step1");
            if (dataOut.length() > 0) {
                String[] nombreCampoOut = new String[dataOut.length()];
                String[] dataTypeCampoOut = new String[dataOut.length()];
                Log.d("step1", "step1.1");
                for (int i = 0; i < dataOut.length(); i++) {
                    JSONObject row = dataOut.getJSONObject(i);
                    nombreCampoOut[i] = row.getString("campos");
                    dataTypeCampoOut[i] = row.getString("tipoDato");
                    Log.d("campoOut", row.getString("campos"));
                }

                Log.d("step1", "step1.1");
                if (nombreCampoPermitido == null) {
                    nombreCampoPermitido = new String[dataOut.length()];
                    tipoDatoCampoPermitido = new String[dataOut.length()];

                    Log.d("step1", "entro null");
                    nombreCampoPermitido = nombreCampoOut;
                    tipoDatoCampoPermitido = dataTypeCampoOut;
                } else {
                    //  String [] respaldoNombreCampoPermitido = Arrays.copyOfRange(nombreCampoPermitido,0,nombreCampoPermitido.length-1);
                    //  String [] respaldoTipoDatoPermitido = Arrays.copyOfRange(tipoDatoCampoPermitido,0,tipoDatoCampoPermitido.length-1);
                    String[] respaldoNombreCampoPermitido = new String[nombreCampoPermitido.length];
                    String[] respaldoTipoDatoPermitido = new String[tipoDatoCampoPermitido.length];
                    Log.d("step1", "step1.2");
                    for (int i = 0; i < nombreCampoPermitido.length; i++) {
                        respaldoNombreCampoPermitido[i] = nombreCampoPermitido[i];
                        respaldoTipoDatoPermitido[i] = tipoDatoCampoPermitido[i];
                    }

                    Log.d("step1", "step1.3");
                    nombreCampoPermitido = new String[respaldoNombreCampoPermitido.length + nombreCampoOut.length];
                    tipoDatoCampoPermitido = new String[respaldoTipoDatoPermitido.length + dataTypeCampoOut.length];

                    for (int i = 0; i < respaldoNombreCampoPermitido.length; i++) {
                        nombreCampoPermitido[i] = respaldoNombreCampoPermitido[i];
                        tipoDatoCampoPermitido[i] = respaldoTipoDatoPermitido[i];
                    }

                    for (int i = respaldoNombreCampoPermitido.length, j = 0; i < (respaldoNombreCampoPermitido.length + nombreCampoOut.length); i++, j++) {
                        nombreCampoPermitido[i] = nombreCampoOut[j];
                        tipoDatoCampoPermitido[i] = dataTypeCampoOut[j];
                    }
                }


            }
            Log.d("step1", "step1.10");
            try {
                Log.d("taste", dataToSend[buenos.length() - 1][0]);
                dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            } catch (Exception e) {
                if (nombreCampoPermitido != null)
                    dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            }




            Log.d("step1", "step1");

            dialog = new Dialog(nContext);
            dialog.setContentView(R.layout.viewfoliostosend);
            dialog.setTitle("Confirme y llene los datos");
            dialog.setCanceledOnTouchOutside(false);


            Log.d("step", "step2");
            // set the custom dialog components
            ListView lgood = (ListView) dialog.findViewById(R.id.listViewGood);
            ListView lbad = (ListView) dialog.findViewById(R.id.listViewBad);

            Log.d("step", "step3");
            if (malosQR.length == 0) {
                malosQR = new String[1];
                malosQR[0] = "Todos los Pallets fueron permitidos";
            }

            ArrayAdapter<String> LABad = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, malosQR);
            lbad.setAdapter(LABad);

            Log.d("step", "step4");
            try {
                MyListAdapterGood LAGood = new MyListAdapterGood( pallets, buenos);
                lgood.setAdapter(LAGood);
            } catch (Exception e) {
                Log.e("Error1:", e.getMessage());
            }


            Log.d("step1", "step5");
            //floating buttons
            FloatingActionButton fabConfirm = (FloatingActionButton) dialog.findViewById(R.id.fabConfirmSend);
            FloatingActionButton fabCancel = (FloatingActionButton) dialog.findViewById(R.id.fabCancelSend);

            if (pallets.length == 0)
                fabConfirm.setVisibility(View.VISIBLE);

            Log.d("step1", "step6");
            alreadyConfirmed = 1;
            dialog.show();

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alreadyConfirmed = 0;

                    if (dataOut.length() > 0) {
                        nombreCampoPermitido = Arrays.copyOf(nombreCampoPermitido, nombreCampoPermitido.length - 1);
                        tipoDatoCampoPermitido = Arrays.copyOf(tipoDatoCampoPermitido, tipoDatoCampoPermitido.length - 1);
                    }
                }
            });

            fabConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*boolean isNumber = true;

                    try{
                        Float.parseFloat(etData.getText().toString());
                    } catch (NumberFormatException ex){
                        isNumber= false;
                    }

                    if(isNumber)
                        Toast.makeText(nContext, "OK 1", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(nContext, "ERROR 1", Toast.LENGTH_LONG).show();*/

                    InsertarPallets(Action);
                }
            });

            if (!(buenos.length() > 0))
                fabConfirm.setVisibility(View.INVISIBLE);
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private static void showDialogPalletMap(JSONArray buenos, JSONArray malos,final JSONArray dataOut, final int Action) {
        try {
            pallets = new String[buenos.length()];

            String malosQR[] = new String[malos.length()];

            if (buenos.length() > 0) {
                // String idProductClass[] = new String[buenos.length()];
                for (int i = 0; i < buenos.length(); i++) {
                    JSONObject row = buenos.getJSONObject(i);
                    pallets[i] = row.getString("Pallet");
                }
            }

            Log.d("step1", "step1");
            for (int i = 0; i < malos.length(); i++) {
                JSONObject row = malos.getJSONObject(i);
                malosQR[i] = row.getString("QR");
            }

            Log.d("step1", "step1");
            if (dataOut.length() > 0) {
                String[] nombreCampoOut = new String[dataOut.length()];
                String[] dataTypeCampoOut = new String[dataOut.length()];
                Log.d("step1", "step1.1");
                for (int i = 0; i < dataOut.length(); i++) {
                    JSONObject row = dataOut.getJSONObject(i);
                    nombreCampoOut[i] = row.getString("campos");
                    dataTypeCampoOut[i] = row.getString("tipoDato");
                    Log.d("campoOut", row.getString("campos"));
                }

                Log.d("step1", "step1.1");
                if (nombreCampoPermitido == null) {
                    nombreCampoPermitido = new String[dataOut.length()];
                    tipoDatoCampoPermitido = new String[dataOut.length()];

                    Log.d("step1", "entro null");
                    nombreCampoPermitido = nombreCampoOut;
                    tipoDatoCampoPermitido = dataTypeCampoOut;
                } else {
                    //  String [] respaldoNombreCampoPermitido = Arrays.copyOfRange(nombreCampoPermitido,0,nombreCampoPermitido.length-1);
                    //  String [] respaldoTipoDatoPermitido = Arrays.copyOfRange(tipoDatoCampoPermitido,0,tipoDatoCampoPermitido.length-1);
                    String[] respaldoNombreCampoPermitido = new String[nombreCampoPermitido.length];
                    String[] respaldoTipoDatoPermitido = new String[tipoDatoCampoPermitido.length];
                    Log.d("step1", "step1.2");
                    for (int i = 0; i < nombreCampoPermitido.length; i++) {
                        respaldoNombreCampoPermitido[i] = nombreCampoPermitido[i];
                        respaldoTipoDatoPermitido[i] = tipoDatoCampoPermitido[i];
                    }

                    Log.d("step1", "step1.3");
                    nombreCampoPermitido = new String[respaldoNombreCampoPermitido.length + nombreCampoOut.length];
                    tipoDatoCampoPermitido = new String[respaldoTipoDatoPermitido.length + dataTypeCampoOut.length];

                    for (int i = 0; i < respaldoNombreCampoPermitido.length; i++) {
                        nombreCampoPermitido[i] = respaldoNombreCampoPermitido[i];
                        tipoDatoCampoPermitido[i] = respaldoTipoDatoPermitido[i];
                    }

                    for (int i = respaldoNombreCampoPermitido.length, j = 0; i < (respaldoNombreCampoPermitido.length + nombreCampoOut.length); i++, j++) {
                        nombreCampoPermitido[i] = nombreCampoOut[j];
                        tipoDatoCampoPermitido[i] = dataTypeCampoOut[j];
                    }
                }


            }
            Log.d("step1", "step1.10");
            try {
                Log.d("taste", dataToSend[buenos.length() - 1][0]);
                dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            } catch (Exception e) {
                if (nombreCampoPermitido != null)
                    dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            }

            Log.d("step1", "step1");

            dialog = new Dialog(nContext);
            dialog.setContentView(R.layout.viewfoliostosend);
            dialog.setTitle("Confirme y llene los datos");
            dialog.setCanceledOnTouchOutside(false);


            Log.d("step", "step2");
            // set the custom dialog components
            ListView lgood = (ListView) dialog.findViewById(R.id.listViewGood);
            ListView lbad = (ListView) dialog.findViewById(R.id.listViewBad);

            Log.d("step", "step3");
            if (malosQR.length == 0) {
                malosQR = new String[1];
                malosQR[0] = "Todos los Pallets fueron permitidos";
            }

            ArrayAdapter<String> LABad = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, malosQR);
            lbad.setAdapter(LABad);

            Log.d("step", "step4");
            try {
                MyListAdapterGood LAGood = new MyListAdapterGood( pallets, buenos);
                lgood.setAdapter(LAGood);
            } catch (Exception e) {
                Log.e("Error1:", e.getMessage());
            }


            Log.d("step1", "step5");
            //floating buttons
            FloatingActionButton fabConfirm = (FloatingActionButton) dialog.findViewById(R.id.fabConfirmSend);
            FloatingActionButton fabCancel = (FloatingActionButton) dialog.findViewById(R.id.fabCancelSend);

            if (pallets.length == 0)
                fabConfirm.setVisibility(View.VISIBLE);

            Log.d("step1", "step6");
            alreadyConfirmed = 1;
            dialog.show();

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alreadyConfirmed = 0;

                    if (dataOut.length() > 0) {
                        nombreCampoPermitido = Arrays.copyOf(nombreCampoPermitido, nombreCampoPermitido.length - 1);
                        tipoDatoCampoPermitido = Arrays.copyOf(tipoDatoCampoPermitido, tipoDatoCampoPermitido.length - 1);
                    }
                }
            });

            fabConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*boolean isNumber = true;

                    try{
                        Float.parseFloat(etData.getText().toString());
                    } catch (NumberFormatException ex){
                        isNumber= false;
                    }

                    if(isNumber)
                        Toast.makeText(nContext, "OK 2", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(nContext, "ERROR 2", Toast.LENGTH_LONG).show();*/

                    InsertarPalletsMap(Action);
                }
            });

            if (!(buenos.length() > 0))
                fabConfirm.setVisibility(View.INVISIBLE);
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private static void showDialogFolios(JSONArray buenos, JSONArray malos,final JSONArray dataOut, final int Action) {
        try {
            folios = new String[buenos.length()];
            cajasRealesRecibidas = new String[buenos.length()];
            cajasCosecha = new String[buenos.length()];
            cantidadMerma = new String[buenos.length()];
            razonMermaArray = new String[buenos.length()];
            comments = new String[buenos.length()];

            try {
                Log.d("taste", dataToSendMerma[buenos.length() - 1][0] + "" + banderaMerma[buenos.length() - 1]);
            } catch (Exception e) {
                dataToSendMerma = new String[buenos.length()][];
                banderaMerma = new boolean[buenos.length()];
            }

            String malosQR[] = new String[malos.length()];

            if (buenos.length() > 0) {
                String idProductClass[] = new String[buenos.length()];
                for (int i = 0; i < buenos.length(); i++) {
                    JSONObject row = buenos.getJSONObject(i);
                    folios[i] = row.getString("Folios");
                    idProductClass[i] = row.getString("idProductClass");
                    cajasCosecha[i] = row.getString("cajas");
                    cajasRealesRecibidas[i] = row.getString("cajas");
                    banderaMerma[i] = false;
                }

                BaseDatos bd = new BaseDatos(nContext);
                bd.abrir();
                String datosSku[][] = bd.getSkuQuality(idProductClass[0]);
                bd.cerrar();
                Log.d("step1", "step1");
                if (datosSku.length > 0) {
                    skuProduct = new String[datosSku.length];
                    for (int i = 0; i < datosSku.length; i++)
                        skuProduct[i] = datosSku[i][1];
                }
            }
            Log.d("step1", "step1");
            for (int i = 0; i < malos.length(); i++) {
                JSONObject row = malos.getJSONObject(i);
                malosQR[i] = row.getString("QR");
            }
            Log.d("step1", "step1");
            if (dataOut.length() > 0) {
                String[] nombreCampoOut = new String[dataOut.length()];
                String[] dataTypeCampoOut = new String[dataOut.length()];
                Log.d("step1", "step1.1");
                for (int i = 0; i < dataOut.length(); i++) {
                    JSONObject row = dataOut.getJSONObject(i);
                    nombreCampoOut[i] = row.getString("campos");
                    dataTypeCampoOut[i] = row.getString("tipoDato");
                    Log.d("campoOut", row.getString("campos"));
                }

                Log.d("step1", "step1.1");
                if (nombreCampoPermitido == null) {
                    nombreCampoPermitido = new String[dataOut.length()];
                    tipoDatoCampoPermitido = new String[dataOut.length()];

                    Log.d("step1", "entro null");
                    nombreCampoPermitido = nombreCampoOut;
                    tipoDatoCampoPermitido = dataTypeCampoOut;
                } else {
                    //  String [] respaldoNombreCampoPermitido = Arrays.copyOfRange(nombreCampoPermitido,0,nombreCampoPermitido.length-1);
                    //  String [] respaldoTipoDatoPermitido = Arrays.copyOfRange(tipoDatoCampoPermitido,0,tipoDatoCampoPermitido.length-1);
                    String[] respaldoNombreCampoPermitido = new String[nombreCampoPermitido.length];
                    String[] respaldoTipoDatoPermitido = new String[tipoDatoCampoPermitido.length];
                    Log.d("step1", "step1.2");
                    for (int i = 0; i < nombreCampoPermitido.length; i++) {
                        respaldoNombreCampoPermitido[i] = nombreCampoPermitido[i];
                        respaldoTipoDatoPermitido[i] = tipoDatoCampoPermitido[i];
                    }

                    Log.d("step1", "step1.3");
                    nombreCampoPermitido = new String[respaldoNombreCampoPermitido.length + nombreCampoOut.length];
                    tipoDatoCampoPermitido = new String[respaldoTipoDatoPermitido.length + dataTypeCampoOut.length];

                    for (int i = 0; i < respaldoNombreCampoPermitido.length; i++) {
                        nombreCampoPermitido[i] = respaldoNombreCampoPermitido[i];
                        tipoDatoCampoPermitido[i] = respaldoTipoDatoPermitido[i];
                    }

                    for (int i = respaldoNombreCampoPermitido.length, j = 0; i < (respaldoNombreCampoPermitido.length + nombreCampoOut.length); i++, j++) {
                        nombreCampoPermitido[i] = nombreCampoOut[j];
                        tipoDatoCampoPermitido[i] = dataTypeCampoOut[j];
                    }
                }


            }
            Log.d("step1", "step1.10");
            try {
                Log.d("taste", dataToSend[buenos.length() - 1][0]);
                dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            } catch (Exception e) {
                if (nombreCampoPermitido != null)
                    dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            }

            Log.d("step1", "step1");

            dialog = new Dialog(nContext);
            dialog.setContentView(R.layout.viewfoliostosend);
            dialog.setTitle("Confirme y llene los datos");
            dialog.setCanceledOnTouchOutside(false);


            Log.d("step", "step2");
            // set the custom dialog components
            ListView lgood = (ListView) dialog.findViewById(R.id.listViewGood);
            ListView lbad = (ListView) dialog.findViewById(R.id.listViewBad);

            Log.d("step", "step3");
            if (malosQR.length == 0) {
                malosQR = new String[1];
                malosQR[0] = "Todos los folios fueron permitidos";
            }

            ArrayAdapter<String> LABad = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, malosQR);
            lbad.setAdapter(LABad);

            Log.d("step", "step4");
            try {
                MyListAdapterGood LAGood = new MyListAdapterGood(folios, buenos);
                lgood.setAdapter(LAGood);
            } catch (Exception e) {
                Log.e("Error1:", e.getMessage());
            }


            Log.d("step1", "step5");
            //floating buttons
            FloatingActionButton fabConfirm = (FloatingActionButton) dialog.findViewById(R.id.fabConfirmSend);
            FloatingActionButton fabCancel = (FloatingActionButton) dialog.findViewById(R.id.fabCancelSend);

            if (folios.length == 0)
                fabConfirm.setVisibility(View.VISIBLE);

            Log.d("step1", "step6");
            alreadyConfirmed = 1;
            dialog.show();

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alreadyConfirmed = 0;

                    if (dataOut.length() > 0) {
                        nombreCampoPermitido = Arrays.copyOf(nombreCampoPermitido, nombreCampoPermitido.length - 1);
                        tipoDatoCampoPermitido = Arrays.copyOf(tipoDatoCampoPermitido, tipoDatoCampoPermitido.length - 1);
                    }
                }
            });

            fabConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isNumber = true;

                    try{
                        Float.parseFloat(etData.getText().toString());
                    } catch (NumberFormatException ex){
                        isNumber= false;
                    }

                    if(isNumber)
                        InsertarFolios(Action);
                    else
                        Toast.makeText(nContext, "Número de cajas incorrecto", Toast.LENGTH_LONG).show();
                }
            });

            if (!(buenos.length() > 0))
                fabConfirm.setVisibility(View.INVISIBLE);
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private static void showDialogFoliosMap(JSONArray buenos, JSONArray malos,final JSONArray dataOut, final int Action) {
        try {
            folios = new String[buenos.length()];
            cajasRealesRecibidas = new String[buenos.length()];
            cajasCosecha = new String[buenos.length()];
            cantidadMerma = new String[buenos.length()];
            razonMermaArray = new String[buenos.length()];
            comments = new String[buenos.length()];

            try {
                Log.d("taste", dataToSendMerma[buenos.length() - 1][0] + "" + banderaMerma[buenos.length() - 1]);
            } catch (Exception e) {
                dataToSendMerma = new String[buenos.length()][];
                banderaMerma = new boolean[buenos.length()];
            }

            String malosQR[] = new String[malos.length()];

            if (buenos.length() > 0) {
                String idProductClass[] = new String[buenos.length()];
                for (int i = 0; i < buenos.length(); i++) {
                    JSONObject row = buenos.getJSONObject(i);
                    folios[i] = row.getString("Folios");
                    idProductClass[i] = row.getString("idProductClass");
                    cajasCosecha[i] = row.getString("cajas");
                    cajasRealesRecibidas[i] = row.getString("cajas");
                    banderaMerma[i] = false;
                }

                BaseDatos bd = new BaseDatos(nContext);
                bd.abrir();
                String datosSku[][] = bd.getSkuQuality(idProductClass[0]);
                bd.cerrar();
                Log.d("step1", "step1");
                if (datosSku.length > 0) {
                    skuProduct = new String[datosSku.length];
                    for (int i = 0; i < datosSku.length; i++)
                        skuProduct[i] = datosSku[i][1];
                }
            }
            Log.d("step1", "step1");
            for (int i = 0; i < malos.length(); i++) {
                JSONObject row = malos.getJSONObject(i);
                malosQR[i] = row.getString("QR");
            }
            Log.d("step1", "step1");
            if (dataOut.length() > 0) {
                String[] nombreCampoOut = new String[dataOut.length()];
                String[] dataTypeCampoOut = new String[dataOut.length()];
                Log.d("step1", "step1.1");
                for (int i = 0; i < dataOut.length(); i++) {
                    JSONObject row = dataOut.getJSONObject(i);
                    nombreCampoOut[i] = row.getString("campos");
                    dataTypeCampoOut[i] = row.getString("tipoDato");
                    Log.d("campoOut", row.getString("campos"));
                }

                Log.d("step1", "step1.1");
                if (nombreCampoPermitido == null) {
                    nombreCampoPermitido = new String[dataOut.length()];
                    tipoDatoCampoPermitido = new String[dataOut.length()];

                    Log.d("step1", "entro null");
                    nombreCampoPermitido = nombreCampoOut;
                    tipoDatoCampoPermitido = dataTypeCampoOut;
                } else {
                    //  String [] respaldoNombreCampoPermitido = Arrays.copyOfRange(nombreCampoPermitido,0,nombreCampoPermitido.length-1);
                    //  String [] respaldoTipoDatoPermitido = Arrays.copyOfRange(tipoDatoCampoPermitido,0,tipoDatoCampoPermitido.length-1);
                    String[] respaldoNombreCampoPermitido = new String[nombreCampoPermitido.length];
                    String[] respaldoTipoDatoPermitido = new String[tipoDatoCampoPermitido.length];
                    Log.d("step1", "step1.2");
                    for (int i = 0; i < nombreCampoPermitido.length; i++) {
                        respaldoNombreCampoPermitido[i] = nombreCampoPermitido[i];
                        respaldoTipoDatoPermitido[i] = tipoDatoCampoPermitido[i];
                    }

                    Log.d("step1", "step1.3");
                    nombreCampoPermitido = new String[respaldoNombreCampoPermitido.length + nombreCampoOut.length];
                    tipoDatoCampoPermitido = new String[respaldoTipoDatoPermitido.length + dataTypeCampoOut.length];

                    for (int i = 0; i < respaldoNombreCampoPermitido.length; i++) {
                        nombreCampoPermitido[i] = respaldoNombreCampoPermitido[i];
                        tipoDatoCampoPermitido[i] = respaldoTipoDatoPermitido[i];
                    }

                    for (int i = respaldoNombreCampoPermitido.length, j = 0; i < (respaldoNombreCampoPermitido.length + nombreCampoOut.length); i++, j++) {
                        nombreCampoPermitido[i] = nombreCampoOut[j];
                        tipoDatoCampoPermitido[i] = dataTypeCampoOut[j];
                    }
                }


            }
            Log.d("step1", "step1.10");
            try {
                Log.d("taste", dataToSend[buenos.length() - 1][0]);
                dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            } catch (Exception e) {
                if (nombreCampoPermitido != null)
                    dataToSend = new String[buenos.length()][nombreCampoPermitido.length];
            }

            Log.d("step1", "step1");

            dialog = new Dialog(nContext);
            dialog.setContentView(R.layout.viewfoliostosend);
            dialog.setTitle("Confirme y llene los datos");
            dialog.setCanceledOnTouchOutside(false);


            Log.d("step", "step2");
            // set the custom dialog components
            ListView lgood = (ListView) dialog.findViewById(R.id.listViewGood);
            ListView lbad = (ListView) dialog.findViewById(R.id.listViewBad);

            Log.d("step", "step3");
            if (malosQR.length == 0) {
                malosQR = new String[1];
                malosQR[0] = "Todos los folios fueron permitidos";
            }

            ArrayAdapter<String> LABad = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, malosQR);
            lbad.setAdapter(LABad);

            Log.d("step", "step4");
            try {
                MyListAdapterGood LAGood = new MyListAdapterGood(folios, buenos);
                lgood.setAdapter(LAGood);
            } catch (Exception e) {
                Log.e("Error1:", e.getMessage());
            }


            Log.d("step1", "step5");
            //floating buttons
            FloatingActionButton fabConfirm = (FloatingActionButton) dialog.findViewById(R.id.fabConfirmSend);
            FloatingActionButton fabCancel = (FloatingActionButton) dialog.findViewById(R.id.fabCancelSend);

            if (folios.length == 0)
                fabConfirm.setVisibility(View.VISIBLE);

            Log.d("step1", "step6");
            alreadyConfirmed = 1;
            dialog.show();

            dialog.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alreadyConfirmed = 0;

                    if (dataOut.length() > 0) {
                        nombreCampoPermitido = Arrays.copyOf(nombreCampoPermitido, nombreCampoPermitido.length - 1);
                        tipoDatoCampoPermitido = Arrays.copyOf(tipoDatoCampoPermitido, tipoDatoCampoPermitido.length - 1);
                    }
                }
            });

            fabConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*boolean isNumber = true;

                    try{
                        Float.parseFloat(etData.getText().toString());
                    } catch (NumberFormatException ex){
                        isNumber= false;
                    }

                    if(isNumber)
                        Toast.makeText(nContext, "OK 4", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(nContext, "ERROR 4", Toast.LENGTH_LONG).show();*/

                    InsertarFoliosMap(Action);
                }
            });

            if (!(buenos.length() > 0))
                fabConfirm.setVisibility(View.INVISIBLE);
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }
    }



    public static void InsertarPallets(int Action){


        jsonFoliosInfo="[";
        for(int i=0; i<pallets.length; i++){

            String datosXML ="<datos type=\'1\'>";

            if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {

                for (int l = 0; l < dataToSend[i].length; l++) {
                    if (dataToSend[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo nombre=\'" + nombreCampoPermitido[l] + "\' value=\'" + dataToSend[i][l] + "\'></campo>";
                }

            }

            datosXML += "</datos>";


            //Construyo el json que mandare al storedProcedure
            jsonFoliosInfo+="{\"folio\":\""+pallets[i]+"\""+
                    ",\"XMLData\":\""+datosXML+"\""+
                    ",\"pX\":\""+positions[0]+"\""+
                    ",\"pY\":\""+positions[1]+"\""+
                    ",\"pZ\":\""+positions[2]+"\""+
                    "},";


        }

        jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
        jsonFoliosInfo += "]";
        Log.d("jsonFoliosInfo",jsonFoliosInfo);

        Log.d("idLocation", idLocation);

        try {
            if(Action == 1) {
                new confirmedPallets(config.rutaWebServerOmar + "/insertPallets", idLocation, jsonFoliosInfo).execute();
            }
            else{
                new confirmedFoliosDisponibles(config.rutaWebServerOmar + "/insertPalletsDisponibles", idProductLogSelected, idLocation, jsonFoliosInfo).execute();
            }
        }catch(Exception e){
            Toast.makeText(nContext, "Something went Wrong when try to insert confirmed Pallets", Toast.LENGTH_LONG).show();
        }
    }

    public static void InsertarPalletsMap(int Action){


        jsonFoliosInfo="[";
        for(int i=0; i<pallets.length; i++){

            String datosXML ="<datos type=\'1\'>";

            if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {

                for (int l = 0; l < dataToSend[i].length; l++) {
                    if (dataToSend[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo nombre=\'" + nombreCampoPermitido[l] + "\' value=\'" + dataToSend[i][l] + "\'></campo>";
                }

            }

            datosXML += "</datos>";


            //Construyo el json que mandare al storedProcedure
            jsonFoliosInfo+="{\"folio\":\""+pallets[i]+"\""+
                    ",\"XMLData\":\""+datosXML+"\""+
                    ",\"pX\":\""+positions[0]+"\""+
                    ",\"pY\":\""+positions[1]+"\""+
                    ",\"pZ\":\""+positions[2]+"\""+
                    "},";


        }

        jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
        jsonFoliosInfo += "]";
        Log.d("jsonFoliosInfo",jsonFoliosInfo);

        try {
            if(Action == 1) {
                new confirmedPalletsMap(config.rutaWebServerOmar + "/insertPallets", idLocation, jsonFoliosInfo).execute();
            }
            else{
                new confirmedFoliosDisponiblesMap(config.rutaWebServerOmar + "/insertPalletsDisponibles", idProductLogSelected, idLocation, jsonFoliosInfo).execute();
            }
        }catch(Exception e){
            Toast.makeText(nContext, "Something went Wrong when try to insert confirmed Pallets", Toast.LENGTH_LONG).show();
        }
    }

    public static void InsertarFolios(int Action){

        for(int p = 0; p<itemsMerma.size(); p++){
            HashMap<String, String>  posMerm = itemsMerma.get(p);

            Log.d("itemsMerma(" + p + ")", posMerm.get("posMerma"));

        }

        //Primero compara las cajas que se recibieron
        //con las que realmente llegaron de cosecha
        // incluyendo la merma para enviar el email
        jsonFoliosInfo="[";
        String mensaje="";
        for(int i=0; i<folios.length; i++){

            Log.d("folio", folios[i]);
            //Si las cajas de cosecha no es igual a la suma de la merma con las cajas recibidas en tunel manda email
/*            int cajasTotalesRecibidas = (Integer.parseInt(cajasRealesRecibidas[i]) + Integer.parseInt(cantidadMerma[i]));
            if(Integer.parseInt(cajasCosecha[i]) != cajasTotalesRecibidas ) {
                Log.d("MandarEmail", "folio: " + folios[i] + ". CajasCosecha:" + cajasCosecha[i] + ". CajasRecibidas:" + cajasTotalesRecibidas);
                mensaje += "Discrepancia en:\n Folio: "+folios[i]+
                        " \n Cajas desde cosecha: "+cajasCosecha[i]+
                        " \n Cajas recibidas: "+cajasRealesRecibidas[i]+
                        " \n Cajas mermadas: "+cantidadMerma[i]+
                        " \n Cajas Totales recibidas en tunel: "+cajasTotalesRecibidas+"\n\n\n";


            }*/

            String datosXML ="<datos type=\'1\'>";

            if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {

                for (int l = 0; l < dataToSend[i].length; l++) {
                    if (dataToSend[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo nombre=\'" + nombreCampoPermitido[l] + "\' value=\'" + dataToSend[i][l] + "\'></campo>";
                }

                if (idTypeLocation == 6) {
                    String[] locacionLine = idLocation.split("-");
                    String firstFolioFIFO="";
                    try {
                        JSONObject row = foliosFIFO.getJSONObject(0);
                        firstFolioFIFO = row.getString("vFolio");
                    }catch(Exception ex){
                        Log.e("Error", ex.getMessage());
                    }

                    datosXML += "<campo nombre=\'idLinea\' value=\'" + locacionLine[1] + "\'></campo>";
                    datosXML += "<campo nombre=\'FIFOSugerido\' value=\'" + firstFolioFIFO + "\'></campo>";
                    datosXML += "<campo nombre=\'sku\' value=\'" + SKU + "\'></campo>";
                }
            }

            datosXML += "</datos>";


            //Construyo el json que mandare al storedProcedure
            jsonFoliosInfo+="{\"folio\":\""+folios[i]+"\""+
                            ",\"XMLData\":\""+datosXML+"\""+
                            ",\"pX\":\""+positions[0]+"\""+
                            ",\"pY\":\""+positions[1]+"\""+
                            ",\"pZ\":\""+positions[2]+"\""+
                            "},";


            //posMerm.put("posMerma", i+"");

            HashMap<String, String>  posMerma = new HashMap<>();
            posMerma.put("posMerma", i + "");


            if(itemsMerma.contains(posMerma)){
                //jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
                datosXML = "<datos type=\'2\'>";

                for(int l=0; l<dataToSendMerma[i].length; l++) {
                    if(dataToSendMerma[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo  nombre=\'" + nombreCampoMerma[l] + "\' value=\'" + dataToSendMerma[i][l] + "\'></campo>";
                }




                datosXML += "</datos>";

                jsonFoliosInfo+="{\"folio\":\""+folios[i]+"\""+
                        ",\"XMLData\":\""+datosXML+"\""+
                        ",\"pX\":\""+positions[0]+"\""+
                        ",\"pY\":\""+positions[1]+"\""+
                        ",\"pZ\":\""+positions[2]+"\""+
                        "},";
            }
        }
        //Mando email con todas las discrepancias que ubo.
       // if(mensaje.compareToIgnoreCase("") != 0)
           // sendEmail(mensaje);

        jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
        jsonFoliosInfo += "]";
        Log.d("jsonFoliosInfo",jsonFoliosInfo);

        try {
            if(Action == 1) {
                if(embalajeType == 2){
                    new confirmedFolios(config.rutaWebServerOmar + "/insertPallets", idLocation, jsonFoliosInfo).execute();
                }else {
                    new confirmedFolios(config.rutaWebServerOmar + "/insertFolios", idLocation, jsonFoliosInfo).execute();
                }
            }
            else{
                //Toast.makeText(nContext, "Ya casi lo terminas javier!!!:"+ idProductLogSelected, Toast.LENGTH_LONG).show();
                new confirmedFoliosDisponibles(config.rutaWebServerOmar + "/insertFoliosDisponibles", idProductLogSelected, idLocation, jsonFoliosInfo).execute();
            }
        }catch(Exception e){
            Toast.makeText(nContext, "Something went Wrong when try to insert confirmed Folios", Toast.LENGTH_LONG).show();
        }
    }

    public static void InsertarFoliosMap(int Action){

        for(int p = 0; p<itemsMerma.size(); p++){
            HashMap<String, String>  posMerm = itemsMerma.get(p);

            Log.d("itemsMerma(" + p + ")", posMerm.get("posMerma"));

        }

        //Primero compara las cajas que se recibieron
        //con las que realmente llegaron de cosecha
        // incluyendo la merma para enviar el email
        jsonFoliosInfo="[";
        String mensaje="";
        for(int i=0; i<folios.length; i++){

            Log.d("folio", folios[i]);
            //Si las cajas de cosecha no es igual a la suma de la merma con las cajas recibidas en tunel manda email
/*            int cajasTotalesRecibidas = (Integer.parseInt(cajasRealesRecibidas[i]) + Integer.parseInt(cantidadMerma[i]));
            if(Integer.parseInt(cajasCosecha[i]) != cajasTotalesRecibidas ) {
                Log.d("MandarEmail", "folio: " + folios[i] + ". CajasCosecha:" + cajasCosecha[i] + ". CajasRecibidas:" + cajasTotalesRecibidas);
                mensaje += "Discrepancia en:\n Folio: "+folios[i]+
                        " \n Cajas desde cosecha: "+cajasCosecha[i]+
                        " \n Cajas recibidas: "+cajasRealesRecibidas[i]+
                        " \n Cajas mermadas: "+cantidadMerma[i]+
                        " \n Cajas Totales recibidas en tunel: "+cajasTotalesRecibidas+"\n\n\n";


            }*/

            String datosXML ="<datos type=\'1\'>";

            if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {

                for (int l = 0; l < dataToSend[i].length; l++) {
                    if (dataToSend[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo nombre=\'" + nombreCampoPermitido[l] + "\' value=\'" + dataToSend[i][l] + "\'></campo>";
                }

                if (idTypeLocation == 6) {
                    String[] locacionLine = idLocation.split("-");
                    String firstFolioFIFO="";
                    try {
                        JSONObject row = foliosFIFO.getJSONObject(0);
                        firstFolioFIFO = row.getString("vFolio");
                    }catch(Exception ex){
                        Log.e("Error", ex.getMessage());
                    }

                    datosXML += "<campo nombre=\'idLinea\' value=\'" + locacionLine[1] + "\'></campo>";
                    datosXML += "<campo nombre=\'FIFOSugerido\' value=\'" + firstFolioFIFO + "\'></campo>";
                    datosXML += "<campo nombre=\'sku\' value=\'" + SKU + "\'></campo>";
                }
            }

            datosXML += "</datos>";


            //Construyo el json que mandare al storedProcedure
            jsonFoliosInfo+="{\"folio\":\""+folios[i]+"\""+
                    ",\"XMLData\":\""+datosXML+"\""+
                    ",\"pX\":\""+positions[0]+"\""+
                    ",\"pY\":\""+positions[1]+"\""+
                    ",\"pZ\":\""+positions[2]+"\""+
                    "},";


            //posMerm.put("posMerma", i+"");

            HashMap<String, String>  posMerma = new HashMap<>();
            posMerma.put("posMerma", i + "");


            if(itemsMerma.contains(posMerma)){
                //jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
                datosXML = "<datos type=\'2\'>";

                for(int l=0; l<dataToSendMerma[i].length; l++) {
                    if(dataToSendMerma[i][l].compareToIgnoreCase("") == 0) {
                        Toast.makeText(nContext, "Debes llenar todos los campos para continuar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    datosXML += "<campo  nombre=\'" + nombreCampoMerma[l] + "\' value=\'" + dataToSendMerma[i][l] + "\'></campo>";
                }




                datosXML += "</datos>";

                jsonFoliosInfo+="{\"folio\":\""+folios[i]+"\""+
                        ",\"XMLData\":\""+datosXML+"\""+
                        ",\"pX\":\""+positions[0]+"\""+
                        ",\"pY\":\""+positions[1]+"\""+
                        ",\"pZ\":\""+positions[2]+"\""+
                        "},";
            }
        }
        //Mando email con todas las discrepancias que ubo.
        // if(mensaje.compareToIgnoreCase("") != 0)
        // sendEmail(mensaje);

        jsonFoliosInfo = jsonFoliosInfo.substring(0, jsonFoliosInfo.length() - 1);
        jsonFoliosInfo += "]";
        Log.d("jsonFoliosInfo",jsonFoliosInfo);

        try {
            if(Action == 1) {
                if(embalajeType == 2){
                    new confirmedFoliosMap(config.rutaWebServerOmar + "/insertPallets", idLocation, jsonFoliosInfo).execute();
                }else {
                    new confirmedFoliosMap(config.rutaWebServerOmar + "/insertFolios", idLocation, jsonFoliosInfo).execute();
                }
            }
            else{
                //Toast.makeText(nContext, "Ya casi lo terminas javier!!!:"+ idProductLogSelected, Toast.LENGTH_LONG).show();
                new confirmedFoliosDisponiblesMap(config.rutaWebServerOmar + "/insertFoliosDisponibles", idProductLogSelected, idLocation, jsonFoliosInfo).execute();
            }
        }catch(Exception e){
            Toast.makeText(nContext, "Something went Wrong when try to insert confirmed Folios", Toast.LENGTH_LONG).show();
        }
    }



    public static void sendEmail(String datos) {
        Log.i("Send email", "");
        String[] TO = {"jcalderon@naturesweet.com"};
        String[] CC = {"ocastillo@naturesweet.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Discrepancia en cajas recibidas");
        emailIntent.putExtra(Intent.EXTRA_TEXT, datos);

        try {
            nContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
           // nContext.finish();
            Log.i("Finished", "Finishing sendind email");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(nContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public static class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String msg;

        public RetreiveFeedTask(String msg){
            pDialog = new ProgressDialog(nContext);
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Cargando...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            this.msg = msg;
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("jcalderon@naturesweet.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("desarrolloteam@naturesweet.com.mx"));
                message.setSubject("Discrepancia en cajas");
                message.setContent(msg, "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                pDialog.dismiss();
            }catch(Exception e){
                Log.e("Error",e.getMessage());
            }
            Toast.makeText(nContext, "Message sent", Toast.LENGTH_LONG).show();
        }
    }



    private static class confirmedFoliosDisponibles extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed, idProductLog;
        ProgressDialog pd;

        public confirmedFoliosDisponibles(String url, String idProductLog, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
            this.idProductLog = idProductLog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", idProductLog));
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            if (idTypeLocation != 6) {

                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if (confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for (int i = 0; i < confirmed.length(); i++) {
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String> folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);
                            myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:", "llego");
                    if (noPermits.length() == 0) {
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for (int i = 0; i < noPermits.length(); i++) {
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i] = foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                            MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Embalaje no permitidos");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        } catch (Exception e) {
                            Log.e("Error -- >", e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:", e.getMessage());
                }
            }else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);
                            myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los Embalajes han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
        }
    }

    private static class confirmedFoliosDisponiblesMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed, idProductLog;
        ProgressDialog pd;

        public confirmedFoliosDisponiblesMap(String url, String idProductLog, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
            this.idProductLog = idProductLog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idProductLog", idProductLog));
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
            String idLocation = sharedpreferences.getString("iLocation","-1");

            if (idTypeLocation != 6) {

                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if (confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for (int i = 0; i < confirmed.length(); i++) {
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String> folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);
                            //myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:", "llego");
                    if (noPermits.length() == 0) {
                        dialog.dismiss();
                       // setView();
                        Mapeo.drawLocation(idLocation);
                        Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for (int i = 0; i < noPermits.length(); i++) {
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i] = foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                            MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Embalaje no permitidos");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        } catch (Exception e) {
                            Log.e("Error -- >", e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:", e.getMessage());
                }
            }else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);
                           // myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                       // setView();

                        Mapeo.drawLocation(idLocation);
                        Toast.makeText(nContext,"Los Embalajes han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
        }
    }



    private static class confirmedPallets extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed;
        ProgressDialog pd;

        public confirmedPallets(String url, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            if (idTypeLocation != 6) {
                try{


                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");



                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("Pallet"));
                            items.remove(folioMap);
                            myListAdapter.notifyDataSetChanged();
                        }

                    }
                    try {
                        JSONArray noPermits = json.getJSONArray("table2");
                        Log.d("step1:", "llego");
                        if (noPermits.length() == 0) {
                            dialog.dismiss();
                            setView();
                            Toast.makeText(nContext, "Los Pallets han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                            PopUp pop = new PopUp(nContext, "Los Pallets han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                            pop.showPopUp();


                        } else {

                            JSONObject foliosNotPermit;
                            String folios[] = new String[noPermits.length()];
                            // String sku[] = new String[noPermits.length()];

                            for (int i = 0; i < noPermits.length(); i++) {
                                foliosNotPermit = noPermits.getJSONObject(i);
                                folios[i] = foliosNotPermit.getString("Pallet");
                                //sku[i] = foliosNotPermit.getString("sku");
                            }
                            try {

                                LayoutInflater inflater = nContext.getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                                MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                                ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                                listFoliosNotPermit1.setAdapter(adapterA);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                                alertDialog.setTitle("Pallets no permitidos");
                                // .setMessage("Are you sure you want to delete this entry?")
                                alertDialog.setView(dialoglayout)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setIcon(R.drawable.alerticon);
                                alertDialog.setCancelable(false);
                                alertDialog.create();
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e("Error -- >", e.getMessage());
                            }
                        }
                    }catch(Exception e){
                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext, "Los Pallets han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                        PopUp pop = new PopUp(nContext, "Los Pallets han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                        pop.showPopUp();
                    }


                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error", e.getMessage());

                    PopUp pop = new PopUp(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", PopUp.POPUP_INCORRECT);
                    pop.showPopUp();
                }

            }
            else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("Folio"));
                            items.remove(folioMap);
                            myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
        }
    }

    private static class confirmedPalletsMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed;
        ProgressDialog pd;

        public confirmedPalletsMap(String url, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }


            SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
            String idLocation = sharedpreferences.getString("iLocation","-1");

            if (idTypeLocation != 6) {
                try{


                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");



                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("Pallet"));
                            items.remove(folioMap);
                         //   myListAdapter.notifyDataSetChanged();
                        }

                    }
                    try {
                        JSONArray noPermits = json.getJSONArray("table2");
                        Log.d("step1:", "llego");
                        if (noPermits.length() == 0) {
                            dialog.dismiss();
                           // setView();
                            Mapeo.drawLocation(idLocation);
                            Toast.makeText(nContext, "Los Pallets han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                            PopUp pop = new PopUp(nContext, "Los Pallets han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                            pop.showPopUp();


                        } else {

                            JSONObject foliosNotPermit;
                            String folios[] = new String[noPermits.length()];
                            // String sku[] = new String[noPermits.length()];

                            for (int i = 0; i < noPermits.length(); i++) {
                                foliosNotPermit = noPermits.getJSONObject(i);
                                folios[i] = foliosNotPermit.getString("Pallet");
                                //sku[i] = foliosNotPermit.getString("sku");
                            }
                            try {

                                LayoutInflater inflater = nContext.getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                                MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                                ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                                listFoliosNotPermit1.setAdapter(adapterA);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                                alertDialog.setTitle("Pallets no permitidos");
                                // .setMessage("Are you sure you want to delete this entry?")
                                alertDialog.setView(dialoglayout)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setIcon(R.drawable.alerticon);
                                alertDialog.setCancelable(false);
                                alertDialog.create();
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e("Error -- >", e.getMessage());
                            }
                        }
                    }catch(Exception e){
                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        //setView();
                        Mapeo.drawLocation(idLocation);
                        Toast.makeText(nContext, "Los Pallets han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                        PopUp pop = new PopUp(nContext, "Los Pallets han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                        pop.showPopUp();
                    }


                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error", e.getMessage());

                    PopUp pop = new PopUp(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", PopUp.POPUP_INCORRECT);
                    pop.showPopUp();
                }

            }
            else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("Folio"));
                            items.remove(folioMap);
                            //myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
        }
    }

    private static class confirmedFolios extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed;
        ProgressDialog pd;

        public confirmedFolios(String url, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            if (idTypeLocation != 6) {
                try{
                    /*JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");

                    if(confirmed.length() > 0){
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }*/


                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");



                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);

                            if(myListAdapter != null)
                                myListAdapter.notifyDataSetChanged();
                        }

                    }
                    try {
                        JSONArray noPermits = json.getJSONArray("table2");
                        Log.d("step1:", "llego");
                        if (noPermits.length() == 0) {
                            dialog.dismiss();
                            setView();
                            Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                            PopUp pop = new PopUp(nContext,"Los folios han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                            pop.showPopUp();
                        } else {

                            JSONObject foliosNotPermit;
                            String folios[] = new String[noPermits.length()];
                            // String sku[] = new String[noPermits.length()];

                            for (int i = 0; i < noPermits.length(); i++) {
                                foliosNotPermit = noPermits.getJSONObject(i);
                                folios[i] = foliosNotPermit.getString("vFolio");
                                //sku[i] = foliosNotPermit.getString("sku");
                            }
                            try {

                                LayoutInflater inflater = nContext.getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                                MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                                ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                                listFoliosNotPermit1.setAdapter(adapterA);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                                alertDialog.setTitle("Folios no permitidos");
                                // .setMessage("Are you sure you want to delete this entry?")
                                alertDialog.setView(dialoglayout)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setIcon(R.drawable.alerticon);
                                alertDialog.setCancelable(false);
                                alertDialog.create();
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e("Error -- >", e.getMessage());
                            }
                        }
                    }catch(Exception e){
                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                        PopUp pop = new PopUp(nContext,"Los folios han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                        pop.showPopUp();
                    }


                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error",e.getMessage());

                    PopUp pop = new PopUp(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", PopUp.POPUP_INCORRECT);
                    pop.showPopUp();
                }

            }
            else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);

                            if(myListAdapter != null)
                                myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                       // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                                   // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
        }
    }

    private static class confirmedFoliosMap extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String loca, foliosConfirmed;
        ProgressDialog pd;

        public confirmedFoliosMap(String url, String loca, String foliosConfirmed) {
            this.url = url;
            this.loca = loca;
            this.foliosConfirmed = foliosConfirmed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext){
                @Override
                public void onBackPressed() {
                    //super.onBackPressed();
                }
            };
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", loca));
                params.add(new BasicNameValuePair("json", foliosConfirmed));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }
            SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
            String idLocation = sharedpreferences.getString("iLocation","-1");

            if (idTypeLocation != 6) {
                try{
                    /*JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");

                    if(confirmed.length() > 0){
                        dialog.dismiss();
                        setView();
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }*/


                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");



                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);

                           /// if(myListAdapter != null)
                           //     myListAdapter.notifyDataSetChanged();
                        }

                    }
                    try {
                        JSONArray noPermits = json.getJSONArray("table2");
                        Log.d("step1:", "llego");
                        if (noPermits.length() == 0) {
                            dialog.dismiss();
                           // setView();


                            Mapeo.drawLocation(idLocation);
                            Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                            PopUp pop = new PopUp(nContext,"Los folios han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                            pop.showPopUp();
                        } else {

                            JSONObject foliosNotPermit;
                            String folios[] = new String[noPermits.length()];
                            // String sku[] = new String[noPermits.length()];

                            for (int i = 0; i < noPermits.length(); i++) {
                                foliosNotPermit = noPermits.getJSONObject(i);
                                folios[i] = foliosNotPermit.getString("vFolio");
                                //sku[i] = foliosNotPermit.getString("sku");
                            }
                            try {

                                LayoutInflater inflater = nContext.getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.viewfoliosnopermitlocation, null);
                                MyAdapterNotPermitLocation adapterA = new MyAdapterNotPermitLocation(nContext, folios, noPermits);
                                ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermit);
                                listFoliosNotPermit1.setAdapter(adapterA);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                                alertDialog.setTitle("Folios no permitidos");
                                // .setMessage("Are you sure you want to delete this entry?")
                                alertDialog.setView(dialoglayout)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setIcon(R.drawable.alerticon);
                                alertDialog.setCancelable(false);
                                alertDialog.create();
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e("Error -- >", e.getMessage());
                            }
                        }
                    }catch(Exception e){
                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        //setView();
                        Mapeo.drawLocation(idLocation);
                        Toast.makeText(nContext, "Los folios han sido insertados en la locación exitosamente", Toast.LENGTH_LONG).show();

                        PopUp pop = new PopUp(nContext,"Los folios han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
                        pop.showPopUp();
                    }


                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error", e.getMessage());

                    PopUp pop = new PopUp(nContext,"Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", PopUp.POPUP_INCORRECT);
                    pop.showPopUp();
                }

            }
            else{
                try{
                    JSONObject json = new JSONObject(res);
                    JSONArray confirmed = json.getJSONArray("table1");
                    JSONArray noPermits = json.getJSONArray("table2");

                    if(confirmed.length() > 0) {
                        JSONObject foliosConfirmed[] = new JSONObject[confirmed.length()];
                        for(int i = 0; i<confirmed.length(); i++){
                            foliosConfirmed[i] = confirmed.getJSONObject(i);

                            HashMap<String, String>  folioMap = new HashMap<String, String>();
                            folioMap.put("embalaje", foliosConfirmed[i].getString("vFolio"));
                            items.remove(folioMap);

                           // if(myListAdapter != null)
                            ///    myListAdapter.notifyDataSetChanged();
                        }

                    }
                    Log.d("step1:","llego");
                    if(noPermits.length() == 0) {
                        dialog.dismiss();
                       // setView();
                        Mapeo.drawLocation(idLocation);
                        Toast.makeText(nContext,"Los folios han sido insertados en la locación exitosamente",Toast.LENGTH_LONG).show();
                    }
                    else{

                        JSONObject foliosNotPermit;
                        String folios[] = new String[noPermits.length()];
                        // String sku[] = new String[noPermits.length()];

                        for(int i = 0; i<noPermits.length(); i++){
                            foliosNotPermit = noPermits.getJSONObject(i);
                            folios[i]=foliosNotPermit.getString("vFolio");
                            //sku[i] = foliosNotPermit.getString("sku");
                        }
                        try {

                            LayoutInflater inflater = nContext.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.viewdialogline, null);
                            MyaAdapterNotPermit adapterA = new MyaAdapterNotPermit(nContext, folios, noPermits);
                            ListView listFoliosNotPermit1 = (ListView) dialoglayout.findViewById(R.id.listViewFoliosNotPermitinLine);
                            listFoliosNotPermit1.setAdapter(adapterA);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
                            alertDialog.setTitle("Folios no permitidos en la linea");
                            // .setMessage("Are you sure you want to delete this entry?")
                            alertDialog.setView(dialoglayout)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })*/
                            alertDialog.setIcon(R.drawable.alerticon);
                            alertDialog.setCancelable(false);
                            alertDialog.create();
                            alertDialog.show();
                        }catch(Exception e){
                            Log.e("Error -- >",e.getMessage());
                        }
                    }

                }catch(Exception e){
                    Toast.makeText(nContext, "Error al insertar los datos. Asegurese de estar conectado a internet o contacte al administrador de la aplicación.", Toast.LENGTH_LONG).show();
                    Log.e("Error:",e.getMessage());
                }
            }
            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
        }
    }



    private static class sendQRCodeLocationToServer extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String QRCode, accion;
        private Activity nContext;
        ProgressDialog pd;

        public sendQRCodeLocationToServer(String url, String accion, String QRCode, Activity nContext) {
            this.url = url;
            this.accion = accion;
            this.QRCode = QRCode;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("accion", accion));
                params.add(new BasicNameValuePair("QRCode", QRCode));
                params.add(new BasicNameValuePair("pX", positions[0]));
                params.add(new BasicNameValuePair("pY", positions[1]));
                params.add(new BasicNameValuePair("pZ", positions[2]));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("CodigoLocacion", this.QRCode+" -- > hola");
            Log.d("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {

                JSONObject INFOWH = new JSONObject(res);
                JSONArray wareHouse = INFOWH.getJSONArray("table1");
                datosPermitidos = INFOWH.getJSONArray("table2");
                JSONArray exists = INFOWH.getJSONArray("table4");
                JSONArray container = INFOWH.getJSONArray("table5");

                //In case line
                JSONArray linea = INFOWH.getJSONArray("table6");
                JSONArray SKULines =  INFOWH.getJSONArray("table7");

                if(wareHouse.length() > 0){
                    rowDatosLocat = wareHouse.getJSONObject(0);

                    lineInfoView.setVisibility(View.INVISIBLE);
                    /*idLocat.setText(rowDatosLocat.getString("nameLocation"));
                    tipoLocat.setText(rowDatosLocat.getString("nameTypeLocation"));
                    contentLocat.setText(rowDatosLocat.getString("nameContainer"));*/



                    if(Integer.parseInt(rowDatosLocat.getString("width")) > 0 &&
                        Integer.parseInt(rowDatosLocat.getString("height")) > 0 &&
                            Integer.parseInt(rowDatosLocat.getString("lenght")) > 0) {//Si tiene tamaño entonces no permite muchos folios
                        allowSeveralRegisters = false;

                        //verificamos que X Y Z esten dentro del rango del tamaño de la locacion
                            if( (
                                    !(Integer.parseInt(positions[0]) > 0) ||
                                    !(Integer.parseInt(positions[0]) <= Integer.parseInt(rowDatosLocat.getString("width")))
                                )
                                    ||
                                (
                                        !(Integer.parseInt(positions[2]) > 0) ||
                                                !(Integer.parseInt(positions[2]) <= Integer.parseInt(rowDatosLocat.getString("height")))
                                )
                                    ||
                                (
                                        !(Integer.parseInt(positions[1]) > 0) ||
                                                !(Integer.parseInt(positions[1]) <= Integer.parseInt(rowDatosLocat.getString("lenght")))
                                )
                            ) {
                                Toast.makeText(nContext, "Las coordenadas esta fuera del rango de la locación", Toast.LENGTH_LONG).show();
                                et_locatCode.setText("");
                                viewInfoLocation.setVisibility(View.INVISIBLE);
                                letAdd = 0;

                                //fabAdd.setVisibility(View.INVISIBLE);
                                idLocation = "-1";
                                //readLoc = 1;

                                items.clear();
                                myListAdapter.notifyDataSetChanged();
                                //viewInfoLocation.setVisibility(View.VISIBLE);
                                return;
                            }

                    }
                    else
                        allowSeveralRegisters = true;

                    idTypeLocation = Integer.parseInt(rowDatosLocat.getString("idTypeLocation"));

                    idFarmLocation = rowDatosLocat.getString("id_Farm");

                    if(idTypeLocation != 1)
                        allowSeveralRegisters = false;
                    else {
                        allowSeveralRegisters = true;

                        BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();
                        String[][] datos = bd.getOnHoldReasonsForFarm(idFarmLocation);
                        bd.cerrar();

                        mermaReasonsID = new String[datos.length];
                        mermaReasonsName= new String[datos.length];

                        for(int i=0; i<datos.length; i++){
                            mermaReasonsID[i] = datos[i][0];
                            mermaReasonsName[i] = datos[i][1];
                        }

                    }

                    if(idTypeLocation == 6){//Si es linea toma estos catalogos
                        BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();
                        String datosAlimentador[][] = bd.getUserExtraRole(idFarmLocation);
                        bd.cerrar();

                        if(datosAlimentador.length > 0){
                            alimentadorID = new String[datosAlimentador.length];
                            alimentadorName = new String[datosAlimentador.length];

                            for(int i=0; i<datosAlimentador.length; i++){
                                alimentadorID[i] = datosAlimentador[i][0];
                                alimentadorName[i] = datosAlimentador[i][1];
                            }
                        }
                    }

                    viewInfoLocation.setVisibility(View.VISIBLE);

                   /* idLocat.setText(
                            "Código QR: \t "+rowDatosLocat.getString("idQR")+"\n"+
                                    "Nombre de Locación: \t "+rowDatosLocat.getString("nameLocation")+"\n"+
                                    "Tipo de locación: \t "+rowDatosLocat.getString("nameTypeLocation")+"\n"+
                                    "Farm: \t "+ rowDatosLocat.getString("Farm")+"\n"+
                                    "width: \t"+ rowDatosLocat.getString("width")+"\n"+
                                    "height: \t"+ rowDatosLocat.getString("height")+"\n"+
                                    "lenght: \t"+ rowDatosLocat.getString("lenght")
                    );*/
                    //qrCode, nameLocat, locatType, farm, width, height, lenght;

                    qrCode.setText(rowDatosLocat.getString("idQR"));
                    nameLocat.setText(rowDatosLocat.getString("nameLocation"));
                    locatType.setText(rowDatosLocat.getString("nameTypeLocation"));
                    farm.setText(rowDatosLocat.getString("Farm"));
                    width.setText(rowDatosLocat.getString("width"));
                    height.setText(rowDatosLocat.getString("height"));
                    lenght.setText(rowDatosLocat.getString("lenght"));



                /*    contentLocat.setText(
                            "Nombre: "+rowDatosLocat.getString("nameContainer")+"\n"+
                            "Formas: "+rowDatosLocat.getString("formas")+"\n"+
                            "Cases: "+rowDatosLocat.getString("cases")+"\n"+
                            "Pallets: "+rowDatosLocat.getString("pallets")+"\n"
                    );*/

                    itemsEmbalaje = new ArrayList<HashMap<String,String>>();

                    if(container.length() > 0){
                        String dato="";
                        JSONObject cont;
                        for(int i=0; i<container.length(); i++){
                            cont = container.getJSONObject(i);
                            dato += cont.getString("NombreEmbalaje")+"\n";

                            HashMap<String, String>  itemEmb = new HashMap<String, String>();
                            itemEmb.put("idEmbalaje", cont.getString("idEmbalaje"));

                            itemsEmbalaje.add(itemEmb);
                        }

                        contentLocat.setText(dato);
                    }else{
                        contentLocat.setText("");
                    }

                    verifyAllEmbalajes();

                    String posicionesString="";
                    posicionesString+="X --> "+ positions[0]+"\n";
                    posicionesString+="Y --> "+ positions[1]+"\n";
                    posicionesString+="Z --> "+ positions[2]+"\n";

                    posiciones.setText(posicionesString);

                    if(datosPermitidos.length() > 0){
                        String datos="";
                        JSONObject rowDatosPermitidos;
                        nombreCampoPermitido = new String[datosPermitidos.length()];
                        tipoDatoCampoPermitido = new String[datosPermitidos.length()];

                        camposCatalogID = new String[datosPermitidos.length()][];
                        camposCatalogName = new String[datosPermitidos.length()][];

                        int sizeCatalogs=0;
                        for(int i=0; i<datosPermitidos.length(); i++) {
                            rowDatosPermitidos = datosPermitidos.getJSONObject(i);
                            if(rowDatosPermitidos.getString("tipoDato").contains("catalog")) {
                                String[] catalogosT = rowDatosPermitidos.getString("tipoDato").split(":");
                                if (catalogosT.length == 4)
                                    sizeCatalogs++;
                            }
                        }

                        camposCatalogID = new String[sizeCatalogs][];
                        camposCatalogName = new String[sizeCatalogs][];

                        for(int i =0, contCatalogs=0; i<datosPermitidos.length(); i++){
                            rowDatosPermitidos = datosPermitidos.getJSONObject(i);
                            datos += rowDatosPermitidos.getString("campos")+"\n";
                            nombreCampoPermitido[i] = rowDatosPermitidos.getString("campos");
                            tipoDatoCampoPermitido[i] = rowDatosPermitidos.getString("tipoDato");

                            if(rowDatosPermitidos.getString("tipoDato").contains("catalog")) {
                                String [] catalogos = rowDatosPermitidos.getString("tipoDato").split(":");
                                if(catalogos.length != 4)
                                    tipoDatoCampoPermitido[i] = "int";
                                else{

                                    BaseDatos bd = new BaseDatos(nContext);
                                    bd.abrir();
                                    String[][] data = bd.consultaTabla(catalogos[1], catalogos[2], catalogos[3]);
                                    bd.cerrar();
                                   // Log.d("datosTabla", data[0][1]);


                                    if(data.length>0) {
                                        camposCatalogID[contCatalogs] = new String[data.length];
                                        camposCatalogName[contCatalogs] = new String[data.length];

                                        for (int j = 0; j < data.length; j++) {
                                            camposCatalogID[contCatalogs][j] = data[j][0];
                                            camposCatalogName[contCatalogs][j] = data[j][1];
                                        }
                                        contCatalogs++;
                                    }
                                    else
                                        tipoDatoCampoPermitido[i] = "int";


                                }
                            }



                        }

                        tipoLocat.setText(datos);
                    }else{
                        tipoLocat.setText("");
                        nombreCampoPermitido = null;
                    }

                    idLocation = rowDatosLocat.getString("idQR");
                    conCatalogosVista = -1;

                    if(! (exists.length() > 0)) {
                        //letAdd = 1;

                        //fabAdd.setVisibility(View.VISIBLE);

                       // readLoc = 0;
                        et_locatCode.setText("");

                        Log.d("PosicionDesocupada", "true");

                        posiciones.setTextColor(nContext.getResources().getColor(R.color.black));
                        posiciones.setText(posiciones.getText() + "\n" + "Locación Disponible");
                        busyLocation = 0;
                    }else {
                        Toast.makeText(nContext, "Esta posición ya esta ocupada", Toast.LENGTH_SHORT).show();

                        posiciones.setTextColor(nContext.getResources().getColor(R.color.red));
                        posiciones.setText(posiciones.getText() + "\n" + "Locación ocupada");

                        items.clear();
                        myListAdapter.notifyDataSetChanged();
                        letAdd = 0;

                        busyLocation = 1;

                        //fabAdd.setVisibility(View.INVISIBLE);
                        fabSend.setVisibility(View.INVISIBLE);
                        //idLocation = "-1";
                       // readLoc = 1;
                    }

                    if(idTypeLocation == 6){//Si es linea
                        //Verificar que haya información de la linea leida
                        if(linea.length() > 0){
                            lineInfoView.setVisibility(View.VISIBLE);
                            JSONObject infoLine;
                            String nameLine="", idLine="";
                            for(int i = 0; i < linea.length(); i++){
                                infoLine = linea.getJSONObject(i);
                                nameLine = infoLine.getString("vNameLine");
                                idLine = infoLine.getString("id_LinePackage");
                            }
                            idLocation = rowDatosLocat.getString("idQR")+"-"+idLine;
                            lineName.setText(nameLine);

                            if(SKULines.length() > 0){
                                SKUinLine = new String[SKULines.length()];
                                JSONObject skuInfo;
                                String skuDisponibles="";
                                for(int i=0; i<SKULines.length(); i++){
                                    skuInfo = SKULines.getJSONObject(i);
                                    skuDisponibles+=skuInfo.getString("vSKU")+"\n";
                                    SKUinLine[i] = skuInfo.getString("vSKU");
                                }

                                skuLine.setText(skuDisponibles);

                                embalajes.removeAllViews();
                                embalajes.addView(viewIsALine);

                                fabAdd.setVisibility(fabAdd.INVISIBLE);

                               // fabAdd.setVisibility(View.INVISIBLE);

                            }else{
                                Toast.makeText(nContext,"Esta linea no fue encontrada", Toast.LENGTH_LONG).show();
                                posiciones.setTextColor(nContext.getResources().getColor(R.color.red));
                                posiciones.setText(posicionesString + "\n" + "Linea no encontrada");

                                items.clear();
                                myListAdapter.notifyDataSetChanged();
                                letAdd = 0;
                                itemsEmbalaje.clear();


                              //  fabAdd.setVisibility(View.INVISIBLE);
                                fabSend.setVisibility(View.INVISIBLE);

                                lineInfoView.setVisibility(View.INVISIBLE);
                                embalajes.removeAllViews();
                                embalajes.addView(viewNotALine);
                                //idLocation = "-1";
                               // readLoc = 1;
                            }

                        }else{
                            Toast.makeText(nContext,"Esta linea no fue encontrada", Toast.LENGTH_LONG).show();
                            posiciones.setTextColor(nContext.getResources().getColor(R.color.red));
                            posiciones.setText(posicionesString + "\n" + "Linea no encontrada");

                            items.clear();
                            myListAdapter.notifyDataSetChanged();
                            letAdd = 0;
                            skuLine.setText("");
                            itemsEmbalaje.clear();

                            // fabAdd.setVisibility(View.INVISIBLE);
                            fabSend.setVisibility(View.INVISIBLE);
                            //idLocation = "-1";
                            //readLoc = 1;
                        }



                    }else{//si no es linea
                        embalajes.removeAllViews();
                        embalajes.addView(viewNotALine);
                    }


                }else{
                    Toast.makeText(nContext, "El codigo leido no es una locación", Toast.LENGTH_LONG).show();
                    et_locatCode.setText("");
                    viewInfoLocation.setVisibility(View.INVISIBLE);
                   // letAdd = 0;
                    if(itemsEmbalaje != null)
                        itemsEmbalaje.clear();
                   // fabAdd.setVisibility(View.INVISIBLE);
                    idLocation = "-1";
                   // readLoc = 1;

                    items.clear();
                    myListAdapter.notifyDataSetChanged();
                    //et_locatCode.setText("");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error",e.getMessage());
                Toast.makeText(nContext, "No hay conexión al webservice. Revise su conexión a internet o contacte al administrador de la aplicación. ", Toast.LENGTH_LONG).show();
            }


            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/

        }
    }

    public static void putDatosPermitidos(JSONArray dataPermit){
        datosPermitidos = dataPermit;

        try {

            if (datosPermitidos.length() > 0) {
                String datos = "";
                JSONObject rowDatosPermitidos;
                nombreCampoPermitido = new String[datosPermitidos.length()];
                tipoDatoCampoPermitido = new String[datosPermitidos.length()];

                camposCatalogID = new String[datosPermitidos.length()][];
                camposCatalogName = new String[datosPermitidos.length()][];

                int sizeCatalogs = 0;
                for (int i = 0; i < datosPermitidos.length(); i++) {
                    rowDatosPermitidos = datosPermitidos.getJSONObject(i);
                    if (rowDatosPermitidos.getString("tipoDato").contains("catalog")) {
                        String[] catalogosT = rowDatosPermitidos.getString("tipoDato").split(":");
                        if (catalogosT.length == 4)
                            sizeCatalogs++;
                    }
                }

                camposCatalogID = new String[sizeCatalogs][];
                camposCatalogName = new String[sizeCatalogs][];

                for (int i = 0, contCatalogs = 0; i < datosPermitidos.length(); i++) {
                    rowDatosPermitidos = datosPermitidos.getJSONObject(i);
                    datos += rowDatosPermitidos.getString("campos") + "\n";
                    nombreCampoPermitido[i] = rowDatosPermitidos.getString("campos");
                    tipoDatoCampoPermitido[i] = rowDatosPermitidos.getString("tipoDato");

                    if (rowDatosPermitidos.getString("tipoDato").contains("catalog")) {
                        String[] catalogos = rowDatosPermitidos.getString("tipoDato").split(":");
                        if (catalogos.length != 4)
                            tipoDatoCampoPermitido[i] = "int";
                        else {

                            BaseDatos bd = new BaseDatos(nContext);
                            bd.abrir();
                            String[][] data = bd.consultaTabla(catalogos[1], catalogos[2], catalogos[3]);
                            bd.cerrar();
                            // Log.d("datosTabla", data[0][1]);


                            if (data.length > 0) {
                                camposCatalogID[contCatalogs] = new String[data.length];
                                camposCatalogName[contCatalogs] = new String[data.length];

                                for (int j = 0; j < data.length; j++) {
                                    camposCatalogID[contCatalogs][j] = data[j][0];
                                    camposCatalogName[contCatalogs][j] = data[j][1];
                                }
                                contCatalogs++;
                            } else
                                tipoDatoCampoPermitido[i] = "int";


                        }
                    }


                }

                //tipoLocat.setText(datos);
            } else {
               // tipoLocat.setText("");
                nombreCampoPermitido = null;
            }
        }catch(Exception ex){
            Toast.makeText(nContext, "Error", Toast.LENGTH_LONG).show();
        }

    }

    private static class MyListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String, String>> items;

        public MyListAdapter(Context context, ArrayList<HashMap<String, String>> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = nContext.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.viewtextview, null);
            TextView embalaje = (TextView) view.findViewById(R.id.txtEmbalaje);
            Button btnRemove = (Button) view.findViewById(R.id.btnRemove);

            HashMap<String, String> item = items.get(position);
            embalaje.setText(item.get("embalaje"));

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    myListAdapter.notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    private static class MyListAdapterGood extends BaseAdapter {

        private JSONArray buen;
        private String[] folios;

        public MyListAdapterGood(String[] folios, JSONArray buenos) {
            this.buen = buenos;
            this.folios = folios;
        }

        @Override
        public int getCount() {
            return folios.length;
        }

        @Override
        public Object getItem(int position) {
            return folios[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (embalajeType == 2) {

               /* ViewHolderPallet holder;

                if(convertView == null) {
                    LayoutInflater layoutInflater = nContext.getLayoutInflater();
                    convertView  = layoutInflater.inflate(R.layout.viewpalletgood, null);
                    holder = new ViewHolderPallet();


                    holder.txtPallet = (TextView) convertView.findViewById(R.id.pallet);
                    holder.txtPlant = (TextView) convertView.findViewById(R.id.plant);
                    holder.txtSku = (TextView) convertView.findViewById(R.id.sku);
                    holder.txtLocacion = (TextView) convertView.findViewById(R.id.locacion);
                    holder. txtxyz = (TextView) convertView.findViewById(R.id.xyz);
                    holder.txtCajasRe = (TextView) convertView.findViewById(R.id.cajas);

                    holder.gvDatos = (MyGridView) convertView.findViewById(R.id.gridViewCamposXML);

                    convertView.setTag(holder);


                }else{
                    holder = (ViewHolderPallet) convertView.getTag();
                }*/


                LayoutInflater layoutInflater = nContext.getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.viewpalletgood, null);

                TextView txtPallet = (TextView) view.findViewById(R.id.pallet);
                TextView txtPlant = (TextView) view.findViewById(R.id.plant);
                TextView txtSku = (TextView) view.findViewById(R.id.sku);
                TextView txtLocacion = (TextView) view.findViewById(R.id.locacion);
                TextView txtxyz = (TextView) view.findViewById(R.id.xyz);
                TextView txtCajasRe = (TextView) view.findViewById(R.id.cajas);

                final MyGridView gvDatos = (MyGridView) view.findViewById(R.id.gridViewCamposXML);
              //  final MyGridView gvDatosMerma = (MyGridView) view.findViewById(R.id.gridViewCamposMermaXML);

                if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {
                    gridAdapter = new GridViewAdapter(nombreCampoPermitido, tipoDatoCampoPermitido, position);
                    gvDatos.setAdapter(gridAdapter);
                }


                txtPallet.setText(folios[position]);
                try {
                    JSONObject row = buen.getJSONObject(position);
                    txtPlant.setText(row.getString("iPlant"));
                    txtSku.setText(row.getString("vSku"));
                    txtLocacion.setText(row.getString("vNameLocation"));
                    txtxyz.setText("("+row.getString("iXPosition")+", "+row.getString("iYPosition")+", "+row.getString("iZPosition")+")");
                    //txtLibras.setText(row.getString("libras"));
                    txtCajasRe.setText(row.getString("iCasesTotal"));

                    //etCajas.setText(row.getString("cajas"));
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }


                return view;
            } else {

               /* final ViewHolderFolios holder;

                if(convertView == null ){
                    LayoutInflater layoutInflater = nContext.getLayoutInflater();
                    convertView = layoutInflater.inflate(R.layout.viewfoliosgood, null);
                    holder = new ViewHolderFolios();

                    holder.txtFolios = (TextView) convertView.findViewById(R.id.folio);
                    holder.txtPlant = (TextView) convertView.findViewById(R.id.plant);
                    holder.txtInv = (TextView) convertView.findViewById(R.id.inv);
                    holder.txtHCF = (TextView) convertView.findViewById(R.id.horaCosechaFin);
                    holder.txtSecc = (TextView) convertView.findViewById(R.id.seccion);
                    //TextView txtLibras = (TextView) view.findViewById(R.id.libras);
                    holder.txtCajasRe = (TextView) convertView.findViewById(R.id.cajasRe);

                    holder.btnMerma = (Button) convertView.findViewById(R.id.btnMerma);
                    //holder.lv_Fields = (ListView) convertView.findViewById(R.id.lv_Fields);
                    holder.gvDatos = (MyGridView) convertView.findViewById(R.id.gridViewCamposXML);
                    holder.gvDatosMerma = (MyGridView) convertView.findViewById(R.id.gridViewCamposMermaXML);
                    convertView.setTag(holder);

                }else{
                    holder = (ViewHolderFolios) convertView.getTag();
                }*/
                LayoutInflater layoutInflater = nContext.getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.viewfoliosgood, null);

                //viewfoliostosend

               TextView txtFolios = (TextView) view.findViewById(R.id.folio);
                TextView txtPlant = (TextView) view.findViewById(R.id.plant);
                TextView txtInv = (TextView) view.findViewById(R.id.inv);
                TextView txtHCF = (TextView) view.findViewById(R.id.horaCosechaFin);
                TextView txtSecc = (TextView) view.findViewById(R.id.seccion);
                //TextView txtLibras = (TextView) view.findViewById(R.id.libras);
                TextView txtCajasRe = (TextView) view.findViewById(R.id.cajasRe);

                Button btnMerma = (Button) view.findViewById(R.id.btnMerma);

                final MyGridView gvDatos = (MyGridView) view.findViewById(R.id.gridViewCamposXML);
                final MyGridView gvDatosMerma = (MyGridView) view.findViewById(R.id.gridViewCamposMermaXML);



                if (datosPermitidos.length() > 0 || nombreCampoPermitido != null) {
                    gridAdapter = new GridViewAdapter(nombreCampoPermitido, tipoDatoCampoPermitido, position);
                    gvDatos.setAdapter(gridAdapter);
                   // holder.lv_Fields.setAdapter(gridAdapter);
                }


                if (idTypeLocation == 1) {
                   // btnMerma.setVisibility(View.VISIBLE);
                   // btnMerma.setText("Mermar?");
                } else if (idTypeLocation == 6) {
                    btnMerma.setVisibility(View.INVISIBLE);
                    //btnMerma.setText("Desgrane?");
                } else
                     btnMerma.setVisibility(View.INVISIBLE);


                btnMerma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (idTypeLocation == 1) {//para mermar
                            if (!banderaMerma[position]) {
                                gvDatosMerma.setVisibility(View.VISIBLE);
                                banderaMerma[position] = true;
                                new getMermaFields(config.rutaWebServerOmar + "/getOnholdData", gvDatosMerma, position).execute();
                            } else {
                                banderaMerma[position] = false;

                                //Removemos el dato de la lista
                                HashMap<String, String> posMerm = new HashMap<String, String>();
                                posMerm.put("posMerma", position + "");

                                itemsMerma.remove(posMerm);

                                gvDatosMerma.removeAllViewsInLayout();
                                gvDatosMerma.refreshDrawableState();
                                gvDatosMerma.setVisibility(View.INVISIBLE);

                            }
                        }

                        if (idTypeLocation == 6) { //Si es de linea y si es desgrane...

                        }
                    }
                });

           /* EditText etCajas = (EditText) view.findViewById(R.id.cajas);
            EditText etMerma = (EditText) view.findViewById(R.id.merma);
            EditText etComments = (EditText) view.findViewById(R.id.comments);

            final Spinner razonMerma =  (Spinner) view.findViewById(R.id.spinnerRazonMerma);

            etCajas.addTextChangedListener(new MyTextWatcherCajas(position));
            etMerma.addTextChangedListener(new MyTextWatcherMerma(position));
            etComments.addTextChangedListener(new MyTextWatcherComments(position));

            etCajas.setText(cajasRealesRecibidas[position]);
            etMerma.setText("0");

            String[] mermaReazons = {"Merma por calidad", "Accidente"};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext,android.R.layout.simple_spinner_item, mermaReazons);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            razonMerma.setAdapter(adapter);

            razonMerma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                    if(parent.getItemAtPosition(positionSpinner).toString().compareToIgnoreCase("Merma por calidad") == 0){
                        razonMermaArray[position] = "32";
                        Log.d("RazonMerma[" + position + "]", "32");
                    }else{
                        razonMermaArray[position] = "12";
                        Log.d("RazonMerma[" + position + "]", "12");
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

                txtFolios.setText(folios[position]);
                try {
                    JSONObject row = buen.getJSONObject(position);
                    txtPlant.setText(row.getString("farm"));
                    txtInv.setText(row.getString("gh"));
                    txtHCF.setText(row.getString("endTime"));
                    txtSecc.setText(row.getString("secciones"));
                    //txtLibras.setText(row.getString("libras"));
                    txtCajasRe.setText(row.getString("cajas"));

                    //etCajas.setText(row.getString("cajas"));
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }


                return view;

            }

        }

        static class ViewHolderFolios {
            TextView txtFolios;
            TextView txtPlant;
            TextView txtInv;
            TextView txtHCF;
            TextView txtSecc;
            MyGridView gvDatos;
            TextView txtCajasRe;
            Button btnMerma;
            MyGridView gvDatosMerma;
          //  ListView lv_Fields;
        }

        static class ViewHolderPallet {
            TextView txtPallet;
            TextView txtPlant;
            TextView txtSku;
            TextView txtLocacion;
            TextView txtxyz;
            TextView txtCajasRe;
            MyGridView gvDatos;
        }

    }

    private static class MyTextWatcher implements TextWatcher {

        private int folioPosition, dataPosition, actionA;

        public MyTextWatcher( int folioPosition, int dataPosition, int aa) {
            this.folioPosition = folioPosition;
            this.dataPosition = dataPosition;
            this.actionA = aa;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // int position = (Integer) mEditText.getTag();
            // do something with position:
            if(actionA == 1) {
                Log.d("dataToSend[" + folioPosition + "][" + dataPosition + "]", s.toString());
                dataToSend[folioPosition][dataPosition] = s.toString();
            }else if(actionA==2){//si es merma
                Log.d("merma","Metelo a una matrix de merma");
                Log.d("dataToSendMerma["+folioPosition+"]["+dataPosition+"]",s.toString());
                dataToSendMerma[folioPosition][dataPosition] = s.toString();
            }
        }
    }

    private static class GridViewAdapter extends ArrayAdapter {
        private String[] dataAllowed, dataTypeAllowed;
        private int folioPosition;
        private int actionAdapt;
       // private int conCatalogosVista;

        public GridViewAdapter(String[] dataAllowed, String[] dataTypeAllowed, int folioPosition) {
            super(nContext, R.layout.griditemlayout, dataAllowed);
            this.dataAllowed = dataAllowed;
            this.dataTypeAllowed = dataTypeAllowed;
            this.folioPosition = folioPosition;
            this.actionAdapt = 1;
            conCatalogosVista=-1;
        }

        static class ViewHolder {
            TextView tvHeader;
            EditText etData;
            Spinner spData;
            TextInputLayout usernameWrapper;
        }

        public GridViewAdapter(String[] dataAllowed, String[] dataTypeAllowed, int folioPosition, int accionAdapter) {
            super(nContext, R.layout.griditemlayout, dataAllowed );
            this.dataAllowed = dataAllowed;
            this.dataTypeAllowed = dataTypeAllowed;
            this.folioPosition = folioPosition;
            this.actionAdapt = accionAdapter;
            conCatalogosVista=-1;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

          /*  ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater=nContext.getLayoutInflater();
               // View rowView=inflater.inflate(R.layout.griditemlayout, null, true);
                convertView = inflater.inflate(R.layout.griditemlayout, null);
                holder = new ViewHolder();

                holder.tvHeader = (TextView) convertView.findViewById(R.id.tvHeader);
                holder.etData = (EditText) convertView.findViewById(R.id.etData);
                holder.spData = (Spinner) convertView.findViewById(R.id.spinnerData);
                holder.usernameWrapper = (TextInputLayout) convertView.findViewById(R.id.wrapperData);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }*/

            LayoutInflater inflater=nContext.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.griditemlayout, null, true);
            TextView tvHeader = (TextView) rowView.findViewById(R.id.tvHeader);
            etData = (EditText) rowView.findViewById(R.id.etData);
            Spinner spData = (Spinner) rowView.findViewById(R.id.spinnerData);
            TextInputLayout usernameWrapper = (TextInputLayout) rowView.findViewById(R.id.wrapperData);

                if (dataTypeAllowed[position].contains("catalog")) {



                        final String[] catalogo = dataTypeAllowed[position].split(":");

                        if (catalogo.length == 4) {//Verificar que la estructura sean 4 datos. catalog:nametable:idReturned:nameToShow

                            tvHeader.setText(dataAllowed[position]);
                            etData.setVisibility(View.INVISIBLE);
                            spData.setVisibility(View.VISIBLE);

                            if(idTypeLocation == 6) {

                                if(catalogo[1].compareToIgnoreCase("tbl_Cat_UserExtraRole") == 0) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, alimentadorName);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spData.setAdapter(adapter);
                                }

                                if(catalogo[1].compareToIgnoreCase("tbl_SkuQuality") == 0) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, skuProduct);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spData.setAdapter(adapter);
                                }


                                spData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                                        //if (parent.getItemAtPosition(positionSpinner).toString().compareToIgnoreCase("Merma por calidad") == 0) {
                                        Log.d("posicion", conCatalogosVista + "");
                                        //dataToSendMerma[folioPosition][position] = camposCatalogID[conCatalogosVista][positionSpinner];
                                        if(catalogo[1].compareToIgnoreCase("tbl_Cat_UserExtraRole") == 0) {
                                            dataToSend[folioPosition][position] = alimentadorID[positionSpinner];
                                            Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]", alimentadorID[positionSpinner]);
                                        }

                                        if(catalogo[1].compareToIgnoreCase("tbl_SkuQuality") == 0) {
                                            dataToSend[folioPosition][position] = skuProduct[positionSpinner];
                                            Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]", skuProduct[positionSpinner]);
                                        }

                                        // } else {
                                        //     dataToSendMerma[folioPosition][position] = mermaReasonsID[positionSpinner];
                                        //     Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]",mermaReasonsID[positionSpinner]);
                                        //  }


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }else {

                                conCatalogosVista++;


                                //only if it is merma
                                if (actionAdapt == 2) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, mermaReasonsName);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spData.setAdapter(adapter);

                                    spData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                                            //if (parent.getItemAtPosition(positionSpinner).toString().compareToIgnoreCase("Merma por calidad") == 0) {
                                            Log.d("posicion", conCatalogosVista + "");
                                            //dataToSendMerma[folioPosition][position] = camposCatalogID[conCatalogosVista][positionSpinner];
                                            dataToSendMerma[folioPosition][position] = mermaReasonsID[positionSpinner];
                                            Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]", mermaReasonsID[positionSpinner]);
                                            // } else {
                                            //     dataToSendMerma[folioPosition][position] = mermaReasonsID[positionSpinner];
                                            //     Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]",mermaReasonsID[positionSpinner]);
                                            //  }


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                } else {

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, camposCatalogName[conCatalogosVista]);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spData.setAdapter(adapter);

                                    spData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                                            //if (parent.getItemAtPosition(positionSpinner).toString().compareToIgnoreCase("Merma por calidad") == 0) {
                                            Log.d("posicion", conCatalogosVista + "");
                                            //dataToSendMerma[folioPosition][position] = camposCatalogID[conCatalogosVista][positionSpinner];
                                            dataToSend[folioPosition][position] = camposCatalogID[conCatalogosVista][positionSpinner];
                                            Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]", camposCatalogID[conCatalogosVista][positionSpinner]);
                                            // } else {
                                            //     dataToSendMerma[folioPosition][position] = mermaReasonsID[positionSpinner];
                                            //     Log.d("dataToSendMerma[" + folioPosition + "][" + position + "]",mermaReasonsID[positionSpinner]);
                                            //  }


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    //    }
                                }

                            }

                        }

                } else {
                    etData.setText(" ");
                    if (dataTypeAllowed[position].compareToIgnoreCase("int") == 0) {
                        //etData.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        etData.setText("0");
                    }

                    if (dataTypeAllowed[position].compareToIgnoreCase("decimal") == 0 ||
                            dataTypeAllowed[position].compareToIgnoreCase("float") == 0 ||
                            dataTypeAllowed[position].compareToIgnoreCase("double") == 0) {
                        //etData.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        etData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        etData.setText("0");
                    }

                    tvHeader.setText("");
                    etData.setVisibility(View.VISIBLE);
                    spData.setVisibility(View.INVISIBLE);

                    //we put the hint with effect
                    usernameWrapper.setHint(dataAllowed[position]);

                    //add a textwatcher tosave data for each folio
                    etData.addTextChangedListener(new MyTextWatcher(folioPosition, position, actionAdapt));
                    if(actionAdapt == 1) {
                        try {
                            etData.setText(dataToSend[folioPosition][position]);
                            Log.d("dtsG", dataToSend[folioPosition][position]);
                        }catch(Exception ex){
                            Log.e("GridViewAdapterError", ex.getMessage());
                        }
                    }
                }
                //tvHeader.setText(dataAllowed[position]);
                // etData.setHint(dataAllowed[position]);

            //etData.requestFocus();
            //etData.setText("0");
            //etData.getText().clear();


            return rowView;
        }

    }

    public static class getMermaFields extends AsyncTask<String, String, String> {
        public String url;
        private ProgressDialog pd;
        private MyGridView gvDataMerma;
        private int posit;

        public getMermaFields(String url, MyGridView gvDataMerma, int position){
            this.url = url;
            pd = new ProgressDialog(nContext);
            this.gvDataMerma = gvDataMerma;
            this.posit = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Sync... Please wait!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="", step="0";
            try {


              //  List<NameValuePair> params = new ArrayList<NameValuePair>();

                step="2";
                HttpPost httppostreq = new HttpPost(url);
                step="3";
               // httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step="4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step="5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step="6";
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(String res) {
            // Toast.makeText(GuardarRiego.this, res, Toast.LENGTH_LONG).show();
            Log.d("idWEbMethod -- >", res);

            try {
                pd.dismiss();
            }catch(Exception e){

            }

            try {

                JSONArray campoMerma = new JSONArray(res);
                JSONObject row;
                nombreCampoMerma = new String[campoMerma.length()];
                tipoDatoCampoMerma = new String[campoMerma.length()];

                //Se inicializa la posición que se dio click para guardar los datos de merma
                dataToSendMerma[posit] = new String[campoMerma.length()];


                HashMap<String, String>  posMerm = new HashMap<String, String>();
                posMerm.put("posMerma", posit+"");

                Log.d("PosMerma -- >", posit+"");

                if(!itemsMerma.contains(posMerm))
                    itemsMerma.add(posMerm);

                for(int i=0; i<campoMerma.length(); i++){
                    row = campoMerma.getJSONObject(i);
                    nombreCampoMerma[i] = row.getString("campos");
                    tipoDatoCampoMerma[i] = row.getString("tipoDato");
                }

                //Ultimo valor indicamos que es merma
                gridAdapter = new GridViewAdapter(nombreCampoMerma, tipoDatoCampoMerma, posit, 2);
                gvDataMerma.setAdapter(gridAdapter);
            }catch(Exception e){

            }

        }
    }

    public static String[][] getDataToSend(){
        return dataToSend;
    }

    public static void putDataToSend(String[][] d){
        dataToSend = d;
    }

    private static class getFIFOinLine extends AsyncTask<String, String, String> {
        public String url;
        private String idQR, SKU;
        ProgressDialog pd;

        public getFIFOinLine(String url, String idQR, String SKU) {
            this.url = url;
            this.idQR = idQR;
            this.SKU = SKU;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("SKU", this.SKU));
                params.add(new BasicNameValuePair("idLocation", this.idQR));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step = "4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step = "5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step = "6";
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
                Log.e("Error", t.getMessage()+" - "+jsoncadena);

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(String res) {
           Log.d("webMethod", res);

            try{
                JSONObject json = new JSONObject(res);
                foliosFIFO = json.getJSONArray("table1");

                if(foliosFIFO.length() > 0) {

                    adaptFIFOLine = new FIFOlineAdapter(nContext, foliosFIFO);

                    nContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fabAdd.setVisibility(fabAdd.VISIBLE);
                            fifoList.setAdapter(adaptFIFOLine);
                            fifoList.setVisibility(fifoList.VISIBLE);
                            layoutFormAinLine.setVisibility(layoutFormAinLine.VISIBLE);
                        }
                    });
                }else{

                    nContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fabAdd.setVisibility(fabAdd.VISIBLE);
                            layoutFormAinLine.setVisibility(layoutFormAinLine.VISIBLE);
                            Toast.makeText(nContext, "No hay Folios para mostrar", Toast.LENGTH_LONG).show();
                            fifoList.setVisibility(fifoList.INVISIBLE);
                            //fifoList.setAdapter(adaptFIFOLine);
                        }
                    });
                }

            }catch(Exception ex){
                Log.e("Error", ex.getMessage());
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(nContext, "Error con el servidor. Revise su conexión a internet.", Toast.LENGTH_LONG).show();
                    }
                });

            }

            try{
                pd.dismiss();
            }catch(Exception ex){
                Log.e("Error", ex.getMessage());
            }
        }
    }

}
