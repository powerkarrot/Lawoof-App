package com.example.karrot.lawoof.Util.HTTPS

import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Future

class LawoofApiRequest{
    val lawoofRestClient = LawoofRestClient()
    /**
     * Function used to authenticate the user with our api.
     * @param email email used by the user
     * @param password password used by the user
     * @param responseHandler responseHandler used for parsing the response
     */
    fun login(email: String, password: String, responseHandler: AsyncHttpResponseHandler) {
        val params = RequestParams()
        params.add("email", email)
        params.add("pwd", password)
        lawoofRestClient.post("/user/login", params, responseHandler)
    }

    /**
     * Function used to get the registered walks from a user with a given email.
     * @param email email used by the user
     * @param accessToken accessToken used to authenticate the api call (notYetImplemented)
     * @param responseHandler responseHandler used for parsing the response
     */
    fun getWalks(email: String, accessToken : String,  responseHandler: AsyncHttpResponseHandler){
        val params = RequestParams()
        params.add("email", email)
        params.add("accessToken", accessToken)
        lawoofRestClient.post("/walks/get", params, responseHandler)
    }

    /**
     * Function used to get the registered pets from a user with a given email.
     * @param email email used by the user
     * @param accessToken accessToken used to authenticate the api call (notYetImplemented)
     * @param responseHandler responseHandler used for parsing the response
     */
    fun getPetsByEmail(email: String, accessToken : String,  responseHandler: AsyncHttpResponseHandler){
        val params = RequestParams()
        params.add("email", email)
        params.add("accessToken", accessToken)
        lawoofRestClient.post("/pets/get/byemail", params, responseHandler)
    }

    fun addPet(accessToken : String, email: String, name: String, age: String, pClass: String, species: String, breed: String,
               color: String, sex: String, castrated: Boolean, friendliness: Boolean, description: String, responseHandler: AsyncHttpResponseHandler){

        val params = RequestParams()

        params.add("email", email)
        params.add("name", name)
        params.add("age", age)
        params.add("class", pClass)
        params.add("species", species)
        params.add("breed", breed)
        params.add("color", color)
        params.add("sex", sex)
        params.add("castrated", castrated.toString())
        params.add("friendliness", friendliness.toString())
        params.add("description", description)

        lawoofRestClient.post("/pets/add", params, responseHandler)
    }

    fun addWalks(accessToken : String, email: String, title: String, date: String, time : String, description: String, difficulty : String, location : String, locationName : String, locationType : String, /*vararg*/ participants : String, responseHandler: AsyncHttpResponseHandler) {

//        val obj = JSONObject()
//        for( p in participants) {
//            obj.put("participants", p)
//        }

        val params = RequestParams()
        params.add("email", email)
        params.add("title", title)
        params.add("date", date)
        params.add("time", time)
        params.add("description", description)
        params.add("difficulty", difficulty)
        params.add("location", location)
        params.add("locationname", locationName)
        params.add("locationType", locationType)
//        params.add("participants", JSON.stringify(participants)) // where is JSON and why cant i import it -.-
        //TODO this is probably wrong
//        params.add("participants", obj.toString())
        params.add("participants", participants)

        lawoofRestClient.post("/walks/add", params, responseHandler)

    }


    fun registerUser(accessToken : String, email: String, lastName: String, name: String, street: String, city: String, zip: String, username: String, pwd: String, responseHandler: AsyncHttpResponseHandler){
        val params = RequestParams()

        params.add("email", email)
        params.add("last_name", lastName)
        params.add("name", name)
        params.add("street", street)
        params.add("city", city)
        params.add("zip", zip)
        params.add("username", username)
        params.add("pwd", pwd)

        lawoofRestClient.post("/user/new", params, responseHandler)
    }
}
