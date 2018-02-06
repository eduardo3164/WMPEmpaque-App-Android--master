package com.ns.empaque.wmpempaque.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.Modelo.config;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by jcalderon on 31/03/2016.
 */
public class CasesPrePalletDB {

    public static String TBL_PRE_PALLET = "tbl_PrePallet";
    public static String TBL_LINES_PRE_PALLET = "tbl_LinesPrePallet";
    public static String TBL_CASES = "tbl_Cases_Prepallet";
    public static String TBL_FOLIOS_PREPALLET = "tbl_folios_prepallet";
    public static String TBL_CASES_INCREMENT = "tblCases";
    public static String TBL_PP_CASESCDE = "tbl_pp_casesCode";

    //Campos de CaseIncrement
    //VCODECASE;
    //VFOLIO
    //VGH
    //VCASESIZE
    //LINENUMBER
    //IFARM
    public static String VCOMPANY = "vCompany";
    public static String IWEEK = "iWeek";


    //Campos tbl_pp_casesCode
    public static String IDPPCASESCODE = "idPPCasesCode";
    public static String VFOLIO = "vFolio";
    public static String VCODE = "vCode";
    public static String VGH = "vGreenHouse";
    public static String VSIZE = "vSize";
    public static String LINENUMBER = "iLineNumber";
    public static String IFARM = "iFarm";

    //Campos pre-pallet
    public static final String ID_LINE_PP = "idLinePrePallet";
    public static final String ID_PRE_PALLET_FK = "idPrePalletFK";
    public static final String UUID_PRE_PALLET = "uuidPrePallet";
    public static final String ID_LINE_FK ="idLineFK";
    public static final String UUID_LINE_PP ="uuid";
    public static final String SYNC_LINE_PP ="sync";

    //Campos pre-pallet
    public static final String IDPREPALLET = "idPrepallet";
    public static final String IDPREPALLETSERVER = "idPrepalletServer";
    public static final String PROMOTIONS ="Promotion";
    public static final String RAZON_DESGRANE ="rDesgrane";
    public static final String SIZE = "Size";
    public static final String UNICKEY = "unicKey";

    //Campos cases
    public static String IDCASE = "idCasePrepallet";
    public static String IDCASESERVER = "idCasePrepalletServer";
    public static String VCODECASE = "vCodeCase";

    //Campos FoliosPrePallet
    public static String IDFOLIOPREPALLET = "idFolioPrePallet";
    public static String IDFOLIOPREPALLETSERVER = "idFolioPrepalletServer";
    public static String VCODEFOLIO = "vCodeFolio";
    public static String ICAJAS = "iCajas";
    public static String IDPRODUCTLOG = "idProductLog";

    private static SQLiteDatabase db;
    private static  DateFormat dateFormat;
    private static Calendar cal;
    private static Context nContext;

    private static String unicID;
    private static SharedPreferences sharedpreferences;

    public CasesPrePalletDB(SQLiteDatabase db, Context nContext){
        this.db = db;
        this.nContext = nContext;

        sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);

