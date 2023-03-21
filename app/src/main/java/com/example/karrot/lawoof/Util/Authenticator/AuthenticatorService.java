package com.example.karrot.lawoof.Util.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by ex3c on 25.02.2017.
 */

public class AuthenticatorService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Authenticator mAuthenticator = new Authenticator(this);
        return mAuthenticator.getIBinder();
    }
}
