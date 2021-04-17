package sample;

import sample.logic.GameController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static final String GAME_MANAGER_PATH_NAME = "SnakeGame";
    public static final int PORT = 8080;

    public static GameController getGameManager() throws RemoteException, NotBoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            return (GameController)registry.lookup(GAME_MANAGER_PATH_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
