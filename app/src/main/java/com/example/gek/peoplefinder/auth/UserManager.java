/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gek.peoplefinder.auth;

import android.util.Log;

import com.example.gek.peoplefinder.PeopleFinderApplication;
import com.facebook.login.LoginManager;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

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
        Log.d(TAG, "setActiveUser: " + user.toString());
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, PeopleFinderApplication.REALM_URL).build();
        Realm.setDefaultConfiguration(defaultConfig);
    }
}
