package com.pacman.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hud {
    TextureAtlas atlas;
    private BitmapFont font;
    private TextureRegion lifeIcon;  // Pac-Man sprite for lives

    public Hud() {
        font = new BitmapFont(); // default LibGDX font

        atlas = new TextureAtlas("pacman.atlas");
        lifeIcon = atlas.findRegion("pacman_ninety");
    }

    public void render(SpriteBatch batch, int score, int lives) {
        // draw score at top-left
        font.draw(batch, "SCORE: " + score, 20, 460);

        // draw lives at bottom-left
        for (int i = 0; i < lives; i++) {
            batch.draw(lifeIcon, 20 + i * 30, 20, 24, 24);
        }
    }

    public void dispose() {
        font.dispose();
    }
}

