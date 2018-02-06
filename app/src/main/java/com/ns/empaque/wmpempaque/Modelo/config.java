package com.ns.empaque.wmpempaque.Modelo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import com.ns.empaque.wmpempaque.QrManagement.qrManagement;
//import com.ns.empaque.wmpempaque.locationManagement.locatsManagement;

/**
 * Created by jcalderon on 29/12/2015.
 * MEJORAS DE LA NUEVA VERSIÓN
 *  SE CORRIGIÓ EL BUG DEL CIERRE DE LA APLICACIÓN EN EL MAPEO.
 *  MUESTRA UN DIALOG MAS GRAFICO AL MOMENTO QUE SE REALICE UNA ACCIÓN CORRECTAMENTE O NO.
 *  SE MEJORO INTERFAZ GRAFICA.
 *
 */
public class config {
    public static double versionApp = 1.34;
    public static int versionDB = 1;
    //public static String rutaWebServerOmar="http://192.168.167.179:7894/WMPEmpaqueWS/WS/WMPWebService.asmx";
    //public static String rutaWebServerOmar="http://192.168.167.63:1111/WebServices/WebServices.asmx";
    //public static String rutaWebServerOmar="http://empaque.naturesweet.com.mx/WebServices/WebServices.asmx";
    //public static String rutaWebServerOmar="http://192.168.167.130:1573/WebServices/WebServices.asmx";

//    public static String rutaWebServerOmar="http://192.168.167.191:1570/WMPEmpaqueWS/WS/WMPWebService.asmx";
//    public static String rutaWebServerOmar="http://192.168.167.191:1570/WebServices/WebServices.asmx";

//    TEST MÉXICO
//    public static String rutaWebServerOmar="http://192.168.167.191:555/WMPEmpaqueWS/WS/WMPWebService.asmx";
//    public static String pathEmpaqueQAIB = "http://192.168.167.191:555/pages/Quality/FrmQAIBMovil.aspx";

//    PRODUCCIÓN MÉXICO
    public static String rutaWebServerOmar="http://empaque.naturesweet.com.mx/WMPEmpaqueWS/WS/WMPWebService.asmx";
    public static String pathEmpaqueQAIB = "http://empaque.naturesweet.com.mx/pages/Quality/FrmQAIBMovil.aspx";

//    PRODUCCIÓN ARIZONA
//    public static String rutaWebServerOmar="http://empaqueusa.naturesweet.com/WMPEmpaqueWS/WS/WMPWebService.asmx";
//    public static String pathEmpaqueQAIB = "http://empaqueusa.naturesweet.com/pages/Quality/FrmQAIBMovil.aspx";


    //public static String WS="http://192.168.167.191:1570/ws/ws/wmpwebservice.asmx";
    //public static String rutaWebServerJavier="http://192.168.56.1/Riego250116/Webservice/riegoServerWeb.asmx";

    public static String razonDesgrane [] = { "Sin desgrane", "Desgrane por venta", "Desgrane logistic", "Desgrane calidad" };
    public static String mesesEN[] = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dec" };

    public static void updateContent(RelativeLayout content, View v){
        content.removeAllViews();
        content.setBackgroundResource(R.color.blanco);
        content.addView(v);
    }

    public static void updateContent(LinearLayout content, View v){
        content.removeAllViews();
        content.setBackgroundResource(R.color.blanco);
        content.addView(v);
    }

    public static void backContent(RelativeLayout content){
        content.removeAllViews();
        content.setBackgroundResource(R.drawable.worldns);
    }

    public static int validaString( String cadena ){
        /**************REGULAR EXPRESSION'S
         *** RETORNOS ***
         * 1 = FOLIO - done
         * 2 = PALLET -  done
         * 3 = CASE
         * 4 = LOCATION
         * 0 = ERROR - done
         * **********************/

        //Comprobar si el String cadena es folio
        if (cadena.matches("^[0-9]{3,4}-[0-9a-zA-Z]+"))
            return 1;
        else//Comprobar si la cadena es un pallet
            if(cadena.matches("^[0-9]{5}[ZD][0-9]{4}[SMLX][0-9]{2}[A-Z]{1}"))
                return 2;
            else
                if(cadena.matches("^[0-9]+[|][0-9]+[.][0-9]+[.][0-9]+$") )
                    return 4;
                else
                    if(cadena.matches("^[A-Z]{2}[0-9]{6}[A-Z][0-9]{3}[0-9]{3,}$") )
                        return 3;//case
                    else
                        return 0;



    }

