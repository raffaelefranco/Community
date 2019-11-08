package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Question;

import static android.content.ContentValues.TAG;
import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class NewRequestActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private EditText title;
    private EditText text;
    private Button button_ok;
    private Button button_cancel;
    private Set<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        title = (EditText) findViewById(R.id.title);
        text = (EditText) findViewById(R.id.text);
        button_ok = (Button) findViewById(R.id.new_req);
        button_cancel = (Button) findViewById(R.id.not_new_req);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("") || text.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), getString(R.string.insert_data), Toast.LENGTH_SHORT).show();
                else
                    new NewRequestActivity.NewReqTask().execute(title.getText().toString(), text.getText().toString());
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                text.setText("");
            }
        });

    }

    public class NewReqTask extends AsyncTask<String, Void, String> {

        private Question question;
        private SharedPreferences preferences;


        protected String doInBackground(String... params) {
            String title = params[0];
            String text = params[1];

            preferences = getSharedPreferences(prefName, MODE_PRIVATE);

            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            question = new Question(title, text);
            question.setUser(username);

            ClientResource cr;
            Gson gson = new Gson();

            String jsonResponse = null;
            cr = new ClientResource(baseURI + "requests");


            try {
                jsonResponse = cr.put(gson.toJson(question, Question.class)).getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(jsonResponse, InvalidKeyException.class);
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

            preferences.getStringSet("titles", titles);
            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

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


