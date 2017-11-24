package com.mygdx.game;

/**
 * Created by T on 2017-11-05.
 */
public class BluetoothConstants {

    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_NONE = 0;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 6;

    public static final int REQUEST_ENABLE_BT = 10;
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";
}
