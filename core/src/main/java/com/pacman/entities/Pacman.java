package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.Direction;
import com.pacman.entities.PacmanState;
import com.pacman.screens.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
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
    private PacmanState state;

    private final float speed = 650f / 5f; // set speed to 1/5th of the screen width
    private final int pacmanTileSize = 15;

    private TextureAtlas atlas;
    private TextureRegion currentFrame;
    private TextureRegion ninetyPacman; // mouth open at ninety-degree angle
    private TextureRegion acutePacman; // mouth open at acute angle
    private Animation<TextureRegion> deathAnimation;

    private Direction direction;

    private ArrayList<PacmanListener> listeners;

    public Pacman() {
        // default position: place pacman at the center of tile (13, 23)
        this.centerX = Map.getTileCenterX(13);
        this.centerY = Map.getTileCenterY(23);
        this.radius = 6;

        atlas = new TextureAtlas("pacman.atlas");
        ninetyPacman = atlas.findRegion("pacman_ninety");
        acutePacman = atlas.findRegion("pacman_acute");
        deathAnimation = new Animation<TextureRegion>(0.3f, atlas.findRegions("pacman_death"));
        deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        currentFrame = acutePacman; // default to open mouth
        
        stateTime = 0;
        state = PacmanState.ALIVE;
        direction = Direction.RIGHT;
        
        listeners = new ArrayList<>();
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
        this.centerY = Map.getTileCenterY(row);
        this.centerX = Map.getTileCenterX(column);
    }

    public void resetPosition() {
        // reset Pac-Man to the starting position
        setPosition(23, 13);
    }

    public void update(float deltaTime) {
        if (state == PacmanState.DYING) {
            updateDeath(deltaTime);
            return;
        }

        if (state == PacmanState.DEAD) return;

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
            default:
                break;
        }

        float mapWidth = Map.columns * Map.TILE_SIZE;

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
            listener.onPacmanMoved(getPacmanLogicalTile(), direction);
        }

        updatePacmanAnimationState(deltaTime);
        checkPelletCollision();

    }

    public void updatePacmanAnimationState(float deltaTime) {
        // update animation frame for mouth state
        // alternate between ninety and acute mouth based on time
        stateTime += deltaTime;
        if (stateTime >= 0.3f) { // toggle between acute and obtuse 
            currentFrame = (currentFrame == acutePacman) ? ninetyPacman : acutePacman;
            stateTime = 0; // reset state time
        }
    }

    public void render(SpriteBatch batch) {
        // rotate pac-man based on the current direction
        float rotationAngle;

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
                rotationAngle = 270; // flip vertically to go down
                break;
            default:
                rotationAngle = 0;
                break;
        }

        float offsetX = pacmanTileSize / 2f;  // offset from center to bottom-left corner
        float offsetY = pacmanTileSize / 2f;

        // adjust the position based on the center
        float drawX = centerX - offsetX;
        float drawY = centerY - offsetY;

        batch.draw(currentFrame, drawX, drawY, offsetX, offsetY, pacmanTileSize, pacmanTileSize, 1, 1, rotationAngle);
    }

    private void updateDeath(float delta) {
        stateTime += delta;

        currentFrame = deathAnimation.getKeyFrame(stateTime, false);

        if (deathAnimation.isAnimationFinished(stateTime)) {
            state = PacmanState.DEAD;
        }
    }

    private void checkPelletCollision() {
        Vector2 position = getPacmanMapTile();
        int tileX = (int)(position.x);
        int tileY = (int)(position.y);

        if (Map.isPellet(tileX, tileY)) {
            ServiceLocator.getGameManager().onPelletEaten(tileX, tileY, Map.isPowerPellet(tileX, tileY));
        }
    }

    public void die() {
        if (state == PacmanState.DYING) return;
        state = PacmanState.DYING;
        stateTime = 0;
    }

    public void revive() {
        state = PacmanState.ALIVE;
        currentFrame = acutePacman;
        resetPosition();
    }

    public boolean isDead() {
        return state == PacmanState.DEAD;
    }

    public PacmanState getState() {
        return state;
    }

    public Vector2 getPacmanMapTile() {
        int tileX = (int)(centerX / Map.TILE_SIZE);
        int tileY = (int)(centerY / Map.TILE_SIZE);

        int mapY = Map.rows - 1 - tileY;

        return new Vector2(tileX, mapY);
    }

    public Vector2 getPacmanTilePosition() {
        int tileX = (int) Math.floor((centerX - (pacmanTileSize / 2.0f)) / Map.TILE_SIZE);
        int tileY = (int) Math.floor((centerY - (pacmanTileSize / 2.0f)) / Map.TILE_SIZE);

        int flippedY = Map.rows - 1 - tileY;

        if (tileX >= 0 && tileX < Map.columns &&
            flippedY >= 0 && flippedY < Map.rows) {
        } 

        return new Vector2(tileX, tileY); // return grid position
    }

    public Vector2 getPacmanLogicalTile() {
        int tileX = (int) Math.floor(centerX / Map.TILE_SIZE);
        int tileY = (int) Math.floor(centerY / Map.TILE_SIZE);

        if (Map.isWall(tileX, tileY)) {
            // choose the nearest non-wall neighbor
            Vector2 best = null;
            float bestDist = Float.MAX_VALUE;

            for (Vector2 dir : Map.NEIGHBORS) { // up, down, left, right
                int nx = tileX + (int)dir.x;
                int ny = tileY + (int)dir.y;
                if (!Map.isWall(nx, ny)) {
                    float dx = centerX - (nx * Map.TILE_SIZE + Map.TILE_SIZE/2f);
                    float dy = centerY - (ny * Map.TILE_SIZE + Map.TILE_SIZE/2f);
                    float dist = dx * dx + dy * dy;
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
