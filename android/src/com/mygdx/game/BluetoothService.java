package com.mygdx.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class BluetoothService {

    private BluetoothAdapter bluetoothAdapter;
    private final Activity mActivity;
    private Handler mHandler;
    private int mState;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public BluetoothService(Activity activity, Handler handler){

        mActivity = activity;
        mHandler = handler;
        mState = BluetoothConstants.STATE_NONE;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public synchronized int getState(){
        return mState;
    }

    public synchronized void setState(int state){
        mState = state;
        mHandler.obtainMessage(BluetoothConstants.MESSAGE_STATE_CHANGE,state,-1).sendToTarget();;
    }

    public void enableBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBluetoothIntent,BluetoothConstants.REQUEST_ENABLE_BT);
        }
    }

    public void discoverDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public void stopDiscovering(){
        bluetoothAdapter.cancelDiscovery();
    }

    public synchronized void connect(BluetoothDevice bluetoothDevice) {

        if (mState == BluetoothConstants.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }


        mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
        setState(BluetoothConstants.STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice bluetoothDevice){

        if (mConnectThread != null){
            mConnectThread.cancel();;
            mConnectThread = null;
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(BluetoothConstants.STATE_CONNECTED);

        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothConstants.DEVICE_NAME, bluetoothDevice.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public synchronized void stop(){
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(BluetoothConstants.STATE_NONE);
    }

    public void write(byte[] send){
        ConnectedThread tempThread;
        synchronized (this){
            tempThread = mConnectedThread;
        }
        tempThread.write(send);
    }

    public class ConnectThread extends Thread {

        private final BluetoothDevice bluetoothDevice;
        private BluetoothSocket bluetoothSocket;

        private UUID DEFAULT_UUID =
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
            BluetoothSocket socket = null;

            try {
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            } catch (IOException e) {
                failedConn();
            }
            bluetoothSocket = socket;
        }

        /*public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                bluetoothSocket.connect();
            } catch (IOException connectExemption) {
                failedConn();
            }
            synchronized (BluetoothService.this){
                mConnectThread = null;
            }
            connected(bluetoothSocket,bluetoothDevice);

        }*/

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                if(!bluetoothSocket.isConnected())
                    bluetoothSocket.connect();
            } catch (IOException connectExemption) {

                try {
                    try {
                        bluetoothSocket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,2);
                        bluetoothSocket.connect();
                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {
                    } catch (NoSuchMethodException e) {
                    }
                } catch (IOException closeExemption) {
                    failedConn();
                }
            }
            synchronized (BluetoothService.this){
                mConnectThread = null;
            }
            connected(bluetoothSocket,bluetoothDevice);
        }



        public void cancel(){
            try {
                bluetoothSocket.close();
            }catch (IOException closeExemption){
            }
        }
    }

    public class ConnectedThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket){

            this.bluetoothSocket = socket;
            InputStream temporaryInput = null;
            OutputStream temporaryOutput = null;

            try {
                temporaryInput = socket.getInputStream();
                temporaryOutput = socket.getOutputStream();

            }catch (IOException e){
                e.printStackTrace();
            }
            inputStream = temporaryInput;
            outputStream = temporaryOutput;

        }
        public void run() {
            byte[] buffer = new byte[1024];
            int start = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += inputStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = start; i < bytes; i++) {
                        if(buffer[i] == "!".getBytes()[0]) {
                            mHandler.obtainMessage(BluetoothConstants.MESSAGE_READ, start, i, buffer).sendToTarget();
                            start = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                start = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer){

            try{
                outputStream.write(buffer);
                mHandler.obtainMessage(BluetoothConstants.MESSAGE_WRITE,-1,-1,buffer).sendToTarget();
            }catch (IOException e){
                cantSend();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                cantClose();
            }
        }
    }

    public void failedConn(){
        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_TOAST);
        Bundle bundle= new Bundle();
        bundle.putString(BluetoothConstants.TOAST,"Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        mState = BluetoothConstants.STATE_NONE;
    }
    private void connectionLost() {
        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothConstants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = BluetoothConstants.STATE_NONE;
    }

    public void cantSend(){
        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_TOAST);
        Bundle bundle= new Bundle();
        bundle.putString(BluetoothConstants.TOAST,"Can't send data");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public void cantClose(){
        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_TOAST);
        Bundle bundle= new Bundle();
        bundle.putString(BluetoothConstants.TOAST,"Closing error");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

}
