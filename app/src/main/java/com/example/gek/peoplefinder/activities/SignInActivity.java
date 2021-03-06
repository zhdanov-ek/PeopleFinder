package com.example.gek.peoplefinder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gek.peoplefinder.PeopleFinderApplication;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.AuthMode;
import com.example.gek.peoplefinder.auth.FacebookAuth;
import com.example.gek.peoplefinder.auth.GoogleAuth;
import com.example.gek.peoplefinder.auth.UserManager;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.example.gek.peoplefinder.PeopleFinderApplication.AUTH_URL;

public class SignInActivity extends AppCompatActivity implements SyncUser.Callback {

    public static final String ACTION_IGNORE_CURRENT_USER = "action.ignoreCurrentUser";

    @BindView(R.id.tietUserName) protected TextInputEditText tietUserName;
    @BindView(R.id.tietPassword) protected TextInputEditText tietPassword;
    @BindView(R.id.tilUserName) protected TextInputLayout tilUserName;
    @BindView(R.id.tilPassword) protected TextInputLayout tilPassword;
    @BindView(R.id.progressView) protected View progressView;
    @BindView(R.id.loginFormView) protected View loginFormView;
    @BindView(R.id.tvRegister) protected TextView tvRegister;

    private FacebookAuth facebookAuth;
    private GoogleAuth googleAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        initTextWatchers();
        SpannableString content = new SpannableString(getString(R.string.action_sign_up));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvRegister.setText(content);

        tietPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.log_in || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        // Check if we already got a user, if yes, just continue automatically
        if (savedInstanceState == null) {
            if (!ACTION_IGNORE_CURRENT_USER.equals(getIntent().getAction())) {
                final SyncUser user = SyncUser.current();
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
                                SyncUser.logInAsync(credentials, AUTH_URL, SignInActivity.this);
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
                SyncUser.logInAsync(credentials, AUTH_URL, SignInActivity.this);
            }

            @Override
            public void onError(String s) {
                super.onError(s);
            }
        };
    }


    private void initTextWatchers(){
        tietPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilUserName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick(R.id.tvRegister) protected void clickOnRegister(){
        startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.btnSignIn) protected void clickOnSignIn(){
        attemptLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuth.onActivityResult(requestCode, resultCode, data);
        facebookAuth.onActivityResult(requestCode, resultCode, data);
    }

    private void loginComplete(SyncUser user) {
        UserManager.setActiveUser(user);
        Connection.getInstance().refreshData();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void attemptLogin() {
        tilUserName.setError(null);
        tilPassword.setError(null);

        final String email = tietUserName.getText().toString();
        final String password = tietPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_invalid_password));
            focusView = tietPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            tilUserName.setError(getString(R.string.error_name_required));
            focusView = tietUserName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            UserManager.setAuthMode(AuthMode.PASSWORD);
            SyncUser.logInAsync(SyncCredentials.usernamePassword(email, password, false), PeopleFinderApplication.AUTH_URL, this);
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
    public void onSuccess(Object result) {
        if (result instanceof SyncUser){
            if (UserManager.getAuthMode() == AuthMode.PASSWORD){
                SettingsHelper.setUserName(tietUserName.getText().toString());
            }
            showProgress(false);
            loginComplete((SyncUser) result);
        }
    }

    @Override
    public void onError(ObjectServerError error) {
        showProgress(false);
        String errorMsg;
        switch (error.getErrorCode()) {
            case UNKNOWN_ACCOUNT:
                errorMsg = getString(R.string.error_account_not_exists);
                break;
            case INVALID_CREDENTIALS:
                errorMsg = getString(R.string.error_authentication_failed); // This message covers also expired account token
                break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }


}

