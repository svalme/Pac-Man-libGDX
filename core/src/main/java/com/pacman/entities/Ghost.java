package com.pacman.entities;

import com.pacman.utilities.PathManager;
import static com.pacman.entities.GhostState.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Ghost {
    private static final int TILE_SIZE = 24;

    private Vector2 position;
    private Vector2 target;
    private Vector2 nextMove;
    private Vector2 scatterTarget;
    private Vector2 homeBase;

    private GhostState state;

    private PathManager pathManager;
    private int[][] map;

    private Texture normalTexture;
    private Texture frightenedTexture;
    private Texture eatenTexture;

    private static final float CHASE_SPEED = 80f;
    private static final float SCATTER_SPEED = 80f;
    private static final float FRIGHTENED_SPEED = 40f;
    private static final float EATEN_SPEED = 120f; // back to home


    public Ghost(Vector2 position, Texture texture, int[][] map) {
        this.position = position;
        this.state = CHASE;  // Default state
        this.map = map;
        this.pathManager = new PathManager(map);

        this.scatterTarget = new Vector2(12, 15); // test scatter corner
        this.homeBase = new Vector2(14, 15); // test home position
        this.nextMove = homeBase;

        this.normalTexture = texture;
       // loadTextures();
    }
/*
    private void loadTextures() {
        frightenedTexture = new Texture("ghost_frightened.png");
        eatenTexture = new Texture("ghost_eaten.png");
    }
*/

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public void updateTarget(Vector2 pacmanPosition, float deltaTime) {
        //System.out.println("Ghost state: " + this.state);
        switch (state) {
            case CHASE:
                target = pacmanPosition;
                // target = pacman.getPacmanTilePosition()? but you need a reference to pacman
                // maybe a coordination class that has a reference to pacman instead? then we use that class if necessary?
                pathManager.updatePath(position, target); // A* Path to Pac-Man
                break;

            case SCATTER:
                target = scatterTarget;
                pathManager.updatePath(position, target); // A* Path to scatter corner
                break;

            case FRIGHTENED:
                if (nextMove == null /* || reachedTarget() */ ) {
                    nextMove = getRandomTarget(); // Pick a random direction
                }
                position.add(nextMove.cpy().scl(deltaTime * 100f)); // Move randomly
                return; // No A*

            case EATEN:
                target = homeBase;
                pathManager.updatePath(position, target);
                break;
        }

        nextMove = pathManager.moveTowardsTarget(position, deltaTime);
        if (nextMove != null) {
            position = nextMove;
        }
    }

    // for use with getRandomTarget()
    private boolean isValidTarget(Vector2 target) {
        int tileX = (int) target.x;
        int tileY = (int) target.y;

        // Check if the tile is within map bounds and walkable
        return tileX >= 0 && tileX < map.length &&
            tileY >= 0 && tileY < map[0].length &&
            (map[tileX][tileY] == 0 || map[tileX][tileY] == 26 || map[tileX][tileY] == 27);  // 0: EMPTY, 26: PELLET_LARGE, 27: PELLET_SMALL
    }

    private Vector2 getRandomTarget() {
        Random rand = new Random();
        Vector2 randomTarget;
        do {
            int x = rand.nextInt(map.length);
            int y = rand.nextInt(map[0].length);
            randomTarget = new Vector2(x, y);
        } while (!isValidTarget(randomTarget)); // Keep picking until it's valid

        return randomTarget;
    }

    public void move(float deltaTime) {
        //pathManager.moveTowardsTarget(position, nextMove, deltaTime);
    }

    public void render(SpriteBatch batch) {
        Texture textureToUse =// (state == GhostState.FRIGHTENED) ? frightenedTexture :
                               // (state == GhostState.EATEN) ? eatenTexture :
                                normalTexture;
        batch.draw(textureToUse, position.x * TILE_SIZE, position.y * TILE_SIZE);
    }

    public void setState(GhostState newState) {
        this.state = newState;
    }

    public GhostState getState() {
        return this.state;
    }

    public Vector2 getPosition() {
        return position;
    }

}
