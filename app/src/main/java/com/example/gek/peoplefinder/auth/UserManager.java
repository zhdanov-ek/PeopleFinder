package com.example.gek.peoplefinder.auth;

import android.util.Log;

import com.example.gek.peoplefinder.PeopleFinderApplication;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.facebook.login.LoginManager;

import io.realm.ObjectServerError;
import io.realm.PermissionManager;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncSession;
import io.realm.SyncUser;
import io.realm.permissions.AccessLevel;
import io.realm.permissions.PermissionRequest;
import io.realm.permissions.UserCondition;

public class UserManager {
    private static final String TAG = "H_USER_MANAGER";

    private static AuthMode mode = AuthMode.PASSWORD; // default

    public static void setAuthMode(AuthMode m) {
        mode = m;
    }

    public static AuthMode getAuthMode(){
        return mode;
    }

    public static void logoutActiveUser() {
        if (SyncUser.current() != null){
            SyncUser.current().logOut();
        }
        Realm.removeDefaultConfiguration();
        switch (mode) {
            case PASSWORD: {
                break;
            }
            case FACEBOOK: {
                LoginManager.getInstance().logOut();
                break;
            }
            case GOOGLE: {
                break;
            }
        }
    }

    // Configure Realm for the current active user
    public static void setActiveUser(SyncUser user) {
        SyncConfiguration configuration = new SyncConfiguration.Builder(user, PeopleFinderApplication.REALM_URL)
                .errorHandler(new SyncSession.ErrorHandler() {
                    @Override
                    public void onError(SyncSession session, ObjectServerError error) {
                        Log.d(TAG, "onError: session: " + session.toString());
                        Log.d(TAG, "onError: error: " + error.toString());
                    }
                })
                .build();
        Realm.setDefaultConfiguration(configuration);
        setPublicPermissionForData(user);
    }

    private static void setPublicPermissionForData(SyncUser user){
        if (SettingsHelper.getUserEmail() != null
                && SettingsHelper.getUserEmail().contentEquals(Const.OWNER_DATA)){
            PermissionManager permissionManager = user.getPermissionManager();

            // Create request
            UserCondition condition = UserCondition.noExistingPermissions();
            PermissionRequest request = new PermissionRequest(condition, PeopleFinderApplication.REALM_URL, AccessLevel.ADMIN);
            permissionManager.applyPermissions(request, new PermissionManager.ApplyPermissionsCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: ");
                }

                @Override
                public void onError(ObjectServerError error) {
                    Log.d(TAG, "onError: ");
                }
            });
            permissionManager.close();
        }
    }
}
