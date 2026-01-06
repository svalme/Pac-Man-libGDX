package com.pacman.entities;

import com.pacman.entities.Phase;
import com.pacman.utilities.ServiceLocator;
import com.pacman.entities.GhostState;
import com.pacman.entities.Ghost;
import com.pacman.entities.PacmanState;

import java.util.ArrayList;
import java.util.List;

public class GhostManager {

    private final List<Ghost> ghosts = new ArrayList<>();
    private final List<Phase> phases;

    private int phaseIndex = 0;
    private float timer = 0f;

    private boolean frightened = false;
    private float frightenedTimer = 0f;

    private Ghost blinky;

    public GhostManager(List<Phase> phases) {
        this.phases = phases;
    }

    public void register(Ghost ghost) {
        ghosts.add(ghost);
        ghost.setState(phases.get(0).state); // initial state
    }

    public void update(float delta) {
        if (ServiceLocator.getGameManager().getPacman().getState() == PacmanState.DYING)
            return;

        for (Ghost ghost : ghosts) {
            ghost.updateTarget(delta); 
        }

        if (frightened) {
            frightenedTimer -= delta;
            if (frightenedTimer <= 0f) {
                frightened = false;
                setAll(phases.get(phaseIndex).state);
            }
            return;
        }

        timer += delta;

        if (timer >= phases.get(phaseIndex).duration) {
            timer = 0;
            phaseIndex = (phaseIndex + 1) % phases.size();
            setAll(phases.get(phaseIndex).state);
        }
    }

    public void triggerFrightened(float duration) {
        frightened = true;
        frightenedTimer = duration;
        setAll(GhostState.FRIGHTENED);
    }

    private void setAll(GhostState state) {
        for (Ghost g : ghosts) {
            g.setState(state);
        }
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void resetGhosts() {
        for (Ghost g : ghosts) {
            g.resetPosition();
            g.setState(GhostState.SCATTER);
        }
    }

    public void setBlinky(Ghost blinky) {
        this.blinky = blinky;
    }

    public Ghost getBlinky() {
        return this.blinky;
    }

}
