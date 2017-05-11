package com.example.alex.pointsofinterest;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Gravity;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemHandler {

    boolean returned=false;
    boolean upload=false;
    MapFragment mapFrag;

    static ItemizedIconOverlay<OverlayItem> items;

    public void handleListItemClick(int index)
    {
        mapFrag.SetLocation(items.getItem(index).getPoint().getLatitude(),items.getItem(index).getPoint().getLongitude());
    }
    public static ItemizedIconOverlay GetItems()
    {
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);
        mapFrag=(MapFragment)getFragmentManager().findFragmentById(R.id.mapFrag);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!returned) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if(pref.getBoolean("autoupload",true)) {
                Toast.makeText(this, "Auto uploading", Toast.LENGTH_SHORT).show();
                upload=true;
            }
            else {
                Toast.makeText(this, "Not uploading", Toast.LENGTH_SHORT).show();
                upload=false;
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapFrag.Save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        ListFrag lf=(ListFrag)getFragmentManager().findFragmentById(R.id.listFrag);
        returned=false;
        if(item.getItemId() == R.id.AddPOI)
        {
            Intent intent = new Intent(this,AddPointOfInterest.class);
            startActivityForResult(intent,0);

            return true;
        }
        else if(item.getItemId()==R.id.SaveAll)
        {
            mapFrag.Save();
            return true;

        }
        else if(item.getItemId()==R.id.Preferences)
        {
            Intent intent=new Intent(this,PreferencesActivity.class);
            startActivity(intent);
            return true;

        }
        else if(item.getItemId()==R.id.Load){
            try{

                FileReader fr=new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/poi.csv");
                BufferedReader reader=new BufferedReader(fr);
                String line;
                while((line=reader.readLine())!=null)
                {
                    String[] components=line.split(",");

                    OverlayItem p=new OverlayItem(components[0],components[2],new GeoPoint(Double.parseDouble(components[4]),Double.parseDouble(components[3])));
                    mapFrag.addMark(p);
                }
                reader.close();
            }
            catch (IOException e) {
                new AlertDialog.Builder(this).setMessage("ERROR: "+e).setPositiveButton("OK",null).show();
            }
            return true;
        }
        else if(item.getItemId()==R.id.Download)
        {
            mapFrag.download();
            return true;
        }
        else if(item.getItemId()==R.id.List)
        {
            Intent intent = new Intent (this, ListActivity.class);
            startActivityForResult(intent,1);

            return true;
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

                OverlayItem p=new OverlayItem(name,type,desc,new GeoPoint(mapFrag.getLat(),mapFrag.getLon()));
                if(upload)
                {
                    mapFrag.upload(name,type,desc,mapFrag.getLat(),mapFrag.getLon());
                }

                mapFrag.addMark(p);
            }
            returned=true;
        }
        else
        if(requestCode==1)
        {

            if (resultCode==RESULT_OK)
            {
                Bundle extras=intent.getExtras();

                int index=extras.getInt("com.example.index");

                mapFrag.SetLocation(items.getItem(index).getPoint().getLatitude(),items.getItem(index).getPoint().getLongitude());
            }
            returned=true;
        }
    }

    public void updateItems(ItemizedIconOverlay<OverlayItem> items)
    {
        ListFrag lf=(ListFrag)getFragmentManager().findFragmentById(R.id.listFrag);
        lf.ListUpdate(items);
        this.items=items;
    }

    public void SetCenter(double lat,double lon)
    {
        mapFrag.SetLocation(lat,lon);
    }
}
