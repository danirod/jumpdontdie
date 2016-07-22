/*
 * This file is part of Jump Don't Die.
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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

import es.danirod.jddprototype.game.entities.EntityFactory;
import es.danirod.jddprototype.game.entities.FloorEntity;
import es.danirod.jddprototype.game.entities.PlayerEntity;
import es.danirod.jddprototype.game.entities.SpikeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main screen for the game. All the fun happen here.
 */
public class GameScreen extends BaseScreen {

    /** Stage instance for Scene2D rendering. */
    private Stage stage;

    /** World instance for Box2D engine. */
    private World world;

    /** Player entity. */
    private PlayerEntity player;

    /** List of floors attached to this level. */
    private List<FloorEntity> floorList = new ArrayList<FloorEntity>();

    /** List of spikes attached to this level. */
    private List<SpikeEntity> spikeList = new ArrayList<SpikeEntity>();

    /** Jump sound that has to play when the player jumps. */
    private Sound jumpSound;

    /** Die sound that has to play when the player collides with a spike. */
    private Sound dieSound;

    /** Background music that has to play on the background all the time. */
    private Music backgroundMusic;

    /** Initial position of the camera. Required for reseting the viewport. */
    private Vector3 position;

    /**
     * Create the screen. Since this constructor cannot be invoked before libGDX is fully started,
     * it is safe to do critical code here such as loading assets and setting up the stage.
     * @param game
     */
    public GameScreen(es.danirod.jddprototype.game.MainGame game) {
        super(game);

        // Create a new Scene2D stage for displaying things.
        stage = new Stage(new FitViewport(640, 360));
        position = new Vector3(stage.getCamera().position);

        // Create a new Box2D world for managing things.
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener());

