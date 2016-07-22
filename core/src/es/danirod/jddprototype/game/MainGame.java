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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

/**
 * This is our main game. This is the class that we pass to the Application in Android launcher
 * as well as in desktop launcher. Because we want to create a screen-based game, we use Game
 * class, which has methods for creating multiple screens.
 */
public class MainGame extends Game {

    /** This is the asset manager we use to centralize the assets. */
    private AssetManager manager;

    /**
     * These are the screens that we use in this game. I invite you to use a better system than
     * just public variables. For instance, you could create an ArrayList or maybe use some
     * structure such as a map where you can associate a number or a string to a screen.
     */
    public BaseScreen loadingScreen, menuScreen, gameScreen, gameOverScreen, creditsScreen;

    @Override
    public void create() {
        // Initialize the asset manager. We add every aset to the manager so that it can be loaded
        // inside the LoadingScreen screen. Remember to put the name of the asset in the first
        // argument, then the type of the asset in the second argument.
        manager = new AssetManager();
        manager.load("floor.png", Texture.class);
        manager.load("gameover.png", Texture.class);
        manager.load("overfloor.png", Texture.class);
        manager.load("logo.png", Texture.class);
        manager.load("spike.png", Texture.class);
        manager.load("player.png", Texture.class);
        manager.load("audio/die.ogg", Sound.class);
        manager.load("audio/jump.ogg", Sound.class);
        manager.load("audio/song.ogg", Music.class);

        // Enter the loading screen to load the assets.
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
    }

    /**
     * This method is invoked by LoadingScreen when all the assets are loaded. Use this method
     * as a second-step loader. You can load the rest of the screens here and jump to the main
     * screen now that everything is loaded.
     */
    public void finishLoading() {
        menuScreen = new es.danirod.jddprototype.game.MenuScreen(this);
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this);
        creditsScreen = new CreditsScreen(this);
        setScreen(menuScreen);
    }

    public AssetManager getManager() {
        return manager;
    }

}
