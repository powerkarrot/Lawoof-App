package com.example.karrot.lawoof.Util.HTTPS

import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.concurrent.CountDownLatch

class LawoofApiResponseHandler : JsonHttpResponseHandler() {

    val countDownLatch = CountDownLatch(1)
    var jsonResponseData : JSONObject? = null

    override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
        jsonResponseData = response
        countDownLatch.countDown()
    }

    override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
        super.onFailure(statusCode, headers, throwable, errorResponse)
        jsonResponseData = errorResponse
        countDownLatch.countDown()
    }
}