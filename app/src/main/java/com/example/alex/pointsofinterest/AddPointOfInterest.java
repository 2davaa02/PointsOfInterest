package com.example.alex.pointsofinterest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPointOfInterest extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point_of_interest);


        Button b=(Button)findViewById(R.id.savepoi);
        b.setOnClickListener(this);

    }


    public void onClick (View v)throws NullPointerException
    {
        Intent intent = new Intent();
        Bundle bundle=new Bundle();

        String[] s=new String[3];

        try{
            s[0]=((EditText)findViewById(R.id.POI_name)).getText().toString();
            s[1]=((EditText)findViewById(R.id.POI_type)).getText().toString();
            s[2]=((EditText)findViewById(R.id.POI_desc)).getText().toString();
        }
        catch(NullPointerException  e){
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        }

        if(!s[0].isEmpty()&&!s[1].isEmpty()&&!s[2].isEmpty())
        {
            bundle.putString("com.example.newPoi_name", s[0]);
            bundle.putString("com.example.newPoi_type", s[1]);
            bundle.putString("com.example.newPoi_desc", s[2]);

            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        }
    }
}
