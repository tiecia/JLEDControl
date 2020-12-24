package examples;

import jledcontrol.ArtNetLEDDevice;
import java.awt.*;

public class Example2 {
    public static void main(String[] args) {
        int numOfLights = 100;

        //Create an ArtNetLEDDevice with specified number of lights and hostname.
        ArtNetLEDDevice device = new ArtNetLEDDevice(numOfLights, "192.168.68.178");

        //Create a Color array of the same length as the number of lights
        Color[] c = new Color[numOfLights];

        //Populate array
        for (int i = 0; i<c.length; i++) {
            c[i] = new Color(1, 49, 20);
        }

        //Send the color data to the device
        device.send(c);
    }
}
