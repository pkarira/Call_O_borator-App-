package com.sdsmdg.pulkit.call_o_brator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    public static String phoneNumber;
    public static ITelephony telephonyService;
    public static boolean wasRinging;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.intent = intent;
        mContext = context;
        try {
            if (LoginActivity.loggedIn == true) {
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
                telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                Class c = Class.forName(telephonyManager.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(telephonyManager);
                Bundle bundle = intent.getExtras();
                phoneNumber = bundle.getString("incoming_number");

            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state) {
                case TelephonyManager.CALL_STATE_RINGING:
                String contactName = "";
                if (getContactName(incomingNumber, mContext) != "")
                    contactName = getContactName(incomingNumber, mContext);
                else if (getContactName(incomingNumber.substring(1), mContext) != "")
                    contactName = getContactName(incomingNumber.substring(1), mContext);
                else if (getContactName(incomingNumber.substring(2), mContext) != "")
                    contactName = getContactName(incomingNumber.substring(2), mContext);
                else if (getContactName(incomingNumber.substring(3), mContext) != "")
                    contactName = getContactName(incomingNumber.substring(3), mContext);
                Log.e("contact", contactName);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("number", incomingNumber);
                    jsonObject.put("name", contactName);
                    jsonObject.put("room", LoginActivity.contactNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("contact", "message emitted");
                MainActivity.mSocket.emit("contactInfo", jsonObject);
                wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("state","in answered");
                    if (!wasRinging) {
                        JSONObject stop = new JSONObject();
                        try {
                            stop.put("room", LoginActivity.contactNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MainActivity.mSocket.emit("stop",stop);
                    } else {
                    }
                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("state","in cancelled");
                    if (!wasRinging) {
                        Log.d("state","stop");
                        JSONObject stop = new JSONObject();
                        try {
                            stop.put("room", LoginActivity.contactNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MainActivity.mSocket.emit("stop",stop);
                    } else {
                    }
                    wasRinging = false;
                    break;
            }
        }
    }

    public static void rejectCall() {
        if ((phoneNumber != null))
            telephonyService.endCall();
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }
}
