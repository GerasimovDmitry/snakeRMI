package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Models.Coord;
import sample.Models.Direction;
import sample.Models.GameState;
import sample.Models.Player;
import sample.logic.GameController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Main extends Application {
    private static BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void start(Stage primaryStage) throws Exception{

        final Registry registry;
        GameController service;
        BufferedReader consoleReader;


        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        try {
            consoleReader = new BufferedReader(
                    new InputStreamReader(System.in));
            registry = LocateRegistry.getRegistry(Server.PORT);
            service = (GameController) registry.lookup(Server.BINDING);
            GameState gameState = service.getInitState(40,40,1);
            printSnake(gameState.getSnake());
            System.out.println("MOVE");
            gameState = service.getNextState(gameState);
            printSnake(gameState.getSnake());

            ArrayList<Player> players = new ArrayList<>();
            players.add(new Player("Bob", 1));
            players.add(new Player("Tom", 2));
            players.add(new Player("Tom", 2));

            ArrayList<Player> players2 = new ArrayList<>();
            players2 = service.getLeaderBoard();
            service.saveLeaderBoard(players);
            players2 = service.getLeaderBoard();
            printPlayers(players2);

            consoleReader.close();
        } catch (Exception e) {
            System.out.println("Something went wrong on client side");
            System.out.println(e.getMessage());
        }

    }


    public static void main(String[] args) {
        launch(args);
    }

    private static void printSnake(ArrayList<Coord> snake) {
        System.out.print("PRINT");
        for (int i = 0; i <= snake.size()-1; i++) {
            System.out.println(i);
            System.out.print("coordX: ");
            System.out.println(snake.get(i).getX());
            System.out.print("coordY: ");
            System.out.println(snake.get(i).getY());
        }
        System.out.println();
    }

    private static void printPlayers(ArrayList<Player> players) {
        System.out.println("players");
        for (Player player: players) {
            System.out.println(player.getName() + " " + player.getScore());
        }
    }
}
