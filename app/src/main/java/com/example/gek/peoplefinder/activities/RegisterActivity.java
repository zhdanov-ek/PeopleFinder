package com.example.gek.peoplefinder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static android.text.TextUtils.isEmpty;
import static com.example.gek.peoplefinder.PeopleFinderApplication.AUTH_URL;

public class RegisterActivity extends AppCompatActivity implements SyncUser.Callback {

    @BindView(R.id.tietUserName) protected TextInputEditText tietUserName;
    @BindView(R.id.tietPassword) protected TextInputEditText tietPassword;
    @BindView(R.id.tietPasswordConfirmation) protected TextInputEditText tietPasswordConfirmation;
    @BindView(R.id.tilUserName) protected TextInputLayout tilUserName;
    @BindView(R.id.tilPassword) protected TextInputLayout tilPassword;
    @BindView(R.id.tilPasswordConfirmation) protected TextInputLayout tilPasswordConfirmation;
    @BindView(R.id.progressView) protected View progressView;
    @BindView(R.id.registerFormView) protected View registerFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initTextWatchers();
        tietPasswordConfirmation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btnTryRegister) protected void onClickTryRegister(){
        attemptRegister();
    }

    private void attemptRegister() {
        tilUserName.setError(null);
        tilPassword.setError(null);
        tilPasswordConfirmation.setError(null);

        final String username = tietUserName.getText().toString();
        final String password = tietPassword.getText().toString();
        final String passwordConfirmation = tietPasswordConfirmation.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (isEmpty(username)) {
            tilUserName.setError(getString(R.string.error_name_required));
            focusView = tietUserName;
            cancel = true;
        }

        if (isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_invalid_password));
            focusView = tietPassword;
            cancel = true;
        }

        if (isEmpty(passwordConfirmation)) {
            tilPasswordConfirmation.setError(getString(R.string.error_invalid_confirmation_password));
            focusView = tietPasswordConfirmation;
            cancel = true;
        }

        if (!password.equals(passwordConfirmation)) {
            tilPasswordConfirmation.setError(getString(R.string.error_incorrect_password));
            focusView = tietPasswordConfirmation;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            SyncUser.loginAsync(SyncCredentials.usernamePassword(username, password, true), AUTH_URL, new SyncUser.Callback() {
                @Override
                public void onSuccess(SyncUser user) {
                    registrationComplete(user);
                }

                @Override
                public void onError(ObjectServerError error) {
                    showProgress(false);
                    String errorMsg;
                    switch (error.getErrorCode()) {
                        case EXISTING_ACCOUNT: errorMsg = getString(R.string.error_account_already_exists);
                            break;
                        default:
                            errorMsg = error.toString();
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void registrationComplete(SyncUser user) {
        UserManager.setActiveUser(user);
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        registerFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    public void onSuccess(SyncUser user) {
        registrationComplete(user);
    }

    @Override
    public void onError(ObjectServerError error) {
        String errorMsg;
        switch (error.getErrorCode()) {
            case EXISTING_ACCOUNT: errorMsg = getString(R.string.error_account_already_exists);
                break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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

        tietPasswordConfirmation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPasswordConfirmation.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
