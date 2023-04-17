package com.unima.risk6.gui.scenes;

import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.controllers.SelectedUserSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SelectedUserScene extends Scene implements InitializableScene{
  private SelectedUserSceneController selectedUserSceneController;
  private User user;
  public SelectedUserScene() {
    super(new BorderPane());
  }

  @Override
  public void init(){
      if(selectedUserSceneController != null){
        selectedUserSceneController.init(user);
      }
  }

  public void setController(
      SelectedUserSceneController selectedUserSceneController) {
    this.selectedUserSceneController = selectedUserSceneController;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
