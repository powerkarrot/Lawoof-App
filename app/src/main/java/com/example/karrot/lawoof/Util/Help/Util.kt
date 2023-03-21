package com.example.karrot.lawoof.Util.Help

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.example.karrot.lawoof.Activities.LoginActivity
import com.example.karrot.lawoof.Content.Pet
import com.example.karrot.lawoof.Content.User
import com.example.karrot.lawoof.Content.Walk
import com.example.karrot.lawoof.Util.HTTPS.LawoofApiRequest
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class Util {
    companion object {
        fun  createAccount(email: String, password: String, context: Context): Account {
            val account = Account(email, LoginActivity.ARG_ACCOUNT_TYPE)
            val am = AccountManager.get(context)

            //For now, only allow for one local Lawoof account to be stored.
            val accounts = am.getAccountsByType(LoginActivity.ARG_ACCOUNT_TYPE)
            for (a in accounts) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    System.out.println("Account removed")
                    am.removeAccountExplicitly(a)
                } else {
                    @Suppress("DEPRECATION")
                    System.out.println("Account removed")
                    am.removeAccount(a, null, null)
                }
            }
            am.addAccountExplicitly(account, password, null)
            System.out.println("Account created: $email $password")
            return account
        }

        fun addPet(jsonArray: JSONArray) : MutableList<Pet> {
            val petList: MutableList<Pet> = ArrayList()
            for (i in 0..(jsonArray.length() - 1)) {
                val pet = Pet()
                val item = jsonArray.getJSONObject(i)
                println("Array mini parse: $item")
                val keys = item.keys()
                for (key in keys) {
                    //Only catches one-level nested stuff, so to say. but can be expanded xD
                    if(item.get(key) is  JSONObject) {
                        val obj = item.getJSONObject(key)
                        val arrayKeys = obj.keys()
                        for (k in arrayKeys)  {
                            pet.addDetail2(k.toString(), obj.get(k).toString())
                            println("Array key: $k; Array value: ${obj.get(k)}")
                        }
                    }
                    else {
                        println("Key: " + key + " value: " + item.get(key).toString() + " pet: " + item.get("name"))
                        pet.addDetail2(key.toString(), item.get(key).toString())
                    }
                }
                //watch out. if we were to call add pet while using the app (like when adding a pet) the lastseenlocation will be outdated and
                // pets.places will have duplicate. pet.configurepet needs to be smwhere else lel
                //erm like in login
                pet.configurePet()
                petList.add(pet)
                println("Pet places: ${pet.places[0].position.longitude}")
                println("Pet lastKnownLoc: " + pet.lastKnownLoc.latitude + " and long ${pet.lastKnownLoc.longitude}")
            }
            User.setPets(ArrayList(petList))
            return petList
        }

        fun updatePets() {
            val apiRequest = LawoofApiRequest()

            apiRequest.getPetsByEmail(User.get_id(), "test123", object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    super.onSuccess(statusCode, headers, response)
                    println(response.toString())
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    super.onSuccess(statusCode, headers, response)
                    val petArray = response!!.getJSONArray("value")

                    val petList = Util.addPet(petArray)
                    User.setPets(ArrayList(petList))
                }
                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    println(errorResponse.toString())

                }
            })
        }

        fun addWalk(jsonArray: JSONArray) : MutableList<Walk> {

            val walkList: MutableList<Walk> = ArrayList()
            for (i in 0..(jsonArray.length() - 1)) {
                val walk = Walk()
                val item = jsonArray.getJSONObject(i)
                println("Walkarray mini parse: $item")
                println("Key test: " + item.get("title"))
                val keys = item.keys()
                for (key in keys) {
                    //Quick n' dirty solution for now.
//                    if(item.get(key) is  JSONObject) {
//                    } else {
                        println("Key: " + key + " value: " + item.get(key).toString() + " walk: " + item.get("title"))
                        walk.addDetail2(key.toString(), item.get(key).toString())
  //                  }
                }
                walkList.add(walk)
            }
            User.setMyWalks(ArrayList(walkList))
            return walkList
        }

        fun updateWalks() {
            val apiRequest = LawoofApiRequest()

            apiRequest.getWalks(User.get_id(), "test123", object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    super.onSuccess(statusCode, headers, response)
                    println(response.toString())
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {

                    super.onSuccess(statusCode, headers, response)
                    val walkArray = response!!.getJSONArray("value")
                    val walkList = Util.addWalk(walkArray)
                    User.setMyWalks(ArrayList(walkList))
                    println("User walks set: " + User.getMyWalks().size)
                }
                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    println(errorResponse.toString())
                }
            })
        }
    }
}

