package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;

import java.io.IOException;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.User;


public class RegisterActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private EditText username;
    private EditText password;
    private Button registrationButton;
    private Button cancelButton;
    private final String TAG = "Community";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username1);
        password = (EditText) findViewById(R.id.password1);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        registrationButton = (Button) findViewById(R.id.register);
        cancelButton = (Button) findViewById(R.id.cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() == 0 || password.getText().toString().length() == 0) /* Verifico se username e password inseriti nei campi siano sufficientemente lunghi*/
                    Snackbar.make(username, R.string.incorrect, Snackbar.LENGTH_LONG).show();

                else {
                    new RegisterUserTask().execute("registration", username.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public class RegisterUserTask extends AsyncTask<String, Void, String> {

        private String response;

        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            cr = new ClientResource(baseURI + params[0]);
            Gson gson = new Gson();
            response = null;
            User user = new User(params[1], params[2].toCharArray());

            try {
                response = cr.post(gson.toJson(user, User.class)).getText();
                if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE)
                    throw gson.fromJson(response, InvalidKeyException.class);
            } catch (IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            } catch (InvalidKeyException e) {
                response = null;
                runOnUiThread(new Runnable() { // Poiché queste operazioni si stanno effettuando in un metodo dell'AsyncTask, per la visualizzazione di Toast è necessario eseguire un "Thread UI".
                    public void run() {
                        Snackbar.make(username, "Username not available", Snackbar.LENGTH_LONG).show();

                        username.setText(null);
                        password.setText(null);
                    }
                });
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String res) {
            if (res != null) {
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                if (res.contains(getString(R.string.add_user))) {
                    Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
            }
        }
    }
}
