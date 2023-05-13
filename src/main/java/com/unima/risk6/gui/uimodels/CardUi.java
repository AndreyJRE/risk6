package com.unima.risk6.gui.uimodels;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

/**
 * Represents a graphical user interface (UI) representation of a card.
 *
 * @author mmeider
 */

public class CardUi extends StackPane {

  private Card card;

  private Rectangle cardFrame;

  private ImageView cardSymbolImage;

  private boolean clicked = false;

  /**
   * Creates the UI representation of a card associated with a specific country.
   *
   * @param card      The card object.
   * @param countryUi The UI representation of the associated country.
   */

  public CardUi(Card card, CountryUi countryUi) {
    super();
    this.card = card;
    cardFrame = new Rectangle(200, 400);
    cardFrame.setFill(Color.WHITE);
    cardFrame.setStroke(Color.BLACK);
    cardFrame.setArcHeight(25);
    cardFrame.setArcWidth(25);
    setImageByCardSymbol(card);
    Label chatLabel = new Label(
        countryUi.getCountry().getCountryName().name().replaceAll("_", " "));
    chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold");
    SVGPath countryPath = countryUi.svgPathClone(countryUi.getCountryPath());
    VBox cardVbox = new VBox();
    cardVbox.setSpacing(10);
    cardVbox.getChildren().addAll(chatLabel, countryPath, cardSymbolImage);
    cardVbox.setAlignment(Pos.CENTER);
    this.getChildren().addAll(cardFrame, cardVbox);
  }

  /**
   * Creates a UI representation of a card without an associated country.
   *
   * @param card The card object.
   */

  public CardUi(Card card) {
    super();
    this.card = card;
    cardFrame = new Rectangle(100, 185);
    cardFrame.setArcHeight(25);
    cardFrame.setArcWidth(25);
    setImageByCardSymbol(card);
    this.getChildren().addAll(cardFrame, cardSymbolImage);
  }

  /**
   * Sets the image of the card based on the card symbol.
   *
   * @param card The card object.
   */

  private void setImageByCardSymbol(Card card) {
    switch (card.getCardSymbol()) {
      case CANNON -> cardSymbolImage = new ImageView(
          ImageConfiguration.getImageByName(ImageName.CANNON_ICON));
      case INFANTRY -> cardSymbolImage = new ImageView(
          ImageConfiguration.getImageByName(ImageName.INFANTRY_ICON));
      case CAVALRY -> cardSymbolImage = new ImageView(
          ImageConfiguration.getImageByName(ImageName.CAVALRY_ICON));
      case WILDCARD -> cardSymbolImage = new ImageView(
          ImageConfiguration.getImageByName(ImageName.WILDCARD_ICON));
      default -> {
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardUi cardUi = (CardUi) o;
    return Objects.equals(card, cardUi.card);
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public boolean isClicked() {
    return clicked;
  }

  @Override
  public int hashCode() {
    return Objects.hash(card);
  }

  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

  public Rectangle getCardFrame() {
    return cardFrame;
  }
}
