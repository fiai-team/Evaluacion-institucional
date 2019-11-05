package uci.acreditacion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import uci.acreditacion.adapter.CalendarAdapter;
import uci.acreditacion.util.CalendarCollection;

public class Cronograma extends AppCompatActivity {

	public GregorianCalendar cal_month, cal_month_copy;
	private CalendarAdapter cal_adapter;
	private TextView tv_month;
	int ids;
	ArrayList<Object[]> lista;
	SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_cronograma);
		exe();
	}

	public void exe() {
		DataBase data = new DataBase(getApplicationContext());
		lista = data.getCronograma();
		preferencias = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
		ids = preferencias.getInt("crono_id", 1);

		for(int i=0;i<lista.size(); i++) {
			if((Integer)lista.get(i)[0]==ids)
				getSupportActionBar().setTitle((String)lista.get(i)[1]);
		}

		CalendarCollection.date_collection_arr=new ArrayList<>();
		//Dias de Eventos
		if(ids!=4 && ids!=5) {
			CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-02","2"));
			CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-03","3"));
		}
		CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-04","4"));
		CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-05","5"));
		CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-06","6"));
		CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-07","7"));
		CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-12-08","8"));

		// this can be add to make calendar
		cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
		int date = (cal_month.get(GregorianCalendar.MONTH) == 11)?cal_month.get(GregorianCalendar.DATE):1;
		cal_month.set(2017, 11, date);
		cal_month_copy = (GregorianCalendar) cal_month.clone();
		cal_adapter = new CalendarAdapter(this, cal_month, CalendarCollection.date_collection_arr);

		tv_month = (TextView) findViewById(R.id.tv_month);
		tv_month.setText("Diciembre "+android.text.format.DateFormat.format("yyyy", cal_month));

		GridView gridview = (GridView) findViewById(R.id.gv_calendar);
		gridview.setAdapter(cal_adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String selectedGridDate = CalendarAdapter.day_string.get(position);
				getPositionList(selectedGridDate, Cronograma.this);
			}
		});
	}

	public void getPositionList(String date,final Activity act){

		int len=CalendarCollection.date_collection_arr.size();
		for (int i = 0; i < len; i++) {
			CalendarCollection cal_collection=CalendarCollection.date_collection_arr.get(i);
			String event_date=cal_collection.date;
			String event_message=cal_collection.event_message;

			if (date.equals(event_date)) {
				Cronograma.this.startActivity(new Intent(getApplicationContext(),CronogramaDetail.class).putExtra("day",event_message).putExtra("id",ids));
			}
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
			Cronograma.this.startActivity(new Intent(getApplicationContext(), Opciones.class));
		}
		return super.onOptionsItemSelected(item);
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
