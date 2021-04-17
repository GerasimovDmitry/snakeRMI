package sample.logic;

import sample.Models.Coord;
import sample.Models.GameState;
import sample.Models.Player;
import sample.ModelsUI.StepParams;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class GameControllerImpl implements GameController {

    private static Random rand = new Random();
    @Override
    public GameState getNextState(GameState currentState) throws RemoteException {

        if (currentState.isExit) {
            currentState.setGameOver(true);
            return currentState;
        }


        int y, x;
        y = currentState.getSnake().get(0).getY();
        x = currentState.getSnake().get(0).getX();
        for (int i = currentState.getSnake().size() - 1; i >= 1; i--) {
            currentState.getSnake().get(i).setX(currentState.getSnake().get(i - 1).getX());
            currentState.getSnake().get(i).setY(currentState.getSnake().get(i - 1).getY());
        }

        switch (currentState.getDirection()) {
            case up:
                if (!(currentState.getSnake().get(1).getX() == x && y == currentState.getSnake().get(1).getY() - 1)) {
                    y--;
                }
                else {
                    y++;
                }
                currentState.getSnake().get(0).setY(y);
                if (currentState.getSnake().get(0).getY() < 0) {
                    if (currentState.getLvl() == 3) {
                        currentState.setGameOver(true); //or set y = fieldHeight
                    }
                    else {
                        y = currentState.getFieldHeight();
                        currentState.getSnake().get(0).setY(y);
                    }
                }
                break;
            case down:
                if (!(currentState.getSnake().get(1).getX() == x && y == currentState.getSnake().get(1).getY() + 1)) {
                    y++;
                }
                else {
                    y--;
                }
                currentState.getSnake().get(0).setY(y);
                if (currentState.getSnake().get(0).getY() >= currentState.getFieldHeight()) {
                    if (currentState.getLvl() == 3) {
                        currentState.setGameOver(true); //or set y = 0
                    }
                    else {
                        y = 0;
                        currentState.getSnake().get(0).setY(y);
                    }
                }
                break;
            case left:
                if (!(currentState.getSnake().get(1).getX() - 1 == x && y == currentState.getSnake().get(1).getY())) {
                    x--;
                }
                else {
                    x++;
                }
                currentState.getSnake().get(0).setX(x);
                if (currentState.getSnake().get(0).getX() < 0) {
                    if (currentState.getLvl() == 3) {
                        currentState.setGameOver(true); //or set x = fieldWidth;
                    }
                    else {
                        x = currentState.getFieldWidth();
                        currentState.getSnake().get(0).setX(x);
                    }
                }
                break;
            case right:
                if (!(currentState.getSnake().get(1).getX() + 1 == x && y == currentState.getSnake().get(1).getY())) {
                    x++;
                }
                else {
                    x--;
                }
                currentState.getSnake().get(0).setX(x);
                if (currentState.getSnake().get(0).getX() >= currentState.getFieldWidth()) {
                    if (currentState.getLvl() == 3) {
                        currentState.setGameOver(true);  //or set x = 0;
                    }
                    else {
                        x = 0;
                        currentState.getSnake().get(0).setX(x);
                    }
                }
                break;
        }
        // eat food
        if (currentState.getFood().getX() == currentState.getSnake().get(0).getX() &&
                currentState.getFood().getY() == currentState.getSnake().get(0).getY()) {

            ArrayList<Coord> newSnake =  currentState.getSnake();
            for (int i = 0; i < currentState.getLvl() * 2 - 1; i++) {
                int blockX = newSnake.get(newSnake.size() - 1).getX();
                int blockY = newSnake.get(newSnake.size() - 1).getY();
                switch (currentState.getDirection()) {
                    case up:
                        blockY++;
                        break;
                    case down:
                        blockY--;
                        break;
                    case left:
                        blockX++;
                        break;
                    case right:
                        blockX--;
                        break;

                }
                newSnake.add(new Coord(blockX, blockY));
            }
            currentState.setSnake(newSnake);
            currentState.setFood(getFood(currentState.getSnake(), currentState.getFieldWidth(), currentState.getFieldHeight()));
            currentState.setScore(currentState.getScore() + currentState.getLvl());
            currentState.setSpeed(currentState.getSpeed() + currentState.getLvl());
        }

        // self destroy
        for (int i = 1; i <= currentState.getSnake().size() - 1; i++) {
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
        String filename = "leaderboard.bin";
        ArrayList<Player> players = new ArrayList<Player>();
        File leaderboard = new File(filename);
        if (!leaderboard.exists()) {
            try {
                leaderboard.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename)))
        {

            players = (ArrayList<Player>)ois.readObject();
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
        return players;
    }

    @Override
    public void saveLeaderBoard(ArrayList<Player> players) throws RemoteException {
        String filename = "leaderboard.bin";

        File leaderboard = new File(filename);
        if (leaderboard.exists()) {
            leaderboard.delete();
        }

        try {
            leaderboard.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))
        {
            oos.writeObject(players);
            System.out.println("File has been written");
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
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
