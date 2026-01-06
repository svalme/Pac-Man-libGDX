package com.pacman.utilities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.Direction;
import com.pacman.screens.Map;
import com.pacman.screens.WallAtlasRegion;

import java.util.ArrayList;
import java.util.Random;

public class FrightenedPathFinder {

    private final int[][] map;
    private Direction currentDirection = Direction.NONE;
    private final Random random;

    public FrightenedPathFinder() {
        this.map = Map.map;
        this.random = new Random();
    }

    public void reset() {
        currentDirection = Direction.NONE;
    }

    public void update(Vector2 position, float speed, float delta) {
        if (currentDirection == Direction.NONE) {
            chooseNewDirection(position);
        } else if (atTileCenter(position)) {
            chooseNewDirection(position);
        }
    }

    // at middle of current tile, choose a new tile to go to
    private boolean atTileCenter(Vector2 pos) {
        float cx = pos.x % 1f;
        float cy = pos.y % 1f;
        return Math.abs(cx - 0.5f) < 0.05f && Math.abs(cy - 0.5f) < 0.05f;
    }

    private void chooseNewDirection(Vector2 position) {
        ArrayList<Direction> options = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE) continue;
            if (dir == currentDirection.opposite()) continue;

            int nx = (int) position.x + dir.dx;
            int ny = (int) position.y + dir.dy;

            if (isWalkable(nx, ny)) {
                options.add(dir);
            }
        }

        if (options.isEmpty()) {
            currentDirection = currentDirection.opposite();
        } else {
            currentDirection = options.get(random.nextInt(options.size()));
        }
    }

    private boolean isWalkable(int x, int y) {
        if (x < 0 || x >= Map.columns || y < 0 || y >= Map.rows)
            return false;

        int tile = map[Map.rows - 1 - y][x];

        return tile == WallAtlasRegion.EMPTY.ordinal()
            || tile == WallAtlasRegion.PELLET_SMALL.ordinal()
            || tile == WallAtlasRegion.PELLET_LARGE.ordinal()
            || tile == WallAtlasRegion.JAIL_DOOR.ordinal();
    }

    public Vector2 move(Vector2 position, float speed, float delta) {
        if (currentDirection == Direction.NONE) return position;


        float dx = currentDirection.dx * speed * delta;
        float dy = currentDirection.dy * speed * delta;

        float nextX = position.x + dx;
        float nextY = position.y + dy;

        int currX = (int) position.x;
        int currY = (int) position.y;

        int nextTileX = (int) nextX;
        int nextTileY = (int) nextY;

        // prevent wall clipping
        if ((nextTileX != currX || nextTileY != currY) &&
            !isWalkable(nextTileX, nextTileY)) {

            snapToCenter(position);
            return position;
        }

        position.set(nextX, nextY);
        return position;
    }

    private void snapToCenter(Vector2 pos) {
        int tx = (int) pos.x;
        int ty = (int) pos.y;
        pos.set(tx + 0.5f, ty + 0.5f);
    }
    
}
