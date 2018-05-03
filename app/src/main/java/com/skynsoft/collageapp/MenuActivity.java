package com.skynsoft.collageapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Servidor.BaseDatos;
import Servidor.PeticionEstudiantes;
import Servidor.RepresentanteConectado;

public class MenuActivity extends AppCompatActivity {


    private SQLiteDatabase miBdd;


    RepresentanteConectado representante= RepresentanteConectado.getObjetoRepresentante();
    String[] listadoCedulas;
    String[] listadoNombres;
    Spinner spnEstudiante;
    String  cedulaSeleccionada="",nombreSeleccionado="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        crearBaseDatos();

        /*Toast.makeText(getApplicationContext(),"CODIGO_USU: "+representante.codigo_usu+" - NOMBRE: "
                +representante.nombre+" - USERNAME: "+representante.username,Toast.LENGTH_LONG).show();*/

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        spnEstudiante=(Spinner) findViewById(R.id.spn_estudiante_menu);



        consultarEstudiantes();

        spnEstudiante.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(i!=0){
                cedulaSeleccionada=listadoCedulas[i];
                nombreSeleccionado=listadoNombres[i];
            }

               // Toast.makeText(getApplicationContext(),"->"+cedulaSeleccionada,Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });



        try {
          servicio();

        }catch(Exception ex){

        }




    }


    public void servicio() {

        try{
            Intent intent = new Intent(getApplicationContext(), NotificacionReciver.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificacionReciver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long firstMillis = System.currentTimeMillis(); //first run of alarm is immediate // aranca la palicacion
            int intervalMillis = 1 * 1 * 1000; //3
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
            //Toast.makeText(getApplicationContext(),"INICIANDO SERVICIO",Toast.LENGTH_SHORT).show();

        }catch(Exception ex){

        }
    }



    public void consultarEstudiantes(){


        List<String> values = new ArrayList<String>();

        PeticionEstudiantes peticionEstudiantes=new PeticionEstudiantes();

        String respuesta=peticionEstudiantes.enviarDatos(this.consultarCodigoRepresentante());

        if(!respuesta.equals("false")) {

            try {
                JSONArray listadoRepresentados=new JSONArray(respuesta);


                listadoCedulas=new String[listadoRepresentados.length()];
                listadoNombres=new String[listadoRepresentados.length()];

                for(int i=0;i<listadoRepresentados.length();i++){

                    JSONObject estudianteAux= new JSONObject(listadoRepresentados.get(i).toString());

                    listadoCedulas[i]=estudianteAux.getString("cedula_est");
                    listadoNombres[i]=estudianteAux.getString("nombre_est");
                    values.add(estudianteAux.getString("nombre_est"));
                }


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_list, values);
                dataAdapter.setDropDownViewResource(R.layout.spinner_list);
                spnEstudiante.setAdapter(dataAdapter);





            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"No se encontraron estudiantes representados por su persona",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"No se encontraron estudiantes representados por su persona",Toast.LENGTH_SHORT).show();
        }

    }

    public void eliminarBaseDatos(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert)
                    .setTitle("CONFIRMACIÓN")
                    .setMessage("¿Está seguro/a que desea salir del sistema?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            try{

                                miBdd=openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), MODE_WORLD_WRITEABLE,null);//
                                miBdd.execSQL("drop table representante");
                                Log.i("confirmacion","CONFIRMACION; BASE DE DATOS ELIMINADA EXITOSAMENTE");
                                Toast.makeText(getApplicationContext(),"Sesión Cerrada Exitosamente",Toast.LENGTH_LONG).show();
                                finish();

                            }catch (Exception e){
                                Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
                            }




                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("CONFIRMACIÓN")
                    .setMessage("¿Está seguro/a que desea salir del sistema?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {


                            try{

                                miBdd=openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), MODE_WORLD_WRITEABLE,null);//
                                miBdd.execSQL("drop table representante");
                                Log.i("confirmacion","CONFIRMACION; BASE DE DATOS ELIMINADA EXITOSAMENTE");
                                Toast.makeText(getApplicationContext(),"Sesión Cerrada Exitosamente",Toast.LENGTH_LONG).show();
                                finish();

                            }catch (Exception e){
                                Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
                            }




                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }


    }


    public void abrirAsistencia(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Intent asistencia=new Intent(getApplicationContext(),AsistenciaActivity.class);
        asistencia.putExtra("cedula_est",cedulaSeleccionada);
        asistencia.putExtra("nombre_est",nombreSeleccionado);
        startActivity(asistencia);
    }


    public void abrirAportes(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Intent aportes=new Intent(getApplicationContext(),AportesActivity.class);
        aportes.putExtra("cedula_est",cedulaSeleccionada);
        aportes.putExtra("nombre_est",nombreSeleccionado);
        startActivity(aportes);
    }


    public void abrirComunicado(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Intent comunicado=new Intent(getApplicationContext(),ComunicadoActivity.class);
        comunicado.putExtra("cedula_est",cedulaSeleccionada);
        comunicado.putExtra("nombre_est",nombreSeleccionado);
        startActivity(comunicado);
    }


    public void abrirDisciplina(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Intent disciplina=new Intent(getApplicationContext(),DisciplinaActivity.class);
        disciplina.putExtra("cedula_est",cedulaSeleccionada);
        disciplina.putExtra("nombre_est",nombreSeleccionado);
        startActivity(disciplina);
    }


    public void abrirPassword(View view){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Intent disciplina=new Intent(getApplicationContext(),PasswordActivity.class);
        startActivity(disciplina);
    }


    public String consultarCodigoRepresentante(){

        String codigo_usu="0";
        Cursor miCursor = miBdd.rawQuery("select * from representante",null);

        if(miCursor.moveToFirst()){

            do{
                codigo_usu=miCursor.getString(1);
            } while(miCursor.moveToNext());


        }else{
            Toast.makeText(getApplicationContext(),"No hay datos",Toast.LENGTH_SHORT).show();
        }

        return codigo_usu;


    }


    public void crearBaseDatos(){
        try{
            miBdd=openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), MODE_WORLD_WRITEABLE,null);//
            Log.i("confirmacion","CONFIRMACION; BASE DE DATOS CREADA EXITOSAMENTE");
        }catch (Exception e){
            Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
        }
    }


}
