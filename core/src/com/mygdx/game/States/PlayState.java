package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.BluetoothGame;
import com.mygdx.game.GameInterface;
import com.mygdx.game.Sprites.Ballon;
import com.mygdx.game.Sprites.Obstacle;

/**
 * Created by T on 2017-10-17.
 */
public class PlayState extends State implements GameInterface {
    private Ballon ballon;
    Texture background;

    private static final int CLOUDS_SPACING = 200;
    private static final int CLOUND_COUNT = 4;
    private Array<Obstacle> clouds;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        ballon = new Ballon(0,0);
        background = new Texture("sky.png");
        camera.setToOrtho(false, BluetoothGame.WIDTH,background.getHeight());

        clouds = new Array<Obstacle>();

        for(int i = 1; i <= CLOUND_COUNT; i++){
            clouds.add(new Obstacle(i * (CLOUDS_SPACING + Obstacle.CLOUD_WIDTH), 10));
        }

    }


    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched())
            ballon.fallDown();
    }

    @Override
    public void update(float dt) {
        handleInput();
        ballon.update(dt);
        camera.position.x = ballon.getPostition().x + 10;

        for(Obstacle obstacle : clouds) {
            if (camera.position.x - (camera.viewportWidth / 2) >= obstacle.getTopPostCloud().x + Obstacle.CLOUD_WIDTH) {
                obstacle.changePosition(obstacle.getTopPostCloud().x + (CLOUDS_SPACING + Obstacle.CLOUD_WIDTH)*CLOUND_COUNT);
            }
        }

        camera.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        //sb.draw(background,0,0,BtGame.WIDTH,background.getHeight());
        sb.draw(background,camera.position.x - (camera.viewportWidth/2),0,BluetoothGame.WIDTH,background.getHeight());
        sb.draw(ballon.getTexture(),ballon.getPostition().x - (camera.viewportWidth/2),ballon.getPostition().y,100,100);
        for(Obstacle obstacle : clouds){
            for(int i = 0; i < obstacle.getTopPostCloud().y/50; i++){
                int val = i % 2 == 1 ? i * -5 : i * 5;
                sb.draw(obstacle.getCloudTop(), obstacle.getTopPostCloud().x + val, obstacle.getTopPostCloud().y + i*50,obstacle.getCloudTop().getWidth()/2,obstacle.getCloudTop().getHeight()/2);
            }
            for(int i = 0; i < obstacle.getBottomPosCloud().y/5; i++){
                int val = i % 2 == 1 ? i * -5 : i * 5;
                sb.draw(obstacle.getCloudBottom(), obstacle.getBottomPosCloud().x + val, obstacle.getBottomPosCloud().y - i*50,obstacle.getCloudBottom().getWidth()/2,obstacle.getCloudBottom().getHeight()/2);
            }
        }

        sb.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void isConnected() {

    }

    @Override
    public void updatePosition(int data) {
        int y = (Gdx.graphics.getHeight() * data)/4095;
        ballon.updateBallonPosition(y);
    }
}
