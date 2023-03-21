package com.example.karrot.lawoof.Util.HTTPS;

import android.location.Location;
import android.os.AsyncTask;
import android.util.JsonReader;

import com.example.karrot.lawoof.Util.CallableActivity.CallbackTask;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Util.JSON.JSONReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by ex3c on 03.03.2017.
 */

public class Request extends AsyncTask<String, Void, JsonReader> implements CallbackTask {

    public enum RequestType {
        SERVERSTATUS, REGISTER, LOGIN, GET_ALL_WALKS, GET_ALL_WALKS_FROM_USER, ADD_WALK,
        REGISTER_FOR_WALK, ADD_PET, UPDATE_PET_LOCATION ,GET_PET_BYID
    };

    private RequestType type;

    private final static boolean DEBUG = false;
    private final static int PORTNUMBER = DEBUG ? 64569 : 61650;
    private final static String HOSTURL = "https://lawoof.ex3c.de:" + PORTNUMBER;
    private final static String SERVERSTATUS = "/";
    private final static String LOGIN = "api/user/login";
    private final static String REGISTER = "api/user/new";
    private final static String WALK = "api/walks/get";
    private final static String WALK_ADD = "api/walks/add";
    private final static String WALK_PARTICIPANTS = "api/walks/update/participants";  //TODO: not sure
    private final static String PET_ADD = "/pets/add";
    private final static String PET_UPDATE_LOCATION = "/pets/update/location";  //TODO: create it
    private final static String GETPETS = "get/pets/get/byemail";

    private String postBody = "";
    private String errorMessage = "";
    private JSONReader reader = new JSONReader();
    private HttpURLConnection urlConnection;
    private boolean successful = false;

    private List<JsonReader> results = new ArrayList<JsonReader>();
    private List<CallbackUI> callbacks = new ArrayList<CallbackUI>();

