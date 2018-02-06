package com.ns.empaque.wmpempaque.AsignarPrepallets;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.BaseDatos.CasesPrePalletDB;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.PopUp.PopUp;
import com.ns.empaque.wmpempaque.PrinterConection.pConnect;
import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by jcalderon on 25/07/2016.
 */
public class AddPrePallet {

    private static Activity nContext;
    private static LayoutInflater inflater;
    private static FloatingActionButton fabAdd, fabSync;
    private static ListView lv_PrePallet;

    private static String idFarmSelected, idSiteSelected, SKUSelected;
    private static int posicionSize, posicionPromo, posicionDesgr;
    private static String[] codeSize, idPromo, desc;
    private static ArrayList<PrePallet> PrePalletList;
    private static listPPAdapter adaptadorPP;

    private static LinearLayout lytSitePP;
    private static Spinner cboxPlanta;
    private static Spinner cboxSite;
    private static Spinner cboxSKU;
    private static Spinner cboxTamanio;
    private static Spinner cboxPromotion;
    private static Spinner cboxDesgrane;
    private static ListView listaLineas;
    private static ArrayList<Linea> selectedItems;
    private static Button btnAceptar;
    private static Button btnCancelar;
    private static ArrayList<Integer> listaPlantaSRC;
    private static ArrayList<String> listaSitesSRC;
    private static ArrayList<Integer> listaLineaSRC;
    private static ArrayList<Integer> listaSizeSRC;
    private static ArrayList<String> listaPromoSRC;
    private static boolean lineasSeleccionadas[];
    public static SharedPreferences sharedPreferences;

    public AddPrePallet(Activity nContext) {
        this.nContext = nContext;
        selectedItems = new ArrayList<Linea>();
        sharedPreferences = nContext.getSharedPreferences("WMPEmpaqueApp", nContext.MODE_PRIVATE);
    }

    public static View getPopUpView(){
        View v;
        inflater = nContext.getLayoutInflater();
        v = inflater.inflate(R.layout.view_list_local_prepallet, null, true);

        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAddPP);
        fabSync = (FloatingActionButton) v.findViewById(R.id.fabSyncPP);
        lv_PrePallet = (ListView) v.findViewById(R.id.prepalletList);

