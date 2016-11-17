package dam.isi.frsf.utn.edu.ar.lab05;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class SearchActivity extends AppCompatActivity {
    private EditText etMinutos;
    private ToggleButton tbTareaTerminada;
    private Button buscar;
    private ProyectoDAO proyectoDAO;
    private Cursor cursor;
    private ArrayList<Tarea> listaTareas = new ArrayList<>();
    private TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etMinutos = (EditText)findViewById(R.id.etMinutosDesviados);
        tbTareaTerminada = (ToggleButton)findViewById(R.id.tbTareaTerminada);
        resultado = (TextView)findViewById(R.id.tvResultado);
        buscar = (Button)findViewById(R.id.btnBusqueda);


        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etMinutos.getText().toString().isEmpty()) {
                    listaTareas = proyectoDAO.listarDesviosPlanificacion(tbTareaTerminada.isChecked(),Integer.parseInt(etMinutos.getText().toString()));
                    String res = "";
                    int desvio = 0;
                    for (Tarea aux:listaTareas){
                        desvio = aux.getMinutosTrabajados() - (aux.getHorasEstimadas() * 60);
                        res=res.concat(aux.getDescripcion()).concat(" - Desvio: ").concat(String.valueOf(desvio)).concat("\n");
                    }
                    resultado.setText(res);
                    resultado.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        proyectoDAO = new ProyectoDAO(SearchActivity.this);
        proyectoDAO.open();
        cursor = proyectoDAO.listaTareas(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();
    }
}
