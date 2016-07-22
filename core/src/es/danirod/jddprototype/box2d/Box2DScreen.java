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

package es.danirod.jddprototype.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This screen is a prototype of the game using only Box2D. It uses a renderer to show you the
 * things that are hidden in Box2D usually. There are a lot of variables here, so don't judge me.
 */
public class Box2DScreen extends es.danirod.jddprototype.game.BaseScreen {

    public Box2DScreen(es.danirod.jddprototype.game.MainGame game) {
        super(game);
    }

    /** World instance. Everything in Box2D has to be added to the world. */
    private World world;

    /** Debug renderer. It renders worlds to the screen to make it possible to see them. */
    private Box2DDebugRenderer renderer;

    /** Camera. We have to create a camera to tell the renderer how to draw the world. */
    private OrthographicCamera camera;

    /** The bodies that we use in this example. */
    private Body minijoeBody, floorBody, spikeBody;

    /** The fixtures that we use in this example. */
    private Fixture minijoeFixture, floorFixture, spikeFixture;

    /** Some variables that could be encapsulated if this had been a better example. */
    private boolean mustJump, isJumping, isAlive = true;

    @Override
    public void show() {
        // Create the world. We give it some gravity that is similar to the one used in the earth.
        // Box2D, like any other physics system in this world, treats Y coordinates upwards. Thus,
        // by using y = -10, we make gravity go down. If you look at your physics book, it is
        // the same.
        world = new World(new Vector2(0, -10), true);

        // Create a renderer and a camera to make it possible for us to see what is in the world.
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(16, 9);
        camera.translate(0, 1);

        // Create the bodies for entities in this world.
        minijoeBody = world.createBody(BodyDefFactory.createPlayer());
        floorBody = world.createBody(BodyDefFactory.createFloor());
        spikeBody = world.createBody(BodyDefFactory.createSpikes(6f));

        // Create the fixture for the entities in this world.
        minijoeFixture = FixtureFactory.createPlayerFixture(minijoeBody);
        floorFixture = FixtureFactory.createFloorFixture(floorBody);
        spikeFixture = FixtureFactory.createSpikeFixture(spikeBody);

        // Set the user data to some categories that will let us handle collisions in a more
        // generic way. Player can collide with floor and with spike.
        minijoeFixture.setUserData("player");
        floorFixture.setUserData("floor");
        spikeFixture.setUserData("spike");

        // Set the contact listener for this world. The contact listener will handle contacts.
        world.setContactListener(new Box2DScreenContactListener());
    }

    @Override
    public void dispose() {
        // Destroy all the fixtures from their bodies.
        floorBody.destroyFixture(floorFixture);
        minijoeBody.destroyFixture(minijoeFixture);
        spikeBody.destroyFixture(spikeFixture);

        // Destroy all the bodies from their world.
        world.destroyBody(minijoeBody);
        world.destroyBody(floorBody);
        world.destroyBody(spikeBody);

        // Dispose all the things.
        world.dispose();
        renderer.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // The player must jump if the screen has been touched and if it is not already jumping.
        // By setting mustJump to true, it will start jumping in the next frame.
        if (Gdx.input.justTouched() && !isJumping) {
            mustJump = true;
        }

        // If the player must jump, then jump.
        if (mustJump) {
            mustJump = false;
            makePlayerJump();
        }

        // Keep moving the player while is alive. As I explain in the video, I only want to change
        // the horizontal component for the speed. I keep the Y speed because I don't want to
        // change the speed used by jumping forces.
        if (isAlive) {
            float velocidadY = minijoeBody.getLinearVelocity().y;
            minijoeBody.setLinearVelocity(8, velocidadY);
        }

        // Iterate the world.
        world.step(delta, 6, 2);

        // Render the world.
        camera.update();
        renderer.render(world, camera.combined);
    }

    private void makePlayerJump() {
        // Apply an impulse to change the speed of the body at the moment.
        Vector2 position = minijoeBody.getPosition();
        minijoeBody.applyLinearImpulse(0, 20, position.x, position.y, true);
    }

    /**
     * This is the contact listener I use to handle the collisions.
     */
    private class Box2DScreenContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            // Get the fixtures.
            Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();

            // Safety check. If there is no user data, then don't handle this collision.
            // I don't show this line in the videos but is important to avoid NullPointerExceptions.
            if (fixtureA.getUserData() == null || fixtureB.getUserData() == null) {
                return;
            }

            // You know what is lame about contacts? You never know which entity is A and which
            // entity is B, so you have to write the same code twice. This is sad but important.
            if ((fixtureA.getUserData().equals("player") && fixtureB.getUserData().equals("floor")) ||
                    (fixtureA.getUserData().equals("floor") && fixtureB.getUserData().equals("player"))) {
                // So player and floor have collided.

                if (Gdx.input.isTouched()) {
                    // If the screen is still touched, jump again.
                    mustJump = true;
                }

                // Stop jumping anyway.
                isJumping = false;
            }

            if ((fixtureA.getUserData().equals("player") && fixtureB.getUserData().equals("spike")) ||
                    (fixtureA.getUserData().equals("spike") && fixtureB.getUserData().equals("player"))) {
                // Spike and player have collided. Insta-death.
                isAlive = false;
            }
        }

        @Override
        public void endContact(Contact contact) {
            // End the collision.
            Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();

            // This is another way for guessing which fixture are you working with. If you have
            // the reference to that fixture you can just check if both variable reference the
            // same instance.
            if (fixtureA == minijoeFixture && fixtureB == floorFixture) {
                isJumping = true;
            }

            if (fixtureA == floorFixture && fixtureB == minijoeFixture) {
                isJumping = true;
            }
        }

        // Lonely methods I don't use.
        @Override public void preSolve(Contact contact, Manifold oldManifold) { }
        @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
    }
}
