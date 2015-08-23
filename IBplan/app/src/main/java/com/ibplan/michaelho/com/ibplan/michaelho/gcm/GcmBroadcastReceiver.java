package com.ibplan.michaelho.com.ibplan.michaelho.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.GCMConfig;

/**
 * WakefulBroadcastReceiver Helper for the common pattern of implementing a
 * BroadcastReceiver that receives a device wakeup event and then passes the
 * work off to a Service, while ensuring that the device does not go back to
 * sleep during the transition. This class takes care of creating and managing a
 * partial wake lock for you; you must request the WAKE_LOCK permission to use
 * it.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver implements GCMConfig{

    @Override
	public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
	}
}
