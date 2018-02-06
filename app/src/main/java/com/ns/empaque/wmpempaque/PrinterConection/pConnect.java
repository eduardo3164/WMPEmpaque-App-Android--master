package com.ns.empaque.wmpempaque.PrinterConection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import com.zebra.isv.util.DemoSleeper;
//import com.zebra.isv.util.SettingsHelper;
import com.ns.empaque.wmpempaque.CasesPrinter.caseIncrement;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.WMPEmpaque;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

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

/**
 * Created by jcalderon on 15/11/2016.
 */
public class pConnect {

    private Activity nContext;
    private ZebraPrinter printer;
    private Connection printerConnection;
    final public TextView Status;

    private SimpleDateFormat formatoFecha;
    private Date fechaActual;
    private String IPImpresora;
    private String puertoImpresora;

    public pConnect(Activity nContext, TextView Status){
        this.nContext = nContext;
        this.Status = Status;

        formatoFecha = new SimpleDateFormat("yyMMdd");
        fechaActual = new Date();

        IPImpresora = WMPEmpaque.ipImpresoraConfigurada();
        puertoImpresora = WMPEmpaque.puertoImpresoraConfigurada();
    }

    /*public void print(final ArrayList<caseIncrement> listToPrint) {
        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Status.setText("Imprimiendo...");
            }
        });

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                doConnection(listToPrint);
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }*/

    public void print(final ArrayList<caseIncrement> caseIncList, boolean calidad) {
        new asyncTaskGetInfoCasePrint(config.rutaWebServerOmar+"/getInfoCasePrint", caseIncList, calidad).execute();
    }

