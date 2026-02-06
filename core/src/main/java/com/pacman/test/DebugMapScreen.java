package com.pacman.test;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.pacman.screens.Map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.pacman.utilities.Pathfinding;
import com.pacman.screens.MenuScreen;
import com.pacman.utilities.ServiceLocator;

import java.util.List;

public class DebugMapScreen implements Screen {

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    private final int TILE_SIZE = Map.TILE_SIZE;

    public DebugMapScreen() {
        int screenWidth = Map.columns * Map.TILE_SIZE + 400;
        int screenHeight = Map.rows * Map.TILE_SIZE;
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(screenWidth, screenHeight, camera);
        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.update();

        batch = new SpriteBatch();
        font = new BitmapFont(); // default font is fine for debug
        Map.loadValues();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); // no UI needed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw the map
        Map.drawMap(batch);

        // Mouse world position
        Vector3 mouseWorld = camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
        );

        float worldX = mouseWorld.x;
        float worldY = mouseWorld.y;

        // Convert to tile coordinates
        int tileX = (int) (worldX / TILE_SIZE);
        int tileY = Map.rows - 1 - (int) (worldY / TILE_SIZE);

        // Draw debug text
        font.draw(batch, "WORLD: (" + (int)worldX + ", " + (int)worldY + ")", 730, 400);
        font.draw(batch, "TILE: (" + tileX + ", " + tileY + ")", 730 ,500);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
