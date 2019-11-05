package uci.acreditacion;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DirectorioBusqueda extends AppCompatActivity {

    ArrayList<String> lista;
    Dialog dialog_datos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_list);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null) {
            //Recibir Dia
            lista = (ArrayList<String>) bundle.get("lista");

            Adapta Adapt = new Adapta(this);
            ListView listaSearch = (ListView) findViewById (R.id.learch);
            listaSearch.setAdapter(Adapt);
        }
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
        ArrayList<Object[]> datos = data.getDataUser(name);

        imagen = (ImageView) dialog_datos.findViewById(R.id.imagen);
        txt_nombre = (TextView) dialog_datos.findViewById(R.id.txt_nombre);
        txt_cargo = (TextView) dialog_datos.findViewById(R.id.txt_user);
        txt_tel = (TextView) dialog_datos.findViewById(R.id.txt_tel);

        String id = String.valueOf(datos.get(0)[0]);
        int resID = getResources().getIdentifier("img_0"+id, "drawable", "uci.acreditacion");
        imagen.setImageResource(resID);     //Foto del Usuario segun su ID
        txt_nombre.setText(name);                                           //Nombre del Usuario
        txt_cargo.setText(datos.get(0)[1].toString());                      //Cargo  del Usuario
        txt_tel.setText(datos.get(0)[2].toString());                        //Telefono del Usuario

        dialog_datos.show();
    }

    //Clase Adaptadora del ListView
    class Adapta extends ArrayAdapter<String> {

        AppCompatActivity appCompatActivity;

        Adapta(AppCompatActivity context) {
            super(context, R.layout.search_text, lista);
            appCompatActivity = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.search_text, null);
            final TextView a = (TextView) item.findViewById(R.id.ext);
            a.setText(lista.get(position).toString());

            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog((String) a.getText());
                }
            });
            return(item);
        }
    }
}
