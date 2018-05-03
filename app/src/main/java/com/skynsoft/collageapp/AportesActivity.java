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

import Servidor.PeticionMaterias;

public class AportesActivity extends AppCompatActivity {


    String cedula_est="",nombre_est="";
    String[] listadoCodigosMaterias;
    Spinner spnMaterias,spnQuimestre;
    String codigoMateriaSelecionado="";
    String quimestreSeleccionado="";
    WebView webView;
    TextView txtCurso;
    String curso="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aportes);

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

        spnMaterias=(Spinner) findViewById(R.id.spn_materias_aportes);
        spnQuimestre=(Spinner) findViewById(R.id.spn_quimestres);
        txtCurso=(TextView) findViewById(R.id.txt_curso_aportes);
        webView=(WebView) findViewById(R.id.web_aportes);

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


    public void consultarAsistencias(View view)
    {



        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        if (Build.VERSION.SDK_INT < 18) {
            webView.clearView();
        } else {
            webView.loadUrl("about:blank");
        }



        quimestreSeleccionado=spnQuimestre.getSelectedItem().toString();


        if(!codigoMateriaSelecionado.equals("") ) {
                    Toast.makeText(getApplicationContext(),"Consultando aportes...",Toast.LENGTH_SHORT).show();
                    String url= Servidor.Servidor.getUrl()+"aportes/detalleMovilPorEstudiante/"+codigoMateriaSelecionado+"/"+cedula_est+"/"+quimestreSeleccionado;
                   /* Toast.makeText(getApplicationContext(),"->"+url,Toast.LENGTH_SHORT).show();
                    Log.i("MI URL:",url);*/

                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(url);
                    //gvResultado.setAdapter(new GridAdapter(items));
        }else{
            Toast.makeText(getApplicationContext(), "Seleccione la materia", Toast.LENGTH_LONG).show();
        }

    }

}
