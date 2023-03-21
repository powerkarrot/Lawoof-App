package com.example.karrot.lawoof.Util.Serialization;

import android.os.AsyncTask;
import android.util.Base64;

import com.example.karrot.lawoof.Util.CallableActivity.CallbackTask;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 *  Created by ex3c on 04.03.2017.
 */

/**
 * Handles Object Serialization and Deserialization
 * @param <T>
 */
public class SerializationTask<T>{

    protected enum Operation {SERIALIZE, DESERIALIZE}

    private List<CallbackUI> callbacks = new ArrayList<CallbackUI>();

    private Serialization<T, String> serialization;
    private Serialization<String, T> deserialization;


    public SerializationTask(CallbackUI ... callbackUIs){
        Collections.addAll(callbacks, callbackUIs);
        serialization = new Serialization<T, String>(Operation.SERIALIZE, callbacks);
        deserialization = new Serialization<String, T>(Operation.DESERIALIZE, callbacks);
    }

    /**
     * Serializes an undefined amount of objects into a base64 String.
     * @param params Object to be serialized
     */
    @SafeVarargs
    public final void serialize(T... params){
        if(params.length >= 1)
            serialization.execute(params);
    }

    /**
     * Deserializes an undefined amount of base64 strings into Objects.
     * @param params Base64 string to be deserialized
     */
    public void deserialize(String ... params){
        if(params.length >= 1)
            deserialization.execute(params);
    }

    /**
     * Returns the base64 encoded strings.
     * @return List of base64 strings
     */
    public List<String> getBase64EncodedObjects(){
        return this.serialization.getResults();
    }

    /**
     * Returns the unencoded objects.
     * @return List of objects
     */
    public List<T> getBase64DecodedObjects(){
        return this.deserialization.getResults();
    }

    /**
     * Asynctask for our serialization.
     * @param <T> Input Parameter for Serialization - Objecttype, Deserialization - String
     * @param <N> Output Parameter for Serialization - String, Deserialization - Objecttype
     */
    private class Serialization<T, N> extends AsyncTask<T, Void, N> implements CallbackTask{

        Operation operation;

        List<CallbackUI> callbacks;
        List<N> results = new ArrayList<N>();

        public Serialization(Operation operation){this.operation = operation;}

        /**
         * Constructor to build our Serialization Asynctask
         * @param operation Serialize or Deserialize
         * @param callbacks List of Callbacks
         */
        public Serialization(Operation operation, List<CallbackUI> callbacks){
            this.operation = operation;
            this.callbacks = callbacks;
        }

        /**
         * Converts a serializable object into an byteArray.
         * @param t Object
         * @return byteArray
         */
        public byte[] toByteArray(T t){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(t);
                out.flush();
                byte[] result = bos.toByteArray();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Converts an byteArray into an Object
         * @param bytes Input ByteArray
         * @return Object
         */
        public N toObject(byte[] bytes){
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                N result = (N)in.readObject();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Converts a byteArray into a base64 encoded string
         * @param bytes Input byteArray
         * @return base64 encoded string
         */
        public java.lang.String getBase64EncodedString(byte[] bytes){
            java.lang.String result = Base64.encodeToString(bytes, Base64.URL_SAFE);
            return result;
        }

        /**
         * Converts a base64 string into a byteArray
         * @param string base64 String
         * @return byteArray
         */
        public  byte[] getBase64DecodedBytes(java.lang.String string){
            byte[] result = Base64.decode(string, Base64.URL_SAFE);
            return result;
        }

        /**
         * Returns the results of the operation
         * @return List of Objects or Strings
         */
        public List<N> getResults() {
            return results;
        }

        /**
         * Calls the registered callables after successfull execution
         * @param n
         */
        @Override
        protected void onPostExecute(N n) {
            super.onPostExecute(n);
            call();
        }

        /**
         * Serializes or deserializes objects based of choosen operation
         * @param params Objects
         * @return Results
         */
        @Override
        protected N doInBackground(T... params) {
            N result = null;
            switch (operation){
                case SERIALIZE:
                    for (T t : params){
                        if(t instanceof Serializable){
                            byte[] bytes = toByteArray(t);
                            result = (N)getBase64EncodedString(bytes);
                            System.out.println("Place BASE64: " + result);
                            results.add(result);
                        }
                    }
                    break;
                case DESERIALIZE:
                    for (T t: params){
                        if(t instanceof String){
                            String base64 = (String) t;
                            byte[] bytes = getBase64DecodedBytes(base64);
                            result = toObject(bytes);
                            System.out.println("DESERIALIZE: " + result);
                            results.add(result);
                        }
                    }
                    break;
            }
            return null;
        }

        @Override
        public void call() {
            for(CallbackUI callback : callbacks){
                callback.updateUI();
            }
        }
    }
}
