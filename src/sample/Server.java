package sample;

import sample.logic.GameControllerImpl;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    static final int PORT = 8080;
    static final String BINDING = "SnakeGame";
    public static void main(String[] args) {
        try {
            final GameControllerImpl service = new GameControllerImpl();
            Remote stub = UnicastRemoteObject.exportObject(service, 0);
            final Registry registry = LocateRegistry.createRegistry(PORT);
            registry.bind(BINDING, stub);
        } catch (Exception e) {
            System.out.println("Something went wrong on server side");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
