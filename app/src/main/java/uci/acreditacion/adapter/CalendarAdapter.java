package uci.acreditacion.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import uci.acreditacion.R;
import uci.acreditacion.util.CalendarCollection;

public class CalendarAdapter extends BaseAdapter {
	private Context context;

	private java.util.Calendar month;
	public GregorianCalendar pmonth;
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;

	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int mnthlength;
	String itemvalue, curentDateString;
	DateFormat df;

	private ArrayList<String> items;
	public static List<String> day_string;
	private View previousView;
	public ArrayList<CalendarCollection>  date_collection_arr;

	public CalendarAdapter(Context context, GregorianCalendar monthCalendar, ArrayList<CalendarCollection> date_collection_arr) {
		this.date_collection_arr=date_collection_arr;
		CalendarAdapter.day_string = new ArrayList<String>();
		Locale.setDefault(Locale.US);
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		this.context = context;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		curentDateString = df.format(selectedDate.getTime());
		refreshDays();
	}

	public int getCount() {
		return day_string.size();
	}

	public Object getItem(int position) {
		return day_string.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView dayView;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.cal_item, null);
		}

		dayView = (TextView) v.findViewById(R.id.date);
		String[] separatedTime = day_string.get(position).split("-");
		
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			dayView.setTextColor(Color.GRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			dayView.setTextColor(Color.GRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			dayView.setTextColor(Color.BLACK);
		}
		
		if (day_string.get(position).equals(curentDateString)) {
			v.setBackgroundColor(Color.GRAY);
		} else {
			v.setBackgroundColor(Color.WHITE);
		}
		
		dayView.setText(gridvalue);
		setEventView(v, position,dayView);
		return v;
	}

	public void refreshDays() {
		// clear items
		items.clear();
		day_string.clear();
		Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...

		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		for (int n = 0; n < mnthlength; n++) {
			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			day_string.add(itemvalue);
		}
	}

	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

	public void setEventView(View v,int pos,TextView txt){
		
		int len=CalendarCollection.date_collection_arr.size();
		for (int i = 0; i < len; i++) {
			CalendarCollection cal_obj=CalendarCollection.date_collection_arr.get(i);
			String date=cal_obj.date;
			int len1=day_string.size();
			if (len1>pos) {
				if (day_string.get(pos).equals(date)) {
					v.setBackgroundColor(Color.BLACK);
					v.setBackgroundResource(R.drawable.rounded_calender_item);
					txt.setTextColor(Color.BLACK);
				}
			}
		}
	}
	

}