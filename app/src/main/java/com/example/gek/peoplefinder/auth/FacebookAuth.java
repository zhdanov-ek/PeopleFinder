package com.example.gek.peoplefinder.auth;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Provide authentication using users Facebook account.
 * https://developers.facebook.com/docs/facebook-login/android
 */
public abstract class FacebookAuth {
    private final LoginButton loginButton;
    private final CallbackManager callbackManager;

    public FacebookAuth(final LoginButton loginBtn) {
        callbackManager = CallbackManager.Factory.create();
        this.loginButton = loginBtn;
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                onRegistrationComplete(loginResult);
            }

            @Override
            public void onCancel() {
                onAuthCancelled();
            }

            @Override
            public void onError(FacebookException exception) {
                onAuthError();
            }
        });

    }

    /**
     * Called if the authentication is cancelled by the user.
     *
     * Adapter method, developer might want to override this method  to provide
     * custom logic.
     */
    public void onAuthCancelled() {}

    /**
     * Called if the authentication fails.
     *
     * Adapter method, developer might want to override this method  to provide
     * custom logic.
     */
    public void onAuthError () {}

    /**
     * Notify this class about the {@link FragmentActivity#onResume()} event.
     */
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called once we obtain a token from Facebook API.
     * @param loginResult contains the token obtained from Facebook API.
     */
    public abstract void onRegistrationComplete(final LoginResult loginResult);
}
