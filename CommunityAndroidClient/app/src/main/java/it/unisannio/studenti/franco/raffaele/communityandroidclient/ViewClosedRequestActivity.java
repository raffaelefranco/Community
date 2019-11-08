package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Item;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Question;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Response;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;


public class ViewClosedRequestActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private SharedPreferences preferences;
    private ListView list;
    private final String TAG = "Community";
    private Question question;
    private ArrayList<Item> responses;
    private TextView title;
    private TextView text;
    private CustomAdapter adapter;
    private String param;
    private Set<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_closed_request);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        title = (TextView) findViewById(R.id.textView_title1);
        text = (TextView) findViewById(R.id.textView_text1);
        list = (ListView) findViewById(R.id.response_list1);

        Intent fromCaller = getIntent();
        param = fromCaller.getStringExtra(getResources().getString(R.string.key));

        new ViewClosedRequestActivity.GetClosedReqRestTask().execute(param);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewClosedRequestActivity.this, ViewResponseActivity.class);
                intent.putExtra(getResources().getString(R.string.key_response), responses.get(position).getDescription());
                startActivity(intent);
            }
        });
    }

    public class GetClosedReqRestTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            String URI = baseURI + "closed_requests/" + username + "/" + params[0];
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.get().getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(jsonResponse, InvalidKeyException.class);
                question = gson.fromJson(jsonResponse, Question.class);
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
                title.setText(question.getTitle().toString());
                text.setText(question.getText().toString());

                responses = new ArrayList<Item>();

                for (Response r : question.getResponses())
                    responses.add(new Item(r.getUser(), r.getText()));
                if (responses.size() == 0) {
                    responses.add(new Item(getString(R.string.no_responses_available), ""));
                }
                adapter = new CustomAdapter(getApplicationContext(), R.layout.list_item_responses, responses);
                list.setAdapter(adapter);
            }
        }

    }

    public class CustomAdapter extends ArrayAdapter<Item> {

        public CustomAdapter(Context context, int textViewResourceId, List<Item> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_responses, null);

            TextView activity = (TextView) convertView.findViewById(R.id.username_resp);
            TextView description = (TextView) convertView.findViewById(R.id.response);

            Item item = getItem(position);

            activity.setText(item.getActivity());
            description.setText(item.getDescription());

            return convertView;
        }
    }

}
