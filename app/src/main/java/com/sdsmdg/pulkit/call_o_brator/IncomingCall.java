package com.sdsmdg.pulkit.call_o_brator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pulkit on 9/10/17.
 */

public class IncomingCall extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d("MyPhoneListener", state + "   incoming no:" + incomingNumber);
            if (state == 1) {
                String msg = "New Phone Call Event. Incomming Number : " + incomingNumber;
                int duration = Toast.LENGTH_LONG;
               Toast.makeText(mContext, msg, duration).show();
            }
        }
    }
}
