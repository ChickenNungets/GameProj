package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;

/**
 * GameState is used to implement interactivity. A GameState instance
 * will store a world and information regarding a players current state,
 * such as location or health.
 */
public class GameState implements Serializable {
    // These vectors represent the four cardinal directions.
    private static final Point[] VECTORS =
        {new Point(0, 1), new Point(1, 0), new Point(0, -1), new Point(-1, 0)};
    // C vectors is a collection of vectors that represent points covered by a circle.
    // used when limiting vision.
    private static final HashSet<Point> CVECTORS = new HashSet<>();
    private final int WIDTH;
    private final int HEIGHT;
    private Random random;
    TERenderer ter;
    // initialWorld is used when resetting between stages.
    TETile[][] initialWorld;
    TETile[][] world;
    // tWorld is used to display limited vision.
    TETile[][] tWorld;
    // zWorld is used to display zombie paths.
    TETile[][] zWorld;
    Point steveLocation;
    // An array containing all zombies.
    Zombie[] zombieLocation;
    Point gold;
    // Radius of circle used for cVectors.
    private final int RADIUS = 5;
    int zCount;
    int stage;
    int health;
    //These to values are used to toggle on and off limited vision/zombie paths.
    boolean torch;
    boolean zPaths;

    public GameState(int width, int height) {
        //initialize some values
        ter = new TERenderer();
        steveLocation = new Point();
        gold = new Point();
        ter.initialize(width, height);
        WIDTH = width;
        HEIGHT = height;

        //initialize cVectors.
        for (int i = -RADIUS; i <= RADIUS; i += 1) {
            for (int j = -RADIUS; j <= RADIUS; j += 1) {
                if (Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2)) <= RADIUS) {
                    CVECTORS.add(new Point(i, j));
                }
            }
        }
    }

    //initialize GameState to the given world.
    public void setWorld(TETile[][] w, int zombCount, Random r) {
        tWorld = new TETile[WIDTH][HEIGHT];
        zWorld = new TETile[WIDTH][HEIGHT];
        random = r;
        torch = false;
        zPaths = false;
        zCount = zombCount;
        initialWorld = deepCopy(w);
        stage = 1;
        world = w;
        int z = 0;
        zombieLocation = new Zombie[zombCount];
        // Here we iterate over the given world and initialize steve, out zombies,
        // and our different worlds.
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                zWorld[x][y] = w[x][y];
                tWorld[x][y] = Tileset.NOTHING;
                if (w[x][y].equals(Tileset.STEVE)) {
                    steveLocation.x = x;
                    steveLocation.y = y;
                } else if (w[x][y].equals(Tileset.ZOMBIE)) {
                    zombieLocation[z] = new Zombie(x, y);
                    z += 1;
                } else if (w[x][y].equals(Tileset.GOLD)) {
                    gold = new Point(x, y);
                }
            }
        }
        // Loop over zombies and initialize their paths.
        for (Zombie zo: zombieLocation) {
            if (zo != null) {
                zo.updatePath(steveLocation, world);
            }
        }
        health = 10;
    }

    // Move src in the given vector direction. Used to update player location as well
    // zombie location.
    public Point move(Point src, Point dir, TETile type) throws InterruptedException {
        // next is the src will move to.
        Point next = new Point(src.x + dir.x, src.y + dir.y);
        // check that next is not out of bounds.
        if (world[next.x][next.y].equals(Tileset.NOTHING)
                || world[next.x][next.y].equals(Tileset.CENTERNOTHING)) {
            // update tiles.
            world[src.x][src.y] = Tileset.NOTHING;
            world[next.x][next.y] = type;
            src = next;
        }
        //return the point we are moving to.
        return src;
    }

    // Move steve in direction dir.
    public void moveSt(Point dir) throws InterruptedException {
        // First simply move steve.
        steveLocation = move(steveLocation, dir, Tileset.STEVE);
        // Check for zombies around steve, and damage appropriately.
        checkDamage();
        // If player is less than 2 tiles away from the gold block:
        if (steveLocation.distanceSq(gold) < 4) {
            for (Point p : VECTORS) {
                // Check if the gold block is next to player.
                if (world[steveLocation.x + p.x][steveLocation.y + p.y].equals(Tileset.GOLD)) {
                    render();
                    Thread.sleep(500);
                    Engine.gameWin();
                }
            }
        }
    }
    // Check for zombies around player, and damage appropriately.
    private void checkDamage() throws InterruptedException {
        for (Point p: VECTORS) {
            if (world[steveLocation.x + p.x][steveLocation.y + p.y].equals(Tileset.ZOMBIE)) {
                render();
                Thread.sleep(200);
                health -= stage;
                if (health <= 0) {
                    Engine.gameOver(stage);
                }
            }
        }
    }

    // Render za warudo.
    public void render() {
        //initialize his health.
        String hud = "";
        String heart = "\u2665";
        for (int i = 0; i < health; i += 1) {
            hud += heart;
        }
        // if hte player toggled torch or their health is low enough,
        // render the world with limited vision.
        if (torch || health <= stage) {
            torch();
            ter.renderFrame(tWorld, hud, "Stage: " + stage);
        } else if (zPaths) { // Renders the world with zombie paths.
            zombPaths();
            ter.renderFrame(zWorld, hud, "Stage: " + stage);
        } else { //Renders the world normally.
            ter.renderFrame(world, hud, "Stage: " + stage);
        }
    }

    //Update the paths and locations of zombies.
    public void updateZombie() throws InterruptedException {
        for (Zombie z: zombieLocation) {
            if (z == null) {
                continue;
            }
            //Update zombie path.
            z.pathReset(steveLocation, world, random);
            // Move the zombie in its path. z.progress returns the appropriate direction.
            Point n = move(new Point(z.x, z.y), z.progress(), Tileset.ZOMBIE);
            //update zombie location.
            z.x = n.x;
            z.y = n.y;
        }
        // With our new zombie locations, check to see if steve should be damaged.
        checkDamage();
    }
    /**
     * Old pathfinding where zombie just went in the general direction of steve.
     for (int i = 0; i < zombieLocation.length; i += 1) {
     Point z = zombieLocation[i];
     if (z == null) {
     continue;
     }
     Point diff = new Point(steveLocation.x - z.x,
     steveLocation.y - z.y);
     boolean check = false;
     if (Math.abs(diff.distance(new Point(0, 0))) < 2) {
     check = true;
     }
     Point backup = new Point();
     if (diff.x == 0) {
     diff.x = 1;
     } else if (diff.y == 0) {
     diff.y = 1;
     }
     if (Math.abs(diff.y) > Math.abs(diff.x)) {
     backup.x = diff.x / Math.abs(diff.x);
     backup.y = 0;
     diff.x = 0;
     diff.y = diff.y / Math.abs(diff.y);

     } else {
     backup.y = diff.y / Math.abs(diff.y);
     backup.x = 0;
     diff.y = 0;
     diff.x = diff.x / Math.abs(diff.x);
     }
     Point prev = z;

     zombieLocation[i] = move(z, diff, Tileset.ZOMBIE, check);

     if (zombieLocation[i].equals(prev)) {
     zombieLocation[i] = move(z, backup, Tileset.ZOMBIE, check);
     }
     }
     checkDamage();
     }
     **/

    // Reset the world in the event the player clears a stage.
    public void reset() {
        world = initialWorld;
        setWorld(initialWorld, zCount, random);
    }

    //Source: https://stackoverflow.com/questions/1564832/how-do-i-do-a-deep-copy-of-a-2d-array
    // -in-java
    //Thanks to SlavaSt for this solution. Makes a deep copy of a world.
    private TETile[][] deepCopy(TETile[][] matrix) {
        return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
    }

    //toggle torch(limited vision) if player presses t.
    public void torchToggle() {
        torch = !torch;
    }

    // Update tWorld so it is accurate.
    private void torch() {
        int x;
        int y;
        for (int xi = 0; xi < WIDTH; xi += 1) {
            for (int yi = 0; yi < HEIGHT; yi += 1) {
                tWorld[xi][yi] = Tileset.NOTHING;
            }
        }
        for (Point p : CVECTORS) {
            x = p.x + steveLocation.x;
            y = p.y + steveLocation.y;
            if (inBounds(x, y)) {
                tWorld[x][y] = world[x][y];
            }
        }
    }

    // toggle zombie paths if payer presses z.
    public void zToggle() {
        zPaths = !zPaths;
    }

    //Update z world so it is accurate with world.
    public void zombPaths() {
        for (int xi = 0; xi < WIDTH; xi += 1) {
            for (int yi = 0; yi < HEIGHT; yi += 1) {
                zWorld[xi][yi] = world[xi][yi];
            }
        }

        //draw in zombie paths.
        for (Zombie z: zombieLocation) {
            if (z != null) {
                for (Point p: z.path) {
                    if (zWorld[p.x][p.y] != Tileset.STEVE && zWorld[p.x][p.y] != Tileset.ZOMBIE) {
                        zWorld[p.x][p.y] = Tileset.PATH;
                    }
                }
            }
        }
    }

    // returns if the given point is within game bounds.
    private boolean inBounds(int x, int y) {
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1) {
            return false;
        }
        return true;
    }
}
