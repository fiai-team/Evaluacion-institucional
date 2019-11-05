package uci.acreditacion;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Catedra extends AppCompatActivity {

    Dialog dialog_catedras = null;
    ListView lista_catedras;

    ImageView img_catedra;
    TextView nombre_catedra;

    ArrayList<String[]> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catedra);

        (findViewById(R.id.btn_mostrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrar_dialog_catedras();
            }
        });

    }

    public void mostrar_dialog_catedras(){
        // con este tema personalizado evitamos los bordes por defecto
        dialog_catedras = new Dialog(this,R.style.Theme_Dialog_Translucent);
        //deshabilitamos el título por defecto
        dialog_catedras.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        dialog_catedras.setCancelable(true);
        //establecemos el contenido de nuestro dialog
        dialog_catedras.setContentView(R.layout.dialog_catedras);

        DataBase data = new DataBase(getApplicationContext());
        list = data.getCatedra();

        ad Adapt = new ad(this);
        lista_catedras = (ListView) dialog_catedras.findViewById(R.id.lista_catedras);
        lista_catedras.setAdapter(Adapt);

        lista_catedras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = list.get(position)[0];

                Catedra.this.startActivity(new Intent(getApplicationContext(),CatedraInfo.class).putExtra("catedra",name));

            }
        });

        dialog_catedras.show();


    }

    //Clase Adaptadora del ListView
    class ad extends ArrayAdapter<String[]> {

        AppCompatActivity appCompatActivity;

        ad(AppCompatActivity context) {
            super(context, R.layout.powered_list, list);
            appCompatActivity = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.dialog_catedras_item, null);

            img_catedra = (ImageView) item.findViewById(R.id.img_catedra);
            nombre_catedra = (TextView) item.findViewById(R.id.nombre_catedra);

            nombre_catedra.setText("Cátedra Honorífica "+list.get(position)[0]);
            String photo = list.get(position)[1];

            int resID = getResources().getIdentifier(photo, "drawable", "uci.acreditacion");      //Foto segun el ID
            img_catedra.setImageResource(resID);     //Foto del Usuario segun su ID

            return(item);
        }
    }
}
