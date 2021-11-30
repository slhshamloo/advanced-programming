package model.user;

public class User {
    private final String username;
    private String password;
    private int highScore;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }
}
