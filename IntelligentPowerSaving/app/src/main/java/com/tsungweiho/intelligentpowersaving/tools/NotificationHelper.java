package com.tsungweiho.intelligentpowersaving.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;

import java.lang.ref.WeakReference;

/**
 * Created by AKiniyalocts on 1/15/15.
 * Modified by Tsung Wei Ho on 3/1/17.
 * This class is just created to help with notifications, definitely not necessary.
 */
public class NotificationHelper {
    public final static String TAG = "NotificationHelper";

    private WeakReference<Context> context;


    public NotificationHelper(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void createUploadingNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle(context.get().getString(R.string.notification_progress));


        mBuilder.setColor(context.get().getResources().getColor(R.color.colorPrimary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(context.get().getString(R.string.app_name).hashCode(), mBuilder.build());

    }

    public void createUploadedNotification(ImageResponse response) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle(context.get().getString(R.string.notifaction_success));

        mBuilder.setContentText(response.data.link);

        mBuilder.setColor(context.get().getResources().getColor(R.color.colorPrimary));


        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(context.get(), 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.data.link));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(context.get(), 0, shareIntent, 0);
        mBuilder.addAction(new NotificationCompat.Action(R.drawable.abc_ic_menu_share_mtrl_alpha,
                context.get().getString(R.string.notification_share_link), pIntent));

        NotificationManager mNotificationManager =
                (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(context.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }

    public void createFailedUploadNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(context.get().getString(R.string.notification_fail));


        mBuilder.setColor(context.get().getResources().getColor(R.color.colorPrimary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(context.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }

    public void createReceivedMessageNotification() {
        // TODO create service
    }
}
