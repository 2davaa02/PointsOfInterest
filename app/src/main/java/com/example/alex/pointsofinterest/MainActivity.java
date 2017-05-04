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

public class MainActivity extends AppCompatActivity {

    boolean returned=false;
    boolean upload=false;
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

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);
        mv.getOverlays().add(items);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if(!returned) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            mv.getController().setZoom(16);
            mv.getController().setCenter(new GeoPoint(50.9097,-1.4044));

            if(pref.getBoolean("autoupload",true))
            {
                Toast.makeText(this, "Auto uploading", Toast.LENGTH_SHORT).show();
                upload=true;

            }
            else
            {
                Toast.makeText(this, "Not uploading", Toast.LENGTH_SHORT).show();
                upload=false;

            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        new MySaveTask().execute(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        returned=false;
        if(item.getItemId() == R.id.AddPOI)
        {
            lat = mv.getMapCenter().getLatitude();
            lon = mv.getMapCenter().getLongitude();
            Intent intent = new Intent(this,AddPointOfInterest.class);
            startActivityForResult(intent,0);

            return true;
        }
        else if(item.getItemId()==R.id.SaveAll)
        {
            MySaveTask s=new MySaveTask();
            s.execute(items);
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
                    items.addItem(p);

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
            MyDownloadTask t=new MyDownloadTask();
            t.execute();
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

                OverlayItem p=new OverlayItem(name,type,desc,new GeoPoint(lat,lon));
                if(upload)
                {
                    MyUploadTask u=new MyUploadTask();
                    u.execute(name,type,desc,String.valueOf(lat),String.valueOf(lon));
                }

                items.addItem(p);
                mv.getOverlays().add(items);
            }
            returned=true;
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
                    pw.println(pois[0].getItem(i).getUid()+","+pois[0].getItem(i).getTitle()+","+pois[0].getItem(i).getSnippet()+","+pois[0].getItem(i).getPoint().getLongitude()+","+pois[0].getItem(i).getPoint().getLatitude());
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



        @Override
        protected void onPostExecute(Boolean s)
        {
            super.onPostExecute(s);
            if(s)
            {
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Not saved!", Toast.LENGTH_SHORT).show();
            }

        }

    }

   class MyDownloadTask extends AsyncTask<Void,Void,ItemizedIconOverlay<OverlayItem>>
    {
        public ItemizedIconOverlay<OverlayItem> doInBackground(Void... unused)
        {
            HttpURLConnection conn = null;
            try
            {
                URL url = new URL("http://www.free-map.org.uk/course/mad/ws/get.php?year=17&username=user029&format=csv");
                conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();

                if(conn.getResponseCode() == 200)
                {

                    items = new ItemizedIconOverlay<OverlayItem>(MainActivity.this, new ArrayList<OverlayItem>(), null);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        String[] components = line.split(",");
                        if(components.length==5)
                        {
                            OverlayItem item=new OverlayItem(components[0],components[1],components[2],new GeoPoint(Double.parseDouble(components[4]),Double.parseDouble(components[3])));
                            items.addItem(item);
                        }
                    }

                    return items;
                }
                else
                    return null;


            }
            catch(IOException e)
            {
                return null;
            }
            finally
            {
                if(conn!=null)
                    conn.disconnect();
            }
        }

        public void onPostExecute(ItemizedIconOverlay<OverlayItem> items)
        {
            mv.getOverlays().add(items);
        }
    }

    class MyUploadTask extends AsyncTask<String,Void,String>
    {
        public String doInBackground(String... strings)
        {
            HttpURLConnection conn = null;
            try
            {
                URL url = new URL("http://www.free-map.org.uk/course/mad/ws/add.php");
                conn = (HttpURLConnection) url.openConnection();

                String postData = "username=user029&name="+strings[0]+"&type="+strings[1]+"&description="+strings[2]+"&lat="+strings[3]+"&lon="+strings[4]+"&year=17";
                // For POST
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(postData.length());

                OutputStream out = null;
                out = conn.getOutputStream();
                out.write(postData.getBytes());
                if(conn.getResponseCode() == 200)
                {
                    InputStream in = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String all = "", line;
                    while((line = br.readLine()) !=null)
                        all += line;
                    return all;
                }
                else
                    return "HTTP ERROR: " + conn.getResponseCode();
            }
            catch(IOException e)
            {
                return e.toString();
            }
            finally
            {
                if(conn!=null)
                    conn.disconnect();
            }
        }

        public void onPostExecute(String result)
        {

            new AlertDialog.Builder(MainActivity.this).
                    setMessage("Server sent back: " + result).
                    setPositiveButton("OK", null).show();
        }
    }
}
