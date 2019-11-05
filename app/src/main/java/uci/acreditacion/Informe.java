package uci.acreditacion;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.CssStatus;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class Informe extends AppCompatActivity implements PageFragment.OnFragmentReadyListener {

    private Reader reader;
    private ViewPager mViewPager;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * (FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //private int pageCount = Integer.MAX_VALUE;
    private int pageCount = 25;
    private int pxScreenWidth;

    private boolean isPickedWebView = false;

    private MenuItem searchMenuItem;
    private Spinner SpinnerView;

    private boolean isSkippedToPage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe);

// - - - - - - - - - - - - - - - - Código que estaba en el MenuActivity - - - - - - - - - - - - - - //

        String ePub = "informe.epub";
        File file = new File(getCacheDir() + "/" + ePub);

        if (!file.exists()) try {
            InputStream is = getAssets().open(ePub);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String filePath = file.getPath();
        isPickedWebView = true;

// - - - - - - - - - - - - - - - - - - - - Fin del Código - - - - - - - - - - - - - - - - - - - - - //
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Informe.this.startActivity(new Intent(getApplicationContext(),Principal.class));
            }
        });*/

        pxScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        try {
            reader = new Reader();

            // Setting optionals once per file is enough.
            //reader.setMaxContentPerSection(1250);
            reader.setCssStatus(isPickedWebView ? CssStatus.INCLUDE : CssStatus.OMIT);
            reader.setIsIncludingTextContent(true);
            reader.setIsOmittingTitleTag(true);

            // This method must be called before readSection.
            reader.setFullContent(filePath);

//                int lastSavedPage = reader.setFullContentWithProgress(filePath);
            if (reader.isSavedProgressFound()) {
                int lastSavedPage = reader.loadProgress();
                mViewPager.setCurrentItem(lastSavedPage);
            }

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    //Pagina que va pasando
                }

                @Override
                public void onPageSelected(int position) {

                    //Pagina Seleccionada
                    SpinnerView.setSelection(position);
                    if(position==24) {
                        mViewPager.setCurrentItem(0);
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    //Pagina Anterior

                }
            });

        } catch (ReadingException e) {
            Toast.makeText(Informe.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onFragmentReady(int position) {

        BookSection bookSection = null;

        try {
            bookSection = reader.readSection(position);
        } catch (ReadingException e) {
            e.printStackTrace();
            Toast.makeText(Informe.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (OutOfPagesException e) {
            e.printStackTrace();
            this.pageCount = e.getPageCount();

            if (isSkippedToPage) {
                Toast.makeText(Informe.this, "La página Maxima es: " + this.pageCount, Toast.LENGTH_LONG).show();
            }

            mSectionsPagerAdapter.notifyDataSetChanged();
        }

        isSkippedToPage = false;

        if (bookSection != null) {
            return setFragmentView(isPickedWebView, bookSection.getSectionContent(), "text/html", "UTF-8"); // reader.isContentStyled
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.informe_spinner, menu);

        searchMenuItem = menu.findItem(R.id.spin);
        SpinnerView = (Spinner) MenuItemCompat.getActionView(searchMenuItem);

        //Pedir Datos de la Base de Datos en un ArrayList
        DataBase data = new DataBase(getApplicationContext());
        List list = data.getPages();

        ArrayAdapter countryAdapter = new ArrayAdapter(this, R.layout.spinner_dropdown, list);
        countryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        SpinnerView.setAdapter(countryAdapter);

        try {
            int lastSavedPage = reader.loadProgress();
            int page = data.getPageSelect(lastSavedPage);
            SpinnerView.setSelected(true);
            SpinnerView.setSelection(page-1);

        } catch (ReadingException e) {
            e.printStackTrace();
        }

        SpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerView.setSelected(true);
                SpinnerView.setSelection(position);
                if(position == 23)
                    mViewPager.setCurrentItem(position+1);
                else
                    mViewPager.setCurrentItem(position);

                //Debemos poner un codigo que luego de la seleccion te muestre un mensaje de cargando ...
                //Toast.makeText(Informe.this, "Cargando ... ", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!SpinnerView.isFocused()) {
            loseFocusOnSearchView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            reader.saveProgress(mViewPager.getCurrentItem());
            Toast.makeText(Informe.this, "Página Guardada: " + mViewPager.getCurrentItem() + "...", Toast.LENGTH_SHORT).show();
        } catch (ReadingException e) {
            e.printStackTrace();
            Toast.makeText(Informe.this, "Proceso no ha sido guardado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (OutOfPagesException e) {
            e.printStackTrace();
            Toast.makeText(Informe.this, "Proceso no ha sido guardado. Fuera de los límites. Número de páginas: " + e.getPageCount(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View setFragmentView(boolean isContentStyled, String data, String mimeType, String encoding) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (isContentStyled) {
            WebView webView = new WebView(Informe.this);
            webView.loadDataWithBaseURL(null, data, mimeType, encoding, null);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }

            webView.setLayoutParams(layoutParams);

            return webView;
        } else {
            ScrollView scrollView = new ScrollView(Informe.this);
            scrollView.setLayoutParams(layoutParams);

            TextView textView = new TextView(Informe.this);
            textView.setLayoutParams(layoutParams);

            textView.setText(Html.fromHtml(data, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    String imageAsStr = source.substring(source.indexOf(";base64,") + 8);
                    byte[] imageAsBytes = Base64.decode(imageAsStr, Base64.DEFAULT);
                    Bitmap imageAsBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                    int imageWidthStartPx = (pxScreenWidth - imageAsBitmap.getWidth()) / 2;
                    int imageWidthEndPx = pxScreenWidth - imageWidthStartPx;

                    Drawable imageAsDrawable = new BitmapDrawable(getResources(), imageAsBitmap);
                    imageAsDrawable.setBounds(imageWidthStartPx, 0, imageWidthEndPx, imageAsBitmap.getHeight());
                    return imageAsDrawable;
                }
            }, null));

            int pxPadding = dpToPx(12);

            textView.setPadding(pxPadding, pxPadding, pxPadding, pxPadding);

            scrollView.addView(textView);
            return scrollView;
        }
    }

    private void loseFocusOnSearchView() {
        SpinnerView.clearFocus();
        MenuItemCompat.collapseActionView(searchMenuItem);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return PageFragment.newInstance(position);
        }
    }

    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(KeyCode, event);

    }

}

