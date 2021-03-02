package com.github.tiecia.jledcontrol;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ArtNetLEDServer {
    /**
     * Stores all the Art Net LED Devices. Key is the name that is used to reference it. Value is the com.github.tiecia.jledcontrol.ArtNetLEDDevice object.
     */
    private Map<String, ArtNetLEDDevice> devices;

    /**
     * Creates a empty com.github.tiecia.jledcontrol.ArtNetLEDServer
     */
    public ArtNetLEDServer(){
        this.devices = new HashMap();
    }

    /**
     * Creates an com.github.tiecia.jledcontrol.ArtNetLEDServer with predefined devices.
     * @param devices the devices to create the server with.
     */
    public ArtNetLEDServer(Map<String, ArtNetLEDDevice> devices){
        this.devices = devices;
    }

    public void setDevices(Map<String, ArtNetLEDDevice> devices) {
        this.devices = devices;
    }

    public Map<String, ArtNetLEDDevice> getDevices() {
        return devices;
    }

    /**
     * Retrieves a singular ArtNet device stored in the server referenced by the string name.
     * @param name the name of the device to retrieve.
     * @return null if there is no corresponding device. The corresponding {@link ArtNetLEDDevice} if a corresponding device is found.
     */
    public ArtNetLEDDevice getDevice(String name){
        return devices.get(name);
    }

    /**
     * Adds a new ArtNet device to the server.
     * @param name the name of the device to add. This name will be used for retrieval later.
     * @param device the device to add to the server.
     * @return null if there was no previous device called "name".
     *         If there was a previous device called "name", this method returns the overwritten {@link ArtNetLEDDevice}.
     */
    public ArtNetLEDDevice addDevice(String name, ArtNetLEDDevice device){
        return devices.put(name, device);
    }

    /**
     * Removes the corresponding device from the server
     * @param name the name of the device to remove.
     * @return null if no device called "name" existed. The {@link ArtNetLEDDevice} that was removed if a device was removed.
     */
    public ArtNetLEDDevice removeDevice(String name){
        return devices.remove(name);
    }

    /**
     * Adds the {@link Color} data contained in "strip" to the DMX queue of all devices contained in this server.
     * @param strip the {@link Color} data to add to all queues.
     */
    public void addToQueue(Color[] strip){
        for(ArtNetLEDDevice device : this.devices.values()){
            device.addToQueue(strip);
        }
    }

    /**
     * Sends the {@link Color} data contained in "strip" to all devices contained in this server.
     * Equivalent of calling {@link #addToQueue(Color[]) addToQueue(Color[])} then {@link #send() send()}.
     * @param strip the {@link Color} data to send to all devices.
     */
    public void send(Color[] strip){
        for(ArtNetLEDDevice device : this.devices.values()){
            device.send(strip);
        }
    }

    /**
     * Sends the current queue of all devices this server controls.
     */
    public void send(){
        for(ArtNetLEDDevice device : this.devices.values()){
            device.send();
        }
    }
}
