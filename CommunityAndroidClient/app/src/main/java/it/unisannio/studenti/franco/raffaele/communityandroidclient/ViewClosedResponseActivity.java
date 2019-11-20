package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.Response;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;

public class ViewClosedResponseActivity extends AppCompatActivity {

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

        username_req.setText(r.getUser().toString());
        score.setText(Integer.toString(r.getScore()));

        text.setText(param.toString());

    }

}

