package com.example.alex.pointsofinterest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;


/**
 * Created by Alex on 06-May-17.
 */


public class ListFrag extends ListFragment
{
    String[] entries;
    String[] entryValues;
    ItemizedIconOverlay<OverlayItem> itemsMap;

    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void ListUpdate(ItemizedIconOverlay<OverlayItem> items)
    {
        this.itemsMap=items;
        entries=new String[items.size()];
        entryValues=new String[items.size()];

        for(int i=0;i<items.size();i++)
        {
            entries[i]=items.getItem(i).getUid();
            entryValues[i]=items.getItem(i).getSnippet();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, entries);
        setListAdapter(adapter);
    }
    public void onListItemClick(ListView lv, View v, int index, long id)
    {
        MainActivity activity = (MainActivity) getActivity();
        activity.SetCenter(itemsMap.getItem(index).getPoint().getLatitude(),itemsMap.getItem(index).getPoint().getLongitude());
    }

}