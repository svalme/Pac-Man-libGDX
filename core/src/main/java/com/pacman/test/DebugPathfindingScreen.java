package com.pacman.test;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.pacman.utilities.Pathfinding;
import com.pacman.screens.MenuScreen;
import com.pacman.utilities.ServiceLocator;

import java.util.List;

public class DebugPathfindingScreen implements Screen {

    private final Game game;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private int[][] map;
    private Pathfinding pathfinding;
    private List<Vector2> path;

    private Vector2 start, target;

    private final int TILE_SIZE = 32;

    public DebugPathfindingScreen() {
        this.game = ServiceLocator.getGameInstance();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        shapeRenderer = new ShapeRenderer();

        // 0 = empty, 1 = wall
        map = new int[][] {
            {0,0,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,0,1,1,1,0},
            {0,0,0,0,1,0,0,0,1,0},
            {0,1,1,0,1,1,1,0,1,0},
            {0,0,0,0,0,0,0,0,0,0},
        };

        pathfinding = new Pathfinding();
        start = new Vector2(0, 0);
        target = new Vector2(9, 4);
        path = pathfinding.aStarPathfinding(start, target);
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // vertical offset to center map since it's smaller than world
        float mapHeight = map.length * TILE_SIZE;
        float yOffset = (camera.viewportHeight - mapHeight) / 2f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw the map
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                // top-left origin: Y = y * TILE_SIZE
                shapeRenderer.setColor(map[y][x] == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                shapeRenderer.rect(x * TILE_SIZE, yOffset + (map.length - 1 - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // draw the path
        if (path != null) {
            shapeRenderer.setColor(Color.RED);
            for (Vector2 step : path) {
                shapeRenderer.rect(step.x * TILE_SIZE, yOffset + (map.length - 1 - (int) step.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // draw start/target
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(start.x * TILE_SIZE, yOffset + (map.length - 1 - (int) start.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(target.x * TILE_SIZE, yOffset + (map.length - 1 - (int) target.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        shapeRenderer.end();
    }

    private void handleInput() {

        // Set the input processor to handle key events
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                // Check if the released key was F1
                if (keycode == Input.Keys.F1) {
                    game.setScreen(new MenuScreen(game));
                    return true; // The event was handled
                }
                return false; // The event was not handled
            }
        });

        if (Gdx.input.justTouched()) {
            Vector3 worldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(worldCoords);

            float mapHeight = map.length * TILE_SIZE;
            float yOffset = (camera.viewportHeight - mapHeight) / 2f;

            // convert world coordinates to tile indices
            int x = (int)(worldCoords.x / TILE_SIZE);
            int y = (int)((worldCoords.y - yOffset) / TILE_SIZE);

            // flip Y to match top-left map array
            y = map.length - 1 - y;

            // clamp to valid tiles
            x = MathUtils.clamp(x, 0, map[0].length - 1);
            y = MathUtils.clamp(y, 0, map.length - 1);


           if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                start.set(x, y);
            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                target.set(x, y);
            }

            path = pathfinding.aStarPathfinding(start, target);
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapeRenderer.dispose(); }
}
