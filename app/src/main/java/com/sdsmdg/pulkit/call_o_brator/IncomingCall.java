package com.sdsmdg.pulkit.call_o_brator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by pulkit on 9/10/17.
 */

public class IncomingCall extends BroadcastReceiver {
    Context mContext;
    public static TelephonyManager telephonyManager = null;
    public static Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.intent = intent;
        mContext = context;
        try {
            Log.d("status","call received");
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == 1) {
                Log.d("status","sending message");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("number", "dhjhhsdf");
                    jsonObject.put("name", "djcskjdsk");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.mSocket.emit("contactInfo", jsonObject);
                String msg = "New Phone Call Event. Incomming Number : " + incomingNumber;
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void rejectCall() {
        Log.d("status","in call rejection");
        try {
            Class c = Class.forName(telephonyManager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephonyManager);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");
            Log.d("INCOMING", phoneNumber);
            if ((phoneNumber != null)) {
                telephonyService.endCall();
                Log.d("HANG UP", phoneNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
