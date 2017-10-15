package com.sdsmdg.pulkit.call_o_brator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public static String contactNumber;
    public static boolean loggedIn=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.button2);
        final EditText contact = (EditText) findViewById(R.id.editText);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = contact.getText().toString();
                if (number.equals(""))
                    contact.setError("Please Enter Contact Number");
                else {
                    loggedIn=true;
                    contactNumber = number;
                    Intent next =new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(next);
                }
            }
        });
    }
}
