package com.pacman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pacman.test.DebugPathfindingScreen;

/** First screen of the application. Displayed after the application is created. */

public class MenuScreen implements Screen {

    final Main game;
    private Stage stage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    public MenuScreen(final Main game) {
        this.game = game;

    }

    @Override
    public void show() {
        // Prepare your screen here.

        float tileSize = 16;
        float worldWidth = 650;  // 448
        float worldHeight = 650; // 496

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        int button_width = 192;
        int button_height = 54;
        int space_between_buttons = 30;

        // play button
        Texture play_button_texture = new Texture(Gdx.files.internal("play_button.png"));

        Image play_button = new Image(play_button_texture);
        play_button.setSize(button_width, button_height);
        play_button.setPosition(
            (Gdx.graphics.getWidth() - play_button.getWidth()) / 2,
            (float) Gdx.graphics.getHeight() / 2
        ); // Center screen

        //System.out.println("play button height: " + (float) (Gdx.graphics.getHeight() / 2));

        play_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game)); // Navigate to the GameScreen
            }
        });


        // scores button
        Texture scores_button_texture = new Texture(Gdx.files.internal("scores_button.png"));

        Image scores_button = new Image(scores_button_texture);
        scores_button.setSize(button_width, button_height);
        scores_button.setPosition(
            (Gdx.graphics.getWidth() - scores_button.getWidth()) / 2,
            (float) (Gdx.graphics.getHeight() / 2) - (button_height + space_between_buttons)
        ); // Center screen

        //System.out.println("scores button height: " + (float) ((Gdx.graphics.getHeight() / 2) - (button_height + space_between_buttons)));

        scores_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DebugPathfindingScreen()); // Navigate to the GameScreen
            }
        });


        // exit button
        Texture exit_button_texture = new Texture(Gdx.files.internal("exit_button.png"));

        Image exit_button = new Image(exit_button_texture);
        exit_button.setSize(button_width, button_height);
        exit_button.setPosition(
            (Gdx.graphics.getWidth() - exit_button.getWidth()) / 2,
            (float) (Gdx.graphics.getHeight() / 2) - 2*(button_height + space_between_buttons)
        ); // Center screen

        //System.out.println("exit button height: " + (float) ((Gdx.graphics.getHeight() / 2) - 2*(button_height + space_between_buttons)));

        exit_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new GameScreen(game)); // Navigate to the GameScreen
            }
        });




        stage.addActor(play_button);
        stage.addActor(scores_button);
        stage.addActor(exit_button);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
    }
}
