package com.pacman.utilities;

import java.util.List;
import com.badlogic.gdx.math.Vector2;

public class PathManager {
    private Pathfinding pathfinding;
    private int[][] map;
    private List<Vector2> path;
    private Vector2 lastTarget;

    public PathManager(int[][] map) {
        this.map = map;
        this.pathfinding = new Pathfinding(); // will need different paths for different ghosts
        this.lastTarget = null;
    }

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    // get a new path
    public void updatePath(Vector2 position, Vector2 target) {
        if (isPathEmpty() || !target.equals(lastTarget)) {
            System.out.println("In PathManager.updatePath(): Proceeding with new target");
            System.out.println("Target. x: " + target.x + ", y: " + target.y);
            lastTarget = target;
            path = pathfinding.aStarPathfinding(position, target, map);
        }
    }


    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public Vector2 getNextMove() {
        return (path != null && path.size() > 1) ? path.get(1) : null;
    }

    public boolean isPathEmpty() {
        return path == null || path.isEmpty();
    }

    public Vector2 moveTowardsTarget(Vector2 position, float deltaTime) {

        if (!isPathEmpty()) {
            Vector2 nextTile = path.get(0); // get the next tile in the path (integer coordinates)
            Vector2 direction = nextTile.cpy().sub(position).nor(); // direction to next tile

            // move towards the next tile
            position.add(direction.scl(deltaTime * 5f));

            // snap to tile once close enough
            if (position.epsilonEquals(nextTile, 0.1f)) {
                //position.set(nextTile); // snap to exact tile position
                path.remove(0); // move to the next step in the path
            }

            return position;
        }
        return null;
    }

}

