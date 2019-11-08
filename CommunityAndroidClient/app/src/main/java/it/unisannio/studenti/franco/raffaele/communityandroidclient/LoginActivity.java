package it.unisannio.studenti.franco.raffaele.communityandroidclient;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;

import it.unisannio.studenti.franco.raffaele.communityandroidclient.commons.ErrorCodes;


public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;
    private EditText mPasswordView;
    public static final String prefName = "CommunityApplication";
    private String baseURI = "http://10.0.2.2:8182/CommunityApplication/";
    private Button mLoginInButton;
    private Button mSignInButton;
    private CheckBox checkBox;
    private static final String TAG = "Community";
    private DialogFragment mDialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mLoginInButton = (Button) findViewById(R.id.login);

        if (preferences.contains("username")) {
            continueLogin();
        }

        mLoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUsernameView.getText().toString().equals("") || mPasswordView.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.insert_data, Toast.LENGTH_LONG).show();
                } else
                    continueLogin();
            }
        });

        mSignInButton = (Button) findViewById(R.id.signin);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void continueLogin() {
        // Show ProgressDialog as Login process begins
        mDialog = ProgressDialogFragment.newInstance();
        // Show new ProgressDialogFragment
        mDialog.show(getFragmentManager(), "Login");
        // Finish the Login process
        finishLogin();
    }

    private void finishLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Pretend to do something before login
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.toString());
                } finally {
                    if (preferences.contains("username")) {
                        new LoginRestTask().execute("login", preferences.getString("username", null), preferences.getString("password", null));
                    } else
                        new LoginRestTask().execute("login", mUsernameView.getText().toString(), mPasswordView.getText().toString());
                }
            }
        }).start();
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        // Build ProgressDialog
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Create new ProgressDialog
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            // Set Dialog message
            dialog.setMessage(getString(R.string.loading));
            // Dialog will be displayed for an unknown amount of time
            dialog.setIndeterminate(true);
            return dialog;
        }
    }

    public class LoginRestTask extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {
            ClientResource cr = new ClientResource(baseURI + params[0]);
            Gson gson = new Gson();
            String gsonResponse = null;
            Boolean response;
            // Log.i("Connection", "Connection establishing");

            try {
                gsonResponse = cr.post(gson.toJson(params[1] + ";" + params[2], String.class)).getText();
                if (cr.getStatus().getCode() == 200) {
                    response = gson.fromJson(gsonResponse, Boolean.class);
                    if (response)
                        return 0;
                    return 2;
                } else if (cr.getStatus().getCode() == ErrorCodes.INVALID_KEY_CODE) {
                    return 1;
                } else {
                    return 2;
                }
            } catch (ResourceException | IOException e1) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
                return 3;
            }
        }

        protected void onPostExecute(Integer c) {
            if (c == 0) {
                Intent myIntent = new Intent(LoginActivity.this, MenuActivity.class);
                SharedPreferences.Editor edit = preferences.edit();
                checkBox = (CheckBox) findViewById(R.id.checkBox);
                if (checkBox.isChecked()) {
                    edit.putString("username", String.valueOf(mUsernameView.getText())).apply();
                    edit.putString("password", String.valueOf(mPasswordView.getText())).apply();
                } else if (!checkBox.isChecked()) {
                    edit.putString("user", String.valueOf(mUsernameView.getText())).apply();
                }
                startActivity(myIntent);
            } else if (c == 1) {
                Toast.makeText(getApplicationContext(), R.string.unregistered, Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(myIntent);
            } else if (c == 2) {
                Toast.makeText(getApplicationContext(), R.string.wrong_credential, Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        }
    }
}
