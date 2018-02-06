package com.ns.empaque.wmpempaque;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ns.empaque.wmpempaque.AsigPrefolio2Folio.AsigPreFolio;
import com.ns.empaque.wmpempaque.AsignarPrepallets.AsignarPrepallet;
import com.ns.empaque.wmpempaque.AsignarPrepallets.Folio;
import com.ns.empaque.wmpempaque.AsignarPrepallets.InsupPrePallet;
import com.ns.empaque.wmpempaque.Avisos.Avisos;
import com.ns.empaque.wmpempaque.Avisos.avisosAdapter;
import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.CasesPrinter.CasesList;
import com.ns.empaque.wmpempaque.Desgrane.Desgrane;
import com.ns.empaque.wmpempaque.MAP.Mapeo;
import com.ns.empaque.wmpempaque.MermaLinea.MermaLinea;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.OnHold.AddProductoOnHold;
import com.ns.empaque.wmpempaque.OnHold.OnHold;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.PrePallet.PrePallet;
import com.ns.empaque.wmpempaque.QAIB.QAIB;
import com.ns.empaque.wmpempaque.UbicationByQr.UbicationByQr;
import com.ns.empaque.wmpempaque.UpdateFolio.UpdateFolio;
import com.ns.empaque.wmpempaque.insertLine.insertLines;
import com.ns.empaque.wmpempaque.qrScanner.IntentIntegrator;
import com.ns.empaque.wmpempaque.qrScanner.IntentResult;
import com.nullwire.trace.ExceptionHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class WMPEmpaque extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static RelativeLayout contenidos;
    public static  LayoutInflater inflater;
    public static ListView lvAvisos;
    private static Context mContext;
    private static ArrayList<Avisos> avisosAList;
    private static avisosAdapter AdapterAvisos;
    //public View v;
    public static int tipoApp = 0, prefolio = 0, caseAsignCasePrepallet = 0, linea = 0, scanOnHold = 0, qaUser = 1;
    public static SharedPreferences sharedpreferences;
    FloatingActionMenu menu1;
    FloatingActionButton syncCtalogs, syncPrePallet;
   // public FloatingActionButton fab, fabAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wmpempaque);

        sharedpreferences = getSharedPreferences("WMPEmpaqueApp", this.MODE_PRIVATE);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WMP Empaque v "+config.versionApp);
        toolbar.setSubtitle(sharedpreferences.getString("username", ""));
        setSupportActionBar(toolbar);

        mContext = WMPEmpaque.this;
        inflater = WMPEmpaque.this.getLayoutInflater();


        tipoApp = 0;
                /*Accion flotante*/
        menu1 = (FloatingActionMenu) findViewById(R.id.menu1);
        menu1.setClosedOnTouchOutside(true);
        //menu1.setBackgroundResource(R.drawable.syncbtn);

        syncPrePallet = (FloatingActionButton) findViewById(R.id.fabSyncTablas);
        syncCtalogs = (FloatingActionButton) findViewById(R.id.fabSyncCatalogs);

        syncPrePallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrePallet pp = new PrePallet(WMPEmpaque.this);
                pp.sincronizar();
            }
        });

      //  PopUp pop = new PopUp(this, "Los Pallets han sido insertados en la locación exitosamente", PopUp.POPUP_OK);
       // pop.showPopUp();

        syncCtalogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCatalogs();
            }
        });

        contenidos = (RelativeLayout) findViewById(R.id.content);

        setAvisos();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BaseDatos bd = new BaseDatos(this);
        bd.abrir();
        bd.cerrar();

        //Get "hasIn" value. If the var doesn't exist yet false is returned
        boolean hasIn = sharedpreferences.getBoolean("hasIn", false);

        contenidos.setBackgroundResource(R.drawable.worldns);

        if(!hasIn) {
            syncCatalogs();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("unicID", UUID.randomUUID().toString());
            editor.putInt("totalPrint", 24);
            editor.putInt("totalPrintCalidad", 3);
            editor.putInt("scanMode", 1);
            editor.commit();
        }


        // config.syncronizar(this);
       // Toast.makeText(this, config.validaString("15021Z1234X62X") + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAvisos() {
        View v;
        inflater = this.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_content_lv_avisos, null, true);

        lvAvisos = (ListView) v.findViewById(R.id.lv_avisos);
        avisosAList = new ArrayList<>();

        contenidos.addView(v);

        prefolio = 0;
        tipoApp = 0;
        caseAsignCasePrepallet = 0;

        new getAvisos(config.rutaWebServerOmar+"/get_avisos", this).execute();
        new getVersion(config.rutaWebServerOmar+"/get_version", this).execute();

    }

    public static boolean impresoraConfigurada(){
        return sharedpreferences.getBoolean("printerConfig", false);
    }

    public static String ipImpresoraConfigurada(){
        return sharedpreferences.getString("ipPrinter", "");
    }

    public static String puertoImpresoraConfigurada(){
        return sharedpreferences.getString("puertoPrinter", "");
    }

    public static void mostrarDialogo(String msj){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mContext);

        alertDialog2.setTitle("Mensaje");
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

    public static void setAvisos(Activity nContext) {
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.vw_content_lv_avisos, null, true);

        lvAvisos = (ListView) v.findViewById(R.id.lv_avisos);
        avisosAList = new ArrayList<>();

        contenidos.addView(v);

        prefolio = 0;
        tipoApp = 0;
        caseAsignCasePrepallet = 0;

        new getAvisos(config.rutaWebServerOmar+"/get_avisos", nContext).execute();

        new getVersion(config.rutaWebServerOmar+"/get_version", nContext).execute();

    }

    public void syncCatalogs(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //bring all plants from server
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_Farms", 1).execute();

                //brings type Content locations
                // new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",this, "spr_GetTypeWarehouseContainer",3).execute();


                //brings line SKU
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_LineSKU",4).execute();

                //save locations without sync to the server, and bring all warehouse saved on server
                //this method only works if theres some locations without sync
                //config.syncLocations(this, navigationView);

                //brings all linepackage from server
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_LinePackage",5).execute();


                //brings all onHoldReasons
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_onHoldReasons",6).execute();

                //brings Embalaje Catalogs
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_Embalajes",7).execute();

                //brings sku Quality
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_skuQuality",8).execute();

                //brings treshReason
                //new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_threshReason",9).execute(); //USA NO

                //brings userExtraRole
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_UserExtraRole",10).execute();

                //brings all SKU's FROM tblitemMaster
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table", WMPEmpaque.this, "ev2_spr_get_SKUFromItemMaster",11).execute();

                //brings the size Code
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "spr_Get_SizeCode",12).execute();

                //brings the promotions
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "spr_Get_Promotions",13).execute();

                //brings the locations
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "spr_Get_Locations",14).execute();

                //brings the catContainerEmbalaje
                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "spr_Get_Cat_ContainerEmbalaje",15).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_get_NSCalendar",16).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_get_Impresoras", 17).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "spr_Get_PackTurnos", 18).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getPlantsDepartments", 19).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getSitesDepartments", 20).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getPlantsSites", 21).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getReasonWaste", 22).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getReasonDepartments", 23).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getQualityType", 24).execute();

                new config.AysnTaskGetTables(config.rutaWebServerOmar+"/Get_Table",  WMPEmpaque.this, "ev2_spr_getDispositionWaste", 25).execute();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tipoApp", tipoApp);
        //outState.putString("codigoLinea", AsignarPrepallet.codigoLinea);

        switch(tipoApp){
            case 1:
                //outState.putInt("accion",qrManagement.saveUpdate);
                break;
            case 2:
                //outState.putInt("accion",locatsManagement.saveUpdate);
                break;
            case 4:
             /*   outState.putString("idLocat",UbicationByQr.idLocation);
                outState.putStringArray("posiciones", UbicationByQr.positions);
                outState.putInt("letAdd", UbicationByQr.letAdd);
                outState.putInt("areRead", UbicationByQr.areRead);
                outState.putStringArray("items", UbicationByQr.devolverItems());
                outState.putInt("alreadyConfirm", UbicationByQr.alreadyConfirmed);
                outState.putSerializable("dataToSend", UbicationByQr.getDataToSend());*/
                break;
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        tipoApp = savedInstanceState.getInt("tipoApp");
        //String codigoLinea = savedInstanceState.getString("codigoLinea");

        /*if(tipoApp == 6) {
            new AsignarPrepallet(WMPEmpaque.this, contenidos).setView();
            AsignarPrepallet.GetLineInfo(codigoLinea);
        } else {
            Toast.makeText(WMPEmpaque.this, "Ya estas en Asignación de Prepallets", Toast.LENGTH_SHORT).show();
        }*/


        if(tipoApp == 1){
            int accion = savedInstanceState.getInt("accion");

        }

        if (tipoApp == 2){
            int accion = savedInstanceState.getInt("accion");

        }

        if(tipoApp == 3) {

        }


        /*if(tipoApp == 4) {
            String idLocat = savedInstanceState.getString("idLocat");
            String[] positions = savedInstanceState.getStringArray("posiciones");
            int ledAdd = savedInstanceState.getInt("letAdd");
            int areRead = savedInstanceState.getInt("areRead");
            int alreadyConfirm = savedInstanceState.getInt("alreadyConfirm");
            String[] items = savedInstanceState.getStringArray("items");
            String[][] dts = (String[][]) savedInstanceState.getSerializable("dataToSend");
            //Log.d("dts[0][0]",dts[0][0]);
            if(idLocat.compareToIgnoreCase("-1") != 0) {
                UbicationByQr ubq = new UbicationByQr(this, contenidos);
                ubq.setView();
                ubq.sendQRCodeToServer(idLocat + "|"+positions[0]+"."+positions[1]+"."+positions[2]);

                if(ledAdd == 1 && areRead == 1) {

                    if(items.length > 0) {
                        ubq.ingresarItems(items);
                        ubq.fabSend.setVisibility(View.VISIBLE);

                        if (alreadyConfirm == 1) {
                            UbicationByQr.putDataToSend(dts);
                            UbicationByQr.enviarFoliosToServer(this);
                        }
                    }
                }
            }
            else
                new UbicationByQr(this, contenidos).setView();
        }*/


        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            Toast.makeText(this, "WMPEmpaque", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.wmpempaque, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
       // fab.show();
      /*  if(id==R.id.nav_qr_manag){
            if(tipoApp != 1) {
                tipoApp = 1;
                new qrManagement(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, R.string.alreadyQRManager, Toast.LENGTH_SHORT).show();

        }*/
      /*  if(id==R.id.nav_loc_manag){
            if(tipoApp != 2) {
                tipoApp = 2;
                new locatsManagement(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, R.string.alreadyLocationManager, Toast.LENGTH_SHORT).show();

        }*/

        if(id == R.id.nav_ubicationMap){
            if(tipoApp != 4) {
                tipoApp = 4;
              //  new UbicationByQr(WMPEmpaque.this, contenidos).setView();
                new Mapeo(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, R.string.alreadyUbicationByQR, Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.nav_ubication){
            if(tipoApp != 5) {
                tipoApp = 5;
                  new UbicationByQr(WMPEmpaque.this, contenidos).setView();
                //new Mapeo(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, R.string.alreadyUbicationByQR, Toast.LENGTH_SHORT).show();
        }


        if(id == R.id.nav_desgrane){
            if(tipoApp != 7) {
                tipoApp = 7;
                new Desgrane(WMPEmpaque.this, contenidos).setView();
                //new Mapeo(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, R.string.alreadyInDesgrane, Toast.LENGTH_SHORT).show();
        }

/*
        if(id == R.id.nav_Prepallet){
            if(tipoApp != 3) {
                tipoApp = 3;
                new PrePalletList(WMPEmpaque.this, contenidos).setView();

            }else
                Toast.makeText(WMPEmpaque.this, "Ya estas en PrePallet", Toast.LENGTH_SHORT).show();
        }*/

       if(id == R.id.nav_Asign_PrePallet_Pallet){
            if(tipoApp != 6) {
                tipoApp = 6;
                new AsignarPrepallet(WMPEmpaque.this, contenidos).setView();

            }else
                Toast.makeText(WMPEmpaque.this, "Ya estas en Asignación de Prepallets", Toast.LENGTH_SHORT).show();
            }

            try {
                this.openFileOutput("asd", MODE_PRIVATE);
            }catch(Exception e){

            }

            if(id == R.id.nav_printLabels){
                if(tipoApp != 8) {
                    tipoApp = 8;
                    new CasesList(WMPEmpaque.this, contenidos).setView();

                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Imprimir Etiquetas de cajas", Toast.LENGTH_SHORT).show();
            }

            if(id == R.id.nav_Asig_preFolio){
                if(tipoApp != 9) {
                    tipoApp = 9;
                    new AsigPreFolio(WMPEmpaque.this, contenidos).setView();

                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Asignar Prefolio", Toast.LENGTH_SHORT).show();
            }


            if(id == R.id.nav_InsertLine){
                if(tipoApp != 10) {
                    tipoApp = 10;
                    new insertLines(WMPEmpaque.this, contenidos).setView();

                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Lineas", Toast.LENGTH_SHORT).show();
            }

            if(id == R.id.nav_OnHold){
                if(tipoApp != 11) {
                    tipoApp = 11;
                    new OnHold(WMPEmpaque.this, contenidos).setView();
                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en On Hold", Toast.LENGTH_SHORT).show();
            }

            if(id == R.id.nav_ConsolidaFolio){
                if(tipoApp != 12) {
                    tipoApp = 12;
                    new UpdateFolio(WMPEmpaque.this, contenidos).setView();
                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Consolidación de Pre-Folio", Toast.LENGTH_SHORT).show();
            }

            if(id == R.id.nav_QAIB){
                if(tipoApp != 13) {
                    tipoApp = 13;
                    new QAIB(WMPEmpaque.this, contenidos).setView();
                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Calidad Inbound", Toast.LENGTH_SHORT).show();
            }

            if(id == R.id.nav_mermaLinea){
                if(tipoApp != 14) {
                    tipoApp = 14;
                    new MermaLinea(WMPEmpaque.this, contenidos).setView();
                }else
                    Toast.makeText(WMPEmpaque.this, "Ya estas en Merma en Línea", Toast.LENGTH_SHORT).show();
            }

        if(id == R.id.nav_PesarFolio){
            if(tipoApp != 15) {
                tipoApp = 15;
                new MermaLinea(WMPEmpaque.this, contenidos).setView();
            }else
                Toast.makeText(WMPEmpaque.this, "Ya estas en Pesar Folio", Toast.LENGTH_SHORT).show();
        }

        /*if(id == R.id.nav_logOff){
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putBoolean("hasLoggedIn", false);
            editor.commit();

            Intent sesion = new Intent(WMPEmpaque.this, LogIn.class);
            startActivity(sesion);

            this.finish();
        }*/

        if(id == R.id.nav_config_impresoras){
            configurarImpresoras();
        }


      /*  if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {

            if (tipoApp == 1) {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (scanningResult != null) {

                    //get the extras that are returned from the intent
                    String contents = intent.getStringExtra("SCAN_RESULT");
                    String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                    Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG).show();
                    /*qrManagement.tvCode.setText(contents);
                    qrManagement.tvKindCode.setText(format);
                    qrManagement.updateListView(contents);*/
                } else {
                    Toast.makeText(this,
                            R.string.noScan, Toast.LENGTH_SHORT).show();
                }
            } else
                if(tipoApp == 2){
                    IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                    if (scanningResult != null) {

                        //get the extras that are returned from the intent
                        String contents = intent.getStringExtra("SCAN_RESULT");
                        String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                        Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG).show();
                       /* locatsManagement.tvCode.setText(contents);
                        locatsManagement.tvKindCode.setText(format);
                        locatsManagement.updateListView(contents);*/
                    } else {
                        Toast.makeText(this,
                                R.string.noScan, Toast.LENGTH_SHORT).show();


                    }
                }else
                    if(tipoApp == 3){
                        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                        if (scanningResult != null) {



                        } else {
                            Toast.makeText(this,
                                    R.string.noScan, Toast.LENGTH_SHORT).show();


                        }
                    }else
                        if(tipoApp == 4){//Ubicacion por QR MAP
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {
                               /* if(UbicationByQr.letAdd == 1 && UbicationByQr.readLoc == 0) { //lee formas o pallets
                                    //get the extras that are returned from the intent
                                    String contents = intent.getStringExtra("SCAN_RESULT");
                                    contents = contents.replace(" ","");
                                    UbicationByQr.addEmbalaje(contents);
                                }else{ //lee locacion
                                    //get the extras that are returned from the intent
                                    String contents = intent.getStringExtra("SCAN_RESULT");
                                    UbicationByQr.sendQRCodeToServer(contents);
                                }*/
                                UbicationByQr ubqr = new UbicationByQr(WMPEmpaque.this, contenidos);
                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");
                                ubqr.addEmbalajeFromMap(contents);
                                ubqr.enviarFoliosToServerFromMap(WMPEmpaque.this);

                            } else {
                                Toast.makeText(this,
                                        R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        }else  if(tipoApp == 5) {//Ubicacion por QR
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {
                                if(UbicationByQr.letAdd == 1 && UbicationByQr.readLoc == 0) { //lee formas o pallets
                                    //get the extras that are returned from the intent
                                    String contents = intent.getStringExtra("SCAN_RESULT");
                                    contents = contents.replace(" ","");
                                    UbicationByQr.addEmbalaje(contents);
                                }else{ //lee locacion
                                    //get the extras that are returned from the intent
                                    String contents = intent.getStringExtra("SCAN_RESULT");
                                    UbicationByQr.sendQRCodeToServer(contents);
                                }
                               /* UbicationByQr ubqr = new UbicationByQr(WMPEmpaque.this, contenidos);
                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");
                                ubqr.addEmbalajeFromMap(contents);
                                ubqr.enviarFoliosToServerFromMap(WMPEmpaque.this);*/

                            } else {
                                Toast.makeText(this,
                                        R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(tipoApp == 6){
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(caseAsignCasePrepallet == 1){//si es case para el prepallet
                                    caseAsignCasePrepallet = 0;
                                    InsupPrePallet.searCasesInServer(contents);
                                }else {

                                    if (AsignarPrepallet.isALine(contents)) {
                                        AsignarPrepallet.codigoLinea = contents;
                                        AsignarPrepallet.GetLineInfo(contents);
                                    } else {
                                        Toast.makeText(WMPEmpaque.this, "No tiene estructura de linea", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        } else if (tipoApp == 7) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                    Desgrane.sendFolio(contents);
                                }else {
                                    Toast.makeText(WMPEmpaque.this, "El codigo leido no parece un folio", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        } else if (tipoApp == 9) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(prefolio == 0) {//si es folio normal no de preharvest
                                    if (config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                        AsigPreFolio.checkFolio(contents);
                                    } else {
                                        Toast.makeText(WMPEmpaque.this, "El codigo leido no parece un folio", Toast.LENGTH_LONG).show();
                                    }
                                }else{//Si es un folio de preharvest
                                    if (config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                        AsigPreFolio.checkPreFolio(contents);
                                    } else {
                                        Toast.makeText(WMPEmpaque.this, "El codigo leido no parece un folio", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        } else if (tipoApp == 10) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(linea == 1){
                                    insertLines.getLineInformation(contents);
                                    linea=0;
                                }else{
                                    insertLines.sendFolioToLine(contents);
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        } else if (tipoApp == 11) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(scanOnHold == 1){
                                    Log.d("SCAN", "ENTRA");
                                    OnHold.buscarLocacion(contents);
                                } else if(scanOnHold == 2){
                                    AddProductoOnHold.buscarProducto(contents);
                                } else if(scanOnHold == 3){
                                    OnHold.buscarEnOnHold(contents);
                                }
                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        }else if (tipoApp == 12) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(prefolio == 0) {//si es folio normal no de preharvest
                                    if (config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                        UpdateFolio.checkFolio(contents);
                                    } else {
                                        Toast.makeText(WMPEmpaque.this, "El codigo leido no parece un folio", Toast.LENGTH_LONG).show();
                                    }
                                }else{//Si es un folio de preharvest
                                    if (config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                        UpdateFolio.checkPreFolio(contents);
                                    } else {
                                        Toast.makeText(WMPEmpaque.this, "El codigo leido no parece un folio", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        }else if(tipoApp == 13){//QAIB
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                if(qaUser == 1) {
                                    QAIB.validateUserQRCode(contents);
                                }else {

                                    if (config.validaString(contents, WMPEmpaque.this) == 1) {//Si es folio
                                        QAIB.validaFolio(contents);
                                    } else {
                                        Toast.makeText(this, "No parece folio", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        } else if (tipoApp == 14) {
                            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                            if (scanningResult != null) {

                                String contents = intent.getStringExtra("SCAN_RESULT");
                                contents = contents.replace(" ","");

                                Log.d("SCAN", "ENTRA");
                                MermaLinea.buscarLocacion(contents);
                            } else {
                                Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
                            }
                        }
        }catch(Exception e){
            Toast.makeText(this, R.string.noScan, Toast.LENGTH_SHORT).show();
            Log.d("Text", e.getMessage()+"");
        }
    }


    public void configurarImpresoras(){
        View dialoglayout = inflater.inflate(R.layout.dialogo_impresoras, null, true);

        final TextView lblImpresora = (TextView) dialoglayout.findViewById(R.id.lblImpresoraActual);
        ListView listaImpresoras = (ListView) dialoglayout.findViewById(R.id.listaImpresoras);

        BaseDatos bd = new BaseDatos(WMPEmpaque.this);
        bd.abrir();

        final String impresoras[][] = bd.obtenerImpresoras();
        String srcListaImp[] = new String[impresoras.length];
        ArrayList<String> listaImp = new ArrayList<String>();

        for(int i = 0; i < impresoras.length; i++){
            srcListaImp[i] = impresoras[i][2] + " - " + impresoras[i][3] + " - " + impresoras[i][4] + ":" + impresoras[i][5];
            listaImp.add(impresoras[i][0]);
        }

        bd.cerrar();

        ArrayAdapter<String> adaptadorLista = new ArrayAdapter<String>(WMPEmpaque.this, android.R.layout.simple_list_item_multiple_choice, srcListaImp);
        listaImpresoras.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaImpresoras.setAdapter(adaptadorLista);

        if(!sharedpreferences.getBoolean("printerConfig", false)) {
            lblImpresora.setText("(Ninguna)");
        } else {
            listaImpresoras.setItemChecked(listaImp.indexOf(sharedpreferences.getString("idPrinter", "")), true);
            lblImpresora.setText(sharedpreferences.getString("nameFarmPrinter", "") + " - " + sharedpreferences.getString("namePrinter", "") + " - " + sharedpreferences.getString("ipPrinter", "") + ":" + sharedpreferences.getString("puertoPrinter", ""));
        }


        listaImpresoras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lblImpresora.setText(impresoras[i][2] + " - " + impresoras[i][3] + " - " + impresoras[i][4] + ":" + impresoras[i][5]);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putBoolean("printerConfig", true);
                editor.putString("idPrinter",  impresoras[i][0]);
                editor.putString("nameFarmPrinter",  impresoras[i][2]);
                editor.putString("namePrinter", impresoras[i][3]);
                editor.putString("ipPrinter",  impresoras[i][4]);
                editor.putString("puertoPrinter", impresoras[i][5]);

                editor.commit();
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WMPEmpaque.this);
        alertDialog.setView(dialoglayout);
        alertDialog.setIcon(R.drawable.iprint);
        alertDialog.setTitle("Configuración de impresoras");
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

    public static class getAvisos extends AsyncTask<String, String, String> {
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
          //  pd = new ProgressDialog(WMPEmpaque.this);
        //    pd.setIndeterminate(true);
        //    pd.setMessage("Cargando información...");
        //    pd.setCanceledOnTouchOutside(false);
        //    pd.show();
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
                // just end the background thread
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
              //  runOnUiThread(new Runnable() {
               //     @Override
               //     public void run() {
              //          Toast.makeText(WMPEmpaque.this, "Error al tratar de conectar al webservice. Revice su conexión a internet", Toast.LENGTH_LONG).show();
              //      }
              //  });

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
            }catch(Exception e){

            }

            try {

                JSONArray avisos    = new JSONArray(res);
                avisosAList.clear();

                if(avisos.length() > 0){
                    for(int  i = 0; i< avisos.length(); i++){

                        Avisos av = new Avisos();
                        JSONObject row = avisos.getJSONObject(i);

                        av.setTitleSpanish(row.getString("vNameAvisoS"));
                        av.setTitleEnglish(row.getString("vNameAvisoE"));
                        av.setDescriptionEnglish(row.getString("vDescriptionE"));
                        av.setDescriptionSpanish(row.getString("vDescriptionS"));
                        av.setFechaCreación(row.getString("dDateCreate"));
                        av.setUserCreacion(row.getString("vUserCreate"));

                        avisosAList.add(av);
                    }

                    AdapterAvisos = new avisosAdapter(nContext, avisosAList);
                    lvAvisos.setAdapter(AdapterAvisos);

                }



            }catch(Exception e){
                // Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage()+"");
            }

        }
    }


    public static class getVersion extends AsyncTask<String, String, String> {
        //private ProgressDialog pDialog;
        public String url;
        public Activity nContext;
        // ProgressDialog pd;

        public getVersion(String url, Activity nContext) {
            this.url = url;
            this.nContext = nContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  pd = new ProgressDialog(WMPEmpaque.this);
            //    pd.setIndeterminate(true);
            //    pd.setMessage("Cargando información...");
            //    pd.setCanceledOnTouchOutside(false);
            //    pd.show();
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
                // just end the background thread
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
                //  runOnUiThread(new Runnable() {
                //     @Override
                //     public void run() {
                //          Toast.makeText(WMPEmpaque.this, "Error al tratar de conectar al webservice. Revice su conexión a internet", Toast.LENGTH_LONG).show();
                //      }
                //  });

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
            }catch(Exception e){

            }

            try {

                JSONArray avisos    = new JSONArray(res);

                if(avisos.length() > 0){

                    JSONObject row = avisos.getJSONObject(0);

                    sharedpreferences.edit().putString("vPassword", row.getString("vPassword")).commit();
                    sharedpreferences.edit().putInt("iSleepPassword", row.getInt("iSleepPassword")).commit();

                    if(Double.parseDouble(row.getString("vNameVersion")) > config.versionApp){
                        new PopUp(nContext, "La versión de la aplicación no es la más Actual, si continuas usandola puede que se presenten problemas. " +
                                "  Lo recomendale es que desinstales y vuelvas a instalar desde la página empaque.naturesweet.com.mx ó " +
                                "contacta a los de sistemas en tu planta", PopUp.POPUP_INFORMATION).showPopUp();
                    }

                }


            }catch(Exception e){
                // Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + "Hay un problema con la conexión a internet",Toast.LENGTH_LONG).show();
                Log.e("Error recibir datos",e.getMessage()+"");
            }

        }
    }

}
