package byow.TileEngine;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset implements Serializable {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    public static final TETile PATH = new TETile('*', Color.red, Color.BLACK, "path");
    public static final TETile DIAMOND = new TETile('m', Color.green, Color.GRAY,
            "diamond", "byow/PNGs/diamond.png");
    public static final TETile DIRT = new TETile('m', Color.green, Color.GRAY,
            "dirt", "byow/PNGs/dirt.png");
    public static final TETile MCGRASS = new TETile('m', Color.green, Color.GRAY,
            "grass", "byow/PNGs/grass.png");
    public static final TETile STONE = new TETile('m', Color.green, Color.GRAY,
            "stone", "byow/PNGs/stone.png");
    public static final TETile WOOD = new TETile('m', Color.green, Color.GRAY,
            "wood", "byow/PNGs/wood.png");
    public static final TETile CENTERNOTHING = new TETile(' ', Color.black, Color.black,
            "nothing?");
    public static final TETile STEVE = new TETile('@', Color.YELLOW, Color.YELLOW,
            "Steve", "byow/PNGs/steve.png");
    public static final TETile ZOMBIE = new TETile('@', Color.RED, Color.RED,
            "a zombie.", "byow/PNGs/zombie.png");
    public static final TETile GOLD = new TETile('G', Color.YELLOW, Color.BLACK,
            "Gold!", "byow/PNGs/gold.png");

    public static final HashSet<TETile> SPECIALS =
            new HashSet<>(Arrays.asList(CENTERNOTHING, ZOMBIE, STEVE, GOLD));
    public static final TETile CREEPER = new TETile('G', Color.YELLOW, Color.BLACK,
            "Gold!", "creeper/PNGs/gold.png");
}
