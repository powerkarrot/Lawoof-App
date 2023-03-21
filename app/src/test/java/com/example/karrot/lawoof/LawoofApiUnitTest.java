package com.example.karrot.lawoof;


import android.app.Instrumentation;
import android.content.Context;

import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class LawoofApiUnitTest {

    private Context context;
    private Instrumentation instr;

    @Before
    public void setup() {
        instr = getInstrumentation();
        context = instr.getContext();
    }

    @Test
    public void user_login() {

        LawoofApiRequest apiRequest = new LawoofApiRequest();

        LawoofApiResponseHandler responseHandler = new LawoofApiResponseHandler();
        AsyncLawoofApiResponseHandler responseHandler1 = new AsyncLawoofApiResponseHandler();
        //apiRequest.login("ex3c@ex3c.de", "test123");
        apiRequest.login("ex3c@ex3c.de", "test123", responseHandler1);

        // Wait for responseHandler to finish request.
        try {
            Boolean ready = responseHandler1.signal.await(10, TimeUnit.SECONDS);
            System.out.println(responseHandler1.test);
            assertTrue(ready);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //assertNull(responseHandler.serverResponse);

        /*
        try {
            if(responseHandler.serverResponse != null)
            {
                System.out.println("Response: " + responseHandler.serverResponse.getString("status"));
                assertEquals("Success",responseHandler.serverResponse.getString("status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private class LawoofApiResponseHandler extends JsonHttpResponseHandler {
        final CountDownLatch signal = new CountDownLatch(1);
        public JSONObject serverResponse;
        String test = "Es geht nicht!";

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                String status = response.getString("status");
                serverResponse = response;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            test = "Es geht!";
            signal.countDown();
            signal.notify();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
            serverResponse = errorResponse;
            signal.countDown();
            signal.notify();
        }

    }

    private class AsyncLawoofApiResponseHandler extends AsyncHttpResponseHandler{

        final CountDownLatch signal = new CountDownLatch(1);
        String test = "Es geht nicht!";

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            test = "Es geht!";
            test = Arrays.toString(responseBody);
            signal.countDown();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }
}
