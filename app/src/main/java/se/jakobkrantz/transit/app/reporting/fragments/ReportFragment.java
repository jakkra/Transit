package se.jakobkrantz.transit.app.reporting.fragments;

/*
 * Created by krantz on 14-12-16.
 */

import android.app.Activity;
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
import android.widget.*;
import at.markushi.ui.CircleButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.base.FragmentEventListener;
import se.jakobkrantz.transit.app.reporting.ReportActivity;
import se.jakobkrantz.transit.app.utils.BundleConstants;
import se.jakobkrantz.transit.app.utils.GcmConstants;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static se.jakobkrantz.transit.app.R.id.gcmButton;

public class ReportFragment extends Fragment implements View.OnClickListener {
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "24223089278";

    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private Context context;

    private String regid;

    private CircleButton sendButton;
    private Spinner disturbanceType;
    private EditText disturbanceNote;
    private NumberPicker minutePicker;
    private TextView fromStation;
    private TextView toStation;
    private FragmentEventListener eventListener;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (checkPlayServices()) {
            Log.i("gcm", "play services OK");
            context = getActivity();
            gcm = GoogleCloudMessaging.getInstance(context);

            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                Log.i("gcm", "new user, reg");
                registerInBackground();
            }

            View view = inflater.inflate(R.layout.fragment_report, container, false);
            sendButton = (CircleButton) view.findViewById(gcmButton);
            disturbanceType = (Spinner) view.findViewById(R.id.spinner);
            disturbanceNote = (EditText) view.findViewById(R.id.disturbance_note);
            fromStation = (TextView) view.findViewById(R.id.report_from_station);
            toStation = (TextView) view.findViewById(R.id.report_to_station);
            fromStation.setOnClickListener(this);
            toStation.setOnClickListener(this);

            minutePicker = (NumberPicker) view.findViewById(R.id.minutePicker);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(60);
            minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.disturbance_types, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            disturbanceType.setAdapter(spinnerAdapter);


            sendButton.setOnClickListener(this);

            fillStationsText(getArguments());

            return view;

        } else {
            Log.i("GCM ReportActivity", "No valid Google Play Services APK found.");
            return inflater.inflate(R.layout.fragment_no_play_services, container, false);
        }
    }

    private void fillStationsText(Bundle args) {
        if (args != null) {
            String from = args.getString(BundleConstants.FROM_STATION);
            String to = args.getString(BundleConstants.TO_STATION);
            if (from != null) fromStation.setText(from);
            if (to != null) toStation.setText(to);
        }
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
                    Toast.makeText(getActivity(), "GCM Registration failed", Toast.LENGTH_LONG);
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
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
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
            unregisterDevice(regid);
            registerInBackground();
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
        return context.getSharedPreferences(ReportActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void unregisterDevice(String regid) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                Bundle dataToSend = new Bundle();
                dataToSend.putString(GcmConstants.ACTION, GcmConstants.ACTION_UNREGISTER);

                try {
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", params[0], dataToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (!aBoolean) {
                    Log.e("ReportFragment unregister", "Unregister failed");
                }
            }
        }.execute(regid, null, null);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.report_from_station) {
            Bundle args = getArguments();
            if (args == null) {
                args = new Bundle();
            }
            args.putString(BundleConstants.SOURCE, BundleConstants.SOURCE_FROM_STATION);
            eventListener.onEvent(BaseActivity.FragmentTypes.SEARCH_STATION, args);
        } else if (v.getId() == R.id.report_to_station) {
            Bundle args = getArguments();
            if (args == null) {
                args = new Bundle();
            }
            args.putString(BundleConstants.SOURCE, BundleConstants.SOURCE_TO_STATION);
            eventListener.onEvent(BaseActivity.FragmentTypes.SEARCH_STATION, args);
        } else if (v.getId() == R.id.gcmButton) {

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Bundle args = getArguments();

                    String msg;
                    try {
                        Bundle dataToSend = new Bundle();
                        String fromNbr = null;
                        String toNbr = null;
                        if (args != null) {
                            fromNbr = args.getString(BundleConstants.FROM_STATION_ID);
                            toNbr = args.getString(BundleConstants.TO_STATION_ID);
                        }
                        if (fromNbr == null || toNbr == null) {
                            Toast.makeText(getActivity(), "Must fill both from and to station", Toast.LENGTH_SHORT).show();
                            return "error, must fill both from and to field";
                        }


                        dataToSend.putString(GcmConstants.ACTION, GcmConstants.ACTION_REPORT_DISTURBANCE);
                        dataToSend.putString(GcmConstants.DISTURBANCE_FROM_STATION_NBR, fromNbr);
                        dataToSend.putString(GcmConstants.DISTURBANCE_TO_STATION_NBR, toNbr);
                        dataToSend.putString(GcmConstants.DISTURBANCE_FROM_STATION_NAME, fromStation.getText().toString());
                        dataToSend.putString(GcmConstants.DISTURBANCE_TO_STATION_NAME, toStation.getText().toString());
                        dataToSend.putString(GcmConstants.DISTURBANCE_APPROX_MINS, Integer.toString(minutePicker.getValue()));
                        dataToSend.putString(GcmConstants.DISTURBANCE_TYPE, disturbanceType.getSelectedItem().toString());

                        if (!disturbanceNote.getText().equals("")) {
                            dataToSend.putString(GcmConstants.DISTURBANCE_NOTE, disturbanceNote.getText().toString());
                        }
                        String id = UUID.randomUUID().toString();
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, dataToSend);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            eventListener = (FragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StationSelectedListener");
        }
    }
}
