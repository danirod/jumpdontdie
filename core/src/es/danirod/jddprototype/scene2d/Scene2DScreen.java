/*
 * This file is part of Jump Don't Die
 * Copyright (C) 2015 Dani Rodríguez <danirod@outlook.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.danirod.jddprototype.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import es.danirod.jddprototype.game.BaseScreen;
import es.danirod.jddprototype.game.MainGame;

/**
 * This screen uses Scene2D to show how it works.
 */
public class Scene2DScreen extends BaseScreen {

    /** The Scene2D stage where all the actors are added. */
    private Stage stage;

    /** The actor that represents the player. */
    private PlayerActor player;

    /** The actor that represent spikes. */
    private es.danirod.jddprototype.scene2d.SpikeActor spikes;

    /** Textures used in this screen. */
    private Texture playerTexture, spikeTexture;

    /** Regions used in this screen. */
    private TextureRegion spikeRegion;

    public Scene2DScreen(MainGame game) {
        super(game);

        // Load assets using new Texture instead of asset manager.
        playerTexture = new Texture("player.png");
        spikeTexture = new Texture("spike.png");
        spikeRegion = new TextureRegion(spikeTexture, 0, 64, 128, 64);
    }

    @Override
    public void show() {
        // Create a new stage.
        stage = new Stage(new FitViewport(640, 400));

        // Load the actors.
        player = new PlayerActor(playerTexture);
        spikes = new es.danirod.jddprototype.scene2d.SpikeActor(spikeRegion, 2100, 100, 500);
        player.setPosition(20, 100);

        // Add the actors to the screen. They won't be visible if you don't add them.
        stage.addActor(player);
        stage.addActor(spikes);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        checkCollisions();
        stage.draw();
    }

    /**
     * This method checks collisions between the player and the spikes using the bounding box
     * method. There is a collision if the bounding boxes that represent both actors overlap.
     */
    private void checkCollisions() {
        if (player.isAlive() &&  (player.getX() + player.getWidth()) > spikes.getX()) {
            System.out.println("A collision has happened.");
            player.setAlive(false);
        }
    }

    @Override
    public void dispose() {
        playerTexture.dispose();
        spikeTexture.dispose();
        stage.dispose();
    }
}
