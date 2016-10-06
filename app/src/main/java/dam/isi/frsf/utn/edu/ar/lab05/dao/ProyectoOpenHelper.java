package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoOpenHelper extends SQLiteOpenHelper {

    private Context context;

    public ProyectoOpenHelper(Context context) {
        super(context, ProyectoDBMetadata.NOMBRE_DB, null, ProyectoDBMetadata.VERSION_DB);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            ArrayList<String> tablas = this.leerTablas();
            for (String sql : tablas) {
                db.execSQL(sql);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<String> leerTablas() throws IOException {
        InputStream is = context.getAssets().open("estructura-db.sql");
        InputStreamReader r = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(r);
        String strLine;
        ArrayList<String> res = new ArrayList<String>();
        while ((strLine = br.readLine()) != null) {
            Log.d("SQL", strLine);
            res.add(strLine);
        }
        br.close();
        r.close();
        is.close();
        return res;
    }
}
