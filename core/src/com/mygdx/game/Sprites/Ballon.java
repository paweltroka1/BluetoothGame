package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by T on 2017-10-17.
 */
public class Ballon {
    private static final int GRAVITY = -5;
    private static final int MOVEMENT = 100;
    private Vector3 postition;
    private Vector3 velocity;
    private Texture ballon;


    public Ballon(int x, int y){

        postition = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        ballon = new Texture("ballon.png");
    }
    public void update(float dt) {
        if (postition.y <= 0)
        {
            postition.y = 0;
            postition.add(MOVEMENT * dt,0, 0);

        }
        else {
            velocity.add(0, GRAVITY, 0);
            velocity.scl(dt);
            postition.add(MOVEMENT * dt, velocity.y, 0);
            velocity.scl(1 / dt);
        }

    }
    public void fallDown() {
        if (postition.y == 0) {
            postition.add(0, 1, 0);
        }
        velocity.y = 150;



    }

    public Vector3 getPostition() {
        return postition;
    }

    public Texture getTexture() {
        return ballon;
    }

    public void updateBallonPosition(int data) {

        if (postition.y == 0) {
            postition.add(0, 1, 0);
        }
        velocity.y = data;

    }
}
