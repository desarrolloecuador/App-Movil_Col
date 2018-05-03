package com.skynsoft.collageapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import Servidor.BaseDatos;
import Servidor.PeticionPassword;

public class PasswordActivity extends AppCompatActivity {


    EditText txtPasswordActual,txtPasswordNuevo,txtPasswordConfirmada;

    private SQLiteDatabase miBdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        txtPasswordActual=(EditText) findViewById(R.id.txt_password_actual);
        txtPasswordNuevo=(EditText) findViewById(R.id.txt_password_nuevo);
        txtPasswordConfirmada=(EditText) findViewById(R.id.txt_password_confirmada);

        crearBaseDatos();


    }

    public void crearBaseDatos(){
        try{
            miBdd=openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), MODE_WORLD_WRITEABLE,null);//
            Log.i("confirmacion","CONFIRMACION; BASE DE DATOS CREADA EXITOSAMENTE");
        }catch (Exception e){
            Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
        }
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


    public void cambiarPassword(View view){
        String passwordActual=txtPasswordActual.getText().toString();
        String passwordNuevo=txtPasswordNuevo.getText().toString();
        String passwordConfirmada=txtPasswordConfirmada.getText().toString();

        if(!passwordActual.equals("") && !passwordNuevo.equals("") && !passwordConfirmada.equals("") && passwordActual.length()>=6 && passwordConfirmada.length()>=6 && passwordNuevo.length()>=6){

            if(passwordConfirmada.equals(passwordNuevo)){

                PeticionPassword peticion=new PeticionPassword();
                String respuesta=peticion.enviarDatos(this.consultarCodigoRepresentante(),passwordActual,passwordNuevo);

                try{
                    JSONObject Jobject = new JSONObject(respuesta);

                    String miResultado=Jobject.getString("resultado");

                    if(miResultado.equals("ok")){
                        borrarBdd();
                        finish();
                        Toast.makeText(getApplicationContext(),"Contraseña actualizada exitosamente. Inicie sesión con sus nuevas credenciales",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(),"La contraseña actual ingresada no es la correcta.",Toast.LENGTH_LONG).show();
                    }


                }catch(Exception ex) {
                    Toast.makeText(getApplicationContext(),"Error al procesar, intente nuevamente.",Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(),"La nueva contraseña no coincide",Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Complete los campos con al menos 6 caracteres de longitud",Toast.LENGTH_LONG).show();
        }

    }


    public void borrarBdd(){
        try{

            miBdd=openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), MODE_WORLD_WRITEABLE,null);//
            miBdd.execSQL("drop table representante");
            Log.i("confirmacion","CONFIRMACION; BASE DE DATOS ELIMINADA EXITOSAMENTE");
            finish();

        }catch (Exception e){
            Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
        }
    }
}
