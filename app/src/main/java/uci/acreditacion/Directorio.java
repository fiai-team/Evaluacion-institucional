package uci.acreditacion;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uci.acreditacion.adapter.CustomExpandableListAdapter;

public class Directorio extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    Dialog dialog_datos = null;
    Integer id;
    private MenuItem searchMenuItem;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorio);

        expandableListView = (ExpandableListView) findViewById(R.id.directorio);
        //Pedir Datos de la Base de Datos en un HashMap
        DataBase data = new DataBase(getApplicationContext());
        expandableListDetail = data.getDirectorioAll();

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                dialog(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                return false;
            }
        });

    }

    public void dialog(String name) {
        if(name == "Sin Resultados")
            return;

        ImageView imagen;
        TextView txt_nombre, txt_cargo, txt_tel;

        // con este tema personalizado evitamos los bordes por defecto
        dialog_datos = new Dialog(this,R.style.Theme_Dialog_Translucent);
        //deshabilitamos el título por defecto
        dialog_datos.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        dialog_datos.setCancelable(true);
        //establecemos el contenido de nuestro dialog
        dialog_datos.setContentView(R.layout.dialog_datos);

        //Extraer Datos del Usuario
        DataBase data = new DataBase(getApplicationContext());
        ArrayList<Object[]> datos = data.getDirectorioUser(name);

        imagen = (ImageView) dialog_datos.findViewById(R.id.imagen);
        txt_nombre = (TextView) dialog_datos.findViewById(R.id.txt_nombre);
        txt_cargo = (TextView) dialog_datos.findViewById(R.id.txt_user);
        txt_tel = (TextView) dialog_datos.findViewById(R.id.txt_tel);

        String id = String.valueOf(datos.get(0)[0]);
        int resID = getResources().getIdentifier("img_0"+id, "drawable", "uci.acreditacion");
        imagen.setImageResource(resID);     //Foto del Usuario segun su ID
        txt_nombre.setText(datos.get(0)[1].toString());                                           //Nombre del Usuario
        System.out.println(datos.get(0)[1].toString());
        txt_cargo.setText(datos.get(0)[2].toString());                      //Cargo  del Usuario
        txt_tel.setText(datos.get(0)[3].toString());                        //Telefono del Usuario

        dialog_datos.show();
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
                        Toast.makeText(Directorio.this, "No se permiten números", Toast.LENGTH_SHORT).show();
                    } else {
                        //Extraer Datos del Usuario
                        DataBase data = new DataBase(getApplicationContext());
                        ArrayList<String> datos = data.getSearchUser(query);
                        Directorio.this.startActivity(new Intent(getApplicationContext(),DirectorioBusqueda.class).putExtra("lista",datos));
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

    private void loseFocusOnSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
        MenuItemCompat.collapseActionView(searchMenuItem);
    }
}
