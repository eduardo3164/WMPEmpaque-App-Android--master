package com.ns.empaque.wmpempaque.MAP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.R;
import com.ns.empaque.wmpempaque.UbicationByQr.UbicationByQr;
import com.ns.empaque.wmpempaque.WMPEmpaque;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcalderon on 25/05/2016.
 */
public class Mapeo {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    public static ArrayList<LocacionesOcupadas> busyLoct;
    private static Spinner spFarm, spLocations, spLevels;
    private static GridView gvLocation;
    public static FloatingActionButton fabAtras, fabHome;

    private static String[] idFarm, idLocation, levels;
    private static int level = 1;

   // public static String[] posiciones;

    public Mapeo(Activity nContext, RelativeLayout content){
        this.nContext = nContext;
        this.content = content;
    }

    public static void setView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.mapeogeneral, null, true);
        config.updateContent(content, v);

        busyLoct = new ArrayList<>();

        spFarm = (Spinner) v.findViewById(R.id.spFarms);
        spLocations = (Spinner) v.findViewById(R.id.spLocations);
        spLevels = (Spinner) v.findViewById(R.id.spLevels);
        gvLocation = (GridView) v.findViewById(R.id.gv_Location);
        fabAtras = (FloatingActionButton) v.findViewById(R.id.fabAtras);
        fabHome = (FloatingActionButton) v.findViewById(R.id.fabHome);

        llenarSpinnerFarm();

        eventoSpinnerFarm();


        eventoSpinnerLocation();

        eventoSpinnerLevels();

        //llenarSpinnerLocation();

        fabAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeAllViewsInLayout();
                config.backContent(content);
                WMPEmpaque.tipoApp = 0;
            }
        });

    }

    private static void eventoSpinnerLevels() {
        spLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(levels != null) {
                    level = Integer.parseInt(levels[position]);
                }

                SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
                String iLocation = sharedpreferences.getString("iLocation","1");
                drawLocation(iLocation, level);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static void eventoSpinnerLocation() {
        spLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (idLocation != null) {
                    SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("iLocation", idLocation[position]);
                    editor.commit();
                    drawLocation(idLocation[position]);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void drawLocation(String location) {
        busyLoct.clear();
        new getFolioByWareHouse(config.rutaWebServerOmar + "/getFolioByWareHouse", location, 1).execute();
    }

    public static void drawLocation(String location, int lev) {
        busyLoct.clear();
        new getFolioByWareHouse(config.rutaWebServerOmar + "/getFolioByWareHouse", location, 2).execute();
    }

    private static void eventoSpinnerFarm() {

        spFarm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("iFarm", idFarm[position]);
                editor.commit();

               llenarSpinnerLocation(idFarm[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private static void llenarSpinnerLocation(String idFarm) {

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos[][] = bd.getLocationsByFarm(idFarm);
        bd.cerrar();

        if(datos.length > 0) {
            spLocations.setEnabled(true);
            String[] name = new String[datos.length];
            idLocation = new String[datos.length];
            for (int i = 0; i < datos.length; i++) {
                name[i] = datos[i][1];
                idLocation[i] = datos[i][0];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, name);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLocations.setAdapter(adapter);




        }else{
            Toast.makeText(nContext, nContext.getString(R.string.errorReadLocations), Toast.LENGTH_LONG).show();

            String[] nada = {"No hay locaciones"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, nada);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLocations.setAdapter(adapter);
            spLocations.setEnabled(false);
        }
    }

    private static void llenarSpinnerFarm() {

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos[][] = bd.obtenerFarms();//cambiar para obtener locaciones
        bd.cerrar();

        if(datos.length > 0) {
            String[] name = new String[datos.length];
            idFarm = new String[datos.length];
            for (int i = 0; i < datos.length; i++) {
                name[i] = datos[i][1];
                idFarm[i] = datos[i][0];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, name);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFarm.setAdapter(adapter);
        }else{
            Toast.makeText(nContext, nContext.getString(R.string.errorReadFarms), Toast.LENGTH_LONG).show();
        }

    }

    private static class getFolioByWareHouse extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url, idLocation;
        public int action;
        ProgressDialog pd;

        public getFolioByWareHouse(String url, String location, int action) {
            this.url = url;
            this.action = action;
            idLocation = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage("Cargando información...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {

            String jsoncadena = "", step = "0";
            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idLocation", idLocation));
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
                // just end the background thread
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(nContext, "Error al tratar de conectar al webservice. Revice su conexión a internet", Toast.LENGTH_LONG).show();
                    }
                });

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
                //JSONObject json     = new JSONObject(res);
                JSONArray folios    = new JSONArray(res);
                if(folios.length() > 0){
                    for(int  i = 0; i< folios.length(); i++){

                        LocacionesOcupadas lo = new LocacionesOcupadas();
                        JSONObject row = folios.getJSONObject(i);

                        lo.setFolio(row.getString("vfolio"));
                        lo.setHoras(row.getInt("horas"));
                        lo.setCalidad(row.getString("Calidad"));
                        lo.setProducto(row.getString("ProductClass"));
                        lo.setGreenhouse(row.getString("vgreenhouse"));
                        lo.setIdLocation(row.getInt("id_location"));
                        lo.setxPos(row.getInt("ixposition"));
                        lo.setyPos(row.getInt("iyposition"));
                        lo.setzPos(row.getInt("izposition"));
                        lo.setBoxes(row.getInt("cajas"));

                        busyLoct.add(lo);
                    }
                }



            }catch(Exception e){
               // Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage()+"");
            }

            BaseDatos bd = new BaseDatos(nContext);
            bd.abrir();
            locacion lc  = bd.getLocationById(idLocation);
            UbicationByQr.itemsEmbalaje = bd.getEmbalajes(lc.getContainer() + "");
            UbicationByQr.idLocation = lc.getCode();
            bd.cerrar();

           //UbicationByQr.positions = posiciones;
            if(action == 1)
                llenarSpinnerLevels(lc.getHeight());

            //putDatosPermitidos
            new getFramework(config.rutaWebServerOmar+"/getFramework", lc.getIdLocation()+"").execute();

           // gvLocation.setStretchMode(gvLocation.NO_STRETCH);

            if(lc.getWidth() > lc.getLenght() )
                gvLocation.setNumColumns(lc.getLenght());
            else
                gvLocation.setNumColumns(lc.getWidth());

            Log.d("width", lc.getWidth() + "");
            Log.d("lenght", lc.getLenght()+"");

            positionsAdapter adaptador = new positionsAdapter(nContext, lc, busyLoct, level);
            gvLocation.setAdapter(adaptador);

        }
    }

    private static void llenarSpinnerLevels(int height) {
        if(height > 0){

            spLevels.setEnabled(true);

            levels = new String[height];

            for(int i=0; i<height; i++)
                levels[i] = (i+1)+"";

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, levels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLevels.setAdapter(adapter);

        }else{
            String[] nada = {"No hay altura"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, nada);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLevels.setAdapter(adapter);
            spLevels.setEnabled(false);
        }

    }

    private static class getFramework extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url, idLocation;
        ProgressDialog pd;

        public getFramework(String url, String location) {
            this.url = url;
            idLocation = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage("Cargando información...");
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
                params.add(new BasicNameValuePair("idLocation", idLocation));
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
                //JSONObject json     = new JSONObject(res);
                JSONArray dataPermit    = new JSONArray(res);

                UbicationByQr.putDatosPermitidos(dataPermit);

            }catch(Exception e){
                Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage()+"");
            }
        }


    }

}
