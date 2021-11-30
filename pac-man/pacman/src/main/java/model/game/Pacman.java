package model.game;

public class Pacman extends Character {

    private int lives;

    public Pacman(int lives, int posX, int posY) {
        super(posX, posY);
        this.lives = lives;
    }

    @Override
    protected double getSpeed() {
        return 1.25;
    }
}
