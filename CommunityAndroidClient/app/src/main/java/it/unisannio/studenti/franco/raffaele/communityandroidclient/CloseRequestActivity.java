package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;


public class CloseRequestActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private SharedPreferences preferences;
    private Set<String> titles;
    private AutoCompleteTextView title;
    private final String TAG = "Community";
    private String[] questions;
    private Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_request);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);
        title = (AutoCompleteTextView) findViewById(R.id.title_auto);
        ok = (Button) findViewById(R.id.remove_button);
        Button cancel = (Button) findViewById(R.id.not_remove_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CloseRequestActivity.this, MenuActivity.class);
                startActivity(myIntent);
            }
        });


        if (preferences.contains("titles")) {
            titles = preferences.getStringSet("titles", titles);
            questions = titles.toArray(new String[titles.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_requests, questions);
            title.setAdapter(adapter);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CloseRequestActivity.CloseReqRestTask().execute(title.getText().toString());
                }
            });
        }
    }

    public class CloseReqRestTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            String URI = baseURI + "closed_requests/status/" + username;
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.post(gson.toJson(params[0], String.class)).getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(jsonResponse, InvalidKeyException.class);
            } catch (ResourceException | IOException e1) {
                if (org.restlet.data.Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
                    jsonResponse = "Access unauthorized by the server, check your credentials";
                    Log.e(TAG, jsonResponse);
                } else {
                    jsonResponse = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() +
                            " - " + cr.getStatus().getReasonPhrase();
                    Log.e(TAG, jsonResponse);
                }
            } catch (InvalidKeyException e2) {
                String error2 = "Error: " + cr.getStatus().getCode() + " - " + e2.getMessage();
                Log.e(TAG, error2);
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String res) {

            preferences.getStringSet("titles", titles);
            Snackbar.make(ok, res, Snackbar.LENGTH_LONG).show();

            titles = preferences.getStringSet("titles", titles);

            if (titles == null) {
                titles = new HashSet<>();
            }

            titles.add(String.valueOf(title.getText()));
            SharedPreferences.Editor edit = preferences.edit();

            edit.putStringSet("titles", titles).apply();
        }
    }
}
