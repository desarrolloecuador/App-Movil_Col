package com.skynsoft.collageapp;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Servidor.PeticionComunicados;

public class ComunicadoActivity extends AppCompatActivity {

    String cedula_est="",nombre_est="";

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunicado);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("cedula_est")!= null && bundle.getString("nombre_est")!= null)
        {
            cedula_est=bundle.getString("cedula_est");
            nombre_est=bundle.getString("nombre_est");
        }


        webView=(WebView) findViewById(R.id.web_comunicados);


        consultarComunicados();


    }



    public void consultarComunicados(){


        String resultado = "";


        if (Build.VERSION.SDK_INT < 18) {
            webView.clearView();
        } else {
            webView.loadUrl("about:blank");
        }


        PeticionComunicados peticionComunicados=new PeticionComunicados();


            String respuesta = peticionComunicados.enviarDatos(cedula_est);

            if (!respuesta.equals("false")) {

                try {
                    //Toast.makeText(getApplicationContext(),"Consultando asistencias...",Toast.LENGTH_LONG).show();
                    JSONArray listadoComunicados = new JSONArray(respuesta);

                    resultado = "<html><body> ";

                    resultado += "<table border='1' cellpadding='1' style='border-collapse:collapse;'>";
                    resultado += "<tr style='font-size:14px;'><th style='width: 5%; background-color:#D6D7D7;'><center>No.</center></th><th style='width: 20%; background-color:#D6D7D7;'><center>Materia</center></th><th style='width: 50%; background-color:#D6D7D7;'><center>Mensaje</center></th><th style='width: 50%; background-color:#D6D7D7;'><center>Estado</center></th></tr>";



                    for (int i = 0; i < listadoComunicados.length(); i++) {
                        JSONObject comunicadoAux = new JSONObject(listadoComunicados.get(i).toString());
                        int numeroAux = i + 1;

                        Date fechaAux = calcularFecha(comunicadoAux.getString("fecha_com"));
                        Date hoy = new Date();

                        if (fechaAux.before(hoy)) {
                            int dias = (int) ((hoy.getTime() - fechaAux.getTime()) / 86400000);
                            if (dias == 0) {
                                resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + comunicadoAux.getString("nombre_mat") + "</center></td><td><p style='text-align:justify;'>" + comunicadoAux.getString("mensaje_com") + "<p></td><td style='background-color:#FFCC00;'><center>Hoy</center></td></tr>";
                            } else {
                                resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + comunicadoAux.getString("nombre_mat") + "</center></td><td><p style='text-align:justify;'>" + comunicadoAux.getString("mensaje_com") + "</p></td><td style='background-color:#FF2D55;'><center>-" + dias + "d</center></td></tr>";
                            }
                        } else {
                            int dias = (int) ((fechaAux.getTime() - hoy.getTime()) / 86400000);
                            dias+=1;
                            if (dias<=3) {
                                resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + comunicadoAux.getString("nombre_mat") + "</center></td><td><p style='text-align:justify;'>" + comunicadoAux.getString("mensaje_com") + "</p></td><td style='background-color:#FFCC00;'><center> +" + dias + "d</center></td></tr>";
                            }else{
                                resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + comunicadoAux.getString("nombre_mat") + "</center></td><td><p style='text-align:justify;'>" + comunicadoAux.getString("mensaje_com") + "</p></td><td style='background-color:#4CD964;'><center> +" + dias + "d</center></td></tr>";
                            }
                        }





                        //Toast.makeText(getApplicationContext(),"DIAS: "+dias,Toast.LENGTH_LONG).show();



                    }


                    resultado += "</table></body></html>";
                    webView.loadData(resultado, "text/html; charset=utf-8", "utf-8");



                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se encontraron comunicados", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron comunicados", Toast.LENGTH_SHORT).show();
            }


    }


    public Date calcularFecha(String fecha){


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


        try {

            Date date = formatter.parse(fecha);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;

    }


}
