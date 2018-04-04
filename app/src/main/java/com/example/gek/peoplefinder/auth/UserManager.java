package com.example.gek.peoplefinder.auth;

import com.example.gek.peoplefinder.PeopleFinderApplication;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.facebook.login.LoginManager;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.permissions.PermissionChange;

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
        switch (mode) {
            case PASSWORD: {
                // Do nothing, handled by the `User.currentUser().logout();`
                break;
            }
            case FACEBOOK: {
                LoginManager.getInstance().logOut();
                break;
            }
            case GOOGLE: {
                // the connection is handled by `enableAutoManage` mode
                break;
            }
        }
        SyncUser.currentUser().logout();
    }

    // Configure Realm for the current active user
    public static void setActiveUser(SyncUser user) {
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, PeopleFinderApplication.REALM_URL).build();
        Realm.setDefaultConfiguration(defaultConfig);
        setPublicPermissionForData(user);
    }

    private static void setPublicPermissionForData(SyncUser user){
        if (SettingsHelper.getUserEmail() != null
                && SettingsHelper.getUserEmail().contentEquals(Const.OWNER_DATA)){
            Realm managementRealm = user.getManagementRealm();
            managementRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Boolean mayRead = true; // Grant read access
                    Boolean mayWrite = true; // Keep current permission
                    Boolean mayManage = false; // Revoke management access
                    PermissionChange change = new PermissionChange(PeopleFinderApplication.REALM_URL,
                            "*",
                            mayRead,
                            mayWrite,
                            mayManage);
                    realm.insert(change);
                }
            });
        }
    }
}
