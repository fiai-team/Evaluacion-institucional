package uci.acreditacion;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class Video extends AppCompatActivity {

     VideoView video;
        FrameLayout contenedor, menu_reproduccion;
        TextView texto_posicion, texto_total, texto_correr;
        ImageView atras, play, adelante, img_atras;
        Thread hilo_progreso;
        SeekBar progreso;
        boolean cambiar_barra, mover, pausar, menu_oculto;
        int position, correr, cant_mover, x_1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video);
            getSupportActionBar().hide();

            cambiar_barra = true; mover = false; pausar = false; position = 0; menu_oculto = true;
            video = (VideoView) findViewById(R.id.video);

            String ruta = "android.resource://" + getPackageName() + "/" + R.raw.video;
            video.setVideoURI(Uri.parse(ruta));
            video.start();  correr = 5000;//(video.getDuration() * 5 / 100) ;

            atras = (ImageView) findViewById(R.id.atras);
            play = (ImageView) findViewById(R.id.play);
            adelante = (ImageView) findViewById(R.id.adelante);

            texto_posicion = (TextView) findViewById(R.id.texto_posicion);
            texto_total = (TextView) findViewById(R.id.texto_total);
            texto_correr = (TextView) findViewById(R.id.texto_correr);
            texto_correr.setText("");

            progreso = (SeekBar) findViewById(R.id.progreso);
            contenedor = (FrameLayout) findViewById(R.id.contenedor);
            menu_reproduccion = (FrameLayout) findViewById(R.id.menu_reproduccion);

            img_atras = (ImageView) findViewById(R.id.img_atras);


// - - - - - - - Ciclo interminable para la actualizacion de la barra de progreso - - - - - - - - - //
            final Handler mhandler = new Handler();                                                     //
            hilo_progreso = new Thread(new Runnable() {                                                 //
                @Override public void run() {                                                           //
                    while(true){                                                                        //
                        try {                                                                           //
                            mhandler.post(new Runnable() {                                              //
                                @Override public void run() {                                           //
                                    texto_total.setText(actualizar_tiempo(video.getDuration(),false));        //
                                    texto_posicion.setText(actualizar_tiempo(video.getCurrentPosition(),false));//
                                    if(cambiar_barra){                                                  //
                                        progreso.setMax(video.getDuration());                           //
                                        progreso.setProgress(video.getCurrentPosition());               //
                                        // - - - - - - Actualizacion del boton play/pause - - - - - - - //
                                        if(video.isPlaying()){                                          //
                                            play.setImageResource(android.R.drawable.ic_media_pause);   //
                                        }else{                                                          //
                                            play.setImageResource(android.R.drawable.ic_media_play);    //
                                        }                                                               //
                                    }                                                                   //
                                }                                                                       //
                            });                                                                         //
                            Thread.sleep(100);                                                          //
                        }catch (Exception e){}                                                          //
                    }                                                                                   //
                }                                                                                       //
            });hilo_progreso.start();                                                                   //
// - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - //




// - - - - - - - - - - - - Evento para cuando se acaba el video - - - - - - - - - - - - - - - - - - //
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    position = 0;
                    video.seekTo(0);
                    video.pause();
                    startActivity(new Intent(getApplicationContext(), Principal.class));
                }
            });
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -//




// - - - - - - - - - - - Eventos relacionados con la barra de progreso - - - - - - - - - - - - - - -//
            progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(!cambiar_barra){video.seekTo(progress);}
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    cambiar_barra = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    cambiar_barra = true;
                    position = progreso.getProgress();
                    video.seekTo(position);
                    if(video.isPlaying())
                        video.start();
                }
            });
// - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - //




// - - - - - - - - - - Eventos del click en el menu de reproduccion - - - - - - - - - - - - - - - - //
            play.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(video.isPlaying()){
                        position = video.getCurrentPosition();
                        video.pause();
                    }else{
                        video.seekTo(position);
                        video.start();
                    }
                }
            });

            adelante.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    position = video.getCurrentPosition();
                    if(position < (video.getDuration() - correr)){
                        video.seekTo(position + correr);
                    }
                    if(video.isPlaying()) video.start();
                }
            });

            atras.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    position = video.getCurrentPosition();
                    if(position > correr){
                        video.seekTo(position - correr);
                    }else if(position < correr){
                        video.seekTo(0);
                    }
                    if(video.isPlaying()){
                        video.start();
                    }
                }
            });
// - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - //

            img_atras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(getApplicationContext(), Principal.class));
                    //finish();

                }
            });



            menu_reproduccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            contenedor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            contenedor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action){
                        case MotionEvent.ACTION_DOWN:
                            x_1 = (int) event.getX();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mover = ((int) event.getX() - x_1) > 20 || ((int) event.getX() - x_1) < 20;
                            cant_mover = ((int) event.getX() - x_1) * 20;
                            texto_correr.setText(actualizar_tiempo(cant_mover, true));
                            break;
                        case MotionEvent.ACTION_UP:
                            if(cant_mover + video.getCurrentPosition() < video.getDuration() && cant_mover + video.getCurrentPosition() >= 0 && mover) {
                                video.seekTo(video.getCurrentPosition() + cant_mover);
                                if(video.isPlaying()) video.start();
                            }
                            texto_correr.setText(""); cant_mover = 0; mover = false;

                            final Handler t_pausar = new Handler();
                            t_pausar.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pausar = false;

                                }
                            },500);



                            if(pausar == false){
                                pausar = true;
                                if(menu_oculto){
                                    animUp(menu_reproduccion);
                                    animDownatras(img_atras);
                                    menu_oculto = false;
                                }else{
                                    animDown(menu_reproduccion);
                                    animUpatras(img_atras);
                                    menu_oculto = true;
                                }

                            }else{

                                position = video.getCurrentPosition();
                                pausar = false;
                                if(video.isPlaying()){
                                    video.pause();
                                }else{
                                    video.seekTo(position);
                                    video.start();
                                }
                            }



                            break;
                    }

                    return false;
                }
            });





        }

        public void animUp(View v){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.aparecer_menu);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }

        public void animDown(View v){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.esconder_menu);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }

        public void animUpatras(View v){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.esconder_atras);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }

        public void animDownatras(View v){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.aparecer_atras);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }


        public String actualizar_tiempo(int ms, boolean corriendo){
            String tiempo_total = ""; int seg, min;
            if(corriendo){
                if(ms < 0){
                    ms *= -1;
                    tiempo_total += "-";
                }else tiempo_total += "+";
            }
            seg = ms / 1000;
            min = seg / 60; seg %= 60;
            if(seg < 10){
                if(min < 10) tiempo_total += "0" + min + ":0" + seg;
                else tiempo_total += min + ":0" + seg;
            }else{
                if(min < 10) tiempo_total += "0" + min + ":" + seg;
                else tiempo_total += min + ":" + seg;
            }
            return tiempo_total;
        }


        public void onPause(){
            super.onPause();
            if(video.isPlaying()) {
                position = video.getCurrentPosition();
                video.pause();
            }
        }

        public void onResume(){
            super.onResume();
            cambiar_barra = true;
            if(video.isPlaying() == false){
                video.seekTo(position);
                video.start();
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