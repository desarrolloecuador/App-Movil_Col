package com.skynsoft.collageapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import Servidor.NotificacionRecivida;

public class DetalleComunicadoActivity extends AppCompatActivity {

    String materia="Materia",fecha="Fecha",mensaje="Mensaje";
    NotificacionRecivida miNotificacion=NotificacionRecivida.getNotificacionRecivida();

    TextView txtFecha,txtMateria;
    WebView webMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_comunicado);

        materia=miNotificacion.materia;
        fecha=miNotificacion.fecha;
        mensaje=miNotificacion.mensaje;

        //txtFecha=(TextView)findViewById(R.id.txt_fecha_detalle);
        //txtMateria=(TextView)findViewById(R.id.txt_materia_detalle);
        //webMensaje=(WebView) findViewById(R.id.web_mensaje_detalle);

        //txtFecha.setText(fecha);
        //txtMateria.setText(materia);
        //webMensaje.loadData("<p style='text-align:justify'>"+mensaje+"</p>","text/html; charset=utf-8", "utf-8");


    }
}
