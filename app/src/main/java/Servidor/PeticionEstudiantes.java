package Servidor;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PeticionEstudiantes extends AsyncTask<String, String, String> {
    @Override
    protected  String doInBackground(String ...arg0) {return "ok";}

    public String enviarDatos(String codigo_usu)
    {
        String respuesta="vacio";
        try{
            Response response=null;
            OkHttpClient client= new OkHttpClient();
            RequestBody body= new FormBody.Builder()
                    .add("codigo_usu",codigo_usu)
                    .build();
            Request request= new Request.Builder()
                    .url(Servidor.getUrl()+"representantes/obtenerEstudiantesRepresentadosMovil")
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
