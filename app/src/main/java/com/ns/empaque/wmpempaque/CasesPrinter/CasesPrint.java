package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Desgrane.BoxesFolioInLine;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.R;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by jcalderon on 27/10/2016.
 */
public class CasesPrint {

   // public static RelativeLayout content;
    public static LayoutInflater inflater;
    public static Activity nContext;

    private static String[] codeSize;
    private static LinearLayout lytSites;
    private static Spinner spPlantas, spSites, spLine, spSize, spHora;
    private static String idFarmSelected="-1", idSiteSelected="-1", CodeSize="-1", horaSelected="-1", lineSelected="-1";
    private static ImageView seeFolios;
    private static ListView lv_foliosInline;
    public static adaptadorBoxesInLine adapBoxesLine;
    public static ArrayList<BoxesFolioInLine> BoxesInLine;
    public static int idCaseHeader;
    private static ArrayList<Integer> listaPlantasSRC;
    private static ArrayList<Integer> listaSizeSRC;
    private static ArrayList<Integer> listaLineaSRC;
    private static ArrayList<String> listaSitesSRC;
    public static SharedPreferences sharedPreferences;

    public CasesPrint(Activity c, RelativeLayout content){
        this.nContext = c;
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
       // this.content = content;
    }

    public static View setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_cases_print, null, true);
