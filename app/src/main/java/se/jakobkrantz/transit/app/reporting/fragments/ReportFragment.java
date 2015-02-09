package se.jakobkrantz.transit.app.reporting.fragments;

/*
 * Created by krantz on 14-12-16.
 */

import android.app.Activity;
import android.content.*;
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
import se.jakobkrantz.transit.app.reporting.MessageIntentService;
import se.jakobkrantz.transit.app.reporting.ReportActivity;
import se.jakobkrantz.transit.app.utils.BundleConstants;
import se.jakobkrantz.transit.app.utils.GcmConstants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import static se.jakobkrantz.transit.app.R.id.gcmButton;

public class ReportFragment extends Fragment implements View.OnClickListener {
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "0";
    private static final String REGISTRATION_SUCCESSFUL = "regSucc";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "24223089278";

    private GoogleCloudMessaging gcm;
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

    private RegistrationBroadcastReceiver broadcastReceiver;
    private boolean ackReceived;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        broadcastReceiver = new RegistrationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MessageIntentService.ACTION_MESSAGE_INTENT_SERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (checkPlayServices()) {
            Log.i("GCM", "Play Services OK");
            gcm = GoogleCloudMessaging.getInstance(context);

            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                unregisterDevice(regid);
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
                    registerOnBackend();
                    Log.e("GCM ReportFragment register", "Device registered, registration ID=" + regid);
                    msg = "";
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    Log.e("GCM ReportFragment register", "Error :" + ex.getMessage());
                    msg = "GCM Registration failed, please try again later";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!msg.isEmpty()) {
                    Log.i("GCM ReportFragment msg: ", msg);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }

            }
        }.execute(null, null, null);
    }

    private void registerOnBackend() throws IOException {
        String id = UUID.randomUUID().toString(); //Should be changed from just a random.
        Bundle data = new Bundle();
        data.putString(GcmConstants.ACTION, GcmConstants.ACTION_REGISTER);
        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
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
                Log.i("GCM", "This device is not supported. Please add play services");
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
     * @return true if backend server has received application's GCM Registration ID
     */
    private boolean wasRegToBackendSuccessful() {
        final SharedPreferences prefs = getGcmPreferences(context);
        return prefs.getBoolean(REGISTRATION_SUCCESSFUL, false);
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

    private void unregisterDevice(String regId) {
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
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (!success) {
                    Log.i("ReportFragment unregister", "Unregister failed");
                }
            }
        }.execute(regId, null, null);
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
                    if (wasRegToBackendSuccessful()) {
                        System.out.println("Backend successful refgister");
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
                                return "Error, must fill both from and to field";
                            }
                            dataToSend.putString(GcmConstants.ACTION, GcmConstants.ACTION_REPORT_DISTURBANCE);
                            dataToSend.putString(GcmConstants.DISTURBANCE_FROM_STATION_NBR, fromNbr);
                            dataToSend.putString(GcmConstants.DISTURBANCE_TO_STATION_NBR, toNbr);
                            dataToSend.putString(GcmConstants.DISTURBANCE_FROM_STATION_NAME, fromStation.getText().toString());
                            dataToSend.putString(GcmConstants.DISTURBANCE_TO_STATION_NAME, toStation.getText().toString());
                            dataToSend.putString(GcmConstants.DISTURBANCE_APPROX_MINS, Integer.toString(minutePicker.getValue()));
                            dataToSend.putString(GcmConstants.DISTURBANCE_TYPE, disturbanceType.getSelectedItem().toString());
                            dataToSend.putString(GcmConstants.DISTURBANCE_REPORT_TIME, new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(Calendar.getInstance().getTime()));
                            dataToSend.putString(GcmConstants.DISTURBANCE_REPORT_TIME_MILLIS, Long.toString(System.currentTimeMillis()));

                            if (!disturbanceNote.getText().equals("")) {
                                dataToSend.putString(GcmConstants.DISTURBANCE_NOTE, disturbanceNote.getText().toString());
                            }
                            String id = UUID.randomUUID().toString();
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, dataToSend);
                            waitForAck(5000);
                            msg = "Report send";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                        return msg;
                    } else {
                        try {
                            System.out.println("RegOnBackend");
                            registerOnBackend();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String msg = "Not registered to backend, trying again. Please try to resend the report later";
                        return msg;
                    }
                }

                @Override
                protected void onPostExecute(String msg) {
                    Log.i("GCM ReportActivity ", msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }.execute(null, null, null);
        }
    }

    /**
     * @param millis time to wait for ack from server
     */
    private void waitForAck(final long millis) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (!ackReceived) {
                    Toast.makeText(context, "Backend server offline, try again later", Toast.LENGTH_LONG).show();
                }
                ackReceived = false;
            }
        }.execute();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastReceiver);
    }

    public class RegistrationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(GcmConstants.ACTION);
            if (action.equals(GcmConstants.ACTION_REGISTER_SUCCESSFUL)) {
                final SharedPreferences prefs = getGcmPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(REGISTRATION_SUCCESSFUL, true);
                editor.commit();
            } else if (action.equals(GcmConstants.ACTION_ACK)) {
                ackReceived = true;
            }
        }

    }
}
