package com.pacman.utilities;

import com.badlogic.gdx.Game;
import com.pacman.screens.Map;

public class ServiceLocator {

    private static Game gameInstance;
    private static Map mapInstance;

    public static void registerGame(Game game) {
        gameInstance = game;
    }

    public static Game getGameInstance() {
        return gameInstance;
    }

    public static void registerMap(Map map) {
        mapInstance = map;
    }

    public static Map getMapInstance() {
        return mapInstance;
    }

}
