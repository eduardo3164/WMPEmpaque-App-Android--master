package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Desgrane.BoxesFolioInLine;
import com.ns.empaque.wmpempaque.Modelo.config;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by jcalderon on 16/11/2016.
 */
public class CasesList {

    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static Activity nContext;
    private static FloatingActionButton fabAtras;
    private static LinearLayout lytDescargarCases;
    private static ImageView btn_AddCase;
    private static ListView lv_CasesLocalDB;
    private static casesListAdapter adapListCases;
    private static ArrayList<CaseCode> ccList;
    public static int idCaseHeader;

    private static DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private static SimpleDateFormat dateFormatter;
    private static Calendar calendario;
    private static boolean fechaInicio;
    private static String idFarmSelected, idSiteSelected;
    private static ArrayList<Integer> listaPlantasSRC;
    private static ArrayList<String> listaSitesSRC;
    private static LinearLayout lytSiteDCH;
    private static EditText txtFechaInicio;
    private static EditText txtFechaFin;
    private static Spinner cboxPlanta;
    private static Spinner cboxSite;
    private static ListView listaLineas;
    private static boolean lineasSeleccionadas[];
    private static ArrayList<Linea> selectedItems;
    public static SharedPreferences sharedPreferences;

    public CasesList(Activity c, RelativeLayout content){
        this.nContext = c;
        this.content = content;
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
    }

