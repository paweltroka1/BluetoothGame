package com.mygdx.game.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.mygdx.game.BluetoothGame;

public class GameFragment extends AndroidFragmentApplication
{
    private static BluetoothGame bluetoothGame = null;

    public BluetoothGame getGameInstance(){
        return bluetoothGame;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if( bluetoothGame == null)
        {
            bluetoothGame = new BluetoothGame();
        }
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        return initializeForView(bluetoothGame, config);
    }
}
