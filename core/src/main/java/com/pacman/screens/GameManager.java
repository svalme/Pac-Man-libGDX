package com.pacman.screens;

import com.pacman.entities.GhostManager;
import com.pacman.entities.Ghost;
import com.pacman.entities.GhostState;
import com.pacman.entities.Pacman;
import com.pacman.entities.PacmanState;
import com.pacman.screens.Map;

public class GameManager {

    private Pacman pacman;
    private GhostManager ghostManager;

    private int score = 0;
    private int lives = 3;

    public GameManager(Pacman pacman, GhostManager ghostManager) {
        this.pacman = pacman;
        this.ghostManager = ghostManager;
    }

    public void update(float delta) {
        pacman.update(delta);

        if (pacman.getState() == PacmanState.DEAD) {
            lives--;
            if (lives > 0) {
                pacman.revive();
                ghostManager.resetGhosts();
            }
            return;
        }

        ghostManager.update(delta);
        handleCollisions();
    }

    private void handleCollisions() {
        for (Ghost ghost : ghostManager.getGhosts()) {

            if (!isColliding(pacman, ghost)) continue;

            if (ghost.getState() == GhostState.FRIGHTENED) {
                ghost.setState(GhostState.EATEN);
                score += 200;
            } /* else if (ghost.getState() != GhostState.EATEN) {
                loseLife();
            } */
              else {
                    pacman.die();
                }
        }
    }

    private boolean isColliding(Pacman p, Ghost g) {
        return p.getPacmanLogicalTile().epsilonEquals(g.getPosition(), 0.1f);
    }

    private void loseLife() {
        lives--;
        if (lives > 0) {
            pacman.resetPosition();
            ghostManager.resetGhosts();
        }
    }

    public void onPelletEaten(int x, int y, boolean isPowerPellet) {
        if (isPowerPellet) {
            score += 50;
            ghostManager.triggerFrightened(6.0f);
        } else {
            score += 10;
        }
        Map.removePellet(x, y);
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public Pacman getPacman() {
        return pacman;
    }

}
