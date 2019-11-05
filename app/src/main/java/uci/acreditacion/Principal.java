package uci.acreditacion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import uci.acreditacion.adapter.CircularAdapter;
import uci.acreditacion.adapter.CircularListView;

public class Principal extends AppCompatActivity {

    private CircularItemAdapter circularsadapter;
    VideoView video;
    ArrayList<Integer> itemTitles = new ArrayList<>();
    ArrayList<Integer> itemImages = new ArrayList<>();
    private static int ver = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;

    private void mostrarPrincipal() {
        setContentView(R.layout.activity_inicio);
        this.video = new VideoView(this);
        video = (VideoView) findViewById(R.id.inicio);
        String ruta = "android.resource://" + getPackageName() + "/" + R.raw.inicio;
        video.setVideoURI(Uri.parse(ruta));
        video.start();

        Intent intent = new Intent(getApplicationContext(), ProgressIntentService.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        startService(intent);

        if (ver == 1) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    Principal.this.runOnUiThread(new Runnable() {
                        public void run() {
                            declarar();
                        }
                    });
                }
            }, 4500);
        } else
            declarar();
    }

    public void declarar(){

        setContentView(R.layout.activity_principal);

        itemImages.add(getResources().getIdentifier("selector_mapa", "drawable", "uci.acreditacion"));
        itemImages.add(getResources().getIdentifier("selector_video", "drawable", "uci.acreditacion"));
        itemImages.add(getResources().getIdentifier("selector_otros", "drawable", "uci.acreditacion"));
        itemImages.add(getResources().getIdentifier("selector_directorio", "drawable", "uci.acreditacion"));
        itemImages.add(getResources().getIdentifier("selector_informe", "drawable", "uci.acreditacion"));
        itemImages.add(getResources().getIdentifier("selector_cronograma", "drawable", "uci.acreditacion"));

        itemTitles.add(getResources().getIdentifier("imageMapa", "id", "uci.acreditacion"));
        itemTitles.add(getResources().getIdentifier("imageVideo", "id", "uci.acreditacion"));
        itemTitles.add(getResources().getIdentifier("otros", "id", "uci.acreditacion"));
        itemTitles.add(getResources().getIdentifier("directorio", "id", "uci.acreditacion"));
        itemTitles.add(getResources().getIdentifier("imageInforme", "id", "uci.acreditacion"));
        itemTitles.add(getResources().getIdentifier("imageCronograma", "id", "uci.acreditacion"));

        // usage sample
        final CircularListView circularListView = (CircularListView) findViewById(R.id.vp);
        circularsadapter = new CircularItemAdapter(getLayoutInflater(), itemTitles, itemImages);
        circularListView.setAdapter(circularsadapter);
        circularListView.setRadius(80);

        findViewById(R.id.imageVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), Video.class));
            }
        });
        findViewById(R.id.imageCronograma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), Cronograma.class));
            }
        });
        findViewById(R.id.imageInforme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Principal.this, "Cargando ... ", Toast.LENGTH_LONG).show();
                Principal.this.startActivity(new Intent(getApplicationContext(), Informe.class));
            }
        });
        findViewById(R.id.directorio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), Directorio.class));
            }
        });
        findViewById(R.id.imageMapa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), Mapa.class));
            }
        });
        findViewById(R.id.otros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), Catedra.class));
            }
        });/*
        findViewById(R.id.otros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Principal.this.startActivity(new Intent(getApplicationContext(), Paseo.class));
                Toast.makeText(Principal.this, "Anexo Paseo de las Esculturas ", Toast.LENGTH_LONG).show();
            }
        });*/
        findViewById(R.id.img_grupo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Principal.this.startActivity(new Intent(getApplicationContext(), FIAI.class));
            }
        });
        findViewById(R.id.img_atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        });
        ver = 0;
    }

    public void quit() {
        finishAffinity();
        System.exit(0);
    }

    private boolean copyAssets() {
        try
        {
            AssetManager assetManager = getAssets();
            String[] files = null;

            files = assetManager.list("");

            if (files != null) for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(getExternalFilesDir(null), filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    void copyToSD()
    {
        if(new File(getExternalFilesDir(null),"cuba.map").exists())
        {
            mostrarPrincipal();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!copyAssets())
                {
                    if(new File(getExternalFilesDir(null),"cuba.map").exists())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mostrarPrincipal();
                            }
                        });
                        return;
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                            ((TextView)findViewById(R.id.progressText)).setText("No se pudo copiar el archivo.");
                        }
                    });
                }
            }
        },"copiador").start();
    }



    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int checkpermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkpermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else copyToSD();
        } else
            copyToSD();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                SharedPreferences pref = getSharedPreferences("cronograma", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("type", 1);
                editor.commit();
                finish();
                startActivity(getIntent());
                return;
            }
        }
    }


    // you should extends CircularAdapter to add your custom item
    private class CircularItemAdapter extends CircularAdapter {

        private ArrayList<Integer> mItems;
        private LayoutInflater mInflater;
        private ArrayList<View> mItemViews;
        private ArrayList<Integer> mImages;

        public CircularItemAdapter(LayoutInflater inflater, ArrayList<Integer> items, ArrayList<Integer> itemImages){
            this.mItemViews = new ArrayList<>();
            this.mItems = items;
            this.mInflater = inflater;
            this.mImages = itemImages;

            for(int i=0; i<6; i++){
                View view = mInflater.inflate(R.layout.view_circular_item, null);
                ImageView itemView = (ImageView) view.findViewById(R.id.item);
                itemView.setId(mItems.get(i));
                itemView.setImageResource(mImages.get(i));
                mItemViews.add(view);
            }
        }

        @Override
        public ArrayList<View> getAllViews() {
            return mItemViews;
        }

        @Override
        public int getCount() {
            return mItemViews.size();
        }

        @Override
        public View getItemAt(int i) {
            return mItemViews.get(i);
        }

        @Override
        public void removeItemAt(int i) {
            if(mItemViews.size() > 0) {
                mItemViews.remove(i);
                notifyItemChange();
            }
        }

        @Override
        public void addItem(View view) {
            mItemViews.add(view);
            notifyItemChange();
        }
    }


}