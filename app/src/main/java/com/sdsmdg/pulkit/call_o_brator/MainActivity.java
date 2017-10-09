package com.sdsmdg.pulkit.call_o_brator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
    }
}
