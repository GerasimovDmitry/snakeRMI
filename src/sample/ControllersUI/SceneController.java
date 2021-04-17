package sample.ControllersUI;

import javafx.scene.*;

import java.util.HashMap;



public class SceneController {

    private HashMap<String, Parent> screenMap = new HashMap<>();

    private Scene mainScene;

    public SceneController(Scene main) {
        this.mainScene = main;
    }

    public void addScreen(String name, Parent node) {
        screenMap.put(name, node);
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
    }

    public void activateScreen(String name) {
        mainScene.setRoot(screenMap.get(name));
    }

    public Parent getScreen(String name) {
        return screenMap.get(name);
    }
}