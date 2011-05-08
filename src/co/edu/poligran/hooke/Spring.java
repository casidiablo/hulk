package co.edu.poligran.hooke;

import processing.core.PVector;

public class Spring {
    private PVector[] points;
    private float ringHeight;

    public Spring(float ringHeight, int springRings) {
        if (springRings < 1) {
            throw new RuntimeException("Rings must be greater than 1");
        }
        this.ringHeight = ringHeight;
        points = new PVector[springRings*4+3];
        for (int i = 0; i < points.length; i++) {
            points[i] = new PVector();
        }
    }

    public PVector[] getPoints(float x1, float y1, float x2, float y2) {
        points[0].x = x1;
        points[0].y = y1;
        boolean picUp = true;
        for (int i = 1; i < points.length - 1; i++) {
            points[i].x = x1 + i * (x2 - x1) / points.length;
            float down = y1 + ringHeight / 2;
            float up = y1 - ringHeight / 2;
            if (i == 1 || points[i - 1].y == down || points[i - 1].y == up) {
                points[i].y = y1;
            } else if (picUp) {
                points[i].y = up;
                picUp = false;
            } else {
                points[i].y = down;
                picUp = true;
            }
        }
        points[points.length-1].x = x2;
        points[points.length-1].y = y2;
        return points;
    }
}
