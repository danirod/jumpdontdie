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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Extra screen to show the credits for the work.
 */
public class CreditsScreen extends BaseScreen {

    /** The stage where all the buttons are added. */
    private Stage stage;

    /** The skin that we use to set the style of the buttons. */
    private Skin skin;

    /** The label with all the information. */
    private Label credits;

    /** The back button you use to jump to the game screen. */
    private TextButton back;

    public CreditsScreen(final es.danirod.jddprototype.game.MainGame game) {
        super(game);

        // Create a new stage, as usual.
        stage = new Stage(new FitViewport(640, 360));

        // Load the skin file. The skin file contains information about the skins. It can be
        // passed to any widget in Scene2D UI to set the style. It just works, amazing.
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // For instance, here you see that I create a new button by telling the label of the
        // button as well as the skin file. The background image for the button is in the skin
        // file.
        back = new TextButton("Back", skin);

        credits = new Label("Jump Don't Die v1.0.2\n" +
                "Copyright (C) 2015-2016 Dani Rodriguez\n" +
                "This game is GNU GPL. Get the code at github.com/danirod/JumpDontDie\n\n" +

                "Music: \"Long Time Coming\" Kevin MacLeod (incompetech.com)\n" +
                "Licensed under Creative Commons: By Attribution 3.0", skin);

        // Add capture listeners. Capture listeners have one method, changed, that is executed
        // when the button is pressed or when the user interacts somehow with the widget. They are
        // cool because they let you execute some code when you press them.
        back.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Take me to the game screen!
                game.setScreen(game.menuScreen);
            }
        });

        // Now I position things on screen. Sorry for making this the hardest part of this screen.
        // I position things on the screen so that they look centered. This is why I make the
        // buttons the same size.
        credits.setPosition(20, 340 - credits.getHeight());
        back.setSize(200, 80);
        back.setPosition(40, 50);

        // Do not forget to add actors to the stage or we wouldn't see anything.
        stage.addActor(back);
        stage.addActor(credits);
    }

    @Override
    public void show() {
        // Now this is important. If you want to be able to click the button, you have to make
        // the Input system handle input using this Stage. Stages are also InputProcessors. By
        // making the Stage the default input processor for this game, it is now possible to
        // click on buttons and even to type on input fields.
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // When the screen is no more visible, you have to remember to unset the input processor.
        // Otherwise, input might act weird, because even if you aren't using this screen, you are
        // still using the stage for handling input.
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // Dispose assets.
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}
