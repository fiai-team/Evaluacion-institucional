package uci.acreditacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CronogramaDetail extends AppCompatActivity {

    ListView lvl;
    ArrayList<Object[]> lista;
    int ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronograma_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null) {
            //Recibir Dia
            String date = (String) bundle.get("day");
            Integer id = (Integer) bundle.get("id");

            //Pedir Datos de la Base de Datos en un ArrayList
            DataBase data = new DataBase(getApplicationContext());
            lista = data.getDayAll(date, id);

            //Mostrar ListView por el Adaptador
            Adaptador adaptador = new Adaptador(this);
            lvl = (ListView) findViewById (R.id.lista);
            lvl.setAdapter(adaptador);
        }
    }

    //Clase Adaptadora del ListView
    class Adaptador extends ArrayAdapter<Object[]> {

        AppCompatActivity appCompatActivity;

        Adaptador(AppCompatActivity context) {
            super(context, R.layout.activity_cronograma_list, lista);
            appCompatActivity = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.activity_cronograma_list, null);

            //Ajustando Vista de la Hora
            int horas = (int) lista.get(position)[1];
            String min = (horas%100==0)?"00":String.valueOf(horas%100);
            String hour = (horas/100>12)?String.valueOf((horas/100)-12):String.valueOf(horas/100);
            hour = (hour == "1")?"10":hour;
            String tip = (horas/100<12)?"AM":"PM";

            //Mostrar Hora
            TextView hora = (TextView) item.findViewById (R.id.hora);
            hora.setText(hour+":"+min+" "+tip);

            //Mostrar Actividad del Dia
            TextView activity = (TextView) item.findViewById (R.id.activity);
            activity.setText(lista.get(position)[2].toString());

            //Mostrar Ubicacion
            TextView ubicacion = (TextView) item.findViewById (R.id.locate);
            ubicacion.setText(lista.get(position)[3].toString());

            //Datos Ubicacion
            ubicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBase data = new DataBase(getApplicationContext());
                    ArrayList<Double[]> lat = (data.posicion((Integer) lista.get(position)[4]));
                    //System.out.println(lista.get(position)[0]);
                    //System.out.println(lista.get(position)[4]);
                    if(lat.get(0)[0] == 0.0 || lat.get(0)[1] == 0.0){
                        CronogramaDetail.this.startActivity(new Intent(getApplicationContext(), Mapa.class));
                    } else {
                        CronogramaDetail.this.startActivity(new Intent(getApplicationContext(),
                                Mapa.class).putExtra("lat", lat.get(0)[0]).putExtra("lon", lat.get(0)[1]));
                    }
                }
            });
            return(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cronograma, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.config) {
            CronogramaDetail.this.startActivity(new Intent(getApplicationContext(), Opciones.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
