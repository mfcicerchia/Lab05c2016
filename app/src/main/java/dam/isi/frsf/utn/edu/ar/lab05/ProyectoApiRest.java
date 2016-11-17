package dam.isi.frsf.utn.edu.ar.lab05;

import org.json.JSONObject;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;

/**
 * Created by martdominguez on 20/10/2016.
 */
public class ProyectoApiRest {

    public void crearProyecto(Proyecto p){

    }
    public void borrarProyecto(Integer id){

    }
    public void actualizarProyecto(Proyecto p){

    }
    public List<Proyecto> listarProyectos(){
        return null;
    }

    public Proyecto buscarProyecto(Integer id){
        RestClient cliRest = new RestClient();
        JSONObject t = cliRest.getById(1,"proyectos");
        // transformar el objeto JSON a proyecto y retornarlo
        return null;
    }

}
