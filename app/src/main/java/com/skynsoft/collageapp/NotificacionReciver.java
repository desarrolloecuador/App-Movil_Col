package com.skynsoft.collageapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Random;

import Servidor.BaseDatos;
import Servidor.NotificacionRecivida;
import Servidor.PeticionNotificaciones;


public class NotificacionReciver extends BroadcastReceiver {


    public static int REQUEST_CODE=1000;
    private NotificationManager notificationManager;
    private  int NOTIFICATION_ID;

    String t="--";
    String fecha="Comunicado";

    private SQLiteDatabase miBdd;



    @Override
    public void onReceive(Context context, Intent intent) {






        Log.i("NOTIFICACION","MI NOTIFICACION");

        crearBaseDatos(context);


        try{

            PeticionNotificaciones peticion=new PeticionNotificaciones();
            String respuesta=peticion.enviarDatos(consultarCodigoRepresentante(context));


            JSONObject Jobject = new JSONObject(respuesta);


            t=Jobject.getString("nombre_mat")+"\n";
            t+=Jobject.getString("mensaje_com");
            fecha=Jobject.getString("fecha_com");
            //Activity a abrir
            Intent i = new Intent(context, DetalleComunicadoActivity.class);
            NotificacionRecivida miNotificacion=NotificacionRecivida.getNotificacionRecivida();
            miNotificacion.setAtributos(Jobject.getString("nombre_mat"),Jobject.getString("fecha_com"),Jobject.getString("mensaje_com"));
            context.startService(i);

            triggerNotification(context);

        }catch(Exception ex) {
            //Toast.makeText(Context,"El usuario o contraseña ingresados son incorrectos.",Toast.LENGTH_LONG).show();
        }


    }

    private void triggerNotification(Context contexto) {

        try{

            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            NOTIFICATION_ID=n;

            Intent notificationIntent = new Intent(contexto, DetalleComunicadoActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            long[] pattern = new long[]{100, 1000, 2000};

            NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
            builder.setContentIntent(contentIntent)

                    .setTicker("" )
                    .setContentTitle("U.E.F \"ABC\"")
                    .setContentText(t)
                    .setContentInfo(fecha)
                    .setLargeIcon(BitmapFactory.decodeResource(contexto.getResources(), R.drawable.logo10))
                    .setSmallIcon(R.drawable.logo11)
                    .setAutoCancel(true) //Cuando se pulsa la notificación ésta desaparece
                    .setSound(defaultSound)
                    .setVibrate(pattern);


            Notification notificacion = new NotificationCompat.BigTextStyle(builder)
                    .bigText(t)
                    .setBigContentTitle("U.E.F \"ABC\"")
                    .setSummaryText("Notificaciones")
                    .build();

            notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notificacion);

        }catch(Exception ex){

        }

    }




    public void crearBaseDatos(Context c){
        try{
            miBdd=c.openOrCreateDatabase(BaseDatos.obtenerNombreBdd(), c.MODE_WORLD_WRITEABLE,null);//
            Log.i("confirmacion","CONFIRMACION; BASE DE DATOS CREADA EXITOSAMENTE");
        }catch (Exception e){
            Log.i("error","ERROR; SE ENCONTRO EL SIGUIENTE ERROR: "+e);
        }
    }

    public String consultarCodigoRepresentante(Context c){

        String codigo_usu="0";
        Cursor miCursor = miBdd.rawQuery("select * from representante",null);

        if(miCursor.moveToFirst()){

            do{
                codigo_usu=miCursor.getString(1);
            } while(miCursor.moveToNext());


        }else{
            Toast.makeText(c.getApplicationContext(),"No hay datos",Toast.LENGTH_SHORT).show();
        }

        return codigo_usu;


    }





}




