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
        cosito = new Cosito(bigBoxSize+((width-bigBoxSize)/2)-width*TAM_BOX_FACTOR/2, width*TAM_BOX_FACTOR);
        cosito.setLimitX1(bigBoxSize+COMPRESSED_SPRING_FACTOR*width);
        cosito.setLimitX2(bigBoxSize+STRECHED_SPRING_FACTOR*width);

        spring = new Spring(cosito.getTam()/2, 4);
    }

    @Override
    public void draw() {
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
        movingBox = false;
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
}
