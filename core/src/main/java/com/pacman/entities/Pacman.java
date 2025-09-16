package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.pacman.utilities.ServiceLocator;

import java.util.ArrayList;
import java.util.List;


public class Pacman {

    private float centerX, centerY; // center position of Pacman
    private float radius; // radius of Pacman's collision circle
    private float stateTime; // animation state time
    private final float speed = 650f / 5f; // set speed to 1/5th of the screen width
    private final int pacmanTileSize = 15;

    private int lives; // pacman lives

    private Map mapInstance;

    TextureAtlas atlas;
    private TextureRegion ninetyPacman; // mouth open at ninety-degree angle
    private TextureRegion acutePacman; // mouth open at acute angle
    private TextureRegion currentFrame;
    private Direction direction;

    private List<PacmanListener> listeners;

    public Pacman() {
        // default position: place pacman at the center of tile (13, 23)
        this.centerX = Map.getTileCenterX(13);
        this.centerY = Map.getTileCenterY(23);
        this.radius = 6;

        atlas = new TextureAtlas("pacman.atlas");
        ninetyPacman = atlas.findRegion("pacman_ninety");
        acutePacman = atlas.findRegion("pacman_acute");

        // default state
        lives = 3;
        direction = Direction.RIGHT;
        currentFrame = acutePacman; // default to open mouth
        stateTime = 0;

        listeners = new ArrayList<>();

        mapInstance = ServiceLocator.getMapInstance();
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public Circle getCollisionCircle() {
        return new Circle(centerX, centerY, radius);
    }

    public void setDirection(Direction newDirection) {
        direction = newDirection;
    }

    public void setPosition(int row, int column) {
        this.centerX = Map.getTileCenterX(column);
        this.centerY = Map.getTileCenterY(row);
    }

    public void update(float deltaTime) {
        // update position based on direction and speed
        float targetX = centerX;
        float targetY = centerY;

        switch (direction) {
            case UP:
                targetY += speed * deltaTime;
                break;
            case DOWN:
                targetY -= speed * deltaTime;
                break;
            case LEFT:
                targetX -= speed * deltaTime;
                break;
            case RIGHT:
                targetX += speed * deltaTime;
                break;
        }

        float mapWidth = mapInstance.map[0].length * Map.TILE_SIZE;

        // enter through side tunnel
        if (centerX < -radius) {
            centerX = mapWidth - Map.TILE_SIZE / 2f; // wrap to the right
        } else if (centerX > mapWidth - Map.TILE_SIZE / 2f) {
            centerX = Map.TILE_SIZE / 2f; // wrap to the left
        }

        // collision check with the map
        if (Map.collisionFree(targetX, targetY, radius, direction)) {
            centerX = targetX;
            centerY = targetY;
        }

        // notify all observers
        for (PacmanListener listener : listeners) {
            listener.onPacmanMoved(getPacmanLogicalTile());
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

        float offsetX = pacmanTileSize / 2f;  // offset from center to bottom-left corner
        float offsetY = pacmanTileSize / 2f;

        // adjust the position based on the center
        float drawX = centerX - offsetX;
        float drawY = centerY - offsetY;

        batch.draw(currentFrame, drawX, drawY, offsetX, offsetY, pacmanTileSize, pacmanTileSize, 1, 1, rotationAngle);
    }

    private void ghostCollision() {
        lives--;
        resetPacmanPosition();
    }

    private void resetPacmanPosition() {
        // reset Pac-Man to the starting position
        centerX = Map.getTileCenterX(13);
        centerY = Map.getTileCenterY(23);
    }

    public Vector2 getPacmanTilePosition() {
        int tileX = (int) Math.floor((centerX - (pacmanTileSize / 2.0f)) / Map.TILE_SIZE);
        int tileY = (int) Math.floor((centerY - (pacmanTileSize / 2.0f)) / Map.TILE_SIZE);

        int flippedY = Map.rows - 1 - tileY;

        if (tileX >= 0 && tileX < Map.columns &&
            flippedY >= 0 && flippedY < Map.rows) {
            System.out.println("Tile: " + mapInstance.map[flippedY][tileX]);
        } else {
            System.out.println("Out of bounds: (" + tileX + "," + flippedY + ")");
        }

        return new Vector2(tileX, tileY); // return grid position
    }

    public Vector2 getPacmanLogicalTile() {
        int tileX = (int) Math.floor(centerX / Map.TILE_SIZE);
        int tileY = (int) Math.floor(centerY / Map.TILE_SIZE);

        if (mapInstance.isWall(tileX, tileY)) {
            // choose the nearest non-wall neighbor
            Vector2 best = null;
            float bestDist = Float.MAX_VALUE;

            for (Vector2 dir : Map.NEIGHBORS) { // up, down, left, right
                int nx = tileX + (int)dir.x;
                int ny = tileY + (int)dir.y;
                if (!mapInstance.isWall(nx, ny)) {
                    float dx = centerX - (nx * Map.TILE_SIZE + Map.TILE_SIZE/2f);
                    float dy = centerY - (ny * Map.TILE_SIZE + Map.TILE_SIZE/2f);
                    float dist = dx*dx + dy*dy;
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = new Vector2(nx, ny);
                    }
                }
            }
            return best != null ? best : new Vector2(tileX, tileY);
        }
        return new Vector2(tileX, tileY);
    }


    public void addListener(PacmanListener listener) {
        listeners.add(listener);
    }

}
