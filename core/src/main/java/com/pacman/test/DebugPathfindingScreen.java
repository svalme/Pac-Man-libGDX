package com.pacman.test;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.Pathfinding;
import com.pacman.screens.MenuScreen;

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

    public DebugPathfindingScreen(Game game) {
        this.game = game;
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
        path = pathfinding.aStarPathfinding(start, target, map);
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the map
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                // top-left origin: Y = y * TILE_SIZE
                shapeRenderer.setColor(map[y][x] == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                shapeRenderer.rect(x * TILE_SIZE, (map.length - 1 - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Draw the path
        if (path != null) {
            shapeRenderer.setColor(Color.RED);
            for (Vector2 step : path) {
                shapeRenderer.rect(step.x * TILE_SIZE, (map.length - 1 - (int) step.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Draw start/target
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(start.x * TILE_SIZE, (map.length - 1 - (int) start.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(target.x * TILE_SIZE, (map.length - 1 - (int) target.y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        shapeRenderer.end();
    }

    private void handleInput() {

        // Set the input processor to handle key events
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                // Check if the released key was F1
                if (keycode == Input.Keys.F1) {
                    // Toggle the debug mode flag
                    //isDebugMode = !isDebugMode;
                    //Gdx.app.log("F1Toggle", "Debug mode toggled: " + isDebugMode);
                    game.setScreen(new MenuScreen(game));
                    return true; // The event was handled
                }
                return false; // The event was not handled
            }
        });


        if (Gdx.input.justTouched()) {
            int x = (int)(Gdx.input.getX() / TILE_SIZE);
            int y = (int)(Gdx.input.getY() / TILE_SIZE);

            System.out.println("x, y from input. x: " + x + ", y: " + y);

            // Convert screen Y to top-left origin
            y = map.length - 1 - y;
            System.out.println("y conversion: " + y);
            System.out.println("map.length: " + map.length);
            y *= -1;

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                start.set(x, y);
            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                target.set(x, y);
            }

            path = pathfinding.aStarPathfinding(start, target, map);
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapeRenderer.dispose(); }
}
