package com.ns.empaque.wmpempaque.insertLine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.MAP.locacion;
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
import java.util.List;

/**
 * Created by jcalderon on 31/01/2017.
 */

public class insertLines {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static SharedPreferences sharedPreferences;

    private static LinearLayout btnScanLine, btnAtras, btnHome, infoLines, ly_sku,btnGoFolioToLine,
                                btnGoSKUFIFO, ly_FolioToLine, btnScanFolioToLine, ly_addCases, btnAddCasesInLine,
                                ly_infoFolio, ly_traza;

    //private static ScrollView sc_Traz;
    private static TextView tv_FarmLocation, tv_LineName, tv_Traz, tv_folioInfo; //tv_codeLocation, tv_codeName;
    private static RecyclerView rv_skus,rvTrazaFolio;
    private static EditText et_sku, et_Boxes, et_Line, et_Folio;
    private static TextInputLayout inputLayout;
    private static Spinner sp_Trip, sp_Turno;

    /*********************************************************/
    //Objetos para la informacion del folio
    /*********************************************************/

    private static TextView tv_Folio, tv_secciones, tv_invernadero, tv_product, tv_totalCases, tv_hic, tv_hfc, tv_QA, tv_casesAvailable;

    /********************************************************/
    /********************************************************/

    private static String idLine = "-1", vSKU = "-1", vFolio = "-1", idFarmLine = "-1",
                            idFeeder = "-1", codeLocation = "-1", fifo="-1", vCompletedCode ="-1",
                            idTurno = "-1";

    private static int topCases=0;

    private static ArrayAdapter<String> tripAdapter, turnosAdapter;

    private static ArrayList<Integer> idFeederAl, idPackTurnosAl;

    public insertLines(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
    }

