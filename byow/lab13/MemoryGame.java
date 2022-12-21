package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int len;
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) throws InterruptedException {


        //long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, 0);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.len = 3;

        //TODO: Initialize random number generator
    }

    public String generateRandomString(int n) {
        String ret = "";
        for (int i = 0; i < n; i += 1) {
            ret += (char) RandomUtils.uniform(new Random(), 97, 123);
        }
        return ret;
    }

    public void drawFrame(String s, boolean gameOver, String topMessage) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) width / 2, (double) height / 2, s);
        font = new Font("Monaco", Font.PLAIN, 18);
        StdDraw.setFont(font);
        if (!gameOver) {
            StdDraw.text((double) width / 2, height - 1, "Round: " + (len - 2) + "        " + topMessage + "        " + ENCOURAGEMENT[RandomUtils.uniform(new Random(), ENCOURAGEMENT.length)]);
            StdDraw.text(width / 2, height - 2, "---------------------------------------------------------------------------------------------------------");
        }
        StdDraw.show();
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) throws InterruptedException {
        for (int i = 0; i < letters.length(); i += 1) {
            drawFrame(Character.toString(letters.charAt(i)), false, "Watch!");
            Thread.sleep(1000);
            drawFrame("", false, "Watch!");
            Thread.sleep(500);
        }
    }

    public String solicitNCharsInput(int n) throws InterruptedException {
        String ret = "";
        drawFrame(ret, false, "Type!");
        for (int i = 0; i < n; i += 1) {
            while (!StdDraw.hasNextKeyTyped()) {
                Thread.sleep(100);
            }
            ret += StdDraw.nextKeyTyped();
            drawFrame(ret, false, "Type!");
        }
        Thread.sleep(1000);
        return ret;
    }

    public void startGame() throws InterruptedException {
        //TODO: Set any relevant variables before the game starts
        String actual = generateRandomString(len);
        String guess;
        //TODO: Establish Engine loop
        while (true) {
            drawFrame("Round " + (len - 2), false, "");
            Thread.sleep(1000);
            flashSequence(actual);
            guess = solicitNCharsInput(len);
            if (!guess.equals(actual)) {
                drawFrame("Game Over!\n You made it to round " + (len - 2), true, "");
                break;
            }
            len += 1;
            actual = generateRandomString(len);
        }
    }

}