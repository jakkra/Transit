package se.jakobkrantz.transit.app.reporting;/*
 * Created by krantz on 14-12-10.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class ResponseBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Brodcastreceiver", "Received packet");
        ComponentName componentName = new ComponentName(context.getPackageName(), MessageIntentService.class.getName());
        //start service, device will be awake while launching
        startWakefulService(context, intent.setComponent(componentName));
        setResultCode(Activity.RESULT_OK);
    }
}
