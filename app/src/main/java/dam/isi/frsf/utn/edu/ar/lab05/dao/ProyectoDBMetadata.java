package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.provider.BaseColumns;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDBMetadata {

    public static final int VERSION_DB = 1;
    public static final String NOMBRE_DB= "lab05.db";
    public static final String TABLA_PROYECTO= "PROYECTO";
    public static final String TABLA_PROYECTO_ALIAS= " pry";
    public static final String TABLA_USUARIOS= "USUARIOS";
    public static final String TABLA_USUARIOS_ALIAS= " usr";
    public static final String TABLA_TAREAS= "TAREA";
    public static final String TABLA_TAREAS_ALIAS= " tar";
    public static final String TABLA_PRIORIDAD= "PRIORIDAD";
    public static final String TABLA_PRIORIDAD_ALIAS= " pri";

    public static class TablaProyectoMetadata implements BaseColumns{
        public static final String TITULO ="TITULO";
        public static final String TIT_PROY_ALIAS ="TITULO_PROYECTO";
    }

    public static class TablaUsuariosMetadata implements BaseColumns{
        public static final String USUARIO ="NOMBRE";
        public static final String USUARIO_ALIAS ="NOMBRE_USUARIO";
        public static final String MAIL ="CORREO_ELECTRONICO";
    }

    public static class TablaTareasMetadata implements BaseColumns{
        public static final String TAREA  ="DESCRIPCION";
        public static final String HORAS_PLANIFICADAS ="HORAS_PLANIFICADAS";
        public static final String MINUTOS_TRABAJADOS ="MINUTOS_TRABAJDOS";
        public static final String PRIORIDAD="ID_PRIORIDAD";
        public static final String RESPONSABLE ="ID_RESPONSABLE";
        public static final String PROYECTO ="ID_PROYECTO";
        public static final String FINALIZADA ="FINALIZADA";
    }

    public static class TablaPrioridadMetadata implements BaseColumns{
        public static final String PRIORIDAD ="TITULO";
        public static final String PRIORIDAD_ALIAS ="NOMBRE_PRIORIDAD";
    }

}
