package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

// Source: http://www.cokeandcode.com/main/tutorials/path-finding/
// Thanks to this website for a very clear tutorial of which much is this code is based around.

public class Pathfinding implements Serializable {
    //Map is our internal node representation of the world.
    private Node[][] map;
    public static final Point[] VECTORS = {new Point(0, 1), new Point(1, 0), new Point(0, -1),
        new Point(-1, 0)};
    private int height;
    private int width;

    //Initialize map to the given world.
    public Pathfinding(TETile[][] world) {
        width = world.length;
        height = world[0].length;
        map = new Node[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                map[x][y] = new Node(x, y, world[x][y]);
            }
        }
    }
    //A* pathfinding algorithm: sx and sy are the staring point, and tx and ty the end.
    public ArrayList<Point> findPath(int sx, int sy, int tx, int ty) {
        //open tracks the nodes we would like to explore next.
        PriorityQueue<Node> open = new PriorityQueue<>(10, new NodeComparator());

        // closed tracks nodes that we have explored.
        ArrayList<Node> closed = new ArrayList<>();

        map[sx][sy].cost = 0;
        open.clear();
        open.add(map[sx][sy]);
        map[tx][ty].parent = null;

        // while there are still unexplored nodes to consider:
        while (open.size() != 0) {
            Node current = open.poll();
            closed.add(current);
            // If current is our goal, we are done.
            if (current == map[tx][ty]) {
                break;
            }

            // Consider all neighbors of current.
            for (Point p: VECTORS) {
                int xp = current.x + p.x;
                int yp = current.y + p.y;

                // Only consider a neighbor if it is passable (traversable).
                if (map[xp][yp].passable) {
                    //Because this is an unweighted graph we simply add 1 to cost.
                    int nextStepCost = current.cost + 1;
                    Node neighbor = map[xp][yp];

                    // If our current calculated cost is better that the best we have
                    // found so far, we remove it from open and closed.
                    if (nextStepCost < neighbor.cost) {
                        if (open.contains(neighbor)) {
                            open.remove(neighbor);
                        }
                        if (closed.contains(neighbor)) {
                            closed.remove(neighbor);
                        }
                    }

                    // In the case neighbor was removed from open and closed,
                    // we update its values appropriately, because a better path
                    // was found.
                    if (!open.contains(neighbor) && !closed.contains(neighbor)) {
                        neighbor.cost = nextStepCost;
                        neighbor.parent = current;
                        // This is where score is initialized.
                        neighbor.score = neighbor.cost + neighbor.h(tx, ty);
                        open.add(neighbor);
                    }
                }
            }
        }

        // In this part we generate the path.
        if (map[tx][ty].parent == null) {
            return null;
        } else {
            ArrayList<Point> p = new ArrayList<>();
            //simply iterate through the targets parents until we get to the source.
            for (Node target = map[tx][ty]; target != map[sx][sy]; target = target.parent) {
                p.add(0, new Point(target.x, target.y));
            }
            p.add(0, new Point(sx, sy));
            return p;
        }
    }

    // This comparator is what orders our nodes in the open set above.
    // A node has greater priority if its overall score is smaller,
    // indicating a potential good path. Score is the sum of distance
    // from source and our heuristic function for the given node.
    // I am using manhattan distance because it is easier to compute
    // than euclidean distance.
    private class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            if (o1.score < o2.score) {
                return -1;
            } else if (o1.score > o2.score) {
                return 1;
            }
            return 0;
        }
    }

    private class Node {
        int x;
        int y;
        boolean passable;
        int cost;
        int score;
        Node parent;

        Node(int px, int py, TETile type) {
            x = px;
            y = py;
            // Cost is initialized to be max value.
            cost = Integer.MAX_VALUE;
            if (type.equals(Tileset.NOTHING) || type.equals(Tileset.CENTERNOTHING)
                    || type.equals(Tileset.STEVE)) {
                passable = true;
            } else {
                passable = false;
            }
        }

        // Heuristic function
        public int h(int destx, int desty) {
            //manhattan heuristic
            return Math.abs(x - destx) + Math.abs(y - desty);
        }
    }
}
