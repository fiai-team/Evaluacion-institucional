package uci.acreditacion;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FIAI extends AppCompatActivity {

    private MenuItem searchMenuItem;
    private SearchView searchView;
    ArrayList<String[]> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiai);
        DataBase data = new DataBase(getApplicationContext());
        lista = data.Desarrolladores();

        Adaptado Adapt = new Adaptado(this);
        ListView listaSearch = (ListView) findViewById (R.id.lista_fiai);
        listaSearch.setAdapter(Adapt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.informe, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query != null && !query.equals("")) {

                    if (TextUtils.isDigitsOnly(query)) {
                        loseFocusOnSearchView();
                        Toast.makeText(FIAI.this, "No se permiten números", Toast.LENGTH_SHORT).show();
                    } else {
                        //Extraer Datos del Usuario
                        DataBase data = new DataBase(getApplicationContext());
                        ArrayList<String[]> datos = data.getSearchDesarrollador(query);
                        buscar(datos);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void buscar(ArrayList<String[]> datos) {
        this.setContentView(R.layout.search_list);
        //Extraer Datos del Usuario
        lista = datos;

        Adaptado Adapt = new Adaptado(this);
        ListView listaSearch = (ListView) findViewById (R.id.learch);
        listaSearch.setAdapter(Adapt);
    }

    private void loseFocusOnSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
        MenuItemCompat.collapseActionView(searchMenuItem);
    }

    //Clase Adaptadora del ListView
    class Adaptado extends ArrayAdapter<String[]> {

        AppCompatActivity appCompatActivity;

        Adaptado(AppCompatActivity context) {
            super(context, R.layout.powered_list, lista);
            appCompatActivity = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.powered_list, null);

            final ImageView imagen = (ImageView) item.findViewById(R.id.imagen);
            final TextView nombre = (TextView) item.findViewById(R.id.nombre);
            final TextView cargo = (TextView) item.findViewById(R.id.cargo);
            final TextView work = (TextView) item.findViewById(R.id.work);

            String photo = lista.get(position)[0];
            nombre.setText(lista.get(position)[1]);
            cargo.setText(lista.get(position)[3]);
            work.setText(lista.get(position)[2]);

            int resID = getResources().getIdentifier("dev_0"+photo, "drawable", "uci.acreditacion");      //Foto segun el ID
            //int resID = getResources().getIdentifier("img_0", "drawable", "uci.acreditacion");      //Foto
            imagen.setImageResource(resID);     //Foto del Usuario segun su ID

            if(lista.size() == 0) {
                nombre.setText("Sin Resultados");
                cargo.setText("Desconocido");
                work.setText("desconocido@email.cu");
                int res = getResources().getIdentifier("img_0", "drawable", "uci.acreditacion");      //Foto
                imagen.setImageResource(res);
            }
            return(item);
        }
    }

    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(getApplicationContext(), Principal.class));
            finish();
            return true;
        }
        return super.onKeyDown(KeyCode, event);

    }
}
