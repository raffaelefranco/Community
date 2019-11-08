package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.ArrayList;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class AllOpenedRequestActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private SharedPreferences preferences;
    private ListView list;
    private final String TAG = "Community";
    private ArrayAdapter<String> adapter;
    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_opened_request);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);
        list = (ListView) findViewById(R.id.all_op_req_list);

        new AllOpenedRequestActivity.AllOpenedReqRestTask().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllOpenedRequestActivity.this, ViewOpenedRequestActivity.class);
                intent.putExtra(getResources().getString(R.string.key), questions.get(position));
                startActivity(intent);
            }
        });
    }

    public class AllOpenedReqRestTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();

            String URI = baseURI + "opened_requests";
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.get().getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(jsonResponse, InvalidKeyException.class);
                questions = gson.fromJson(jsonResponse, ArrayList.class);
                if (questions.size() == 0)
                    questions.add(getString(R.string.no_requests));
            } catch (ResourceException | IOException e1) {
                if (org.restlet.data.Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
                    // Unauthorized access
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
            if (res != null) {
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item_requests, questions);
                list.setAdapter(adapter);
                list.setVisibility(View.VISIBLE);
            }
        }
    }
}
