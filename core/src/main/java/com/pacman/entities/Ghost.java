package com.pacman.entities;

import com.pacman.utilities.FrightenedPathFinder;
import com.pacman.utilities.PathManager;
import com.pacman.utilities.ServiceLocator;
import com.pacman.entities.GhostState;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Ghost implements PacmanListener {
    private static final int TILE_SIZE = 24;

    private Vector2 position;
    private Vector2 target;
    private Vector2 nextMove;
    private Vector2 scatterTarget;
    private Vector2 homeBase;
    private Vector2 pacmanPosition;
    private Direction pacmanDirection;
    private GhostState state;

    private PathManager pathManager;
    private FrightenedPathFinder frightenedPathFinder;

    private GhostType type;
    private TextureRegion normalTexture;
    private TextureRegion blueFrightenedTexture;
    private TextureRegion eatenTexture;
    private TextureRegion whiteFrightenedTexture;

    private final float FRIGHTENED_SPEED = 5f;
    private float frightenedTimer;
    private final float FRIGHTENED_DURATION;
    private final float FLASH_START; // last 2 seconds
    private float flashTimer;
    private boolean flashWhite;


    public Ghost(Vector2 position, GhostType type) {
        this.position = position;
        this.state = GhostState.CHASE;  // Default state
        this.type = type;

        this.pathManager = new PathManager();
        this.frightenedPathFinder = new FrightenedPathFinder();
        this.homeBase = position; // test home position
        this.nextMove = position;
       
        frightenedTimer = 0f;
        FRIGHTENED_DURATION = 6f;
        FLASH_START = 2f; // last 2 seconds
        flashTimer = 0f;
        flashWhite = false;

        loadTextures();
    }

    public void setScatterTarget(Vector2 sTarget) {
        this.scatterTarget = sTarget;
    }

    private void loadTextures() {
        TextureAtlas atlas = new TextureAtlas("ghosts.atlas");

        switch (type) {
            case PINKY:
                this.normalTexture = atlas.findRegion("ghost_pink_soft");
                break;
            case CLYDE:
                this.normalTexture = atlas.findRegion("ghost_orange_soft");
                break;
            case INKY:
                this.normalTexture = atlas.findRegion("ghost_blue_soft");
                break;
            case BLINKY:
                this.normalTexture = atlas.findRegion("ghost_red_soft");
                break;
            default:
                this.normalTexture = atlas.findRegion("ghost_orange_soft");
                break;                
        }

        this.blueFrightenedTexture = atlas.findRegion("Ghost_Vulnerable_Blue");
        this.eatenTexture = atlas.findRegion("Ghost_Eyes_Left");
        this.whiteFrightenedTexture = atlas.findRegion("Ghost_Vulnerable_White");

    }

    public void resetPosition() {
        position = homeBase;
    }

    @Override
    public void onPacmanMoved(Vector2 newPosition, Direction newDirection) {
        pacmanPosition = newPosition.cpy(); // Update target whenever Pacman moves
        pacmanDirection = newDirection;
    }

    private Vector2 computeChaseTarget() {
        switch (type) {

            case BLINKY:
                return pacmanPosition.cpy(); // direct chase

            case PINKY:
                return pacmanPosition.cpy().add(
                    pacmanDirection.toVector().scl(4)
                );

            case INKY:
                Vector2 ahead = pacmanPosition.cpy()
                    .add(pacmanDirection.toVector().scl(2));
                Vector2 blinkyPosition = ServiceLocator.getGhostManager().getBlinky().getPosition();
                Vector2 vec = ahead.cpy().sub(blinkyPosition);
                return blinkyPosition.cpy().add(vec.scl(2));

            case CLYDE:
                float dist = position.dst(pacmanPosition);
                if (dist < 8) {
                    return scatterTarget;
                } else {
                    return pacmanPosition;
                }

            default:
                return pacmanPosition;
        }
    }


    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public void updateTarget(float deltaTime) {

        switch (state) {
            case CHASE:
                target = computeChaseTarget();
                pathManager.updatePath(position, target); // A* Path to Pac-Man
                break;
            case SCATTER:
                target = scatterTarget;
                pathManager.updatePath(position, target); // A* Path to scatter corner
                break;
            case FRIGHTENED:
                updateFrightened(deltaTime);
                frightenedPathFinder.update(position, FRIGHTENED_SPEED, deltaTime);
                break;
            case EATEN:
                target = homeBase;
                pathManager.updatePath(position, target);
                break;
            default:
                break;
        }

        if (state != GhostState.FRIGHTENED) {
            nextMove = pathManager.moveTowardsTarget(position, deltaTime);
            if (nextMove != null) {
                position = nextMove;
            }
        } else {
            nextMove = frightenedPathFinder.move(position, FRIGHTENED_SPEED, deltaTime);
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion textureRegion;

        if (state == GhostState.EATEN) {
            textureRegion = eatenTexture;
        } else if (state == GhostState.FRIGHTENED) {
            textureRegion = flashWhite ? whiteFrightenedTexture : blueFrightenedTexture;
        } else {
            textureRegion = this.normalTexture;
        }

        batch.draw(textureRegion, position.x * TILE_SIZE, position.y * TILE_SIZE);
    }


    public GhostState getState() {
        return this.state;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void setState(GhostState newState) {
        if (newState == GhostState.FRIGHTENED) {
            frightenedTimer = FRIGHTENED_DURATION;
            flashTimer = 0f;
            flashWhite = false;
            frightenedPathFinder.reset();
        }
        this.state = newState;
    }
    
    private void updateFrightened(float delta) {
        frightenedTimer -= delta;

        if (frightenedTimer <= 0f) {
            state = GhostState.CHASE;
            return;
        }

        // flashing near the end
        if (frightenedTimer <= FLASH_START) {
            flashTimer += delta;
            if (flashTimer >= 0.2f) {
                flashWhite = !flashWhite;
                flashTimer = 0f;
            }
        }
    }


}
