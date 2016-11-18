package dam.isi.frsf.utn.edu.ar.lab05;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by martdominguez on 20/10/2016.
 */
public class RestClient {

    private final String IP_SERVER = "10.0.2.2";
    private final String PORT_SERVER = "4000";
    private final String TAG_LOG = "LAB06";
    public JSONObject getById(Integer id,String path) {
        JSONObject resultado = null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path+"/"+id);
            Log.d("TAG_LOG",url.getPath()+ " --> "+url.toString());
            urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            Log.d("TAG_LOG",url.getPath()+ " --> "+sb.toString());
            resultado = new JSONObject(sb.toString());
        }
        catch (IOException e) {
            Log.e("TEST-ARR",e.getMessage(),e);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!=null) urlConnection.disconnect();
        }
        return resultado;
    }

    public JSONArray getByAll(Integer id,String path) {
        JSONArray resultado = null;
        return resultado;
    }

    public void crear(JSONObject objeto,String path) {
        HttpURLConnection urlConnection=null;
        try {
            String str = objeto.toString();
            byte[] data=str.getBytes();

            URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setFixedLengthStreamingMode(data.length);
            urlConnection.setRequestProperty("Content-Type","application/json");

            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
            printout.write(data);
            printout.flush();
            printout.close();
            Log.d("TEST-ARR","FIN!!! "+urlConnection.getResponseMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
    }
    public void actualizar(JSONObject objeto,String path) {

    }

    public void borrar(Integer id,String path) {

    }
}
