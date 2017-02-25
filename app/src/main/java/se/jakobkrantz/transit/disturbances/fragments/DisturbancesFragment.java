package se.jakobkrantz.transit.disturbances.fragments;/*
 * Created by jakkra on 2015-01-25.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.disturbances.DisturbanceAdapter;
import se.jakobkrantz.transit.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.reporting.MessageIntentService;

public class DisturbancesFragment extends Fragment {
    private Context context;
    private RecyclerView list;
    private FloatingActionButton reportButton;
    private Bundle data;
    private View.OnClickListener listener;
    private DisturbanceAdapter adapter;
    private DisturbanceBroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        broadcastReceiver = new DisturbanceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MessageIntentService.ACTION_DISTURBANCE_RECEIVED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disturbances, container, false);
        list = (RecyclerView) view.findViewById(R.id.disturbance_list);
        reportButton = (FloatingActionButton) view.findViewById(R.id.disturbance_button);
        reportButton.setOnClickListener(listener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        Object[] arr = getDisturbances(context).toArray();

        adapter = new DisturbanceAdapter(Arrays.copyOf(arr, arr.length, String[].class));
        list.setAdapter(adapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && data != null) {
            outState.putAll(data);
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean(getResources().getString(R.string.pref_key_accept_dist_report), true)) {
                Object[] arr = getDisturbances(context).toArray();
                adapter.updateDisturbances(Arrays.copyOf(arr, arr.length, String[].class));
            }

        }

    }
}
