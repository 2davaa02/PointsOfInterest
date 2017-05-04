package com.example.alex.pointsofinterest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MapView mv;
    double lat,lon;
    ItemizedIconOverlay<OverlayItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);

        mv = (MapView)findViewById(R.id.map1);
        mv.setBuiltInZoomControls(true);
        mv.getController().setZoom(16);
        mv.getController().setCenter(new GeoPoint(50.9097,-1.4044));

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);
        mv.getOverlays().add(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.AddPOI)
        {
            lat = mv.getMapCenter().getLatitude();
            lon = mv.getMapCenter().getLongitude();
            Intent intent = new Intent(this,AddPointOfInterest.class);
            startActivityForResult(intent,0);

            return true;
        }
        else if (item.getItemId()==R.id.SaveAll)
        {
            MySaveTask s=new MySaveTask();
            s.execute(items);
        }
        return false;
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent intent)
    {

        if(requestCode==0)
        {

            if (resultCode==RESULT_OK)
            {
                Bundle extras=intent.getExtras();

                String name=extras.getString("com.example.newPoi_name"),
                        type=extras.getString("com.example.newPoi_type"),
                        desc=extras.getString("com.example.newPoi_desc");

                OverlayItem p=new OverlayItem(name,desc,new GeoPoint(lat,lon));
                
                p.setMarker(getResources().getDrawable(R.drawable.poi));
                items.addItem(p);
                mv.getOverlays().add(items);
            }
        }
    }

    class MySaveTask extends AsyncTask<ItemizedIconOverlay<OverlayItem>, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(ItemizedIconOverlay<OverlayItem>... pois) {
            try
            {
                String fname= Environment.getExternalStorageDirectory().getAbsolutePath() +"/poi.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(fname));


                for(int i=0;i<pois[0].size();i++)
                {
                    pw.println(pois[0].getItem(i).getTitle()+","+pois[0].getItem(i).getSnippet()+","+pois[0].getItem(i).getPoint().getLongitude()+","+pois[0].getItem(i).getPoint().getLatitude());
                }
                    pw.close();
                    return true;
            }
            catch(IOException e)
            {
                System.out.println ("I/O Error: " + e);
                return false;
            }
        }

        public void onPostExecute(Boolean b)
        {
            if(b)
            {
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Not saved!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
