package com.sdsmdg.pulkit.call_o_brator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by pulkit on 18/10/17.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationCall extends NotificationListenerService {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    static StatusBarNotification mysbn;
    Context context;

    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        mysbn = sbn;
        try {

            String packageName = sbn.getPackageName();
            Intent intent = new Intent("Msg");
            intent.putExtra("package", packageName);
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
