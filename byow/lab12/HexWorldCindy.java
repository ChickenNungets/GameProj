package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorldCindy {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;


    // Draw a hexagon of size s, where xStart and yStart are the x and y coordinates of the bottom-left corner
    public static void addHexagon(TERenderer ter, TETile[][] world, int s, int xStart, int yStart) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        // TERenderer ter = new TERenderer();
        // ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        /* TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        } */

        // Draw bottom half of hexagon.
        for (int i = 0; i < s; i += 1) {
            for (int x = xStart - i; x < xStart + i + 2; x += 1) {
                world[x][yStart + i] = Tileset.WALL;
            }
        }

        // Draw top half of hexagon.
        int newHeight = yStart + s + 1;
        for (int i = s - 1; i > -1; i -= 1) {
            for (int x = xStart - i; x < xStart + i + 2; x += 1) {
                world[x][newHeight + s - i - 2] = Tileset.WALL;
            }
        }

        ter.renderFrame(world, "", "");
    }

    public static int[][] helperTesselation(int s) {
        int[][] output = new int[19][0];
        int index = 0;
        int startX = 20; int startY = 10;
        for (int i = 0; i < 5; i++) {
            int x = startX;
            int y = startY + (2 * s * i);
            output[index] = new int[]{x, y};
            index += 1;
        }
        for (int i = 0; i < 4; i++) {
            int x1 = startX - (2*s) + 2;
            int y1 = startY + s + (2*s*i);
            output[index] = new int[]{x1, y1};
            int x2 = startX + (2*s) - 2;
            int y2 = startY + s + (2*s*i);
            output[index + 1] = new int[]{x2, y2};
            index += 2;
        }
        for (int i = 0; i < 3; i++) {
            int x1 = startX - 2*(2*s) + 3;
            int y1 = startY + (2*s) + (2*s*i);
            output[index] = new int[]{x1, y1};
            int x2 = startX + 2*(2*s) - 3;
            int y2 = startY + (2*s) + (2*s*i);
            output[index + 1] = new int[]{x2, y2};
            index += 2;
        }
        return output;
    }

    public static void tesselation(TERenderer ter, TETile[][] world, int s) {
        for (int i = 0; i < 19; i++) {
            int[] helper = helperTesselation(s)[i];
            int newX = helper[0]; int newY = helper[1];
            addHexagon(ter, world, s, newX, newY);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        tesselation(ter, world, 2);
    }
}
