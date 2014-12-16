package se.jakobkrantz.transit.app.reporting.fragments;/*
 * Created by krantz on 14-12-16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.utils.GcmConstants;
import se.jakobkrantz.transit.app.reporting.ReportActivity;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportFragment extends Fragment implements View.OnClickListener{
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "0";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "24223089278";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;

    private Button sendButton;
    private TextView messageToSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_report, container, false);
        sendButton = (Button) view.findViewById(R.id.gcmButton);
        messageToSend = (TextView) view.findViewById(R.id.gcmTextSend);
        sendButton.setOnClickListener(this);
        context = getActivity();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            Log.i("gcm", "play servicees OK");
            gcm = GoogleCloudMessaging.getInstance(getActivity());
            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                Log.i("gcm", "new user, reg");

                registerInBackground();
            }
        } else {
            Log.i("GCM ReportActivity", "No valid Google Play Services APK found.");
        }
        return view;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(SENDER_ID);
                    String id = UUID.randomUUID().toString();
                    Bundle data = new Bundle();
                    data.putString(GcmConstants.ACTION, GcmConstants.ACTION_REGISTER);
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Device registered, registration ID=" + regid;

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("GCM ReportActivity msg: ", msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GCM Activity", "This device is not supported. Please add play services");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("GCM ReportActivity", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCM ReportActivity", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GCM ReportActivity", "App version changed. Need to re register");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(ReportActivity.class.getSimpleName(),Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg;
                try {
                    Bundle data = new Bundle();
                    data.putString(GcmConstants.ACTION, GcmConstants.ACTION_SET_INTERESTING_LOCATIONS);
                    data.putString("CITY", "Stockholm");
                    data.putString(GcmConstants.ACTION_SET_INTERESTING_LOCATIONS, "Testing from client");
                    String id = UUID.randomUUID().toString();
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("GCM ReportActivity ", msg);
            }
        }.execute(null, null, null);

    }
}
