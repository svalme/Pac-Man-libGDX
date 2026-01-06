package com.pacman.entities;

import com.pacman.entities.GhostState;

public class Phase {
    public GhostState state;
    public float duration;

    public Phase(GhostState state, float duration) {
        this.state = state;
        this.duration = duration;
    }
}
