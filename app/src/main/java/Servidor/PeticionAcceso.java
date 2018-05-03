package Servidor;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PeticionAcceso extends AsyncTask<String, String, String> {
    @Override
    protected  String doInBackground(String ...arg0) {return "ok";}

    public String enviarDatos(String username,String password)
    {
        String respuesta="vacio";
        try{
            Response response=null;

            OkHttpClient client= new OkHttpClient();

            RequestBody body= new FormBody.Builder()
                    .add("username_usu",username)
                    .add("password_usu",password)
                    .build();

            Request request= new Request.Builder()
                    .url(Servidor.getUrl()+"security/validarDatosMovil")
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
