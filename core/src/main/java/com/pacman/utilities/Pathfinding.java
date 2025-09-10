package com.pacman.utilities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.Map;
import com.pacman.screens.WallAtlasRegion;

import java.util.*;

public class Pathfinding {

    private Vector2[] DIRECTIONS = {
        new Vector2(1, 0),  // right
        new Vector2(-1, 0), // left
        new Vector2(0, 1),  // down
        new Vector2(0, -1)  // up
    };

    private int[][] map = ServiceLocator.getMapInstance().map;

    public List<Vector2> aStarPathfinding(Vector2 start, Vector2 target) {

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        HashMap<Object, Object> allNodes = new HashMap<>(); // track nodes by position

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll(); // get the node with lowest fCost

            if ((int)current.position.x == (int)target.x && (int)current.position.y == (int)target.y) { // assumes vectors have decimal values instead of integer values; technically, Vector2 has float coordinates
                return reconstructPath(current);
            }

            for (Vector2 neighborPos : getNeighbors(current.position)) {
                float gCost = current.gCost + 1; // movement cost of 1 for each tile

                Node neighbor = (Node) allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Float.MAX_VALUE, heuristic(neighborPos, target))); // if the neighbor node exists, get it, otherwise, create one

                if (gCost < neighbor.gCost) { // better path found
                    neighbor.gCost = gCost;
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;

                    allNodes.put(neighborPos, neighbor);
                    openSet.remove(neighbor); // remove to update priority
                    openSet.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // no path found
    }

    public List<Vector2> getNeighbors(Vector2 position) {
        List<Vector2> neighbors = new ArrayList<>();
        int x = (int) position.x;
        int y = (int) position.y;

        for (Vector2 dir : DIRECTIONS) { // checking all 4 possible directions
            int newX = x + (int)dir.x;
            int newY = y + (int)dir.y;

            if (isValidMove(newX, newY)) {
                neighbors.add(new Vector2(newX, newY));
            }
        }

        return neighbors;
    }

    private boolean isValidMove(int x, int y) {

        if (x < 0 || y < 0 || x >= map[0].length || y >= map.length) { // check bounds
            return false;
        }

        // check if tile is walkable
        int tile = map[map.length - 1 - y][x];

        return tile == WallAtlasRegion.EMPTY.ordinal() ||
            tile == WallAtlasRegion.PELLET_SMALL.ordinal() ||
            tile == WallAtlasRegion.PELLET_LARGE.ordinal() ||
            tile == WallAtlasRegion.JAIL_DOOR.ordinal(); // ghost allowed through jail doors
    }

    private float heuristic(Vector2 a, Vector2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);  // Manhattan Distance
    }

    private List<Vector2> reconstructPath(Node currentNode) {
        List<Vector2> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode.position);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
