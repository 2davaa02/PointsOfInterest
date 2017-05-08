package com.example.alex.pointsofinterest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 06-May-17.
 */
public class MapFragment extends Fragment {

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));

        mv = (MapView) activity.findViewById(R.id.map1);

        mv.getController().setZoom(16);
        mv.getController().setCenter(new GeoPoint(50.9097, -1.4044));


        mv.setBuiltInZoomControls(true);

        items = new ItemizedIconOverlay<OverlayItem>(activity, new ArrayList<OverlayItem>(), null);
        mv.getOverlays().add(items);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, parent);
    }

    public void SetLocation(double lat, double lon) {
        mv.getController().setCenter(new GeoPoint(lat, lon));
        mv.getController().setZoom(18);
    }

    public void Save() {
        new MySaveTask().execute(items);
    }

    class MySaveTask extends AsyncTask<ItemizedIconOverlay<OverlayItem>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ItemizedIconOverlay<OverlayItem>... pois) {
            try {
                String fname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/poi.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(fname));


                for (int i = 0; i < pois[0].size(); i++) {
                    pw.println(pois[0].getItem(i).getUid() + "," + pois[0].getItem(i).getTitle() + "," + pois[0].getItem(i).getSnippet() + "," + pois[0].getItem(i).getPoint().getLongitude() + "," + pois[0].getItem(i).getPoint().getLatitude());
                }
                pw.close();
                return true;
            } catch (IOException e) {
                System.out.println("I/O Error: " + e);
                return false;
            }
        }

    }


    public double getLat()
    {
        return mv.getMapCenter().getLatitude();
    }

    public double getLon()
    {
        return mv.getMapCenter().getLongitude();
    }
    public void addMark(OverlayItem p)
    {
        items.addItem(p);
        mv.getOverlays().add(items);
        MainActivity activity = (MainActivity) getActivity();
        activity.updateItems(items);
    }

    public void download()
    {
        MyDownloadTask t=new MyDownloadTask();
        t.execute();
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

                    items = new ItemizedIconOverlay<OverlayItem>(getActivity(), new ArrayList<OverlayItem>(), null);
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
            MainActivity activity = (MainActivity) getActivity();
            activity.updateItems(items);
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

            new AlertDialog.Builder(getActivity()).
                    setMessage("Server sent back: " + result).
                    setPositiveButton("OK", null).show();
        }
    }

    public void upload(String name,String type,String desc,double lat,double lon)
    {
        MyUploadTask u=new MyUploadTask();
        u.execute(name,type,desc,String.valueOf(lat),String.valueOf(lon));
    }
}