    public static int validaString( String cadena, Activity nContext ){
        /**************REGULAR EXPRESSION'S
         *** RETORNOS ***
         * 1 = FOLIO - done
         * 2 = PALLET -  done
         * 3 = CASE
         * 4 = LOCATION
         * 7 = QA USER
         * 0 = ERROR - done
         * **********************/

        BaseDatos bd  = new BaseDatos(nContext);
        bd.abrir();
            String idEmbalaje = bd.getEmbalaje(cadena);
        bd.cerrar();

        try {
            return Integer.parseInt(idEmbalaje);
        }
        catch(Exception ex){
            return 0;
        }

        //Comprobar si el String cadena es folio
       /* if (cadena.matches("^[0-9]{3,4}-[0-9a-zA-Z]+"))
            return 1;
        else//Comprobar si la cadena es un pallet
            if(cadena.matches("^[0-9]{5}[ZD][0-9]{4}[SMLX][0-9]{2}[A-Z]{1}"))
                return 2;
            else
            if(cadena.matches("^[0-9]+[|][0-9]+[.][0-9]+[.][0-9]+$") )
                return 4;
            else
            if(cadena.matches("^[A-Z]{2}[0-9]{6}[A-Z][0-9]{3}[0-9]{3,}$") )
                return 3;//case
            else
                return 0;*/



    }


    public static String validaEmbalaje(String embalaje, Activity nContext){
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
            String idEmbalaje = bd.getEmbalaje(embalaje);
        bd.cerrar();

        return idEmbalaje;
    }

    public static void syncLocations(Context nContext ,View view){

     /*   try {
            BaseDatos bd = new BaseDatos(nContext);
            bd.abrir();
            String[][] datosLocat = bd.getQrLocation_to_sync();
            bd.cerrar();


            if (datosLocat.length > 0) {
                String resultsJSON="[";
                for (int i = 0; i < datosLocat.length; i++)
                    resultsJSON += "{\"idLocatServer\":" + (datosLocat[i][0] != null ? "\"" + datosLocat[i][0] + "\"" : null) +
                            ", \"vName\":\"" + datosLocat[i][1] +
                            "\", \"iWidth\":\"0" +
                            "\", \"iHeight\":\"0" +
                            "\", \"active\":\"" + datosLocat[i][2] +
                            "\", \"vDescripWarehouse\":\""+ datosLocat[i][3] +
                            "\", \"bWaste\":\"0"+
                            "\", \"id_Plant\":\"" +datosLocat[i][4] +
                            "\", \"iWorkingFor\":\"" +datosLocat[i][5] +
                            "\", \"iTypeWarehouse\":\"" + datosLocat[i][6] + "\"},";

                resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
                resultsJSON += "]";

                Toast.makeText(nContext, resultsJSON, Snackbar.LENGTH_LONG).show();

                new AysnTaskGetTables(config.rutaWebServerOmar+"/insertCatWareHouse",(Activity) nContext, resultsJSON, 2).execute();
            }else{
                Snackbar.make(view, "No hay locaciones para syncronizar.", Snackbar.LENGTH_LONG).show();
            }


        }catch(Exception e){
            Snackbar.make(view, "Error -- >"+e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
*/
    }

