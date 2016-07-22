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

package es.danirod.jddprototype.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * This screen is executed when you start the screen and it is used to load things in the
 * asset manager. There are a lot of ways for loading things in an AssetManager. This is a
 * simple example that displays a label saying "LOADING" as well as the percentage of the
 * game that has been loaded.
 */
public class LoadingScreen extends BaseScreen {

    /** Labels are also Actors. We will use Scene2D UI. */
    private Stage stage;

    /** This is the skin file (see GameOverScreen for more information on this). */
    private Skin skin;

    /** This is the label that we use to display some text on the screen. */
    private Label loading;

    public LoadingScreen(es.danirod.jddprototype.game.MainGame game) {
        super(game);

        // Set up the stage and the skin. See GameOverScreen for more comments on this.
        stage = new Stage(new FitViewport(640, 360));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Create some loading text using this skin file and position it on screen.
        loading = new Label("Loading...", skin);
        loading.setPosition(320 - loading.getWidth() / 2, 180 - loading.getHeight() / 2);
        stage.addActor(loading);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // This is important. To load an asset from the asset manager you call update() method.
        // this method will return true if it has finished loading. Else it will return false.
        // You usually want to do the code that changes to the main menu screen if it has finished
        // loading, else you update the screen to not make the user angry and keep loading.
        if (game.getManager().update()) {
            // I'll notify the game that all the assets are loaded so that it can load the
            // remaining set of screens and enter the main menu. This avoids Exceptions because
            // screens cannot be loaded until all the assets are loaded.
            game.finishLoading();
        } else {
            // getProgress() returns the progress of the load in a range of [0,1]. We multiply
            // this progress per * 100 so that we can display it as a percentage.
            int progress = (int) (game.getManager().getProgress() * 100);
            loading.setText("Loading... " + progress + "%");
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
