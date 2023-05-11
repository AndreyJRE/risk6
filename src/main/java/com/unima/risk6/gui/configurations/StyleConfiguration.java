package com.unima.risk6.gui.configurations;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.UserDto;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class StyleConfiguration {

  private static final String NORMAL_BUTTON_STYLE = "-fx-background-color: linear-gradient"
      + "(#FFDAB9, #FFA07A)"
      + "; -fx-background-radius: 40; -fx-border-radius: 40;"
      + " -fx-text-fill: #FFFFFF;";
  private static final String HOVER_BUTTON_STYLE = "-fx-background-color: linear-gradient"
      + "(#FFA07A, "
      + "#FFDAB9)"
      + "; -fx-background-radius: 40; -fx-border-radius: 40"
      + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
      + " -fx-text-fill: #FFFFFF;";

  public static void applyButtonStyle(Button button) {
    button.setStyle(NORMAL_BUTTON_STYLE);
    button.hoverProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        button.setStyle(HOVER_BUTTON_STYLE);
      } else {
        button.setStyle(NORMAL_BUTTON_STYLE);
      }
    });
  }

  public static Path generateBackArrow() {
    Path arrow = new Path();
    arrow.getElements().add(new MoveTo(10, 15));
    arrow.getElements().add(new LineTo(30, 0));
    arrow.getElements().add(new MoveTo(30, 30));
    arrow.getElements().add(new LineTo(10, 15));
    arrow.setStrokeWidth(3);
    arrow.setStroke(Color.BLACK);
    arrow.setFill(Color.TRANSPARENT);
    return arrow;
  }

  public static boolean showConfirmationDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Set custom button types
    ButtonType yesButton = new ButtonType("Yes");
    ButtonType noButton = new ButtonType("No");
    alert.getButtonTypes().setAll(noButton, yesButton);

    // Set styles directly in JavaFX
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle("-fx-background-color: linear-gradient(to top, #ffffff, #f2f2f2);"
        + " -fx-border-color: #bbb;"
        + " -fx-border-width: 1;"
        + " -fx-border-style: solid;");
    dialogPane.lookup(".label").setStyle("-fx-font-size: 14;"
        + " -fx-font-weight: bold;"
        + " -fx-text-fill: #444;");

    // Apply styles to buttons
    Button yesButtonNode = (Button) dialogPane.lookupButton(yesButton);
    Button noButtonNode = (Button) dialogPane.lookupButton(noButton);

    for (Button button : new Button[]{yesButtonNode, noButtonNode}) {
      button.setStyle("-fx-background-color: linear-gradient(#FFDAB9, #FFA07A);"
          + " -fx-text-fill: white;"
          + " -fx-background-radius: 5;"
          + " -fx-padding: 5 15 5 15;");

      button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #FFDAB9; "
          + "-fx-text-fill: white;"));
      button.setOnMouseExited(event -> button.setStyle("-fx-background-color: linear-gradient"
          + "(#FFDAB9, #FFA07A); -fx-text-fill: white;"));
      button.setOnMousePressed(event -> button.setStyle("-fx-background-color: #FFA07A; "
          + "-fx-text-fill: white;"));
      button.setOnMouseReleased(event -> button.setStyle("-fx-background-color: linear-gradient"
          + "(#FFDAB9, #FFA07A); -fx-text-fill: white;"));
    }

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == yesButton;
  }

  public static void showErrorDialog(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }


  public void handleUsernameExists(String problem, String title, String body) {
    Alert alert = new Alert(Alert.AlertType.WARNING, problem,
        ButtonType.OK);
    alert.showAndWait();
    if (alert.getResult() == ButtonType.OK) {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(body);
      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        UserDto userDto = GameConfiguration.getMyGameUser();
        userDto.setUsername(result.get());
        LobbyConfiguration.sendJoinServer(userDto);
      }
    }
  }
}
