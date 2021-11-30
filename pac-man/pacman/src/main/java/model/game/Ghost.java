package model.game;

public class Ghost extends Character {

    private static double speed = 0.75;

    public Ghost(double posX, double posY) {
        super(posX, posY);
    }

    public void setStationary() {
        speed = 0;
    }

    public void setFleeing() {
        speed = 0.5;
    }

    public void setChasing() {
        speed = 0.75;
    }

    @Override
    protected double getSpeed() {
        return speed;
    }
}
