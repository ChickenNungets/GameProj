package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import com.google.common.escape.CharEscaper;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Engine {

    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    // Difficulty controls the zombie spawn rate per room.
    public static double DIFFICULTY = 0.4;
    private static int zombCount = 0;
    static long SEED = 0;
    public static final File CWD = new File(System.getProperty("user.dir"));
    //This file is where the game is saved to.
    public static final File location = PersistanceUtils.join(CWD, "GameState");
    private static boolean nextStage;
    //Represents the 8 adjacent points centering around the origin.
    public static final Point[] vectorsExt = {new Point(0, 1), new Point(1, 0), new Point(0, -1),
            new Point(-1, 0), new Point(1, 1), new Point(-1, 1), new Point(1, -1),
            new Point(-1, -1)};
    // Centers determines where hallways are drawn to and where zombies/steve spawns.
    private static ArrayList<Point> centers = new ArrayList<>();

    // The finalWorldFrame
    public static TETile[][] finalWorldFrame = null;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws InterruptedException, IOException {
        GameState G = new GameState(WIDTH, HEIGHT);
        G.ter.initialize(WIDTH, HEIGHT);
        // Show start screen.
        startScreen();
        char input;
        while (true) {
            waitForInput();
            input = StdDraw.nextKeyTyped();
            // New world
            if (input == 'n' || input == 'N') {
                drawFrame("Enter seed:", "type 'S' after seed is entered");
                String strSeed = "";
                while (true) {
                    waitForInput();
                    input = StdDraw.nextKeyTyped();
                    if (input == 'S' || input == 's') {
                        break;
                    }
                    strSeed += input;
                    drawFrame("Enter seed: " + strSeed, "type 'S' after seed is entered");
                }
                SEED = Long.parseLong(strSeed);
                G.setWorld(generateWorld(SEED), zombCount, new Random(SEED));
                drawFrame("Stage: 1");
                Thread.sleep(1500);
                gameLoop(G);
            } else if (input == 'L' || input == 'l') { // Load world
                if (location.exists()) {
                    // The game is saved as a GameState object.
                    G = PersistanceUtils.readObject(location, GameState.class);
                    gameLoop(G);
                } else {
                    System.exit(0);
                }

            } else if (input == 'Q' || input == 'q') { // Quit
                System.exit(0);
            }
        }
    }

    // Draws a black frame with strSeed in the center.
    private static void drawFrame(String strSeed) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, strSeed);
        StdDraw.show();
    }

    private static void drawFrame(String strSeed, String extra) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, strSeed);
        font = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3, extra);
        StdDraw.show();
    }

    //Creates the game over screen.
    public static void gameOver(int s) throws InterruptedException {
        drawFrame("Game Over (╯°□°)╯︵ ┻━┻ ");
        Thread.sleep(2000);
        drawFrame("You got to stage " + s + ".");
        Thread.sleep(2000);
        System.exit(0);
    }

    //Creates a stage won screen and changes nextStage to true,
    //activating the next stage.
    public static void gameWin() throws InterruptedException {
        drawFrame("Stage Won (▀̿Ĺ̯▀̿ ̿)");
        Thread.sleep(2000);
        nextStage = true;
    }

    //The main game loop.
    private static void gameLoop(GameState G) throws InterruptedException, IOException {
        char input = 0;
        //prevInput is used in the case the user enters :Q
        char prevInput;
        int zombieTimer = 0;
        //this is the delay in millis between loops.
        int refresh = 50;
        //determines the speed of zombies. A higher stages means faster moving.
        int zombRate = (600 * 5 / (G.stage + 2)) / refresh;
        nextStage = false;
        while (true) {
            Thread.sleep(refresh);
            //render a frame
            G.render();
            // If there was a key pressed, we check to see if
            // the user wants to quit or if they pressed was w, a, s, d, or t.
            if (StdDraw.hasNextKeyTyped()) {
                prevInput = input;
                input = StdDraw.nextKeyTyped();
                checkForQuit(prevInput, input, G);
                changeStateChar(G, input);
            }
            // every few cycles (determined by zombieRate) we update our zombies location.
            zombieTimer = (zombieTimer + 1) % zombRate;
            if (zombieTimer == 0) {
                G.updateZombie();
            }
            // Check to see if user has completed a stage. If so we reset and Move on to the next.
            if (nextStage) {
                int temp = G.stage;
                G.reset();
                G.stage = temp + 1;
                drawFrame("Stage: " + G.stage);
                Thread.sleep(1500);
                nextStage = false;
                gameLoop(G);
            }
        }
    }

    //Save and quit the game.
    private static void checkForQuit(char prevInput, char input, GameState G) throws IOException {
        String s = Character.toString(prevInput) + Character.toString(input);
        s = s.toUpperCase();
        if (s.equals(":Q")) {
            location.createNewFile();
            PersistanceUtils.writeObject(location, G);
            System.exit(0);
        }
    }

    //This method takes a key input and maps it to the
    //corresponding GameState command.
    private static void changeStateChar(GameState G, char input) throws InterruptedException {
        switch (input) {
            case 'w' -> G.moveSt(new Point(0, 1));
            case 'a' -> G.moveSt(new Point(-1, 0));
            case 's' -> G.moveSt(new Point(0, -1));
            case 'd' -> G.moveSt(new Point(1, 0));
            case 't' -> G.torchToggle();
            case 'p' -> G.zToggle();
        }
    }

    //Computer does nothing until a key is presesd.
    private static void waitForInput() throws InterruptedException {
        while (!StdDraw.hasNextKeyTyped()) {
            Thread.sleep(100);
        }
    }

    private static void startScreen() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 45);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) WIDTH / 2, (double) 3 * HEIGHT / 4, "MINESHAFT SURVIVAL");
        font = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Game Controls:");
        font = new Font("Monaco", Font.ITALIC, 18);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "Movement: W, A, S, D   Show Paths: P   Limit Vision: T");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        input = input.toUpperCase();
        String strSeed = "";
        long seed;
        char current = input.charAt(0);
        if (current == 'N') {
            int counter = 1;
            while (true) {
                current = input.charAt(counter);
                counter += 1;
                if (current == 'S') {
                    break;
                }
                strSeed += current;
            }
            seed = Long.parseLong(strSeed);
            finalWorldFrame = generateWorld(seed);
        }
        return finalWorldFrame;
        /* BigInteger tempSeed = new BigInteger(input.substring(1, input.length() - 1));
        Integer seed = tempSeed.intValue();
        TETile[][] finalWorldFrame = generateWorld(seed);
        return finalWorldFrame; */
    }

    public static void main(String[] args) {
        /*TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = generateWorld(934);

        ter.renderFrame(world); */
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("n456780s");
        ter.renderFrame(world, "", "");

    }
    //This will be the main function for world generation.
    static TETile[][] generateWorld(long seed) {
        centers = new ArrayList<>();

        Random r = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        initializeBackground(r, world);
        generateRooms(r, world);
        generateHalls(r, world);
        generateWalls(world);
        return world;
    }

    //Generate wooden walls surrounding world.
    private static void generateWalls(TETile[][] world) {
        for (int x = 0; x < WIDTH - 1; x += 1) {
            for (int y = 0; y < HEIGHT - 1; y += 1) {
                //basically if the tile is a random background tile.
                if (!world[x][y].equals(Tileset.NOTHING) && !world[x][y].equals(Tileset.WOOD)
                        && !Tileset.SPECIALS.contains(world[x][y])) {
                    //iterate over adjacent points
                    for (Point p: vectorsExt) {
                        //If an adjacent point is a Nothing tile, then the current tile must be a wall.
                        if (x + p.x >= 0 && y + p.y >= 0 && (world[x + p.x][y + p.y].equals(Tileset.NOTHING) || Tileset.SPECIALS.contains(world[x + p.x][y + p.y]))) {
                            world[x][y] = Tileset.WOOD;
                            break;
                        }
                    }
                }
            }
        }
    }
    // initialize background tiles.
    private static void initializeBackground(Random r, TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (y == HEIGHT - 1) {
                    world[x][y] = Tileset.MCGRASS;
                } else if (y < HEIGHT - 4) {
                    world[x][y] = Tileset.STONE;

                    if (RandomUtils.uniform(r) < 0.14) {
                        //the bound param controls the chance of diamond spawning.
                        world[x][y] = Tileset.DIAMOND;
                    }
                }
                else world[x][y] = Tileset.DIRT;
            }
        }
    }

    //Generates halls that connect the separate rooms.
    private static void generateHalls(Random r, TETile[][] world) {
        // Add hallways connecting consecutive rooms.
        for (int i = 1; i < centers.size(); i += 1) {
            addHallway(centers.get(i - 1), centers.get(i), world, r);
            // Add longer hallways between more distant rooms.
            if (i % 6 == 0) {
                addHallway(centers.get(i - 5), centers.get(i), world, r);
            }
        }
    }

    private static void addHallway(Point prevRoom, Point endRoom, TETile[][] world, Random r) {
        int startX = prevRoom.x; int endX = endRoom.x;
        int startY = prevRoom.y; int endY = endRoom.y;
        int xDiff = (int) Math.signum((double) (endX - startX));
        int yDiff = (int) Math.signum((double) (endY - startY));
        int direction = RandomUtils.uniform(r, 0, 2);

        if (direction == 0) {
            for (int x = startX; x != endX; x += xDiff) {
                if (!Tileset.SPECIALS.contains((world[x][startY]))) {
                    world[x][startY] = Tileset.NOTHING;
                }
            }
            for (int y = startY; y != endY; y += yDiff) {
                if (!Tileset.SPECIALS.contains((world[endX][y]))) {
                    world[endX][y] = Tileset.NOTHING;
                }
            }
        } else if (direction == 1) {
            for (int x = startX; x != endX; x += xDiff) {
                if (!Tileset.SPECIALS.contains((world[x][endY]))) {
                    world[x][endY] = Tileset.NOTHING;
                }
            }
            for (int y = startY; y != endY; y += yDiff) {
                if (!Tileset.SPECIALS.contains((world[startX][y]))) {
                    world[startX][y] = Tileset.NOTHING;
                }
            }
        }
    }


    //Generates random sized rooms.
    private static void generateRooms(Random r, TETile[][] world) {
        //steveSpawn determines what room steve will spawn in.
        int steveSpawn = 0;
        double zombieSpawn;
        int roomCounter = 0;
        for (int x = 2; x < WIDTH * 7 / 8; x += WIDTH / 8) {
            for (int y = 1; y < HEIGHT * 3 / 4; y += HEIGHT / 4) {
                int xVal = RandomUtils.uniform(r, x, x - 1 + WIDTH / 8 - 1);
                int yVal = RandomUtils.uniform(r, y, y - 1 + HEIGHT / 4);
                zombieSpawn = RandomUtils.uniform(r);
                world[xVal][yVal] = Tileset.NOTHING;
                spawnRoom(r, world, xVal, yVal, roomCounter == steveSpawn, roomCounter > 3 && zombieSpawn < DIFFICULTY, roomCounter == 20);
                roomCounter += 1;
            }
        }
    }

    //Spawns a random sized room with its bottom left corner at the specified position.
    private static void spawnRoom(Random r, TETile[][] world, int xVal, int yVal, boolean spawnSteve, boolean zombSpawn, boolean last) {
        int xLim = RandomUtils.uniform(r, 3, WIDTH / 8);
        int yLim = RandomUtils.uniform(r, 3, HEIGHT / 4);
        int xCenter = RandomUtils.uniform(r, xVal, xVal + xLim);
        int yCenter = RandomUtils.uniform(r, yVal, yVal + yLim);

        for (int x = xVal; x < xVal + xLim; x += 1) {
            for (int y = yVal; y < yVal + yLim; y += 1) {
                world[x][y] = Tileset.NOTHING;
                if (x == xCenter && y == yCenter) {
                    if (spawnSteve) {
                        world[x][y] = Tileset.STEVE;
                    } else if (last) {
                        world[x][y] = Tileset.GOLD;
                    } else if (zombSpawn) {
                        zombCount += 1;
                        world[x][y] = Tileset.ZOMBIE;
                    } else {
                        world[x][y] = Tileset.CENTERNOTHING;
                    }
                    centers.add(new Point(x, y));
                }
            }
        }
    }
}
