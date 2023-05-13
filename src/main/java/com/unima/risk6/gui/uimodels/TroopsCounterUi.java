package com.unima.risk6.gui.uimodels;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * Represents a graphical user interface (UI) component for displaying troop counters, used by
 * CountryUi.
 *
 * @author mmeider
 */

public class TroopsCounterUi extends Group {

  private double ellipseSize = 10;

  private Ellipse ellipseCounter;

  private Text text;


  /**
   * Constructs a TroopsCounterUi object, which consists of an ellipse and text with the specified
   * coordinates for the center of the ellipse.
   *
   * @param ellipseX The x-coordinate of the center of the ellipse.
   * @param ellipseY The y-coordinate of the center of the ellipse.
   */

  public TroopsCounterUi(double ellipseX, double ellipseY) {
    super();
    this.ellipseCounter = new Ellipse(ellipseX, ellipseY, ellipseSize, ellipseSize);

    ellipseCounter.setStroke(Color.BLACK);
    ellipseCounter.setFill(Color.WHITE);

    text = new Text("0");
    text.setFont(new Font("Arial", ellipseSize));
    text.setFill(Color.BLACK);
    text.setX(ellipseX - text.getLayoutBounds().getWidth() * 0.5);
    text.setY(ellipseY + text.getLayoutBounds().getHeight() * 0.3);

    this.getChildren().add(ellipseCounter);

    this.getChildren().add(text);
    this.setMouseTransparent(true);
  }

  /**
   * Updates the troop count displayed on the counter.
   *
   * @param troops The new troop count.
   */
  public void update(int troops) {
    this.setText(String.valueOf(troops));
  }


  /**
   * Sets the text to be displayed on the counter.
   *
   * @param counterText The text to be displayed on the counter.
   */
  public void setText(String counterText) {
    this.text.setText(counterText);
    this.text.setX(this.getEllipseCounter().getCenterX() - text.getLayoutBounds().getWidth() * 0.5);
    this.text.setY(
        this.getEllipseCounter().getCenterY() + text.getLayoutBounds().getHeight() * 0.3);
  }

  public Ellipse getEllipseCounter() {
    return ellipseCounter;
  }
}