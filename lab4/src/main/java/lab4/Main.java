package lab4;

import java.util.Random;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        int winX = -900;
        int winY = 350;
        int pointN = 20;
        int range = 50;
        int time = 1000;

        Coordinate2D[] points = new Coordinate2D[pointN];
        Random r = new Random();
        for (int i = 0; i < points.length; i++) {
            points[i] = new Coordinate2D(range * r.nextDouble(), range * r.nextDouble());
        }

        ConvexHull con = new ConvexHull();
        for (Coordinate2D point : points) {
            con.insert(point);
            con.showPlot(winX, winY);
            Thread.sleep(time);
        }

        for (Coordinate2D point : points) {
            con.delete(point);
            con.showPlot(winX, winY);
            Thread.sleep(time);
        }

        System.out.println("Completed.");
    }
}
