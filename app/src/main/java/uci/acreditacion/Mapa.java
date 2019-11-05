package uci.acreditacion;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.ArrayList;

public class Mapa extends AppCompatActivity {
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    ArrayList<String> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        this.mapView = new MapView(this);
        setContentView(R.layout.activity_mapa);
        LinearLayout ll=(LinearLayout)findViewById(R.id.ll);
        mapView.layout(0, 0, ll.getWidth(), ll.getHeight());
        ll.addView(mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        DataBase dbh = new DataBase(getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null) {
            //Recibir Dia
            double lon = (double) bundle.get("lon");
            double lat = (double) bundle.get("lat");
            this.mapView.getModel().mapViewPosition.setCenter(new LatLong(lat, lon));
            this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 18);
        } else {
            this.mapView.getModel().mapViewPosition.setCenter(new LatLong(22.9881563, -82.4648623));
            this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 15);
        }

        // tile renderer layer using internal render theme
        File myf = new File(getExternalFilesDir(null),"cuba.map");

        if(!myf.exists())
            Toast.makeText(getApplicationContext(),"No se encuentra el archivo",Toast.LENGTH_LONG).show();
        else
        {
            MapDataStore mapDataStore = new MapFile(myf);
            this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

            // only once a layer is associated with a mapView the rendering starts
            this.mapView.getLayerManager().getLayers().add(tileRendererLayer) ;

            Cursor c = dbh.GetMarkers();
            if(c==null)
                return;
            do {
                double lat=c.getDouble(c.getColumnIndex(DataBase.MainTable.FIELD_LAT));
                double lon=c.getDouble(c.getColumnIndex(DataBase.MainTable.FIELD_LON));
                int id=c.getInt(c.getColumnIndex(DataBase.MainTable.FIELD_ID));
                String photo=c.getString(c.getColumnIndex(DataBase.MainTable.FIELD_TYPE));
                String texto=c.getString(c.getColumnIndex(DataBase.MainTable.FIELD_TITLE));
                createPositionMarker(lat, lon, id, photo, texto);
            }
            while (c.moveToNext());
            //createPositionMarker(22.991254, -82.46354, 1);
        }
    }

    private void createPositionMarker(double paramDouble1, double paramDouble2,int id, String photo, String texto)
    {

        final LatLong localLatLong = new LatLong(paramDouble1, paramDouble2);
        int resID = getResources().getIdentifier(String.valueOf(photo), "drawable", "uci.acreditacion");      //Foto segun el ID
        InfoMarker positionmarker = new InfoMarker(localLatLong,
                AndroidGraphicFactory.convertToBitmap(getApplicationContext()
                        .getResources().getDrawable(resID)),0,0,id,texto);

        mapView.getLayerManager().getLayers().add(positionmarker);
        mapView.getLayerManager().redrawLayers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*this.mapView.getLayerManager().getLayers().remove(this.tileRendererLayer);
        this.tileRendererLayer.onDestroy();*/
    }

    protected void onDestroy() {
        super.onDestroy();
        this.tileCache.destroy();
        this.mapView.getModel().mapViewPosition.destroy();
        this.mapView.destroy();
        AndroidGraphicFactory.clearResourceMemoryCache();
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
                        Toast.makeText(Mapa.this, "No se permiten números", Toast.LENGTH_SHORT).show();
                    } else {
                        //Extraer Datos del Usuario
                        DataBase data = new DataBase(getApplicationContext());
                        ArrayList<String> datos = data.getSearchMapa(query);
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

    private void buscar(ArrayList<String> datos) {
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
    class Adaptado extends ArrayAdapter<String> {

        AppCompatActivity appCompatActivity;

        Adaptado(AppCompatActivity context) {
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
                    DataBase data = new DataBase(getApplicationContext());
                    ArrayList<Double[]> id = data.getMapa((String) a.getText());
                    Mapa.this.startActivity(new Intent(getApplicationContext(),
                            Mapa.class).putExtra("lat", id.get(0)[0]).putExtra("lon", id.get(0)[1]));
                }
            });
            return(item);
        }
    }

    public class InfoMarker extends Marker {
        private int id;
        private String text;
        private LatLong latLong;
        private int offsetX = 0;
        private int offsetY = 0;
        private Paint paint;

        public InfoMarker(LatLong latLong, org.mapsforge.core.graphics.Bitmap bitmap, int horizontalOffset, int verticalOffset, int id, String texto) {
            super(latLong, bitmap, horizontalOffset, verticalOffset);
            this.id=id;
            this.latLong=latLong;
            this.text=texto;

            this.paint = AndroidGraphicFactory.INSTANCE.createPaint();
            this.paint.setColor(0x77ff0000);
            this.paint.setStrokeWidth(0);
            this.paint.setStyle(Style.FILL);
        }

        @Override
        public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
            super.draw(boundingBox, zoomLevel, canvas, topLeftPoint);

            int pixelX = (int) (MercatorProjection.longitudeToPixelX(latLong.longitude, zoomLevel) - topLeftPoint.x);
            int pixelY = (int) (MercatorProjection.latitudeToPixelY(latLong.latitude, zoomLevel) - topLeftPoint.y);
            canvas.drawText(text, pixelX + offsetX, pixelY + offsetY, paint);
        }

        @Override
        public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {

            double centerX = layerXY.x + getHorizontalOffset();
            double centerY = layerXY.y + getVerticalOffset();

            double radiusX = (getBitmap().getWidth() / 2) *1.1;
            double radiusY = (getBitmap().getHeight() / 2) *1.1;


            double distX = Math.abs(centerX - tapXY.x);
            double distY = Math.abs(centerY - tapXY.y);


            if( distX < radiusX && distY < radiusY){
                //Intent i=new Intent(Mapa.this,MapaDetail.class);
                //i.putExtra("record_ID",id);
                //Mapa.this.startActivity(i);

                DataBase dbh = new DataBase(getApplicationContext());
                DataBase.MyRecord info = dbh.GetMarkerInfo(id);
                Toast.makeText(Mapa.this, info.title, Toast.LENGTH_LONG).show();

                return true;
            }
            return false;
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
