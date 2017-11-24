package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.BluetoothGame;

/**
 * Created by T on 2017-10-17.
 */
public class MenuState extends State {

    private Texture background;
    //private Texture button;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("sky.png");
        camera.setToOrtho(false, BluetoothGame.WIDTH,background.getHeight());

    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(background,0,0);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
