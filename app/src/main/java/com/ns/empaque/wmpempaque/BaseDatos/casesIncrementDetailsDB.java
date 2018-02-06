package com.ns.empaque.wmpempaque.BaseDatos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;
import com.ns.empaque.wmpempaque.CasesPrinter.caseIncrement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jcalderon on 15/11/2016.
 */
public class casesIncrementDetailsDB {

    private static SQLiteDatabase db;
    private static DateFormat dateFormat;
    private static Calendar cal;
    private static Activity nContext;
    private static SharedPreferences sharedpreferences;

    public static String TBL_CASES_INCREMENT_DETAILS = "tblCasesIncrementDetails";

    public static String VCODECASE = "vCodeCase";
    public static String VCODECASEHEADER = "vCodeCaseHeader";
    public static String UUID = "UUID";
    public static String UUIDHEADER = "UUIDHeader";
    public static String VFOLIO = "vFolio";

    //public static String SKU_CID = "vSKU";
    //public static String GTIN_CID = "vGTIN";
    //public static String DESCRIPTION_CID = "vDescription";
    //public static String UNITS_CID = "vUnits";
    //public static String OZ_CID = "vOZ";

    public casesIncrementDetailsDB(SQLiteDatabase db, Activity nContext){
        this.db = db;
        this.nContext = nContext;

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal = Calendar.getInstance();
    }

