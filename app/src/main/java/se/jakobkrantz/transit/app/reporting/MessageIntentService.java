package se.jakobkrantz.transit.app.reporting;/*
 * Created by krantz on 14-12-10.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.app.searching.SearchActivity;
import se.jakobkrantz.transit.app.utils.GcmConstants;

public class MessageIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public MessageIntentService() {
        super("MessageIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("OnHandelIntent", "Received from server");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e("GCM error", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("GCM message delete type", extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("GCM received: ", extras.toString());
                Log.i("GCM -::-", extras.getString("distFromName"));
                //TODO Stuff

                sendNotification("Mellan " + extras.getString(GcmConstants.DISTURBANCE_FROM_STATION_NAME) + " och " + extras.getString(GcmConstants.DISTURBANCE_TO_STATION_NAME), extras);
            } else {
                if (messageType != null) {
                    Log.e("GCM rec unknown message", messageType);
                }
            }
        }
        // Release the wake lock
        ResponseBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, Bundle data) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, DisturbancesActivity.class);
        intent.putExtras(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Log.e("SendNot", data.getString(GcmConstants.DISTURBANCE_FROM_STATION_NAME));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.delay_reported))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg).setSmallIcon(R.drawable.ic_launcher);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
