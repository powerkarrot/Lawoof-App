package com.example.karrot.lawoof.Util.MQTT;

import com.example.karrot.lawoof.Activities.MainActivity;
import com.example.karrot.lawoof.Content.Pet;
import com.example.karrot.lawoof.Content.User;
import com.example.karrot.lawoof.Fragments.PetDetailFragment;
import com.example.karrot.lawoof.Util.Help.LatLngTime;
import com.example.karrot.lawoof.Util.Help.LostPets;
import com.example.karrot.lawoof.Util.Help.MarkerData;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * @author: Created by karrot on 04/03/2017.
 */

/**
 * Service to handle MQTT Messages
 */

public class MQTTClient implements MqttCallback {

    MainActivity ma;
    User user;
    org.eclipse.paho.client.mqttv3.MqttClient client;

    protected MarkerOptions markerOptions = new MarkerOptions();
    protected ArrayList<Marker> marker = new ArrayList<>();
    public Hashtable<Pet, Marker> markerHashtable = new Hashtable<>();


    public boolean received = false;

    /**
     * Connects to MWTT broker and subscribes to topic
     * @return
     * @throws Exception
     */
    public boolean connect() throws  Exception{
        MqttConnectOptions options = new MqttConnectOptions();

        //Sets whether the client and server should remember state across restarts and reconnects
        options.setCleanSession(false);
        //Sets the connection timeout value (in seconds)
        options.setConnectionTimeout(30);
        //Sets the "keep alive" interval (in seconds)
        options.setKeepAliveInterval(60);

        MemoryPersistence memoryPersistence = new MemoryPersistence();
        client = new org.eclipse.paho.client.mqttv3.MqttClient(Config.server, Config.friendlyName, memoryPersistence);
        client.setCallback(this);
        client.connect();
        client.subscribe(Config.topic);
        System.out.println("Mqtt connected");
        System.out.println("Mqtt subscribed to " + Config.topic);
        return true;
    }

    //WE NEVAH DISCONNECT
    public  boolean disconnect() throws  Exception{
        client.disconnect();
        return true;
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection was lost: " + cause);
    }

    /** Callback method to handle incoming messages.
     *  Parses messages which are either positions from own pets or positions from
     *  lost pets from other people.
     * TODO: Handle positions from other users
     * */
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("MQTTClient:MESSAGE ARRIVED");
        System.out.println("MQTTClient:MESSAGE IS; " + message.toString() + " TOPIC: " + topic);

        String mes = message.toString();
        System.out.println("MQTTClient:Received MQTT Message: " + mes);
        String[] owner;
        owner = mes.split("=");
        String[] whichpet ;
        whichpet = owner[1].split(":");
        String[] parse;
        parse = whichpet[1].split("-");
        String position = parse[0];
        String time = parse[1];

        LatLngTime lt = new LatLngTime(position, time);

        System.out.println("MQTTClient: Position: "+ lt.position.toString());
        System.out.println("MQTTClient: Time: "+ lt.time);

        //TODO: change logic to this!
        //if(!MainActivity.getLostPets().contains(lostPets)) MainActivity.getLostPets().add(lostPets);


        //Own pets
        for(Pet pet : User.getPets()){
            System.out.println("MQTTClient: pet name == mqtt mes pet?" + pet.name.toString().equals(whichpet[0].toString()));
           // if (pet.name.toString().equals(topic.toString())){
            if(pet.name.toString().equals(whichpet[0])) {
                pet.places.add(lt);
                System.out.println("MQTTClient: " + pet.toString() + " place is " + pet.places.toString());
                break;
                // not own pet, so its a lost pet :P
            } else {
            }
        }

        //TODO: Fix this
        //Someone elses pet
        if(!owner[0].toString().equals(User.email.toString()))  {
            System.out.println("MQTTClient:  Lost Pet belonging to: " + owner[0]);

            Pet pet = new Pet();
            pet.places.add(lt);
            pet.name = whichpet[0];
            LostPets lostPets = new LostPets(owner[0], pet);
            //first lost pet
            if (MainActivity.getLostPets().size() == 0) {
                MainActivity.getLostPets().add(lostPets);
                MainActivity.markerHashTable.put(pet, new MarkerData());
                System.out.println("MQTTClient: first lost " + lostPets.pet.name + " places: " + lostPets.pet.places);
                System.out.println("MQTTClient: first : " + MainActivity.getLostPets().toString());
            }
            for (LostPets lp : MainActivity.getLostPets()) {
               // if (lp.equals(lostPets)) {
                //update lost pet position
                if((lp.owner.toString().equals(owner[0].toString()) && lp.pet.name.toString().equals(whichpet[0].toString()))) {
                    lp.pet.places.add(lt);
                    System.out.println("MQTTClient: added position for " + lp.pet.name + " places: " + lp.pet.places);
                    System.out.println("MQTTClient: added : " + MainActivity.getLostPets().toString());
                    break;
                } else { //add lost pet
                    MainActivity.getLostPets().add(lostPets);
                    MainActivity.markerHashTable.put(pet, new MarkerData());
                    System.out.println("MQTTClient: new lost " + lostPets.pet.name + " places: " + lostPets.pet.places);
                    System.out.println("MQTTClient: new : " + MainActivity.getLostPets().toString());
                }
            }
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    /**
     * Publishes positions from own lost pets
     * TODO: publish own position
     * @param mes
     * @param lost
     */
    public void publish(LatLngTime mes, Pet lost) {
        String pet = lost.name + ":";
        String owner = User.email + "=";
        MqttMessage message = new MqttMessage();
        message.setPayload(owner.concat(pet.concat(mes.toString())).getBytes());
        message.setQos(2);
        message.setRetained(false);
        MqttDeliveryToken token = null;
        for (String s : PetDetailFragment.emailcontacts) {
            MqttTopic mytopic = client.getTopic(s);
            try {
                // publish message to broker
                token = mytopic.publish(message);
                System.out.println("MQTT Client: Published lost pet: " + message + " to: " + mytopic);
                // Wait until the message has been delivered to the broker
                token.waitForCompletion();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}