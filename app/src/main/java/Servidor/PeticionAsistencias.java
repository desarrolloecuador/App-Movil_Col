package Servidor;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PeticionAsistencias extends AsyncTask<String, String, String> {
    @Override
    protected  String doInBackground(String ...arg0) {return "ok";}

    public String enviarDatos(String cedula_est,String codigo_mat,String fecha_inicial,String fecha_final)
    {
        String respuesta="vacio";
        try{
            Response response=null;
            OkHttpClient client= new OkHttpClient();
            RequestBody body= new FormBody.Builder()
                    .add("cedula_est",cedula_est)
                    .add("codigo_mat",codigo_mat)
                    .add("fecha_inicial",fecha_inicial)
                    .add("fecha_final",fecha_final)
                    .build();
            Request request= new Request.Builder()
                    .url(Servidor.getUrl()+"estudiantes/obtenerAsistenciaPorEstudianteMateriaMovil")
                    .post(body)
                    .build();

            try
            {
                response= client.newCall(request).execute();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            respuesta=response.body().string();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return respuesta;


    }
}
