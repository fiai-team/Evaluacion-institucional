package uci.acreditacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Opciones extends AppCompatActivity {


	LinearLayout opcion_activar_notificacion;
	CheckBox chk_notificacion;
	RadioGroup radios;
	Spinner spiner;
	SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opciones);

		opcion_activar_notificacion = (LinearLayout) findViewById(R.id.opcion_activar_notificacion);
		chk_notificacion = (CheckBox) findViewById(R.id.chk_notificacion);

		pref = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
		boolean cheker = pref.getBoolean("notificacion", true);
		chk_notificacion.setChecked(cheker);

        opcion_activar_notificacion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ProgressIntentService.class);
		        intent.setAction(Constants.ACTION_RUN_ISERVICE);
				pref = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
				
				if(chk_notificacion.isChecked() == false){
					
			        startService(intent);	

			        SharedPreferences.Editor editor = pref.edit();
			        editor.putBoolean("notificacion", true);
			        editor.commit();
			        
				}else if(chk_notificacion.isChecked() == true){
					
					stopService(intent);

			        SharedPreferences.Editor editor = pref.edit();
			        editor.putBoolean("notificacion", false);
			        editor.commit();
				}

				boolean cheker = pref.getBoolean("notificacion", true);
				chk_notificacion.setChecked(cheker);

				//System.out.println(pref.getBoolean("notificacion", true));
			}
		});

		radios = (RadioGroup) findViewById(R.id.radios);

        switch(pref.getInt("crono_id", 1)) {
            case 1:
                ((RadioButton) findViewById(R.id.radioButton)).setChecked(true);
                break;
            case 2:
                ((RadioButton) findViewById(R.id.radioButton2)).setChecked(true);
                break;
            case 3:
                ((RadioButton) findViewById(R.id.radioButton3)).setChecked(true);
                break;
			case 4:
				((RadioButton) findViewById(R.id.radioButton4)).setChecked(true);
				break;
			case 5:
				((RadioButton) findViewById(R.id.radioButton5)).setChecked(true);
				break;
        }


		radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
			SharedPreferences preferencias = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferencias.edit();
			switch(checkedId) {
				case R.id.radioButton:
					editor.putInt("crono_id", 1);
					editor.commit();
					break;
				case R.id.radioButton2:
					editor.putInt("crono_id", 2);
					editor.commit();
					break;
				case R.id.radioButton3:
					editor.putInt("crono_id", 3);
					editor.commit();
					break;
				case R.id.radioButton4:
					editor.putInt("crono_id", 4);
					editor.commit();
					break;
				case R.id.radioButton5:
					editor.putInt("crono_id", 5);
					editor.commit();
					break;
			}
			}
		});

		pref = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
		spiner = (Spinner) findViewById(R.id.spinner);
		final List list = new ArrayList();
		list.add("30 min");
		list.add("1 hora");
		list.add("2 horas");

		ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,list);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiner.setAdapter(arrayAdapter);

		//System.out.println(pref.getInt("alert", 30));
		spiner.setSelected(true);
		switch(pref.getInt("alert", 30)) {
			case 100:
				spiner.setSelection(1);
				break;
			case 200:
				spiner.setSelection(2);
				break;
			default:
				spiner.setSelection(0);
				break;
		}

		spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String temp = (String) list.get(position);
				SharedPreferences preferencias = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferencias.edit();
				switch (temp) {
					case "30 min":
						editor.putInt("alert", 30);
						editor.commit();
						Toast.makeText(Opciones.this, "Tiempo de Notificación actual: 30 min", Toast.LENGTH_LONG).show();
						break;
					case "1 hora":
						editor.putInt("alert", 100);
						editor.commit();
						Toast.makeText(Opciones.this, "Tiempo de Notificación actual: 1 hora", Toast.LENGTH_LONG).show();
						break;
					case "2 horas":
						editor.putInt("alert", 200);
						editor.commit();
						Toast.makeText(Opciones.this, "Tiempo de Notificación actual: 2 horas", Toast.LENGTH_LONG).show();
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

        // Filtro de acciones que ser�n alertadas
        IntentFilter filter = new IntentFilter(
                Constants.ACTION_RUN_ISERVICE);
        filter.addAction(Constants.ACTION_PROGRESS_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver( receiver, filter);
        
	}
@Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent.getAction() == Constants.ACTION_RUN_ISERVICE){
        		//progressText.setText(intent.getIntExtra(Constants.EXTRA_PROGRESS, -1) + "");
        	}
        	else if(intent.getAction() == Constants.ACTION_PROGRESS_EXIT){
        		//progressText.setText("Progreso");
        	}
        	

        }
    }

	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if(KeyCode == KeyEvent.KEYCODE_BACK){
			startActivity(new Intent(getApplicationContext(), Cronograma.class));
			finish();
			return true;
		}
		return super.onKeyDown(KeyCode, event);

	}
	
}
