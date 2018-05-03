package com.skynsoft.collageapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import Servidor.PeticionAsistencias;
import Servidor.PeticionMaterias;

public class AsistenciaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String cedula_est="",nombre_est="";
    String[] listadoCodigosMaterias;
    Spinner spnMaterias;
    String codigoMateriaSelecionado="";
    WebView webView;

    String curso="";
    TextView txtCurso;

    EditText txtDesde,txtHasta;

    GridView gvResultado;




    ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

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

        spnMaterias=(Spinner) findViewById(R.id.spn_materias_asistencia);
        txtCurso=(TextView) findViewById(R.id.txt_curso_asistencia);



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

        txtDesde=(EditText) findViewById(R.id.txt_desde);
        txtHasta=(EditText) findViewById(R.id.txt_hasta);
        gvResultado=(GridView) findViewById(R.id.gv_asistencias);

        gvResultado.setBackgroundColor(Color.WHITE);
        gvResultado.setVerticalSpacing(1);
        gvResultado.setHorizontalSpacing(1);

        webView=(WebView) findViewById(R.id.web_asistencias);




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
        String resultado = "";


        if (Build.VERSION.SDK_INT < 18) {
            webView.clearView();
        } else {
            webView.loadUrl("about:blank");
        }

        final ArrayList<String> items = new ArrayList<String>();

        PeticionAsistencias peticionAsistencias=new PeticionAsistencias();

        String fechaInicial=txtDesde.getText().toString();
        String fechaFinal=txtHasta.getText().toString();

        if(!fechaFinal.equals("") && !fechaInicial.equals("") && !codigoMateriaSelecionado.equals("") ) {


            String respuesta = peticionAsistencias.enviarDatos(cedula_est, codigoMateriaSelecionado, fechaInicial, fechaFinal);

            if (!respuesta.equals("false")) {

                try {
                    Toast.makeText(getApplicationContext(),"Consultando asistencias...",Toast.LENGTH_LONG).show();
                    JSONArray listadoAsistencias = new JSONArray(respuesta);

                    resultado = "<html><body> ";

                    resultado += "<table border='1' cellpadding='10' style='border-collapse:collapse;'>";
                    resultado += "<tr><th style='width: 15%; background-color:#D6D7D7;'><center>No.</center></th><th style='width: 60%; background-color:#D6D7D7;'><center>Fecha y Hora</center></th><th style='width: 25%; background-color:#D6D7D7;'><center>Estado</center></th></tr>";

                    items.add("No. , FECHA Y HORA , ESTADO");

                    for (int i = 0; i < listadoAsistencias.length(); i++) {
                        JSONObject materiasAux = new JSONObject(listadoAsistencias.get(i).toString());
                        int numeroAux = i + 1;

                        if (materiasAux.getString("estado_ra").equals("0")) {
                            resultado += "<tr><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + materiasAux.getString("fecha_asi") + " " + materiasAux.getString("hora_asi") + "</center></td><td style='background-color:#007AFF; color:white;'><center>N/A</center></td></tr>";
                        }

                        if (materiasAux.getString("estado_ra").equals("1")) {
                            resultado += "<tr><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + materiasAux.getString("fecha_asi") + " " + materiasAux.getString("hora_asi") + "</center></td><td style='background-color:#4CD964; color:white;'><center>Normal</center></td></tr>";
                        }


                        if (materiasAux.getString("estado_ra").equals("2")) {
                            resultado += "<tr><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + materiasAux.getString("fecha_asi") + " " + materiasAux.getString("hora_asi") + "</center></td><td style='background-color:#34AADC; color:white;'><center>Atraso</center></td></tr>";
                        }


                        if (materiasAux.getString("estado_ra").equals("3")) {
                            resultado += "<tr><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + materiasAux.getString("fecha_asi") + " " + materiasAux.getString("hora_asi") + "</center></td><td style='background-color:#FFCC00; color:white;'><center>Falta</center></td></tr>";
                        }


                        if (materiasAux.getString("estado_ra").equals("4")) {
                            resultado += "<tr><td><center>" + String.valueOf(numeroAux) + "</center></td><td><center>" + materiasAux.getString("fecha_asi") + " " + materiasAux.getString("hora_asi") + "</center></td><td style='background-color:#FF2D55; color:white;'><center>Fuga</center></td></tr>";
                        }


                    }


                    resultado += "</table></body></html>";
                    webView.loadData(resultado, "text/html; charset=utf-8", "utf-8");

                    //gvResultado.setAdapter(new GridAdapter(items));


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se encontraron asistencias", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron asistencias", Toast.LENGTH_SHORT).show();
            }
        }else{

            Toast.makeText(getApplicationContext(), "Seleccione una materia e indique un rango de fechas válido", Toast.LENGTH_LONG).show();

        }

    }

    public void datePicker(View v){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(),"Fecha");
    }





    private void setDate(final Calendar calendar){
        //final DateFormat dateFormat= DateFormat.getDateInstance("YYYY-mm-dd");
        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
        //Toast.makeText(getApplicationContext(),"-> "+tf.format(calendar.getTime()),Toast.LENGTH_SHORT).show();

        if(txtDesde.isFocused()) {
            txtDesde.setText(tf.format(calendar.getTime()));
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtDesde.getWindowToken(), 0);
        }

        if(txtHasta.isFocused()){
            txtHasta.setText(tf.format(calendar.getTime()));
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtHasta.getWindowToken(), 0);
        }
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        Calendar cal=new GregorianCalendar(year,month,day);
        setDate(cal);
    }



    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c= Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getActivity(),year,month,day);
        }
    }



    private static final int ROW_ITEMS = 3;

    private static final class GridAdapter extends BaseAdapter {

        final ArrayList<String> mItems;
        final int mCount;

        /**
         * Default constructor
         * @param items to fill data to
         */
        private GridAdapter(final ArrayList<String> items) {

            mCount = items.size() * ROW_ITEMS;
            mItems = new ArrayList<String>(mCount);

            // for small size of items it's ok to do it here, sync way
            for (String item : items) {
                // get separate string parts, divided by ,
                final String[] parts = item.split(",");

                // remove spaces from parts
                for (String part : parts) {
                    part.replace(" ", "");
                    mItems.add(part);
                }
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            final TextView text = (TextView) view.findViewById(android.R.id.text1);

            text.setText(mItems.get(position));

            return view;
        }
    }

}
