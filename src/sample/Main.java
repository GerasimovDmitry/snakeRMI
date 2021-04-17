package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.ControllersUI.SceneController;
import sample.Models.Direction;
import sample.Models.GameState;
import sample.Models.Player;
import sample.logic.GameController;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Main extends Application {

    private double animationSpeed = 1.1;
    private Direction direction = Direction.left;
    private volatile Boolean isGameExit = false;
    public int fieldWidth = 35;
    public int fieldHeight = 20;
    public int speed = 1;
    public int lvl = 1;

    public GameState currentGameState;

    public SceneController sceneController;
    public GameController gameManager;



    public Stage nameSetterWindow = new Stage();
    public Stage errorWindow = new Stage();
    public Stage helpWindow = new Stage();
    public Stage primaryStage;


    public Scene errorScene;
    public Scene nameSetterScene;
    public Scene helpScene;
    public Scene primaryScene;

    private Parent startPageView;
    private Parent leaderBoardView;
    private Parent gameView;
    private Parent helpView;
    private Parent lvlView;
    private Parent nameAndScoreView;
    private Parent errorView;

    private AnimationTimer animationTimer;

    public Button exitButton;
    public Button gameStartButton;
    public Button leaderboardButton;
    public Button lvlButton;
    public Button lvlBackButton;
    public Button finishGameButton;
    public Canvas gameCanvas;
    //public ImageView helpButton;
    public Label scoreLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryScene = new Scene(new Pane(), 800, 600);
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setTitle("Java RMI & Javafx Snake");

        sceneController = new SceneController(this.primaryScene);

        this.loadStartView();
        this.loadNameSetterView();
        this.loadErrorView();
        this.loadHelpView();

        sceneController.addScreen("startPageView", startPageView);

        sceneController.activateScreen("startPageView");

        setUpElements(startPageView);
        initPopovers();
        this.primaryStage.show();
        initRMI();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initRMI() {
        try {
            this.gameManager = ClientRMI.getGameManager();
        } catch (Exception e) {
            this.openErrorPage("Connection refused");
            e.printStackTrace();
        }
    }

    private void setUpElements(Parent scene) {
        this.exitButton = (Button) scene.lookup("#exitButton");
        this.leaderboardButton = (Button) scene.lookup("#leaderboardButton");
        this.gameStartButton = (Button) scene.lookup("#gameStartButton");
        //this.helpButton = (ImageView) scene.lookup("#helpButton");
        this.lvlButton = (Button) scene.lookup("#lvlButton");

        this.exitButton.setOnAction(e -> this.onExitClick());
        this.leaderboardButton.setOnAction(e -> this.openLeaderboard());
        this.gameStartButton.setOnAction(e -> this.onStartGameClick());
        this.lvlButton.setOnAction(e -> this.onSelectLvlClick());
        //this.helpButton.setOnMouseClicked(e -> this.onHelpClick());
    }

    private void loadStartView() throws Exception {
        startPageView = FXMLLoader.load(getClass().getResource("pages/menu.fxml"));
        sceneController.addScreen("startPageView", startPageView);
    }

    private void loadLeaderboardView() {
        try {
            leaderBoardView = FXMLLoader.load(getClass().getResource("pages/leaderBoard.fxml"));
            sceneController.addScreen("leaderBoardView", leaderBoardView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSelectLvlView() {
        try {
            lvlView = FXMLLoader.load(getClass().getResource("pages/level.fxml"));
            sceneController.addScreen("lvlView", lvlView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadHelpView() {
        try {
            helpView = FXMLLoader.load(getClass().getResource("pages/about.fxml"));
            sceneController.addScreen("helpView", helpView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGameView() {
        try {
            gameView = FXMLLoader.load(getClass().getResource("pages/game.fxml"));
            sceneController.addScreen("gameView", gameView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNameSetterView() {
        try {
            nameAndScoreView = FXMLLoader.load(getClass().getResource("pages/addLeader.fxml"));
            sceneController.addScreen("nameAndScoreView", nameAndScoreView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadErrorView() {
        try {
            errorView = FXMLLoader.load(getClass().getResource("pages/error.fxml"));
            sceneController.addScreen("errorView", errorView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initPopovers() {
        this.errorScene = new Scene(this.errorView);
        this.nameSetterScene = new Scene(this.nameAndScoreView);
        this.helpScene = new Scene(this.helpView);

        helpWindow.setTitle("Справка");
        helpWindow.setScene(helpScene);
        helpWindow.initModality(Modality.WINDOW_MODAL);
        helpWindow.initOwner(primaryStage);

        nameSetterWindow.setScene(nameSetterScene);
        nameSetterWindow.initModality(Modality.APPLICATION_MODAL);
        nameSetterWindow.initOwner(primaryStage);

        errorWindow.setTitle("");
        errorWindow.setScene(errorScene);
        errorWindow.initModality(Modality.WINDOW_MODAL);
        errorWindow.initOwner(primaryStage);
    }

    public void gameTick(GraphicsContext graphicsContext2D) {

        try {
            if (currentGameState == null) {
                currentGameState = gameManager.getInitState(fieldWidth, fieldHeight, lvl);
            } else {
                currentGameState = gameManager.getNextState(currentGameState);
            }

            scoreLabel.setText(String.valueOf(currentGameState.getScore()));
            System.out.println(currentGameState + ": " + currentGameState.isGameOver() + currentGameState.getScore());

            if (currentGameState.isGameOver()) {
                isGameExit = true;
                return;
            }

            var snake = currentGameState.getSnake();
            var foodCoordinate = currentGameState.getFood();

            graphicsContext2D.setFill(Color.web("e4c9f0"));
            graphicsContext2D.fillRect(0, 0, currentGameState.getFieldWidth() * 20, currentGameState.getFieldHeight() * 20);

            Color foodColor = Color.web("c94908");
/*            for (int i = 0; i < currentGameState.getFieldWidth() - 1; i++) {
                for (int j = 0; j < currentGameState.getFieldHeight()- 1; j++) {
                    graphicsContext2D.setFill(Color.web("bbf2e7"));
                    graphicsContext2D.setStroke(Color.web("bbf2e7"));
                    graphicsContext2D.strokeRect(i * 20, j *20, (i+1) * 20, (j+1) * 20);
                }
            }*/
            graphicsContext2D.setFill(foodColor);
            graphicsContext2D.fillOval(foodCoordinate.getX() * 20, foodCoordinate.getY() * 20, 20, 20);


            graphicsContext2D.setFill(Color.web("ab22a4"));
            graphicsContext2D.fillRect(snake.get(0).getX() * 20, snake.get(0).getY() * 20, 20 - 1, 20 - 1);
            graphicsContext2D.setFill(Color.web("ab22a4"));
            graphicsContext2D.fillRect(snake.get(0).getX() * 20, snake.get(0).getY() * 20, 20 - 2, 20 - 2);
            for (int i = 1; i <= snake.size() - 1; i++) {
                graphicsContext2D.setFill(Color.web("ed9ae9"));
                graphicsContext2D.fillRect(snake.get(i).getX() * 20, snake.get(i).getY() * 20, 20 - 1, 20 - 1);
                graphicsContext2D.setFill(Color.web("ed9ae9"));
                graphicsContext2D.fillRect(snake.get(i).getX() * 20, snake.get(i).getY() * 20, 20 - 2, 20 - 2);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            this.animationTimer.stop();
        }
    }

    // Actions block

    public void onExitClick() {
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }
    }

    public void openLeaderboard() {
        if (this.leaderBoardView == null) {
            this.loadLeaderboardView();
        }
        if (this.leaderBoardView != null && this.primaryStage != null) {
            sceneController.activateScreen("leaderBoardView");

            var backButton = (Button) this.leaderBoardView.lookup("#backButton");
            var borderPane = (BorderPane) this.leaderBoardView.lookup("#borderPane");

            if (backButton == null) return;

            backButton.setOnAction(e -> {
                sceneController.activateScreen("startPageView");
            });

            try {
                var list = FXCollections.observableArrayList(gameManager.getLeaderBoard());
                TableView<Player> tableView = new TableView<>();

                TableColumn<Player, String> name = new TableColumn<>("Игрок");
                name.setMinWidth(75);
                name.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<Player, Integer> score = new TableColumn<>("Счет");
                score.setMinWidth(75);
                score.setCellValueFactory(new PropertyValueFactory<>("score"));

                tableView.setItems(list);
                tableView.getColumns().addAll(name, score);

                borderPane.setCenter(tableView);

            } catch (RemoteException e) {
                this.openErrorPage(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void onSelectLvlClick() {
        if (this.lvlView == null) {
            this.loadSelectLvlView();
        }
        if (this.lvlView != null && this.primaryStage != null) {
            sceneController.activateScreen("lvlView");
            if (currentGameState == null) {
                try {
                    currentGameState = gameManager.getInitState(fieldWidth, fieldHeight, lvl);
                } catch (RemoteException e) {
                    this.openErrorPage(e.getMessage());
                    e.printStackTrace();
                }
            }

            var easyLvlButton = (Button) this.lvlView.lookup("#easyLvlButton");
            var mediumLvlButton = (Button) this.lvlView.lookup("#mediumLvlButton");
            var hardLvlButton = (Button) this.lvlView.lookup("#hardLvlButton");
            var lvlBackButton = (Button) this.lvlView.lookup("#lvlBackButton");

            easyLvlButton.setOnAction(actionEvent -> {
                this.currentGameState.setLvl(1);
                lvl = 1;
                sceneController.activateScreen("startPageView");
            });

            mediumLvlButton.setOnAction(actionEvent -> {
                this.currentGameState.setLvl(2);
                lvl = 2;
                sceneController.activateScreen("startPageView");
            });

            hardLvlButton.setOnAction(actionEvent -> {
                this.currentGameState.setLvl(3);
                lvl = 3;
                sceneController.activateScreen("startPageView");
            });

            lvlBackButton.setOnAction(e -> {
                sceneController.activateScreen("startPageView");
            });
        }


    }

    public void onStartGameClick() {
        if (this.gameView == null) {
            this.loadGameView();
        }
        if (this.gameView != null && this.primaryStage != null) {
            sceneController.activateScreen("gameView");

            var finishGameButton = (Button) this.gameView.lookup("#finishGameButton");
            var gameGridCanvas = (Canvas) this.gameView.lookup("#gameCanvas");
            scoreLabel = (Label) this.gameView.lookup("#scoreLabel");

            finishGameButton.setOnAction(actionEvent -> {
                if (isGameExit) {
                    sceneController.activateScreen("startPageView");
                }
                isGameExit = true;
            });

            this.gameView.setOnKeyPressed(keyEvent -> this.onKeyPressed(keyEvent.getCode()));
            this.gameView.setOnKeyReleased(keyEvent -> this.onKeyPressed(keyEvent.getCode()));

            try {
                currentGameState = gameManager.getInitState(fieldWidth, fieldHeight, lvl);
                scoreLabel.setText(String.valueOf(currentGameState.getScore()));

                gameGridCanvas.setHeight(20 * currentGameState.getFieldHeight());
                gameGridCanvas.setWidth(20 * currentGameState.getFieldWidth());

                GraphicsContext graphicsContext2D = gameGridCanvas.getGraphicsContext2D();

                animationTimer = new AnimationTimer() {
                    long lastTick = 0;
                    final long second = 1_000_000_000;

                    public void handle(long now) {
                        if (lastTick == 0) {
                            lastTick = now;
                            gameTick(graphicsContext2D);
                            return;
                        }

                        if (now - lastTick > second / (currentGameState.getSpeed() * animationSpeed)) {
                            lastTick = now;
                            gameTick(graphicsContext2D);
                        }

                        if (isGameExit) {
                            animationTimer.stop();
                            gameOver();
                        }
                    }
                };

                animationTimer.start();

            } catch (RemoteException e) {
                this.openErrorPage(null);
                e.printStackTrace();
            }
        }
    }

    public void onHelpClick() {
        if (this.helpView == null) {
            this.loadHelpView();
        }
        if (this.helpView != null && this.primaryStage != null) {

            Stage helpWindow = new Stage();
            helpWindow.setTitle("Справка");
            helpWindow.setScene(new Scene(this.helpView));

            helpWindow.initModality(Modality.WINDOW_MODAL);
            helpWindow.initOwner(primaryStage);

            helpWindow.show();

            Button backClick = (Button) this.helpView.lookup("#backToMenuButton");

            if (backClick == null) return;

            backClick.setCancelButton(true);
            backClick.setOnAction(e -> helpWindow.close());
        }
    }

    public void onKeyPressed(KeyCode keyCode) {
        System.out.println(keyCode);
        if (keyCode == KeyCode.W || keyCode == KeyCode.UP) {
            if (direction != Direction.down) {
                direction = Direction.up;
            }
        } else if (keyCode == KeyCode.A || keyCode == KeyCode.LEFT) {
            if (direction != Direction.right) {
                direction = Direction.left;
            }
        } else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
            if (direction != Direction.up) {
                direction = Direction.down;
            }
        } else if (keyCode == KeyCode.D || keyCode == KeyCode.RIGHT) {
            if (direction != Direction.left) {
                direction = Direction.right;
            }
        }
        currentGameState.setDirection(direction);

    }

    public void openErrorPage(String message) {

        var backClick = (Button) this.errorView.lookup("#closeButton");
        var label = (Label) this.errorView.lookup("#errorLabel");

        if (message != null) {
            label.setText(message);
        }
        backClick.setOnAction(e -> errorWindow.close());

        errorWindow.show();
    }

    private void gameOver() {
        isGameExit = false;
        if (currentGameState.getScore() != 0) {
            var player = new Player("unknown", currentGameState.getScore());
            nameSetterWindow.setTitle("Счет: " + player.getScore());

            nameSetterWindow.show();

            var backClick = (Button) this.nameAndScoreView.lookup("#okButton");
            var textField = (TextField) this.nameAndScoreView.lookup("#nameField");

            textField.setText(player.getName());

            if (backClick == null) return;

            backClick.setOnAction(e -> {
                player.setName(textField.getText());
                try {
                    ArrayList<Player> players = this.gameManager.getLeaderBoard();
                    players.add(player);
                    this.gameManager.saveLeaderBoard(players);
                    System.out.println("Leaderboard saved");
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                    this.openErrorPage(exception.getMessage());
                }
                nameSetterWindow.close();
                openLeaderboard();
            });
        } else {
            sceneController.activateScreen("startPageView");
        }
    }
}