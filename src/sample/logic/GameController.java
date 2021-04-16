package sample.logic;

import sample.Models.GameState;
import sample.Models.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface GameController  extends Remote {
    GameState getNextState(GameState currentState) throws RemoteException;
    GameState getInitState(int fieldWidth, int fieldHeight, int lvl) throws RemoteException;
    ArrayList<Player> getLeaderBoard() throws RemoteException;
    void saveLeaderBoard(ArrayList<Player> players) throws RemoteException;
}
