package closestPair;

import java.util.*;

public class PointComparatorByX implements Comparator<Point> {

    public int compare(Point p1, Point p2) {
        return p1.compareToByX(p2);
    }

}
