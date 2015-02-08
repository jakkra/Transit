package se.jakobkrantz.transit.app.reporting;/*
 * Created by krantz on 14-12-10.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.app.utils.GcmConstants;

public class MessageIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_MESSAGE_INTENT_SERVICE = "action_messIntent";
    public static final String REGISTRATION_SUCCESSFUL = "reggSuccessful";

    public static final String ACTION_DISTRUBANCE_RECEIVED = "action_messIntent";
    public static final String DISTURBANCE_EXTRAS = "reggSuccessful";
    private static final String ACK_RECEIVED = "ackReceived";

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

                try {
                    if (extras.getString(GcmConstants.ACTION).equals(GcmConstants.ACTION_REGISTER_SUCCESSFUL)) {
                        notifySuccessfulRegistration();
                    } else if (extras.getString(GcmConstants.ACTION).equals(GcmConstants.ACTION_REPORT_DISTURBANCE)) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        //TODO Shouldn't be done here, prefs should unregister from gcm, so the app won't receive them at all.
                        boolean shouldNotify = prefs.getBoolean(getResources().getString(R.string.pref_key_accept_not), true);
                        boolean shouldAcceptReport = prefs.getBoolean(getResources().getString(R.string.pref_key_accept_dist_report), true);
                        if (shouldAcceptReport && !shouldNotify) {
                            notifyDisturbanceReport(extras);
                        } else if (shouldAcceptReport && shouldNotify) {
                            sendNotification("Mellan " + extras.getString(GcmConstants.DISTURBANCE_FROM_STATION_NAME) + " och " + extras.getString(GcmConstants.DISTURBANCE_TO_STATION_NAME), extras);
                            notifyDisturbanceReport(extras);
                        }
                    } else if (extras.getString(GcmConstants.ACTION).equals(GcmConstants.ACTION_ACK)) {
                        notifyAckReportReceived();
                    }
                } catch (NullPointerException e) {

                }
            } else {
                if (messageType != null) {
                    Log.i("GCM rec unknown message", messageType);
                }
            }

        }
        // Release the wake lock
        ResponseBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void notifyAckReportReceived() {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MESSAGE_INTENT_SERVICE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(ACK_RECEIVED, true);
        intentResponse.putExtra(GcmConstants.ACTION, GcmConstants.ACTION_ACK);

        sendBroadcast(intentResponse);
    }

    private void notifySuccessfulRegistration() {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MESSAGE_INTENT_SERVICE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(GcmConstants.ACTION, GcmConstants.ACTION_REGISTER_SUCCESSFUL);

        intentResponse.putExtra(REGISTRATION_SUCCESSFUL, true);
        sendBroadcast(intentResponse);
    }

    private void notifyDisturbanceReport(Bundle extras) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_DISTRUBANCE_RECEIVED);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(DISTURBANCE_EXTRAS, extras);
        sendBroadcast(intentResponse);
    }

    private void sendNotification(String msg, Bundle data) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, DisturbancesActivity.class);
        intent.putExtras(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.delay_reported))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg).setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true).setVibrate(new long[]{1000, 500, 1000});
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
