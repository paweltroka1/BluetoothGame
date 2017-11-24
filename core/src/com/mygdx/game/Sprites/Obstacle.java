package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by T on 2017-10-18.
 */
public class Obstacle {
    public static final int CLOUD_WIDTH = 300;
    private static final int GAP_BETTWEEN = 200;
    private static final int LOWEST_GAP = 20;
    private static final int GAP_POSITION = 100;
    private Texture cloudTop, cloudBottom;
    private Vector2 topPosCloud, bottomPosCloud;
    Random rand;

    public Obstacle(float x, float y){
        cloudTop = new Texture("cloud1.jpg");
        cloudBottom = new Texture("cloud1.jpg");
        rand = new Random();

        topPosCloud = new Vector2(x, rand.nextInt(GAP_POSITION) + GAP_BETTWEEN + LOWEST_GAP);
        bottomPosCloud = new Vector2(x , topPosCloud.y - GAP_BETTWEEN - 20);

    }

    public Texture getCloudTop() {
        return cloudTop;
    }

    public Texture getCloudBottom() {
        return cloudBottom;
    }

    public Vector2 getTopPostCloud() {
        return topPosCloud;
    }

    public Vector2 getBottomPosCloud() {
        return bottomPosCloud;
    }

    public void changePosition(float x){
        topPosCloud.set(x, rand.nextInt(GAP_POSITION) + GAP_BETTWEEN + LOWEST_GAP);
        bottomPosCloud.set(x , topPosCloud.y - GAP_BETTWEEN - 20);
    }
}
