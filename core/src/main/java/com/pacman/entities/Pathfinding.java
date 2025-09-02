package com.pacman.entities;

import com.pacman.entities.*;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.WallAtlasRegion;


import java.util.*;

public class Pathfinding {
    private static final Vector2[] DIRECTIONS = {
        new Vector2(1, 0),  // right
        new Vector2(-1, 0), // left
        new Vector2(0, 1),  // down
        new Vector2(0, -1)  // up
    };

    public List<Vector2> aStarPathfinding(Vector2 start, Vector2 target, int[][] map) {

        System.out.println("Start: " + start + ", Target: " + target);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Vector2, Node> allNodes = new HashMap<>(); // track nodes by position

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll(); // get the node with lowest fCost
            //System.out.println("Current node: " + current);

            // goal reached
            /*if (current.position.epsilonEquals(target, 0.1f)) { // assumes vectors have decimal values instead of integer values; technically, Vector2 has float coordinates
                System.out.println("Goal attained");
                return reconstructPath(current);
            }*/

            if ((int)current.position.x == (int)target.x && (int)current.position.y == (int)target.y) { // assumes vectors have decimal values instead of integer values; technically, Vector2 has float coordinates
                System.out.println("Goal attained");
                return reconstructPath(current);
            }

            for (Vector2 neighborPos : getNeighbors(current.position, map)) {
                //System.out.println("Neighbor: " + neighborPos);
                float gCost = current.gCost + 1; // movement cost of 1 for each tile

                Node neighbor = allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Float.MAX_VALUE, heuristic(neighborPos, target))); // if the neighbor node exists, get it, otherwise, create one

                if (gCost < neighbor.gCost) { // better path found
                    //System.out.println("Adding to a path");
                    neighbor.gCost = gCost;
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;

                    allNodes.put(neighborPos, neighbor);
                    openSet.remove(neighbor); // remove to update priority
                    openSet.add(neighbor);

                    //System.out.println("Added neighbor: " + neighbor);
                }
            }
        }

        System.out.println("No Path Found");
        return Collections.emptyList(); // no path found
    }

    public List<Vector2> getNeighbors(Vector2 position, int[][] map) {
        List<Vector2> neighbors = new ArrayList<>();
        int x = (int) position.x;
        int y = (int) position.y;

        // checking all 4 possible directions
        for (Vector2 dir : DIRECTIONS) {
            int newX = x + (int)dir.x;
            int newY = y + (int)dir.y;

            if (isValidMove(newX, newY, map)) {
                //System.out.println("Valid move. X: " + newX + " Y: " + newY);
                neighbors.add(new Vector2(newX, newY));
            }
        }

        //System.out.println("From getNeighbors(): " + neighbors);
        return neighbors;
    }

    private boolean isValidMove(int x, int y, int[][] map) {
        // check bounds
        if (x < 0 || y < 0 || x >= map[0].length || y >= map.length) {
            return false;
        }

        // check if tile is walkable
        // need to check if tile is not already visited
        int tile = map[y][x];
        //System.out.println("Tile: " + tile);

        return tile == WallAtlasRegion.EMPTY.ordinal() || tile == WallAtlasRegion.PELLET_SMALL.ordinal() || tile == WallAtlasRegion.PELLET_LARGE.ordinal();
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
        System.out.println("Returning Path: " + path);
        return path;
    }
}
