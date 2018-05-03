package com.skynsoft.collageapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Servidor.PeticionDisciplinas;
import Servidor.PeticionMaterias;

public class DisciplinaActivity extends AppCompatActivity {

    String cedula_est="",nombre_est="";
    String[] listadoCodigosMaterias;
    Spinner spnMaterias;
    String codigoMateriaSelecionado="";
    TextView txtCurso;
    String curso="";

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplina);

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

        //Toast.makeText(getApplicationContext(),"CEDULA: "+cedula_est+" - NOMBRE: "+nombre_est,Toast.LENGTH_SHORT).show();

        spnMaterias=(Spinner) findViewById(R.id.spn_materias_disciplinas);
        txtCurso=(TextView) findViewById(R.id.txt_curso_disciplina);
        webView=(WebView) findViewById(R.id.web_disciplinas);


        consultarMaterias();



        spnMaterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                codigoMateriaSelecionado=listadoCodigosMaterias[i];
                // Toast.makeText(getApplicationContext(),"->"+cedulaSeleccionada,Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


    }


    public void consultarMaterias(){


        List<String> values = new ArrayList<String>();

        PeticionMaterias peticionMaterias=new PeticionMaterias();

        String respuesta=peticionMaterias.enviarDatos(cedula_est);

        if(!respuesta.equals("false")) {

            try {
                JSONArray listadoMaterias=new JSONArray(respuesta);


                listadoCodigosMaterias=new String[listadoMaterias.length()];

                for(int i=0;i<listadoMaterias.length();i++){
                    JSONObject materiasAux= new JSONObject(listadoMaterias.get(i).toString());
                    listadoCodigosMaterias[i]=materiasAux.getString("codigo_mat");
                    values.add(materiasAux.getString("nombre_mat"));
                    curso=materiasAux.getString("nombre_cur")+" \""+materiasAux.getString("paralelo_cur")+" \" | "+materiasAux.getString("seccion_cur");
                }


                txtCurso.setText(curso);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_list, values);
                dataAdapter.setDropDownViewResource(R.layout.spinner_list);
                spnMaterias.setAdapter(dataAdapter);





            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"No se encontraron materias",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"No se encontraron materias",Toast.LENGTH_SHORT).show();
        }

    }


    public void consultarDisciplinas(View view){

        String resultado = "";

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        if (Build.VERSION.SDK_INT < 18) {
            webView.clearView();
        } else {
            webView.loadUrl("about:blank");
        }

        PeticionDisciplinas peticionDisciplinas=new PeticionDisciplinas();


            String respuesta = peticionDisciplinas.enviarDatos(codigoMateriaSelecionado,cedula_est);

            if (!respuesta.equals("false")) {

                try {
                    Toast.makeText(getApplicationContext(),"Consultando disciplina...",Toast.LENGTH_LONG).show();
                    JSONArray listadoDisciplina = new JSONArray(respuesta); 

                    resultado = "<html><body> ";

                    resultado += "<table border='1' cellpadding='10' style='border-collapse:collapse;'>";
                    resultado += "<tr><th style='width: 15%; background-color:#D6D7D7;'><center>No.</center></th><th style='width: 60%; background-color:#D6D7D7;'><center>Fecha</center></th> <th style='width: 60%; background-color:#D6D7D7;'><center>Observación</center></th> <th style='width: 25%; background-color:#D6D7D7;'><center>Calificación</center></th></tr>";


                    for (int i = 0; i < listadoDisciplina.length(); i++) {
                        JSONObject disciplina_aux = new JSONObject(listadoDisciplina.get(i).toString());
                        int numeroAux = i + 1;



                        String calificacion="0";


                        calificacion = disciplina_aux.getString("calificacion_cd");


                        if(calificacion.equals("D")){
                            resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + disciplina_aux.getString("fecha_dis")  + "</center></td><td><center>" + disciplina_aux.getString("observacion_cd")  + "</center></td><td style='background-color:#d8d8d8; color:black;'><center><b>N/A</b></center></td></tr>";
                        }

                        if(calificacion.equals("C")){
                            resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + disciplina_aux.getString("fecha_dis")  + "</center></td><td><center>" + disciplina_aux.getString("observacion_cd")  + "</center></td><td style='background-color:#FF2D55; color:black;'><center><b>"+disciplina_aux.getString("calificacion_cd")+"</b></center></td></tr>";
                        }



                        if(calificacion.equals("B")){
                            resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + disciplina_aux.getString("fecha_dis")  + "</center></td><td><center>" + disciplina_aux.getString("observacion_cd")  + "</center></td><td style='background-color:#FFCC00; color:black;'><center><b>"+disciplina_aux.getString("calificacion_cd")+"</b></center></td></tr>";
                        }


                        if(calificacion.equals("A")){
                            resultado += "<tr style='font-size:14px;'><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + disciplina_aux.getString("fecha_dis")  + "</center></td><td><center>" + disciplina_aux.getString("observacion_cd")  + "</center></td><td style='background-color:#4CD964; color:black;'><center><b>"+disciplina_aux.getString("calificacion_cd")+"</b></center></td></tr>";
                        }


                    }


                    resultado += "</table></body></html>";
                    webView.loadData(resultado, "text/html; charset=utf-8", "utf-8");

                    //gvResultado.setAdapter(new GridAdapter(items));


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se encontraron registros de disciplina.", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron asistencias", Toast.LENGTH_SHORT).show();
            }

    }
}
