package com.example.alex.pointsofinterest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;


import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.w3c.dom.Text;


/**
 * Created by Alex on 06-May-17.
 */


public class ListFrag extends ListFragment
{
    String[] entries;
    String[] entryValues;
    ItemizedIconOverlay<OverlayItem> itemsMap;
    ListItemHandler handler;



    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void setListItemHandler(ListItemHandler h) {
        handler=h;
    }

    public void ListUpdate(ItemizedIconOverlay<OverlayItem> items)
    {
        if(items.size()!=0) {
            this.itemsMap = items;
            entries = new String[items.size()];
            entryValues = new String[items.size()];

            for (int i = 0; i < items.size(); i++) {
                entries[i] = items.getItem(i).getUid();
                entryValues[i] = items.getItem(i).getSnippet();
            }
            MyArrayAdapter adapter = new MyArrayAdapter(getActivity(), R.layout.list_layout, entries);
            setListAdapter(adapter);
        }
    }
    public void onListItemClick(ListView lv, View v, int index, long id)
    {
        handler.handleListItemClick(index);
    }

    public class MyArrayAdapter extends ArrayAdapter<String>
    {
        private Context context;
        private String[] objects;

        public MyArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String string = objects[position];

            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.poi_entry, null);

            TextView name = (TextView) view.findViewById(R.id.poi_name);
            name.setText(string);

            TextView desc = (TextView) view.findViewById(R.id.poi_descr);
            desc.setText(entryValues[position]);

            return view;
        }

    }



}