package com.pacman.screens;

import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Map {

    private TextureAtlas atlas;
    private TextureRegion[] tileRegions;
    public static int[][] map;
    public static final int TILE_SIZE = 24; // tile size
    public static int columns = 28;
    public static int rows = 31;

    public static final Vector2[] NEIGHBORS = {
            new Vector2( 1,  0),  // right
            new Vector2(-1,  0),  // left
            new Vector2( 0,  1),  // up
            new Vector2( 0, -1)   // down
    };

    public Map() {

        atlas = new TextureAtlas("map.atlas");

        // map each WallAtlasRegion to a TextureRegion
        tileRegions = new TextureRegion[WallAtlasRegion.values().length];
        for (WallAtlasRegion region : WallAtlasRegion.values()) {
            tileRegions[region.ordinal()] = atlas.findRegion(region.name().toLowerCase()); //new TextureAtlas.AtlasRegion[]{atlas.findRegion(region.name().toLowerCase())};
        }

        map = new int[][] {
            { 22,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  28,  29,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  23},
            { 24,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  25},
            { 24,  27,  38,   2,   2,  39,  27,  38,   2,   2,   2,  39,  27,   3,   4,  27,  38,   2,   2,   2,  39,  27,  38,   2,   2,  39,  27,  25},
            { 24,  26,   3,   0,   0,   4,  27,   3,   0,   0,   0,   4,  27,   3,   4,  27,   3,   0,   0,   0,   4,  27,   3,   0,   0,   4,  26,  25},
            { 24,  27,  36,   1,   1,  37,  27,  36,   1,   1,   1,  37,  27,  36,  37,  27,  36,   1,   1,   1,  37,  27,  36,   1,   1,  37,  27,  25},
            { 24,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  25},
            { 24,  27,  38,   2,   2,  39,  27,  38,  39,  27,  38,   2,   2,   2,   2,   2,   2,  39,  27,  38,  39,  27,  38,   2,   2,  39,  27,  25},
            { 24,  27,  36,   1,   1,  37,  27,   3,   4,  27,  36,   1,   1,  12,  11,   1,   1,  37,  27,   3,   4,  27,  36,   1,   1,  37,  27,  25},
            { 24,  27,  27,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,  27,  27,  25},
            { 18,  20,  20,  20,  20,  39,  27,   3,   5,   2,   2,  39,   0,   3,   4,   0,  38,   2,   2,   6,   4,  27,  38,  20,  20,  20,  20,  19},
            {  0,   0,   0,   0,   0,  24,  27,   3,  11,   1,   1,  37,   0,  36,  37,   0,  36,   1,   1,  12,   4,  27,  25,   0,   0,   0,   0,   0},
            {  0,   0,   0,   0,   0,  24,  27,   3,   4,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   3,   4,  27,  25,   0,   0,   0,   0,   0},
            {  0,   0,   0,   0,   0,  24,  27,   3,   4,   0,  16,  20,  20,  15,  15,  20,  20,  17,   0,   3,   4,  27,  25,   0,   0,   0,   0,   0},
            { 21,  21,  21,  21,  21,  37,  27,  36,  37,   0,  25,   0,   0,   0,   0,   0,   0,  24,   0,  36,  37,  27,  36,  21,  21,  21,  21,  21},
            {  0,   0,   0,   0,   0,   0,  27,   0,   0,   0,  25,   0,   0,   0,   0,   0,   0,  24,   0,   0,   0,  27,   0,   0,   0,   0,   0,   0},
            { 20,  20,  20,  20,  20,  39,  27,  38,  39,   0,  25,   0,   0,   0,   0,   0,   0,  24,   0,  38,  39,  27,  38,  20,  20,  20,  20,  20},
            {  0,   0,   0,   0,   0,  24,  27,   3,   4,   0,  13,  21,  21,  21,  21,  21,  21,  14,   0,   3,   4,  27,  25,   0,   0,   0,   0,   0},
            {  0,   0,   0,   0,   0,  24,  27,   3,   4,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   3,   4,  27,  25,   0,   0,   0,   0,   0},
            {  0,   0,   0,   0,   0,  24,  27,   3,   4,   0,  38,   2,   2,   2,   2,   2,   2,  39,   0,   3,   4,  27,  25,   0,   0,   0,   0,   0},
            { 22,  21,  21,  21,  21,  37,  27,  36,  37,   0,  36,   1,   1,  12,  11,   1,   1,  37,   0,  36,  37,  27,  36,  21,  21,  21,  21,  23},
            { 24,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  25},
            { 24,  27,  38,   2,   2,  39,  27,  38,   2,   2,   2,  39,  27,   3,   4,  27,  38,   2,   2,   2,  39,  27,  38,   2,   2,  39,  27,  25},
            { 24,  27,  36,   1,  12,   4,  27,  36,   1,   1,   1,  37,  27,  36,  37,  27,  36,   1,   1,   1,  37,  27,   3,  11,   1,  37,  27,  25},
            { 24,  26,  27,  27,   3,   4,  27,  27,  27,  27,  27,  27,  27,   0,   0,  27,  27,  27,  27,  27,  27,  27,   3,   4,  27,  27,  26,  25},
            { 35,   2,  39,  27,   3,   4,  27,  38,  39,  27,  38,   2,   2,   2,   2,   2,   2,  39,  27,  38,  39,  27,   3,   4,  27,  38,   2,  33},
            { 34,   1,  37,  27,  36,  37,  27,   3,   4,  27,  36,   1,   1,  12,  11,   1,   1,  37,  27,   3,   4,  27,  36,  37,  27,  36,   1,  32},
            { 24,  27,  27,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,   3,   4,  27,  27,  27,  27,  27,  27,  25},
            { 24,  27,  38,   2,   2,   2,   2,   6,   5,   2,   2,  39,  27,   3,   4,  27,  38,   2,   2,   6,   5,   2,   2,   2,   2,  39,  27,  25},
            { 24,  27,  36,   1,   1,   1,   1,   1,   1,   1,   1,  37,  27,  36,  37,  27,  36,   1,   1,   1,   1,   1,   1,   1,   1,  37,  27,  25},
            { 24,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  25},
            { 18,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  19}
        };

    }
/*
    public static Map getInstance() {
        if (mapInstance == null) {
            mapInstance = new Map();
        }
        return mapInstance;
    }
*/
    public void drawMap(SpriteBatch batch) {

        // draw tiles from the tile map
        for (int y = 0; y < map.length; y++) {
            //System.out.printf("y: %d\n", y);
            for (int x = 0; x < map[y].length; x++) {
                //System.out.printf("x: %d\n", x);
                int tileType = map[y][x]; // Get the tile type (ordinal of WallAtlasRegion)
                TextureRegion region = tileRegions[tileType]; // Get the corresponding region

                // draw the tile
                int flippedY = map.length - 1 - y;
                batch.draw(region, x * TILE_SIZE, flippedY * TILE_SIZE);
            }
        }

    }

    public static boolean collisionFree(float centerX, float centerY, float radius, Direction direction) {
        float leadingX = centerX;
        float leadingY = centerY;

        // adjust leading edge based on the direction and pacman's center coordinates
        switch (direction) {
            case UP:
                leadingY += radius;
                break;
            case DOWN:
                leadingY -= radius;
                break;
            case LEFT:
                leadingX -= radius;
                break;
            case RIGHT:
                leadingX += radius;
                break;
        }

        // convert leading edge to tile coordinates
        int tileX = (int)(leadingX / TILE_SIZE);
        int tileY = map.length - 1 - (int)(leadingY / TILE_SIZE);

        // check bounds
        if (tileY < 0 || tileY >= map.length || tileX < 0 || tileX >= map[0].length) {
            return false; // Out of bounds = collision
        }
/*
        if (tileX < 0 || tileX >= map[0].length) {
            return true; // true for wrap-around
        }*/

        int tileValue = map[tileY][tileX];
        //System.out.printf("tileY: %d, tileX: %d\n", tileY, tileX);
        //System.out.printf("tileValue: %d\n", tileValue);
        // check if the tile is a wall
        return (tileValue == WallAtlasRegion.EMPTY.getValue()) ||
               (tileValue == WallAtlasRegion.PELLET_LARGE.getValue()) ||
               (tileValue == WallAtlasRegion.PELLET_SMALL.getValue());
        //System.out.printf("collision: %b\n", collision);
        //return collision; // hit empty wall or pellet
    }

    public boolean isWall(int tileX, int tileY) {

        if (tileY < 0 || tileY >= map.length || tileX < 0 || tileX >= map[0].length) {
            return true;
        }

        int tileValue = map[tileY][tileX];
        return (tileValue != WallAtlasRegion.EMPTY.getValue()) ||
                (tileValue != WallAtlasRegion.PELLET_LARGE.getValue()) ||
                (tileValue != WallAtlasRegion.PELLET_SMALL.getValue());
    }

    // return the x coordinate of the center of a tile
    public static float getTileCenterX(float column) {
        return (column * TILE_SIZE) + (TILE_SIZE / 2.0f);
    }

    // return the y coordinate of the center of a tile
    public static float getTileCenterY(float row) {
        int flippedRow = rows - 1 - (int)row;
        return (flippedRow * TILE_SIZE) + (TILE_SIZE / 2.0f);
    }

    public void dispose() {
        atlas.dispose();
    }

}
