package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {

    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";

    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    public Cursor listaTareas(Integer idProyecto){
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }

    public void nuevaTarea(Tarea t){
        /**Creamos un Content Values que utilizaremos para añadir la tarea a la base de datos*/
        ContentValues nuevaTarea = new ContentValues();
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata._ID,t.getId());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,t.getProyecto().getId());
        nuevaTarea.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,t.getFinalizada());
        /**Añadimos la tarea a la base de datos*/
        try {
            db.insert(ProyectoDBMetadata.TABLA_TAREAS, null, nuevaTarea);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public void actualizarTarea(Tarea t){

    }

    public void borrarTarea(Tarea t){

    }

    public List<Prioridad> listarPrioridades(){
        String[] campos = new String[] {ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD};
        Cursor c = db.query(ProyectoDBMetadata.TABLA_PRIORIDAD, campos, null, null, null, null, null);
        Prioridad prioridad;
        int prioridadID = 1;
        List<Prioridad> lista = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                prioridad = new Prioridad(prioridadID,c.getString(0));
                lista.add(prioridad);
                prioridadID++;
            }while (c.moveToNext());
        }
        return lista;
    }

    public List<Usuario> listarUsuarios(){
        List<Usuario> listaUsuarios = new ArrayList<>();
        Usuario usuario;
        String[] campos = new String[] {ProyectoDBMetadata.TablaUsuariosMetadata._ID,ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO,ProyectoDBMetadata.TablaUsuariosMetadata.MAIL};
        Cursor c = db.query(ProyectoDBMetadata.TABLA_USUARIOS, campos, null, null, null, null, null);
        if (c.moveToFirst()){
            do {
                usuario = new Usuario(c.getInt(0),c.getString(1),c.getString(2));
                listaUsuarios.add(usuario);
            }while (c.moveToNext());
        }
        return listaUsuarios;
    }

    public void finalizar(Integer idTarea){
        /**Establecemos los campos-valores a actualizar*/
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        /** retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
         * que el tiempo planificado.
         * si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
         */
        return null;
    }

    public long updateTiempoFinTarea(int id, long inicio){
        try {
            long auxtime;
            /**Definimos la sentencia where para guardar los Minutos Trabajados**/
            String where = ProyectoDBMetadata.TablaTareasMetadata._ID + "=" + String.valueOf(id);
            /**Recuperamos el valor almacenado de Minutos Trabajados**/
            String[] campos = new String[] {ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS};
            String[] args = new String[] {String.valueOf(id)};
            Cursor c = db.query(ProyectoDBMetadata.TABLA_TAREAS,campos,ProyectoDBMetadata.TablaTareasMetadata._ID + "= ?",args,null,null,null);
            int auxTiempoTrabajado = 0;
            if (c.moveToFirst()){
                auxTiempoTrabajado = c.getInt(0);
            }
            /**Calculamos el valor y lo almacenamos**/
            ContentValues newTime = new ContentValues();
            auxtime = (((System.currentTimeMillis() - inicio) / 1000)/5) + auxTiempoTrabajado;
            newTime.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,auxtime);
            return db.update(ProyectoDBMetadata.TABLA_TAREAS,newTime,where,null);
        }catch (SQLiteException ex){
            return -1;
        }
    }

    public int getLastTareaId(){
        String[] campos = new String[] {ProyectoDBMetadata.TablaTareasMetadata._ID};
        Cursor c = db.query(ProyectoDBMetadata.TABLA_TAREAS, campos, null, null, null, null, null);
        if (c.moveToLast())
            return c.getInt(0);
        return -1;
    }

    public Cursor getCursorUsuarios(){
        Cursor c = db.query(
                ProyectoDBMetadata.TABLA_USUARIOS,
                new String[]{"rowid _id", /**Se hace la busqueda de esta forma debido a que SimpleCursorAdapter espera la columna clave como _id y en la tabla esta definida como _ID**/
                ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO,
                ProyectoDBMetadata.TablaUsuariosMetadata.MAIL},
                null,
                null,
                null,
                null,
                null);
        return c;
    }



}
