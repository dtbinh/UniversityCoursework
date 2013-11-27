package closestPair;

import java.util.*;

/** Two-dimensional point. */
public class Point {

    /** Position on the X axis. */
    public Integer x;

    /** Position on the Y axis. */
    public Integer y;

    /** Constructor. */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Get the position on the X axis. */
    public int getX() {
        return this.x;
    }

    /** Get the position on the Y axis. */
    public int getY() {
        return this.y;
    }

    /** Calculate the square distance between this point and another point. */
    public int sqrDist(Point other) {
        int diffOfX = x - other.getX();
        int diffOfY = y - other.getY();
        return diffOfX * diffOfX + diffOfY * diffOfY;
    }

    /** Check if this point equals to the other one. */
    public boolean equals(Point other) {
        return x.equals(other.getX()) && y.equals(other.getY());
    }

    /** Compare two points prioritized by the positions on the X axis. */
    public int compareToByX(Point other) {
        if (x.equals(other.getX()))
            return y.compareTo(other.getY());
        else
            return x.compareTo(other.getX());
    }

    /** Compare two points prioritized by the positions on the Y axis. */
    public int compareToByY(Point other) {
        if (y.equals(other.getY()))
            return x.compareTo(other.getX());
        else
            return y.compareTo(other.getY());
    }

    /** Convert this point into a string representation. */
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

}
