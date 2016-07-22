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

package es.danirod.jddprototype.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * This is the body the user controls. It has to jump and don't die, like the title of the game
 * says. You can make it jump by touching the screen. Don't let the player touch any spike or
 * you will lose.
 */
public class PlayerEntity extends Actor {

    /** The player texture. */
    private Texture texture;

    /** The world instance this player is in. */
    private World world;

    /** The body for this player. */
    private Body body;

    /** The fixture for this player. */
    private Fixture fixture;

    /**
     * Is the player alive? If he touches a spike, is not alive. The player will only move and
     * jump if it's alive. Otherwise it is said that the user has lost and the game is over.
     */
    private boolean alive = true;

    /**
     * Is the player jumping? If the player is jumping, then it is not possible to jump again
     * because the user cannot double jump. The flag has to be set when starting a jump and be
     * unset when touching the floor again.
     */
    private boolean jumping = false;

    /**
     * Does the player have to jump? This flag is used when the player touches the floor and the
     * user is still touching the screen, to make a double jump. Remember that we cannot add
     * a force inside a ContactListener. We have to use this flag to remember that the player
     * had to jump after the collision.
     */
    private boolean mustJump = false;

    public PlayerEntity(World world, Texture texture, Vector2 position) {
        this.world = world;
        this.texture = texture;

        // Create the player body.
        BodyDef def = new BodyDef();                // (1) Create the body definition.
        def.position.set(position);                 // (2) Put the body in the initial position.
        def.type = BodyDef.BodyType.DynamicBody;    // (3) Remember to make it dynamic.
        body = world.createBody(def);               // (4) Now create the body.

        // Give it some shape.
        PolygonShape box = new PolygonShape();      // (1) Create the shape.
        box.setAsBox(0.5f, 0.5f);                   // (2) 1x1 meter box.
        fixture = body.createFixture(box, 3);       // (3) Create the fixture.
        fixture.setUserData("player");              // (4) Set the user data.
        box.dispose();                              // (5) Destroy the shape.

        // Set the size to a value that is big enough to be rendered on the screen.
        setSize(es.danirod.jddprototype.game.Constants.PIXELS_IN_METER, es.danirod.jddprototype.game.Constants.PIXELS_IN_METER);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Always update the position of the actor when you are going to draw it, so that the
        // position of the actor on the screen is as accurate as possible to the current position
        // of the Box2D body.
        setPosition((body.getPosition().x - 0.5f) * es.danirod.jddprototype.game.Constants.PIXELS_IN_METER,
                    (body.getPosition().y - 0.5f) * es.danirod.jddprototype.game.Constants.PIXELS_IN_METER);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        // Jump when you touch the screen.
        if (Gdx.input.justTouched()) {
            jump();
        }

        // Jump if we were required to jump during a collision.
        if (mustJump) {
            mustJump = false;
            jump();
        }

        // If the player is alive, change the speed so that it moves.
        if (alive) {
            // Only change X speed. Do not change Y speed because if the player is jumping,
            // this speed has to be managed by the forces applied to the player. If we modify
            // Y speed, jumps can get very very weir.d
            float speedY = body.getLinearVelocity().y;
            body.setLinearVelocity(es.danirod.jddprototype.game.Constants.PLAYER_SPEED, speedY);
        }

        // If the player is jumping, apply some opposite force so that the player falls faster.
        if (jumping) {
            body.applyForceToCenter(0, -es.danirod.jddprototype.game.Constants.IMPULSE_JUMP * 1.15f, true);
        }
    }

    public void jump() {
        // The player must not be already jumping and be alive to jump.
        if (!jumping && alive) {
            jumping = true;

            // Apply an impulse to the player. This will make change the velocity almost
            // at the moment unlike using forces, which gradually changes the force used
            // during the jump. We get the position becase we have to apply the impulse
            // at the center of mass of the body.
            Vector2 position = body.getPosition();
            body.applyLinearImpulse(0, es.danirod.jddprototype.game.Constants.IMPULSE_JUMP, position.x, position.y, true);
        }
    }

    public void detach() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
    }

    // Getter and setter festival below here.

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void setMustJump(boolean mustJump) {
        this.mustJump = mustJump;
    }
}
