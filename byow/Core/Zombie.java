package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Zombie implements Serializable {
    int x;
    int y;
    ArrayList<Point> path = new ArrayList<>();

    public Zombie(int px, int py) {
        x = px;
        y = py;
    }

    public void updatePath(Point steve, TETile[][] world) {
        //Updates the path the zombie will follow.
        Pathfinding p = new Pathfinding(world);
        ArrayList<Point> pth = p.findPath(x, y, steve.x, steve.y);
        if (pth != null) {
            path = pth;
        }
    }

    public void pathReset(Point steve, TETile[][] world, Random random) {
        //Updates the zombies path based on distance to steve. The closer
        //to steve it is, the more likely it is to update its path.
        double md = Math.abs(steve.x - x) + Math.abs(steve.y - y);
        if (md < 7 || Math.pow(RandomUtils.uniform(random), 2) < 7 / md) {
            updatePath(steve, world);
        }
    }

    public Point progress() {
        // Make the zombie step forward in its path. It returns the
        // difference between its next and current position, which is
        // used in the move method in GameState.
        if (path != null && path.size() > 1) {
            Point p = new Point();
            p.x = path.get(1).x - x;
            p.y = path.get(1).y - y;
            x = path.get(1).x;
            y = path.get(1).y;
            path.remove(0);
            return p;
        } else {
            return new Point(0, 0);
        }
    }
}
