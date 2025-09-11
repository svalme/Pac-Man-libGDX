package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;

public interface PacmanListener {
    void onPacmanMoved(Vector2 newPosition);
}
