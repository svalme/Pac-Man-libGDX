package com.pacman.test;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.WallAtlasRegion;

import java.util.List;

public class PathPrinter {

    public static void printPath(List<Vector2> path, int[][] map) {
        if (path == null || path.isEmpty()) {
            System.out.println("Path is empty.");
            return;
        }

        int rows = map.length;

        System.out.println("---- PATH DEBUG ----");
        for (Vector2 step : path) {
            int worldX = (int) step.x;
            int worldY = (int) step.y;

            // convert to array coordinates
            int arrayY = rows - 1 - worldY;

            int rawValue = map[arrayY][worldX];
            WallAtlasRegion type = WallAtlasRegion.fromValue(rawValue);

            System.out.printf("World: (%d, %d) -> Array: (%d, %d) -> %s\n",
                worldX, worldY, worldX, arrayY, type.name());
        }
        System.out.println("--------------------");
    }
}
