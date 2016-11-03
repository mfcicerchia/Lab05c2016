package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;

/**
 * Created by mdominguez on 06/10/16.
 */
public class TareaCursorAdapter extends CursorAdapter {
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    private long startTime;
    private Integer taskId;

    public TareaCursorAdapter(Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao = dao;
        this.contexto = contexto;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_tarea, viewGroup, false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        /**obtener la posicion de la fila actual y asignarla a los botones y checkboxes*/
        int pos = cursor.getPosition();

        /** Referencias UI.*/
        TextView nombre = (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado = (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        TextView tiempoTrabajado = (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        TextView prioridad = (TextView) view.findViewById(R.id.tareaPrioridad);
        TextView responsable = (TextView) view.findViewById(R.id.tareaResponsable);
        CheckBox finalizada = (CheckBox) view.findViewById(R.id.tareaFinalizada);

        final Button btnFinalizar = (Button) view.findViewById(R.id.tareaBtnFinalizada);
        final Button btnEditar = (Button) view.findViewById(R.id.tareaBtnEditarDatos);
        final ToggleButton btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);
        final Button btnEliminar = (Button) view.findViewById(R.id.btnEliminar);

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(horasAsigandas * 60 + " minutos");

        Integer minutosAsigandos = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(minutosAsigandos + " minutos");
        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        prioridad.setText(p);
        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS)));
        finalizada.setChecked(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA)) == 1);
        finalizada.setTextIsSelectable(false);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea = (Integer) view.getTag();
                Intent intEditarAct = new Intent(contexto, AltaTareaActivity.class);
                intEditarAct.putExtra("ID_TAREA", idTarea);
                intEditarAct.putExtra("esEdicion",Boolean.TRUE);
                context.startActivity(intEditarAct);

            }
        });

        btnEstado.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer idTarea = (Integer) v.getTag();
                /**Dejar la marca de tiempo para empezar a trabajar.*/
                if (startTime == 0){
                    taskId = idTarea;
                    startTime = System.currentTimeMillis();
                    //tareaId = myDao
                }
                else{
                    if (taskId==idTarea){
                        final long auxTime = startTime;
                        Thread backGroundUpdate = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d("LAB05-MAIN", "finalizar tarea : --- " + idTarea);
                                myDao.updateTiempoFinTarea(idTarea, auxTime);
                                handlerRefresh.sendEmptyMessage(1);
                            }
                        });
                        backGroundUpdate.start();
                        startTime=0;
                    }
                    else{
                        btnEstado.setChecked(Boolean.FALSE);
                    }

                }

            }
        });

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea = (Integer) view.getTag();
                Thread backGroundUpdate = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("LAB05-MAIN", "finalizar tarea : --- " + idTarea);
                        myDao.finalizar(idTarea);
                        handlerRefresh.sendEmptyMessage(1);
                    }
                });
                backGroundUpdate.start();
                btnEstado.setChecked(Boolean.FALSE);
                startTime=0;
            }
        });

        btnEliminar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
        }
    };
}