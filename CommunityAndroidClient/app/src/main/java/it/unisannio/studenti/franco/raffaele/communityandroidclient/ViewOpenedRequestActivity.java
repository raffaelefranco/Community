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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Item;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Question;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Response;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class ViewOpenedRequestActivity extends AppCompatActivity {

    private final String TAG = "Community";
    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private SharedPreferences preferences;
    private ListView list;
    private Question question;
    private ArrayList<Item> responses;
    private TextView title;
    private TextView text;
    private TextView username_req;
    private CustomAdapter adapter;
    private Button answer;
    private Button close;
    private Button menu;
    private String param;
    private Set<String> titles;
    private TextView score;
    private Button up;
    private Button down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_opened_user_request);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        title = findViewById(R.id.textView_title);
        text = findViewById(R.id.textView_text);
        username_req = findViewById(R.id.textView_username_req);
        list = findViewById(R.id.response_list);
        answer = findViewById(R.id.answer);
        close = findViewById(R.id.close);
        menu = findViewById(R.id.menu_button);
        score = findViewById(R.id.textView_score);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);

        Intent fromCaller = getIntent();
        param = fromCaller.getStringExtra(getResources().getString(R.string.key));

        new ViewOpenedRequestActivity.GetOpenedReqRestTask().execute(param);

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewOpenedRequestActivity.this, AnswerActivity.class);
                intent.putExtra(getResources().getString(R.string.key), param);
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewOpenedRequestActivity.CloseReqRestTask().execute(param);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewOpenedRequestActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewOpenedRequestActivity.this, ViewResponseActivity.class);
                intent.putExtra(getResources().getString(R.string.key_response), responses.get(position).getDescription());
                intent.putExtra(getResources().getString(R.string.key_response1), question.searchResponseByUserEText(responses.get(position).getActivity(), responses.get(position).getDescription()));
                startActivity(intent);
            }

        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewOpenedRequestActivity.ScoreQuestionTask().execute(param, "5");
                int score1 = question.getScore() + 5;
                score.setText(Integer.toString(score1));
                up.setClickable(false);
                down.setClickable(false);

            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewOpenedRequestActivity.ScoreQuestionTask().execute(param, "2");
                int score1 = question.getScore() - 2;
                score.setText(Integer.toString(score1));
                up.setClickable(false);
                down.setClickable(false);

            }
        });
    }

    public class GetOpenedReqRestTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            String URI = baseURI + "opened_requests_title/" + params[0];
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.get().getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(jsonResponse, InvalidKeyException.class);
                question = gson.fromJson(jsonResponse, Question.class);
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
            if (res != null) {
                title.setText(question.getTitle());
                text.setText(question.getText());
                username_req.setText(question.getUser());
                String scoreString = Integer.toString(question.getScore());
                score.setText(scoreString);

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

            Snackbar.make(title, res, Snackbar.LENGTH_LONG).show();

            titles = preferences.getStringSet("titles", titles);

            if (titles == null) {
                titles = new HashSet<>();
            }

            titles.add(String.valueOf(title.getText()));
            SharedPreferences.Editor edit = preferences.edit();

            edit.putStringSet("titles", titles).apply();
        }
    }


    public class ScoreQuestionTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            String URI = baseURI + "score_requests/" + username;
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.post(gson.toJson(params[0] + ";" + params[1], String.class)).getText();
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
            //Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            ;

            Snackbar snackbar = Snackbar.make(answer, res, Snackbar.LENGTH_LONG);
            snackbar.show();

            titles = preferences.getStringSet("titles", titles);

            if (titles == null) {
                titles = new HashSet<>();
            }

            titles.add(String.valueOf(title.getText()));
            SharedPreferences.Editor edit = preferences.edit();

            edit.putStringSet("titles", titles).apply();
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

            TextView activity = convertView.findViewById(R.id.username_resp);
            TextView description = convertView.findViewById(R.id.response);

            Item item = getItem(position);

            activity.setText(item.getActivity());
            description.setText(item.getDescription());

            return convertView;
        }
    }

}