        unicID = sharedpreferences.getString("unicID","-1");
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        cal = Calendar.getInstance();
    }

    public static int difBoxesByFolio(String folio){
        Cursor c = db.rawQuery("SELECT SUM("+ICAJAS+") FROM "+TBL_FOLIOS_PREPALLET+" WHERE "+VCODEFOLIO+" = '"+folio+"' GROUP BY "+VCODEFOLIO, null);

        c.moveToFirst();
        int Boxes = c.getInt(0);
        c.close();

        return Boxes;
    }

    public static void createTableCases(){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CASES + " (" +
                IDCASE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IDCASESERVER + " INTEGER, " +
                VCODECASE + " TEXT," +
                IDPREPALLET + " INTEGER," +
                IDPREPALLETSERVER + " INTEGER, " +
                UNICKEY+" TEXT,"+
                BaseDatos.ACTIVE + " INTEGER," +
                BaseDatos.FECHA_REGISTRO + " DATETIME, " +
                BaseDatos.FECHA_ACTUALIZACION + " DATETIME, " +
                BaseDatos.USERCREATED + " TEXT, " +
                BaseDatos.USERUPDATED + " TEXT, " +
                BaseDatos.SYNC + " INTEGER)");
    }

    public static void createTableFolios(){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_FOLIOS_PREPALLET + " (" +
                IDFOLIOPREPALLET + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IDFOLIOPREPALLETSERVER + " INTEGER, " +
                VCODEFOLIO + " TEXT," +
                ICAJAS+ " INTEGER," +
                IDPREPALLET+ " INTEGER," +
                IDPREPALLETSERVER + " INTEGER, "+
                IDPRODUCTLOG + " INTEGER,"+
                BaseDatos.ACTIVE + " INTEGER," +
                BaseDatos.FECHA_REGISTRO + " DATETIME, " +
                BaseDatos.FECHA_ACTUALIZACION + " DATETIME, " +
                BaseDatos.USERCREATED + " TEXT, " +
                BaseDatos.USERUPDATED + " TEXT, " +
                BaseDatos.SYNC + " INTEGER)");


    }

    public static void createTablePrePallet() {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_PRE_PALLET + " (" +
                IDPREPALLET + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IDPREPALLETSERVER + " INTEGER, " +
                PROMOTIONS + " TEXT,"+
                RAZON_DESGRANE + " TEXT,"+
                SIZE + " TEXT,"+
                BaseDatos.IDFARM + " INTEGER, " +
                BaseDatos.ID_LINE + " INTEGER, " +
                BaseDatos.VSKU + " INTEGER, " +
                BaseDatos.ACTIVE + " INTEGER," +
                UNICKEY+" TEXT,"+
                BaseDatos.FECHA_REGISTRO + " DATETIME, " +
                BaseDatos.FECHA_ACTUALIZACION + " DATETIME, " +
                BaseDatos.USERCREATED + " TEXT, " +
                BaseDatos.USERUPDATED + " TEXT, " +
                BaseDatos.SYNC + " INTEGER)");
    }

    public static void createTableLinesPrePallet() {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LINES_PRE_PALLET + " (" +
                ID_LINE_PP + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_PRE_PALLET_FK + " INTEGER, " +
                UUID_PRE_PALLET + " TEXT, " +
                ID_LINE_FK + " INTEGER, " +
                UUID_LINE_PP + " TEXT, " +
                SYNC_LINE_PP + " INTEGER)");
    }

    public static  String[][] getCasesFromPrepallet(String idPrepallet){
        String columnas[] = {IDCASE, VCODECASE};
        Cursor c = db.query(TBL_CASES, columnas, IDPREPALLET+" = "+idPrepallet, null, null, null, IDCASE);
        String resultado [][] = new String[c.getCount()][columnas.length] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<columnas.length; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;
    }

    public static Cursor getPrePalletsByLine(String idLine){
        Cursor c = db.rawQuery("SELECT pp."+IDPREPALLET+", pp."+IDPREPALLETSERVER+", pp."+
                PROMOTIONS+", nx."+BaseDatos.DESCPROMOTIONS +", pp."+SIZE+", pp."+BaseDatos.VSKU+", pp."+
                BaseDatos.IDFARM+", f."+BaseDatos.NOMBREFARM +", pp."+
                BaseDatos.ID_LINE+", lp."+BaseDatos.VNAMELINE+ ", pp."+
                BaseDatos.FECHA_REGISTRO+", pp."+BaseDatos.SYNC+", "+
                "(SELECT COUNT("+IDCASE+") FROM "+TBL_CASES+" WHERE "+IDPREPALLET+" = pp."+IDPREPALLET +") AS cajasRegistradas "+
                "FROM "+TBL_PRE_PALLET+" pp "+
                "INNER JOIN "+BaseDatos.NOMBRE_TABLA_FARMS+" f on (f."+BaseDatos.IDFARM+" = pp."+BaseDatos.IDFARM+") "+
                "INNER JOIN "+BaseDatos.TBL_CAT_LINESPACKAGE+" lp on (lp."+BaseDatos.ID_LINEPACKAGE+" = pp."+BaseDatos.ID_LINE+") "+
                "INNER JOIN "+BaseDatos.NXIV00102+" nx on (nx."+BaseDatos.IDPROMOTIONS+" = pp."+PROMOTIONS+") "+
                "WHERE pp."+BaseDatos.ID_LINE+" = "+idLine+
                " AND pp."+BaseDatos.SYNC+" = 0"
                ,null);
       /* String resultado [][] = new String[c.getCount()][11] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<11; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;*/

        return c;
    }

    public static Cursor getAllPrePallets(){
        Cursor c = db.rawQuery("SELECT pp." + IDPREPALLET +
                                    ", pp." + IDPREPALLETSERVER +
                                    ", pp." + PROMOTIONS +
                                    ", nx." + BaseDatos.DESCPROMOTIONS +
                                    ", pp." + SIZE +
                                    ", pp." + BaseDatos.VSKU +
                                    ", pp." + BaseDatos.IDFARM +
                                    ", f." + BaseDatos.NOMBREFARM +
                                    ", pp."+ BaseDatos.ID_LINE +
                                    //", lp." + BaseDatos.VNAMELINE +
                                    //", pp." + BaseDatos.FECHA_REGISTRO + " AS " + BaseDatos.VNAMELINE +
                                    ", pp." + BaseDatos.FECHA_REGISTRO +
                                    ", pp." + BaseDatos.SYNC + ", " +
                                    "(SELECT COUNT(" + IDCASE + ") FROM " + TBL_CASES + " WHERE " + IDPREPALLET + " = pp." + IDPREPALLET + ") AS cajasRegistradas, " +
                                    "(SELECT " + BaseDatos.CASESPERPALLET + " FROM " + BaseDatos.TBL_ITEMMASTER + " WHERE RTRIM(" + BaseDatos.ITEMNMBR + ") = pp." + BaseDatos.VSKU + ") AS casesPerPallet " +

                                "FROM " + TBL_PRE_PALLET + " pp "+
                                "INNER JOIN " + BaseDatos.NOMBRE_TABLA_FARMS + " f on (f." + BaseDatos.IDFARM + " = pp." + BaseDatos.IDFARM + ") " +
                                //"INNER JOIN " + BaseDatos.TBL_CAT_LINESPACKAGE + " lp on (lp." + BaseDatos.ID_LINEPACKAGE + " = pp." + BaseDatos.ID_LINE + ") " +
                                "INNER JOIN " + BaseDatos.NXIV00102 + " nx on (nx." + BaseDatos.IDPROMOTIONS + " = pp." + PROMOTIONS + ") " +
                                "ORDER BY pp." + BaseDatos.SYNC + " ASC, pp." + BaseDatos.FECHA_REGISTRO + " DESC"
                                //" WHERE pp." + BaseDatos.SYNC+" = 0"
                ,null);

       /* String resultado [][] = new String[c.getCount()][11] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<11; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;*/

        return c;
    }

    public static String getNameLines(int idPP){
        Cursor c = db.rawQuery("SELECT l." + BaseDatos.VNAMELINE + " FROM " + TBL_LINES_PRE_PALLET + " AS lp " +
                               "INNER JOIN " + BaseDatos.TBL_CAT_LINESPACKAGE + " AS l ON(l." + BaseDatos.ID_LINEPACKAGE + " = lp." + ID_LINE_FK + ") " +
                               "WHERE " + ID_PRE_PALLET_FK + " = " + idPP, null);

        String lineas = "";

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            lineas += c.getString(0) + ", ";
        }

        return lineas;
    }

    public static String getNameLine(String idLine){
        Cursor c = db.rawQuery("SELECT " + BaseDatos.VNAMELINE + " FROM " + BaseDatos.TBL_CAT_LINESPACKAGE + " WHERE " + BaseDatos.ID_LINEPACKAGE + " = " + idLine, null);

        String linea = "";

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            linea = c.getString(0);
        }

        return linea;
    }

    public static ArrayList<Linea> getLinesPP(int idPP){
        //Cursor c = db.rawQuery("SELECT " + ID_LINE_FK + " FROM " + TBL_LINES_PRE_PALLET + " WHERE " + ID_PRE_PALLET_FK + " = " + idPP, null);
        Cursor c = db.rawQuery("SELECT lp." + ID_LINE_FK + ", l." + BaseDatos.ID_GP + " FROM " + TBL_LINES_PRE_PALLET + " AS lp " +
                               "INNER JOIN " + BaseDatos.TBL_CAT_LINESPACKAGE + " AS l ON(l." + BaseDatos.ID_LINEPACKAGE + " = lp." + ID_LINE_FK + ") " +
                               "WHERE " + ID_PRE_PALLET_FK + " = " + idPP, null);

        ArrayList<Linea> lineasPP = new ArrayList<Linea>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Linea l = new Linea();

            l.setIdLinea(c.getInt(0));
            l.setIdGPLinea(c.getInt(1));
            l.setActive(true);

            lineasPP.add(l);
        }

        return lineasPP;
    }

    public static String[][] getPrePallets(){
       String columnas[] = {IDPREPALLET,  BaseDatos.SYNC,  BaseDatos.ACTIVE, BaseDatos.IDFARM, BaseDatos.ID_LINE, BaseDatos.VSKU};
       Cursor c = db.query(TBL_PRE_PALLET, columnas, null, null, null, null, IDPREPALLET);

        String resultado [][] = new String[c.getCount()][columnas.length] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<columnas.length; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;
    }

    public static String[][] getPrePalletsToSync(){
        String columnas[] = {IDPREPALLETSERVER, IDPREPALLET, BaseDatos.IDFARM, BaseDatos.ID_LINE,
                             BaseDatos.VSKU,  BaseDatos.ACTIVE, BaseDatos.FECHA_REGISTRO,
                             BaseDatos.FECHA_ACTUALIZACION, BaseDatos.USERCREATED, BaseDatos.USERUPDATED, PROMOTIONS, SIZE, UNICKEY, RAZON_DESGRANE  };

        Cursor c = db.query(TBL_PRE_PALLET, columnas, BaseDatos.SYNC+" = 0", null, null, null, IDPREPALLET);
        String resultado [][] = new String[c.getCount()][columnas.length+1] ;

        String macAdress = config.obtenerMACAddress(nContext);

        Log.d("macAddress",macAdress);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            for (int i = 0; i < columnas.length+1; i++) {
                if (i == columnas.length)
                    resultado[c.getPosition()][i] = macAdress;
                else
                    resultado[c.getPosition()][i] = c.getString(i);
            }
        }

        c.close();
        return resultado;
    }

    public static String[][] getLinesPrePalletsToSync(){
        String columnas[] = { ID_PRE_PALLET_FK, UUID_PRE_PALLET, ID_LINE_FK, UUID_LINE_PP };

        Cursor c = db.query(TBL_LINES_PRE_PALLET, columnas, SYNC_LINE_PP + " = 0", null, null, null, ID_LINE_PP);
        String resultado [][] = new String[c.getCount()][columnas.length] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            for (int i = 0; i < columnas.length; i++) {
                resultado[c.getPosition()][i] = c.getString(i);
            }
        }

        c.close();
        return resultado;
    }

    public long actualizaPPLAfterSync(String vUUID){
        ContentValues cv = new ContentValues();
        cv.put(SYNC_LINE_PP, 1);

        return db.update(TBL_LINES_PRE_PALLET, cv, UUID_LINE_PP + " = '" + vUUID + "'", null);
    }

    public long actualizaPrePalletIDSync(String idPrePallet, String idPrePalletServer){
        ContentValues cv = new ContentValues();
        cv.put(IDPREPALLETSERVER, idPrePalletServer);
        cv.put(BaseDatos.SYNC, 1);
        if(db.update(TBL_PRE_PALLET, cv, IDPREPALLET + " = "+idPrePallet, null) > 0){
            ContentValues cv2 = new ContentValues();
            cv2.put(IDPREPALLETSERVER, idPrePalletServer);

            ContentValues cv3 = new ContentValues();
            cv3.put(IDPREPALLETSERVER, idPrePalletServer);
            db.update(TBL_CASES,cv3,IDPREPALLET + " = "+idPrePallet, null);

            return db.update(TBL_FOLIOS_PREPALLET,cv2,IDPREPALLET + " = "+idPrePallet, null);
        }else
            return -1;
    }

    public long actualizaPPAfterSync(int idPP, String vUUID){
        ContentValues cv = new ContentValues();

        cv.put(IDPREPALLETSERVER, idPP);
        cv.put(BaseDatos.SYNC, 1);

        return db.update(TBL_PRE_PALLET, cv, UNICKEY + " = '" + vUUID + "'", null);
    }

    public long actualizaPPAfterSendGP(String vUUID, int idGP, String pallet){
        /*ContentValues cv = new ContentValues();

        cv.put(ID_GP, idGP);
        cv.put(CODE_PALLET, pallet);

        return db.update(TBL_PRE_PALLET, cv, UNICKEY + " = '" + vUUID + "'", null);*/
        return 1;
    }

    public long actualizaPrePalletCasesIDSync(String idCase, String idCaseServer){
        ContentValues cv = new ContentValues();
        cv.put(IDCASESERVER, idCaseServer);
        cv.put(BaseDatos.SYNC, 1);
        return db.update(TBL_CASES, cv, IDCASE+" = "+idCase, null);
    }

    public static String[][] getCasesPrePalletsToSync(){
        String columnas[] = {IDCASESERVER, IDCASE, VCODECASE, IDPREPALLET,
                             IDPREPALLETSERVER ,  BaseDatos.ACTIVE, BaseDatos.FECHA_REGISTRO,
                             BaseDatos.FECHA_ACTUALIZACION, BaseDatos.USERCREATED, BaseDatos.USERUPDATED  };

        Cursor c = db.query(TBL_CASES, columnas, BaseDatos.SYNC+" = 0", null, null, null, IDCASE);
        String resultado [][] = new String[c.getCount()][columnas.length+2] ;

        String macAdress = config.obtenerMACAddress(nContext);

        Log.d("macAddress", macAdress);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            for (int i = 0; i < columnas.length+2; i++) {
                if (i == columnas.length)
                    resultado[c.getPosition()][i] = macAdress;
                else if (i == columnas.length + 1)
                    resultado[c.getPosition()][i] = unicID;
                else
                    resultado[c.getPosition()][i] = c.getString(i);
            }
        }

        c.close();
        return resultado;
    }

    public static String[][] getFoliosPrePalletsToSync(){
        String columnas[] = {VCODEFOLIO, ICAJAS, IDPREPALLET,
                             IDPREPALLETSERVER, IDPRODUCTLOG,  BaseDatos.ACTIVE, BaseDatos.FECHA_REGISTRO,
                             BaseDatos.FECHA_ACTUALIZACION, BaseDatos.USERCREATED, BaseDatos.USERUPDATED  };

        Cursor c = db.query(TBL_FOLIOS_PREPALLET, columnas, BaseDatos.SYNC+" = 0", null, null, null, null);
        String resultado [][] = new String[c.getCount()][columnas.length+2] ;

       // sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
      //  unicID = UUID.randomUUID().toString();

        String macAdress = config.obtenerMACAddress(nContext);

        Log.d("macAddress", macAdress);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            for (int i = 0; i < columnas.length+2; i++) {
                if (i == columnas.length)
                    resultado[c.getPosition()][i] = macAdress;
                else if (i == columnas.length + 1)
                    resultado[c.getPosition()][i] = unicID;
                else
                    resultado[c.getPosition()][i] = c.getString(i);
            }
        }

        c.close();
        return resultado;
    }

    public static String[][] getPrePallet(String idPrepallet){
        String columnas[] = {IDPREPALLET,  BaseDatos.NOMBREFARM, BaseDatos.VSKU,
                             BaseDatos.VNAMELINE, BaseDatos.IDFARM,
                             BaseDatos.ID_LINE, PROMOTIONS, BaseDatos.DESCPROMOTIONS, SIZE};

        Cursor c = db.rawQuery("SELECT pp." + columnas[0] + ", f." + columnas[1] + ", pp." + columnas[2] + ", l." + columnas[3] + ", pp." + columnas[4] +
                ", pp." + columnas[5] + ", pp." + columnas[6] + ", prm." + columnas[7] + ", pp." + columnas[8] + " FROM " + TBL_PRE_PALLET +
                " pp INNER JOIN " + BaseDatos.NOMBRE_TABLA_FARMS +
                " f ON f." + BaseDatos.IDFARM + " = pp." + BaseDatos.IDFARM +
                " INNER JOIN " + BaseDatos.TBL_CAT_LINESPACKAGE + " l ON" +
                " l." + BaseDatos.ID_LINEPACKAGE + " = pp." + BaseDatos.ID_LINE +
                " INNER JOIN " + BaseDatos.NXIV00102 + " prm ON " +
                "prm." + BaseDatos.IDPROMOTIONS + " = pp." + PROMOTIONS
                + " WHERE pp." + IDPREPALLET + " = " + idPrepallet, null);
        String resultado [][] = new String[c.getCount()][columnas.length] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<columnas.length; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;
    }

    public static long insertPrePallet(String idFarm, ArrayList<Linea> lines, String vSKU, String active, String size, String idPromo, int desgrane){
        ContentValues cv = new ContentValues();
        String unicKey = UUID.randomUUID().toString();
        String lastID;

        cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.FECHA_ACTUALIZACION, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERCREATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.IDFARM, idFarm);
        cv.put(BaseDatos.ID_LINE, 0);
        cv.put(BaseDatos.VSKU, vSKU);
        cv.put(BaseDatos.ACTIVE, active);
        cv.put(UNICKEY, unicKey);
        cv.put(SIZE, size);
        cv.put(PROMOTIONS, idPromo);
        cv.put(RAZON_DESGRANE, desgrane);
        cv.put(BaseDatos.SYNC, 0);

        if (db.insert(TBL_PRE_PALLET, null, cv) > 0){
            Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
            c.moveToFirst();
            lastID = c.getString(0);
            c.close();

            for (int i = 0; i < lines.size(); i++){
                ContentValues cv2 = new ContentValues();

                cv2.put(ID_PRE_PALLET_FK, lastID);
                cv2.put(UUID_PRE_PALLET, unicKey);
                cv2.put(ID_LINE_FK, lines.get(i).getIdLinea());
                cv2.put(UUID_LINE_PP, UUID.randomUUID().toString());
                cv2.put(SYNC_LINE_PP, 0);

                db.insert(TBL_LINES_PRE_PALLET, null, cv2);
            }

            return 1;
        } else {
            return 0;
        }
    }

    /*public static long insertPrePallet(ArrayList<HashMap<String, String>> cajas, ArrayList<HashMap<String, String>> folios, String idFarm, String idLine, String vSKU, String active, String size, String idPromo){
        ContentValues cv = new ContentValues();
        String unicKey = UUID.randomUUID().toString();

        cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERCREATED, "jcalderon");
        cv.put(BaseDatos.IDFARM, idFarm);
        cv.put(BaseDatos.ID_LINE, idLine);
        cv.put(BaseDatos.VSKU, vSKU);
        cv.put(BaseDatos.ACTIVE, active);
        cv.put(UNICKEY, unicKey);
        cv.put(SIZE, size);
        cv.put(PROMOTIONS, idPromo);
        cv.put(BaseDatos.SYNC, 0);

        if (db.insert(TBL_PRE_PALLET, null, cv) > 0){
            Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
            c.moveToFirst();
            String idPrePallet = c.getString(0);
            c.close();

           /* for(int i=0; i<cajas.size(); i++){
                HashMap<String, String> item = cajas.get(i);
                insertCases(item.get("case").toString(), idPrePallet);
            }
            */
            /*for(int i=0; i<folios.size(); i++){ eSTO SE HACE AL MOMENTO DE MANDARLO A GP
                HashMap<String, String> item = folios.get(i);
                insertFolio(item.get("idProductLog"), item.get("folio"), item.get("cajas"), idPrePallet);

                Log.d("folio | boxes", item.get("folio") + " | " + item.get("cajas"));
            }*/

            /*return 1;
        } else {
            return 0;
        }
    }*/

    public static long updatePrePalletr(ArrayList<HashMap<String, String>> cajas,  ArrayList<HashMap<String, String>> folios, String idFarm, ArrayList<Linea> lines, String vSKU, String active, String idPrePallet, String size, String idPromo, int desgrane){
        ContentValues cv = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv.put(BaseDatos.FECHA_ACTUALIZACION, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.IDFARM, idFarm);
        cv.put(BaseDatos.VSKU, vSKU);
        cv.put(BaseDatos.ACTIVE, active);
        cv.put(SIZE, size);
        cv.put(PROMOTIONS, idPromo);
        cv.put(RAZON_DESGRANE, desgrane);
        cv.put(BaseDatos.SYNC, 0);

        if(db.update(TBL_PRE_PALLET, cv, IDPREPALLET + " = " + idPrePallet, null) > 0){
            cv2.put(BaseDatos.SYNC, 0);
            db.update(TBL_CASES, cv2, IDPREPALLET + " = " + idPrePallet, null);

            db.delete(TBL_LINES_PRE_PALLET, ID_PRE_PALLET_FK + " = " + idPrePallet, null);

            for (int i = 0; i < lines.size(); i++){
                ContentValues cv3 = new ContentValues();

                cv3.put(ID_PRE_PALLET_FK, idPrePallet);
                cv3.put(ID_LINE_FK, lines.get(i).getIdLinea());
                cv3.put(UUID_LINE_PP, UUID.randomUUID().toString());
                cv3.put(SYNC_LINE_PP, 0);

                db.insert(TBL_LINES_PRE_PALLET, null, cv3);
            }

            return 1;
        }else
            return 0;
    }

    /*public static long updatePrePalletr(ArrayList<HashMap<String, String>> cajas,  ArrayList<HashMap<String, String>> folios,
                                        String idFarm, String idLine, String vSKU, String active, String idPrePallet, String size, String idPromo){
        ContentValues cv = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv.put(BaseDatos.FECHA_ACTUALIZACION, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERUPDATED, "jcalderon");
        cv.put(BaseDatos.IDFARM, idFarm);
        cv.put(BaseDatos.ID_LINE, idLine);
        cv.put(BaseDatos.VSKU, vSKU);
        cv.put(BaseDatos.ACTIVE, active);
        cv.put(SIZE, size);
        cv.put(PROMOTIONS, idPromo);
        cv.put(BaseDatos.SYNC, 0);

        if(db.update(TBL_PRE_PALLET, cv, IDPREPALLET + " = " + idPrePallet, null) > 0){
            cv2.put(BaseDatos.SYNC, 0);
            db.update(TBL_CASES, cv2, IDPREPALLET + " = " + idPrePallet, null);
            //db.update(TBL_FOLIOS_PREPALLET, cv2, IDPREPALLET + " = " + idPrePallet, null);
           // Toast.makeText(BaseDatos.nContext,"se actualizo",Toast.LENGTH_LONG).show();

          /*  for(int i=0; i<cajas.size(); i++){
                HashMap<String, String> item = cajas.get(i);
                insertCases(item.get("case").toString(), idPrePallet);
            }*/

           /* if(folios.size() > 0) {
                for (int i = 0; i < folios.size(); i++) {
                    HashMap<String, String> item = folios.get(i);
                    insertFolio(item.get("idProductLog"), item.get("folio"), item.get("cajas"), idPrePallet);

                    Log.d("folio | boxes", item.get("folio") + " | " + item.get("cajas"));
                }
            }*/

            /*return 1;
        }else
            return 0;
    }*/

    public static String[][] buscaCase(String cases){
        String columnas[] = {IDCASE, VCODECASE};
        Cursor c = db.query(TBL_CASES, columnas, VCODECASE + " = '"+cases+"'", null, null, null, IDCASE);
        String resultado [][] = new String[c.getCount()][columnas.length] ;

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<columnas.length; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;
    }

    public static long eraseCase(String vCodeCase){
        return db.delete(TBL_CASES, VCODECASE + " = '"+vCodeCase+"'", null);
    }

    private static long insertFolio(String idProductLog, String vCode, String cajas, String idPrePallet){

        ContentValues cv = new ContentValues();

        cv.put(VCODEFOLIO, vCode);
        cv.put(ICAJAS, cajas);
        cv.put(IDPREPALLET, idPrePallet);
        cv.put(IDPRODUCTLOG, idProductLog);
        cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
        cv.put(BaseDatos.USERCREATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
        cv.put(BaseDatos.ACTIVE, 1);
        cv.put(BaseDatos.SYNC, 0);

        return db.insert(TBL_FOLIOS_PREPALLET, null, cv);

    }

    private static long insertCases(String vcodecase, String idPrepallet){

        String datos[][] = buscaCase(vcodecase);

        if(datos.length == 0){

            ContentValues cv = new ContentValues();

            cv.put(VCODECASE, vcodecase);
            cv.put(IDPREPALLET, idPrepallet);
            cv.put(BaseDatos.FECHA_REGISTRO, dateFormat.format(cal.getTime()));
            cv.put(BaseDatos.USERCREATED, sharedpreferences.getString("username","jcalderon"));
            cv.put(BaseDatos.USERUPDATED, sharedpreferences.getString("username","jcalderon"));
            cv.put(BaseDatos.ACTIVE, 1);
            cv.put(BaseDatos.SYNC, 0);

            return db.insert(TBL_CASES, null, cv);

        }else
            return 0;
    }

    public long actualizaFoliosIDSync(String vFolio, String idPrePalletTablet) {
        ContentValues cv = new ContentValues();
        cv.put(BaseDatos.SYNC, 1);
        return db.update(TBL_FOLIOS_PREPALLET, cv, IDPREPALLET+" = "+ idPrePalletTablet, null);
    }

}

