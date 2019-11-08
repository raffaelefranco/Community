package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.restlet.resource.ClientResource;

import java.io.IOException;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;

import static it.unisannio.studenti.franco.raffaele.communityandroidclient.LoginActivity.prefName;


public class AnswerActivity extends AppCompatActivity {
    private TextView text;
    private Button ok;
    private Button cancel;
    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private SharedPreferences preferences;
    private final String TAG = "Community";
    private String param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        text = (TextView) findViewById(R.id.response_text);
        ok = (Button) findViewById(R.id.post_response);
        cancel = (Button) findViewById(R.id.no_post_response);
        preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        Intent fromCaller = getIntent();
        param = fromCaller.getStringExtra(getResources().getString(R.string.key));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), getString(R.string.insert_data), Toast.LENGTH_SHORT).show();
                else {
                    new AnswerActivity.AnswerUserTask().execute(text.getText().toString());
                    Intent intent = new Intent(AnswerActivity.this, ViewOpenedRequestActivity.class);
                    intent.putExtra(getResources().getString(R.string.key), param);
                    startActivity(intent);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerActivity.this, ViewOpenedRequestActivity.class);
                intent.putExtra(getResources().getString(R.string.key), param);
                startActivity(intent);
            }
        });
    }

    public class AnswerUserTask extends AsyncTask<String, Void, String> {

        /* AsyncTask per l'esecuzione in background delle operazioni asincrone dedicate alla registrazione di un nuovo utente */

        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            // Creo la risorsa client basandomi su una URI costituita da baseURI + parametro passato dal metodo di gestione di un componente grafico (bottone) */
            Gson gson = new Gson();
            String username = preferences.getString("username", null);

            if (username == null) {
                username = preferences.getString("user", null);
            }

            cr = new ClientResource(baseURI + "opened_requests/" + param + "/response");

            String question = null;
            String payload = username + ":" + params[0];

            try {
                question = cr.post(gson.toJson(payload, String.class)).getText(); // Effettuo la Request HTTP con metodo "POST" e inserisco in response la Response HTTP.
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE) // Se mi viene restituito il codice di errore per una chiave invalida, lancio l'eccezione (ad esempio, Username duplicato)
                    throw gson.fromJson(question, InvalidKeyException.class);
            } catch (IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            } catch (InvalidKeyException e) {
                String error2 = "Error: " + cr.getStatus().getCode() + " - " + e.getMessage();
                Log.e(TAG, error2);
            }
            return question;
        }
    }
}
