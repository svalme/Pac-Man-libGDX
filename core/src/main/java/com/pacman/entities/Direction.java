package com.pacman.entities;
import com.badlogic.gdx.math.Vector2;

public enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** Returns the opposite direction */
    public Direction opposite() {
        switch (this) {
            case UP:    return DOWN;
            case DOWN:  return UP;
            case LEFT:  return RIGHT;
            case RIGHT: return LEFT;
            default:    return NONE;
        }
    }

    /** Convert to a Vector2 for movement */
    public Vector2 toVector() {
        return new Vector2(dx, dy);
    }
}
