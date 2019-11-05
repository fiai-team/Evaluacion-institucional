package uci.acreditacion;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class CatedraInfo extends AppCompatActivity {

    ArrayList<Object[]> lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catedra_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if(bundle!=null) {
            //Recibir Dia
            String cronograma = (String) bundle.get("catedra");

            DataBase data = new DataBase(getApplicationContext());
            lista = data.getCatedraInfo(cronograma);

            getSupportActionBar().setTitle((String) lista.get(0)[0]);

            ((TextView) findViewById(R.id.cat_descripcion)).setText((String) lista.get(0)[1]);

            ((TextView) findViewById(R.id.cat_objetivos)).setText((String) lista.get(0)[3]);

            ((TextView) findViewById(R.id.cat_presidente)).setText((String) lista.get(0)[5]);

            ((TextView) findViewById(R.id.cat_vicepresidente)).setText((String) lista.get(0)[4]);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.catedra, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DataBase data = new DataBase(getApplicationContext());
        final ArrayList<Double[]> lat = (data.posicion((Integer) lista.get(0)[6]));
        int id = item.getItemId();
        if (id == R.id.cat) {
            CatedraInfo.this.startActivity(new Intent(getApplicationContext(),
                Mapa.class).putExtra("lat", lat.get(0)[0]).putExtra("lon", lat.get(0)[1]));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