//        config.updateContent(content, v);

        spPlantas = (Spinner) v.findViewById(R.id.spPlant);
        lytSites = (LinearLayout) v.findViewById(R.id.lytSites);
        spSites = (Spinner) v.findViewById(R.id.spSite);
        spLine = (Spinner) v.findViewById(R.id.spLine);
        spSize = (Spinner) v.findViewById(R.id.spSize);
        spHora = (Spinner) v.findViewById(R.id.spHora);
        seeFolios = (ImageView) v.findViewById(R.id.btn_SeeFolios);
        lv_foliosInline = (ListView) v.findViewById(R.id.lv_foliosInline);

        BoxesInLine = new ArrayList<>();

        adapBoxesLine = new adaptadorBoxesInLine(nContext, BoxesInLine);

        seeFolios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncTaskGetBoxesFoliosinLine(config.rutaWebServerOmar+"/getFoliosFromLine", lineSelected).execute();
            }
        });

        loadPlantas();
        loadSize();
        loadHora();

        lv_foliosInline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Verificamos que se elegido una codigo del combo
                //if(CodeSize.compareToIgnoreCase("-1") != 0) {
                    final BoxesFolioInLine b = BoxesInLine.get(position);

                    if(b.getEstado()){
                        //final double totalLbs = (b.getLbsXBox() * b.getBoxesAvailable());

                        //Verificamos que ese folio haya sido elegido anteriormente.
                        final CaseCode ccp = generateCode(b);
                        BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();
                        final ArrayList<CaseCode> ccListCode = bd.cidb.getCasesCodeHeader(ccp.getCode());
                        Log.d("cc.getCode()", ccp.getCode()  + " | " + ccp.getFolio());
                        bd.cerrar();

                        //Si ya fue comenzado con cajas se pregunta, sino sigue de manera normal.
                        if(ccListCode.size() > 0){
                            String c = "";

                            for(int i = 0; i < ccListCode.size(); i++) {
                                CaseCode cc = ccListCode.get(i);
                                c += cc.getCode() + ",";
                            }

                            Toast.makeText(nContext, "Este folio tiene Codigos de cajas registradas, desea continuar registrando cases de este folio o empezar otro CaseLabel diferente?", Toast.LENGTH_LONG).show();
                            View v;
                            inflater = nContext.getLayoutInflater();
                            v = inflater.inflate(R.layout.vw_popup_foliosquestion, null, true);

                            TextView txtInfo = (TextView) v.findViewById(R.id.txtInfo);
                            TextView btnCancelText = (TextView) v.findViewById(R.id.btnCancelText);
                            TextView btnAcceptText = (TextView) v.findViewById(R.id.btnAcceptText);
                            LinearLayout btnAccept = (LinearLayout) v.findViewById(R.id.btnAccept);
                            LinearLayout btnCancel = (LinearLayout) v.findViewById(R.id.btnCanel);

                            txtInfo.setText("Este folio " + b.getvFolio() + ", tiene los siguientes Codigos de Caja registrados " + c + " desea continuar generando/registrando cases incrementables?");
                            btnAcceptText.setText("Continuar!");
                            btnCancelText.setText("Cancelar!");

                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
                            alertDialog2.setView(v);
                            alertDialog2.setCancelable(false);
                            final AlertDialog ad2 = alertDialog2.create();

                            ad2.show();

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ad2.dismiss();
                                }
                            });

                            btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final CaseCode cc = ccListCode.get(0);
                                    BaseDatos bd = new BaseDatos(nContext);
                                    bd.abrir();
                                    final ArrayList<caseIncrement> ciList = bd.ciddb.getCaseIncrementDetails(cc.getCode());
                                    bd.cerrar();

                                    ad2.dismiss();

                                    nContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AysnTaskSyncCaseHeader(config.rutaWebServerOmar + "/getCaseHeader", ccp, ccListCode, ciList, 1).execute();
                                        }
                                    });
                                }
                            });
                        } else {
                            nContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AysnTaskSyncCaseHeader(config.rutaWebServerOmar + "/getCaseHeader", ccp, new ArrayList<CaseCode>(), new ArrayList<caseIncrement>(), 0).execute();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(nContext, "NO SE PUEDEN GENERAR MÁS CASES DE ESTE FOLIO", Toast.LENGTH_SHORT).show();
                    }
                //} else {
                //    new PopUp(nContext, "Elija un tamaño por favor!", PopUp.POPUP_INFORMATION).showPopUp();
                //}

            }
        });

        return v;
    }

    public static void showGenerateCases(CaseCode ccp, ArrayList<CaseCode> ccList, ArrayList<caseIncrement> ciList, BoxesFolioInLine folioInLine, int Status){
        GenerateCases gc = new GenerateCases(nContext, ccp, ccList, ciList, Status, idCaseHeader, folioInLine);
        View vw = gc.getPopUpView();

        FloatingActionButton fabAtrasAddDialog = (FloatingActionButton) vw.findViewById(R.id.fabAtras);

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(vw);

        alertDialog2.setIcon(R.drawable.naturesweet);
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ad2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ad2.show();

        fabAtrasAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad2.dismiss();
            }
        });
    }

    private static CaseCode generateCode(BoxesFolioInLine b) {
        Calendar cal = Calendar.getInstance();
        DecimalFormat df =  new DecimalFormat("00");

        String fechaActual = cal.get(Calendar.YEAR)+"-"+(df.format(cal.get(Calendar.MONTH) + 1))+"-"+df.format(cal.get(Calendar.DAY_OF_MONTH));
        //String hour = cal.get(Calendar.HOUR_OF_DAY)+"";

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();

        ArrayList<HashMap<String, String>> a = bd.getWeek(fechaActual);

        bd.cerrar();

        HashMap<String, String> date = a.get(0);

        CaseCode cc = new CaseCode();

        cc.setCompany("D");
        cc.setSize(CodeSize);

        cc.setWeek(Integer.parseInt(date.get("WK")));
        cc.setDay(Integer.parseInt(date.get("DY")));
        //cc.setHour(Integer.parseInt(hour));
        cc.setHour(Integer.parseInt(horaSelected));

        cc.setIdGPLine(b.getLineNumber());
        cc.setGreenHouse(b.getGH().substring(1, b.getGH().length()));
        cc.setFarm(b.getFarmNumber());
        cc.setFolio(b.getvFolio());

        cc.setUUIDHeader(UUID.randomUUID().toString());
        cc.setSKU(b.getSKU());
        cc.setActive(1);

        bd.abrir();
        cc.setNombreLinea(bd.getNombreLinea(cc.getFarm(), cc.getIdGPLine()));
        cc.setIdLinePackage(Integer.parseInt(lineSelected));
        Log.d("idLinePackage", lineSelected);
        bd.cerrar();

        return cc;
    }

    private static void loadSize() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos [][] = bd.getSizeCode();
        bd.cerrar();

        if(datos.length > 0){
            codeSize = new String[datos.length];

            listaSizeSRC = new ArrayList<Integer>();

            for(int i = 0; i<datos.length; i++){
                codeSize[i] = datos[i][1];
                listaSizeSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, codeSize);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);

            spSize.setSelection(listaSizeSRC.indexOf(sharedPreferences.getInt("idSize", listaSizeSRC.get(0))));

            spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CodeSize  = codeSize[position];
                    config.actualizarSharedPreferencesInt(nContext, "idSize", listaSizeSRC.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }

    private static void loadHora() {
        final ArrayList<String> horas = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        DecimalFormat df =  new DecimalFormat("00");
        String hr = df.format(cal.get(Calendar.HOUR_OF_DAY));

        for(int i = 0; i < 24; i++)
            horas.add((i < 10) ? ("0" + i) : (i+""));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, horas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHora.setAdapter(adapter);

        spHora.setSelection(horas.indexOf(hr));

        spHora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                horaSelected  = horas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void loadPlantas() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.obtenerFarmsToPrePallet();
        bd.cerrar();

        if(datos.length > 0){
            String []namePlant = new String[datos.length];
            listaPlantasSRC = new ArrayList<Integer>();

            for(int i = 0; i<datos.length; i++) {
                namePlant[i] = datos[i][1];
                listaPlantasSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, namePlant);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPlantas.setAdapter(adapter);

            spPlantas.setSelection(listaPlantasSRC.indexOf(sharedPreferences.getInt("idPlanta", listaPlantasSRC.get(0))));

            spPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idFarmSelected = datos[position][0];

                    if(idFarmSelected.equals("3") || idFarmSelected.equals("9")){
                        lytSites.setVisibility(View.VISIBLE);
                        loadSites(datos[position][0]);
                    } else {
                        lytSites.setVisibility(View.GONE);
                        loadLines(datos[position][0], "", false);
                    }

                    config.actualizarSharedPreferencesInt(nContext, "idPlanta", listaPlantasSRC.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

        }else{
            Toast.makeText(nContext, "No hay plantas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    private static void loadSites(String idFarm) {
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
            spSites.setAdapter(adapter);

            spSites.setSelection(listaSitesSRC.indexOf(sharedPreferences.getString("idSite", listaSitesSRC.get(0))));
        } else {
            idSiteSelected = "-1";
            String []emptyData = new String[1];

            emptyData[0] = "No hay datos";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, emptyData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spSites.setAdapter(adapter);
            Toast.makeText(nContext, "No hay Lineas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

        spSites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    idSiteSelected = sites[position];

                    loadLines(idFarmSelected, idSiteSelected, true);

                    BoxesInLine.clear();
                    adapBoxesLine.notifyDataSetChanged();
                    config.actualizarSharedPreferencesString(nContext, "idSite", listaSitesSRC.get(position));
                }catch(Exception e){
                    idSiteSelected = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void loadLines(String idFarm, String idSite, boolean isUSA) {
        BaseDatos bd = new BaseDatos(nContext);
        final String[][] datos;

        bd.abrir();
        if(isUSA)
            datos = bd.getLinePackage(idFarm, idSite);
        else
            datos = bd.getLinePackage(idFarm);
        bd.cerrar();

        if(datos.length > 0){
            String []nameLine = new String[datos.length];

            listaLineaSRC = new ArrayList<Integer>();

            for(int i = 0; i<datos.length; i++) {
                nameLine[i] = datos[i][1];
                listaLineaSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, nameLine);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLine.setAdapter(adapter);

            spLine.setSelection(listaLineaSRC.indexOf(sharedPreferences.getInt("idLinea", listaLineaSRC.get(0))));
        } else {
            lineSelected = "-1";
            String []emptyData = new String[1];

            emptyData[0] = "No hay datos";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, emptyData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spLine.setAdapter(adapter);
            Toast.makeText(nContext, "No hay Lineas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

        spLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    lineSelected = datos[position][0];
                    BoxesInLine.clear();
                    adapBoxesLine.notifyDataSetChanged();
                    config.actualizarSharedPreferencesInt(nContext, "idLinea", listaLineaSRC.get(position));
                }catch(Exception e){
                    lineSelected = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    //ASYNCTASK TO CHECK FOLIO'S BOXES AVAILABLE IN LINE
    /**********************************************************/
    private static class asyncTaskGetBoxesFoliosinLine extends AsyncTask<String, String, String> {

        public String url, idLine;
        ProgressDialog pd;
        int fin = 0;

        public asyncTaskGetBoxesFoliosinLine(String url, String idLine) {
            this.url = url;
            this.idLine = idLine;
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
            String jsoncadena = "", step = "0";

            Log.d("idLine", this.idLine);

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLine", this.idLine));
                params.add(new BasicNameValuePair("sku", ""));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                httppostreq.setEntity(new UrlEncodedFormEntity(params));

                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 50000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 50000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                final HttpClient Client = new DefaultHttpClient(httpParameters);

                step = "4";
                HttpResponse httpresponse = Client.execute(httppostreq);
                step = "5";
                jsoncadena = EntityUtils.toString(httpresponse.getEntity());
                step = "6";
                this.fin = 6;
            } catch (Exception t) {
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute (String res) {
            BoxesInLine.clear();

            Log.d("iDWebMeth -- >", res);
            Log.e("iDWebMeth -- >", res);
            Log.i("iDWebMeth -- >", res);

            try {
                pd.dismiss();
            } catch (Exception e) {
                Log.e("Error", "" + e.getMessage());
            }

            try {
                Log.d("",res);
                JSONObject OBJJS = new JSONObject(res);
                JSONArray JSONBoxesInLine = OBJJS.getJSONArray("table1");
                double librasDisponibles;
                double librasCasesGenerados;

                if(JSONBoxesInLine.length() > 0) {
                    for (int i = 0; i < JSONBoxesInLine.length(); i++) {
                        BoxesFolioInLine bl = new BoxesFolioInLine();
                        JSONObject row = JSONBoxesInLine.getJSONObject(i);

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

                        BoxesInLine.add(bl);
                    }
                    //Toast.makeText(nContext, "llenó", Toast.LENGTH_LONG).show();//this, android.R.layout.simple_list_item_1,eje

                    //Llenar lista de cajas en linea
                    adapBoxesLine = new adaptadorBoxesInLine(nContext, BoxesInLine);
                    lv_foliosInline.setAdapter(adapBoxesLine);
                }
            } catch (Exception e) {
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos", e.getMessage());
            }

            if(this.fin < 6){
                Log.d("iDWebMeth -- >", "No llego al final");
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - No alcanzó el servicio Web", Toast.LENGTH_LONG).show();
                new PopUp(nContext, "-No se alcanzó el servicio Web\n- Asegurate de estar conectado a internet Por favor!\n- Si estás conectado a Internet y el problema consiste contacto al administrador de la aplicación.", PopUp.POPUP_INFORMATION).showPopUp();
            }
        }
    }

    private static class AysnTaskSyncCaseHeader extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialogData;
        private String URL;
        private CaseCode cc;
        private ArrayList<CaseCode> ccListCode;
        private ArrayList<caseIncrement> ciList;
        private String caseHeaderJSON;
        private int Status;

        public AysnTaskSyncCaseHeader(String url, CaseCode cc, ArrayList<CaseCode> ccListCode, ArrayList<caseIncrement> ciList, int Status) {
            this.URL = url;

            progressDialogData = new ProgressDialog(nContext);
            this.cc = cc;
            this.ccListCode = ccListCode;
            this.ciList = ciList;
            this.Status = Status;

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

                showGenerateCases(cc, ccListCode, ciList, bl, Status);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(nContext, "No hay conexión con el web service. Revise su conexión a Internet", Toast.LENGTH_LONG).show();
            }

            progressDialogData.dismiss();
        }
    }
}
