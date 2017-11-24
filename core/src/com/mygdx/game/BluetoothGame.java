package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.States.GameStateManager;
import com.mygdx.game.States.MenuState;

import java.util.ArrayList;

public class BluetoothGame extends ApplicationAdapter {

	GameInterface gameInterface;

	public static final int WIDTH = 600;
	public static final int HEIGHT = 800;
	public static final String TITLE = "EMG Game!";

	private GameStateManager gsm;
	private SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}


	public void dataForGame(ArrayList<Integer> values) {

		for(int i = 0; i < values.size(); i++){
			gameInterface.updatePosition(values.get(i));
		}

	}

	public void isConnected() {
		gameInterface.isConnected();
	}
}
