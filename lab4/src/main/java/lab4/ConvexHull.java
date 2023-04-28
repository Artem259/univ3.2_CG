package lab4;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;


public class ConvexHull extends ConvexHullHalf {
    protected final ConvexHullHalf topHullHalf;
    protected final ConvexHullHalf bottomHullHalf;
    protected final ArrayList<Coordinate2D> points;
    protected final JFrame plotFrame;

    public ConvexHull() {
        topHullHalf = new ConvexHullHalf();
        bottomHullHalf = new ConvexHullHalf();
        points = new ArrayList<>();
        plotFrame = new JFrame();
        plotFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void insert(Coordinate2D e) {
        points.add(e);
        topHullHalf.insert(e);
        bottomHullHalf.insert(new Coordinate2D(e.x, -e.y));
    }

    @Override
    public void delete(Coordinate2D e) {
        points.remove(e);
        topHullHalf.delete(e);
        bottomHullHalf.delete(new Coordinate2D(e.x, -e.y));
    }

    @Override
    public ArrayList<Coordinate2D> getHull() {
        ArrayList<Coordinate2D> topHull = topHullHalf.getHull();
        ArrayList<Coordinate2D> bottomHull = bottomHullHalf.getHull();
        bottomHull.replaceAll(coord -> new Coordinate2D(coord.x, -coord.y));
        Collections.reverse(bottomHull);
        if (!bottomHull.isEmpty()) {
            bottomHull.remove(0);
        }
        ArrayList<Coordinate2D> hull = new ArrayList<>(Stream.concat(topHull.stream(), bottomHull.stream()).toList());
        if (hull.isEmpty()) {
            return new ArrayList<>(points);
        }
        return hull;
    }

    public void showPlot(int xPos, int yPos) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Coordinate2D point : points) {
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
        }
        int sizeX = (int) ((maxX - minX) * Plot.sizeFactor() + 2 * Plot.sizeFactor());
        int sizeY = (int) ((maxY - minY) * Plot.sizeFactor() + 2 * Plot.sizeFactor());
        plotFrame.getContentPane().removeAll();
        plotFrame.getContentPane().repaint();
        plotFrame.getContentPane().setPreferredSize(new Dimension(sizeX, sizeY));
        if (!points.isEmpty()) {
            plotFrame.setLocation(xPos + ((int) (minX*Plot.sizeFactor())), yPos + ((int) (minY*Plot.sizeFactor())));
        } else {
            plotFrame.setLocation(xPos, yPos);
        }
        ArrayList<Coordinate2D> currentHull = getHull();
        plotFrame.add(new Plot(points, currentHull, minX, minY));
        plotFrame.getContentPane().validate();
        plotFrame.getContentPane().repaint();
        plotFrame.pack();
        plotFrame.setVisible(true);
    }
}
