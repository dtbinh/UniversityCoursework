package closestPair;

import java.util.*;
import java.io.*;

/** Set of two-dimensional points. */
public class PointSet {

    /** The content of the set. */
    private ArrayList<Point> content;

    /** The index of points for the iterator. */
    private int index = 0;

    /** Constructor. */
    public PointSet() {
        content = new ArrayList<Point>();
    }

    /** Constructor. */
    public PointSet(Point[] points) {
        content = new ArrayList<Point>();
        for (Point p : points)
            content.add(p);
    }

    /** Get the size of the point set. */
    public int size() {
        return content.size();
    }

    /** Add a point to the set. */
    public void addPoint(Point p) {
        content.add(p);
    }

    /** Get the next point with respect to the iterator. */
    public Point nextPoint() {
        if (index < content.size()) {
            Point result = content.get(index);
            index += 1;
            return result;
        } else
            return null;
    }

    /** Reset the position of the iterator. */
    public void resetIter() {
        index = 0;
    }

    /** Convert this point set into a string representation. */
    public String toString() {
        String result = "[";
        for (int i = 0; i < content.size(); i++) {
            if (i != 0)
                result += ", " + content.get(i).toString();
            else
                result += content.get(i).toString();
        }
        result += "]";
        return result;
    }

    /** Read points from a text file. */
    public static PointSet getPoints(String fname) 
            throws UnreadablePointSetException {
        PointSet result = new PointSet();

        try{
            FileInputStream fstream = new FileInputStream(fname);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(in));
            String line;
    
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) continue;
                String[] tokens = line.split("\\ +");
                if (tokens.length == 2) {
                    int x = Integer.parseInt(tokens[0]);
                    int y = Integer.parseInt(tokens[1]);
                    result.addPoint(new Point(x, y));
                } else
                    throw new UnreadablePointSetException();
            }
    
            return result;
        } catch (Exception e) {
            throw new UnreadablePointSetException();
        }
    }

    /** Randomly generate a set of points with respect to the specified number. */
    public static PointSet generatePoints(int n) {
        PointSet result = new PointSet();
        Random randomizer = new Random();

        for (int i = 0; i < n; i++) {
            int x = randomizer.nextInt(10000) - 5000;
            int y = randomizer.nextInt(10000) - 5000;
            Point p = new Point(x, y);
            result.addPoint(p);
        }

        return result;
    }

    /** Sort the point set according to the specified mode ('x' for sorting by the X axis, and 'y' for sorting by the Y axis). */
    public Point[] sort(char c) throws UnknownSortOptionException {
        Point[] result = new Point[content.size()];
        for (int i = 0; i < content.size(); i++)
            result[i] = content.get(i);
        switch (c) {
            case 'x':
                Arrays.sort(result, new PointComparatorByX());
                break;
            case 'y':
                Arrays.sort(result, new PointComparatorByY());
                break;
            default:
                throw new UnknownSortOptionException();
        }
        return result;
    }

    /** Find the square distance of the closest pair of points in the point set with the naive O(n^2) method. WARNING: This method turns very slow when the point set becomes large. */
    public static int naiveClosestPair(PointSet P) 
            throws TrivialClosestPairException 
    {
        ArrayList<Point> content = P.content;

        if (content.size() < 2) 
            throw new TrivialClosestPairException();

        Point p1, p2;

        if (content.size() == 2) {
            p1 = content.get(0);
            p2 = content.get(1);
            return p1.sqrDist(p2);
        }

        int distance = Integer.MAX_VALUE;
        int tempdist;
        for (int i = 0; i < content.size() - 1; i++) {
            for (int j = i + 1; j < content.size(); j++) {
                p1 = content.get(i);
                p2 = content.get(j);
                tempdist = p1.sqrDist(p2);
                if (tempdist < distance)
                    distance = tempdist;
            }
        }

        return distance;
    }

}
