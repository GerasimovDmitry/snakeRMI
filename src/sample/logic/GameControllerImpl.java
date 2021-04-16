package sample.logic;

import sample.Models.Coord;
import sample.Models.GameState;
import sample.Models.Player;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class GameControllerImpl implements GameController {

    private static Random rand = new Random();
    @Override
    public GameState getNextState(GameState currentState) throws RemoteException {
        for (int i = currentState.getSnake().size() - 1; i >= 1; i--) {
            currentState.getSnake().get(i).setX(currentState.getSnake().get(i - 1).getX());
            currentState.getSnake().get(i).setY(currentState.getSnake().get(i - 1).getY());
        }
        int y, x;
        y = currentState.getSnake().get(0).getY();
        x = currentState.getSnake().get(0).getX();

        switch (currentState.getDirection()) {
            case up:
                y--;
                currentState.getSnake().get(0).setY(y);
                if (currentState.getSnake().get(0).getY() < 0) {
                    currentState.setGameOver(true); //or set y = fieldHeight
                }
                break;
            case down:
                y++;
                currentState.getSnake().get(0).setY(y);
                if (currentState.getSnake().get(0).getY() > currentState.getFieldHeight()) {
                    currentState.setGameOver(true); //or set y = 0
                }
                break;
            case left:
                x--;
                currentState.getSnake().get(0).setX(x);
                if (currentState.getSnake().get(0).getX() < 0) {
                    currentState.setGameOver(true); //or set x = fieldWidth;
                }
                break;
            case right:
                x++;
                currentState.getSnake().get(0).setX(x);
                if (currentState.getSnake().get(0).getX() > currentState.getFieldWidth()) {
                    currentState.setGameOver(true);  //or set x = 0;
                }
                break;

        }

        // eat food
        if (currentState.getFood().getX() == currentState.getSnake().get(0).getX() &&
                currentState.getFood().getY() == currentState.getSnake().get(0).getY()) {

            ArrayList<Coord> newSnake =  currentState.getSnake();
            newSnake.add(new Coord(-1, -1));
            currentState.setSnake(newSnake);
            currentState.setFood(getFood(currentState.getSnake(), currentState.getFieldWidth(), currentState.getFieldHeight()));
            currentState.setScore(currentState.getScore() + 1);
            currentState.setSpeed(currentState.getSpeed() + currentState.getLvl());
        }

        // self destroy
        for (int i = 1; i < currentState.getSnake().size(); i++) {
            if (currentState.getSnake().get(0).getX() == currentState.getSnake().get(i).getX() && currentState.getSnake().get(0).getY() == currentState.getSnake().get(i).getY()) {
                currentState.setGameOver(true);
            }
        }


        return currentState;
    }

    @Override
    public GameState getInitState(int fieldWidth, int fieldHeight, int lvl) throws RemoteException {
        GameState gameState = new GameState(fieldWidth, fieldHeight, lvl);
        gameState.setFood(getFood(gameState.getSnake(), gameState.getFieldWidth(), gameState.getFieldHeight()));
        return gameState;
    }

    @Override
    public ArrayList<Player> getLeaderBoard() throws RemoteException {
        return null;
    }

    @Override
    public void saveLeaderBoard(ArrayList<Player> players) throws RemoteException {

    }

    private Coord getFood(ArrayList<Coord> snake, int width, int height) {
        Coord food;
        int foodX, foodY;

        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Coord c : snake) {
                if (c.getX() == foodX && c.getY() == foodY) {
                    continue start;
                }
            }
            break;

        }
        food = new Coord(foodX, foodY);
        return food;
    }
}
