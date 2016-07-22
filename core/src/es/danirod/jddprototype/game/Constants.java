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

/**
 * Some class for defining constant values used in the game, so that they can be changed from
 * a single location instead of lurking the code to find the values.
 */
public class Constants {

    /**
     * How many pixels there are in a meter. As explained in the video, this is important, because
     * your simulation is in meters but you have to somehow convert these meters to pixels so that
     * they can be rendered at a size visible by the user.
     */
    public static final float PIXELS_IN_METER = 90f;

    /**
     * The force in N/s that the player uses to jump in an impulse. This force will also be applied
     * in the opposite direction to make the player fall faster multiplied by some value to make
     * it stronger.
     */
    public static final int IMPULSE_JUMP = 20;

    /**
     * This is the speed that the player has. The larger this value is, the faster the player will
     * go. Don't make this value very high without putting more distance between every obstacle
     * in the circuit.
     */
    public static final float PLAYER_SPEED = 8f;
}
