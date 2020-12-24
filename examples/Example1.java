package examples;

import jledcontrol.ArtNetLEDDevice;
import java.awt.*;

public class Example1 {
    public static void main(String[] args) {
        int numOfLights = 500;

        //Create an ArtNetLEDDevice with specified number of lights, hostname, first universe, and color order.
        ArtNetLEDDevice device = new ArtNetLEDDevice(numOfLights,0, "192.168.68.178", ArtNetLEDDevice.ColorOrder.GRB);

        //Create a Color array of the same length as the number of lights
        Color[] c = new Color[numOfLights];

        //Populate array
        for (int i = 0; i<c.length; i++) {
            c[i] = new Color(8, 226, 97);
        }

        //Add color data to queue
        device.addToQueue(c);

        //Send the queue
        device.send();
    }
}
