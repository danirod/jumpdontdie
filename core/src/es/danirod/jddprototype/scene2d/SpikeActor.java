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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * This actor represents the spikes used in the Scene2D screen.
 */
public class SpikeActor extends Actor {

    /** Texture for the spikes. */
    private TextureRegion spikes;

    /** Speed the spikes use to travel leftwards. */
    private float speed;

    /**
     * Create some spikes.
     * @param spikes
     * @param x     initial position
     * @param y     initial position
     * @param speed     speed for travelling
     */
    public SpikeActor(TextureRegion spikes, float x, float y, float speed) {
        this.spikes = spikes;
        this.speed = speed;

        // Place the spike where you have been told to.
        setPosition(x, y);
        setSize(spikes.getRegionWidth(), spikes.getRegionHeight());
    }

    @Override
    public void act(float delta) {
        // When acting, infinitesimally move the spikes to the left so that they look like if
        // they actually moved to the left. This is linear motion. It travels left as much as
        // it can using the speed and the time between this frame and the previous one.
        setX(getX() - speed * delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(spikes, getX(), getY());
    }
}
