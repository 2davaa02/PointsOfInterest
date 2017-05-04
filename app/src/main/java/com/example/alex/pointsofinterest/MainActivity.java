package com.example.alex.pointsofinterest;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

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
        return false;
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent intent) throws NullPointerException
    {

        if(requestCode==0)
        {

            if (resultCode==RESULT_OK)
            {
                Bundle extras=intent.getExtras();

                String name=extras.getString("com.example.newPoi_name"),type=extras.getString("com.example.newPoi_type"),desc=extras.getString("com.example.newPoi_desc");;


                OverlayItem poi = new OverlayItem(name, desc, new GeoPoint(lat,lon));
                poi.setMarker(getResources().getDrawable(R.drawable.poi));
            }
        }
    }
}
