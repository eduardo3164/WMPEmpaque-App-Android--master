package com.ns.empaque.wmpempaque.Desgrane;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Folio;
import com.ns.empaque.wmpempaque.AsignarPrepallets.PrePallet;
import com.ns.empaque.wmpempaque.AsignarPrepallets.cases;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcalderon on 23/09/2016.
 */
public class Desgrane {

    public static RelativeLayout content;
    public static LayoutInflater inflater;
    public static Activity nContext;
    public static ArrayList<BoxesFolioInLine> BoxesInFolio;
    public static adaptadorBoxesInFolio adaptadorListFolios;
    private static ListView lv_BoxesInLine;
    private static FloatingActionButton fabAtras, fabLookForFolio;
    private static EditText et_locatCode;

    public Desgrane(Activity c, RelativeLayout content){
//        super(nContext, R.layout.desgrane_view,BoxesInFolio);
        this.nContext = c;
        this.content = content;
        this.BoxesInFolio = BoxesInFolio;
    }

    public static void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.desgrane_view, null, true);
        config.updateContent(content, v);
       //final ListView list = (ListView)v.findViewById(R.id.lv_BoxesInLine);


        fabAtras = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        fabLookForFolio = (FloatingActionButton) v.findViewById(R.id.fabLookfor);
        final Button boton = (Button) v.findViewById(R.id.btnSendLocat);
        lv_BoxesInLine = (ListView) v.findViewById(R.id.lv_BoxesInLine);
        et_locatCode = (EditText) v.findViewById(R.id.et_locatCode);
        BoxesInFolio = new ArrayList<>();



        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String vFolio = et_locatCode.getText().toString();

                if(config.validaString(vFolio, nContext) == 1) {//Si es folio
                    refreshList(vFolio);
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
                    AlertDialog dialog;
                    builder.setMessage("El Codigo Ingresado NO Parece un Folio").setTitle("Error");
                    dialog = builder.create();
                    dialog.show();
                    //Toast.makeText(nContext, "El codigo ingresado no parece un folio", Toast.LENGTH_LONG).show();
                }

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

        fabLookForFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });





    }

    public static  void removeItem(int pos){
        //Log.d("REMOVE", pos+"");
            BoxesInFolio.remove(pos);

        adaptadorListFolios.notifyDataSetChanged();
    }

    public static void refreshList(String vFolio){
        //et_locatCode.requestFocus();

        for(int i = (BoxesInFolio.size() - 1); i >= 0; i--){
            if(BoxesInFolio.get(i).getvFolio().equalsIgnoreCase(vFolio) )
                BoxesInFolio.remove(i);
        }

       // adaptadorListFolios.notifyDataSetChanged();

        sendFolio(vFolio);
    }

    public static void sendFolio(String folio){
       // new asyncTaskGetBoxesFoliosinLine(config.rutaWebServerOmar+"/getBoxesInLine", folio).execute();
        new asyncTaskGetBoxesFoliosinLine(config.rutaWebServerOmar+"/getBoxesInLine", folio).execute();
       // new WMPEmpaque.getAvisos(config.rutaWebServerOmar+"/get_avisos", nContext).execute();
     //   new getAvisos(config.rutaWebServerOmar+"/get_avisos", nContext).execute();
        //Log.d("Folio desgrane", folio + " - " + config.rutaWebServerOmar + "/get_avisos");
        Log.d("Folio desgrane", folio + " - " + config.rutaWebServerOmar + "/get_avisos");

    }



    //ASYNCTASK TO CHECK FOLIO'S BOXES AVAILABLE IN LINE
   /**********************************************************/
    public static class asyncTaskGetBoxesFoliosinLine extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url, vFolio;
        ProgressDialog pd;
        int fin = 0;

        public asyncTaskGetBoxesFoliosinLine(String url, String vFolio) {
            this.url = url;
            this.vFolio = vFolio;
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
           // final HttpClient Client = new DefaultHttpClient();
            String jsoncadena = "", step = "0";
            try {
                step = "1";
                 List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("vFolio", this.vFolio));
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
                this.fin = 6;
            } catch (Exception t) {
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;

        }

        @Override
        protected void onPostExecute (String res) {
            try {
                BoxesInFolio.clear();
            }catch(Exception e){

            }

            Log.d("iDWebMeth -- >", res);
            Log.e("iDWebMeth -- >", res);
            Log.i("iDWebMeth -- >", res);

            /*Desactiva el progressDialog una vez que haya terminado de subir todo al server.*/
            try {
                pd.dismiss();
            } catch (Exception e) {
                Log.e("Error", "" + e.getMessage());
            }
            //Log.d("iDWebMeth -- >", res);
            try {
                Log.d("",res);
               //Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();

                JSONArray JSONBoxesInLine = new JSONArray(res);

                //adaptadorPP.notifyDataSetChanged();

                if(JSONBoxesInLine.length() > 0) {
                    for (int i = 0; i < JSONBoxesInLine.length(); i++) {

                        BoxesFolioInLine bl = new BoxesFolioInLine();
                        JSONObject row = JSONBoxesInLine.getJSONObject(i);

                        bl.setIdProductLog(row.getInt("idProductLog"));
                        bl.setIdProduct(row.getInt("idProducto"));
                        bl.setvFolio(row.getString("Folio"));
                        bl.setLineNumber(row.getInt("LineNmbr"));
                        bl.setLineName(row.getString("LineName"));
                        bl.setFechaEnterLine(row.getString("lineDintime"));
                        bl.setFarmNumber(row.getInt("plantNmbr"));
                        bl.setFarmName(row.getString("plantName"));
                        bl.setBoxesInLine(row.getInt("BoxesInLine"));
                        bl.setBoxesAssignToPallet(row.getInt("boxesInPallet"));
                        bl.setBoxesAvailable(row.getInt("boxesAvailable"));

                        BoxesInFolio.add(bl);


                    }
                    //Toast.makeText(nContext, "llenó", Toast.LENGTH_LONG).show();//this, android.R.layout.simple_list_item_1,eje

                    //Llenar lista de cajas en linea
                    adaptadorListFolios = new adaptadorBoxesInFolio(nContext, BoxesInFolio);
                    lv_BoxesInLine.setAdapter(adaptadorListFolios);
                    Log.d("ENTRA 2", "--------------------------------------------------------------------------");
                }




            } catch (Exception e) {
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos", e.getMessage());

            }

            if(this.fin < 6){

                Log.d("iDWebMeth -- >", "No llego al final");
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Asegurate de estar conectado a internet por favor", Toast.LENGTH_LONG).show();
                new PopUp(nContext, "- Asegurate de estar conectado a internet Por favor!\n- Si estás conectado a Internet y el problema consiste contacto al administrador de la aplicación.", PopUp.POPUP_INFORMATION).showPopUp();
            }

        }
    }


    private static class getAvisos extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        public Activity nContext;
        // ProgressDialog pd;

        public getAvisos(String url, Activity nContext) {
            this.url = url;
            this.nContext = nContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            String jsoncadena = "", step = "0";
            try {
                step = "1";
                // List<NameValuePair> params = new ArrayList<NameValuePair>();
                //    params.add(new BasicNameValuePair("idLocation", idLocation));
                step = "2";
                HttpPost httppostreq = new HttpPost(url);
                step = "3";
                // httppostreq.setEntity(new UrlEncodedFormEntity(params));

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
                //   pd.dismiss();
                Log.d("iDWebMeth -- >", res);
            }catch(Exception e){

            }

            try {
                Log.d("iDWebMeth -- >", res);
                Toast.makeText(nContext, res, Toast.LENGTH_LONG).show();
            }catch(Exception e){
                // Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage()+"");
            }

        }
    }


}
