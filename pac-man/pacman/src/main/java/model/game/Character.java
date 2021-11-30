package model.game;

public abstract class Character {

    private double posX;
    private double posY;
    private Direction direction;

    public Character(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void moveRight() {
        posX += getSpeed();
    }

    public void moveDown() {
        posY += getSpeed();
    }

    public void moveLeft() {
        posX -= getSpeed();
    }

    public void moveUp() {
        posY -= getSpeed();
    }

    abstract protected double getSpeed();
}
