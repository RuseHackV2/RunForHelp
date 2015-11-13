package com.example.sisko.runforhelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvCreateAccount;
    private FacebookButtonBase fbLogin;
    protected CallbackManager callbackManager;

    private String mFacebookAccssessToken;
    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_log_in);


        initializeLayoutElements();

        callbackManager = CallbackManager.Factory.create();

        btnLogin.setOnClickListener(this);
        tvCreateAccount.setOnClickListener(this);

    }

    private void initializeLayoutElements() {

        btnLogin = (Button) findViewById(R.id.log_in_button);
        etUsername = (EditText) findViewById(R.id.user_email_edit_text);
        etPassword = (EditText) findViewById(R.id.user_password_edit_text);
        tvCreateAccount = (TextView) findViewById(R.id.create_account_text_view);
        fbLogin = (FacebookButtonBase) findViewById(R.id.facebook_login_button);

    }

        @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.log_in_button:

                Intent intent = new Intent(LogInActivity.this, Main2Activity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;
            case R.id.facebook_login_button:

                LoginManager.getInstance().logInWithPublishPermissions(LogInActivity.this, Arrays.asList("public_profile","user_friends"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                mFacebookAccssessToken = loginResult.getAccessToken().getToken();
                                Log.d(TAG, mFacebookAccssessToken);
                                faceBookLogin();
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                break;
            case R.id.create_account_text_view:



                break;
        }
    }

    public void faceBookLogin() {
        String URL = "http://ec2-52-18-130-154.eu-west-1.compute.amazonaws.com:3000/api/v1/auth/facebook";
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", response);
                            Intent intent = new Intent(LogInActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("access_token", mFacebookAccssessToken);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }


}
