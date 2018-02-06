package com.ns.empaque.wmpempaque.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ns.empaque.wmpempaque.AsignarPrepallets.CaseCode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by jcalderon on 15/11/2016.
 */
public class casesIncrementHeaderDB {

    private static SQLiteDatabase db;
    private static DateFormat dateFormat;
    private static Calendar cal;
    private static Context nContext;
    private static SharedPreferences sharedpreferences;

    public static String TBL_CASES_INCREMENT = "tblCasesIncrementHeader";

    public static String VCODECASE = "vCodeCase";
    public static String SKU_CC = "vSKU";
    public static String VFOLIO = "vFolio";
    public static String VGH = "vGreenHouse";
    public static String VSIZE = "vSize";
    public static String LINEGP = "iLineNumber";
    public static String LINEPACKAGE = "iLinePackage";
    public static String NAMELINE = "vNameLine";
    public static String IFARM = "iFarm";
    public static String VCOMPANY = "vCompany";
    public static String IWEEK = "iWeek";
    public static String VHOUR = "vHour";
    public static String VDAY = "vDay";

    public static String UUID = "UUID";


    public casesIncrementHeaderDB(SQLiteDatabase db, Context nContext){
        this.db = db;
        this.nContext = nContext;

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal = Calendar.getInstance();
    }

