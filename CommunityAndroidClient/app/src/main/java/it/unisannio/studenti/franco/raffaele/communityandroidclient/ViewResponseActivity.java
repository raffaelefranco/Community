package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_response);

        Intent fromCaller = getIntent();
        String param = fromCaller.getStringExtra(getResources().getString(R.string.key_response));

        TextView text = (TextView) findViewById(R.id.id);
        text.setText(param);
    }
}
