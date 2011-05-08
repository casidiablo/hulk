package co.edu.poligran.hooke;

public class Cosito {
    private float x;
    private float tam;
    private float limitx1;
    private float limitx2;

    public Cosito(float x, float tam) {
        this.x = x;
        this.tam = tam;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (x > limitx1 && x < limitx2) {
            this.x = x;
        }
    }

    public float getTam() {
        return tam;
    }

    public void setTam(float tam) {
        this.tam = tam;
    }

    public void setLimitX1(float limitx1) {
        this.limitx1 = limitx1;
    }

    public void setLimitX2(float limitx2) {
        this.limitx2 = limitx2;
    }
}