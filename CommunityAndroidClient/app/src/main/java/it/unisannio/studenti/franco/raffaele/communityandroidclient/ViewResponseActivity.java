package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Response;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class ViewResponseActivity extends AppCompatActivity {

    private final String TAG = "Community";
    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private TextView username_req;
    private SharedPreferences preferences;
    private TextView score;
    private Button up;
    private Button down;
    private Response r;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_response);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        username_req = (TextView) findViewById(R.id.textView_username_req1);
        score = (TextView) findViewById(R.id.textView_score);
        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        text = (TextView) findViewById(R.id.text);

        Intent fromCaller = getIntent();
        String param = fromCaller.getStringExtra(getResources().getString(R.string.key_response));
        r = (Response) fromCaller.getSerializableExtra(getResources().getString(R.string.key_response1));

        Log.i(TAG, param);

        username_req.setText(r.getUser().toString());
        score.setText(Integer.toString(r.getScore()));

        text.setText(param.toString());

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewResponseActivity.ScoreQuestionTask().execute(r.getUser(), r.getText(), "10");
                int score1 = r.getScore() + 10;
                score.setText(Integer.toString(score1));
                up.setClickable(false);
                down.setClickable(false);

            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewResponseActivity.ScoreQuestionTask().execute(r.getUser(), r.getText(), "2");
                int score1 = r.getScore() - 2;
                score.setText(Integer.toString(score1));
                up.setClickable(false);
                down.setClickable(false);

            }
        });

    }

    public class ScoreQuestionTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            ClientResource cr;
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            String URI = baseURI + "score_responses/" + username;
            String jsonResponse = null;
            cr = new ClientResource(URI);

            try {
                jsonResponse = cr.post(gson.toJson(params[0] + ";" + params[1] + ";" + params[2], String.class)).getText();

                if(params[2].equals("10"))
                    r.incrementScore(10);
                else if(params[2].equals("2"))
                    r.decrementScore(-2);

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

            Snackbar snackbar = Snackbar.make(up, res, Snackbar.LENGTH_LONG);
            snackbar.show();


        }
    }
}
