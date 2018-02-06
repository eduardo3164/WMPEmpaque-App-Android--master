package com.ns.empaque.wmpempaque.BaseDatos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ns.empaque.wmpempaque.insertLine.packTurnos;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by javier.calderon on 23/02/2017.
 */

public class ev2_tbl_cat_packTurnos {
    private static SQLiteDatabase db;
    private static DateFormat dateFormat;
    private static Calendar cal;
    private static Activity nContext;
    private static SharedPreferences sharedpreferences;

    public static String TBL_CAT_PACKTURNOS = "tbl_cat_packturnos";

    public static String IDTURNO = "idTurno";
    public static String NAMETURNO = "vNameTurno";
    public static String DESCTURNO = "vDescriptionTurno";
    public static String IDFARM = "idFarm";
    public static String BACTIVE = "bActive";

    public ev2_tbl_cat_packTurnos(SQLiteDatabase db, Activity nContext){
        this.db = db;
        this.nContext = nContext;

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal = Calendar.getInstance();
    }

    public static void createTableSync(){
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_PACKTURNOS);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_PACKTURNOS + " (" +
                IDTURNO + " INTEGER, " +
                NAMETURNO + " TEXT, " +
                DESCTURNO+" TEXT,"+
                IDFARM+" INTEGER,"+
                BACTIVE+" INTEGER)");
    }

    public static void createTable(){

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_PACKTURNOS + " (" +
                IDTURNO + " INTEGER, " +
                NAMETURNO + " TEXT, " +
                DESCTURNO+" TEXT,"+
                IDFARM+" INTEGER,"+
                BACTIVE+" INTEGER)");
    }


    public void insertPackTurnos(String idTurno, String nameTurno, String descriptionTurno,
                                 String idFarm, String active){

        ContentValues cv = new ContentValues();

        cv.put(IDTURNO, idTurno);
        cv.put(NAMETURNO, nameTurno);
        cv.put(DESCTURNO, descriptionTurno);
        cv.put(IDFARM, idFarm);
        cv.put(BACTIVE, active);
        db.insert(TBL_CAT_PACKTURNOS, null, cv);

       // Toast.makeText(nContext, active, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<packTurnos> getPackTurnosByPlant(String idPlant){
        //Toast.makeText(nContext, idPlant, Toast.LENGTH_SHORT).show();
        String columnas[] = {IDTURNO, NAMETURNO, DESCTURNO, IDFARM, BACTIVE};
        Cursor c = db.query(TBL_CAT_PACKTURNOS, columnas, BACTIVE+" = 1 AND "+IDFARM+" = "+idPlant, null, null, null, IDTURNO);
        //Cursor c = nBD.rawQuery("SELECT ", null);
        //String resultado [][] = new String[c.getCount()][columnas.length] ;

        ArrayList<packTurnos> al_packTurnos = new ArrayList<>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            packTurnos pt = new packTurnos();

            pt.setIdTurno(c.getInt(0));
            pt.setvNameTurno(c.getString(1));
            pt.setvDescriptionTurno(c.getString(2));
            pt.setIdPlant(c.getInt(3));
            pt.setbActive(Boolean.parseBoolean(c.getString(4)));

           // Toast.makeText(nContext, c.getString(1), Toast.LENGTH_SHORT).show();

            al_packTurnos.add(pt);
        }

        //for(int i=0; i<columnas.length; i++ )
        //resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return al_packTurnos;
    }
}