        // Get the sound effect references that will play during the game.
        jumpSound = game.getManager().get("audio/jump.ogg");
        dieSound = game.getManager().get("audio/die.ogg");
        backgroundMusic = game.getManager().get("audio/song.ogg");
    }

    /**
     * This method will be executed when this screen is about to be rendered.
     * Here, I use this method to set up the initial position for the stage.
     */
    @Override
    public void show() {
        EntityFactory factory = new EntityFactory(game.getManager());

        // Create the player. It has an initial position.
        player = factory.createPlayer(world, new Vector2(1.5f, 1.5f));

        // This is the main floor. That is why is so long.
        floorList.add(factory.createFloor(world, 0, 1000, 1));

        // Now generate some floors over the main floor. Needless to say, that on a real game
        // this should be better engineered. For instance, have all the information for floors
        // and spikes in a data structure or even some level file and generate them without
        // writing lines of code.
        floorList.add(factory.createFloor(world, 15, 10, 2));
        floorList.add(factory.createFloor(world, 30, 8, 2));

        // Generate some spikes too.
        spikeList.add(factory.createSpikes(world, 8, 1));
        spikeList.add(factory.createSpikes(world, 23, 2));
        spikeList.add(factory.createSpikes(world, 35, 2));
        spikeList.add(factory.createSpikes(world, 50, 1));

        // All add the floors and spikes to the stage.
        for (FloorEntity floor : floorList)
            stage.addActor(floor);
        for (SpikeEntity spike : spikeList)
            stage.addActor(spike);

        // Add the player to the stage too.
        stage.addActor(player);

        // Reset the camera to the left. This is required because we have translated the camera
        // during the game. We need to put the camera on the initial position so that you can
        // use it again if you replay the game.
        stage.getCamera().position.set(position);
        stage.getCamera().update();

        // Everything is ready, turn the volume up.
        backgroundMusic.setVolume(0.75f);
        backgroundMusic.play();
    }

    /**
     * This method will be executed when this screen is no more the active screen.
     * I use this method to destroy all the things that have been used in the stage.
     */
    @Override
    public void hide() {
        // Clear the stage. This will remove ALL actors from the stage and it is faster than
        // removing every single actor one by one. This is not shown in the video but it is
        // an improvement.
        stage.clear();

        // Detach every entity from the world they have been living in.
        player.detach();
        for (FloorEntity floor : floorList)
            floor.detach();
        for (SpikeEntity spike : spikeList)
            spike.detach();

        // Clear the lists.
        floorList.clear();
        spikeList.clear();
    }

    /**
     * This method is executed whenever the game requires this screen to be rendered. This will
     * display things on the screen. This method is also used to update the game.
     */
    @Override
    public void render(float delta) {
        // Do not forget to clean the screen.
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the stage. This will update the player speed.
        stage.act();

        // Step the world. This will update the physics and update entity positions.
        world.step(delta, 6, 2);

        // Make the camera follow the player. As long as the player is alive, if the player is
        // moving, make the camera move at the same speed, so that the player is always
        // centered at the same position.
        if (player.getX() > 150 && player.isAlive()) {
            float speed = Constants.PLAYER_SPEED * delta * Constants.PIXELS_IN_METER;
            stage.getCamera().translate(speed, 0, 0);
        }

        // Render the screen. Remember, this is the last step!
        stage.draw();
    }

    /**
     * This method is executed when the screen can be safely disposed.
     * I use this method to dispose things that have to be manually disposed.
     */
    @Override
    public void dispose() {
        // Dispose the stage to remove the Batch references in the graphics card.
        stage.dispose();

        // Dispose the world to remove the Box2D native data (C++ backend, invoked by Java).
        world.dispose();
    }

    /**
     * This is the contact listener that checks the world for collisions and contacts.
     * I use this method to evaluate when things collide, such as player colliding with floor.
     */
    private class GameContactListener implements ContactListener {

        private boolean areCollided(Contact contact, Object userA, Object userB) {
            Object userDataA = contact.getFixtureA().getUserData();
            Object userDataB = contact.getFixtureB().getUserData();

            // This is not in the video! It is a good idea to check that user data is not null.
            // Sometimes you forget to put user data or you get collisions by entities you didn't
            // expect. Not preventing this will probably result in a NullPointerException.
            if (userDataA == null || userDataB == null) {
                return false;
            }

            // Because you never know what is A and what is B, you have to do both checks.
            return (userDataA.equals(userA) && userDataB.equals(userB)) ||
                    (userDataA.equals(userB) && userDataB.equals(userA));
        }

        /**
         * This method is executed when a contact has started: when two fixtures just collided.
         */
        @Override
        public void beginContact(Contact contact) {
            // The player has collided with the floor.
            if (areCollided(contact, "player", "floor")) {
                player.setJumping(false);

                // If the screen is still touched, you have to jump again.
                if (Gdx.input.isTouched()) {
                    jumpSound.play();

                    // You just can't add a force here, because while a contact is being handled
                    // the world is locked. Therefore you have to find a way to remember to make
                    // the player jump AFTER the collision has been handled. Here I update the
                    // flag value mustJump. This will make the player jump on next frame.
                    player.setMustJump(true);
                }
            }

            // The player has collided with something that hurts.
            if (areCollided(contact, "player", "spike")) {

                // Check that is alive. Sometimes you bounce, you don't want to die more than once.
                if (player.isAlive()) {
                    player.setAlive(false);

                    // Sound feedback.
                    backgroundMusic.stop();
                    dieSound.play();

                    // Add an Action. Actions are cool because they let you add animations to your
                    // game. Here I add a sequence action so that two actions happens one after
                    // the other. One action is a delay action. It just waits for 1.5 seconds.
                    // The second actions is a run action. It executes some code. Here, we go
                    // to the game over screen when we die.
                    stage.addAction(
                            Actions.sequence(
                                    Actions.delay(1.5f),
                                    Actions.run(new Runnable() {

                                        @Override
                                        public void run() {
                                            game.setScreen(game.gameOverScreen);
                                        }
                                    })
                            )
                    );
                }
            }
        }

        /**
         * This method is executed when a contact has finished: two fixtures are no more colliding.
         */
        @Override
        public void endContact(Contact contact) {
            // The player is jumping and it is not touching the floor.
            if (areCollided(contact, "player", "floor")) {
                if (player.isAlive()) {
                    jumpSound.play();
                }
            }
        }

        // Here two lonely methods that I don't use but have to override anyway.
        @Override public void preSolve(Contact contact, Manifold oldManifold) { }
        @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
    }
}
