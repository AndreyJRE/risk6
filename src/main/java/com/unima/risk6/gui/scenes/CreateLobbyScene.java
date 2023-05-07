package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.CreateLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class CreateLobbyScene extends Scene implements InitializableScene {

    private CreateLobbySceneController createLobbySceneController;

    public CreateLobbyScene() {
        super(new BorderPane());
    }

    @Override
    public void init() {
        if (createLobbySceneController != null) {
            createLobbySceneController.init();
        }
    }

    public void setController(CreateLobbySceneController createLobbySceneController) {
        this.createLobbySceneController = createLobbySceneController;
    }

}

