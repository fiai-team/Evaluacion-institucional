package uci.acreditacion;

import java.util.ArrayList;
import java.util.Date;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class ProgressIntentService extends IntentService {

    Date tiempo;
	int hora, minuto, dia;
    ArrayList<Object[]> lista;
	
    public ProgressIntentService() {
        super("ProgressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun();
            }
        }
    }

    /**
     * Maneja la acci�n de ejecuci�n del servicio
     */
    private void handleActionRun() {
        try {
            while (true) {
                tiempo = new Date();
                hora = tiempo.getHours();
                minuto = tiempo.getMinutes();
                int times = hora*100+minuto;

                lista = fecha(times);

                if(lista.size() == 0)
                    return;

                int horas = (int) lista.get(0)[0];

                int hor = horas/100;
                int minu = horas%100;

                int temp = minu-minuto;
                if(temp<0) {
                    temp += 60;
                    if(hor!=0)
                        hor--;
                }

                temp=(hor-hora)*100+temp;

                SharedPreferences preferencias = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
                int alert = preferencias.getInt("alert", 30);

                // Bucle de simulaci�n
                //-----------------DEBUG-------------------//
                //System.out.println(temp);
                //System.out.println("/");
                //System.out.println(alert);


                //Se ejecuta cuando se cumple la hora establecida
                if (temp<=alert) {

                    String descripcion = (String) lista.get(0)[1];
                    String min = (horas%100==0)?"00":String.valueOf(horas%100);
                    String hour = (horas/100>12)?String.valueOf((horas/100)-12):String.valueOf(horas/100);
                    hour = (hour == "1")?"10":hour;
                    String tip = (horas/100<12)?"AM":"PM";

                    Intent notIntent = new Intent(getApplicationContext(), Cronograma.class);
                    PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(), 0, notIntent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.icono)).getBitmap())
                            .setSmallIcon(R.drawable.icono)
                            .setContentIntent(contIntent)
                            //.setPriority(2)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentTitle("A las " + hour+":"+min+" "+tip)
                            .setContentText("" + descripcion);

                    startForeground(1, builder.build());

                    Intent localIntent = new Intent(Constants.ACTION_RUN_ISERVICE)
                            .putExtra(Constants.EXTRA_PROGRESS, tiempo.getSeconds());

                    // Emisi�n de {@code localIntent}
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

                    //System.out.println("PERFECT");
                    // Retardo de 1 segundo en la iteraci�n
                } else {
                    //System.out.println("STOP");
                    stopForeground(true);
                }
                // Quitar de primer plano
                Thread.sleep(300000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("ERROR");
        }
    }

    public ArrayList<Object[]> fecha(int times){
        tiempo = new Date();
        hora = tiempo.getHours();
        minuto = tiempo.getMinutes();
        dia = tiempo.getDate();

        SharedPreferences preferencias = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
        int ids = preferencias.getInt("crono_id", 1);

        DataBase data = new DataBase(getApplicationContext());

        return data.getServices(dia, times, ids);
    }
    
    @Override
    public void onDestroy() {

        // Emisi�n para avisar que se termin� el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
