package com.pacman.utilities;

import java.util.List;
import com.badlogic.gdx.math.Vector2;
import com.pacman.test.PathPrinter;

public class PathManager {
    private Pathfinding pathfinding;
    private int[][] map;
    private List<Vector2> path;
    private Vector2 lastTarget;

    public PathManager() {
        this.map = ServiceLocator.getMapInstance().map;
        this.pathfinding = new Pathfinding(); // will need different paths for different ghosts
        this.lastTarget = null;
    }

    // get a new path
    public void updatePath(Vector2 position, Vector2 target) {
        if (isPathEmpty() || !target.equals(lastTarget)) {
            lastTarget = target;
            path = pathfinding.aStarPathfinding(position, target);
            PathPrinter.printPath(path, map);
        }
    }

    public boolean isPathEmpty() {
        return path == null || path.isEmpty();
    }

    public Vector2 moveTowardsTarget(Vector2 position, float deltaTime) {

        if (!isPathEmpty()) {
            Vector2 nextTile = path.getFirst(); // get the next tile in the path (integer coordinates)
            Vector2 direction = nextTile.cpy().sub(position).nor(); // direction to next tile

            position.add(direction.scl(deltaTime * 5f)); // move towards the next tile

            if (position.epsilonEquals(nextTile, 0.1f)) { // snap to tile once close enough
                path.removeFirst(); // move to the next step in the path
            }

            return position;
        }
        return null;
    }

}

