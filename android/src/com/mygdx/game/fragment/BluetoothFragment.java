package com.mygdx.game.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.BluetoothService;
import com.mygdx.game.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothFragment extends Fragment implements View.OnClickListener {

    private BluetoothService bluetoothService;
    private Spinner spinner;
    private Button b1,b2;
    private ArrayList<BluetoothDevice> deviceArrayList;
    private ArrayAdapter<String> nameAdapter;
    private List<String> nameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_fragment,container,false);
        b1 = (Button) view.findViewById(R.id.button_discover);
        b1.setOnClickListener(this);
        b2 = (Button) view.findViewById(R.id.button_connect);
        b2.setOnClickListener(this);
        spinner = (Spinner) view.findViewById(R.id.spinner_devices);

        nameList = new ArrayList<>();
        deviceArrayList = new ArrayList<>();
        nameAdapter = new ArrayAdapter<String>(AndroidLauncher.instance,android.R.layout.simple_spinner_item,nameList);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(nameAdapter);

        bluetoothService = AndroidLauncher.instance.getBluetoothService();

        return view;
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceArrayList.add(device);
                nameList.add(device.getName());
                nameAdapter.notifyDataSetChanged();
                Toast.makeText(AndroidLauncher.instance, "Nazwa: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_connect:
                if(deviceArrayList.isEmpty() == false)
                    bluetoothService.connect(deviceArrayList.get(spinner.getSelectedItemPosition()));
                Toast.makeText(AndroidLauncher.instance,"Connecting to " + deviceArrayList.get(spinner.getSelectedItemPosition()).getName()
                         + " with spinner number " + spinner.getSelectedItemPosition(),Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_discover:
                nameList.clear();
                deviceArrayList.clear();
                bluetoothService.stop();
                bluetoothService.discoverDevices();
                Toast.makeText(AndroidLauncher.instance,"Discovering",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
