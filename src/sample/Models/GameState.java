package sample.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    public boolean isExit;
    private ArrayList<Coord> snake;
    private Coord food;
    private Direction direction;
    private int fieldWidth;
    private int fieldHeight;
    private boolean isGameOver;
    private int score;
    private int lvl;
    private int speed;

    public GameState(int fieldWidth, int fieldHeight, int lvl) {
        this.direction = Direction.right;
        this.food = new Coord(1, 1);
        this.snake = new ArrayList<>();
        this.snake.add(new Coord(10, 10));
        this.snake.add(new Coord(9, 10));
        this.snake.add(new Coord(8,10));
        this.score = 0;
        this.speed = 5;
        this.lvl = lvl;
        this.isGameOver = false;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    public ArrayList<Coord> getSnake() {
        return snake;
    }

    public void setSnake(ArrayList<Coord> snake) {
        this.snake = snake;
    }

    public Coord getFood() {
        return food;
    }

    public void setFood(Coord food) {
        this.food = food;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
