package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;


public class Pacman {

    private float x, y; // center position of Pacman
    private float radius; // radius of Pacman's collision circle
    private float stateTime; // animation state time
    private final float speed = 650f / 5f; // set speed to 1/5th of the screen width

    private int lives; // pacman lives

    TextureAtlas atlas;
    private TextureRegion ninetyPacman; // mouth open at ninety-degree angle
    private TextureRegion acutePacman; // mouth open at acute angle
    private TextureRegion currentFrame;
    private Direction direction;
    private Map map;

    public Pacman(Map map) {
        // default position
        this.x = map.getTileCenterX(13);
        this.y = map.getTileCenterY(23);
        this.radius = 6;

        this.map = map;
        atlas = new TextureAtlas("pacman.atlas");
        ninetyPacman = atlas.findRegion("pacman_ninety");
        acutePacman = atlas.findRegion("pacman_acute");

        // default state
        lives = 3;
        direction = Direction.RIGHT;
        currentFrame = acutePacman; // default to open mouth
        stateTime = 0;
    }

    public float getCenterX() {
        return x;
    }

    public float getCenterY() {
        return y;
    }

    public Circle getCollisionCircle() {
        return new Circle(x, y, radius);
    }

    public void setDirection(Direction newDirection) {
        direction = newDirection;
    }

    public void setPosition(int row, int column) {
        this.x = map.getTileCenterX(column);
        this.y = map.getTileCenterY(row);
    }

    public void update(float deltaTime) {
        // update position based on direction and speed
        float targetX = x;
        float targetY = y;

        switch (direction) {
            case Direction.UP:
                targetY += speed * deltaTime;
                break;
            case Direction.DOWN:
                targetY -= speed * deltaTime;
                break;
            case Direction.LEFT:
                targetX -= speed * deltaTime;
                break;
            case Direction.RIGHT:
                targetX += speed * deltaTime;
                break;
        }

        // collision check with the map
        if (!map.collisionFree(targetX, targetY, radius, direction)) {
           // System.out.printf("collision free: x: %f, y: %f\n", targetX, targetY);
            x = targetX;
            y = targetY;
        }

        // enter through side tunnel
        if (x < 0) {
            x = map.map[0].length * map.TILE_SIZE - map.TILE_SIZE / 2; // wrap to the right
        } else if (x >= map.map[0].length * map.TILE_SIZE) {
            x = map.TILE_SIZE / 2; // wrap to the left
        }

        updatePacmanAnimationState(deltaTime);

    }

    public void updatePacmanAnimationState(float deltaTime) {
        // update animation frame for mouth state
        // alternate between ninety and acute mouth based on time
        stateTime += deltaTime;
        if (stateTime >= 0.3f) { // toggle between acute and obtuse every 0.3 seconds
            currentFrame = (currentFrame == acutePacman) ? ninetyPacman : acutePacman;
            stateTime = 0; // reset state time
        }
    }

    public void render(SpriteBatch batch) {
        // rotate pac-man based on the current direction
        float rotationAngle = 0;

        switch (direction) {
            case RIGHT:
                rotationAngle = 0; // rotate 90 degrees right
                break;
            case UP:
                rotationAngle = 90; // no rotation for up
                break;
            case LEFT:
                rotationAngle = 180; // rotate 90 degrees left
                break;
            case DOWN:
                rotationAngle = 270; // flip vertically for down
                break;
        }

        float offsetX = 15 / 2f;  // offset from center to bottom-left corner
        float offsetY = 15 / 2f;

        // adjust the position based on the center
        float drawX = x - offsetX;
        float drawY = y - offsetY;

        batch.draw(currentFrame, drawX, drawY, offsetX, offsetY, 15, 15, 1, 1, rotationAngle);
    }

    private void ghostCollision() {
        lives--;
        resetPacmanPosition();
    }

    private void resetPacmanPosition() {
        // reset Pac-Man to the starting position
        x = map.getTileCenterX(13);
        y = map.getTileCenterY(23);
    }

    public Vector2 getPacmanTilePosition(float centerX, float centerY, int tileSize) {
        // convert pixel coordinates to tile coordinates
        float tileX = centerX / tileSize;
        float tileY = centerY / tileSize;

        // Vector2 for Pacman's position
        return new Vector2(tileX, tileY);
    }


}
