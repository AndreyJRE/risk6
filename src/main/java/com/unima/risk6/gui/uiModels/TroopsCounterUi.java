package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Country;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class TroopsCounterUi extends Group {

  private double ellipseSize = 10;

  private Ellipse ellipseCounter;


  public TroopsCounterUi(double ellipseX, double ellipseY) {
    super();
    this.ellipseCounter = new Ellipse(ellipseX, ellipseY, ellipseSize, ellipseSize);

    ellipseCounter.setStroke(Color.BLACK);
    ellipseCounter.setFill(Color.WHITE);

    Text text = new Text(Integer.toString((int) (Math.random() * 20)));
    text.setFont(new Font("Arial", ellipseSize));
    text.setFill(Color.BLACK);
    text.setX(ellipseX - text.getLayoutBounds().getWidth() * 0.5);
    text.setY(ellipseY + text.getLayoutBounds().getHeight() * 0.3);

    this.getChildren().add(ellipseCounter);

    this.getChildren().add(text);
  }

  public Ellipse getEllipseCounter() {
    return ellipseCounter;
  }
}