    /**
     *
     * @param email
     * @param callbackUIs
     * @return
     */
    public static Request getMyWalks(String email, CallbackUI... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email);
        r.setCallbacks(callbackUIs);
        r.setType(RequestType.GET_ALL_WALKS_FROM_USER);
        r.execute();
        return r;
    }

    /**
     *
     * @param email
     * @param callbackUIs
     * @return
     */
    public static Request getMyPets(String email, CallbackUI... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email);
        r.setCallbacks(callbackUIs);
        r.setType(RequestType.GET_PET_BYID);
        r.execute();
        return r;
    }

    /**
     *
     * @param email
     * @param password
     * @param callbackUIs
     * @return
     */
    public static Request login(String email, String password, CallbackUI... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email + "&" + "pwd=" + password);
        r.setCallbacks(callbackUIs);
        r.setType(RequestType.LOGIN);
        r.execute();
        System.out.println("Attempt to login...");
        return r;
    }

    /**
     *
     * @param email
     * @param name
     * @param lastname
     * @param password
     * @param username
     * @param email
     * @param street
     * @param city
     * @param zip
     * @param callbackUIs
     * @return
     */
    public static Request register(String email, String name, String lastname,
                                   String password, String username,
                                   String street, String city, String zip, CallbackUI... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email + "&" + "last_name=" + lastname + "&" + "name=" + name + "&"
                + "pwd=" + password + "&" + "username=" + username
                + "&" + "street=" + street + "&" + "city=" + city + "&" + "zip=" +zip);
        r.setType(RequestType.REGISTER);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    /**
     *
     * @param email
     * @param name
     * @param age
     * @param pet_class
     * @param species
     * @param breed
     * @param color
     * @param sex
     * @param castrated
     * @param friendliness
     * @param description
     * @param callbackUIs
     * @return
     */
    public static Request addPet(String email, String name, String age, String pet_class, String species,
                                 String breed, String color, String sex,
                                 String castrated, String friendliness, String description, CallbackUI... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email +"&name=" + name + "&" + "age=" + age + "&" + "class=" + pet_class + "&"
                + "species=" + species + "&" + "breed=" + breed + "&" + "color=" + color
                + "&" + "sex=" + sex + "&" + "castrated=" + castrated + "&" + "friendliness=" + friendliness + "&description=" + description);
        r.setType(RequestType.ADD_PET);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    /**
     *
     * @param callbackUIs
     * @return
     */
    public static Request getServerStatus(CallbackUI ... callbackUIs){
        Request r = new Request();
        r.setType(RequestType.SERVERSTATUS);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    /**
     *
     * @param callbackUIs
     * @return
     */
    public static Request getAllWalks(CallbackUI ... callbackUIs){
        Request r = new Request();
        r.setType(RequestType.GET_ALL_WALKS);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    /**
     *
     * @param email Users email adress
     * @param walkid Internal Walkid
     * @param callbackUIs
     * @return
     */
    //Todo: walkid
    public static  Request registerForWalk(String email, String walkid, CallbackUI ... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email + "&walkid=" + walkid);
        r.setType(RequestType.REGISTER_FOR_WALK);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    /**
     * addWalk method that implements Serialization
     * @param email
     * @param title
     * @param date
     * @param time
     * @param description
     * @param difficulty
     * @param location
     * @param locationtype
     * @param participants //TODO: test
     * @param callbackUIs
     * @return
     */
    public static Request addWalk(String email, String title, String date, String time, String description, String difficulty, Location location, String locationtype, String[] participants, CallbackUI ... callbackUIs){
        // Location in base64 umwandeln und übergeben
        return addWalk(email, title, date, time, description, difficulty, "IMPLEMENT", "google", participants, callbackUIs);
    }

    /**
     * addWalk method that does not implement Serialization
     * @param email
     * @param title
     * @param date
     * @param time
     * @param description
     * @param difficulty
     * @param location
     * @param locationtype
     * @param participants //TODO: test
     * @param callbackUIs
     * @return
     */
    public static Request addWalk(String email, String title, String date, String time, String description, String difficulty, String location, String locationtype, String[] participants, CallbackUI ... callbackUIs){
        Request r = new Request();
        r.setPostBody("email=" + email + "&title=" + title +  "&date=" + date + "&time=" + time
                + "&description=" + description + "&difficulty=" + difficulty
                + "&location=" + location + "&locationtype=" + locationtype + "&participants="+participants);
        r.setType(RequestType.ADD_WALK);
        r.setCallbacks(callbackUIs);
        r.execute();
        return r;
    }

    @Override
    protected void onPostExecute(JsonReader reader) {
        //super.onPostExecute(reader);
        call();
    }

    /**
     * Executes POST and GET Requests in background
     * @param params
     * @return
     */
    @Override
    protected JsonReader doInBackground(String... params) {
        int success;
        switch (type){
            case LOGIN:
                success = POSTData(HOSTURL + LOGIN, postBody);
                System.out.println("Status: " + success);
                if(success != -1){
                    JsonReader r = results.get(success);
                    results.remove(success);
                    try {
                        reader.readUserdata(r);
                        disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return r;
                }
                break;
            case REGISTER:
                success = POSTData(HOSTURL + REGISTER, postBody);
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader reader = new JSONReader();
                    try {
                        this.successful = reader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
            case GET_ALL_WALKS:
                success = GETData(HOSTURL + WALK);
                if(success != -1){
                    JsonReader r = results.get(success);
                    //ToDo: Read all walks!
                    JSONReader reader = new JSONReader();
                    try {
                        successful = reader.readAllWalks(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    return r;
                }
                break;
            case GET_ALL_WALKS_FROM_USER:
                success = POSTData(HOSTURL + WALK, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    //ToDo: Read all user walks!
                    results.remove(success);
                    return r;
                }
                break;
            case GET_PET_BYID:
                success = POSTData(HOSTURL + GETPETS, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        successful = jsonReader.readPets(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
            case ADD_PET:
                success = POSTData(HOSTURL + PET_ADD, postBody);
                if(success != -1) {
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        this.successful = jsonReader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
            case ADD_WALK:
                success = POSTData(HOSTURL + WALK_ADD, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        successful = jsonReader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
            case REGISTER_FOR_WALK:
                success = POSTData(HOSTURL + WALK_PARTICIPANTS, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        successful = jsonReader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
            case UPDATE_PET_LOCATION:
                success = POSTData(HOSTURL + PET_UPDATE_LOCATION, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        successful = jsonReader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
            case SERVERSTATUS:
                success = POSTData(HOSTURL + SERVERSTATUS, postBody );
                if(success != -1){
                    JsonReader r = results.get(success);
                    JSONReader jsonReader = new JSONReader();
                    try {
                        successful = jsonReader.readStatus(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    results.remove(success);
                    disconnect();
                    return r;
                }
                break;
        }
        return null;
    }

    /**
     * POSTData - Function for POST requests
     * @param booty - POST body
     * @param URI - URL
     * @return true/false
     */
    public int POSTData(final String URI ,final String booty){
        URL url = null;
        urlConnection = null;
        try {
            url = new URL(URI);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out, booty);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONReader reader = new JSONReader();

            JsonReader json = new JsonReader(new InputStreamReader(in, "UTF-8"));
            int resultNo = results.size();
            results.add(json);
            return resultNo;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * GETData - Function for GET requests
     * @param URI URL
     * @return true/false
     */
    private int GETData(final String URI){
        URL url = null;
        urlConnection = null;
        try {
            url = new URL(URI);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONReader reader = new JSONReader();
            JsonReader json = new JsonReader(new InputStreamReader(in, "UTF-8"));
            int resultNo = results.size();
            results.add(json);
            return resultNo;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    private void disconnect(){
        urlConnection.disconnect();
    }

    /**
     * Writes to output. Necessary to execute post request
     * @param out OutputStream für den HTTP-Request
     * @param output Ausgabe Parameter für die POST Abfrage
     * @throws IOException Bei Fehlern im OutputStream
     */
    private void writeStream(OutputStream out, String output) throws IOException {
        out.write(output.getBytes());
        out.flush();
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public void setCallbacks(CallbackUI[] callbacks){
        for(CallbackUI callbackUI: callbacks){
            this.callbacks.add(callbackUI);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        System.out.println("Canceled Request");
        disconnect();
        call();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public List<JsonReader> getResults() {
        return results;
    }

    @Override
    public void call() {
        for (CallbackUI callback : callbacks) {
            callback.updateUI(this.type.toString());
            callbacks.remove(callback);
        }
    }
}
