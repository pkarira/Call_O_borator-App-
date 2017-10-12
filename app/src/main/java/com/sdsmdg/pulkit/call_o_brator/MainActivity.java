package com.sdsmdg.pulkit.call_o_brator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    public static Socket mSocket;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send=(Button)findViewById(R.id.button);
        try {
            mSocket = IO.socket("http://10.42.0.1:8080");
        } catch (URISyntaxException e) {
            Log.e("error","error");
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on("reject", onReject);
        mSocket.on("pick", onPick);
        mSocket.on("connect", onConnect);
    }

    private Emitter.Listener onReject = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        IncomingCall.rejectCall();
                        message = data.getString("message");
                        Toast.makeText(getApplication(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener onPick = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        IncomingCall.pickCall();
                        message = data.getString("message");
                        Toast.makeText(getApplication(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener onConnect= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("room", LoginActivity.contactNumber);
                }
            });
        }
    };
}