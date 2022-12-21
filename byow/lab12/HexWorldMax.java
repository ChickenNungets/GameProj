package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */

public class HexWorldMax {
    // change length to get different size hexagons.
    // change seed to get a different map.
    private static final int SEED = 101010;
    private static final int LENGTH = 4;
    private static final int WIDTH = 11 * LENGTH - 1;
    private static final int HEIGHT = 10 * LENGTH + 2;

    // adds a hexagon with bottom length l and
    // with its bottom left tile at the position
    // specified by row and col.
    public static void addHexagon(TETile[][] tiles, int row, int col, TETile type) {
        int difference = 0;
        // add some randomness to the tile color.
        type = TETile.colorVariant(type, 120, 120, 120, new Random());
        for (int y = row; y < row + 2 * LENGTH; y += 1) {
            for (int x = col - difference; x < col + LENGTH + difference; x += 1) {
                tiles[x][y] = type;
            }
            // Based on the current value of y, difference increases,
            // stays the same, or decreases. This is what creates the
            // hexagon shape instead of just a rectangle.
            if (y < row + LENGTH - 1) {
                difference += 1;
            } else if (y > row + LENGTH - 1) {
                difference -= 1;

            }
        }
    }

     public static void tessellatedHex ( int seed, TETile[][] tiles){
            int difference = 0;
            for (int x = LENGTH + 1; x < 10 * LENGTH - 3; x += 2 * LENGTH - 1) {
                for (int y = 2 * LENGTH + 1 - difference; y < 6 * LENGTH + 3 + difference; y += 2 * LENGTH) {
                    addHexagon(tiles, y, x, randTile(seed));
                    seed += 1;
                }
                //The same difference technique is used here,
                //because Im basically creating one big hexagon
                //made of smaller hexagons.
                if (x < 4 * LENGTH - 1) {
                    difference += LENGTH;
                } else if (x > 4 * LENGTH - 1) {
                    difference -= LENGTH;
                }

            }
        }

    public static void main (String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);


        TETile[][] world = new TETile[WIDTH][HEIGHT];
        init(world);

        tessellatedHex(SEED, world);
        ter.renderFrame(world, "", "");
    }

    public static void init (TETile[][]world){
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }
    // Returns a random Tile from Tileset based off seed.
    private static TETile randTile ( int seed){
        Random r = new Random(seed);
        int rand = r.nextInt(6);
        return switch (rand) {
            case 0 -> Tileset.FLOWER;
            case 1 -> Tileset.GRASS;
            case 2 -> Tileset.MOUNTAIN;
            case 3 -> Tileset.SAND;
            case 4 -> Tileset.TREE;
            default -> Tileset.WATER;
        };

    }
}