        PrePalletList = new ArrayList<>();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfigPrePallet();
            }
        });
        fabSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.ns.empaque.wmpempaque.PrePallet.PrePallet pp = new com.ns.empaque.wmpempaque.PrePallet.PrePallet(nContext);
                pp.sincronizar();
            }
        });

        lv_PrePallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { }
        });

        fillList();

        return v;
    }

    public static void prePalletSincronizado(){
        fillList();

        PopUp p = new PopUp(nContext, "El PrePallet ha sido creado correctamente", PopUp.POPUP_OK);
        p.showPopUp();
    }

    public static void fillList(){
        PrePalletList.clear();
        BaseDatos db = new BaseDatos(nContext);
        db.abrir();
        Cursor c = db.cpdb.getAllPrePallets();

        if(c != null){
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                PrePallet pp = new PrePallet();

                Log.d("ID PREPALLET", c.getString(c.getColumnIndex(CasesPrePalletDB.IDPREPALLET)));

                pp.setIdPrePalletTablet(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLET)));
                pp.setIdPrePallet(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLETSERVER)));
                pp.setvPromotion(c.getString(c.getColumnIndex(CasesPrePalletDB.PROMOTIONS)));
                pp.setPromoDesc(c.getString(c.getColumnIndex(BaseDatos.DESCPROMOTIONS)));
                pp.setvSize(c.getString(c.getColumnIndex(CasesPrePalletDB.SIZE)));
                pp.setvSKU(c.getString(c.getColumnIndex(BaseDatos.VSKU)));
                pp.setIdFarm(c.getInt(c.getColumnIndex(BaseDatos.IDFARM)));
                pp.setPlantaName(c.getString(c.getColumnIndex(BaseDatos.NOMBREFARM)));
                //pp.setIdLinePackage(c.getInt(c.getColumnIndex(BaseDatos.ID_LINE)));
                pp.setLine(db.cpdb.getLinesPP(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLET))));

                pp.setNameLine(db.cpdb.getNameLines(c.getInt(c.getColumnIndex(CasesPrePalletDB.IDPREPALLET))));

                pp.setFullDateCreated(c.getString(c.getColumnIndex(BaseDatos.FECHA_REGISTRO)));
                pp.setSync(c.getInt(c.getColumnIndex(BaseDatos.SYNC)));
                pp.setCajas(c.getInt(c.getColumnIndex("cajasRegistradas")));
                pp.setCasesPerPallet(c.getInt(c.getColumnIndex("casesPerPallet")));
                pp.setvPalletID("null");

                PrePalletList.add(pp);
            }
        } else {
            Toast.makeText(nContext, "No se encontro preppalet en la tablet", Toast.LENGTH_LONG).show();
        }

        if(PrePalletList.size() > 0){
            adaptadorPP = new listPPAdapter(nContext, PrePalletList);
            lv_PrePallet.setAdapter(adaptadorPP);
        } else {
            String[] notFound = new String[1];
            notFound[0] = "No se encontraron prepallets!";
            ArrayAdapter<String> notFounAdapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_1, notFound);
            lv_PrePallet.setAdapter(notFounAdapter);
        }

        c.close();
        db.cerrar();
    }

    public static void showDialogConfigPrePallet(){
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.view_crear_prepallet, null);

        cboxPlanta = (Spinner) dialoglayout.findViewById(R.id.cboxPlantaPP);
        lytSitePP = (LinearLayout) dialoglayout.findViewById(R.id.lytSitePP);
        cboxSite = (Spinner) dialoglayout.findViewById(R.id.cboxSitePP);
        listaLineas = (ListView) dialoglayout.findViewById(R.id.listaLineasPP);
        cboxSKU = (Spinner) dialoglayout.findViewById(R.id.cboxSKUPP);
        cboxTamanio = (Spinner) dialoglayout.findViewById(R.id.cboxTamanioPP);
        cboxPromotion = (Spinner) dialoglayout.findViewById(R.id.cboxPromotionPP);
        cboxDesgrane = (Spinner) dialoglayout.findViewById(R.id.cboxDesgranePP);
        btnAceptar = (Button) dialoglayout.findViewById(R.id.btnAceptarPP);
        btnCancelar = (Button) dialoglayout.findViewById(R.id.btnCancelarPP);

        llenarSpinnerPlanta();
        llenarSizeSpinner();
        llenarPromoSpinner();
        llenarDesgrSpinner();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(nContext);
        alertDialog.setView(dialoglayout);
        alertDialog.setIcon(R.drawable.alerticon);
        alertDialog.setTitle("Para continuar con el guardado del pre-pallet, elige sus configuraciones y despues da click en \"Guardar\"");
        alertDialog.setCancelable(false);

        final AlertDialog ad = alertDialog.create();
        ad.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(selectedItems.size() > 0 && cboxSKU.getCount() > 0){
                    com.ns.empaque.wmpempaque.PrePallet.PrePallet pp = new com.ns.empaque.wmpempaque.PrePallet.PrePallet(nContext);
                    pp.setBoxes(null);
                    pp.setActive("1");
                    pp.setFarm(idFarmSelected);
                    pp.setLine(selectedItems);
                    pp.setSKU(SKUSelected);
                    pp.setFolios(null);
                    pp.setSize(codeSize[posicionSize]);
                    pp.setPromotion(idPromo[posicionPromo]);
                    pp.setDesgrane(posicionDesgr);

                    if (pp.savePrePallet() == 1) {
                        pp.sincronizar();
                        ad.dismiss();
                    } else {
                        PopUp p = new PopUp(nContext, "Error al crear el prepallet", PopUp.POPUP_INCORRECT);
                        p.showPopUp();
                    }

                    Toast.makeText(nContext, "OK", Toast.LENGTH_SHORT).show();
                    ad.dismiss();
                } else {
                    PopUp p = new PopUp(nContext, "Tiene que elegir una planta, una linea, un SKU, una promotion y un tamaño valido", PopUp.POPUP_INCORRECT);
                    p.showPopUp();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ad.dismiss();
            }
        });
    }

    private static void llenarSpinnerPlanta() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.obtenerFarmsToPrePallet();
        bd.cerrar();

        if(datos.length > 0){
            String []namePlant = new String[datos.length];
            listaPlantaSRC = new ArrayList<Integer>();

            for(int i = 0; i<datos.length; i++) {
                namePlant[i] = datos[i][1];
                listaPlantaSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, namePlant);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxPlanta.setAdapter(adapter);

            vaciarSpinnerSKU();

            cboxPlanta.setSelection(listaPlantaSRC.indexOf(sharedPreferences.getInt("idPlanta", listaPlantaSRC.get(0))));

            cboxPlanta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idFarmSelected = datos[position][0];

                    if(idFarmSelected.equals("3") || idFarmSelected.equals("9")){
                        llenarSpinnerSite(datos[position][0]);
                        lytSitePP.setVisibility(View.VISIBLE);
                    } else {
                        llenarSpinnerLinea(datos[position][0], "", false);
                        lytSitePP.setVisibility(View.GONE);
                    }

                    vaciarSpinnerSKU();
                    config.actualizarSharedPreferencesInt(nContext, "idPlanta", listaPlantaSRC.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

        } else {
            Toast.makeText(nContext, "No hay plantas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    private static void llenarSpinnerSite(String idPlanta) {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[] sites = bd.getSitesFarm(idPlanta);
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

            cboxSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idSiteSelected = sites[position];
                    llenarSpinnerLinea(idFarmSelected, idSiteSelected, true);
                    vaciarSpinnerSKU();

                    config.actualizarSharedPreferencesString(nContext, "idSite", listaSitesSRC.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

        } else {
            Toast.makeText(nContext, "No hay plantas en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }
    }

    private static void llenarSpinnerLinea(String idFarm, String idSite, boolean isUSA) {
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
            lineasSeleccionadas = new boolean[datos.length];

            listaLineaSRC = new ArrayList<Integer>();

            for(int i = 0; i < datos.length; i++) {
                nameLine[i] = datos[i][0] + " - " + datos[i][1];
                lineasSeleccionadas[i] = false;
                listaLineaSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_list_item_multiple_choice, nameLine);
            listaLineas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listaLineas.setAdapter(adapter);
        } else {
            vaciarListaLineas();
            vaciarSpinnerSKU();

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

                if(selectedItems.size() > 0)
                    llenarSpinnerSKU(selectedItems);
                else {
                    vaciarSpinnerSKU();
                }
            }
        });
    }

    private static void vaciarSpinnerSKU(){
        String vacio[] = new String[0];

        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, vacio);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboxSKU.setAdapter(adapter2);
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

    private static void llenarSpinnerSKU(ArrayList<Linea> lineas) {
        String strLineas = "";

        for(int i = 0; i < lineas.size(); i++)
            strLineas += lineas.get(i).getIdLinea() + ",";

        strLineas = strLineas.substring(0, strLineas.length() - 1);

        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        final String[][] datos = bd.getLinesSKU(strLineas);
        bd.cerrar();

        if(datos.length > 0){
            String[] skuName = new String [datos.length];

            for(int i = 0; i < datos.length; i++)
                skuName[i] = datos[i][0];

            final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, skuName);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxSKU.setAdapter(adapter2);
        } else {
            vaciarSpinnerSKU();
            Toast.makeText(nContext, "No hay Sku's en la base de datos, sincronice por favor", Toast.LENGTH_LONG).show();
        }

        cboxSKU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    SKUSelected = datos[position][0];
                }catch(Exception e){
                    SKUSelected = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void llenarSizeSpinner() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos [][] = bd.getSizeCode();
        bd.cerrar();

        if(datos.length > 0){
            codeSize = new String[datos.length];

            listaSizeSRC = new ArrayList<Integer>();

            for(int i = 0; i < datos.length; i++){
                codeSize[i] = datos[i][1];
                listaSizeSRC.add(Integer.parseInt(datos[i][0]));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, codeSize);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxTamanio.setAdapter(adapter);

            cboxTamanio.setSelection(listaSizeSRC.indexOf(sharedPreferences.getInt("idSize", listaSizeSRC.get(0))));
        }

        cboxTamanio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionSize = position;
                config.actualizarSharedPreferencesInt(nContext, "idSize", listaSizeSRC.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void llenarPromoSpinner() {
        BaseDatos bd = new BaseDatos(nContext);
        bd.abrir();
        String datos [][] = bd.getPromotions();
        bd.cerrar();

        if(datos.length > 0){
            idPromo = new String[datos.length];
            desc = new String[datos.length];

            listaPromoSRC = new ArrayList<String>();

            for(int i = 0; i<datos.length; i++){
                idPromo[i] = datos[i][0];
                desc[i] = datos[i][0] + " - " + datos[i][1];

                listaPromoSRC.add(datos[i][0]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item,desc );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cboxPromotion.setAdapter(adapter);

            cboxPromotion.setSelection(listaPromoSRC.indexOf(sharedPreferences.getString("idPromo", listaPromoSRC.get(0))));
        }

        cboxPromotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionPromo = position;
                config.actualizarSharedPreferencesString(nContext, "idPromo", listaPromoSRC.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private static void llenarDesgrSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(nContext, android.R.layout.simple_spinner_item, config.razonDesgrane);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboxDesgrane.setAdapter(adapter);

        cboxDesgrane.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionDesgr = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
