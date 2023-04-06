package com.unima.risk6.gui.scenes;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class ActivePlayerUi extends Group {

  private Ellipse ellipse;
  private Rectangle rectangle;

  public ActivePlayerUi(double radiusX, double radiusY,
      double rectangleWidth, double rectangleHeight) {

    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ellipse.setFill(Color.WHITE);
    ellipse.setStroke(Color.BLACK);

    rectangle = new Rectangle(rectangleWidth, rectangleHeight);
    rectangle.setFill(Color.WHITE);
    rectangle.setStroke(Color.BLACK);
    rectangle.setArcWidth(rectangleHeight);
    rectangle.setArcHeight(rectangleHeight);
    rectangle.setLayoutX(0);
    rectangle.setLayoutY(0 - rectangleHeight / 2);

    StackPane iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 60, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(50);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);

    Image reinforcementImage = new Image(
        getClass().getResource("/pictures/reinforcement.png").toString());
    ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);

    Image attackImage = new Image(getClass().getResource("/pictures/sword.png").toString());
    ImagePattern attackImagePattern = new ImagePattern(attackImage);

    Image fortifyImage = new Image(getClass().getResource("/pictures/fortify.png").toString());
    ImagePattern fortifyImagePattern = new ImagePattern(fortifyImage);

    Rectangle icon1 = new Rectangle(radiusX, radiusY);
    icon1.setFill(reinforcementImagePattern);
    Rectangle icon2 = new Rectangle(radiusX, radiusY);
    icon2.setFill(attackImagePattern);
    Rectangle icon3 = new Rectangle(radiusX, radiusY);
    icon3.setFill(fortifyImagePattern);

    iconsPane.getChildren().addAll(icon1, icon2, icon3);
    StackPane.setAlignment(icon1, Pos.CENTER_LEFT);
    StackPane.setAlignment(icon2, Pos.CENTER);
    StackPane.setAlignment(icon3, Pos.CENTER_RIGHT);

    getChildren().addAll(rectangle, ellipse, iconsPane);
  }

}
