package se.jakobkrantz.transit.app.disturbances.fragments;/*
 * Created by jakkra on 2015-01-25.
 */

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import at.markushi.ui.CircleButton;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.disturbances.DisturbanceAdapter;
import se.jakobkrantz.transit.app.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.app.reporting.MessageIntentService;
import se.jakobkrantz.transit.app.utils.GcmConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DisturbancesFragment extends Fragment {
    private Context context;
    private RecyclerView list;
    private CircleButton reportButton;
    private Bundle data;
    private View.OnClickListener listener;
    private DisturbanceAdapter adapter;
    private DisturbanceBroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        broadcastReceiver = new DisturbanceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MessageIntentService.ACTION_DISTRUBANCE_RECEIVED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disturbances, container, false);
        list = (RecyclerView) view.findViewById(R.id.disturbance_list);
        reportButton = (CircleButton) view.findViewById(R.id.disturbance_button);
        reportButton.setOnClickListener(listener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        Object[] arr = getDisturbances(context).toArray();

        adapter = new DisturbanceAdapter(Arrays.copyOf(arr, arr.length, String[].class));
        list.setAdapter(adapter);
        setHasOptionsMenu(true);

        data = getArguments();

        if (data != null) {
            fillData(data);
        } else {
            if (savedInstanceState != null)
                fillData(savedInstanceState);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(broadcastReceiver);


    }

    private void fillData(Bundle data) {
        Log.e("Filldata","");

        String from = data.getString(GcmConstants.DISTURBANCE_FROM_STATION_NAME);
        String to = data.getString(GcmConstants.DISTURBANCE_TO_STATION_NAME);
        String type = data.getString(GcmConstants.DISTURBANCE_TYPE);
        String delay = data.getString(GcmConstants.DISTURBANCE_APPROX_MINS);
        String note = data.getString(GcmConstants.DISTURBANCE_NOTE);
        String reportTime = data.getString(GcmConstants.DISTURBANCE_REPORT_TIME);
        String reportTimeMillis = data.getString(GcmConstants.DISTURBANCE_REPORT_TIME_MILLIS);


        String info = "Försening mellan " + from + " och " + to + "\n"
                + "Typ: " + type + "\n"
                + "Approximerad försening: " + delay + "\n"
                + "Notering: " + note + "\n"
                + "Tid för rapporteting: " + reportTime;
        Object[] arr = storeDisturbance(context, info).toArray();
        adapter.updateDisturbances(Arrays.copyOf(arr, arr.length, String[].class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && data != null) {
            outState.putAll(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d("Activity create", savedInstanceState.toString());

        } else {
            if (data != null) {
                // fillData(data);
            } else {
                //textView.setText("Inga rapporteringar");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (View.OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnClickListener");
        }
    }

    private Set<String> storeDisturbance(Context context, String text) {
        final SharedPreferences prefs = getDisturbancePrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = prefs.getStringSet("distSet", new HashSet<String>());
        Set<String> updatedSet = new HashSet<String>(set);
        updatedSet.add(text);
        editor.putStringSet("distSet", updatedSet);
        editor.commit();
        return updatedSet;
    }

    private void clearDisturbances(Context context) {
        final SharedPreferences prefs = getDisturbancePrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("distSet");
        editor.commit();

    }

    private Set<String> getDisturbances(Context context) {
        final SharedPreferences prefs = getDisturbancePrefs(context);
        Set<String> set = prefs.getStringSet("distSet", new HashSet<String>());
        if (set.isEmpty()) {
            return new HashSet<String>();
        }
        return set;
    }


    private SharedPreferences getDisturbancePrefs(Context context) {
        return context.getSharedPreferences(DisturbancesActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_disturbance, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_disturbances:
                adapter.clearAll();
                clearDisturbances(context);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DisturbanceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Rec","");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean(getResources().getString(R.string.pref_key_accept_dist_report), true)) {
                fillData(intent.getBundleExtra(MessageIntentService.DISTURBANCE_EXTRAS));
                Log.e("Rec","in if");

            }
            Log.e("Rec","after");

        }

    }
}
