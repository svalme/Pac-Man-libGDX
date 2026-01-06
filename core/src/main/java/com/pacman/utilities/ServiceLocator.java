package com.pacman.utilities;

import com.badlogic.gdx.Game;
import com.pacman.entities.GhostManager;
import com.pacman.screens.GameManager;

public class ServiceLocator {

    private static Game game;
    private static GhostManager ghostManager;
    private static GameManager  gameManager;

    public static void registerGame(Game g) {
        game = g;
    }

    public static Game getGame() {
        return game;
    }

    public static void registerGhostManager(GhostManager gm) {
        ghostManager = gm;
    }

    public static GhostManager getGhostManager() {
        return ghostManager;
    }

    public static void registerGameManager(GameManager gm) {
        gameManager = gm;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }


}
