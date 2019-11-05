package uci.acreditacion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MapaDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_detail);
        int rid= getIntent().getIntExtra("record_ID", -1);
        System.out.println(rid);
        DataBase dbh = new DataBase(getApplicationContext());
        DataBase.MyRecord info = dbh.GetMarkerInfo(rid);
        ImageView p1=(ImageView)findViewById(R.id.imageView);
        ImageView p2=(ImageView)findViewById(R.id.imageView2);
        ImageView p3=(ImageView)findViewById(R.id.imageView3);
        TextView title=(TextView)findViewById(R.id.textView4);
        TextView description=(TextView)findViewById(R.id.textView);
        //p1.setImageBitmap(info.pict1);
        //p2.setImageBitmap(info.pict2);
        //p3.setImageBitmap(info.pict3);
        title.setText(info.title);
        description.setText(info.description);
    }
}
