package com.tsungweiho.intelligentpowersaving.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;

import java.lang.ref.WeakReference;

/**
 * Created by AKiniyalocts on 1/15/15.
 * Modified by Tsung Wei Ho on 11/10/17.
 */
public class NotificationHelper {
    private final String TAG = "NotificationHelper";

    private WeakReference<Context> context;
    private NotificationManager notificationMgr;

    private static final NotificationHelper ourInstance = new NotificationHelper();

    public static NotificationHelper getInstance() {
        return ourInstance;
    }

    private NotificationHelper() {
        this.context = new WeakReference<>(MainActivity.getContext());
        notificationMgr = (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private NotificationCompat.Builder getBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.get());
        builder.setColor(context.get().getResources().getColor(R.color.colorPrimary));
        builder.setAutoCancel(true);

        return builder;
    }

    void createUploadingNotification() {
        NotificationCompat.Builder builder = getBuilder();
        builder.setSmallIcon(android.R.drawable.ic_menu_upload);
        builder.setContentTitle(context.get().getString(R.string.notification_progress));

        NotificationManager mNotificationManager =
                (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(context.get().getString(R.string.app_name).hashCode(), builder.build());

    }

    void createUploadedNotification(ImageResponse response) {
        NotificationCompat.Builder builder = getBuilder();
        builder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        builder.setContentTitle(context.get().getString(R.string.notifaction_success));
        builder.setContentText(response.data.link);


        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(context.get(), 0, resultIntent, 0);
        builder.setContentIntent(intent);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.data.link));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(context.get(), 0, shareIntent, 0);
        builder.addAction(new NotificationCompat.Action(R.drawable.abc_ic_menu_share_mtrl_alpha,
                context.get().getString(R.string.notification_share_link), pIntent));

        notificationMgr.notify(context.get().getString(R.string.app_name).hashCode(), builder.build());
    }

    void createFailedUploadNotification() {
        NotificationCompat.Builder builder = getBuilder();
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle(context.get().getString(R.string.notification_fail));

        notificationMgr.notify(context.get().getString(R.string.app_name).hashCode(), builder.build());
    }

    public void createReceivedMessageNotification() {
        // TODO create service
    }
}
