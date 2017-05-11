package com.example.alex.pointsofinterest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class ListActivity extends AppCompatActivity implements ListItemHandler {

    ListFrag lf;
    ItemizedIconOverlay<OverlayItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lf=(ListFrag)getFragmentManager().findFragmentById(R.id.listFrag);
        lf.setListItemHandler(this);

        items=MainActivity.GetItems();
        lf.ListUpdate(items);

    }
    public void handleListItemClick(int index)
    {
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("com.example.index",index);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
