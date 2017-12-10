package com.example.gek.peoplefinder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gek.peoplefinder.PeopleFinderApplication;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.AuthMode;
import com.example.gek.peoplefinder.auth.FacebookAuth;
import com.example.gek.peoplefinder.auth.GoogleAuth;
import com.example.gek.peoplefinder.auth.UserManager;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.example.gek.peoplefinder.models.Mark;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.example.gek.peoplefinder.PeopleFinderApplication.AUTH_URL;

public class SignInActivity extends AppCompatActivity implements SyncUser.Callback {

    private static final String TAG = "A_SIGN_IN";
    public static final String ACTION_IGNORE_CURRENT_USER = "action.ignoreCurrentUser";
    private static final int RC_SIGN_IN = 10;

    private EditText etUserName;
    private EditText etPassword;
    private View progressView;
    private View loginFormView;
    private FacebookAuth facebookAuth;
    private GoogleAuth googleAuth;
    private Button btnSignIn, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.log_in || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.sign_in_form);
        progressView = findViewById(R.id.sign_in_progress);

        // Check if we already got a user, if yes, just continue automatically
        if (savedInstanceState == null) {
            if (!ACTION_IGNORE_CURRENT_USER.equals(getIntent().getAction())) {
                final SyncUser user = SyncUser.currentUser();
                if (user != null) {
                    loginComplete(user);
                }
            }
        }

        // Setup Facebook Authentication
        facebookAuth = new FacebookAuth((LoginButton) findViewById(R.id.facebookLogin)) {
            @Override
            public void onRegistrationComplete(final LoginResult loginResult) {
                UserManager.setAuthMode(AuthMode.FACEBOOK);

                // Get date of profile Facebook and make login to realm
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    SettingsHelper.setUserName(object.getString("name"));
                                    SettingsHelper.setUserEmail(object.getString("email"));
                                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    SettingsHelper.setUserProfileImageUrl(profilePicUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Login to Realm
                                SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());
                                SyncUser.loginAsync(credentials, AUTH_URL, SignInActivity.this);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }
        };

        // Setup Google Authentication
        googleAuth = new GoogleAuth((SignInButton) findViewById(R.id.googleSignIn), this) {
            @Override
            public void onRegistrationComplete(GoogleSignInResult result) {
                UserManager.setAuthMode(AuthMode.GOOGLE);
                GoogleSignInAccount acct = result.getSignInAccount();

                if (acct.getDisplayName() != null && acct.getDisplayName().length() > 0){
                    SettingsHelper.setUserName(acct.getDisplayName());
                } else if (acct.getGivenName() != null && acct.getGivenName().length() > 0){
                    SettingsHelper.setUserName(acct.getGivenName());
                }
                SettingsHelper.setUserEmail(acct.getEmail());
                if (acct.getPhotoUrl() != null && acct.getPhotoUrl().toString().length() > 0) {
                    SettingsHelper.setUserProfileImageUrl(acct.getPhotoUrl().toString());
                }

                SyncCredentials credentials = SyncCredentials.google(acct.getIdToken());
                SyncUser.loginAsync(credentials, AUTH_URL, SignInActivity.this);
            }

            @Override
            public void onError(String s) {
                super.onError(s);
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuth.onActivityResult(requestCode, resultCode, data);
        facebookAuth.onActivityResult(requestCode, resultCode, data);
    }

    private void loginComplete(SyncUser user) {
        UserManager.setActiveUser(user);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void attemptLogin() {
        etUserName.setError(null);
        etPassword.setError(null);

        final String email = etUserName.getText().toString();
        final String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            etUserName.setError(getString(R.string.error_field_required));
            focusView = etUserName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            UserManager.setAuthMode(AuthMode.PASSWORD);
            SyncUser.loginAsync(SyncCredentials.usernamePassword(email, password, false), PeopleFinderApplication.AUTH_URL, this);
        }
    }

    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }



    // Realm auth success
    @Override
    public void onSuccess(SyncUser user) {
        if (UserManager.getAuthMode() == AuthMode.PASSWORD){
            SettingsHelper.setUserName(etUserName.getText().toString());
        }
        showProgress(false);
        loginComplete(user);
    }

    @Override
    public void onError(ObjectServerError error) {
        showProgress(false);
        String errorMsg;
        switch (error.getErrorCode()) {
            case UNKNOWN_ACCOUNT:
                errorMsg = "Account does not exists.";
                break;
            case INVALID_CREDENTIALS:
                errorMsg = "The provided credentials are invalid!"; // This message covers also expired account token
                break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }


}

