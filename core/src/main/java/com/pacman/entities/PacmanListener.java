package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.Direction;

public interface PacmanListener {
    void onPacmanMoved(Vector2 newPosition, Direction newDirection);
}
