package com.ns.empaque.wmpempaque;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.Modelo.config;

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

/**
 * Created by Christopher on 30/12/2016.
 */

public class LogIn extends AppCompatActivity {

    public static Button btnLogin;
    public static EditText Username, pass;

    public static CoordinatorLayout coordinatorLayout;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences("WMPEmpaqueApp", this.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", "DBTAPPWMPEMPAQUE");
        editor.putBoolean("hasLoggedIn", true);

        editor.commit();

        boolean hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn) {
            Intent RiegoApp = new Intent(LogIn.this, WMPEmpaque.class);
            startActivity(RiegoApp);
            LogIn.this.finish();
        }

        setContentView(R.layout.activity_sesion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogIn);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.ccordinate);

        inicializaComponents();
        eventosComponents();
    }

    private void eventosComponents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getLogin(config.rutaWebServerOmar + "/Get_Login", Username.getText().toString(), pass.getText().toString()).execute();
            }
        });
    }

    private void inicializaComponents() {
        btnLogin = (Button) findViewById(R.id.btnLogin);

        Username = (EditText) findViewById(R.id.txtUsername);
        pass = (EditText) findViewById(R.id.txtPassword);
    }

    private class getLogin extends AsyncTask<String, String, String> {
        public String url, userName, passw;
        private ProgressDialog pd;

        public getLogin(String url, String userName, String pass){
            this.url = url;
            pd = new ProgressDialog(LogIn.this);
            this.userName = userName;
            this.passw = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setIndeterminate(true);
            pd.setMessage("Cargando... Por favor espere!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            final HttpClient Client = new DefaultHttpClient();
            String jsoncadena="", step="0";

            try {
                step="1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",this.userName));
                params.add(new BasicNameValuePair("password",this.passw));
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
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d("Text -- >", res);
            JSONObject json;

            try {
                json = new JSONObject(res);

                JSONArray JSONarrayGH = json.optJSONArray("Result");
                JSONObject row = JSONarrayGH.getJSONObject(0);
                Log.d("sesion -->", row.getString("Authorized"));

                if(row.getString("Authorized").matches("Yes")){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("username", Username.getText().toString());
                    editor.putBoolean("hasLoggedIn", true);

                    /*if(row.getInt("NSAcount") == 1){
                        editor.putBoolean("NSAccount", true);
                    } else {
                        editor.putBoolean("NSAccount", false);
                    }*/

                    editor.commit();

                    Log.d("username", sharedpreferences.getString("username", ""));
                    Log.d("hasLoggedIn", sharedpreferences.getBoolean("hasLoggedIn", false) + "");

                    LogIn.this.finish();
                    Intent intento = new Intent(LogIn.this, WMPEmpaque.class);
                    startActivity(intento);
                } else {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Usuario y/o contrasela incorrecto(s).", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(LogIn.this, "No hay conexión a internet", Toast.LENGTH_LONG).show();
            }

            pd.dismiss();
        }
    }
}