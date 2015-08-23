package com.ibplan.michaelho.com.ibplan.michaelho.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.GCMConfig;
import com.ibplan.michaelho.com.ibplan.michaelho.fragments.MessageFragment;
import com.ibplan.michaelho.ibplan.MainActivity;
import com.ibplan.michaelho.ibplan.R;
import com.ibplan.michaelho.ibplan.RegisterActivity;

import java.util.Date;

/**
 * Created by MichaelHo on 2015/5/11.
 */
public class GCMIntentService extends IntentService implements GCMConfig {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCM Demo";

    public GCMIntentService()
    {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //解析收到的文字訊息再傳入sendNotification進行顯示
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!bundle.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
                sendNotification(bundle);
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                sendNotification(bundle);
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                for (int i = 0; i < 5; i++)
                {
                    Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());

                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification(bundle);
                Log.i(TAG, "Received: " + bundle.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    //設定Notification要顯示的資訊與點擊Notification要開啟的頁面
    private void sendNotification(Bundle bundle)
    {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String msg = bundle.getString("message");
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("MESSAGE", msg);
        intent.putExtras(b);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent , 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ibplan_logo)
                        .setContentTitle("Intelligent Buildings")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
