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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Helper class for creating fixture definitions without having all that code in Box2DScreen.
 * Yep, in the videos this code is in Box2DScreen. I thought it was a good idea to move this
 * code outside that class to make it easier to see.
 */
public class FixtureFactory {
    public static Fixture createPlayerFixture(Body playerBody) {
        PolygonShape minijoeShape = new PolygonShape();
        minijoeShape.setAsBox(0.5f, 0.5f);
        Fixture fixture = playerBody.createFixture(minijoeShape, 3);
        minijoeShape.dispose();
        return fixture;
    }

    public static Fixture createFloorFixture(Body floorBody) {
        PolygonShape box = new PolygonShape();
        box.setAsBox(500, 1);
        Fixture fixture = floorBody.createFixture(box, 1);
        box.dispose();
        return fixture;
    }

    public static Fixture createSpikeFixture(Body pinchoBody) {
        // This is the harder shape because it is not a box. I have to design the shape in
        // terms of vertices. So I create a vertex array to give the shape all the vertices.
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-0.5f, -0.5f);
        vertices[1] = new Vector2(0.5f, -0.5f);
        vertices[2] = new Vector2(0, 0.5f);

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        Fixture fixture = pinchoBody.createFixture(shape, 1);
        shape.dispose();
        return fixture;
    }
}
