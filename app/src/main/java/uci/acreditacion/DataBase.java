package uci.acreditacion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class DataBase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db = getWritableDatabase();

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    //-------------------------------------Calendario--------------------------------------//

    //Metodo Pedir Datos del Dia
    public ArrayList<Object[]> getCronograma() {

        ArrayList<Object[]> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT id,nombreCronograma FROM cronogramas" , null);

        if(c.moveToFirst()) {
            do {
                Object[] obj = new Object[2];
                obj[0] = c.getInt(0);
                obj[1] = c.getString(1);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }

    //Metodo Pedir Datos del Dia
    public ArrayList<Object[]> getDayAll(String date, Integer id) {

        ArrayList<Object[]> array = new ArrayList<>();
        String day[] = new String[] {String.valueOf(id)};

        Cursor c = db.rawQuery("SELECT day,hour,activity,site,locate FROM events WHERE day="+ date +" AND crono_id=?" , day);

        if(c.moveToFirst()) {
            do {
                Object[] obj = new Object[5];
                obj[0] = c.getInt(0);
                obj[1] = c.getInt(1);
                obj[2] = c.getString(2);
                obj[3] = c.getString(3);
                obj[4] = c.getInt(4);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }
    public ArrayList<Double[]> posicion(int id){

        ArrayList<Double[]> array = new ArrayList<>();
        String nombre[] = new String[] {String.valueOf(id)};

        if(id == 0) {
            Double[] obj = new Double[2];
            obj[0] = 0.0;
            obj[1] = 0.0;
            array.add(obj);

            return array;
        }

        Cursor b = db.rawQuery("SELECT lat,lon FROM mapa WHERE _id=?" , nombre);

        if(b.moveToFirst()) {
            do {
                Double[] obj = new Double[2];
                obj[0] = b.getDouble(0);
                obj[1] = b.getDouble(1);
                array.add(obj);
            } while(b.moveToNext());
        }
        return array;
    }

    //-------------------------------------DIRECTORIO-MAP--------------------------------------//

    //Metodo Pedir Datos del Directorio
    public LinkedHashMap<String, List<String>> getDirectorioAll() {

        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();
        Integer cant = getDirectorioRoles();

        for(int i = 1; i<=cant; i++) {
            List<String> array_user = getCargoDirectorio(i);
            String arreglo = getNameoles(i);
            expandableListDetail.put(arreglo, array_user);
        }

        return expandableListDetail;
    }

    //Metodo Pedir Datos de los Roles
    public Integer getDirectorioRoles() {

        ArrayList<Integer> array = new ArrayList<>();

        //Buscar ID del Cargo
        Cursor cursor = db.rawQuery("SELECT count(id) as cant FROM roles", null);

        if(cursor.moveToFirst()) {
            do {
                Integer obj = cursor.getInt(0);
                array.add(obj);
            } while(cursor.moveToNext());
        }
        return array.get(0);
    }

    //Metodo Pedir Datos de los Roles
    public String getNameoles(int id) {

        ArrayList<String> array = new ArrayList<>();
        String ids[] = new String[] {String.valueOf(id)};

        //Buscar ID del Cargo
        Cursor cursor = db.rawQuery("SELECT role FROM roles WHERE id=?", ids);

        if(cursor.moveToFirst()) {
            do {
                String obj = cursor.getString(0);
                array.add(obj);
            } while(cursor.moveToNext());
        }
        return array.get(0);
    }

    //Metodo Pedir Datos de los Cargos
    public List<String> getCargoDirectorio(Object id) {

        ArrayList<String> array = new ArrayList<>();
        String ids[] = new String[] {String.valueOf(id)};

        //Buscar ID del Cargo
        Cursor cursor = db.rawQuery("SELECT nombreCargo FROM cargos WHERE role_id =?" , ids);

        if(cursor.moveToFirst()) {
            do {
                String obj = cursor.getString(0);
                array.add(obj);
            } while(cursor.moveToNext());
        }
        return array;
    }

    //-------------------------------------DIRECTORIO-DIALOG--------------------------------------//

    //Metodo Pedir Datos del Directorio
    public ArrayList<Object[]>  getDirectorioUser(String name) {

        ArrayList<Object[]> array = new ArrayList<>();
        String names =  String.valueOf(getDirectorioGrup(name).get(0));
        String nombre[] = new String[] {names};

        Cursor c = db.rawQuery("SELECT id,nombre,usuario,telf,radica FROM contacts WHERE cargo=?" , nombre);

        if(c.moveToFirst()) {
            do {
                Object[] obj = new Object[6];
                obj[0] = c.getInt(0);
                obj[1] = c.getString(1);
                obj[2] = c.getString(2);
                obj[3] = c.getInt(3);
                obj[4] = c.getString(4);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }


    //Metodo Pedir Datos de los Roles
    public ArrayList<Object> getDirectorioGrup(String name) {

        ArrayList<Object> array = new ArrayList<>();
        String nombre[] = new String[] {name};

        //Buscar ID del Cargo
        Cursor cursor = db.rawQuery("SELECT id,nombreCargo FROM cargos WHERE nombreCargo =?" , nombre);

        if(cursor.moveToFirst()) {
            do {
                Object obj = cursor.getInt(0);
                array.add(obj);
            } while(cursor.moveToNext());
        }
        return array;
    }

    //Metodo Buscar Usuario
    public ArrayList<String>  getSearchUser(String name) {

        ArrayList<String> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT nombre FROM contacts WHERE nombre LIKE '%"+ name +"%'" , null);
        if(c.moveToFirst()) {
            do {
                String obj = c.getString(0);
                array.add(obj);
            } while(c.moveToNext());
        }
        if(array.size() == 0)
            array.add("Sin Resultados");

        return array;
    }

    //Metodo Pedir Datos del Directorio
    public ArrayList<Object[]>  getDataUser(String name) {

        ArrayList<Object[]> array = new ArrayList<>();
        String nombre[] = new String[] {name};

        Cursor c = db.rawQuery("SELECT id,usuario,telf,radica FROM contacts WHERE nombre=?" , nombre);

        if(c.moveToFirst()) {
            do {
                Object[] obj = new Object[6];
                obj[0] = c.getInt(0);
                obj[1] = c.getString(1);
                obj[2] = c.getInt(2);
                obj[3] = c.getString(3);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }

    /* ------------------------------MAPA------------------------------------- */

    public class MyRecord
    {
        public int _id;
        public String phone,address,title,description,email;
        public double lat, lon;
        public Bitmap pict1, pict2, pict3;
    }

    //Metodo Buscar en el Mapa
    public ArrayList<Double[]> getMapa(String data) {

        ArrayList<Double[]> array = new ArrayList<>();
        String nombre[] = new String[] {data};

        //Buscar ID del Cargo
        Cursor cursor = db.rawQuery("SELECT lat,lon FROM mapa WHERE name =?" , nombre);

        if(cursor.moveToFirst()) {
            do {
                Double[] obj = new Double[2];
                obj[0] = cursor.getDouble(0);
                obj[1] = cursor.getDouble(1);
                array.add(obj);
            } while(cursor.moveToNext());
        }
        return array;
    }

    //Metodo Buscar en el Mapa
    public ArrayList<String>  getSearchMapa(String name) {

        ArrayList<String> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT name FROM mapa WHERE name LIKE '%"+ name +"%'" , null);
        if(c.moveToFirst()) {
            do {
                String obj = c.getString(0);
                array.add(obj);
            } while(c.moveToNext());
        }
        if(array.size() == 0)
            array.add("Sin Resultados");

        return array;
    }

    public static final class MainTable
    {
        private static final String TABLE_NAME="mapa";

        public static final String FIELD_ID="_id";
        public static final String FIELD_PHONE="phone";
        public static final String FIELD_ADDRESS="address";
        public static final String FIELD_TITLE="name";
        public static final String FIELD_DESCRIPTION="description";
        public static final String FIELD_LAT="lat";
        public static final String FIELD_LON="lon";
        public static final String FIELD_EMAIL="email";
        public static final String FIELD_TYPE="class";
        public static final String FIELD_PHOTO1="p1";
        public static final String FIELD_PHOTO2="p2";
        public static final String FIELD_PHOTO3="p3";
    }

    public Cursor GetMarkers()
    {
        MyRecord rd=new MyRecord();
        String[] projection = {
                MainTable.FIELD_ID,
                MainTable.FIELD_TYPE,
                MainTable.FIELD_TITLE,
                MainTable.FIELD_LAT,
                MainTable.FIELD_LON,
        };
        Cursor c = db.query(
                MainTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return (c.moveToFirst())? c: null;
    }

    public MyRecord GetMarkerInfo(int ID) {
        MyRecord rd = new MyRecord();
        String[] projection = {
                MainTable.FIELD_ID,
                MainTable.FIELD_ADDRESS,
                MainTable.FIELD_DESCRIPTION,
                MainTable.FIELD_EMAIL,
                MainTable.FIELD_PHONE,
                MainTable.FIELD_PHOTO1,
                MainTable.FIELD_PHOTO2,
                MainTable.FIELD_PHOTO3,
                MainTable.FIELD_TITLE,
                MainTable.FIELD_TYPE
        };
        String[] selectionArgs = {String.valueOf(ID)};
        String selection = MainTable.FIELD_ID + " LIKE ?";
        Cursor c = db.query(
                MainTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (c.moveToFirst()) {
            rd._id=c.getInt(c.getColumnIndex(MainTable.FIELD_ID));
            rd.address=c.getString(c.getColumnIndex(MainTable.FIELD_ADDRESS));
            rd.description=c.getString(c.getColumnIndex(MainTable.FIELD_DESCRIPTION));
            rd.email=c.getString(c.getColumnIndex(MainTable.FIELD_EMAIL));
            rd.phone=c.getString(c.getColumnIndex(MainTable.FIELD_PHONE));



            byte[] ba=c.getBlob(c.getColumnIndex(MainTable.FIELD_PHOTO1));
            rd.pict1=BitmapFactory.decodeByteArray(ba, 0, ba.length);
            ba=c.getBlob(c.getColumnIndex(MainTable.FIELD_PHOTO2));
            rd.pict2=BitmapFactory.decodeByteArray(ba, 0, ba.length);
            ba=c.getBlob(c.getColumnIndex(MainTable.FIELD_PHOTO3));
            rd.pict3=BitmapFactory.decodeByteArray(ba, 0, ba.length);
            rd.title=c.getString(c.getColumnIndex(MainTable.FIELD_TITLE));
            return rd;
        }
        return null;
    }

    /* ------------------------------MAPA------------------------------------- */

    //Metodo Mostrar Desarrolladores
    public ArrayList<String[]>  Desarrolladores() {

        ArrayList<String[]> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT id,name,work,enviroment FROM desarrollo" , null);
        if(c.moveToFirst()) {
            do {
                String obj[] = new String[4];
                obj[0] = c.getString(0);
                obj[1] = c.getString(1);
                obj[2] = c.getString(2);
                obj[3] = c.getString(3);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }

    //Metodo Buscar Desarrolladores
    public ArrayList<String[]>  getSearchDesarrollador(String name) {

        ArrayList<String[]> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT id,name,work,enviroment FROM desarrollo WHERE name LIKE '%"+ name +"%'" , null);
        if(c.moveToFirst()) {
            do {
                String obj[] = new String[4];
                obj[0] = c.getString(0);
                obj[1] = c.getString(1);
                obj[2] = c.getString(2);
                obj[3] = c.getString(3);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }

    //-------------------------------------Configuración--------------------------------------//


    //Metodo Buscar Desarrolladores
    public ArrayList<Object[]>  getServices(int day, int times, int id) {

        ArrayList<Object[]> array = new ArrayList<>();

        //---------------DEBUG------------//
        //System.out.println("-----");
        //System.out.println(day);
        //System.out.println("/");
        //System.out.println(times);
        //System.out.println("/");
        //System.out.println(id);
        //System.out.println("-----");

        Cursor c = db.rawQuery("SELECT hour,activity,site,locate FROM events WHERE crono_id = "+String.valueOf(id)+" AND hour > "+String.valueOf(times)+" AND day = "+String.valueOf(day)+" LIMIT 1" , null);
        if(c.moveToFirst()) {
            do {
                Object obj[] = new Object[4];
                obj[0] = c.getInt(0);
                obj[1] = c.getString(1);
                obj[2] = c.getString(2);
                obj[3] = c.getInt(3);
                array.add(obj);
            } while(c.moveToNext());
        }
        return array;
    }

    //-------------------------------------INFORME--------------------------------------//

    //Metodo Seleccionar Pagina
    public ArrayList getPages() {

        ArrayList array = new ArrayList();

        Cursor c = db.rawQuery("SELECT pages FROM informe " , null);
        if(c.moveToFirst()) {
            do {
                String a = c.getString(0);
                array.add(a);
            } while(c.moveToNext());
        }

        return array;
    }

    //Metodo Devolver Pagina
    public Integer getPageSelect(int id) {

        ArrayList<Integer> array = new ArrayList<>();
        String ids[] = new String[] {String.valueOf(id)};

        Cursor c = db.rawQuery("SELECT id FROM informe WHERE page_id =?" , ids);
        if(c.moveToFirst()) {
            do {
                array.add(c.getInt(0));
            } while(c.moveToNext());
        }
        return array.get(0);
    }

    //-------------------------------------CATEDRAS--------------------------------------//

    //Metodo Seleccionar Pagina
    public ArrayList<String[]> getCatedra() {

        ArrayList<String[]> array = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT name,phto FROM catedras " , null);
        if(c.moveToFirst()) {
            do {
                String[] a = new String[2];
                a[0] = c.getString(0);
                a[1] = c.getString(1);
                array.add(a);
            } while(c.moveToNext());
        }

        return array;
    }

    //Metodo Devolver Pagina
    public ArrayList<Object[]> getCatedraInfo(String name) {

        ArrayList<Object[]> array = new ArrayList<>();
        String ids[] = new String[] {name};

        System.out.println(name);

        Cursor c = db.rawQuery("SELECT name,description,phto,object,visepresident,president,locate FROM catedras WHERE name=?" , ids);
        if(c.moveToFirst()) {
            do {

                Object[] a = new Object[7];
                a[0] = c.getString(0);
                a[1] = c.getString(1);
                a[2] = c.getString(2);
                a[3] = c.getString(3);
                a[4] = c.getString(4);
                a[5] = c.getString(5);
                a[6] = c.getInt(6);
                array.add(a);
            } while(c.moveToNext());
        }
        return array;
    }
}
