package com.example.karrot.lawoof.Util.HTTPS

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

class LawoofRestClient{
    val BASE_URL = "https://lawoof.ex3c.de/api"
    val httpClient = AsyncHttpClient(true, 80, 443)

    operator fun get(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        httpClient.get(getAbsoluteUrl(url), params, responseHandler)
    }

    fun post(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        httpClient.post(getAbsoluteUrl(url), params, responseHandler)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        return BASE_URL + relativeUrl
    }
}