package dam.isi.frsf.utn.edu.ar.lab05;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity {
    private ProyectoDAO myDao;
    int idTarea;
    EditText descripcion, horasEstimadas;
    SeekBar prioridad;
    Spinner responsable;
    Button btnGuardar, btnCancelar;
    List<Prioridad> listaPrioridad;
    List<Usuario> listaUsuario;
    Proyecto proyecto;
    Integer userID, minutosTrabajados;
    Boolean esEdicion, tareaFinalizada;
    Usuario usuario;
    int id;
    RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        myDao = new ProyectoDAO(AltaTareaActivity.this);
        myDao.open();
        idTarea = getIntent().getExtras().getInt("ID_TAREA");

        restClient = new RestClient();

        descripcion = (EditText)findViewById(R.id.etDescripcion);
        horasEstimadas = (EditText)findViewById(R.id.etHorasEstimadas);
        prioridad = (SeekBar)findViewById(R.id.sbPrioridad);

        /**Seteamos el Spinner**/
        /**Lab6**/
        /**Obtenemos la lista de usuario que tenemos en la base de dato local**/
        List<Usuario> listaSpinner = myDao.listarUsuarios();
        id = listaSpinner.get(listaSpinner.size()-1).getId();
        /**Consulta**/
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver cr = getContentResolver();
        /**Ordenamiento**/
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIXED ASC";
        Cursor cContactos = cr.query(uri, null, null, null, null);

        if (cContactos.moveToFirst()){
            do {
                Log.d("ID--->",cContactos.getString(cContactos.getColumnIndex(ContactsContract.Contacts._ID)));
                Log.d("Nombre--->",cContactos.getString(cContactos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                String cId = cContactos.getString(cContactos.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cContactos.getString(cContactos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String email="";
                Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{cId},null);
                while (emailCur.moveToNext()) {
                    email = emailCur.getString( emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.d("Email-->",email);
                }
                /**Diremos que si el id es cero entonces no esta en la base de datos local**/
                Usuario usuario = new Usuario(0,name,email);
                Boolean existe = Boolean.FALSE;
                for (int i=0; i<listaSpinner.size();i++){
                    if (listaSpinner.get(i).getNombre().equals(usuario.getNombre())){
                        existe=Boolean.TRUE;
                        break;
                    }
                }
                if (!existe){listaSpinner.add(usuario);}
            }while (cContactos.moveToNext());
        }
        responsable = (Spinner)findViewById(R.id.spnrResponsable);
        ArrayAdapter<Usuario> spnrAdapter = new ArrayAdapter<Usuario>(this, android.R.layout.simple_spinner_item,listaSpinner);
        spnrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        responsable.setAdapter(spnrAdapter);
        responsable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long idSpnr) {
                usuario = (Usuario) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**********************/

        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnCancelar = (Button)findViewById(R.id.btnCanelar);
        listaPrioridad = myDao.listarPrioridades();
        listaUsuario = myDao.listarUsuarios();
        proyecto = new Proyecto(1,"TP Integrador");

        /**Verificamos si es una edición en lugar de una tarea nueva**/
        esEdicion = getIntent().getExtras().getBoolean("esEdicion");
        if (esEdicion){
            Cursor c = myDao.listaTareas(1);
            if (c.moveToFirst()) {
                do {
                    if (c.getInt(0)==idTarea){
                        descripcion.setText(c.getString(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
                        horasEstimadas.setText(String.valueOf(c.getInt(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS))));
                        prioridad.setProgress(c.getInt(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD)));
                        responsable.setSelection(c.getInt(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE))-1);
                        if (c.getInt(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==0){
                            tareaFinalizada = Boolean.FALSE;
                        }
                        else {
                            tareaFinalizada = Boolean.TRUE;
                        }
                        minutosTrabajados = c.getInt(c.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
                        break;
                    }
                }while (c.moveToNext());
            }
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Nos fijamos si el usuario esta en la lista de contactos, de lo contrario lo agregamos**/
                if (!existeContacto(usuario.getNombre())){
                    nuevoContacto(usuario);
                }
                if (prioridad.getProgress()>0) {
                    if (usuario.getId()==0){
                        /**Lo añadimos a la base local**/
                        usuario.setId(id+1);
                        myDao.nuevoUsuario(usuario);
                        /**Lo añadimos a la base remota**/
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject nuevoObjeto = new JSONObject();
                                    nuevoObjeto.put("id",String.valueOf(usuario.getId()));
                                    nuevoObjeto.put("nombre",usuario.getNombre());
                                    nuevoObjeto.put("correoElectronico",usuario.getCorreoElectronico());
                                    restClient.crear(nuevoObjeto,"usuarios");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                    if (esEdicion){
                        Tarea tarea = new Tarea(
                                idTarea,
                                Integer.parseInt(horasEstimadas.getText().toString()),
                                minutosTrabajados,
                                tareaFinalizada,
                                proyecto,
                                listaPrioridad.get(prioridad.getProgress()-1),
                                usuario
                        );
                        tarea.setDescripcion(descripcion.getText().toString());

                        myDao.actualizarTarea(tarea);
                    }
                    else {
                        Tarea tarea = new Tarea(
                                idTarea,
                                Integer.parseInt(horasEstimadas.getText().toString()),
                                0,
                                false,
                                proyecto,
                                listaPrioridad.get(prioridad.getProgress()-1),
                                usuario
                        );
                        tarea.setDescripcion(descripcion.getText().toString());

                        myDao.nuevaTarea(tarea);
                    }
                    finish();
                } else {
                    Toast.makeText(AltaTareaActivity.this, "La prioridad debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean existeContacto(String nombreBuscado) {
        boolean existe = Boolean.FALSE;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor c = this.getContentResolver().query(uri, null, ContactsContract.Contacts.DISPLAY_NAME + " LIKE '" + nombreBuscado + "%'", null, sortOrder);

        if (c.moveToFirst()){
            do {
                if (c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(nombreBuscado)) {
                    existe = Boolean.TRUE;
                }
            }while (c.moveToNext());
        }

        return existe;
    }

    public void nuevoContacto(Usuario u){
        String accountType =null;
        String accountName =null;
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, accountName);
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, u.getNombre());
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, u.getCorreoElectronico());
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }
}