    public void printPP(final int nPrint, final String palletID, final String SKU) {

        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Status.setText("Imprimiendo...");
            }
        });


        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                doConnectionPP(nPrint, palletID, SKU);
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    private void doConnection(ArrayList<caseIncrement> listToPrint, boolean calidad) {
        printer = connect();
        if (printer != null) {
            for(int i = 0; i < listToPrint.size(); i++) {
                caseIncrement ci = listToPrint.get(i);
                sendTestLabel(ci, calidad);
            }
            disconnect();
        } else {
            disconnect();
        }
    }

    /*private void doConnection(caseIncrement caseInc) {
        printer = connect();
        if (printer != null) {
            sendTestLabel(caseInc);
            disconnect();
        } else {
            disconnect();
        }
    }*/

    private void doConnectionPP(int nPrint, String palletID, String SKU) {
        printer = connect();
        if (printer != null) {
            sendTestLabelPP(nPrint, palletID, SKU);
            disconnect();
        } else {
            disconnect();
        }
    }

    private ZebraPrinter connect() {
        //setStatus("Connecting...", Color.YELLOW);
        //Toast.makeText(nContext, "Conectando...", Toast.LENGTH_LONG).show();
        nContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Status.setText("Conectando...");
            }
        });

        printerConnection = null;
       // if (isBluetoothSelected()) {NO SERÁ POR BLUETOOTH
       //     printerConnection = new BluetoothConnection(getMacAddressFieldText());
       //     SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());
      //  } else {
            try {
                int port = Integer.parseInt(puertoImpresora);
                printerConnection = new TcpConnection(IPImpresora, port);
               // SettingsHelper.saveIp(this, getTcpAddress());
              //  SettingsHelper.savePort(this, getTcpPortNumber());
            } catch (NumberFormatException e) {
               // setStatus("Port Number Is Invalid", Color.RED);
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Status.setText("Número de puerto invalido...");
                    }
                });

              //  Toast.makeText(nContext, "Número de puerto invalido", Toast.LENGTH_LONG).show();
                return null;
            }
      //  }

        try {
            printerConnection.open();
          //  setStatus("Connected", Color.GREEN);
           // Toast.makeText(nContext, "Conectado", Toast.LENGTH_LONG).show();
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("Conectado!");
                }
            });
        } catch (ConnectionException e) {
           // setStatus("Comm Error! Disconnecting", Color.RED);
            //Toast.makeText(nContext, "Comm Error! Desconectando...", Toast.LENGTH_LONG).show();
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Status.setText("Comm Error! Desconectando...");
                }
            });

            Log.e("Com error", e.getMessage());
            DemoSleeper.sleep(1000);
            disconnect();
        }

        ZebraPrinter printer = null;

        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
               // setStatus("Determining Printer Language", Color.YELLOW);
              //  Toast.makeText(nContext, "Determinando el lenguaje de la impresora", Toast.LENGTH_SHORT).show();
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Status.setText("Determ. Lenguaje. Impre.");
                    }
                });

                final PrinterLanguage pl = printer.getPrinterControlLanguage();
               // setStatus("Printer Language " + pl, Color.BLUE);
             //   Toast.makeText(nContext, "Lenguaje de la impresora "+pl, Toast.LENGTH_SHORT).show();

                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Status.setText("Lenguaje: "+pl);
                    }
                });
            } catch (ConnectionException e) {
                //setStatus("Unknown Printer Language", Color.RED);
               // Toast.makeText(nContext, "Lenguaje de la impresora desconocido...", Toast.LENGTH_LONG).show();
                nContext.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {

                       Status.setText("Error: Lenguaje desconocido");
                   }
                });

                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
              //  setStatus("Unknown Printer Language", Color.RED);
               // Toast.makeText(nContext, "Lenguaje de la impresora desconocido...", Toast.LENGTH_LONG).show();
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Status.setText("Error: Lenguaje desconocido");
                    }
                });

                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    private void disconnect() {
        try {
            //setStatus("Disconnecting", Color.RED);
            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("Desconectando...");
                }
            });
            //Toast.makeText(nContext, "Desconectando...", Toast.LENGTH_LONG).show();
            if (printerConnection != null) {
                printerConnection.close();
            }


            //setStatus("Not Connected", Color.RED);
           // Toast.makeText(nContext, "No conectado", Toast.LENGTH_LONG).show();

            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("");
                }
            });

        } catch (ConnectionException e) {
           // setStatus("COMM Error! Disconnected", Color.RED);
          //  Toast.makeText(nContext, "Error desconectando...", Toast.LENGTH_LONG).show();

            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("Error Desconectando...");
                }
            });
        } finally {
        }
    }

    private void sendTestLabel(final caseIncrement caseInc, boolean calidad) {

        try {

            byte[] configLabel = getConfigLabel(caseInc, calidad);
            printerConnection.write(configLabel);

            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Status.setText("Enviando Datos... "+sToPrint);
                    Status.setText("Enviando Datos...");
                }
            });

            DemoSleeper.sleep(1500);
            /*if (printerConnection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                setStatus(friendlyName, Color.MAGENTA);
                DemoSleeper.sleep(500);
            }*/

        } catch (ConnectionException e) {
            //setStatus(e.getMessage(), Color.RED);
            Toast.makeText(nContext, e.getMessage(), Toast.LENGTH_LONG).show();
            // } finally {
            //       disconnect();
        }
    }

    private byte[] getConfigLabel(caseIncrement caseInc, boolean calidad) {
        PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

        byte[] configLabel = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            //configLabel = (leerArchivo()).getBytes();

            if(calidad){
                configLabel = ("^XA" +

                                "^PON" +
                                "^LH0,0" +
                                "^BY1,3,50" +
                                "^FO20,20" +
                                "^BCN,,Y,N,N,D" +
                                "^FD(01)" + caseInc.getGTIN() + ">8(10)" + caseInc.getCaseCodeHeader() + "^FS (BARCODE)" +

                                "^FWR" +
                                "^FO595,45" +
                                "^A0N,45,40" +
                                "^FDCALIDAD^FS (TEXT)" +

                                "^FO570,110^BY2" +
                                "^BUN,60" +
                                "^FD" + (caseInc.getGTIN().substring(2, caseInc.getGTIN().length())) + "^FS (BARCODE)" +

                                "^FO290,110" +
                                "^BQN,2,10" +
                                "^FDQA," + caseInc.getCaseCode() + "^FS (QR)" +

                                "^FWR" +
                                "^FO320,380" +
                                "^A0N,25,25" +
                                "^FD" + caseInc.getCaseCodeHeader() + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,120" +
                                "^A0N,35,30" +
                                "^FD" + caseInc.getDescription() + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,160" +
                                "^A0N,30,25" +
                                "^FD" + caseInc.getUnits() + "/" + caseInc.getOZ() + " OZ BOWL^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,195" +
                                "^A0N,30,25" +
                                "^FDProduct of Mexico^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,280" +
                                "^A0N,25,18" +
                                "^FDNatureSweet LTD, San^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,305" +
                                "^A0N,25,18" +
                                "^FDAntonio, TX 2013 Susp.^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,330" +
                                "^A0N,25,18" +
                                "^FDAgr. ID # A201 820 710^FS (TEXT)" +

                                "^FO570,240" +
                                "^GB180,60,2^FS (SQUARE)" +

                                "^FWR" +
                                "^FO580,255" +
                                "^A0N,40,40" +
                                "^FD" + config.mesesEN[Integer.parseInt(caseInc.getDatePack().substring(2,4))] + caseInc.getDatePack().substring(4,6) + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO570,220" +
                                "^A0N,25,18" +
                                "^FDPack Date^FS (TEXT)" +

                                "^LRY" +
                                "^FO560,300" +
                                "^GB200,1,100^FS (FILL SQUARE)" +

                                "^FWR" +
                                "^FO630,355^CFG" +
                                "^A0N,30,30" +
                                "^FD" + caseInc.getVoicePickCode().substring(0, 2) + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO660,320^CFG" +
                                "^A0N,80,80" +
                                "^FD" + caseInc.getVoicePickCode().substring(2, 4) + "^FS (TEXT)" +

                                "^XZ").getBytes();
            } else {
                configLabel = ("^XA" +

                                "^PON" +
                                "^LH0,0" +
                                "^BY1,3,50" +
                                "^FO20,20" +
                                "^BCN,,Y,N,N,D" +
                                "^FD(01)" + caseInc.getGTIN() + ">8(10)" + caseInc.getCaseCodeHeader() + "^FS (BARCODE)" +

                                "^FO570,110^BY2" +
                                "^BUN,60" +
                                "^FD" + (caseInc.getGTIN().substring(2, caseInc.getGTIN().length())) + "^FS (BARCODE)" +

                                "^FO290,110" +
                                "^BQN,2,10" +
                                "^FDQA," + caseInc.getCaseCode() + "^FS (QR)" +

                                "^FWR" +
                                "^FO300,380" +
                                "^A0N,25,25" +
                                "^FD" + caseInc.getCaseCode() + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,120" +
                                "^A0N,35,30" +
                                "^FD" + caseInc.getDescription() + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,160" +
                                "^A0N,30,25" +
                                "^FD" + caseInc.getUnits() + "/" + caseInc.getOZ() + " OZ BOWL^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,195" +
                                "^A0N,30,25" +
                                "^FDProduct of Mexico^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,280" +
                                "^A0N,25,18" +
                                "^FDNatureSweet LTD, San^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,305" +
                                "^A0N,25,18" +
                                "^FDAntonio, TX 2013 Susp.^FS (TEXT)" +

                                "^FWR" +
                                "^FO20,330" +
                                "^A0N,25,18" +
                                "^FDAgr. ID # A201 820 710^FS (TEXT)" +

                                "^FO570,240" +
                                "^GB180,60,2^FS (SQUARE)" +

                                "^FWR" +
                                "^FO580,255" +
                                "^A0N,40,40" +
                                "^FD" + config.mesesEN[Integer.parseInt(caseInc.getDatePack().substring(2,4))] + caseInc.getDatePack().substring(4,6) + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO570,220" +
                                "^A0N,25,18" +
                                "^FDPack Date^FS (TEXT)" +

                                "^LRY" +
                                "^FO560,300" +
                                "^GB200,1,100^FS (FILL SQUARE)" +

                                "^FWR" +
                                "^FO630,355^CFG" +
                                "^A0N,30,30" +
                                "^FD" + caseInc.getVoicePickCode().substring(0, 2) + "^FS (TEXT)" +

                                "^FWR" +
                                "^FO660,320^CFG" +
                                "^A0N,80,80" +
                                "^FD" + caseInc.getVoicePickCode().substring(2, 4) + "^FS (TEXT)" +

                                "^XZ").getBytes();
            }
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            String cpclConfigLabel = "! 0 400 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
            configLabel = cpclConfigLabel.getBytes();
        }

        return configLabel;
    }

    private String leerArchivo(){
        String pathTXT = Environment.getExternalStorageDirectory() + "/txt/";

        File file = new File(pathTXT, "file.txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("leerArchivo()", text.toString());

        return text.toString();
    }

    private void sendTestLabelPP(int nPrint, final String palletID, final String SKU) {

        try {
            byte[] configLabel = getConfigLabel(palletID, SKU);

            for(int i = 0; i < nPrint; i++) {
                printerConnection.write(configLabel);
            }

            nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("Enviando Datos...");
                }
            });

            DemoSleeper.sleep(1500);
            /*if (printerConnection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                setStatus(friendlyName, Color.MAGENTA);
                DemoSleeper.sleep(500);
            }*/

        } catch (ConnectionException e) {
            //setStatus(e.getMessage(), Color.RED);
            Toast.makeText(nContext, e.getMessage(), Toast.LENGTH_LONG).show();
            // } finally {
            //       disconnect();
        }
    }

    private byte[] getConfigLabel(String palletID, String SKU) {
        PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

        byte[] configLabel = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            //configLabel = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ".getBytes();

            configLabel = ( "^XA" +

                            "^FO70,80^BY3" +
                            "^B3N,N,100,Y,N" +
                            "^FD" + SKU + "^FS (SKU)" +

                            "^FO500,30" +
                            "^BQ,2,8" +
                            "^FDQA," + palletID + "^FS (QR)" +

                            "^FO490,210" +
                            "^A0N,32,25" +
                            "^FD" + palletID + "^FS (TEXT)" +

                            "^FO70,260^BY3" +
                            "^BCN,100,Y,N,N,A" +
                            "^FD" + palletID + "^FS  (BARCODE)" +

                            "^XZ").getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            String cpclConfigLabel = "! 0 400 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
            configLabel = cpclConfigLabel.getBytes();
        }
        return configLabel;
    }

    private class asyncTaskGetInfoCasePrint extends AsyncTask<String, String, String> {

        private String url;
        private ProgressDialog pd;
        private ArrayList<caseIncrement> caseInc;
        private boolean impCalidad;
        private boolean error = false;

        public asyncTaskGetInfoCasePrint(String url, ArrayList<caseIncrement> caseInc, boolean calidad) {
            this.url = url;
            this.caseInc = caseInc;
            this.impCalidad = calidad;
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

            //Log.d("SKU", caseInc.getSKU());

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("SKU", caseInc.get(0).getSKU()));

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
            } catch (Exception t) {
                error = true;
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute (String res) {
            Log.d("getInfo -- >", res);

            try {
                JSONObject objJSON = new JSONObject(res);
                JSONArray JSONCaseInfo = objJSON.getJSONArray("table1");

                if(JSONCaseInfo.length() > 0) {
                    for (int i = 0; i < JSONCaseInfo.length(); i++) {
                        JSONObject row = JSONCaseInfo.getJSONObject(i);

                        for (int j = 0; j < caseInc.size(); j++){
                            caseInc.get(j).setSKU(row.getString("SKU"));
                            caseInc.get(j).setGTIN(row.getString("GTIN"));
                            caseInc.get(j).setDescription(row.getString("description"));
                            caseInc.get(j).setUnits(row.getString("units"));
                            caseInc.get(j).setOZ(row.getString("OZ"));
                            caseInc.get(j).setDatePack(formatoFecha.format(fechaActual));
                        }
                    }
                } else {
                    error = true;
                }
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
                //Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                //Log.e("Error recibir datos", e.getMessage());
            }

            if(!error)
                new generateVoicePickCode(config.rutaWebServerOmar+"/generateVoicePickCode", caseInc, impCalidad).execute();
            else
                Toast.makeText(nContext, "Error al imprimir", Toast.LENGTH_LONG).show();

            pd.dismiss();
        }
    }

    private class generateVoicePickCode extends AsyncTask<String, String, String> {

        private String url;
        //private ProgressDialog pd;
        private ArrayList<caseIncrement> caseInc;
        private boolean impCalidad;
        private boolean error = false;

        public generateVoicePickCode(String url, ArrayList<caseIncrement> caseInc, boolean calidad) {
            this.url = url;
            this.caseInc = caseInc;
            this.impCalidad = calidad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(nContext);
            pd.setIndeterminate(true);
            pd.setMessage("Cargando...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... args) {
            String jsoncadena = "", step = "0";

            try {
                step = "1";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("GTIN", caseInc.get(0).getGTIN()));
                params.add(new BasicNameValuePair("lot", caseInc.get(0).getCaseCodeHeader()));
                params.add(new BasicNameValuePair("packDate", caseInc.get(0).getDatePack()));

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
            } catch (Exception t) {
                error = true;
                jsoncadena = "" + t.getMessage() + " -- step: " + step;
            }

            return jsoncadena;
        }

        @Override
        protected void onPostExecute (String res) {
            Log.d("generateVoice -- >", res);

            try {
                JSONObject objJSON = new JSONObject(res);
                JSONArray JSONCaseInfo = objJSON.getJSONArray("table1");

                if(JSONCaseInfo.length() > 0) {
                    for (int i = 0; i < JSONCaseInfo.length(); i++) {
                        JSONObject row = JSONCaseInfo.getJSONObject(i);

                        for (int j = 0; j < caseInc.size(); j++)
                            caseInc.get(j).setVoicePickCode(row.getString("voicePickCode"));
                    }
                } else {
                    error = true;
                }
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
                //Toast.makeText(nContext, nContext.getString(R.string.errorToRecieveData) + " - Hay un problema con el Servicio Web", Toast.LENGTH_LONG).show();
                //Log.e("Error recibir datos", e.getMessage());
            }

            if(!error){
                nContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Status.setText("Imprimiendo...");
                    }
                });

                new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        doConnection(caseInc, impCalidad);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }).start();
            } else {
                Toast.makeText(nContext, "Error al imprimir", Toast.LENGTH_LONG).show();
            }
        }
    }
}
