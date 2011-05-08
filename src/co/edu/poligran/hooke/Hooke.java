package co.edu.poligran.hooke;

import android.content.Intent;
import android.graphics.Color;
import processing.core.*;

import java.io.IOException;
import java.text.DecimalFormat;

public class Hooke extends PApplet {

    private static final String TITLE = "Ley de Hulk!";
    private static final int DEFAULT_MASS = 20;
    private static final float DEFAULT_FPS = 20;
    private static final float DEFAULT_CONSTANT = 12;

    private PImage hulk;
    private int textColor;
    public static final float TAM_BIG_BOX_FACTOR = 0.2f;
    private static final float TAM_BOX_FACTOR = 0.1f;
    private static final float COMPRESSED_SPRING_FACTOR = 0.05f;
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
    public static final String MASS = "mass";
    public static final String FPS = "fps";
    public static final String CONSTANT = "constant";
    private float mass;
    private float fps;
    private float springConstant;
    private DecimalFormat formatter;

    @Override
    public void setup() {
        Intent intent = getIntent();
        mass = intent.getFloatExtra(MASS, DEFAULT_MASS);
        fps = intent.getFloatExtra(FPS, DEFAULT_FPS);
        springConstant = intent.getFloatExtra(FPS, DEFAULT_CONSTANT);

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
        cosito = new Cosito(equilibriumPoint, width * TAM_BOX_FACTOR);
        cosito.setLimitX1(bigBoxSize + COMPRESSED_SPRING_FACTOR * width);
        cosito.setLimitX2(2 * equilibriumPoint - bigBoxSize);

        spring = new Spring(cosito.getTam() / 2, 4);

        formatter = new DecimalFormat("#.#");
        formatter.setMaximumFractionDigits(1);
    }

    @Override
    public void draw() {
        long now = System.currentTimeMillis();
        if (hooking && now - lastTick > 1000 / fps) {
            float diff = now - lastTick;
            float dt = diff / 1000.0f;
            vtfutura = vtfutura(dt, vtfutura, xtfutura);
            xtfutura = xtfutura(dt, vtfutura, xtfutura);
            cosito.setX(xtfutura);
            lastTick = now;
        }
        drawBackground();
        drawLabels();
        drawHookeElements();
    }

    private void drawLabels() {
        fill(Color.LTGRAY);
        textAlign(LEFT);
        text("m = " + formatter.format(mass)+"kg", 0, 50);
        text("k = " + formatter.format(springConstant), 0, 100);
        text("x = " + formatter.format(cosito.getX())+"px", 0, 150);
        text("v = " + formatter.format(hooking ? vtfutura : 0), 0, 200);
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
        image(hulk, bigBoxSize / 2 - hulk.width / 2, height - bigBoxSize - hulk.height, hulk.width, hulk.height);
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
            cosito.setX(mouseX - cosito.getTam() / 2);
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

    private float vtfutura(float dt, float vt, float xt) {
        return vt - ((dt * springConstant) / mass) * (xt - equilibriumPoint);
    }

    private float xtfutura(float dt, float vt, float xt) {
        return xt + dt * vtfutura(dt, vt, xt);
    }
}
