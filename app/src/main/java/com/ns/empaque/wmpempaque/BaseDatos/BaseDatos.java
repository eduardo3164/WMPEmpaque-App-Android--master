
package com.ns.empaque.wmpempaque.BaseDatos;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.MAP.locacion;
import com.ns.empaque.wmpempaque.Modelo.config;
import com.ns.empaque.wmpempaque.OnHold.Calidad;
import com.ns.empaque.wmpempaque.OnHold.Departamento;
import com.ns.empaque.wmpempaque.OnHold.Disposicion;
import com.ns.empaque.wmpempaque.OnHold.Razon;
import com.ns.empaque.wmpempaque.OnHold.Regex;
import com.ns.empaque.wmpempaque.insertLine.lineTripulaciones;
import com.ns.empaque.wmpempaque.insertLine.linesPackage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class BaseDatos
{
	/******NOMBRE DE LA BASE DE DATOS*****/
	public static String NOMBRE_BD = "WMPEmpaque";

	/*********NOMBRE DE LAS TABLAS********/
	public static String NOMBRE_TABLA_FARMS = "farms";
	public static String TBL_CAT_REASONS = "tbl_Cat_Reasons";
	public static String TBL_CAT_EMBALAJES = "ev2_tbl_Cat_Embalaje";
	public static String TBL_sku_quality = "tbl_SkuQuality";
	public static String TBL_CAT_USEREXTRAROLE = "tbl_Cat_UserExtraRole";
	public static String TBL_CAT_THREASHREASON = "tbl_Cat_ThreshReason";
	public static String TBL_CAT_LINESPACKAGE = "tbl_Cat_LinesPackage";
	public static String TBL_LINESKU = "tbl_LineSKU";
	public static String TBL_ITEMMASTER = "tbl_itemMaster";
	public static String TBL_SIZECODE = "tbl_sizeCode";
	public static String NXIV00102 = "NXIV00102";
	public static String TBL_LOCATIONS = "ev2_tbl_Cat_Location";
	public static String TBL_CAT_CONTAINEREMBALAJE = "ev2_tbl_Cat_ContainerEmbalaje";
	public static String TBL_CAT_NSCALENDAR = "tblDateCode";
	public static String TBL_CASES_PREPALLET = "tblCasesPrePallet";
	public static String TBL_ASIGN_PALLET = "tblAsigPalletFormsA1";
	public static String TBL_PRE_PALLET_IN_GP = "tblPrePalletInGP";
	public static String NOMBRE_TABLA_IMPRESORAS = "tblImpresoras";
	public static String NOMBRE_TABLA_PESO_EMBARQUE = "ev2_tbl_PesoFolioEmbarque";

	public static String NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS = "ev2_tbl_cat_PlantsDepartments";
	public static String NOMBRE_TABLA_CAT_SITES_DEPARTMENTS = "ev2_tbl_cat_siteDepartment";
	public static String NOMBRE_TABLA_CAT_PLANTS_SITES = "ev2_tbl_Cat_PlantsSites";
	public static String NOMBRE_TABLA_CAT_REASON_WASTE = "ev2_tbl_Cat_ReasonWaste";
	public static String NOMBRE_TABLA_CAT_REASON_DEPARTMENTS = "ev2_tbl_ReasonDepartment";
	public static String NOMBRE_TABLA_CAT_QUALITY_TYPE = "tbl_QualityType";
	public static String NOMBRE_TABLA_CAT_DISPOSITION_WASTE = "tbl_DispositionWaste";
	//sync make a new column (iRev) wich one has to be unico java.util.UUID.

	//CAMPOS PARA TBL_CAT_CONTAINEREMBALAJE
	public static String DGDATE = "DGDate";
	public static String DY = "DY";
	public static String WK = "WK";
	public static String YR = "YR";
	public static String DATECODE = "DateCode";
	public static String WEEK = "Week";
	public static String DAYOFTHEWEEK = "DayOfWeek";

	//CAMPOS PARA CONTAINEREMBALAJE
	public static String IDCONTAINEREMBALAJE = "idContainerEmbalaje";
	public static String IDCONTAINERs = "idContainer";
	//public static String IDEMBALAJE = "idContainer";

	//CAMPOS PARA LOCATIONS
	public static String IDLOCATION = "id_Location";
	public static String VNAMELOCATION = "vNameLocation";
	public static String WIDTH = "iWidth";
	public static String HEIGHT = "iHeight";
	public static String LENGHT = "iLenght";
	public static String VDESCRIPTION = "vDescriptionLocation";
	public static String ID_FARM = "id_Farm";
	public static String IDTYPELOCATION = "id_TypeLocation";
	public static String IDCONTAINER = "id_Container";
	public static String IDFRAMEWORK = "id_Framework";
	public static String DDATECREATED = "dDateCreated";
	public static String VUSERCREATED = "vUserCreated";
	public static String DDATEUPDATED = "dDateUpdated";
	public static String VUSERUPDATED = "vUserUpdated";
	public static String VCODELOCATION = "vCodeLocation";

	//CAMPOS PARA PROMOTIONS
	public static String IDPROMOTIONS = "ID";
	public static String DESCPROMOTIONS = "DESCRIPTION";

	//CAMPOS PARA SIZECODE DEL PREPALLET
	public static String IDSIZECODE = "idSize";
	public static String CODESIZE = "sizeCode";

	//CAMPOS PARA ITEMMASTER
	public static final String IDITEMMASTER = "id";
	public static final String ITEMNMBR = "ITEMNMBR";
	public static final String ITEMDESC = "ITEMDESC";
	public static final String SUPERCATEGORY = "SUPERCATEGORY";
	public static final String CASEWEIGHT = "CASEWEIGHT";
	public static final String PALLETWEIGHT = "PALLETWEIGHT";
	public static final String CASESPERPALLET = "CASESPERPALLET";


	//CAMPOS PARA LINESLU
	public static final String ID_LINE = "id_Line";
	public static final String VSKU = "vSKU";
	public static final String LASTUPDATEBY = "lastUpdateBy";
	public static final String LASTUPDATETIME = "lastUpdateTime";

	//CAMPOS PARA LINEPACKAGE
	public static final String ID_LINEPACKAGE = "id_LinePackage";
	public static final String VNAMELINE = "vNameLine";
	public static final String VDESCRIPTIONLINE = "vDescriptionLine";
	public static final String VPATH_IMAGELINE = "vPath_ImageLine";
	public static final String VTYPELINE = "vTypeLine";
	public static final String ID_GP = "id_GP";
	public static final String ID_SITE = "idSite";

	//CAMPOS THRESHREASON
	public static final String ID_THRESHREASON = "idThreshReason";
	public static final String VREASON = "vReason";
	public static final String IDPLANT = "idPlant";

	//CAMPOS USEREXTRAROLE
	public static final String ID_EXTRAROLE = "id_UserExtraRole";
	public static final String VSHORTNAME = "vShortName";
	public static final String VFULLNAME = "vFullName";
	public static final String ID_PLANT = "id_Plant";

	/**********CAMPOS COMUNES*********/
	public static final String FECHA_REGISTRO = "DateAdd";
	public static final String FECHA_ACTUALIZACION = "DateEdit";
	public static final String USERCREATED = "userAdd";
	public static final String USERUPDATED = "userEdit";
	public static final String ACTIVE = "bActive";
	public static final String SYNC = "sync";

	/******CAMPOS PARA TABLAS FARM*****/
	public static final String IDFARM = "idFarm";
	public static final String NOMBREFARM = "nombreFarm";

	//CATALOGOS DE MERMAONHOLD
	public static final String ID_CAT_REASON = "idCatReason";
	public static final String VNAMEREASON = "vNameReason";
	public static final String VDESCRIPREASON = "vDescripReason";
	public static final String BRELEASE = "bRelease";

	//catalogo de embalajes
	public static final String IDEMBALAJE = "idEmbalaje";
	public static final String VNAMEEMBALAJE = "vNameEmbalaje";
	public static final String VDESCRIPEMBALAJE = "vDescrpEmbalaje";
	public static final String VREGEX = "vRegex";

	//catalogo sku Quality
	public static final String IDSKU = "id_Sku";
	public static final String VNAMESKU = "vNameSku";
	public static final String vQuality = "vQuality";
	public static final String idProduct = "id_Product";
	public static final String iQuality = "iQuality";


	//CAMPOS DE LA TABLA TBL_CASES_PREPALLET
	public static final String ID_CASES_PREPALLET = "idCasesPrePallet";
	public static final String ID_PREPALLET = "idPrePallet";
	public static final String ID_CASES_DETAILS = "idCaseDetails";
	public static final String CODIGO_CASE = "vCodeCase";
	public static final String ESTADO_CP = "bActive";
	public static final String FECHA_CREACION_CP = "dDateCreate";
	public static final String USUARIO_CREACION_CP = "vUserCreate";
	public static final String FECHA_MODIFICAICON_CP = "dDateUpdate";
	public static final String USUARIO_MODIFICACION_CP = "vUserUpdate";
	public static final String MAC_TABLET_CP = "vIdTabletMac";
	public static final String UUID_PP = "vUUIDPP";
	public static final String UUID_CD = "vUUIDCD";
	public static final String UUID_CP = "vUUID";
	public static final String SYNC_CP = "sync";

	//CAMPOS DE LA TABLA TBL_ASIGN_PALLET
	public static final String ID_ASIGN_PALLET = "idAsigPallet";
	public static final String ID_PREPALLET_AP = "idPrePallet";
	//public static final String ID_PREPALLET_TABLET_AP = "idPrePalletTablet";
	//public static final String PALLET_AP = "Pallet";
	public static final String FOLIO_AP = "vFolio";
	public static final String CAJAS_AP = "iCases";
	//public static final String ID_PRODUCT_PALLET_AP = "id_ProductPallet";
	//public static final String ID_PRODUCT_AP = "id_Product";
	public static final String ID_LINEA = "idLine";
	public static final String MAC_TABLET_AP = "vIdTabletMac";
	public static final String UUID_AP = "vUnicSesionKey";
	//public static final String ID_PRODUCT_LOG = "id_Product_Log";
	public static final String SYNC_AP = "sync";

	//CAMPOS DE LA TABLA TBL_PRE_PALLET_IN_GP
	public static final String ID_PRE_PALLET_IN_GP = "idPrePalletInGP";
	public static final String ID_PRE_PALLET_FK = "idPrePalletFK";
	public static final String PALLET_ID = "palletID";
	public static final String ID_PALLET_GP = "idPalletGP";

	//CAMPOS DE LA TABLA TBL_IMPRESRAS
	public static final String ID_IMPRESORA = "idImpresora";
	public static final String ID_PLATA_FK_IMP = "idPlanta";
	public static final String NOMBRE_PLATA_FK_IMP = "nombrePlanta";
	public static final String NOMBRE_IMPRESORA = "vNombre";
	public static final String IP_IMPRESORA = "vIP";
	public static final String PUERTO_IMPRESORA = "vPuerto";
	public static final String ESTADO_IMPRESORA = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS
	public static final String ID_DEPARTMENT_PD = "idDepartment";
	public static final String NOMBRE_DEPARTMENT_PD = "vNameDepartment";
	public static final String DESCRIPCION_DEPARTMENT_PD = "vDescriptionDepartment";
	public static final String ESTADO_DEPARTMENT_PD = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_SITES_DEPARTMENTS
	public static final String ID_SITE_DEPARTMENT = "idSiteDepartment";
	public static final String ID_SITE_SD = "idSite";
	public static final String ID_DEPARTMENT_SD = "idDepartment";
	public static final String ID_PLANTA_SD = "idFarm";
	public static final String ESTADO_SD = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_PLANTS_SITES
	public static final String ID_SITE_PS = "idSite";
	public static final String ID_PLANTA_PS = "idFarm";
	public static final String NOMBRE_SITE_PS = "vSiteName";
	public static final String DESCRIPCION_SITE_PS = "vSiteDescription";
	public static final String ESTADO_PS = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_REASON_WASTE
	public static final String ID_REASON_RW = "idReason";
	public static final String NOMBRE_REASON_RW = "vNameReason";
	public static final String DESCRIPTION_REASON_RW = "vDescriptionReason";
	public static final String LIBERACION_REASON_RW = "bLiberation";
	public static final String ESTADO_REASON_RW = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_REASON_DEPARTMENTS
	public static final String ID_REASON_RD = "idReason";
	public static final String ID_DEPARTMENT_RD = "idDepartment";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_QUALITY_TYPE
	public static final String ID_QUALITY_TYPE = "id_QualityType";
	public static final String NOMBRE_QUALITY_TYPE = "vQualityType";
	public static final String NOMBRE_CORTO_QT = "vSortName";
	public static final String DESCRIPCION_QUALITY_TYPE = "vDescriptionQuality";
	public static final String FACTOR_QUALITY_TYPE = "bCalidadFactor";
	public static final String ESTADO_QUALITY_TYPE = "bActive";

	//CAMPOS DE LA TABLA NOMBRE_TABLA_CAT_DISPOSITION_WASTE
	public static final String ID_DISPOSITION_WASTE = "idDisposition";
	public static final String NOMBRE_DISPOSITION_WASTE = "vNameDisposition";
	public static final String ESTADO_DISPOSITION_WASTE = "bActive";

	//CAMPOS DE LA TABLA EV2_TBL_PESO_FOLIO_EMBARQUE
	public static final String ID_PESO_FOLIO_EMBARQUE = "idPesoFolioEm";
	public static final String FK_PRODUCTO_MERMADO = "idProducto";
	public static final String VFOLIO = "vFolio";
	public static final String FPESO = "fPeso";
	public static final String ESTADO_PESO_FOLIO = "bActive";

	private static final int VERSION_BD = config.versionDB;

	private BDHelper nHelper;
	public static Activity nContext;
	private SQLiteDatabase nBD;

	public static CasesPrePalletDB cpdb;
	public static casesIncrementHeaderDB cidb;
	public static casesIncrementDetailsDB ciddb;
	public static ev2_tbl_cat_packTurnos packTurnosDB;
	private static SharedPreferences sharedpreferences;

	public BaseDatos(Activity c) {
		nContext = c;

		sharedpreferences = nContext.getSharedPreferences("WMPEmpaqueApp", Context.MODE_PRIVATE);
	}

	public class BDHelper extends SQLiteOpenHelper {

		public BDHelper(Context context)
		{
			super(context, NOMBRE_BD, null, VERSION_BD);
		}

		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + NOMBRE_TABLA_FARMS + " (" +
					IDFARM + " INTEGER PRIMARY KEY, " +
					NOMBREFARM + " TEXT, " +
					ACTIVE + " INTEGER," +
					FECHA_REGISTRO + " DATETIME)");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}

	public BaseDatos abrir() {
		nHelper = new BDHelper(nContext);
		nBD = nHelper.getWritableDatabase();

		cpdb = new CasesPrePalletDB(nBD, nContext);
		cpdb.createTableCases();
		cpdb.createTablePrePallet();
        cpdb.createTableLinesPrePallet();
		cpdb.createTableFolios();

		cidb = new casesIncrementHeaderDB(nBD, nContext);
		cidb.crearTablaCasesIncrement();

		ciddb = new casesIncrementDetailsDB(nBD, nContext);
		ciddb.createTable();

		packTurnosDB  = new ev2_tbl_cat_packTurnos(nBD, nContext);
		packTurnosDB.createTable();

		return this;
	}

	public void consulta(String query){
		nBD.execSQL(query);
	}

	public void cerrar()
	{
		nHelper.close();
	}

	public String[][] consultaTabla( String nameTabla, String campo1, String campo2){
		try {

			String columnas[] = {campo1, campo2};
			Cursor c = nBD.query(nameTabla, columnas, null, null, null, null, campo1);
			String resultado[][] = new String[c.getCount()][columnas.length];

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
				for (int i = 0; i < columnas.length; i++)
					resultado[c.getPosition()][i] = c.getString(i);

			c.close();
			return resultado;

		}catch(Exception e){
			return new String[1][1];
		}
	}

	public boolean solicitarContrasena(String fechaActual, String ultimaFecha, int minutos){
		Cursor c = nBD.rawQuery("SELECT CAST((((strftime('%s', '" + fechaActual + "') - strftime('%s', '" + ultimaFecha + "')) % (60 * 60 * 24)) % (60 * 60)) / 60 AS TEXT) AS minutos", null);

		if(c.getCount() != 0){
			c.moveToFirst();
			if(c.getInt(0) > minutos){
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/********************************************************/
	/*************CATALOGO DE CALENDARIO NS*********************/
	/********************************************************/
	public void createTableNSCalendar(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_NSCALENDAR);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_NSCALENDAR + " (" +
				DGDATE + " DATE, " +
				DY + " TEXT, " +
				WK + " TEXT, " +
				YR + " TEXT,"+
				DATECODE + " TEXT, " +
				WEEK + " TEXT, " +
				DAYOFTHEWEEK + " TEXT"
				+ ")");
	}

	public void insertnsCalendar(String dgDate, String dy, String wk,
										String yr, String datecode, String week, String dayoftheweek){

		ContentValues cv = new ContentValues();

		cv.put(DGDATE, dgDate);
		cv.put(DY, dy);
		cv.put(WK, wk);
		cv.put(YR, yr);
		cv.put(DATECODE, datecode);
		cv.put(WEEK, week);
		cv.put(DAYOFTHEWEEK, dayoftheweek);
		nBD.insert(TBL_CAT_NSCALENDAR, null, cv);
	}

	public void createTableImpresoras(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_IMPRESORAS);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_IMPRESORAS + " (" +
					ID_IMPRESORA + " INTEGER, " +
					ID_PLATA_FK_IMP + " INTEGER, " +
					NOMBRE_PLATA_FK_IMP + " TEXT, " +
					NOMBRE_IMPRESORA + " TEXT, " +
					IP_IMPRESORA + " TEXT,"+
					PUERTO_IMPRESORA + " TEXT,"+
					ESTADO_IMPRESORA + " INTEGER"
					+ ")");
	}

	public void insertImpresora(int idImp, int idPta, String nomPta, String nomImp, String ipImp, String ptoImp, int edoImp){
		ContentValues cv = new ContentValues();

		cv.put(ID_IMPRESORA, idImp);
		cv.put(ID_PLATA_FK_IMP, idPta);
		cv.put(NOMBRE_PLATA_FK_IMP, nomPta);
		cv.put(NOMBRE_IMPRESORA, nomImp);
		cv.put(IP_IMPRESORA, ipImp);
		cv.put(PUERTO_IMPRESORA, ptoImp);
		cv.put(ESTADO_IMPRESORA, edoImp);

		nBD.insert(NOMBRE_TABLA_IMPRESORAS, null, cv);
	}

	public void crearTablaPlantsDepartments(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS + " (" +
				ID_DEPARTMENT_PD + " INTEGER, " +
				NOMBRE_DEPARTMENT_PD + " TEXT, " +
				DESCRIPCION_DEPARTMENT_PD + " TEXT, " +
				ESTADO_DEPARTMENT_PD + " INTEGER"
				+ ")");
	}

	public void insertPlantsDepartments(int idDep, String nomDep, String descDep, int edoDep){
		ContentValues cv = new ContentValues();

		cv.put(ID_DEPARTMENT_PD, idDep);
		cv.put(NOMBRE_DEPARTMENT_PD, nomDep);
		cv.put(DESCRIPCION_DEPARTMENT_PD, descDep);
		cv.put(ESTADO_DEPARTMENT_PD, edoDep);

		nBD.insert(NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS, null, cv);
	}

	public void crearTablaSitesDepartments(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_SITES_DEPARTMENTS);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_SITES_DEPARTMENTS + " (" +
				ID_SITE_DEPARTMENT + " INTEGER, " +
				ID_SITE_SD + " INTEGER, " +
				ID_DEPARTMENT_SD + " INTEGER, " +
				ID_PLANTA_SD + " INTEGER, " +
				ESTADO_SD + " INTEGER"
				+ ")");
	}

	public void insertSitesDepartments(int idSiteDep, int idSite, int idDep, int idPlan, int edo){
		ContentValues cv = new ContentValues();

		cv.put(ID_SITE_DEPARTMENT, idSiteDep);
		cv.put(ID_SITE_SD, idSite);
		cv.put(ID_DEPARTMENT_SD, idDep);
		cv.put(ID_PLANTA_SD, idPlan);
		cv.put(ESTADO_SD, edo);

		nBD.insert(NOMBRE_TABLA_CAT_SITES_DEPARTMENTS, null, cv);
	}

	public void crearTablaPlantsSites(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_PLANTS_SITES);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_PLANTS_SITES + " (" +
				ID_SITE_PS + " INTEGER, " +
				ID_PLANTA_PS + " INTEGER, " +
				NOMBRE_SITE_PS + " TEXT, " +
				DESCRIPCION_SITE_PS + " TEXT, " +
				ESTADO_PS + " INTEGER"
				+ ")");
	}

	public void insertPlantsSites(int idSite, int idPlan, String nomSite, String descSite, int edo){
		ContentValues cv = new ContentValues();

		cv.put(ID_SITE_PS, idSite);
		cv.put(ID_PLANTA_PS, idPlan);
		cv.put(NOMBRE_SITE_PS, nomSite);
		cv.put(DESCRIPCION_SITE_PS, descSite);
		cv.put(ESTADO_PS, edo);

		nBD.insert(NOMBRE_TABLA_CAT_PLANTS_SITES, null, cv);
	}

	public void crearTablaReasonWaste(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_REASON_WASTE);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_REASON_WASTE + " (" +
				ID_REASON_RW + " INTEGER, " +
				NOMBRE_REASON_RW + " TEXT, " +
				DESCRIPTION_REASON_RW + " TEXT, " +
				LIBERACION_REASON_RW + " INTEGER, " +
				ESTADO_REASON_RW + " INTEGER"
				+ ")");
	}

	public void insertReasonWaste(int idReas, String nomReas, String descReas, int lib, int edo){
		ContentValues cv = new ContentValues();

		cv.put(ID_REASON_RW, idReas);
		cv.put(NOMBRE_REASON_RW, nomReas);
		cv.put(DESCRIPTION_REASON_RW, descReas);
		cv.put(LIBERACION_REASON_RW, lib);
		cv.put(ESTADO_REASON_RW, edo);

		nBD.insert(NOMBRE_TABLA_CAT_REASON_WASTE, null, cv);
	}

	public void crearTablaReasonDepartments(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_REASON_DEPARTMENTS);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_REASON_DEPARTMENTS + " (" +
				ID_REASON_RD + " INTEGER, " +
				ID_DEPARTMENT_RD + " INTEGER"
				+ ")");
	}

	public void insertReasonDepartments(int idReas, int idDep){
		ContentValues cv = new ContentValues();

		cv.put(ID_REASON_RD, idReas);
		cv.put(ID_DEPARTMENT_RD, idDep);

		nBD.insert(NOMBRE_TABLA_CAT_REASON_DEPARTMENTS, null, cv);
	}

	public void crearTablaQualityType(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_QUALITY_TYPE);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_QUALITY_TYPE + " (" +
				ID_QUALITY_TYPE + " INTEGER, " +
				NOMBRE_QUALITY_TYPE + " TEXT, " +
				NOMBRE_CORTO_QT + " TEXT, " +
				DESCRIPCION_QUALITY_TYPE + " TEXT, " +
				FACTOR_QUALITY_TYPE + " INT, " +
				ESTADO_QUALITY_TYPE + " INTEGER"
				+ ")");
	}

	public void insertQualityType(int idQua, String nomQua, String cortoQua, String descQua, int facQua, int edoQua){
		ContentValues cv = new ContentValues();

		cv.put(ID_QUALITY_TYPE, idQua);
		cv.put(NOMBRE_QUALITY_TYPE, nomQua);
		cv.put(NOMBRE_CORTO_QT, cortoQua);
		cv.put(DESCRIPCION_QUALITY_TYPE, descQua);
		cv.put(FACTOR_QUALITY_TYPE, facQua);
		cv.put(ESTADO_QUALITY_TYPE, edoQua);

		nBD.insert(NOMBRE_TABLA_CAT_QUALITY_TYPE, null, cv);
	}

	public void crearTablaDispositionWaste(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CAT_DISPOSITION_WASTE);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_CAT_DISPOSITION_WASTE + " (" +
				ID_DISPOSITION_WASTE + " TEXT, " +
				NOMBRE_DISPOSITION_WASTE + " TEXT, " +
				ESTADO_DISPOSITION_WASTE + " INTEGER"
				+ ")");
	}

	public void insertDispositionWaste(String idDisp, String nomDisp, int edoDisp){
		ContentValues cv = new ContentValues();

		cv.put(ID_DISPOSITION_WASTE, idDisp);
		cv.put(NOMBRE_DISPOSITION_WASTE, nomDisp);
		cv.put(ESTADO_DISPOSITION_WASTE, edoDisp);

		nBD.insert(NOMBRE_TABLA_CAT_DISPOSITION_WASTE, null, cv);
	}

	public String[][] obtenerImpresoras(){
		String[][] datosImpresoras;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT * FROM " + NOMBRE_TABLA_IMPRESORAS + " WHERE " + ESTADO_IMPRESORA + " = 1", null);

		datosImpresoras = new String[c.getCount()][7];


		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			datosImpresoras[cont][0] = c.getString(c.getColumnIndex(ID_IMPRESORA));
			datosImpresoras[cont][1] = c.getString(c.getColumnIndex(ID_PLATA_FK_IMP));
			datosImpresoras[cont][2] = c.getString(c.getColumnIndex(NOMBRE_PLATA_FK_IMP));
			datosImpresoras[cont][3] = c.getString(c.getColumnIndex(NOMBRE_IMPRESORA));
			datosImpresoras[cont][4] = c.getString(c.getColumnIndex(IP_IMPRESORA));
			datosImpresoras[cont][5] = c.getString(c.getColumnIndex(PUERTO_IMPRESORA));
			datosImpresoras[cont][6] = c.getString(c.getColumnIndex(ESTADO_IMPRESORA));

			cont++;
		}

		c.close();

		return datosImpresoras;
	}


	public ArrayList<HashMap<String,String>> getWeek(String actualDate){

		ArrayList<HashMap<String,String>> date = new ArrayList<>();


		String columnas[] = {DY, WK};
		Cursor c = nBD.query(TBL_CAT_NSCALENDAR, columnas, DGDATE+" = '"+actualDate+"'", null, null, null, null);
		//String resultado [] = new String[c.getCount()];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			//resultado[c.getPosition()] = c.getString(0);
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("DY",c.getString(0));
			map.put("WK",c.getString(1));
		//	Toast.makeText(nContext, c.getString(0), Toast.LENGTH_LONG).show();

			date.add(map);
		}

		c.close();

		return date;
	}


	/********************************************************/
	/*************CATALOGO DE CATCONTAINER*********************/
	/********************************************************/
	public void createTableContainerEmbalaje(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_CONTAINEREMBALAJE);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_CONTAINEREMBALAJE + " (" +
				IDCONTAINEREMBALAJE + " INTEGER, " +
				IDCONTAINERs + " INTEGER, " +
				IDEMBALAJE + " INTEGER, " +
				ACTIVE+" INTEGER"
				+ ")");
	}

	public void insertContainerEmbalaje(String id, String idContainer, String idEmbalaje,
										String active){

		ContentValues cv = new ContentValues();

		cv.put(IDCONTAINEREMBALAJE, id);
		cv.put(IDCONTAINERs, idContainer);
		cv.put(IDEMBALAJE, idEmbalaje);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		nBD.insert(TBL_CAT_CONTAINEREMBALAJE, null, cv);
	}

	public ArrayList<HashMap<String,String>> getEmbalajes(String idContainer){

		ArrayList<HashMap<String,String>> itemsEmbalaje = new ArrayList<HashMap<String,String>>();


		String columnas[] = {IDEMBALAJE};
		Cursor c = nBD.query(TBL_CAT_CONTAINEREMBALAJE, columnas, IDCONTAINERs+" = "+idContainer, null, null, null, null);
		//String resultado [] = new String[c.getCount()];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			//resultado[c.getPosition()] = c.getString(0);
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("idEmbalaje",c.getString(0));

			itemsEmbalaje.add(map);
		}

		c.close();

		return itemsEmbalaje;
	}

	/********************************************************/
	/*************CATALOGO DE LOCATIONS*********************/
	/********************************************************/
	public void createTableLocations(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_LOCATIONS);

		nBD.execSQL("CREATE TABLE " + TBL_LOCATIONS + " (" +
				IDLOCATION + " INTEGER, " +
				VNAMELOCATION + " TEXT, " +
				WIDTH + " INTEGER, " +
				HEIGHT + " INTEGER," +
				LENGHT + " INTEGER," +
				VDESCRIPTION + " TEXT, " +
				ACTIVE+" INTEGER, "+
				ID_FARM + " INTEGER, " +
				IDTYPELOCATION + " INTEGER, " +
				IDCONTAINER + " INTEGER, " +
				IDFRAMEWORK + " INTEGER, " +
				DDATECREATED + " TEXT, " +
				VUSERCREATED + " TEXT, " +
				DDATEUPDATED + " TEXT, " +
				VUSERUPDATED + " TEXT," +
				VCODELOCATION + " TEXT"
				+ ")");
	}

	public void insertLocation(String id, String name, String w,
							   String h, String l, String active, String desc,
							   String farm, String type, String container,
							   String framework, String createdDate,
							   String createdUser, String updatedDate, String updatedUser,
							   String code){

		ContentValues cv = new ContentValues();

		cv.put(IDLOCATION, id);
		cv.put(VNAMELOCATION, name);
		cv.put(WIDTH, w);
		cv.put(HEIGHT, h);
		cv.put(LENGHT, l);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(VDESCRIPTION, desc);
		cv.put(ID_FARM, farm);
		cv.put(IDTYPELOCATION, type);
		cv.put(IDCONTAINER, container);
		cv.put(IDFRAMEWORK, framework);
		cv.put(DDATECREATED, createdDate);
		cv.put(VUSERCREATED, createdUser);
		cv.put(DDATEUPDATED, updatedDate);
		cv.put(VUSERUPDATED, updatedUser);
		cv.put(VCODELOCATION, code);
		nBD.insert(TBL_LOCATIONS, null, cv);
	}


	public locacion getLocationLineByCode(String vCode, int typeLocation){

		String columnas[] = {IDLOCATION, VNAMELOCATION, WIDTH, HEIGHT, LENGHT, ACTIVE,
				VDESCRIPTION, ID_FARM, IDTYPELOCATION, IDCONTAINER, IDFRAMEWORK, DDATECREATED,
				VUSERCREATED, DDATEUPDATED, VUSERUPDATED, VCODELOCATION};

		Cursor c = nBD.query(TBL_LOCATIONS, columnas, ACTIVE + " = 1 AND " + VCODELOCATION +" = '" + vCode + "' COLLATE NOCASE AND " + IDTYPELOCATION + " = " + typeLocation, null, null, null, IDLOCATION);

		locacion resultado = new locacion();

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			resultado.setIdLocation(c.getInt(0));
			resultado.setNameLocation(c.getString(1));
			resultado.setWidth(c.getInt(2));
			resultado.setHeight(c.getInt(3));
			resultado.setLenght(c.getInt(4));
			resultado.setActive(c.getInt(5));
			resultado.setDescription(c.getString(6));
			resultado.setFarm(c.getInt(7));
			resultado.setType(c.getInt(8));
			resultado.setContainer(c.getInt(9));
			resultado.setFramework(c.getInt(10));
			resultado.setCode(c.getString(15));
		}

		c.close();
		return resultado;
	}

	public Regex[] getRegexByCode(String vCode, int typeLocation){
		Regex[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " +
										"em." + IDEMBALAJE + ", " +
										"em." + VNAMEEMBALAJE + ", " +
										"em." + VREGEX + " " +
								"FROM " + TBL_LOCATIONS + " AS cl " +
								"INNER JOIN " + TBL_CAT_CONTAINEREMBALAJE + " AS ce ON (cl." + IDCONTAINER + " = ce." + IDCONTAINERs + ") " +
								"INNER JOIN " + TBL_CAT_EMBALAJES + " AS em ON (ce." + IDEMBALAJE + " = em." + IDEMBALAJE + ") " +
								"WHERE cl." + IDTYPELOCATION + " = " + typeLocation + " " +
								"AND cl." + VCODELOCATION + " = '" + vCode + "' " +
								"AND cl." + ACTIVE + " = 1 " +
								"AND ce." + ACTIVE + " = 1 " +
								"AND em." + ACTIVE + " = 1 ", null);

		resultado = new Regex[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Regex();
				resultado[cont].setIdEmbalaje(c.getInt(0));
				resultado[cont].setNombreEmbalaje(c.getString(1));
				resultado[cont].setRegex(c.getString(2));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Departamento[] getDepartamentosPorPlanta(int idPlanta){
		Departamento[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " +
										"pd." + ID_DEPARTMENT_PD + ", " +
										"sd." + ID_PLANTA_SD + ", " +
										"sd." + ID_SITE_SD + ", " +
										"pd." + NOMBRE_DEPARTMENT_PD + " " +

								"FROM " + NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS + " AS pd " +
								"INNER JOIN " + NOMBRE_TABLA_CAT_SITES_DEPARTMENTS + " AS sd ON (pd." + ID_DEPARTMENT_PD + " = sd." + ID_DEPARTMENT_SD + ") " +
								"INNER JOIN " + NOMBRE_TABLA_CAT_PLANTS_SITES + " AS ps ON (sd." + ID_SITE_SD + " = ps." + ID_SITE_PS + ") " +
								"WHERE sd." + ID_PLANTA_SD + " = ps." + ID_PLANTA_PS + " " +
								//"AND sd." + ID_PLANTA_SD + " = " + idPlanta + " " +
								"AND pd." + ESTADO_DEPARTMENT_PD + " = 1 " +
                                "ORDER BY pd." + NOMBRE_DEPARTMENT_PD + " ASC", null);

		resultado = new Departamento[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Departamento();
				resultado[cont].setIdDepartamento(c.getInt(0));
				resultado[cont].setIdPlanta(c.getInt(1));
				resultado[cont].setIdSite(c.getInt(2));
				resultado[cont].setNombreDepartamento(c.getString(3));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Departamento[] getDepartamentoPorPlanta(int idPlanta, int idDepartamento){
		Departamento[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " +
				"pd." + ID_DEPARTMENT_PD + ", " +
				"sd." + ID_PLANTA_SD + ", " +
				"sd." + ID_SITE_SD + ", " +
				"pd." + NOMBRE_DEPARTMENT_PD + " " +

				"FROM " + NOMBRE_TABLA_CAT_PLANTS_DEPARTMENTS + " AS pd " +
				"INNER JOIN " + NOMBRE_TABLA_CAT_SITES_DEPARTMENTS + " AS sd ON (pd." + ID_DEPARTMENT_PD + " = sd." + ID_DEPARTMENT_SD + ") " +
				"INNER JOIN " + NOMBRE_TABLA_CAT_PLANTS_SITES + " AS ps ON (sd." + ID_SITE_SD + " = ps." + ID_SITE_PS + ") " +
				"WHERE sd." + ID_PLANTA_SD + " = ps." + ID_PLANTA_PS + " " +
				"AND pd." + ID_DEPARTMENT_PD + " = " +idDepartamento + " " +
				//"AND sd." + ID_PLANTA_SD + " = " + idPlanta + " " +
				"AND pd." + ESTADO_DEPARTMENT_PD + " = 1 " +
				"ORDER BY pd." + NOMBRE_DEPARTMENT_PD + " ASC", null);

		resultado = new Departamento[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Departamento();
				resultado[cont].setIdDepartamento(c.getInt(0));
				resultado[cont].setIdPlanta(c.getInt(1));
				resultado[cont].setIdSite(c.getInt(2));
				resultado[cont].setNombreDepartamento(c.getString(3));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Razon[] getRazonesPorDepartamento(int idDepartamento, int liberacion){
		Razon[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " +
										"rw." + ID_REASON_RW + ", " +
										"rd." + ID_DEPARTMENT_RD + ", " +
										"rw." + NOMBRE_REASON_RW + " " +

								"FROM " + NOMBRE_TABLA_CAT_REASON_WASTE + " AS rw " +
								"INNER JOIN " + NOMBRE_TABLA_CAT_REASON_DEPARTMENTS + " AS rd ON (rw." + ID_REASON_RW + " = rd." + ID_REASON_RD + ") " +
								"WHERE rd." + ID_DEPARTMENT_RD + " = " + idDepartamento + " " +
								"AND rw." + LIBERACION_REASON_RW + " = " + liberacion + " " +
								"AND rw." + ESTADO_REASON_RW + " = 1 " +
								"ORDER BY rw." + NOMBRE_REASON_RW + " ASC" , null);

		resultado = new Razon[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Razon();
				resultado[cont].setIdRazon(c.getInt(0));
				resultado[cont].setIdDepartamento(c.getInt(1));
				resultado[cont].setNombreRazon(c.getString(2));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Calidad[] getTiposCalidad(){
		Calidad[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT * FROM " + NOMBRE_TABLA_CAT_QUALITY_TYPE + " " +
								"WHERE " + ESTADO_QUALITY_TYPE + " = 1 " +
								"AND " + ID_QUALITY_TYPE + " != 3 " +
								"AND " + ID_QUALITY_TYPE + " != 4 " +
								"ORDER BY " + NOMBRE_QUALITY_TYPE + " ASC" , null);

		resultado = new Calidad[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Calidad();

				resultado[cont].setIdCalidad(c.getInt(c.getColumnIndex(ID_QUALITY_TYPE)));
				resultado[cont].setNombreCalidad(c.getString(c.getColumnIndex(NOMBRE_QUALITY_TYPE)));
				resultado[cont].setNombreCortoCalidad(c.getString(c.getColumnIndex(NOMBRE_CORTO_QT)));
				resultado[cont].setDescripcionCalidad(c.getString(c.getColumnIndex(DESCRIPCION_QUALITY_TYPE)));
				resultado[cont].setFactorCalidad(c.getInt(c.getColumnIndex(FACTOR_QUALITY_TYPE)));
				resultado[cont].setEstadoCalidad(c.getInt(c.getColumnIndex(ESTADO_QUALITY_TYPE)));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Razon[] getRazonesParaLiberar(){
		Razon[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " + ID_REASON_RW + ", "+
											NOMBRE_REASON_RW + " " +
								"FROM " + NOMBRE_TABLA_CAT_REASON_WASTE + " " +
								"WHERE " + LIBERACION_REASON_RW + " = 1 " +
								"AND " + ESTADO_REASON_RW + " = 1 " +
								"ORDER BY " + NOMBRE_REASON_RW + " ASC" , null);

		resultado = new Razon[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Razon();
				resultado[cont].setIdRazon(c.getInt(0));
				resultado[cont].setNombreRazon(c.getString(1));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public Disposicion[] getDispocisionesMerma(){
		Disposicion[] resultado;
		int cont = 0;

		Cursor c = nBD.rawQuery("SELECT " + ID_DISPOSITION_WASTE + ", "+
											NOMBRE_DISPOSITION_WASTE + ", " +
											ESTADO_DISPOSITION_WASTE + " " +
								"FROM " + NOMBRE_TABLA_CAT_DISPOSITION_WASTE + " " +
								"WHERE " + ESTADO_DISPOSITION_WASTE + " = 1 ", null);

		resultado = new Disposicion[c.getCount()];

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[cont] = new Disposicion();

				resultado[cont].setIdDisposicion(c.getString(c.getColumnIndex(ID_DISPOSITION_WASTE)));
				resultado[cont].setNombreDisposicion(c.getString(c.getColumnIndex(NOMBRE_DISPOSITION_WASTE)));
				resultado[cont].setEstadoDisposicion(c.getInt(c.getColumnIndex(ESTADO_DISPOSITION_WASTE)));
				cont++;
			}
		}

		c.close();

		return resultado;
	}

	public locacion getLocationById( String idLocation){

		String columnas[] = {IDLOCATION, VNAMELOCATION, WIDTH, HEIGHT, LENGHT, ACTIVE,
							 VDESCRIPTION, ID_FARM, IDTYPELOCATION, IDCONTAINER, IDFRAMEWORK, DDATECREATED,
							 VUSERCREATED, DDATEUPDATED, VUSERUPDATED, VCODELOCATION};
		Cursor c = nBD.query(TBL_LOCATIONS, columnas, ACTIVE+" = 1 AND "+IDLOCATION +" = "+idLocation, null, null, null, IDLOCATION);
		//Cursor c = nBD.rawQuery("SELECT ", null);
		//String resultado [][] = new String[c.getCount()][columnas.length] ;

		locacion resultado = new locacion();

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			resultado.setIdLocation(c.getInt(0));
			resultado.setNameLocation(c.getString(1));
			resultado.setWidth(c.getInt(2));
			resultado.setHeight(c.getInt(3));
			resultado.setLenght(c.getInt(4));
			resultado.setActive(c.getInt(5));
			resultado.setDescription(c.getString(6));
			resultado.setFarm(c.getInt(7));
			resultado.setType(c.getInt(8));
			resultado.setContainer(c.getInt(9));
			resultado.setFramework(c.getInt(10));
			resultado.setCode(c.getString(15));
		}

			//for(int i=0; i<columnas.length; i++ )
				//resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;

	}

	public String[][] getLocations(){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LOCATIONS + " (" +
				IDLOCATION + " INTEGER, " +
				VNAMELOCATION + " TEXT, " +
				WIDTH + " INTEGER, " +
				HEIGHT + " INTEGER," +
				LENGHT + " INTEGER," +
				VDESCRIPTION + " TEXT, " +
				ACTIVE+" INTEGER, "+
				ID_FARM + " INTEGER, " +
				IDTYPELOCATION + " INTEGER, " +
				IDCONTAINER + " INTEGER, " +
				IDFRAMEWORK + " INTEGER, " +
				DDATECREATED + " TEXT, " +
				VUSERCREATED + " TEXT, " +
				DDATEUPDATED + " TEXT, " +
				VUSERUPDATED + " TEXT," +
				VCODELOCATION + " TEXT"
				+ ")");

		String columnas[] = {IDLOCATION, VNAMELOCATION};
		Cursor c = nBD.query(TBL_LOCATIONS, columnas, ACTIVE+" = 1", null, null, null, IDLOCATION);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;

	}


	public String[][] getLocationsByFarm( String idFarm){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LOCATIONS + " (" +
				IDLOCATION + " INTEGER, " +
				VNAMELOCATION + " TEXT, " +
				WIDTH + " INTEGER, " +
				HEIGHT + " INTEGER," +
				LENGHT + " INTEGER," +
				VDESCRIPTION + " TEXT, " +
				ACTIVE+" INTEGER, "+
				ID_FARM + " INTEGER, " +
				IDTYPELOCATION + " INTEGER, " +
				IDCONTAINER + " INTEGER, " +
				IDFRAMEWORK + " INTEGER, " +
				DDATECREATED + " TEXT, " +
				VUSERCREATED + " TEXT, " +
				DDATEUPDATED + " TEXT, " +
				VUSERUPDATED + " TEXT," +
				VCODELOCATION + " TEXT"
				+ ")");

		String columnas[] = {IDLOCATION, VNAMELOCATION};
		Cursor c = nBD.query(TBL_LOCATIONS, columnas, ACTIVE+" = 1 AND "+ID_FARM +" = "+idFarm, null, null, null, IDLOCATION);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;

	}



	/********************************************************/
	/*************CATALOGO DE ITEMMASTER*********************/
	/********************************************************/
	public void createTableItemMaster(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_ITEMMASTER);

		nBD.execSQL("CREATE TABLE " + TBL_ITEMMASTER + " (" +
				IDITEMMASTER + " INTEGER, " +
				ITEMNMBR + " INTEGER, " +
				ITEMDESC + " TEXT, " +
				SUPERCATEGORY + " TEXT," +
				CASEWEIGHT + " DECIMAL," +
				PALLETWEIGHT + " DECIMAL," +
				CASESPERPALLET + " INTEGER"
				+ ")");
	}

	public void insertItemMaster(String id, String itemnmbr, String itemdesc,
								 String supercategory, String caseweight, String palletweight,
								 String casesperpallet){

		ContentValues cv = new ContentValues();

		cv.put(IDITEMMASTER, id);
		cv.put(ITEMNMBR, itemnmbr);
		cv.put(ITEMDESC, itemdesc);
		cv.put(SUPERCATEGORY, supercategory);
		cv.put(CASEWEIGHT, caseweight);
		cv.put(PALLETWEIGHT, palletweight);
		cv.put(CASESPERPALLET, casesperpallet);
		nBD.insert(TBL_ITEMMASTER, null, cv);
	}

	public String[] getItemsMaster(){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_ITEMMASTER + " (" +
				IDITEMMASTER + " INTEGER, " +
				ITEMNMBR + " INTEGER, " +
				ITEMDESC + " TEXT, " +
				SUPERCATEGORY + " TEXT," +
				CASEWEIGHT + " DECIMAL," +
				PALLETWEIGHT + " DECIMAL," +
				CASESPERPALLET + " INTEGER"
				+ ")");

		//String columnas[] = {IDITEMMASTER, ITEMNMBR, ITEMDESC, SUPERCATEGORY, CASEWEIGHT, PALLETWEIGHT, CASESPERPALLET};
		String columnas[] = {ITEMNMBR};
		Cursor c = nBD.query(TBL_ITEMMASTER, columnas, null, null, null, null, null);
		String resultado [] = new String[c.getCount()];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			resultado [c.getPosition()] = c.getString(0);

		c.close();
		return resultado;
	}

	/*
	* 	public static final String IDITEMMASTER = "id";
	public static final String ITEMNMBR = "ITEMNMBR";
	public static final String ITEMDESC = "ITEMDESC";
	public static final String SUPERCATEGORY = "SUPERCATEGORY";
	public static final String CASEWEIGHT = "CASEWEIGHT";
	public static final String PALLETWEIGHT = "PALLETWEIGHT";
	public static final String CASESPERPALLET = "CASESPERPALLET";
	*
	* */

	/********************************************************/
	/********************CATALOGO DE PROMOTIONS******************/
	/********************************************************/
	public void createTablePromotions(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NXIV00102);

		nBD.execSQL("CREATE TABLE " + NXIV00102+ " (" +
				IDPROMOTIONS + " TEXT, " +
				DESCPROMOTIONS + " TEXT "
				+")");
	}

	public void insertPromotions(String idPromotion, String DescPromotions){
		ContentValues cv = new ContentValues();

		cv.put(IDPROMOTIONS, idPromotion);
		cv.put(DESCPROMOTIONS, DescPromotions);
		nBD.insert(NXIV00102, null, cv);
	}

	public String[][] getPromotions(){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + NXIV00102+ " (" +
				IDPROMOTIONS + " TEXT, " +
				DESCPROMOTIONS + " TEXT "
				+")");

		String columnas[] = {IDPROMOTIONS, DESCPROMOTIONS};
		Cursor c = nBD.query(NXIV00102, columnas, null, null, null, null, null);
		String resultado [][] = new String[c.getCount()][columnas.length];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();

		return resultado;
	}

	/********************************************************/
	/********************CATALOGO DE TAMAÑOS******************/
	/********************************************************/
	public void createTableSizeCode(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_SIZECODE);

		nBD.execSQL("CREATE TABLE " + TBL_SIZECODE+ " (" +
				IDSIZECODE + " INTEGER, " +
				CODESIZE + " TEXT "
				+")");
	}

	public void insertSizeCode(String idSizeCode, String CodeSize){
		ContentValues cv = new ContentValues();

		cv.put(IDSIZECODE, idSizeCode);
		cv.put(CODESIZE, CodeSize);
		nBD.insert(TBL_SIZECODE, null, cv);
	}

	public String[][] getSizeCode(){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_SIZECODE+ " (" +
				IDSIZECODE + " INTEGER, " +
				CODESIZE + " TEXT "
				+")");

		String columnas[] = {IDSIZECODE, CODESIZE};
		Cursor c = nBD.query(TBL_SIZECODE, columnas, IDSIZECODE + " != 0", null, null, null, null);
		String resultado [][] = new String[c.getCount()][columnas.length];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i = 0; i < columnas.length; i++ )
				resultado[c.getPosition()][i] = c.getString(i);

		c.close();

		return resultado;
	}


	/********************************************************/
	/********************CATALOGO DE LINEPAKAGE******************/
	/********************************************************/
	public void createTableLineSKU(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_LINESKU);

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LINESKU+ " (" +
				ID_LINE + " INTEGER, " +
				VSKU + " TEXT, " +
				LASTUPDATEBY + " TEXT, " +
				LASTUPDATETIME + " TEXT"
				+")");
	}

	public void insertLineSKU(String idLine, String vSKU, String lastUpdateBy, String lastUpdateTime){
		ContentValues cv = new ContentValues();

		cv.put(ID_LINE, idLine);
		cv.put(VSKU, vSKU);
		cv.put(LASTUPDATEBY, lastUpdateBy);
		cv.put(LASTUPDATETIME, lastUpdateTime);
		nBD.insert(TBL_LINESKU, null, cv);
	}

	public ArrayList<String[]> getLineSKUbyIdLine(String idLine){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LINESKU+ " (" +
				ID_LINE + " INTEGER, " +
				VSKU + " TEXT, " +
				LASTUPDATEBY + " TEXT, " +
				LASTUPDATETIME + " TEXT"
				+")");
		ArrayList<String[]> skusList =  new ArrayList<>();

		String columnas[] = {VSKU};
		Cursor c = nBD.query(TBL_LINESKU, columnas, ID_LINE+" = "+idLine , null, null, null, null);
		String resultado [];

		if(c.getCount() != 0) {
			resultado  = new String[c.getCount()];
			int i = 0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				resultado[i] = c.getString(0);
				//Log.d("sku", c.getString(0));
				i++;
			}

			skusList.add(resultado);
		}
		c.close();



		return skusList;
	}

	public String[][] getLineSKU(String idLine){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LINESKU+ " (" +
				ID_LINE + " INTEGER, " +
				VSKU + " TEXT, " +
				LASTUPDATEBY + " TEXT, " +
				LASTUPDATETIME + " TEXT"
				+")");

		String columnas[] = {VSKU};
		Cursor c = nBD.query(TBL_LINESKU, columnas, ID_LINE+" = "+idLine , null, null, null, null);
		String resultado [][] = new String[c.getCount()][columnas.length];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}

    public String[][] getLinesSKU(String lineas){

        nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_LINESKU+ " (" +
                ID_LINE + " INTEGER, " +
                VSKU + " TEXT, " +
                LASTUPDATEBY + " TEXT, " +
                LASTUPDATETIME + " TEXT"
                +")");

        String columnas[] = {VSKU};

        Cursor c = nBD.rawQuery("SELECT DISTINCT " + VSKU + " FROM " + TBL_LINESKU + " WHERE " + ID_LINE + " IN (" + lineas + ") ORDER BY " + VSKU + " ASC", null);

        String resultado [][] = new String[c.getCount()][columnas.length];

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            for(int i=0; i<columnas.length; i++ )
                resultado [c.getPosition()][i] = c.getString(i);

        c.close();
        return resultado;
    }


	/********************************************************/
	/********************CATALOGO DE LINEPAKAGE******************/
	/********************************************************/
	public void createTableLinePackage(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_LINESPACKAGE);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_LINESPACKAGE+ " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER,"+
				ID_GP+" INTEGER,"+
				ID_SITE +" TEXT"
				+")");
	}

	public void insertLinePackage(String idLine, String vNameLine, String vDescript, String vPathImage, String vTypeLine, String active, String idPlant, String idGP, String idSite){
		ContentValues cv = new ContentValues();

		cv.put(ID_LINEPACKAGE, idLine);
		cv.put(VNAMELINE, vNameLine);
		cv.put(VDESCRIPTIONLINE, vDescript);
		cv.put(VPATH_IMAGELINE, vPathImage);
		cv.put(VTYPELINE, vTypeLine);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(ID_PLANT, idPlant);
		cv.put(ID_GP, idGP);
		cv.put(ID_SITE, idSite);
		nBD.insert(TBL_CAT_LINESPACKAGE, null, cv);
	}

	public String[][] getLinePackage(String idFarm){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_LINESPACKAGE + " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE + " INTEGER," +
				ID_PLANT + " INTEGER," +
				ID_GP+" INTEGER,"+
				ID_SITE +" TEXT"
				+ ")");

		String columnas[] = {ID_LINEPACKAGE, VNAMELINE};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ACTIVE+" = 1 AND "+ID_PLANT+" = "+idFarm , null, null, null, ID_LINEPACKAGE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}

	public String[][] getLinePackage(String idFarm, String idSite){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_LINESPACKAGE + " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE + " INTEGER," +
				ID_PLANT + " INTEGER," +
				ID_GP+" INTEGER,"+
				ID_SITE +" TEXT"
				+ ")");

		String columnas[] = {ID_LINEPACKAGE, VNAMELINE};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ACTIVE + " = 1 AND " + ID_PLANT + " = " + idFarm + " AND " + ID_SITE + " = '" + idSite + "'", null, null, null, ID_LINEPACKAGE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}

	public String[] getSitesFarm(String idFarm){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_LINESPACKAGE + " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE + " INTEGER," +
				ID_PLANT + " INTEGER," +
				ID_GP+" INTEGER,"+
				ID_SITE +" TEXT"
				+ ")");

		Cursor c = nBD.rawQuery("SELECT DISTINCT " + ID_SITE + " FROM " + TBL_CAT_LINESPACKAGE + " WHERE " + ACTIVE + " = 1 AND " + ID_PLANT + " = " + idFarm, null);
		String resultado[] = new String[c.getCount()];

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			resultado[c.getPosition()] = c.getString(0);

		c.close();
		return resultado;
	}

	public String getNombreLinea(int idFarm, int idGPLine){
		String columnas[] = {VNAMELINE};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ID_PLANT + " = " + idFarm + " AND " + ID_GP + " = " + idGPLine, null, null, null, ID_LINEPACKAGE);
		String resultado = "";

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			resultado = c.getString(0);

		c.close();

		return resultado;
	}

	public Linea getLinea(String idLine){
		Linea linea = new Linea();

		Cursor c = nBD.rawQuery("SELECT * FROM " + TBL_CAT_LINESPACKAGE + " WHERE " + ID_LINEPACKAGE + " = " + idLine, null);

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			linea.setIdLinea(c.getInt(c.getColumnIndex(ID_LINEPACKAGE)));
			linea.setIdGPLinea(c.getInt(c.getColumnIndex(ID_GP)));
			linea.setNombreLinea(c.getString(c.getColumnIndex(VNAMELINE)));
			linea.setActive((c.getInt(c.getColumnIndex(ACTIVE)) == 1) ? true : false);
		}

		c.close();

		return linea;
	}

	/*public int getIdLineaPackage(int idFarm, int idGPLine){
		String columnas[] = {ID_LINEPACKAGE};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ID_PLANT + " = " + idFarm + " AND " + ID_GP + " = " + idGPLine, null, null, null, ID_LINEPACKAGE);
		int resultado = 0;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			resultado = c.getInt(0);

		c.close();

		return resultado;
	}*/

	public String[][] getInfoLinePackage(String idLine){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_LINESPACKAGE+ " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER,"+
				ID_GP+" INTEGER,"+
				ID_SITE +" TEXT"
				+")");

		String columnas[] = {ID_LINEPACKAGE, VNAMELINE};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ACTIVE+" = 1 AND "+ID_LINEPACKAGE+" = "+idLine , null, null, null, ID_LINEPACKAGE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}

	public ArrayList<linesPackage> getLinePackageByIdLine(String idLine, int idFarm){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_LINESPACKAGE+ " (" +
				ID_LINEPACKAGE + " INTEGER PRIMARY KEY, " +
				VNAMELINE + " TEXT, " +
				VDESCRIPTIONLINE + " TEXT, " +
				VPATH_IMAGELINE + " TEXT, " +
				VTYPELINE + " TEXT, " +
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER,"+
				ID_GP +" INTEGER"
				+")");

		String columnas[] = {ID_LINEPACKAGE, VNAMELINE, VDESCRIPTIONLINE, VPATH_IMAGELINE, VTYPELINE, ACTIVE, ID_PLANT, ID_GP};
		Cursor c = nBD.query(TBL_CAT_LINESPACKAGE, columnas, ACTIVE+" = 1 AND "+ID_LINEPACKAGE+" = "+idLine+" AND "+ID_PLANT+" = "+idFarm , null, null, null, ID_LINEPACKAGE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		ArrayList<linesPackage> alLp = new ArrayList<>();

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			linesPackage lp = new linesPackage();

			lp.setIdLine(c.getInt(0));
			lp.setvNameLine(c.getString(1));
			lp.setvDescriptionLine(c.getString(2));
			lp.setvPathImageLine(c.getString(3));
			lp.setvTypeLine(c.getString(4));
			lp.setbActive(c.getInt(5)==0?false:true);
			lp.setIdPlant(c.getInt(6));
			lp.setIdGP(c.getInt(7));

			alLp.add(lp);
		}

		c.close();
		return alLp;

	}


	/********************************************************/
	/********************TABLAS THREASHREASON******************/
	/********************************************************/
	public void createTableThreshReason(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_THREASHREASON);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_THREASHREASON+ " (" +
				ID_THRESHREASON + " INTEGER PRIMARY KEY, " +
				VREASON + " TEXT, " +
				ACTIVE+" INTEGER,"+
				IDPLANT+" INTEGER"
				+")");
	}

	public void insertThreshReason(String idThreshReason, String vReason,String active, String idPlant){
		ContentValues cv = new ContentValues();

		cv.put(ID_THRESHREASON, idThreshReason);
		cv.put(VREASON, vReason);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(IDPLANT, idPlant);
		nBD.insert(TBL_CAT_THREASHREASON, null, cv);
	}


	public String[][] getThreshReason(String idFarm){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_THREASHREASON + " (" +
				ID_THRESHREASON + " INTEGER PRIMARY KEY, " +
				VREASON + " TEXT, " +
				ACTIVE+" INTEGER,"+
				IDPLANT+" INTEGER"
				+")");

		String columnas[] = {ID_THRESHREASON, VREASON};
		Cursor c = nBD.query(TBL_CAT_THREASHREASON, columnas, ACTIVE+" = 1 AND "+IDPLANT+" = "+idFarm , null, null, null, ID_THRESHREASON);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}



	/********************************************************/
	/********************TABLAS UserExtraRole******************/
	/********************************************************/
	public void createTableuserExtraRole(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_USEREXTRAROLE);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_USEREXTRAROLE+ " (" +
				ID_EXTRAROLE + " INTEGER PRIMARY KEY, " +
				VSHORTNAME + " TEXT, " +
				VFULLNAME+" TEXT,"+
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER"
				+")");
	}

	public void insertUserExtraRole(String idUserExtraRole, String vShortName, String vFullName,  String active, String idPlant){
		ContentValues cv = new ContentValues();

		cv.put(ID_EXTRAROLE, idUserExtraRole);
		cv.put(VSHORTNAME, vShortName);
		cv.put(VFULLNAME, vFullName);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(ID_PLANT, idPlant);
		nBD.insert(TBL_CAT_USEREXTRAROLE, null, cv);
	}

	public String[][] getUserExtraRole(String idFarm){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_USEREXTRAROLE + " (" +
				ID_EXTRAROLE + " INTEGER PRIMARY KEY, " +
				VSHORTNAME + " TEXT, " +
				VFULLNAME+" TEXT,"+
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER"
				+")");

		String columnas[] = {ID_EXTRAROLE, VSHORTNAME};
		Cursor c = nBD.query(TBL_CAT_USEREXTRAROLE, columnas, ACTIVE+" = 1 AND "+ID_PLANT+" = "+idFarm , null, null, null, ID_EXTRAROLE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}


	public ArrayList<lineTripulaciones> getUserExtraRoleAL(String idFarm){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_USEREXTRAROLE + " (" +
				ID_EXTRAROLE + " INTEGER PRIMARY KEY, " +
				VSHORTNAME + " TEXT, " +
				VFULLNAME+" TEXT,"+
				ACTIVE+" INTEGER,"+
				ID_PLANT+" INTEGER"
				+")");

		ArrayList<lineTripulaciones> al_LineTrip =  new ArrayList<>();

		String columnas[] = {ID_EXTRAROLE, VSHORTNAME, VFULLNAME, ID_PLANT};
		Cursor c = nBD.query(TBL_CAT_USEREXTRAROLE, columnas, ACTIVE+" = 1 AND "+ID_PLANT+" = "+idFarm , null, null, null, ID_EXTRAROLE);
		//String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			lineTripulaciones lp = new lineTripulaciones();

			lp.setID(c.getInt(0));
			lp.setName(c.getString(1));
			lp.setFullName(c.getString(2));
			lp.setIdPlant(c.getInt(3));

			al_LineTrip.add(lp);
		}
		//for(int i=0; i<columnas.length; i++ )
		//resultado [c.getPosition()][i] = c.getString(i);

		c.close();


		return al_LineTrip;
	}





	/********************************************************/
	/********************TABLAS sku quality******************/
	/********************************************************/
	public void createTableSkuQuality(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_sku_quality);

		nBD.execSQL("CREATE TABLE " + TBL_sku_quality+ " (" +
				IDSKU + " INTEGER PRIMARY KEY, " +
				VNAMESKU + " TEXT, " +
				vQuality+" TEXT,"+
				ACTIVE+" INTEGER,"+
				idProduct+" INTEGER,"+
				iQuality+" INTEGER"
				+")");
	}

	public void insertSkuQuality(String idSku, String nameSku, String vQualit,  String active, String idProducto, String iQualit){
		ContentValues cv = new ContentValues();

		cv.put(IDSKU, idSku);
		cv.put(VNAMESKU, nameSku);
		cv.put(vQuality, vQualit);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(idProduct, idProducto);
		cv.put(iQuality, iQualit);
		nBD.insert(TBL_sku_quality, null, cv);
	}

	public String[][] getSkuQuality(String idProducto){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_sku_quality + " (" +
				IDSKU + " INTEGER PRIMARY KEY, " +
				VNAMESKU + " TEXT, " +
				vQuality+" TEXT,"+
				ACTIVE+" INTEGER,"+
				idProduct+" INTEGER,"+
				iQuality+" INTEGER"
				+")");

		String columnas[] = {IDSKU, VNAMESKU};
		Cursor c = nBD.query(TBL_sku_quality, columnas, ACTIVE+" = 1 AND "+idProduct+" = "+idProducto , null, null, null, IDSKU);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}



	/********************************************************/
	/*********************TABLAS EMBALAJES*******************/
	/********************************************************/

	public void createTablaEmbalaje(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_EMBALAJES);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_EMBALAJES+ " (" +
				IDEMBALAJE + " INTEGER PRIMARY KEY, " +
				VNAMEEMBALAJE + " TEXT, " +
				VDESCRIPEMBALAJE+" TEXT,"+
				VREGEX+" TEXT,"+
				ACTIVE+" INTEGER"
				+")");
	}

	public void insertEmbalaje(String idEmbalaje, String vNameEmbalaje, String vDescrip,  String vRegex, String active){
		ContentValues cv = new ContentValues();

		cv.put(IDEMBALAJE, idEmbalaje);
		cv.put(VNAMEEMBALAJE, vNameEmbalaje);
		cv.put(VDESCRIPEMBALAJE, vDescrip);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(VREGEX, vRegex);
		nBD.insert(TBL_CAT_EMBALAJES, null, cv);
	}

	public String getEmbalaje(String embalaje){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_EMBALAJES + " (" +
				IDEMBALAJE + " INTEGER PRIMARY KEY, " +
				VNAMEEMBALAJE + " TEXT, " +
				VDESCRIPEMBALAJE+" TEXT,"+
				VREGEX+" TEXT,"+
				ACTIVE+" INTEGER"
				+")");

		String columnas[] = {IDEMBALAJE, VREGEX};
		Cursor c = nBD.query(TBL_CAT_EMBALAJES, columnas, ACTIVE+" = 1", null, null, null, IDEMBALAJE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			if(embalaje.matches(c.getString(1)))
				return c.getString(0);

		c.close();

		return "-1";
	}



	/********************************************************/
	/***********************TABLAS ONHOLD*********************/
	/********************************************************/
	public void createTablaOnHoldReason(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CAT_REASONS);

		nBD.execSQL("CREATE TABLE " + TBL_CAT_REASONS + " (" +
				ID_CAT_REASON + " INTEGER PRIMARY KEY, " +
				VNAMEREASON + " TEXT, " +
				VDESCRIPREASON + " TEXT," +
				ACTIVE + " INTEGER," +
				IDFARM + " INTEGER," +
				BRELEASE + " INTEGER"
				+ ")");

		//Log.d("on hold","a");
	}

	public void insertOnHoldReason(String idCatReason, String vNameReason, String vDescrip, String active, String farm, String realease){
		ContentValues cv = new ContentValues();

		cv.put(ID_CAT_REASON, idCatReason);
		cv.put(VNAMEREASON, vNameReason);
		cv.put(VDESCRIPREASON, vDescrip);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(IDFARM,farm);
		cv.put(BRELEASE, realease.compareToIgnoreCase("True") == 0 ? 1 : 0);
		nBD.insert(TBL_CAT_REASONS, null, cv);
	}

	public String[][] getOnHoldReasonsForFarm(String idFarm){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CAT_REASONS+ " (" +
				ID_CAT_REASON + " INTEGER PRIMARY KEY, " +
				VNAMEREASON + " TEXT, " +
				VDESCRIPREASON+" TEXT,"+
				ACTIVE+" INTEGER,"+
				IDFARM + " INTEGER,"+
				BRELEASE+ " INTEGER"
				+")");

		String columnas[] = {ID_CAT_REASON, VNAMEREASON};
		Cursor c = nBD.query(TBL_CAT_REASONS, columnas, ACTIVE+" = 1 AND "+IDFARM+" = "+idFarm , null, null, null, ID_CAT_REASON);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}


	/********************************************************/
	/********************************************************/
	/********************TABLAS FARMS************************/
	/********************************************************/
	/********************************************************/
	public void insertFarm(String idFarm, String name, String active){

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		ContentValues cv = new ContentValues();

		cv.put(IDFARM, idFarm);
		cv.put(NOMBREFARM, name);
		cv.put(ACTIVE, active.compareToIgnoreCase("True") == 0?1:0);
		cv.put(FECHA_REGISTRO, dateFormat.format(cal.getTime()));
		nBD.insert(NOMBRE_TABLA_FARMS, null, cv);
	}

	public void creaTablaFarm(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_FARMS);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_FARMS + " (" +
				IDFARM + " INTEGER PRIMARY KEY, " +
				NOMBREFARM + " TEXT, " +
				ACTIVE + " INTEGER," +
				FECHA_REGISTRO + " DATETIME)");
	}

	public String[][] obtenerFarms(){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + NOMBRE_TABLA_FARMS + " (" +
					IDFARM + " INTEGER PRIMARY KEY, " +
					NOMBREFARM + " TEXT, " +
					ACTIVE + " INTEGER," +
					FECHA_REGISTRO + " DATETIME)");

		String columnas[] = {IDFARM, NOMBREFARM};
		Cursor c = nBD.query(NOMBRE_TABLA_FARMS, columnas, ACTIVE+" = 1", null, null, null, IDFARM);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}

	public String[][] obtenerFarmsToPrePallet(){
		Cursor c = nBD.rawQuery("SELECT pl." + IDFARM + ", pl." + NOMBREFARM +
								 " FROM " + NOMBRE_TABLA_FARMS + " AS pl" +
								 " INNER JOIN " + TBL_CAT_LINESPACKAGE + " AS li ON (pl." + IDFARM + " = li." + ID_PLANT + ")" +
								 " WHERE li." + ACTIVE + " = 1" +
								 " AND pl." + ACTIVE + " = 1" +
								 " GROUP BY pl." + IDFARM + ", pl." + NOMBREFARM, null);


		String resultado [][] = new String[c.getCount()][2] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			resultado[c.getPosition()][0] = c.getString(0);
			resultado[c.getPosition()][1] = c.getString(1);
		}

		c.close();
		return resultado;
	}

	public String[][] obtenerFarm(String idFarm){

		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + NOMBRE_TABLA_FARMS + " (" +
				IDFARM + " INTEGER PRIMARY KEY, " +
				NOMBREFARM + " TEXT, " +
				ACTIVE + " INTEGER," +
				FECHA_REGISTRO + " DATETIME)");

		String columnas[] = {IDFARM, NOMBREFARM};
		Cursor c = nBD.query(NOMBRE_TABLA_FARMS, columnas, ACTIVE+" = 1 AND "+IDFARM+" = "+idFarm, null, null, null, IDFARM);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;

	}

	public String obtenerNombrePlanta(int idPlanta){
		Cursor c = nBD.rawQuery("SELECT " + NOMBREFARM + " FROM " + NOMBRE_TABLA_FARMS + " WHERE " + IDFARM + " = " + idPlanta, null);
		String nombrePlanta = "";

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			nombrePlanta = c.getString(c.getColumnIndex(NOMBREFARM));
		}

		c.close();
		return nombrePlanta;
	}

	public double obtenerLibrasPorCajaSKU(String sku){
		Cursor c = nBD.rawQuery("SELECT " + CASEWEIGHT + " FROM " + TBL_ITEMMASTER + " WHERE " + ITEMNMBR + " = '" + sku + "'" , null);
		double lbsCajas = 0;

		if(c.getCount() != 0){
			c.moveToFirst();

			do {
				lbsCajas = c.getDouble(c.getColumnIndex(CASEWEIGHT));
			} while(c.moveToNext());
		}

		return lbsCajas;
	}

	public boolean existeTablaCasesPrePallet(){
		Cursor c = nBD.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + TBL_CASES_PREPALLET + "'", null);
		boolean sync = false;

		if(c.getCount() > 0)
			sync = true;

		c.close();

		return sync;
	}

	public void crearTablaCasesPrePallet(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_CASES_PREPALLET);
		nBD.execSQL("CREATE TABLE " + TBL_CASES_PREPALLET + " (" +
				ID_CASES_PREPALLET + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				ID_PREPALLET + " INTEGER, " +
				ID_CASES_DETAILS + " INTEGER, " +
				CODIGO_CASE + " TEXT, " +
				ESTADO_CP + " INTEGER, " +
				FECHA_CREACION_CP + " DATETIME, " +
				USUARIO_CREACION_CP + " TEXT, " +
				FECHA_MODIFICAICON_CP + " DATETIME, " +
				USUARIO_MODIFICACION_CP + " TEXT, " +
				MAC_TABLET_CP + " TEXT, " +
				UUID_PP + " TEXT, " +
				UUID_CD + " TEXT, " +
				UUID_CP + " TEXT, " +
				SYNC_CP + " INTEGER)");

		Log.d("CREATE TABLE", "OK");
	}

	public boolean existeTablaAsigPallet(){
		Cursor c = nBD.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + TBL_ASIGN_PALLET + "'", null);
		boolean sync = false;

		if(c.getCount() > 0)
			sync = true;

		c.close();

		return sync;
	}

	public void crearTablaAsigPallet(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_ASIGN_PALLET);
		nBD.execSQL("CREATE TABLE " + TBL_ASIGN_PALLET + " (" +
				ID_ASIGN_PALLET + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				ID_PREPALLET_AP + " INTEGER, " +
				//ID_PREPALLET_TABLET_AP + " INTEGER, " +
				//PALLET_AP + " TEXT, " +
				FOLIO_AP + " TEXT, " +
				CAJAS_AP + " INTEGER, " +
				//ID_PRODUCT_PALLET_AP + " INTEGER, " +
				//ID_PRODUCT_AP + " INTEGER, " +
				ID_LINEA + " INTEGER, " +
				MAC_TABLET_AP + " TEXT, " +
				UUID_AP + " TEXT, " +
				//ID_PRODUCT_LOG + " INTEGER, " +
				SYNC_AP + " INTEGER)");

		Log.d("CREATE TABLE", "OK");
	}

	public boolean existeTablaPalletsInGP(){
		Cursor c = nBD.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + TBL_PRE_PALLET_IN_GP + "'", null);
		boolean sync = false;

		if(c.getCount() > 0)
			sync = true;

		c.close();

		return sync;
	}

	public void crearTablaPalletsInGP(){
		nBD.execSQL("DROP TABLE IF EXISTS " + TBL_PRE_PALLET_IN_GP);
		nBD.execSQL("CREATE TABLE " + TBL_PRE_PALLET_IN_GP + " (" +
				ID_PRE_PALLET_IN_GP + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				ID_PRE_PALLET_FK + " INTEGER, " +
				PALLET_ID + " TEXT, " +
				ID_PALLET_GP + " TEXT)");

		Log.d("CREATE TABLE", "OK");
	}

	public void insertarCasesPrePallet(int idPP, int idCD, String caseCode, String mac, String uuidPP, String uuidCD) {
		ContentValues cv = new ContentValues();

		cv.put(ID_PREPALLET, idPP);
		cv.put(ID_CASES_DETAILS, idCD);
		cv.put(CODIGO_CASE, caseCode);
		cv.put(ESTADO_CP, 1);
		cv.put(FECHA_CREACION_CP, config.obtenerFechaHora());
		cv.put(USUARIO_CREACION_CP, sharedpreferences.getString("username","jcalderon"));
		cv.put(FECHA_MODIFICAICON_CP, config.obtenerFechaHora());
		cv.put(USUARIO_MODIFICACION_CP, sharedpreferences.getString("username","jcalderon"));
		cv.put(MAC_TABLET_CP, mac);
		cv.put(UUID_PP, uuidPP);
		cv.put(UUID_CD, uuidCD);
		cv.put(UUID_CP, UUID.randomUUID().toString());
		cv.put(SYNC_CP, 0);

		Log.d("INSERT CASE", "OK");

		nBD.insert(TBL_CASES_PREPALLET, null, cv);
	}

	public void borrarCasesPrePallet(int idPrePallet) {
		ContentValues cv = new ContentValues();

		nBD.delete(TBL_CASES_PREPALLET, ID_PREPALLET + " = " + idPrePallet, null);

		Log.d("DELETE CASES", "OK");
	}

	public void insertarAsignacionPrePallet(int idPP, String pallet, String folio, int cajas, String mac, String linea, int idLog) {
		ContentValues cv = new ContentValues();

		cv.put(ID_PREPALLET_AP, idPP);
		//cv.put(ID_PREPALLET_TABLET_AP, idPP);
		//cv.put(PALLET_AP, pallet);
		cv.put(FOLIO_AP, folio);
		cv.put(CAJAS_AP, cajas);
		//cv.put(ID_PRODUCT_PALLET_AP, 0);
		//cv.put(ID_PRODUCT_AP, 0);
		//cv.put(ID_LINEA, linea);
		cv.put(ID_LINEA, linea);
		cv.put(MAC_TABLET_AP, mac);
		cv.put(UUID_AP, UUID.randomUUID().toString());
		//cv.put(ID_PRODUCT_LOG, idLog);
		cv.put(SYNC_AP, 0);

		Log.d("INSERT FOLIO", "OK");

		nBD.insert(TBL_ASIGN_PALLET, null, cv);
	}

	public void borrarAsignacionPrePallet(int idPrePallet) {
		ContentValues cv = new ContentValues();

		nBD.delete(TBL_ASIGN_PALLET, ID_PREPALLET_AP + " = " + idPrePallet, null);

		Log.d("DELETE FOLIOS", "OK");
	}

	public void insertarPalletsInGP(int idPP, String palletID, String idPalletGP) {
		ContentValues cv = new ContentValues();

		cv.put(ID_PRE_PALLET_FK, idPP);
		cv.put(PALLET_ID, palletID);
		cv.put(ID_PALLET_GP, idPalletGP);

		Log.d("ID_PRE_PALLET_FK", idPP+"");
		Log.d("PALLET_ID", palletID);
		Log.d("ID_PALLET_GP", idPalletGP);

		Log.d("INSERT PP IN GP", "OK");

		nBD.insert(TBL_PRE_PALLET_IN_GP, null, cv);
	}

	public String[][] selectCasesPrePalletToSync(){
		Cursor c = nBD.rawQuery("SELECT * FROM " + TBL_CASES_PREPALLET + " WHERE " + SYNC_CP + " = 0", null);
		String [][] registros = new String[c.getCount()][14];
		int cont = 0;

		if(c.getCount() != 0){
			c.moveToFirst();

			do {
				registros[cont][0] = c.getString(0);
				registros[cont][1] = c.getString(1);
				registros[cont][2] = c.getString(2);
				registros[cont][3] = c.getString(3);
				registros[cont][4] = c.getString(4);
				registros[cont][5] = c.getString(5);
				registros[cont][6] = c.getString(6);
				registros[cont][7] = c.getString(7);
				registros[cont][8] = c.getString(8);
				registros[cont][9] = c.getString(9);
				registros[cont][10] = c.getString(10);
				registros[cont][11] = c.getString(11);
				registros[cont][12] = c.getString(12);
				registros[cont][13] = c.getString(13);

				cont++;
			} while(c.moveToNext());
		}

		return registros;
	}

	public String[][] selectAsignacionPrePalletToSync(){
		Cursor c = nBD.rawQuery("SELECT * FROM " + TBL_ASIGN_PALLET + " WHERE " + SYNC_AP + " = 0", null);
		String [][] registros = new String[c.getCount()][8];
		int cont = 0;

		if(c.getCount() != 0){
			c.moveToFirst();

			do {
				registros[cont][0] = c.getString(0);
				registros[cont][1] = c.getString(1);
				registros[cont][2] = c.getString(2);
				registros[cont][3] = c.getString(3);
				registros[cont][4] = c.getString(4);
				registros[cont][5] = c.getString(5);
				registros[cont][6] = c.getString(6);
				registros[cont][7] = c.getString(7);

				cont++;
			} while(c.moveToNext());
		}

		return registros;
	}

	public void selectCasesPrePallet(){
		Cursor c = nBD.rawQuery("SELECT * FROM " + TBL_CASES_PREPALLET, null);

		Log.d("COLUMNAS", "ID | IDPP | IDCD | CASE | EDO | FCREA | UCREA | FUPD | UUPD | MAC TABLET | UUID PP | UUID CD | UUID CP | SYNC | ");

		if(c.getCount() != 0){
			c.moveToFirst();

			do {
				Log.d("COLUMNAS", 	c.getString(0) + " | " +
						c.getString(1) + " | " +
						c.getString(2) + " | " +
						c.getString(3) + " | " +
						c.getString(4) + " | " +
						c.getString(5) + " | " +
						c.getString(6) + " | " +
						c.getString(7) + " | " +
						c.getString(8) + " | " +
						c.getString(9) + " | " +
						c.getString(10) + " | " +
						c.getString(11) + " | " +
						c.getString(12) + " | " +
						c.getString(13) + " | ");
			} while(c.moveToNext());
		}
	}

	public void selectAsignacionPrePallet(){
		Cursor c = nBD.rawQuery("SELECT * FROM " + TBL_ASIGN_PALLET, null);

		Log.d("COLUMNAS", "ID | IDPP | PALLET | FOLIO | CAJAS | ID PP | ID P | ID L | MAC | UUID | ID PL | SYNC | ");

		if(c.getCount() != 0){
			c.moveToFirst();

			do {
				Log.d("COLUMNAS", 	c.getString(0) + " | " +
						c.getString(1) + " | " +
						c.getString(2) + " | " +
						c.getString(3) + " | " +
						c.getString(4) + " | " +
						c.getString(5) + " | " +
						c.getString(6) + " | " +
						c.getString(7) + " | ");
			} while(c.moveToNext());
		}
	}

	public void cambiarEdoSyncCasesPrePallet(String uuid){
		ContentValues cv = new ContentValues();

		cv.put(SYNC_CP, 1);

		nBD.update(TBL_CASES_PREPALLET, cv, UUID_CP + " = '" + uuid + "'", null);
	}

	public void cambiarEdoSyncAsigFoliosPrePallet(String uuid){
		ContentValues cv = new ContentValues();

		cv.put(SYNC_AP, 1);

		nBD.update(TBL_ASIGN_PALLET, cv, UUID_AP + " = '" + uuid + "'", null);
	}


	/********************************************************/
	/********************************************************/
	/********************TABLAS PESO*************************/
	/*******************ACTUALIZACIÓN************************/
	/********************************************************/
	public void insertPeso(String idPeso, String idProducto, String name, String active, String vfolio, String fpeso){

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		ContentValues cv = new ContentValues();

		cv.put(ID_PESO_FOLIO_EMBARQUE, idPeso);
		cv.put(FK_PRODUCTO_MERMADO, idProducto);
		cv.put(VFOLIO, vfolio);
		cv.put(FPESO, fpeso);
		cv.put(ESTADO_PESO_FOLIO, active.compareToIgnoreCase("True") == 0?1:0);

		nBD.insert(NOMBRE_TABLA_PESO_EMBARQUE, null, cv);
	}

	public void creaTablaPeso(){
		nBD.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_PESO_EMBARQUE);

		nBD.execSQL("CREATE TABLE " + NOMBRE_TABLA_PESO_EMBARQUE + " (" +
				ID_PESO_FOLIO_EMBARQUE + " INTEGER PRIMARY KEY, " +
				FK_PRODUCTO_MERMADO + " INTEGER , " +
				VFOLIO + " TEXT, " +
				FPESO + " FLOAT, " +
				ESTADO_PESO_FOLIO + " INTEGER)");
	}

	public String[][] obtenerPeso(){
		nBD.execSQL("CREATE TABLE IF NOT EXISTS " + NOMBRE_TABLA_PESO_EMBARQUE + " (" +
				ID_PESO_FOLIO_EMBARQUE + " INTEGER PRIMARY KEY, " +
				FK_PRODUCTO_MERMADO + " INTEGER , " +
				VFOLIO + " TEXT, " +
				FPESO + " FLOAT, " +
				ESTADO_PESO_FOLIO + " INTEGER)");

		String columnas[] = {ID_PESO_FOLIO_EMBARQUE, FPESO};
		Cursor c = nBD.query(NOMBRE_TABLA_PESO_EMBARQUE, columnas, ACTIVE+" = 1", null, null, null, ID_PESO_FOLIO_EMBARQUE);
		String resultado [][] = new String[c.getCount()][columnas.length] ;

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			for(int i=0; i<columnas.length; i++ )
				resultado [c.getPosition()][i] = c.getString(i);

		c.close();
		return resultado;
	}
}
