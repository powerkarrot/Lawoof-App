package com.example.karrot.lawoof.Util.MQTT;

import com.example.karrot.lawoof.Content.User;

import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Created by karrot on 04/03/2017.
 * Helper class for centralized MQTT configuration
 */

public class Config {
    public static String server = "tcp://test.mosquitto.org:1883";
    //public static String friendlyName = "Dudette";
    public static String friendlyName = MqttClient.generateClientId()+User.name+"app";
    // public static String topic = "powerkarrot@gmail.com";
    public static String topic = User.getEmail();
}