    public static void syncronizar(Context nContext) {

     /*   ProgressDialog pd = new ProgressDialog(nContext);

        pd.setIndeterminate(true);
        pd.setMessage("Cargando...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();*/
      /*  try {

            BaseDatos bd = new BaseDatos(nContext);
            bd.abrir();
            String[][] datosQrManager = bd.getQrCode_to_sync();
            String[][] datosQrLocation = bd.getQrLocation_to_sync();
            bd.cerrar();

            String resultsJSON="{";

            //SI HAY DATOS EN LA TABLA DE QR MANAGER
            if (datosQrManager.length > 0) {
                resultsJSON += "\"tbl_QRManager\":[";
                for (int i = 0; i < datosQrManager.length; i++)
                    resultsJSON += "{\"idQrServer\":" + (datosQrManager[i][0] != null ? "\"" + datosQrManager[i][0] + "\"" : null) +
                            ", \"idQr\":\"" + datosQrManager[i][1] +
                            "\", \"qrCode\":\"" + datosQrManager[i][2] +
                            "\", \"Dato\":\"" + datosQrManager[i][3] +
                            "\", \"tipo\":\"" + datosQrManager[i][4] +
                            "\", \"active\":\"" + datosQrManager[i][5] +
                            "\", \"fechaRegistro\":\"" + datosQrManager[i][6] +
                            "\", \"userCreated\":" + (datosQrManager[i][7] != null ? "\"" + datosQrManager[i][7] + "\"" : "\""+"jcalderon"+"\"") +
                            ", \"fechaActualizacion\":" + (datosQrManager[i][8] != null ? "\"" + datosQrManager[i][8] + "\"" : null) +
                            ", \"userUpdated\":" + (datosQrManager[i][9] != null ? "\"" + datosQrManager[i][9] + "\"" : null) +
                            ", \"idTablet\":\"" + datosQrManager[i][10] + "\"},";

                resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
                resultsJSON += "],";

            }

            ///SI HAY DATOS EN LA TABLA DE QR LOCATIONS*
           /* if(datosQrLocation.length > 0){
                resultsJSON += "\"tbl_QRLocation\":[";
                for (int i = 0; i < datosQrLocation.length; i++)
                    resultsJSON += "{\"idQrLocationServer\":" + (datosQrLocation[i][0] != null ? "\"" + datosQrLocation[i][0] + "\"" : null) +
                            ", \"idQrLocation\":\"" + datosQrLocation[i][1] +
                            "\", \"locationcCode\":\"" + datosQrLocation[i][2] +
                            "\", \"Farm\":\"" + datosQrLocation[i][3] +
                            "\", \"type\":\"" + datosQrLocation[i][4] +
                            "\", \"Site\":\"" + datosQrLocation[i][5] +
                            "\", \"active\":\"" + datosQrLocation[i][6] +
                            "\", \"descripcion\":\"" + datosQrLocation[i][7] +
                            "\", \"fechaRegistro\":\"" + datosQrLocation[i][8] +
                            "\", \"userCreated\":" + (datosQrLocation[i][9] != null ? "\"" + datosQrLocation[i][9] + "\"" : "\""+"jcalderon"+"\"") +
                            ", \"fechaActualizacion\":" + (datosQrLocation[i][10] != null ? "\"" + datosQrLocation[i][10] + "\"" : null) +
                            ", \"userUpdated\":" + (datosQrLocation[i][11] != null ? "\"" + datosQrLocation[i][11] + "\"" : null) +
                            ", \"idTablet\":\"" + datosQrLocation[i][12] + "\"},";

                resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
                resultsJSON += "],";
            }

            resultsJSON = resultsJSON.substring(0, resultsJSON.length() - 1);
            resultsJSON += "}";

            Toast.makeText(nContext, resultsJSON, Toast.LENGTH_LONG).show();
            Log.d("JSONResultado -- >", resultsJSON);

            //new AysnTask(config.rutaWebServer + "/Insert_DataTable", resultsJSON, nContext).execute();

        }catch(Exception e){
            Log.e("Error -- >", "Config - " + e.getMessage());
        }*/
    }

