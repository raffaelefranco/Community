package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Item;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class MenuActivity extends AppCompatActivity {

    private ListView list;
    private SharedPreferences preferences;
    private static final int ALERTTAG = 0, PROGRESSTAG = 1;

    private static final String TAG = "AlertDialogActivity";

    private DialogFragment mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final List<Item> activities = new LinkedList<Item>();
        activities.add(new Item(getString(R.string.act_new_req), getString(R.string.descr_new_req)));
        activities.add(new Item(getString(R.string.act_opened_req), getString(R.string.descr_opened_req)));
        activities.add(new Item(getString(R.string.act_all_req), getString(R.string.descr_all_req)));
        activities.add(new Item(getString(R.string.act_closed_req), getString(R.string.descr_closed_req)));
        activities.add(new Item(getString(R.string.act_close_req), getString(R.string.descr_close_req)));
        activities.add(new Item(getString(R.string.act_logout), getString(R.string.act_logout)));

        list = (ListView) findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_item, activities);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent myIntent;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (activities.get(position).getActivity().equals(getString(R.string.act_new_req))) {
                    myIntent = new Intent(MenuActivity.this, NewRequestActivity.class);
                    startActivity(myIntent);
                } else if (activities.get(position).getActivity().equals(getString(R.string.act_opened_req))) {
                    myIntent = new Intent(MenuActivity.this, OpenedRequestActivity.class);
                    startActivity(myIntent);
                } else if (activities.get(position).getActivity().equals(getString(R.string.act_all_req))) {
                    myIntent = new Intent(MenuActivity.this, AllOpenedRequestActivity.class);
                    startActivity(myIntent);
                } else if (activities.get(position).getActivity().equals(getString(R.string.act_closed_req))) {
                    myIntent = new Intent(MenuActivity.this, ClosedRequestActivity.class);
                    startActivity(myIntent);
                } else if (activities.get(position).getActivity().equals(getString(R.string.act_close_req))) {
                    myIntent = new Intent(MenuActivity.this, CloseRequestActivity.class);
                    startActivity(myIntent);
                } else if (activities.get(position).getActivity().equals(getString(R.string.act_logout))) {
                    showDialogFragment(ALERTTAG);
                }
            }
        });
    }

    public class CustomAdapter extends ArrayAdapter<Item> {

        public CustomAdapter(Context context, int textViewResourceId, List<Item> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item, null);

            TextView activity = (TextView) convertView.findViewById(R.id.activity);
            TextView description = (TextView) convertView.findViewById(R.id.description);

            Item item = getItem(position);

            activity.setText(item.getActivity());
            description.setText(item.getDescription());

            return convertView;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                showDialogFragment(ALERTTAG);
                return true;
            case R.id.info:
                Intent Intent = new Intent(MenuActivity.this, InfoActivity.class);
                startActivity(Intent);
                return true;
            default:
                return false;
        }
    }

    // Show desired Dialog
    public void showDialogFragment(int dialogID) {
        switch (dialogID) {
            // Show AlertDialog
            case ALERTTAG:
                // Create a new AlertDialogFragment
                mDialog = AlertDialogFragment.newInstance();
                // Show AlertDialogFragment
                mDialog.show(getFragmentManager(), "Alert");
                break;
            // Show ProgressDialog
            case PROGRESSTAG:
                // Create a new ProgressDialogFragment
                mDialog = ProgressDialogFragment.newInstance();
                // Show new ProgressDialogFragment
                mDialog.show(getFragmentManager(), "Logout");
                break;
        }
    }

    // Abort or complete Logout based on value of shouldContinue
    private void continueLogout(boolean shouldContinue) {
        if (shouldContinue) {
            // Prevent further interaction with the ShutDown Butotn
            // Show ProgressDialog as shutdown process begins
            showDialogFragment(PROGRESSTAG);
            // Finish the ShutDown process
            finishLogout();
        } else {
            // Abort ShutDown and dismiss dialog
            mDialog.dismiss();
        }
    }

    private void finishLogout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Pretend to do something before
                    // shutting down
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.toString());
                } finally {
                    preferences = getSharedPreferences(prefName, MODE_PRIVATE);
                    preferences.edit().remove("username").apply();
                    preferences.edit().remove("password").apply();
                    preferences.edit().remove("user").apply();
                    Intent Intent = new Intent(MenuActivity.this, LoginActivity.class);
                    startActivity(Intent);
                }
            }
        }).start();
    }

    // Class that creates the AlertDialog
    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.exit))
                    // User cannot dismiss dialog by hitting back button
                    .setCancelable(false)
                    // Set up No Button
                    .setNegativeButton(getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    ((MenuActivity) getActivity())
                                            .continueLogout(false);
                                }
                            })
                    // Set up Yes Button
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((MenuActivity) getActivity())
                                            .continueLogout(true);
                                }
                            }).create();
        }
    }

    // Class that creates the ProgressDialog
    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }
        // Build ProgressDialog
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Create new ProgressDialog
            final ProgressDialog dialog = new ProgressDialog(getActivity());

            // Set Dialog message
            dialog.setMessage(getString(R.string.loading));

            // Dialog will be displayed for an unknown amount of time
            dialog.setIndeterminate(true);
            return dialog;
        }
    }

}
