package com.unima.risk6.gui.uiModels;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class TimeUi extends Group {

  public TimeUi(double rectangleWidth, double rectangleHeight) {

    Image reinforcementImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/sandUhr.png").toString());
    ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);

    Rectangle icon1 = new Rectangle(rectangleWidth, rectangleHeight);
    icon1.setFill(reinforcementImagePattern);

    StackPane iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 60, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(50);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);

    iconsPane.getChildren().addAll(icon1);
    StackPane.setAlignment(icon1, Pos.CENTER);

    getChildren().addAll(iconsPane);
  }
}