    public static void crearTablaCasesIncrement(){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CASES_INCREMENT + " (" +
                VCODECASE + " TEXT, " +
                VGH + " TEXT, " +
                VFOLIO + " TEXT," +
                VSIZE + " TEXT, " +
                LINEGP + " TEXT, " +
                LINEPACKAGE + " TEXT, " +
                NAMELINE + " TEXT, " +
                IFARM + " TEXT, " +
                VCOMPANY + " TEXT, " +
                SKU_CC + " TEXT, " +
                IWEEK + " TEXT, " +
                VHOUR + " TEXT, " +
                VDAY + " TEXT, " +
                UUID+" TEXT,"+
                BaseDatos.ACTIVE + " INTEGER," +
                BaseDatos.FECHA_REGISTRO + " DATETIME, " +
                BaseDatos.FECHA_ACTUALIZACION + " DATETIME, " +
                BaseDatos.USERCREATED + " TEXT, " +
                BaseDatos.USERUPDATED + " TEXT, " +
                BaseDatos.SYNC + " INTEGER)");
    }

    public static void borrarTablaCasesIncrement(){
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CASES_INCREMENT);
    }

    public static long inserCaseIncrementHeader(CaseCode cc) {
        ContentValues cv = new ContentValues();

        cv.put(VCODECASE, cc.getCode());

        cv.put(VGH,cc.getGreenHouse());
        cv.put(VFOLIO, cc.getFolio());
        cv.put(VSIZE, cc.getSize());
        cv.put(LINEGP, cc.getIdGPLine());
        cv.put(LINEPACKAGE, cc.getIdLinePackage());
        cv.put(NAMELINE, cc.getNombreLinea());
        cv.put(IFARM, cc.getFarm());
        cv.put(VCOMPANY, cc.getCompany());
        cv.put(SKU_CC, cc.getSKU());
        cv.put(IWEEK, cc.getWeek());
        cv.put(VHOUR, cc.getHour());
        cv.put(VDAY, cc.getDay());

        cv.put(UUID, cc.getUUIDHeader());
        cv.put(BaseDatos.ACTIVE, "1");
        cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERCREATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.SYNC, cc.getActive()+"");

        return db.insert(TBL_CASES_INCREMENT, null, cv);
    }

    public static long insertCaseIncrementHeader(CaseCode cc) {
        ContentValues cv = new ContentValues();

        cv.put(VCODECASE, cc.getCode());

        cv.put(VGH,cc.getGreenHouse());
        cv.put(VFOLIO, cc.getFolio());
        cv.put(VSIZE, cc.getSize());
        cv.put(LINEGP, cc.getIdGPLine());
        cv.put(LINEPACKAGE, cc.getIdLinePackage());
        cv.put(NAMELINE, cc.getNombreLinea());
        cv.put(IFARM, cc.getFarm());
        cv.put(VCOMPANY, cc.getCompany());
        cv.put(SKU_CC, cc.getSKU());
        cv.put(IWEEK, cc.getWeek());
        cv.put(VHOUR, cc.getHour());
        cv.put(VDAY, cc.getDay());

        cv.put(UUID, cc.getUUIDHeader());
        cv.put(BaseDatos.ACTIVE, "1");
        cv.put(BaseDatos.FECHA_REGISTRO, cc.getCreatedDate());
        cv.put(BaseDatos.FECHA_ACTUALIZACION, cc.getUpdateDate());
        cv.put(BaseDatos.USERCREATED, cc.getCreatedUser());
        cv.put(BaseDatos.USERUPDATED, cc.getCreatedUser());
        cv.put(BaseDatos.SYNC, cc.getActive()+"");

        return db.insert(TBL_CASES_INCREMENT, null, cv);
    }

    public static ArrayList<CaseCode> getCasesCodeHeader(){
        ArrayList<CaseCode> ccList = new ArrayList<>();

        String columnas[] = {VCODECASE, VGH, VFOLIO, VSIZE, LINEGP, IFARM, VCOMPANY, IWEEK, VHOUR, VDAY, UUID, BaseDatos.SYNC, SKU_CC, NAMELINE, LINEPACKAGE};
        Cursor c = db.query(TBL_CASES_INCREMENT, columnas, null, null, null, null, BaseDatos.FECHA_REGISTRO + " DESC");

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            CaseCode cc = new CaseCode();
            cc.setGreenHouse(c.getString(1));
            cc.setFolio(c.getString(2));
            cc.setSize(c.getString(3));
            cc.setIdGPLine(c.getInt(4));
            cc.setFarm(c.getInt(5));
            cc.setCompany(c.getString(6));
            cc.setWeek(c.getInt(7));
            cc.setHour(c.getInt(8));
            cc.setDay(c.getInt(9));
            cc.setUUIDHeader(c.getString(10));
            cc.setSync(c.getInt(11));
            cc.setSKU(c.getString(12));
            cc.setNombreLinea(c.getString(13));
            cc.setIdLinePackage(c.getInt(14));

            ccList.add(cc);
        }

        c.close();

        return ccList;
    }

    public static ArrayList<CaseCode> getCasesCodeHeaderToSync(){
        ArrayList<CaseCode> ccList = new ArrayList<>();

        String columnas[] = {VCODECASE, VGH, VFOLIO, VSIZE, LINEGP, IFARM, VCOMPANY, IWEEK, VHOUR, VDAY, UUID,
                             BaseDatos.SYNC, BaseDatos.ACTIVE, BaseDatos.USERCREATED, BaseDatos.FECHA_REGISTRO, BaseDatos.USERUPDATED,
                             BaseDatos.FECHA_ACTUALIZACION, LINEPACKAGE};

        Cursor c = db.query(TBL_CASES_INCREMENT, columnas, BaseDatos.SYNC +" = 0", null, null, null, null);
        //String resultado [] = new String[c.getCount()];

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            //resultado[c.getPosition()] = c.getString(0);
            CaseCode cc = new CaseCode();
            cc.setGreenHouse(c.getString(1));
            cc.setFolio(c.getString(2));
            cc.setSize(c.getString(3));
            cc.setIdGPLine(c.getInt(4));
            cc.setFarm(c.getInt(5));
            cc.setCompany(c.getString(6));
            cc.setWeek(c.getInt(7));
            cc.setHour(c.getInt(8));
            cc.setDay(c.getInt(9));
            cc.setUUIDHeader(c.getString(10));
            cc.setSync(c.getInt(11));
            cc.setActive(c.getInt(12));
            cc.setCreatedUser(c.getString(13));
            cc.setCreatedDate(c.getString(14));
            cc.setUpdateUser(c.getString(15));
            cc.setUpdateDate(c.getString(16));
            cc.setIdLinePackage(c.getInt(17));

            ccList.add(cc);
        }

        c.close();

        return ccList;
    }

    public static ArrayList<CaseCode> getCasesCodeHeader(String caseCode){
        ArrayList<CaseCode> ccList = new ArrayList<>();

        String columnas[] = {VCODECASE, VGH, VFOLIO, VSIZE, LINEGP, IFARM, VCOMPANY, IWEEK, VHOUR, VDAY, UUID, BaseDatos.SYNC, SKU_CC, NAMELINE, LINEPACKAGE};
        //Cursor c = db.query(TBL_CASES_INCREMENT, columnas, VFOLIO+" = '"+vFolio+"' AND "+VCODECASE+" = '"+caseCode+"'", null, null, null, null);
        Cursor c = db.query(TBL_CASES_INCREMENT, columnas, VCODECASE+" = '"+caseCode+"'", null, null, null, null);
        //String resultado [] = new String[c.getCount()];

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            //resultado[c.getPosition()] = c.getString(0);
            CaseCode cc = new CaseCode();
            cc.setGreenHouse(c.getString(1));
            cc.setFolio(c.getString(2));
            cc.setSize(c.getString(3));
            cc.setIdGPLine(c.getInt(4));
            cc.setFarm(c.getInt(5));
            cc.setCompany(c.getString(6));
            cc.setWeek(c.getInt(7));
            cc.setHour(c.getInt(8));
            cc.setDay(c.getInt(9));
            cc.setUUIDHeader(c.getString(10));
            cc.setSync(c.getInt(11));
            cc.setSKU(c.getString(12));
            cc.setNombreLinea(c.getString(13));
            cc.setIdLinePackage(c.getInt(14));

            ccList.add(cc);
        }

        c.close();

        return ccList;
    }

    public static long InactiveRow(CaseCode cc){
        ContentValues cv = new ContentValues();
        cv.put(BaseDatos.ACTIVE, 0);
        //  return db.update(TBL_CASES_INCREMENT_DETAILS, cv, VCODECASE + "='" + ci.getCaseCode()+"' AND "+UUIDHEADER +" = '"+ci.getUUIDHeader()+"'", null);
        return db.delete(TBL_CASES_INCREMENT, VCODECASE + "='" + cc.getCode()+"' AND "+UUID +" = '"+cc.getUUIDHeader()+"'", null);
    }

    public static long updateSyncUUID(String uuid){

        ContentValues valores = new ContentValues();
        valores.put(BaseDatos.SYNC, 1);

        return db.update(TBL_CASES_INCREMENT, valores, UUID + " = '" + uuid + "'", null);
    }

    /*public String getUUIDHeader(String codeCase, String folio){
        String uuidHeader = "";

        Cursor c = db.rawQuery("SELECT " + UUID + " FROM " + TBL_CASES_INCREMENT + " WHERE " + VCODECASE + " = '" + codeCase + "' AND " + VFOLIO + " = '" + folio + "'", null);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            uuidHeader = c.getString(c.getColumnIndex(UUID));
        }

        c.close();

        return uuidHeader;
    }*/
}
