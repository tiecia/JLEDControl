package com.github.tiecia.jledcontrol;

import ch.bildspur.artnet.*;
import java.awt.*;

public class ArtNetLEDDevice {

    private final ArtNetClient artnet;

    /**
     * The array that the colors will be split into and send via DMX. First dimension is each universe.
     * Second dimension is each channel in the universe. The second dimension is always 512 in length.
     */
    private byte[][] dmx;

    /**
     * The hostname to send the data to.
     */
    private String hostname;

    /**
     * The user specified order of RGB values to be send.
     * This value determines the order in which the RGB channels are placed in the DMX array.
     */
    private ColorOrder colorOrder;

    /**
     * The first universe this device sends.
     */
    private int firstUniverse;

    /**
     * The number of lights this device controls.
     */
    private int numOfLights;

    /**
     * Used to keep track of the universe of the last data point when adding data to the queue.
     */
    private int queueUniverse = 0;

    /**
     * Used to keep track of the index of the last data point when adding to the queue.
     */
    private int queueIndex = 0;

    /**
     * User options of color order.
     */
    public enum ColorOrder {
        RGB, BRG, GRB, RBG, GBR, BGR, RGBW
    }

    /**
     * Creates an com.github.tiecia.jledcontrol.ArtNetLEDDevice with the first universe defaulted to "0" and color order defaulted to "RGB".
     * @param numOfLights the number of lights this device services. If controlling a pixel strip this number should
     *                    be the number of pixels on the strip.
     * @param hostname the hostname/IP address of the ArtNet controller.
     */
    public ArtNetLEDDevice(int numOfLights, String hostname){
        this(numOfLights,0, hostname);
    }

    /**
     * Creates an com.github.tiecia.jledcontrol.ArtNetLEDDevice with the color order defaulted to "RGB"
     * @param numOfLights the number of lights this device services. If controlling a pixel strip this number should
     *                    be the number of pixels on the strip.
     * @param firstUniverse the first universe to be sent to the ArtNet device.
     * @param hostname the hostname/IP address of the ArtNet controller.
     */
    public ArtNetLEDDevice(int numOfLights, int firstUniverse, String hostname){
        this(numOfLights, firstUniverse, hostname, ColorOrder.RGB);
    }

    /**
     * Creates an com.github.tiecia.jledcontrol.ArtNetLEDDevice with no default values.
     * @param numOfLights the number of lights this device services. If controlling a pixel strip this number should
     *                    be the number of pixels on the strip.
     * @param firstUniverse the first universe to be sent to the ArtNet device.
     * @param hostname the hostname/IP address of the ArtNet controller.
     * @param colorOrder the order in which the RGB values will be sent to the ArtNet device.
     *                   Reference choices through the {@link ColorOrder} ColorOrder enum.
     */
    public ArtNetLEDDevice(int numOfLights, int firstUniverse, String hostname, ColorOrder colorOrder){
        this.colorOrder = colorOrder;
        this.numOfLights = numOfLights;
        int bytesNeeded = 0;
        switch (colorOrder) {
            case RGBW:
                bytesNeeded = numOfLights*4;
            default:
                bytesNeeded = numOfLights*3;
        }
        this.dmx = new byte[bytesNeeded/512 + 1][512];

        this.artnet = new ArtNetClient(null);
        this.artnet.start();
        this.firstUniverse = firstUniverse;
        this.hostname = hostname;
    }

    /**
     * Adds the specified array of colors to the queue to be sent to the ArtNet device.
     * @param strip the colors to send to the ArtNet device.
     * @throws IllegalArgumentException if the lenghts of parameter "strip" does not equal the number of lights this device controls.
     */
    public void addToQueue(Color[] strip){
        if(strip.length != numOfLights){
            throw new IllegalArgumentException("The Color[] parameter must be the length as the number of lights this device controls");
        }

        for(Color c : strip){
            if(queueIndex > 509){ //Check if next universe is necessary
                queueIndex = 0;
                queueUniverse++;
            }

            switch (this.colorOrder){
                case RGB:
                    dmx[queueUniverse][queueIndex] = (byte) c.getRed();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getGreen();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getBlue();
                    break;
                case GRB:
                    dmx[queueUniverse][queueIndex] = (byte) c.getGreen();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getRed();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getBlue();
                    break;
                case BRG:
                    dmx[queueUniverse][queueIndex] = (byte) c.getBlue();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getRed();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getGreen();
                    break;
                case RBG:
                    dmx[queueUniverse][queueIndex] = (byte) c.getRed();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getBlue();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getGreen();
                    break;
                case GBR:
                    dmx[queueUniverse][queueIndex] = (byte) c.getGreen();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getBlue();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getRed();
                    break;
                case BGR:
                    dmx[queueUniverse][queueIndex] = (byte) c.getBlue();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getGreen();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getRed();
                    break;
                case RGBW:
                    dmx[queueUniverse][queueIndex] = (byte) c.getRed();
                    dmx[queueUniverse][queueIndex+1] = (byte) c.getGreen();
                    dmx[queueUniverse][queueIndex+2] = (byte) c.getBlue();
                    dmx[queueUniverse][queueIndex+3] = (byte) c.getAlpha();
                    queueIndex += 1;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this.colorOrder);
            }
            queueIndex += 3;
        }
    }

    /**
     * Send the current queue to the ArtNet device.
     */
    public void send(){
        for(int i = 0; i<dmx.length; i++){
            artnet.unicastDmx(this.hostname, 0, i + this.firstUniverse, dmx[i]);
        }
        dmx = new byte[dmx.length][512];
        queueUniverse = 0;
        queueIndex = 0;
    }

    /**
     * Adds the array of colors to the queue then sends the queue to the ArtNet device.
     * @param strip the colors to send to the ArtNet device.
     *              Equivalent of calling {@link #addToQueue(Color[]) addToQueue(Color[])} then {@link #send()}  send()}.
     */
    public void send(Color[] strip){
        addToQueue(strip);
        send();
    }
}