    public static void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_cases_list, null, true);
        config.updateContent(content, v);

        lytDescargarCases = (LinearLayout) v.findViewById(R.id.lytDescargarCases);
        fabAtras = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        btn_AddCase = (ImageView) v.findViewById(R.id.add_Case);
        lv_CasesLocalDB = (ListView) v.findViewById(R.id.lv_CasesLocalDB);

        fillList();

        lytDescargarCases.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mostrarDialogoDescargar();
            }
        });

        btn_AddCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddCases();
            }
        });

        fabAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        lv_CasesLocalDB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CaseCode cc = ccList.get(position);

                BaseDatos bd = new BaseDatos(nContext);

                bd.abrir();
                final ArrayList<caseIncrement> ciList = bd.ciddb.getCaseIncrementDetails(cc.getCode());
                final ArrayList<CaseCode> ccListCode = bd.cidb.getCasesCodeHeader(cc.getCode());
                bd.cerrar();

                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AysnTaskSyncCaseHeader(config.rutaWebServerOmar + "/getCaseHeader", cc, ccListCode, ciList).execute();
                    }
                });
            }
        });
    }

    public static void reFreshList(Activity nContext){
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        ccList = bd.cidb.getCasesCodeHeader();
        bd.cerrar();

        adapListCases.notifyDataSetChanged();
    }

    public static void fillList() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        ccList = bd.cidb.getCasesCodeHeader();
        bd.cerrar();

        adapListCases = new casesListAdapter(nContext, ccList);
        lv_CasesLocalDB.setAdapter(adapListCases);
    }

    private static void showDialogAddCases(){
        CasesPrint cp = new CasesPrint(nContext, content);
        View vw = cp.setView();

        FloatingActionButton fabAtrasAddDialog = (FloatingActionButton) vw.findViewById(R.id.fabAtras);

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(vw);

        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ad2.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ad2.show();

        fabAtrasAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad2.dismiss();
                fillList();
            }
        });
    }/////////////////////////////////////// MéTODO /////////////////////////////////

    public static void mostrarDialogoDescargar(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.vw_descargar_cases_header, null);

        txtFechaInicio = (EditText) dialoglayout.findViewById(R.id.txtFInicioDCH);
        txtFechaFin = (EditText) dialoglayout.findViewById(R.id.txtFFinDCH);
        cboxPlanta = (Spinner) dialoglayout.findViewById(R.id.cboxPlantaDCH);
        lytSiteDCH = (LinearLayout) dialoglayout.findViewById(R.id.lytSiteDCH);
        cboxSite = (Spinner) dialoglayout.findViewById(R.id.cboxSiteDCH);
        listaLineas = (ListView) dialoglayout.findViewById(R.id.listaLineasDCH);

        calendario = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        txtFechaInicio.setText(calendario.get(Calendar.YEAR) + "-" + (((calendario.get(Calendar.MONTH) + 1) < 10) ? ("0" + (calendario.get(Calendar.MONTH) + 1)) : ((calendario.get(Calendar.MONTH) + 1))) + "-" + (((calendario.get(Calendar.DAY_OF_MONTH)) < 10) ? ("0" + (calendario.get(Calendar.DAY_OF_MONTH))) : ((calendario.get(Calendar.DAY_OF_MONTH)))));
        txtFechaFin.setText(calendario.get(Calendar.YEAR) + "-" + (((calendario.get(Calendar.MONTH) + 1) < 10) ? ("0" + (calendario.get(Calendar.MONTH) + 1)) : ((calendario.get(Calendar.MONTH) + 1))) + "-" + (((calendario.get(Calendar.DAY_OF_MONTH)) < 10) ? ("0" + (calendario.get(Calendar.DAY_OF_MONTH))) : ((calendario.get(Calendar.DAY_OF_MONTH)))));

        txtFechaInicio.setInputType(InputType.TYPE_NULL);
        txtFechaFin.setInputType(InputType.TYPE_NULL);

        txtFechaInicio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fechaInicio = true;
                fromDatePickerDialog.show();
            }
        });

        txtFechaFin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fechaInicio = false;
                toDatePickerDialog.show();
            }
        });

        fromDatePickerDialog = new DatePickerDialog(nContext, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                establecerFecha(year, monthOfYear, dayOfMonth);
            }
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(nContext, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                establecerFecha(year, monthOfYear, dayOfMonth);
            }
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        llenarSpinnerPlanta();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        alertDialog.setView(dialoglayout);
        alertDialog.setIcon(R.drawable.descargar);
        alertDialog.setTitle("Descargar Cases");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String fechaInicio = txtFechaInicio.getText().toString();
                String fechaFin = txtFechaFin.getText().toString();
                String idPlanta = idFarmSelected;

                String idLineas = "";

                for(int j = 0; j < selectedItems.size(); j++)
                    idLineas += selectedItems.get(j).getIdLinea() + ",";

                if(selectedItems.size() > 0){
                    idLineas = idLineas.substring(0, idLineas.length() - 1);
                    new AysnTaskDescargarCasesHeader(config.rutaWebServerOmar + "/descargarCaseHeader", idPlanta, idLineas, fechaInicio, fechaFin).execute();
                }
                else
                    Toast.makeText(nContext, "No ha seleccionado líneas", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog ad = alertDialog.create();
        ad.show();
    }

    public static void establecerFecha(int year, int monthOfYear, int dayOfMonth){
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);

        if(fechaInicio)
            txtFechaInicio.setText(dateFormatter.format(cal.getTime()));
        else
            txtFechaFin.setText(dateFormatter.format(cal.getTime()));
    }

    private static void llenarSpinnerPlanta() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.obtenerFarmsToPrePallet();
        bd.cerrar();

        if(datos.length > 0){
            String[] namePlant = new String[datos.length];
            listaPlantasSRC = new ArrayList<Integer>();

            for(int i = 0; i < datos.length; i++) {
                namePlant[i] = datos[i][1];
                listaPlantasSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, namePlant);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxPlanta.setAdapter(adapter);

            cboxPlanta.setSelection(listaPlantasSRC.indexOf(sharedPreferences.getInt("idPlanta", listaPlantasSRC.get(0))));

            cboxPlanta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idFarmSelected = datos[position][0];

                    if(idFarmSelected.equals("3") || idFarmSelected.equals("9")){
                        lytSiteDCH.setVisibility(View.VISIBLE);
                        llenarSpinnerSites(datos[position][0]);
                    } else {
                        lytSiteDCH.setVisibility(View.GONE);
                        llenarListaLinea(datos[position][0], "", false);
                    }

                    config.actualizarSharedPreferencesInt(nContext, "idPlanta", listaPlantasSRC.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

        } else {
            Toast.makeText(nContext, "No hay plantas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    private static void llenarSpinnerSites(String idFarm) {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[] sites = bd.getSitesFarm(idFarm);
        bd.cerrar();

        if(sites.length > 0){
            listaSitesSRC = new ArrayList<String>();

            for(int i = 0; i < sites.length; i++) {
                listaSitesSRC.add(sites[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, sites);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxSite.setAdapter(adapter);

            cboxSite.setSelection(listaSitesSRC.indexOf(sharedPreferences.getString("idSite", listaSitesSRC.get(0))));
        } else {
            idSiteSelected = "-1";
            String []emptyData = new String[1];

            emptyData[0] = "No hay datos";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, emptyData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            cboxSite.setAdapter(adapter);
            Toast.makeText(nContext, "No hay Lineas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

        cboxSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    idSiteSelected = sites[position];

                    llenarListaLinea(idFarmSelected, sites[position], true);

                    config.actualizarSharedPreferencesString(nContext, "idSite", listaSitesSRC.get(position));
                }catch(Exception e){
                    idSiteSelected = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void llenarListaLinea(String idFarm, String site, boolean isUSA) {
        BaseDatos bd = new BaseDatos(nContext);
        final String[][] datos;

        bd.abrir();
        if(isUSA)
            datos = bd.getLinePackage(idFarm, site);
        else
            datos = bd.getLinePackage(idFarm);
        bd.cerrar();

        if(datos.length > 0){
            String[] nameLine = new String[datos.length];
            lineasSeleccionadas = new boolean[datos.length];

            for(int i = 0; i < datos.length; i++) {
                nameLine[i] = datos[i][0] + " - " + datos[i][1];
                lineasSeleccionadas[i] = false;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_multiple_choice, nameLine);
            listaLineas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listaLineas.setAdapter(adapter);
        } else {
            vaciarListaLineas();
            Toast.makeText(nContext, "No hay Lineas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

        listaLineas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!lineasSeleccionadas[position]){
                    lineasSeleccionadas[position] = true;
                    parent.getChildAt(position).setBackgroundColor(Color.parseColor("#2ECCFA"));
                } else {
                    lineasSeleccionadas[position] = false;
                    parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                }

                selectedItems = obtenerLineasSeleccionadas(datos);
            }
        });
    }

    private static void vaciarListaLineas(){
        String vacio[] = new String[0];

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_multiple_choice, vacio);
        listaLineas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listaLineas.setAdapter(adapter);
    }

    private static ArrayList<Linea> obtenerLineasSeleccionadas(String datos[][]){
        SparseBooleanArray checked = listaLineas.getCheckedItemPositions();
        ArrayList<Linea> selectedItems = new ArrayList<Linea>();

        for(int i = 0; i < checked.size(); i++) {
            int index = checked.keyAt(i);

            if(checked.valueAt(i)) {
                Linea l = new Linea();

                l.setIdLinea(Integer.parseInt(datos[index][0]));
                l.setNombreLinea(datos[index][1]);
                l.setActive(true);
                l.setIdGPLinea(0);

                selectedItems.add(l);
            }
        }

        return selectedItems;
    }

    /*public static void sinc(){
        String JSONCaseHeader = syncCasesHeaderAndDetails.getJSONHeader(nContext);
        String JSONCaseDetails = syncCasesHeaderAndDetails.getJSONDetails(nContext);

        if(JSONCaseHeader == null && JSONCaseDetails == null)
            new PopUp(nContext, "1.- Tolos los datos estan sincronizados." + "\n2.- Sin elementos para sincronizar.", PopUp.POPUP_INFORMATION).showPopUp();
        else
            new syncCasesHeaderAndDetails.ATSendTable(config.rutaWebServerOmar+"/insertCasesHeaderAndDetails", nContext, JSONCaseHeader, JSONCaseDetails).execute();

        //new syncCasesHeaderAndDetails.ATSendTable("").execute();//
    }*/

    public static void des(){
        /*tvSin.setText("Sincronizar   ");
        tvSin.setTextColor(Color.parseColor("#000000"));
        btnSinc.setEnabled(true);*/
    }

    public static void showGenerateCases(CaseCode cc, ArrayList<CaseCode> ccListCode, ArrayList<caseIncrement> ciList, BoxesFolioInLine folioInLine, int Status){
        GenerateCases gc = new GenerateCases(nContext, cc, ccListCode, ciList, Status, idCaseHeader, folioInLine);
        View vw = gc.getPopUpView();

        FloatingActionButton fabAtrasAddDialog = (FloatingActionButton) vw.findViewById(R.id.fabAtras);

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2.setView(vw);

        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ad2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ad2.show();

        gc.casesAGenerar();

        fabAtrasAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad2.dismiss();
            }
        });
    }

    private static class AysnTaskSyncCaseHeader extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialogData;
        private String URL;
        private CaseCode cc;
        private ArrayList<CaseCode> ccListCode;
        private ArrayList<caseIncrement> ciList;
        private String caseHeaderJSON;

        public AysnTaskSyncCaseHeader(String url, CaseCode cc, ArrayList<CaseCode> ccListCode, ArrayList<caseIncrement> ciList) {
            this.URL = url;

            progressDialogData = new ProgressDialog(nContext);
            this.cc = cc;
            this.ccListCode = ccListCode;
            this.ciList = ciList;

            caseHeaderJSON =  "[{\"vCodeCase\":\""      + cc.getCode() + "\"," +
                                "\"vGreenHouse\":\""    + cc.getGreenHouse() + "\"," +
                                "\"vFolio\":\""         + cc.getFolio() + "\"," +
                                "\"vSKU\":\""           + cc.getSKU() + "\"," +
                                "\"vSize\":\""          + cc.getSize() + "\"," +
                                "\"iLineNumber\":\""    + cc.getIdGPLine() + "\"," +
                                "\"iLinePackage\":\""   + cc.getIdLinePackage() + "\"," +
                                "\"iFarm\":\""          + cc.getFarm() + "\"," +
                                "\"vCompany\":\""       + cc.getCompany() + "\"," +
                                "\"iWeek\":\""          + cc.getWeek() + "\"," +
                                "\"vHour\":\""          + cc.getHour() + "\"," +
                                "\"vDay\":\""           + cc.getDay() + "\"," +
                                "\"vUUID\":\""          + cc.getUUIDHeader() + "\"," +
                                "\"bActive\":\""        + cc.getActive() + "\"," +
                                "\"CreatedDate\":\""    + config.obtenerFechaHora() + "\"," +
                                "\"CreatedUser\":\""    + "" + "\"," +
                                "\"UpdateDate\":\""     + config.obtenerFechaHora() + "\"," +
                                "\"UpdateUser\":\""     + "" + "\"}]";

            Log.d("caseHeaderJSON", caseHeaderJSON);
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

            try {
                Log.d("caseHeader", cc.getCode());
                Log.d("Folio", cc.getFolio());
                Log.d("SKU", cc.getSKU());

                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("caseHeaderJSON", caseHeaderJSON));
                params.add(new BasicNameValuePair("caseHeaderCODE", cc.getCode()));
                params.add(new BasicNameValuePair("caseHeaderFOLIO", cc.getFolio()));
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
                JSONArray resultadoCasesHeaderJSON;
                JSONArray resultadoCasesDetailsJSON;
                JSONArray resultadoFolioInLineJSON;
                JSONObject row;

                resultadoCasesHeaderJSON = json.optJSONArray("table1");
                resultadoCasesDetailsJSON = json.optJSONArray("table2");
                resultadoFolioInLineJSON = json.optJSONArray("table3");

                for (int i = 0; i < resultadoCasesHeaderJSON.length(); i++) {
                    row = resultadoCasesHeaderJSON.getJSONObject(i);

                    idCaseHeader = row.getInt("idCaseCodeHeader");
                    cc.setGreenHouse(row.getString("vGreenHouse"));
                    cc.setFolio(row.getString("vFolio"));
                    cc.setSKU(row.getString("vSKU"));
                    cc.setSize(row.getString("vSize"));
                    cc.setIdGPLine(row.getInt("idLineGP"));
                    cc.setIdLinePackage(row.getInt("idLinePackage"));
                    cc.setFarm(row.getInt("idFarm"));
                    cc.setCompany(row.getString("vCompany"));
                    cc.setWeek(row.getInt("iWeek"));
                    cc.setHour(row.getInt("vHour"));
                    cc.setDay(row.getInt("vDay"));
                    cc.setUUIDHeader(row.getString("UUIDHeader"));
                    cc.setSync(1);
                    cc.setSKU(cc.getSKU());
                }

                ciList.clear();
                caseIncrement ci;

                for (int i = 0; i < resultadoCasesDetailsJSON.length(); i++) {
                    row = resultadoCasesDetailsJSON.getJSONObject(i);

                    ci = new caseIncrement();

                    ci.setCaseCode(row.getString("vCodeCase"));
                    ci.setUUID(row.getString("vUUID"));
                    ci.setUUIDHeader(row.getString("vUUIDHEADER"));
                    ci.setActive(row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                    ci.setSync(1);
                    ci.setFolio(row.getString("vFolio"));
                    ci.setSKU(row.getString("vSKU"));
                    ci.setCaseCodeHeader(row.getString("vCodeCaseHeader"));
                    ci.setCreatedDate(row.getString("dCreatedDate"));
                    ci.setCreatedUser(row.getString("vUserCreated"));
                    ci.setUpdateDate(row.getString("dUpdatedDate"));
                    ci.setUpdateUser(row.getString("vUserUpdate"));
                    ci.setSKU(cc.getSKU());

                    ciList.add(ci);
                }

                BoxesFolioInLine bl = new BoxesFolioInLine();
                double librasDisponibles;
                double librasCasesGenerados;

                for (int i = 0; i < resultadoFolioInLineJSON.length(); i++) {
                    row = resultadoFolioInLineJSON.getJSONObject(i);

                    bl.setIdProduct(row.getInt("idProducto"));
                    bl.setvFolio(row.getString("Folio"));
                    bl.setLineNumber(row.getInt("LineNmbr"));
                    bl.setLineName(row.getString("LineName"));
                    bl.setFechaEnterLine(row.getString("lineDintime"));
                    bl.setFarmNumber(row.getInt("plantNmbr"));
                    bl.setFarmName(row.getString("plantName"));
                    bl.setBoxesInLine(row.getInt("BoxesInLine"));
                    bl.setIdProductLog(row.getInt("idProductLog"));
                    bl.setSKU(row.getString("SKU"));
                    bl.setGH(row.getString("GH"));
                    bl.setLbsXBox(row.getDouble("LBSxBox"));
                    bl.setBoxesAvailable(row.getInt("boxesAvailable"));
                    bl.setTotalBoxes(row.getInt("TotalBoxes"));

                    bl.setCasesGenerados(row.getInt("casesGenerados"));
                    bl.setLbsPorSKU(row.getDouble("lbsPorSKU"));

                    librasDisponibles = bl.getLbsXBox() * bl.getBoxesAvailable();
                    librasCasesGenerados = bl.getCasesGenerados() * bl.getLbsPorSKU();

                    if((librasDisponibles - librasCasesGenerados) > 0)
                        bl.setEstado(true);
                    else
                        bl.setEstado(false);
                }

                showGenerateCases(cc, ccListCode, ciList, bl, 1);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(nContext, "No hay conexión con el web service. Revise su conexión a Internet", Toast.LENGTH_LONG).show();
            }

            progressDialogData.dismiss();
        }
    }

    private static class AysnTaskDescargarCasesHeader extends AsyncTask<String, String, String> {

        private String url, idPlanta, idLineas, fechaInicio, fechaFin;
        private ProgressDialog progressDialog;
        private boolean errorDownloadData = false;

        public AysnTaskDescargarCasesHeader(String url, String idPlanta, String idLineas, String fInicio, String fFin){
            this.url = url;
            this.idPlanta = idPlanta;
            this.idLineas = idLineas;
            this.fechaInicio = fInicio;
            this.fechaFin = fFin;

            progressDialog = new ProgressDialog(nContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Descargando... Por favor espere!");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="", step="0";

            try {
                step="1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("spName", "spr_descargarAuditorias"));
                params.add(new BasicNameValuePair("idPlanta", idPlanta));
                params.add(new BasicNameValuePair("idLineas", idLineas));
                params.add(new BasicNameValuePair("fInicio", fechaInicio));
                params.add(new BasicNameValuePair("fFin", fechaFin));
                step="2";
                HttpPost httppostreq = new HttpPost(url);
                step="3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));
                step="4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step="5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step="6";
            } catch (Exception t) {
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;
                errorDownloadData = true;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("Text -- >", res);
            JSONObject json;

            try {
                json = new JSONObject(res);
                JSONArray JSONarrayCasesHeader = json.optJSONArray("table1");

                BaseDatos bd = new BaseDatos(nContext);
                bd.abrir();

                if(JSONarrayCasesHeader != null){
                    bd.cidb.borrarTablaCasesIncrement();
                    bd.cidb.crearTablaCasesIncrement();

                    JSONObject row;
                    CaseCode ch;

                    for(int i = 0; i < JSONarrayCasesHeader.length(); i++){
                        row = JSONarrayCasesHeader.getJSONObject(i);
                        ch = new CaseCode();

                        ch.setGreenHouse(row.getString("vGreenHouse"));
                        ch.setFolio(row.getString("vFolio"));
                        ch.setSize(row.getString("vSize"));
                        ch.setIdGPLine(row.getInt("idLineGP"));
                        ch.setIdLinePackage(row.getInt("idLinePackage"));
                        ch.setNombreLinea(row.getString("vLine"));
                        ch.setFarm(row.getInt("idFarm"));
                        ch.setCompany(row.getString("vCompany"));
                        ch.setSKU(row.getString("vSKU"));
                        ch.setWeek(row.getInt("iWeek"));
                        ch.setHour(row.getInt("vHour"));
                        ch.setDay(row.getInt("vDay"));
                        ch.setUUIDHeader(row.getString("UUIDHeader"));
                        ch.setCreatedDate(row.getString("dCreatedDate"));
                        ch.setUpdateDate(row.getString("dUpdatedDate"));
                        ch.setCreatedUser(row.getString("vCreateUser"));
                        ch.setUpdateUser(row.getString("vUpdatedUser"));
                        ch.setSync(1);

                        bd.cidb.insertCaseIncrementHeader(ch);
                    }
                } else {
                    Toast.makeText(nContext, "Sin resultados", Toast.LENGTH_SHORT).show();
                }

                bd.cerrar();

            } catch (JSONException ex) {
                //ex.printStackTrace();
                errorDownloadData = true;
                Toast.makeText(nContext, "Error al conectarse al web service", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();

            if(!errorDownloadData){
                fillList();
            } else {
                Toast.makeText(nContext, "Error a descargar cases", Toast.LENGTH_SHORT).show();
                //mostrarNotificacion(2l, getApplicationContext().getResources().getString(R.string.error_download));
            }
        }
    }
}
