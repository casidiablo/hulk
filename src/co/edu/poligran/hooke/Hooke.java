package co.edu.poligran.hooke;

import android.graphics.Color;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

import java.io.IOException;

public class Hooke extends PApplet {

    private static final String TITLE = "Ley de Hulk!";
    private PImage hulk;
    private int textColor;

    public static final float TAM_BIG_BOX_FACTOR = 0.2f;
    private static final float TAM_BOX_FACTOR = 0.1f;
    private static final float COMPRESSED_SPRING_FACTOR = 0.05f;
    private static final float STRECHED_SPRING_FACTOR = 0.5f;// how much of the screen the spring can be streched
    private Cosito cosito;
    private Spring spring;
    private float floorHeight;
    private float bigBoxSize;
    private boolean movingBox;
    private float springHeight;
    private float equilibriumPoint;
    private float xtfutura;
    private float vtfutura;
    private boolean hooking;
    private long lastTick;
    @Override
    public void setup() {
        try {
            PFont font = new PFont(getAssets().open("Chalkduster-48.vlw"));
            textFont(font);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hulk = loadImage("hulk.png");
        textColor = color(49, 100, 0);

        bigBoxSize = width * TAM_BIG_BOX_FACTOR;
        floorHeight = bigBoxSize / 2;
        springHeight = height - floorHeight - floorHeight / 2;

        // the cosito is created in the center of the free space
        equilibriumPoint = bigBoxSize + ((width - bigBoxSize) / 2) - width * TAM_BOX_FACTOR;
        cosito = new Cosito(equilibriumPoint, width*TAM_BOX_FACTOR);
        cosito.setLimitX1(bigBoxSize+COMPRESSED_SPRING_FACTOR*width);
        cosito.setLimitX2(2*equilibriumPoint-bigBoxSize);

        spring = new Spring(cosito.getTam()/2, 4);
    }

    @Override
    public void draw() {
        long now = System.currentTimeMillis();
        if (hooking && now - lastTick > 100) {
            float diff = now - lastTick;
            float dt = diff /1000.0f;
            vtfutura = vtfutura(dt, vtfutura, xtfutura);
            xtfutura = xtfutura(dt, vtfutura, xtfutura);
            cosito.setX(xtfutura);
            lastTick = now;
        }
        drawBackground();
        drawHookeElements();
    }

    private void drawHookeElements() {
        stroke(0, 0, 255);
        fill(Color.LTGRAY);
        rect(cosito.getX(), height - cosito.getTam() - floorHeight, cosito.getTam(), cosito.getTam());
        strokeWeight(2);
        noFill();
        PVector[] springPoints = spring.getPoints(bigBoxSize, springHeight, cosito.getX(), springHeight);
        beginShape();
        for (PVector p : springPoints) {
            vertex(p.x, p.y);
        }
        endShape();
    }

    private void drawBackground() {
        strokeWeight(4);
        background(255);
        fill(textColor);
        textAlign(CENTER);
        text(TITLE, width / 2, 50);
        image(hulk, bigBoxSize/2-hulk.width/2, height - bigBoxSize - hulk.height, hulk.width, hulk.height);
        noStroke();
        fill(Color.GRAY);
        rect(0, height - bigBoxSize, bigBoxSize, height);
        rect(bigBoxSize, height - floorHeight, width - bigBoxSize, height);
    }

    @Override
    public void mousePressed() {
        super.mousePressed();
        if (mouseX > cosito.getX() && mouseX < cosito.getX() + cosito.getTam() &&
                mouseY > height - cosito.getTam() - floorHeight && mouseY < height - floorHeight) {
            hooking = false;
            movingBox = true;
        }
    }

    @Override
    public void mouseDragged() {
        if (movingBox) {
            cosito.setX(mouseX-cosito.getTam()/2);
        }
    }

    @Override
    public void mouseReleased() {
        super.mouseReleased();
        if (movingBox) {
            vtfutura = 0;
            xtfutura = cosito.getX();
            hooking = true;
            lastTick = System.currentTimeMillis();
            movingBox = false;
        }
    }

    @Override
    public int sketchWidth() {
        return screenWidth;
    }

    @Override
    public int sketchHeight() {
        return screenHeight;
    }

    @Override
    public String sketchRenderer() {
        return A2D;
    }

    private static final float m = 10;
    private static final float k = 12;

    private float vtfutura(float dt, float vt, float xt) {
        return vt - ((dt * k) / m) * (xt - equilibriumPoint);
    }

    private float xtfutura(float dt, float vt, float xt) {
        return xt + dt*vtfutura(dt, vt, xt);
    }
}
