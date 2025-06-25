package com.example.peacemind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IP_page extends AppCompatActivity {
    EditText e1;
    Button b;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_page);
        e1=findViewById(R.id.ip_input);
        b=findViewById(R.id.enter_button);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e1.setText(sh.getString("ip",""));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipaddress=e1.getText().toString();
                int flag=0;
                if (ipaddress.equalsIgnoreCase("")){
                    e1.setError("Enter ip");
                    flag++;
                }
                if (flag==0) {
                    String url1 = "http://" + ipaddress + ":4000/";
                    SharedPreferences.Editor ed = sh.edit();
                    ed.putString("ip", ipaddress);
                    ed.putString("url", url1);
                    ed.commit();
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);
                }
            }
        });
    }
}