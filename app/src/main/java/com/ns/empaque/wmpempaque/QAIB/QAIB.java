package com.ns.empaque.wmpempaque.QAIB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.R;
import com.ns.empaque.wmpempaque.WMPEmpaque;
import com.ns.empaque.wmpempaque.qrScanner.IntentIntegrator;

import static com.ns.empaque.wmpempaque.R.id.infoLines;

/**
 * Created by javier.calderon on 12/05/2017.
 */

public class QAIB {

    private static Activity nContext;
    private static RelativeLayout content;
    private static LayoutInflater inflater;
    private static SharedPreferences sharedPreferences;
    private static LinearLayout btnScanUser, btnScanFolio, btnAtras, btnHome;
    private static TextView userName, txtFolio;

    private static ProgressDialog pd;

    private static WebView wb_QA;

    public QAIB(Activity nContext, RelativeLayout parent){
        this.nContext = nContext;
        this.content = parent;
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
        pd = new ProgressDialog(nContext);
        pd.setIndeterminate(true);
        pd.setCanceledOnTouchOutside(false);
    }

    public static void setView(){
        WMPEmpaque.qaUser = 1;
        IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
        scanIntegrator.initiateScan();

    }

    private static void initComponents(View v) {
        wb_QA = (WebView) v.findViewById(R.id.wb_QA);
        btnScanFolio = (LinearLayout) v.findViewById(R.id.btnScanFolio);
        //btnScanUser = (LinearLayout) v.findViewById(R.id.btnScanUser);
        btnAtras = (LinearLayout) v.findViewById(R.id.btnAtras);
        btnHome = (LinearLayout) v.findViewById(R.id.btnHome);
        userName = (TextView) v.findViewById(R.id.userName);
        txtFolio = (TextView) v.findViewById(R.id.txtFolio);

        //userName.setText(sharedPreferences.getString("QAUser", "Leer el codigo de usuario") + " - " + sharedPreferences.getString("QAFarm", ""));

        wb_QA.getSettings().setJavaScriptEnabled(true);

        wb_QA.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(nContext, description, Toast.LENGTH_SHORT).show();
            }
        });

        //final ProgressDialog pd;

        pd.show();
        pd.setMessage("Cargando... Por favor espere!!");


        wb_QA.setWebViewClient(new WebViewClient() {
                                   public boolean shouldOverrideUrlLoading(WebView view, String url) {


                                       view.loadUrl(url);
                                       return true;
                                   }

                                   public void onPageFinished(WebView view, String url) {
                                       //txtFolio.setText("Folio cargado ingrese la calidad...");
                                       try {
                                            pd.dismiss();
                                       } catch (Exception ex) {

                                       }
                                   }

                                    @Override
                                    public void onLoadResource(WebView view, String url) {
                                       /* view.loadUrl(url);
                                        super.onLoadResource(view, url);*/

                                       /* try {
                                            pd.show();
                                            pd.setMessage("Cargando... Por favor espere!!");
                                        } catch (Exception ex) {

                                        }*/



                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon)
                                    {
                                        // TODO show you progress image
                                        //super.onPageStarted(view, url, favicon);
                                        try {
                                            pd.show();
                                            pd.setMessage("Cargando... Por favor espere!!");
                                        } catch (Exception ex) {

                                        }

                                    }

                                    @Override
                                    public void onReceivedError(WebView view, int errorCode,
                                                                String description, String failingUrl) {
                                        Log.e("ErrorQAWV"," Error occured while loading the web page at Url"+ failingUrl+"." +description);
                                        view.loadUrl("about:blank");
                                        Toast.makeText(nContext,"Error occured, please check newtwork connectivity", Toast.LENGTH_SHORT).show();
                                        new PopUp(nContext,"No hay conexión a internet","No hay conexión a internet",PopUp.POPUP_INFORMATION).showPopUp();
                                        //super.onReceivedError(view, errorCode, description, failingUrl);
                                    }

                               });


            //wb_QA.loadUrl("http://192.168.167.191:888/pages/Quality/FrmQAIBMovil.aspx?key=QAMovil&folio=217-22617&iPlant=2");
        wb_QA.loadUrl(config.pathEmpaqueQAIB+"?x=4");

       /* btnScanUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(nContext);
                scanIntegrator.initiateScan();
            }
        });*/

        btnScanFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMPEmpaque.qaUser = 0;
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
    }

    public static void validateUserQRCode(String UserCode) {
        //if(config.validaString(UserCode, nContext) == 7){ //QAUser
            String []infoQR = UserCode.split("\\|");
       // Toast.makeText(nContext, UserCode, Toast.LENGTH_LONG).show();
        try {
            //Toast.makeText(nContext, infoQR.length+"", Toast.LENGTH_LONG).show();
            if (infoQR.length == 4) {

                if(infoQR[0].compareToIgnoreCase("QA") == 0) {

                    View v;
                    inflater = nContext.getLayoutInflater();
                    v = inflater.inflate(R.layout.vw_qaib, null, true);
                    config.updateContent(content, v);

                    initComponents(v);


                    String user = infoQR[1]; //usuario
                    String Farm = infoQR[2]; //Planta

                    config.actualizarSharedPreferencesString(nContext, "QAUser", user);
                    config.actualizarSharedPreferencesString(nContext, "QAFarm", Farm);
                    //sharedPreferences

                    userName.setText(sharedPreferences.getString("QAUser", "Leer el codigo de usuario") + " - " + sharedPreferences.getString("QAFarm", ""));
                    //Toast.makeText(nContext, "user " + user + " - planta" + Farm, Toast.LENGTH_LONG).show();
                    txtFolio.setText("Presiona para escanear el folio");
                }else{
                    Toast.makeText(nContext, "No es un codigo QR de Calidad, no puedes accesar...", Toast.LENGTH_LONG).show();
                    new PopUp(nContext, "No es un usuario de calidad, No puedes accesar...", "No tienes acceso", PopUp.POPUP_INCORRECT).showPopUp();
                    content.removeAllViewsInLayout();
                    config.backContent(content);

                    WMPEmpaque.tipoApp = 0;
                    WMPEmpaque.setAvisos(nContext);
                }
            } else {
                Toast.makeText(nContext, "No tiene estructura de un Usuario de Calidad, no puedes accesar...", Toast.LENGTH_LONG).show();
                new PopUp(nContext, "No es un usuario de calidad, No puedes accesar...", "No tienes acceso", PopUp.POPUP_INCORRECT).showPopUp();
                content.removeAllViewsInLayout();
                config.backContent(content);

                WMPEmpaque.tipoApp = 0;
                WMPEmpaque.setAvisos(nContext);
            }
        }catch(Exception ex){
            Toast.makeText(nContext, "Error - "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }



       /* }else{
            Toast.makeText(nContext, "El codigo leido no tiene la estructura de un codigo de Usuario de Calidad.", Toast.LENGTH_SHORT).show();
        }*/
    }

    public static void validaFolio(String folio) {
        if(sharedPreferences.getString("QAUser", "").compareToIgnoreCase("") == 0 || sharedPreferences.getString("QAFarm", "").compareToIgnoreCase("") == 0){
            Toast.makeText(nContext, "Por favor lee primero el usuario", Toast.LENGTH_LONG).show();
            new PopUp(nContext, "Lee primero el usuario por favor", "Error de proceso", PopUp.POPUP_INFORMATION).showPopUp();
        }else {
            pd.show();
            pd.setMessage("Cargando Folio... Por favor espere!!");

            wb_QA.loadUrl(config.pathEmpaqueQAIB+"?key=QAMovil&folio="+folio+"&iPlant="+sharedPreferences.getString("QAFarm", "")+"&user="+sharedPreferences.getString("QAUser", ""));


        }

    }
}
