package com.sdsmdg.pulkit.call_o_brator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
       // send=(Button)findViewById(R.id.button);
        try {
            mSocket = IO.socket("https://sync-call.herokuapp.com/");
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
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        message = data.getString("message");
                        Toast.makeText(getApplication(),message,Toast.LENGTH_LONG).show();
                        for (MediaController mediaController : ((MediaSessionManager) getApplicationContext().getSystemService(Context.MEDIA_SESSION_SERVICE)).getActiveSessions(new ComponentName(getApplicationContext(), NotificationCall.class))) {
                            if ("com.android.server.telecom".equals(mediaController.getPackageName())) {
                                mediaController.dispatchMediaButtonEvent(new KeyEvent(1, 79));
                                return;
                            }
                        }
                        /*Intent intent = new Intent(MainActivity.this, AcceptCallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        MainActivity.this.startActivity(intent);*/
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("room", LoginActivity.contactNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.mSocket.emit("logout", jsonObject);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}