package co.edu.poligran.hooke;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

import java.io.IOException;
import java.text.DecimalFormat;

public class Hooke extends PApplet {

    private static final String TITLE = "Ley de Hulk!";
    private static final int DEFAULT_MASS = 20;
    private static final float DEFAULT_FPS = 20;
    private static final float TAM_BIG_BOX_FACTOR = 0.2f;
    private static final float TAM_BOX_FACTOR = 0.1f;
    private static final float COMPRESSED_SPRING_FACTOR = 0.05f;
    private static final float DEFAULT_CONSTANT = 12;

    private PImage hulk;
    private int textColor;
    private Cosito cosito;
    private Spring spring;
    private float floorHeight;
    private float bigBoxSize;
    private boolean movingBox;
    private float springHeight;
    private float equilibriumPoint;
    private float xt;
    private float vt;
    private boolean hooking;
    private long lastTick;
    public static final String MASS = "mass";
    public static final String CONSTANT = "constant";
    public static final String FPS = "fps";
    private float mass;
    private float fps;
    private float springConstant;
    private DecimalFormat formatter;
    private PFont fontLabel;
    private PFont fontTitle;
    private MediaPlayer smash;

    @Override
    public void setup() {
        Intent intent = getIntent();
        mass = intent.getFloatExtra(MASS, DEFAULT_MASS);
        springConstant = intent.getFloatExtra(CONSTANT, DEFAULT_CONSTANT);
        fps = intent.getFloatExtra(FPS, DEFAULT_FPS);

        try {
            fontLabel = new PFont(getAssets().open("Chalkduster-48.vlw"));
            fontTitle = new PFont(getAssets().open("Chalkduster-54.vlw"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        hulk = loadImage("hulk.png");
        textColor = color(49, 100, 0);

        bigBoxSize = width * TAM_BIG_BOX_FACTOR;
        floorHeight = bigBoxSize / 2;
        springHeight = height - floorHeight - floorHeight / 2;

        // the cosito is created in the center of the free space
        float tamBox = width * TAM_BOX_FACTOR;
        equilibriumPoint = bigBoxSize + ((width - bigBoxSize) / 2) - tamBox;
        cosito = new Cosito(equilibriumPoint, tamBox);
        cosito.setLimitX1(bigBoxSize + COMPRESSED_SPRING_FACTOR * width);
        cosito.setLimitX2(2 * equilibriumPoint - bigBoxSize);

        spring = new Spring(cosito.getTam() / 2, 4);

        formatter = new DecimalFormat("#.#");
        formatter.setMaximumFractionDigits(1);
        smash = MediaPlayer.create(this, co.edu.poligran.hooke.R.raw.smash);
    }

    @Override
    public void draw() {
        long now = System.currentTimeMillis();
        if (hooking && now - lastTick > 1000 / fps) {
            float diff = now - lastTick;
            float dt = diff / 1000.0f;
            vt = vt(dt, vt, xt);
            xt = xt(dt, vt, xt);
            cosito.setX(xt);
            lastTick = now;
        }
        drawBackground();
        drawLabels();
        drawHookeElements();
    }

    private void drawLabels() {
        textFont(fontLabel);
        fill(Color.LTGRAY);
        textAlign(LEFT);
        text("m = " + formatter.format(mass) + "kg", 5, 50);
        text("k = " + formatter.format(springConstant), 330, 50);
        text("x = " + formatter.format(cosito.getX() - equilibriumPoint) + "px", 5, 120);
        text("v = " + formatter.format(hooking ? vt : 0) + "px/s", 5, 190);
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
        textFont(fontTitle);
        strokeWeight(4);
        background(255);
        fill(textColor);
        textAlign(RIGHT);
        text(TITLE, width - 5, height / 2 - floorHeight);
        image(hulk, bigBoxSize / 2 - hulk.width / 2, height - bigBoxSize - hulk.height, hulk.width, hulk.height);
        noStroke();
        fill(Color.GRAY);
        rect(0, height - bigBoxSize, bigBoxSize, height);
        rect(bigBoxSize, height - floorHeight, width - bigBoxSize, height);
        stroke(0, 255, 50);
        line(equilibriumPoint + cosito.getTam() / 2, height - floorHeight + 1, equilibriumPoint + cosito.getTam() / 2, height - floorHeight + 10);
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
            vt = 0;
            xt = cosito.getX();
            hooking = true;
            lastTick = System.currentTimeMillis();
            movingBox = false;
            smash.start();
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

    private float vt(float dt, float vt, float xt) {
        return vt - ((dt * springConstant) / mass) * (xt - equilibriumPoint);
    }

    private float xt(float dt, float vt, float xt) {
        return xt + dt * vt(dt, vt, xt);
    }
}
