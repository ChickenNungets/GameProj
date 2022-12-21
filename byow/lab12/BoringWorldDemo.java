package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class BoringWorldDemo {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Random r = new Random(1191);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (y == HEIGHT - 1) {
                    world[x][y] = Tileset.MCGRASS;
                } else if (y < HEIGHT - 4) {
                    world[x][y] = Tileset.STONE;
                    if (r.nextInt(10) == 0) {
                        world[x][y] = Tileset.DIAMOND;
                    }
                }
                else world[x][y] = Tileset.DIRT;
            }
        }

        // draws the world to the screen
        ter.renderFrame(world, "", "");
    }


}
