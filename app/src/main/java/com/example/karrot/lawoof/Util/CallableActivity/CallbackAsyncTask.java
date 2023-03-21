package com.example.karrot.lawoof.Util.CallableActivity;
import android.os.AsyncTask;

/**
 * Created by ex3c on 26.02.2017.
 */

public abstract class CallbackAsyncTask<A, B, C> extends AsyncTask<A, B, C> implements CallbackTask{

    CallbackUI activity;

    public CallbackAsyncTask(CallbackUI activity){
        this.activity = activity;
    }

}
