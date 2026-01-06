package com.pacman.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import com.pacman.entities.Direction;
import com.pacman.entities.Ghost;
import com.pacman.entities.GhostState;
import com.pacman.entities.GhostType;
import com.pacman.entities.GhostManager;
import com.pacman.entities.Pacman;
import com.pacman.entities.Phase;

import com.pacman.utilities.ServiceLocator;
import com.pacman.screens.GameManager;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private GhostManager ghostManager;
    private GameManager gameManager;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private Hud hud;

    public GameScreen() {}

    @Override
    public void show() {

        int screenWidth = Map.columns * Map.TILE_SIZE + 400;
        int screenHeight = Map.rows * Map.TILE_SIZE;

        // set up the camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(screenWidth, screenHeight, camera);

        // center the camera
        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.update();

        Map.loadValues();
        batch = new SpriteBatch();
        hud = new Hud();

        pacman = new Pacman();
        List<Phase> phases = List.of(
            new Phase(GhostState.SCATTER, 7f),
            new Phase(GhostState.CHASE, 20f),
            new Phase(GhostState.SCATTER, 7f),
            new Phase(GhostState.CHASE, 20f)
        );

        ghostManager = new GhostManager(phases);
        ServiceLocator.registerGhostManager(ghostManager);
        ghosts = new ArrayList<>();

        // pink ghost
        Ghost pink_ghost = new Ghost(new Vector2(14, 17), GhostType.PINKY); // fine
        pink_ghost.setScatterTarget(new Vector2(12, 15)); // fine
        ghosts.add(pink_ghost);
        ghostManager.register(pink_ghost);
        pacman.addListener(pink_ghost);

        // orange ghost
        Ghost orange_ghost = new Ghost(new Vector2(18, 19), GhostType.CLYDE);
        orange_ghost.setScatterTarget(new Vector2(9, 14));
        ghosts.add(orange_ghost);
        ghostManager.register(orange_ghost);
        pacman.addListener(orange_ghost);

        // blue ghost
        Ghost blue_ghost = new Ghost(new Vector2(14, 17), GhostType.INKY); // fine
        blue_ghost.setScatterTarget(new Vector2(18, 18)); // fine
        ghosts.add(blue_ghost);
        ghostManager.register(blue_ghost);
        pacman.addListener(blue_ghost);

        // red ghost
        Ghost red_ghost = new Ghost(new Vector2(17, 17), GhostType.BLINKY); // fine
        red_ghost.setScatterTarget(new Vector2(1, 9)); // fine
        ghosts.add(red_ghost);
        ghostManager.register(red_ghost);
        ghostManager.setBlinky(red_ghost);
        pacman.addListener(red_ghost);

        

        gameManager = new GameManager(pacman, ghostManager);
        ServiceLocator.registerGameManager(gameManager);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) pacman.setDirection(Direction.UP);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) pacman.setDirection(Direction.DOWN);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) pacman.setDirection(Direction.LEFT);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) pacman.setDirection(Direction.RIGHT);

        update(delta);

        // start drawing
        batch.begin();
        Map.drawMap(batch);
        pacman.render(batch);

        hud.render(batch, gameManager.getScore(), gameManager.getLives());

        // draw ghosts
        for (Ghost ghost : ghosts) {
            ghost.render(batch);
        }

        batch.end();

        // reset the batch color to opaque (after rendering)
        batch.setColor(1f, 1f, 1f, 1f);

    }

    public void update(float deltaTime) {  
        gameManager.update(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        dispose(); // Clean up resources when this screen is hidden
    }

    @Override
    public void dispose() {
        batch.dispose();
        Map.dispose();
        hud.dispose();
    }
}