    public static String obtenerMACAddress(Context c){
        WifiManager wifiManager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()) {
            // WIFI ALREADY ENABLED. GRAB THE MAC ADDRESS HERE
            WifiInfo info = wifiManager.getConnectionInfo();
            String address = info.getMacAddress();
            return address;
        } else {
            // ENABLE THE WIFI FIRST
            wifiManager.setWifiEnabled(true);

            // WIFI IS NOW ENABLED. GRAB THE MAC ADDRESS HERE
            WifiInfo info = wifiManager.getConnectionInfo();
            String address = info.getMacAddress();
            return address;
        }
    }

    public static String obtenerFechaHora() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String obtenerFecha() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String obtenerHora() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private static class AysnTask extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        private String jsonStr;
        private Context nContext;
        ProgressDialog pd;

        public AysnTask(String url, String jsonStr, Context nContext) {
            this.url = url;
            this.jsonStr = jsonStr;
            this.nContext = nContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage(R.string.charging+"");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";
            try {
                step = "1";
             //   List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("", jsonStr));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
               // httppostreq.setEntity(new UrlEncodedFormEntity(params));
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
            Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            Log.d("iDWebMeth -- >", res);



            /*Desactiva el progressDialog una vez que haya terminado de subir t odo al server.*/
            try {
                pd.dismiss();
            }catch(Exception e){

            }
        }
    }

    public static void Mensaje(final Activity context, final RelativeLayout content, final int mode){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog
                .setCancelable(false)
                .setIcon(R.drawable.tomat)
                .setMessage(R.string.keepSaving)
                .setTitle(R.string.Continue)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        switch (mode) {
                            case 1: //Viene de qrManager
                              //  qrManagement.limpiaComponentes();
                                break;
                            case 2:
                              //  locatsManagement.limpiaComponentes();
                            break;
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        switch (mode) {
                            case 1: //Viene de qrManager
                            //    new qrManagement(context, content).setView();
                                break;
                            case 2:
                              //  new locatsManagement(context, content).setView();
                                break;
                        }

                    }
                })

                .show();
    }

    public static class AysnTaskGetTables extends AsyncTask<String, String, String> {
        public String url;
        private ProgressDialog pd;
        public Activity nContext;
        public String dataToSend;
        public int acc;

        public AysnTaskGetTables(String url, Activity nContext, String dataToSend, int acc){
            this.url = url;
            this.nContext = nContext;
            this.dataToSend = dataToSend;
            this.acc = acc;
            pd = new ProgressDialog(nContext);
            Log.d("Textbg -->", "acc = " + acc + "  - dataTosend = " + dataToSend);
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

                String parametro="";
                if(acc==1 || acc == 3 || acc == 4 ||
                        acc == 5 || acc == 6 || acc == 7 ||
                        acc == 8 || acc == 9 || acc == 10 ||
                        acc == 11 || acc == 12 || acc == 13 ||
                        acc == 14 || acc == 15 || acc == 16 ||
                        acc == 17 || acc==18 || acc==19 || acc==20 ||
                        acc == 21 || acc==22 || acc==23 || acc==24 || acc==25) // 1:farms 3:contentypeLocat 4:typeLocat 5:brings locaciones 6:onholdReasons 7:Embalajes 8:skuQuality 9:threshRason 10:userExtraRole
                    parametro = "SPName";
                if(acc==2)
                    parametro = "json";

                Log.d("Textbg -->", "acc = "+acc+" - parametro = "+parametro+" - dataTosend = "+dataToSend);
                step="1";

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                if(acc == 2) {
                    params.add(new BasicNameValuePair(parametro, dataToSend));
                }
                else {

                    params.add(new BasicNameValuePair(parametro, dataToSend));
                    params.add(new BasicNameValuePair("ParametersArray", ""));
                }


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
                // just end the background thread
                jsoncadena = "No hay conexión a internet. Porfavor conectese a internet y syncronize las plantas y los invernaderos. "+t.getMessage()+" -- step: "+step;

            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute(final String res) {
            // Toast.makeText(GuardarRiego.this, res, Toast.LENGTH_LONG).show();
            nContext.runOnUiThread(new Runnable() {
                public void run() {
                    //Do something on UiThread
                    Log.d("Text -- > "+dataToSend, res);

                    //  JSONObject json;
                    try {
                        //json = new JSONObject(res);
                        JSONArray JSONarrayPlantas = new JSONArray(res);
                        BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();

                        if(acc == 1)  bd.creaTablaFarm();
                        if(acc == 4)  bd.createTableLineSKU();
                        if(acc == 5)  bd.createTableLinePackage();
                        if(acc == 6)  bd.createTablaOnHoldReason();
                        if(acc == 7)  bd.createTablaEmbalaje();
                        if(acc == 8)  bd.createTableSkuQuality();
                        if(acc == 9)  bd.createTableThreshReason();
                        if(acc == 10) bd.createTableuserExtraRole();
                        if(acc == 11) bd.createTableItemMaster();
                        if(acc == 12) bd.createTableSizeCode();
                        if(acc == 13) bd.createTablePromotions();
                        if(acc == 14) bd.createTableLocations();
                        if(acc == 15) bd.createTableContainerEmbalaje();
                        if(acc == 16) bd.createTableNSCalendar();
                        if(acc == 17) bd.createTableImpresoras();
                        if(acc == 18) bd.packTurnosDB.createTableSync();
                        if(acc == 19) bd.crearTablaPlantsDepartments();
                        if(acc == 20) bd.crearTablaSitesDepartments();
                        if(acc == 21) bd.crearTablaPlantsSites();
                        if(acc == 22) bd.crearTablaReasonWaste();
                        if(acc == 23) bd.crearTablaReasonDepartments();
                        if(acc == 24) bd.crearTablaQualityType();
                        if(acc == 25) bd.crearTablaDispositionWaste();

                        for (int i = 0; i < JSONarrayPlantas.length(); i++) {
                            JSONObject row = JSONarrayPlantas.getJSONObject(i);

                            if(acc == 1)//Obtiene Farms
                                bd.insertFarm(row.getString("ID"), row.getString("Description"), row.getString("active"));


                            if(acc == 4)
                                bd.insertLineSKU(row.getString("id_Line"), row.getString("vSKU"), row.getString("lastUpdateBy"), row.getString("lastUpdateTime"));

                            if(acc == 5){
                                bd.insertLinePackage(row.getString("id_LinePackage"),
                                        row.getString("vNameLine"),
                                        row.getString("vDescriptionLine"),
                                        row.getString("vPath_ImageLine"),
                                        row.getString("vTypeLine"),
                                        row.getString("bActive"),
                                        row.getString("id_Plant"),
                                        row.getString("id_GP"),
                                        row.getString("idSite"));
                            }

                            if(acc == 6){//insertamos en onholdReasons
                                bd.insertOnHoldReason(row.getString("id_Cat_Reason"),row.getString("vNameReason"),row.getString("vDescripReason"),
                                        row.getString("bActive"),row.getString("id_Plant"),row.getString("bRelease"));
                            }

                            if(acc == 7){//insertamos Embalajes
                                bd.insertEmbalaje(row.getString("idEmbalaje"),row.getString("vNameEmbalaje"),row.getString("vDescripEmbalaje"),
                                        row.getString("vRegex"),row.getString("bActive"));
                            }

                            if(acc == 8){//insertamos skuQuality
                                bd.insertSkuQuality(row.getString("id_Sku"),row.getString("vNameSku"),row.getString("vQuality"),
                                        row.getString("bActive"),row.getString("id_Product"),row.getString("iQuality"));
                            }

                            if(acc == 9){//insertamos threashReason
                                bd.insertThreshReason(row.getString("idThreshReason"),row.getString("vReason"),
                                        row.getString("bActive"),row.getString("idPlant"));
                            }

                            if(acc == 10){//insertamos userExtraRole
                                bd.insertUserExtraRole(row.getString("id_UserExtraRole"),row.getString("vShortName"),
                                        row.getString("vFullName"),row.getString("bActive"),row.getString("id_Plant"));
                            }

                            if(acc == 11){//insertamos en tblItemMaster
                                bd.insertItemMaster(row.getString("ID"), row.getString("ITEMNMBR"), row.getString("ITEMDESC"),
                                        row.getString("SUPERCATEGORY"), row.getString("CASEWEIGHT"), row.getString("PALLETWEIGHT"),
                                        row.getString("CASESPERPALLET"));
                            }

                            if(acc == 12){
                                bd.insertSizeCode(row.getString("ID"), row.getString("DESCRIPTION"));
                            }

                            if(acc == 13){
                                bd.insertPromotions(row.getString("ID"), row.getString("DESCRIPTION"));
                            }

                            if(acc == 14){
                                bd.insertLocation(row.getString("id_Location"), row.getString("vNameLocation"),
                                        row.getString("iWidth"), row.getString("iHeight"), row.getString("iLenght"),
                                        row.getString("bActive"), row.getString("vDescriptionLocation"), row.getString("id_Farm"),
                                        row.getString("id_TypeLocation"), row.getString("id_Container"), row.getString("id_Framework"),
                                        row.getString("dDateCreated"), row.getString("vUserCreated"), row.getString("dDateUpdated"),
                                        row.getString("vUserUpdated"), row.getString("vCodeLocation"));
                            }

                            if(acc == 15){
                                bd.insertContainerEmbalaje(row.getString("idContainerEmbalaje"),row.getString("idContainer"),
                                        row.getString("idEmbalaje"), row.getString("bActive"));
                            }

                            if(acc == 16){
                                bd.insertnsCalendar(row.getString("DGDate"),row.getString("DY"),
                                        row.getString("WK"), row.getString("YR"), row.getString("DateCode"),
                                        row.getString("Week"), row.getString("DayOfWeek"));
                            }

                            if(acc == 17){
                                bd.insertImpresora( row.getInt("idImpresora"),
                                                    row.getInt("idPlanta"),
                                                    row.getString("Name"),
                                                    row.getString("vNombre"),
                                                    row.getString("vIP"),
                                                    row.getString("vPuerto"),
                                                    row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 18){
                                bd.packTurnosDB.insertPackTurnos( row.getString("idTurno"),
                                        row.getString("vNameTurno"),
                                        row.getString("vDescriptionTurno"),
                                        row.getString("idFarm"),
                                        row.getString("bActive").equalsIgnoreCase("true") ? "1" : "0");
                            }

                            if(acc == 19){
                                bd.insertPlantsDepartments( row.getInt("idDepartment"),
                                                            row.getString("vNameDepartment"),
                                                            row.getString("vDescriptionDepartment"),
                                                            row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 20){
                                bd.insertSitesDepartments(  row.getInt("idSiteDepartment"),
                                                            row.getInt("idSite"),
                                                            row.getInt("idDepartment"),
                                                            row.getInt("idFarm"),
                                                            row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 21){
                                bd.insertPlantsSites(   row.getInt("idSite"),
                                                        row.getInt("idFarm"),
                                                        row.getString("vSiteName"),
                                                        row.getString("vSiteDescription"),
                                                        row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 22){
                                bd.insertReasonWaste(   row.getInt("idReason"),
                                                        row.getString("vNameReason"),
                                                        row.getString("vDescriptionReason"),
                                                        row.getString("bLiberation").equalsIgnoreCase("true") ? 1 : 0,
                                                        row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 23){
                                bd.insertReasonDepartments( row.getInt("idReason"),
                                                            row.getInt("idDepartment"));
                            }

                            if(acc == 24){
                                bd.insertQualityType(   row.getInt("id_QualityType"),
                                                        row.getString("vQualityType"),
                                                        row.getString("vSortName"),
                                                        row.getString("vDescriptionQuality"),
                                                        row.getString("bCalidadFactor").equalsIgnoreCase("true") ? 1 : 0,
                                                        row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }

                            if(acc == 25){
                                bd.insertDispositionWaste(  row.getString("idDisposition"),
                                                            row.getString("vNameDisposition"),
                                                            row.getString("bActive").equalsIgnoreCase("true") ? 1 : 0);
                            }
                        }
                        bd.cerrar();

                        SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("hasIn", true);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(nContext, R.string.notConex, Toast.LENGTH_LONG).show();
                    }

                    try {
                        pd.dismiss();
                    } catch(Exception e){ }
                }
            });
        }
    }

    public static void actualizarSharedPreferencesInt(Activity nContext, String key, int value){
        SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void actualizarSharedPreferencesString(Activity nContext, String key, String value){
        SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean isALine(String content){
        if(content.matches("^[0-9A-Za-z]+-[0-9]{1,3}[|][0][.][0][.][0]"))
            return true;

        return false;
    }
}