    public static void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_linescan, null, true);
        config.updateContent(content, v);

        inicializaComponntes(v);
        btnEvents();

        infoLines.setVisibility(View.INVISIBLE);
        ly_sku.setVisibility(View.INVISIBLE);
        ly_FolioToLine.setVisibility(View.INVISIBLE);
        ly_addCases.setVisibility(View.INVISIBLE);
        //sc_Traz.setVisibility(View.INVISIBLE);
        ly_traza.setVisibility(View.INVISIBLE);
        ly_infoFolio.setVisibility(View.INVISIBLE);


        sp_Trip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               // ArrayList<String> a =  tripAdapter.getItem(i);

                insertLines.idFeeder = idFeederAl.get(i)+"";
                config.actualizarSharedPreferencesInt(nContext, "idTrip", idFeederAl.get(i));
               // new PopUp(nContext, insertLines.idFeeder, PopUp.POPUP_INFORMATION).showPopUp();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_Turno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // ArrayList<String> a =  tripAdapter.getItem(i);

                insertLines.idTurno = idPackTurnosAl.get(i)+"";
                config.actualizarSharedPreferencesInt(nContext, "idTurno", idPackTurnosAl.get(i));
                // new PopUp(nContext, insertLines.idFeeder, PopUp.POPUP_INFORMATION).showPopUp();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        textWatcherforLine();

    }

    private static void textWatcherforLine() {
        et_Line.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(config.isALine(editable.toString())) {
                    getLineInformation(editable.toString());
                }
            }
        });
    }

    private static void inicializaComponntes(View v) {
        //Linears
        btnScanLine = (LinearLayout) v.findViewById(R.id.btnScanLine);
        btnAtras = (LinearLayout) v.findViewById(R.id.btnAtras);
        btnHome =  (LinearLayout) v.findViewById(R.id.btnHome);
       // btnGoLine =  (LinearLayout) v.findViewById(R.id.btnGoLine);
        infoLines = (LinearLayout) v.findViewById(R.id.infoLines);
        ly_sku = (LinearLayout) v.findViewById(R.id.ly_sku);
        btnGoSKUFIFO = (LinearLayout) v.findViewById(R.id.btnGoSKUFIFO);
        ly_FolioToLine = (LinearLayout) v.findViewById(R.id.ly_FolioToLine);
        ly_infoFolio = (LinearLayout) v.findViewById(R.id.ly_infoFolio);
        btnScanFolioToLine = (LinearLayout) v.findViewById(R.id.btnScanFolioToLine);
        ly_addCases = (LinearLayout) v.findViewById(R.id.ly_addCases);
        btnAddCasesInLine = (LinearLayout) v.findViewById(R.id.btnAddCasesInLine);
        btnGoFolioToLine = (LinearLayout) v.findViewById(R.id.btnGoFolioToLine);
        ly_traza = (LinearLayout) v.findViewById(R.id.ly_traza);

        //Scrolls
       // sc_Traz = (ScrollView) v.findViewById(R.id.sc_Traz);

        //TewxtInputLayout
        inputLayout = (TextInputLayout) v.findViewById(R.id.input_layout_SKU);

        //TextViews
        //tv_codeLocation = (TextView) v.findViewById(R.id.tv_codeLocation);
        //tv_codeName = (TextView) v.findViewById(R.id.tv_codeName);
        tv_FarmLocation = (TextView) v.findViewById(R.id.tv_FarmLocation);
        tv_LineName = (TextView) v.findViewById(R.id.tv_LineName);
        tv_Traz = (TextView) v.findViewById(R.id.tv_Traz);
        tv_folioInfo = (TextView) v.findViewById(R.id.tv_folioInfo);

        /*********************************************************/
        //Objetos para la informacion del folio
        /*********************************************************/
        tv_Folio = (TextView) v.findViewById(R.id.tv_Folio);
        tv_secciones = (TextView) v.findViewById(R.id.tv_secciones);
        tv_invernadero = (TextView) v.findViewById(R.id.tv_invernadero);
        tv_product= (TextView) v.findViewById(R.id.tv_product);
        tv_totalCases = (TextView) v.findViewById(R.id.tv_totalCases);
        tv_hic = (TextView) v.findViewById(R.id.tv_hic);
        tv_hfc = (TextView) v.findViewById(R.id.tv_hfc);
        tv_QA = (TextView) v.findViewById(R.id.tv_QA);
        tv_casesAvailable = (TextView) v.findViewById(R.id.tv_casesAvailable);
        /*********************************************************/
        /*********************************************************/


        //RecyclerView
        rv_skus = (RecyclerView) v.findViewById(R.id.rv_skus);
       // rvFIFOLine = (RecyclerView) v.findViewById(R.id.rvFIFOLine);
        rvTrazaFolio = (RecyclerView) v.findViewById(R.id.rvTrazaFolio);

        rv_skus.setLayoutManager(new LinearLayoutManager(nContext));
       // rvFIFOLine.setLayoutManager(new LinearLayoutManager(nContext));


        //EditText
        et_sku = (EditText) v.findViewById(R.id.et_sku);
        et_Boxes = (EditText) v.findViewById(R.id.et_Boxes);
        et_Line = (EditText) v.findViewById(R.id.et_Line);
        et_Folio = (EditText) v.findViewById(R.id.et_Folio);

        //Spinners
        sp_Trip = (Spinner) v.findViewById(R.id.sp_Trip);
        sp_Turno = (Spinner) v.findViewById(R.id.sp_Turno);
    }

    private static void btnEvents() {
        btnScanLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WMPEmpaque.linea = 1;
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(nContext)
                        .setTitle("Salir?")
                        .setMessage("Seguro que deseas salir de este modulo?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                content.removeAllViewsInLayout();
                                config.backContent(content);
                                WMPEmpaque.tipoApp = 0;
                                WMPEmpaque.setAvisos(nContext);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.naturesweet)
                        .show();

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(nContext)
                        .setTitle("Salir?")
                        .setMessage("Seguro que deseas salir de este modulo?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                content.removeAllViewsInLayout();
                                config.backContent(content);
                                WMPEmpaque.tipoApp = 0;
                                WMPEmpaque.setAvisos(nContext);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.naturesweet)
                        .show();


            }
        });

        btnGoSKUFIFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFIFO(et_sku.getText().toString());
                ly_FolioToLine.setVisibility(View.VISIBLE);



            }
        });

        btnGoFolioToLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFolioToLine(et_Folio.getText().toString());
            }
        });

        btnScanFolioToLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WMPEmpaque.linea = 0;
                if(insertLines.vSKU.compareToIgnoreCase("-1") != 0) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                    scanIntegrator.initiateScan();
                }else{
                    new PopUp(nContext, "Hay un problema con el SKU, por favor verifique que exista para poder continuar con el escaneo del folio", "Problema con SKU", PopUp.POPUP_INFORMATION).showPopUp();
                }
            }
        });

        btnAddCasesInLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(insertLines.vSKU.compareToIgnoreCase("-1") != 0) {
                    if(insertLines.idLine.compareToIgnoreCase("-1") != 0){
                        if(insertLines.vFolio.compareToIgnoreCase("-1") != 0) {
                            if(insertLines.idTurno.compareToIgnoreCase("-1") != 0) {
                                if (insertLines.idFeeder.compareToIgnoreCase("-1") != 0) {
                                    if (et_Boxes.getText().toString().compareToIgnoreCase("") == 0) {
                                        new PopUp(nContext, "Para continuar debes digitar la cantidad de cajas que quieres ingresar a linea del folio " + insertLines.vFolio, "Error en cajas", PopUp.POPUP_INFORMATION).showPopUp();
                                    } else {
                                        try {
                                            int cajas = Integer.parseInt(et_Boxes.getText().toString());
                                            if (cajas > 0) {

                                                Toast.makeText(nContext, "Enviando cajas a Linea...", Toast.LENGTH_SHORT).show();
                                                new ATInsertCasesInLine(config.rutaWebServerOmar + "/insertCasesInLine",
                                                        insertLines.idLine, insertLines.vSKU, insertLines.vFolio, cajas + "",
                                                        insertLines.idFeeder, insertLines.codeLocation, insertLines.fifo, insertLines.idTurno, "DBTAPP").execute();
                                                //new PopUp(nContext, "idFeeder: "+idFeeder+"; idTurno: "+idTurno,PopUp.POPUP_INFORMATION).showPopUp();

                                            } else {
                                                new PopUp(nContext, "Debes intentar introducir cajas mayor que cero", "Error en cantidad de cajas", PopUp.POPUP_INFORMATION).showPopUp();
                                            }
                                        } catch (Exception e) {
                                            new PopUp(nContext, "En el campo de texto de las cajas solo debe digitar números", "Error en en campo de texto", PopUp.POPUP_INFORMATION).showPopUp();
                                        }
                                    }
                                } else {
                                    new PopUp(nContext, "No se eligió una tripulación al momento de insertar a linea. Por favo seleccionalo.", "Problema con el alimentador elegido", PopUp.POPUP_INFORMATION).showPopUp();
                                }
                            }else{
                                new PopUp(nContext, "Hay un problema con el Turno, por favor verifique que todo este correcto para poder continuar introduciendo cajas a linea", "Problema con el Turno Seleccionado", PopUp.POPUP_INFORMATION).showPopUp();
                            }
                        } else {
                            new PopUp(nContext, "Hay un problema con el Folio, por favor verifique que todo este correcto para poder continuar introduciendo cajas a linea", "Problema con el Folio Escaneado", PopUp.POPUP_INFORMATION).showPopUp();
                        }
                    }else{
                        new PopUp(nContext, "Hay un problema con la linea, por favor verifique que exista para poder continuar introduciendo cajas a linea", "Problema con la Linea Escaneada", PopUp.POPUP_INFORMATION).showPopUp();
                    }
                }else{
                    new PopUp(nContext, "Hay un problema con el SKU, por favor verifique que exista para poder continuar introduciendo cajas a linea", "Problema con SKU", PopUp.POPUP_INFORMATION).showPopUp();
                }
            }
        });

    }

    public static void sendFolioToLine(String vFolio){
        if(config.validaString(vFolio, nContext) == 1) {
            tv_folioInfo.setText("");
            clearInfoFolio();
            new ATSendFolioToLine(config.rutaWebServerOmar + "/checkFolioToLine", insertLines.idLine, insertLines.vSKU, vFolio).execute();
        }
        else{
            new PopUp(nContext, "El codigo leido no tiene la estructura de un folio", "No parece folio", PopUp.POPUP_INFORMATION).showPopUp();
        }
    }

    private static void showFIFO(String sku) {
        new ATGetFIFO(config.rutaWebServerOmar+"/getFIFOLineNew", idLine, sku).execute();
    }

    public static void getLineInformation(String vCodeLine){

        if(config.isALine(vCodeLine)) {
            try {
                vCompletedCode = vCodeLine;
                String[] code = vCodeLine.split("\\|");

           /* Log.e("code1", code[0]);
            Log.d("code2", code[1]);*/

                //   new PopUp(nContext, "Size: "+code.length+" - [0]: "+code[0]+" - [1]: "+code[1], PopUp.POPUP_OK).showPopUp();

                String[] codeLines = code[0].split("-");

                String idLineLocal = codeLines[1];
                insertLines.codeLocation = codeLines[0];

                // new PopUp(nContext, "idLine: "+ codeLines[1]+" - codeLocation: "+codeLines[0], PopUp.POPUP_OK).showPopUp();

                BaseDatos bd = new BaseDatos(nContext);
                bd.abrir();
                locacion c = bd.getLocationLineByCode(insertLines.codeLocation.trim().toString(), 6);
                bd.cerrar();


                //Si existe la locacion Code
                if (c.getIdLocation() > 0) {
                    // new PopUp(nContext, "todo bien - "+c.getNameLocation(), PopUp.POPUP_OK).showPopUp();
                    infoLines.setVisibility(View.VISIBLE);
                   // tv_codeLocation.setText(c.getCode());
                    //tv_codeName.setText(c.getNameLocation());
                    tv_FarmLocation.setText(c.getFarm() + "");

                    tv_LineName.setText("---");

                    bd.abrir();
                    ArrayList<linesPackage> alLP = bd.getLinePackageByIdLine(idLineLocal, c.getFarm());
                    bd.cerrar();

                    //Si existe la linea o esta activa
                    if (alLP.size() > 0) {
                        //Ponemos el campo de texto del sku en nada para que el usuario lo vuelva a digitar
                        et_sku.setText("");

                        linesPackage lp = alLP.get(0);
                        insertLines.idLine = lp.getIdLine() + "";
                        insertLines.idFarmLine = lp.getIdPlant()+"";
                        Log.d("lineaName", lp.getvNameLine());
                        //  BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();
                        ArrayList<String[]> skusList = bd.getLineSKUbyIdLine(lp.getIdLine() + "");
                        bd.cerrar();

                        tv_LineName.setText(lp.getvNameLine());
                        if (skusList.size() > 0) {
                            rv_skus.setAdapter(new rvListSKUsAdapter(skusList));
                        } else {
                            skusList.clear();
                            String[] noData = new String[1];
                            noData[0] = "No hay SKUs configurados para la linea, por favor sincronize catalogos.";
                            skusList.add(noData);
                            rv_skus.setAdapter(new rvListSKUsAdapter(skusList));

                            new PopUp(nContext, "No se encontró SKU's configurados en la linea.\n\n" +
                                    "Usted puede seguir trabajando, solo no olvide digitar el SKU en el campo de abajo.\n\n" +
                                    "Para configurar SKU's en la linea es necesario ir a la página web empaque.naturesweet.com.mx\n\n" +
                                    "Si ya han sido configurados los SKU's por favor sincronice catalogos.\n\n" +
                                    "Si ya sincronizó catalogos y el problema persiste, por favor contactar al administrador del sistema.", "Sku's no configurados", PopUp.POPUP_INFORMATION).showPopUp();
                        }

                        final String[] skus = skusList.get(0);
                        ly_sku.setVisibility(View.VISIBLE);

                        et_sku.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                btnGoSKUFIFO.setEnabled(true);
                                //btnScanFolioToLine.setEnabled(true);
                                //btnAddCasesInLine.setEnabled(true);

                                if (verifySKUinLine(s.toString(), skus)) {
                                    Log.d("SKU", "SKU in Line");
                                    inputLayout.setError(null);
                                    insertLines.vSKU = s.toString();
                                    //  banderaSKUnotInLine = false;
                                } else {
                                    Log.d("SKU", "SKU not in line");
                                    //  banderaSKUnotInLine = true;
                                    // inputLayout.setError("El SKU no existe en la linea");

                                    if (!verifySKUinItemMaster(s.toString())) {
                                        inputLayout.setError("El SKU no existe.");
                                        Log.d("SKU", "SKU not exists");

                                        btnGoSKUFIFO.setEnabled(false);
                                       // btnScanFolioToLine.setEnabled(false);
                                        //btnAddCasesInLine.setEnabled(false);

                                        insertLines.vSKU = "-1";

                                    } else {
                                        inputLayout.setError("El SKU no esta en la linea");
                                        insertLines.vSKU = s.toString();
                                    }
                                }
                            }
                        });

                    } else {
                        insertLines.idLine = "-1";
                        insertLines.idFarmLine = "-1";
                        tv_LineName.setText("---");
                        ly_sku.setVisibility(View.INVISIBLE);

                        ArrayList<String[]> skusList = new ArrayList<>();
                        String[] noData = new String[1];
                        noData[0] = "No hay SKUs configurados para la linea, por favor sincronize catalogos.";
                        skusList.add(noData);
                        rv_skus.setAdapter(new rvListSKUsAdapter(skusList));

                        new PopUp(nContext, "No se encontró linea activa o no existe.\n\n" +
                                "Para activar o agregar la linea es necesario ir a la página web empaque.naturesweet.com.mx\n\n" +
                                "Si la linea si existe y está activa es necesario sincronizar los catalogos.\n\n" +
                                "Si ya sincronizó catalogos y el problema persiste, por favor contactar al administrador del sistema", "Linea no encontrada", PopUp.POPUP_INCORRECT).showPopUp();
                    }

                } else {
                    new PopUp(nContext, "Por favor Sincronice catalogos.\n" +
                            "\nSi el problema persiste el codigo leido no es una locación Linea.\n\n", "No encontró locación", PopUp.POPUP_INCORRECT).showPopUp();

                    infoLines.setVisibility(View.INVISIBLE);
                    insertLines.codeLocation = "-1";
                }

            } catch (Exception ex) {
                new PopUp(nContext, "Por favor Sincronice catalogos.\n" +
                        "\nSi el problema persiste el codigo leido no es una locación Linea.\n\n" +
                        ex.getMessage(), "No encontró locación", PopUp.POPUP_INCORRECT).showPopUp();
                ex.printStackTrace();
            }

        }else{
            new PopUp(nContext, "El codigo leido no tiene estructura de linea", "No es linea!", PopUp.POPUP_INFORMATION).showPopUp();
            vCompletedCode = "-1";
        }


    }

    public static boolean verifySKUinLine(String SKU, String[] SKUinLine){

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



    public static class ATGetFIFO extends AsyncTask<String, String, String> {
        public String url, idLine, sku;
        private ProgressDialog pd;
        private int step=0;

        public ATGetFIFO(String url, String idLine, String sku){
            this.url = url;
            this.idLine = idLine;
            this.sku = sku;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Obteniendo FIFO... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLine", this.idLine));
                params.add(new BasicNameValuePair("sku", this.sku));

                step=2;
                HttpPost httppostreq = new HttpPost(url);
                step=3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }


        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();

            ArrayList<String[]> alFIFO = new ArrayList<>();


            if(step < 6){
                new PopUp(nContext, "Algo fue mal en la conexión al servidor, por favor vuelve a intentar.", "Sin respuesta del servidor", PopUp.POPUP_INCORRECT).showPopUp();
            }

           // new PopUp(nContext, res, "RESPUESTA",PopUp.POPUP_OK).showPopUp();

            try {
                JSONObject json = new JSONObject(res);
                JSONArray jsFIFOFolio = json.getJSONArray("table1");

                if(jsFIFOFolio.length() > 0){
                    for(int i = 0; i<jsFIFOFolio.length(); i++) {

                        JSONObject rowInfo = jsFIFOFolio.getJSONObject(i);
                        String FIFOFolio[] = new String[rowInfo.length()];

                        FIFOFolio[0] = rowInfo.getString("vFolio");
                        FIFOFolio[1] = rowInfo.getString("vNameLocation");
                        FIFOFolio[2] = rowInfo.getString("casesAvailable");
                        FIFOFolio[3] = rowInfo.getString("QADate");

                        alFIFO.add(FIFOFolio);

                    }

                    insertLines.fifo = alFIFO.get(0)[0];

                    //rvFIFOLine.setAdapter(new rvFIFOLineAdapter(alFIFO));
                    //rvFIFOLine.setAdapter(new FIFOLineAdapter(alFIFO));

                    LayoutInflater inflater = nContext.getLayoutInflater();
                    View viewFIFO = inflater.inflate(R.layout.fifo_popup, null);

                    RecyclerView rvFIFOLine = (RecyclerView) viewFIFO.findViewById(R.id.rvFIFOLine);
                    rvFIFOLine.setLayoutManager(new LinearLayoutManager(nContext));
                    rvFIFOLine.setAdapter(new FIFOLineAdapter(alFIFO));

                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
                    alertDialog2
                            .setView(viewFIFO)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                    //alertDialog2.setIcon(R.drawable.naturesweet);
                    // alertDialog2.setTitle();
                    alertDialog2.setCancelable(false);
                    final AlertDialog ad2 = alertDialog2.create();
                   // ad2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    ad2.show();

                    ad2.getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    //new PopUp(nContext, ""+insertLines.fifo, "FIFO FOLIO FIRST", PopUp.POPUP_INFORMATION).showPopUp();

                }else{
                    new PopUp(nContext, "No hay folios para mostrar en el FIFO", "No se encontraron Folios para ese SKU", PopUp.POPUP_INFORMATION).showPopUp();
                    insertLines.fifo = "-1";
                }




            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

    public static class ATSendFolioToLine extends AsyncTask<String, String, String> {
        public String url, idLine, sku, vFolio;
        private ProgressDialog pd;
        private int step=0;

        public ATSendFolioToLine(String url, String idLine, String sku, String vFolio){
            this.url = url;
            this.idLine = idLine;
            this.sku = sku;
            this.vFolio = vFolio;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Verificando Folio... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLine", this.idLine));
                params.add(new BasicNameValuePair("vSKU", this.sku));
                params.add(new BasicNameValuePair("vFolio", this.vFolio));

                step=2;
                HttpPost httppostreq = new HttpPost(url);
                step=3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }


        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();
            String Mensaje = "";

            if(step < 6){
                new PopUp(nContext, "Algo fue mal en la conexión al servidor, por favor vuelve a intentar.", "Sin respuesta del servidor", PopUp.POPUP_INCORRECT).showPopUp();
            }

            // new PopUp(nContext, res, "RESPUESTA",PopUp.POPUP_OK).showPopUp();

            try {
                JSONObject json = new JSONObject(res);
                JSONArray jsGHSku = json.getJSONArray("table1");
                final JSONArray jsTraza = json.getJSONArray("table2");
                final JSONArray jsInfo = json.getJSONArray("table3");
                JSONArray jsError = json.getJSONArray("table4");

                if(jsError.length() > 0){
                    Mensaje = "";
                    Mensaje += "Folio: "+ this.vFolio+" \n\n";
                    for(int i =0 ; i < jsError.length(); i++) {
                        JSONObject rowError = jsError.getJSONObject(i);
                        Mensaje += " - "+rowError.getString("Razon")+" \n";
                    }

                    new PopUp(nContext, Mensaje, "Folio no permitido en Linea", PopUp.POPUP_INFORMATION).showPopUp();

                    //sc_Traz.setVisibility(View.INVISIBLE);
                    ly_traza.setVisibility(View.INVISIBLE);
                    ly_addCases.setVisibility(View.INVISIBLE);
                    insertLines.vFolio = "-1";
                    //insertLines.vSKU = "-1";

                }else {

                    if (jsGHSku.length() > 0) {
                        JSONObject rowInfo = jsGHSku.getJSONObject(0);

                        String lastSKU, newSKU, lastGH, newGH;
                        lastSKU = rowInfo.getString("lastSKU");
                        newSKU = rowInfo.getString("newSKU");
                        lastGH = rowInfo.getString("lastGH");
                        newGH = rowInfo.getString("newGH");
                        Mensaje = "";
                        if (lastSKU.compareToIgnoreCase(newSKU) != 0) {
                            Mensaje += "El SKU que se intenta introducir a la linea es diferente a los que se estan introduciendo actualmente.  \n\n";
                            Mensaje += "Ultimo SKU introducido es " + lastSKU + ". \n";
                            Mensaje += "Nuevo SKU que se quiere introducir es " + newSKU + ". \n\n";
                        }

                        if (lastGH.compareToIgnoreCase(newGH) != 0) {
                            Mensaje += "El Invernadero que se intenta introducir a la linea es diferente a los que se estan introduciendo actualmente. \n\n";
                            Mensaje += "Ultimo Invernadero introducido es " + lastGH + ". \n";
                            Mensaje += "Nuevo Invernadero que se quiere introducir es " + newGH + ". \n\n";
                        }

                        if (Mensaje.compareToIgnoreCase("") != 0) {
                            new AlertDialog.Builder(nContext)
                                    .setTitle("Cambio de Invernadero o SKU en Linea")
                                    .setMessage("Seguro que deseas Introducir este Folio a Linea? \n\n" + Mensaje)
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //sc_Traz.setVisibility(View.VISIBLE);
                                            ly_traza.setVisibility(View.VISIBLE);
                                            ly_addCases.setVisibility(View.VISIBLE);

                                            insertLines.vFolio = ATSendFolioToLine.this.vFolio;
                                            insertLines.getTrazaFolio(jsTraza);
                                            insertLines.showInfo(jsInfo);
                                            insertLines.fillTrip();
                                            insertLines.fillTurnos();

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            insertLines.vFolio = "-1";
                                           // sc_Traz.setVisibility(View.INVISIBLE);
                                            ly_traza.setVisibility(View.INVISIBLE);
                                            ly_addCases.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                    .setIcon(R.drawable.naturesweet)
                                    .show();
                        }else{
                            //sc_Traz.setVisibility(View.VISIBLE);
                            ly_traza.setVisibility(View.VISIBLE);
                            ly_addCases.setVisibility(View.VISIBLE);

                            insertLines.vFolio = ATSendFolioToLine.this.vFolio;
                            insertLines.getTrazaFolio(jsTraza);
                            insertLines.showInfo(jsInfo);
                            insertLines.fillTrip();
                            insertLines.fillTurnos();
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

    private static void fillTurnos() {

        try {
            turnosAdapter.clear();
            turnosAdapter.notifyDataSetChanged();
        }
        catch(Exception ex){

        }

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final ArrayList<packTurnos> al_packTurns = bd.packTurnosDB.getPackTurnosByPlant(idFarmLine);
        bd.cerrar();

        idPackTurnosAl = new ArrayList<>();
        ArrayList<String> nameTurno = new ArrayList<>();

        for(int i=0; i<al_packTurns.size(); i++){
            idPackTurnosAl.add(al_packTurns.get(i).getIdTurno());
            nameTurno.add(al_packTurns.get(i).getvNameTurno());
        }

         //new PopUp(nContext, ""+sharedPreferences.getInt("idTurno", idPackTurnosAl.get(0)),"Information Turno", PopUp.POPUP_INFORMATION).showPopUp();

        if(al_packTurns.size() > 0){
            turnosAdapter = new ArrayAdapter<>(nContext, android.R.layout.simple_spinner_item, nameTurno);
            sp_Turno.setAdapter(turnosAdapter);
            insertLines.idTurno = idPackTurnosAl.indexOf(sharedPreferences.getInt("idTurno", idPackTurnosAl.get(0)))+"";
            try {
                sp_Turno.setSelection(idPackTurnosAl.indexOf(sharedPreferences.getInt("idTurno", idPackTurnosAl.get(0))));
            }catch(Exception ex){
                insertLines.idTurno = idPackTurnosAl.get(0)+"";
            }
        }else{
            new PopUp(nContext, "No hay turnos en la base de datos local de la tableta.\n\n" +
                    "Por favor sincroniza catalogos.\n\n" +
                    "Si el problema persiste entonces es necesario agregar los turnos en el web empaque.naturesweet.com.mx\n\n" +
                    "Una vez que los agregues en el web, vuelve a sincronizar catalogos.",
                    "No encontro Turnos", PopUp.POPUP_INFORMATION).showPopUp();

            insertLines.idTurno = "-1";
        }

    }

    private static void fillTrip() {

        try {
            tripAdapter.clear();
            tripAdapter.notifyDataSetChanged();
        }
        catch(Exception ex){

        }

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final ArrayList<lineTripulaciones> al_trip = bd.getUserExtraRoleAL(idFarmLine);
        bd.cerrar();

        idFeederAl = new ArrayList<>();
        ArrayList<String> nameFeeder = new ArrayList<>();

        for(int i=0; i<al_trip.size(); i++){
            idFeederAl.add(al_trip.get(i).getID());
            nameFeeder.add(al_trip.get(i).getName());
        }

       // new PopUp(nContext, ""+sharedPreferences.getInt("idTrip", idFeederAl.get(0)),"Information Feeder", PopUp.POPUP_INFORMATION).showPopUp();

        if(al_trip.size() > 0){
            tripAdapter = new ArrayAdapter<>(nContext, android.R.layout.simple_spinner_item, nameFeeder);
            sp_Trip.setAdapter(tripAdapter);
            //insertLines.idFeeder = idFeederAl.get(0)+"";
            insertLines.idFeeder = idFeederAl.indexOf(sharedPreferences.getInt("idTrip", idFeederAl.get(0)))+"";
            try {
                sp_Trip.setSelection(idFeederAl.indexOf(sharedPreferences.getInt("idTrip", idFeederAl.get(0))));
            }catch(Exception ex){
                insertLines.idFeeder = idFeederAl.get(0)+"";
            }
        }else{
            new PopUp(nContext, "No hay tripulaciones en la base de datos local de la tableta.\n\n" +
                    "Por favor sincroniza catalogos.\n\n" +
                    "Si el problema persiste entonces es necesario agregar las tripulaciones en el web empaque.naturesweet.com.mx\n\n" +
                    "Una vez que los agregues en el web, vuelve a sincronizar catalogos.",
                    "No encontro tripulaciones", PopUp.POPUP_INFORMATION).showPopUp();

            insertLines.idFeeder = "-1";
        }

    }

    public static class ATInsertCasesInLine extends AsyncTask<String, String, String> {
        public String url, idLine, sku, vFolio, tripulacion, cajas, codeLocation, fifo, user, idTurno;
        private ProgressDialog pd;
        private int step=0;

        public ATInsertCasesInLine(String url, String idLine, String sku, String vFolio, String cajas, String tripulacion, String codeLocation, String fifo, String idTurno, String user){
            this.url = url;
            this.idLine = idLine;
            this.sku = sku;
            this.vFolio = vFolio;
            this.tripulacion = tripulacion;
            this.cajas = cajas;
            this.codeLocation = codeLocation;
            this.fifo = fifo;
            this.user = user;
            this.idTurno = idTurno;

            pd = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Insertando "+cajas+" Cajas del folio "+vFolio+"... Por favor espere!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="";
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vFolio", this.vFolio));
                params.add(new BasicNameValuePair("idLine", this.idLine));
                params.add(new BasicNameValuePair("vSKU", this.sku));
                params.add(new BasicNameValuePair("cases", this.cajas));
                params.add(new BasicNameValuePair("vCodeLocation", this.codeLocation));
                params.add(new BasicNameValuePair("idFeeder", this.tripulacion));
                params.add(new BasicNameValuePair("fifo", this.fifo));
                params.add(new BasicNameValuePair("idTurno", this.idTurno));
                params.add(new BasicNameValuePair("user", this.user));


                step=2;
                HttpPost httppostreq = new HttpPost(url);
                step=3;
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step=4;
                HttpResponse httpresponse = Client.execute(httppostreq);
                step=5;
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step=6;
            } catch (Exception t) {
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("WebMethod -- >", res);
            // Toast.makeText(nContext, res,Toast.LENGTH_LONG).show();
            String Mensaje = "";

            if(step < 6){
                new PopUp(nContext, "Algo fue mal en la conexión al servidor, por favor vuelve a intentar.", "Sin respuesta del servidor", PopUp.POPUP_INCORRECT).showPopUp();
            }

           //  new PopUp(nContext, res, "RESPUESTA", PopUp.POPUP_OK).showPopUp();

            try {
                JSONObject json = new JSONObject(res);
                JSONArray jsCheck = json.getJSONArray("table1");
                JSONArray jsInfo = json.getJSONArray("table2");

                if(jsCheck.length() > 0){
                    JSONObject rowCheck = jsCheck.getJSONObject(0);

                    if(rowCheck.get("indicator").toString().compareToIgnoreCase("1") == 0){
                        sendFolioToLine(insertLines.vFolio);
                        //showFIFO(insertLines.vSKU);

                        new PopUp(nContext, "Inserción correcta en la linea de empaque", "Inserción correcta", PopUp.POPUP_OK).showPopUp();
                    }else {
                        JSONObject rowInfo = jsInfo.getJSONObject(0);
                        new PopUp(nContext, "No se pudo insertar en line por la siguiente razón:\n\n"+ rowInfo.getString("msg"), "Error al insertar en linea", PopUp.POPUP_INFORMATION).showPopUp();
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
            }

            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }


    private static void showInfo(JSONArray jsInfo) {



        try {
           // String Mensaje = "";
            //Mensaje = "Folio: "+ insertLines.vFolio+" \n\n";
            if(jsInfo.length() > 0){
                JSONObject rowInfo = jsInfo.getJSONObject(0);
              /*  Mensaje += " - Total de cajas: "+rowInfo.getString("TotalCases")+".\n";
                Mensaje += " - Invernadero: "+rowInfo.getString("vGh")+".\n";
                Mensaje += " - Secciones: "+rowInfo.getString("vSecciones")+".\n";
                Mensaje += " - Cosecha Inicio: "+rowInfo.getString("HCI")+".\n";
                Mensaje += " - Cosecha Fin: "+rowInfo.getString("HCF")+".\n";
                Mensaje += " - Cajas Disponibles: "+rowInfo.getString("CasesDisponibles")+".\n";
                Mensaje += " - Calidad: "+rowInfo.getString("Calidad")+".\n";
                Mensaje += " - Producto: "+rowInfo.getString("Producto")+".\n";*/

                insertLines.topCases = Integer.parseInt(rowInfo.getString("CasesDisponibles"));
                eventBoxes();
                et_Boxes.setText(topCases+"");

                tv_Folio.setText(insertLines.vFolio);
                tv_secciones.setText(rowInfo.getString("vSecciones"));
                tv_invernadero.setText(rowInfo.getString("vGh"));
                tv_casesAvailable.setText(rowInfo.getString("CasesDisponibles"));
                tv_totalCases.setText(rowInfo.getString("TotalCases"));
                tv_QA.setText(rowInfo.getString("Calidad"));
                tv_product.setText(rowInfo.getString("Producto"));
                tv_hic.setText(rowInfo.getString("HCI"));
                tv_hfc.setText(rowInfo.getString("HCF"));

                ly_infoFolio.setVisibility(View.VISIBLE);


               Toast.makeText(nContext, "Cajas: "+rowInfo.getString("CasesDisponibles"), Toast.LENGTH_SHORT).show();

                //tv_folioInfo.setText(Mensaje);
                btnAddCasesInLine.setEnabled(true);
                ly_addCases.setVisibility(View.VISIBLE);
            }else{

                //Si no hay info del folio es imposible meter cajas a linea
                ly_addCases.setVisibility(View.INVISIBLE);
                btnAddCasesInLine.setEnabled(false);

                insertLines.vFolio = "-1";
            }
        }catch(Exception e){
            //new PopUp(nContext, "Error inesperado en trazabilidad", "Error al traer información del servidor"PopUp.POPUP_INFORMATION).showPopUp();
            Toast.makeText(nContext, "Error en obtener información del folio", Toast.LENGTH_SHORT).show();
        }
    }

    private static void clearInfoFolio(){
        tv_Folio.setText("");
        tv_secciones.setText("");
        tv_invernadero.setText("");
        tv_casesAvailable.setText("");
        tv_totalCases.setText("");
        tv_QA.setText("");
        tv_product.setText("");
        tv_hic.setText("");
        tv_hfc.setText("");

        ly_infoFolio.setVisibility(View.INVISIBLE);
    }

    private static void eventBoxes() {

        et_Boxes.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (Integer.parseInt(s.toString()) > topCases) {
                        et_Boxes.setText(topCases + "");
                    }
                }catch (Exception e){
                    Log.e("ERROR", e.getMessage());
                   // Toast.makeText(nContext, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static void getTrazaFolio(JSONArray jsTraza) {
        tv_Traz.setText("");
        try {
            if (jsTraza.length() > 0) {
                ArrayList<TrazaLineInformation> alTraza = new ArrayList<>();
                String Mensaje = "\n\nIngresos anteriores del folio\n\n";
                for (int i = 0; i < jsTraza.length(); i++) {
                    JSONObject rowTraza = jsTraza.getJSONObject(i);
                    String TrazaFolio[] = new String[rowTraza.length()];
                    /*Mensaje += " - Nombre de la linea: "+rowTraza.getString("vNameLine")+"\n";
                    Mensaje += " - Fecha de Ingreso: "+rowTraza.getString("dIntime")+"\n";
                    Mensaje += " - Planta Empaque: "+rowTraza.getString("Planta")+"\n";
                    Mensaje += " - Cajas Empacadas: "+rowTraza.getString("iCasesPack")+"\n";
                    Mensaje += " - SKU: "+rowTraza.getString("vSKU")+"\n\n";*/
                    TrazaLineInformation tli = new TrazaLineInformation();

                    tli.setFolio(vFolio);
                    tli.setBoxes(rowTraza.getString("iCasesPack"));
                    tli.setFechaIngreso(rowTraza.getString("dIntime"));
                    tli.setNameLine(rowTraza.getString("vNameLine"));
                    tli.setPlanta(rowTraza.getString("Planta"));
                    tli.setSku(rowTraza.getString("vSKU"));

                    /*TrazaFolio[0] = rowTraza.getString("vNameLine");
                    TrazaFolio[1] = rowTraza.getString("dIntime");
                    TrazaFolio[2] = rowTraza.getString("Planta");
                    TrazaFolio[3] = rowTraza.getString("iCasesPack");
                    TrazaFolio[4] = rowTraza.getString("vSKU");*/

                    alTraza.add(tli);
                }

                tv_Traz.setText(Mensaje);

                rvTrazaFolio.setLayoutManager(new LinearLayoutManager(nContext));
                rvTrazaFolio.setAdapter(new TrazaAdapter(alTraza));
            }
        }catch(Exception e){
            //new PopUp(nContext, "Error inesperado en trazabilidad", "Error al traer información del servidor"PopUp.POPUP_INFORMATION).showPopUp();
            Toast.makeText(nContext, "Error en obtener información de trazabilidad", Toast.LENGTH_SHORT).show();
        }
    }
}