    public static void createTable(){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CASES_INCREMENT_DETAILS + " (" +
                VCODECASE + " TEXT, " +
                VFOLIO + " TEXT, " +
                UUID+" TEXT,"+
                VCODECASEHEADER+" TEXT,"+
                UUIDHEADER+" TEXT,"+
                //SKU_CID + " TEXT,"+
                //GTIN_CID + " TEXT,"+
                //DESCRIPTION_CID + " TEXT,"+
                //UNITS_CID + " TEXT,"+
                //OZ_CID + " TEXT,"+
                BaseDatos.ACTIVE + " INTEGER," +
                BaseDatos.FECHA_REGISTRO + " DATETIME, " +
                BaseDatos.FECHA_ACTUALIZACION + " DATETIME, " +
                BaseDatos.USERCREATED + " TEXT, " +
                BaseDatos.USERUPDATED + " TEXT, " +
                BaseDatos.SYNC + " INTEGER)");
    }

    public static Long inserCaseIncrementDetails(caseIncrement ci) {

        ContentValues cv = new ContentValues();

        cv.put(VCODECASE, ci.getCaseCode());
        cv.put(VFOLIO, ci.getFolio());
        cv.put(UUID, ci.getUUID());
        cv.put(VCODECASEHEADER, ci.getCaseCodeHeader());
        cv.put(UUIDHEADER, ci.getUUIDHeader());

        //cv.put(SKU_CID, ci.getSKU());
        //cv.put(GTIN_CID, ci.getGTIN());
        //cv.put(DESCRIPTION_CID, ci.getDescription());
        //cv.put(UNITS_CID, ci.getUnits());
        //cv.put(OZ_CID, ci.getOZ());

        cv.put(BaseDatos.ACTIVE, ci.getActive()+"");
        cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERCREATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.SYNC, 0);

       return  db.insert(TBL_CASES_INCREMENT_DETAILS, null, cv);
    }

    public static ArrayList<caseIncrement> getCaseIncrementDetails(String vCodeCaseHeader){
        ArrayList<caseIncrement> ciList = new ArrayList<>();

        String columnas[] = {VCODECASE, UUID, UUIDHEADER, BaseDatos.ACTIVE, BaseDatos.SYNC, VFOLIO, VCODECASEHEADER/*,
                             SKU_CID, GTIN_CID, DESCRIPTION_CID, UNITS_CID, OZ_CID*/ };
        final Cursor c = db.query(TBL_CASES_INCREMENT_DETAILS, columnas, VCODECASEHEADER+" = '"+vCodeCaseHeader+"'", null, null, null, VCODECASE);
        //String resultado [] = new String[c.getCount()];

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            //resultado[c.getPosition()] = c.getString(0);
            caseIncrement ci = new caseIncrement();
            ci.setCaseCode(c.getString(0));
            ci.setUUID(c.getString(1));
            ci.setUUIDHeader(c.getString(2));
            ci.setActive(c.getInt(3));
            ci.setSync(c.getInt(4));
            ci.setFolio(c.getString(5));
            ci.setCaseCodeHeader(c.getString(6));

            //ci.setSKU(c.getString(7));
            //ci.setGTIN(c.getString(8));
            //ci.setDescription(c.getString(9));
            //ci.setUnits(c.getString(10));
            //ci.setOZ(c.getString(11));

            /*nContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(nContext, c.getString(0), Toast.LENGTH_SHORT).show();
                }
            });*/


            ciList.add(0,ci);
        }

        c.close();

        return ciList;
    }

    public static long InactiveRow(caseIncrement ci){
        ContentValues cv = new ContentValues();
        cv.put(BaseDatos.ACTIVE, 0);
      //  return db.update(TBL_CASES_INCREMENT_DETAILS, cv, VCODECASE + "='" + ci.getCaseCode()+"' AND "+UUIDHEADER +" = '"+ci.getUUIDHeader()+"'", null);
        return db.delete(TBL_CASES_INCREMENT_DETAILS, VCODECASE + "='" + ci.getCaseCode()+"' AND "+UUIDHEADER +" = '"+ci.getUUIDHeader()+"'", null);
    }

    public static long InactiveRowsByCaseCode(CaseCode c){
        ContentValues cv = new ContentValues();
        cv.put(BaseDatos.ACTIVE, 0);
        //  return db.update(TBL_CASES_INCREMENT_DETAILS, cv, VCODECASE + "='" + ci.getCaseCode()+"' AND "+UUIDHEADER +" = '"+ci.getUUIDHeader()+"'", null);
        return db.delete(TBL_CASES_INCREMENT_DETAILS, VCODECASEHEADER + "='" + c.getCode()+"'", null);

    }

    public static  ArrayList<caseIncrement> getCaseIncrementToSync(){
        ArrayList<caseIncrement> ciList = new ArrayList<>();

        String columnas[] = {VCODECASE, VFOLIO, UUID, VCODECASEHEADER, UUIDHEADER,
                             BaseDatos.SYNC, BaseDatos.ACTIVE, BaseDatos.USERCREATED, BaseDatos.FECHA_REGISTRO, BaseDatos.USERUPDATED,
                             BaseDatos.FECHA_ACTUALIZACION/*, SKU_CID, GTIN_CID, DESCRIPTION_CID, UNITS_CID, OZ_CID*/};

        Cursor c = db.query(TBL_CASES_INCREMENT_DETAILS, columnas,BaseDatos.SYNC +" = 0", null, null, null, null);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            //resultado[c.getPosition()] = c.getString(0);
            caseIncrement ci = new caseIncrement();
            ci.setCaseCode(c.getString(0));
            ci.setFolio(c.getString(1));
            ci.setUUID(c.getString(2));
            ci.setCaseCodeHeader(c.getString(3));
            ci.setUUIDHeader(c.getString(4));
            ci.setSync(c.getInt(5));
            ci.setActive(c.getInt(6));
            ci.setCreatedUser(c.getString(7));
            ci.setCreatedDate(c.getString(8));
            ci.setUpdateUser(c.getString(9));
            ci.setUpdateUser(c.getString(10));

            /*ci.setSKU(c.getString(11));
            ci.setGTIN(c.getString(12));
            ci.setDescription(c.getString(13));
            ci.setUnits(c.getString(14));
            ci.setOZ(c.getString(15));*/

            ciList.add(ci);
        }

        c.close();

        return ciList;
    }


    public static long updateSyncUUID(String uuid){

        ContentValues valores = new ContentValues();
        valores.put(BaseDatos.SYNC, 1);

        return db.update(TBL_CASES_INCREMENT_DETAILS, valores, UUID + " = '" + uuid+"'", null);
    }


}
