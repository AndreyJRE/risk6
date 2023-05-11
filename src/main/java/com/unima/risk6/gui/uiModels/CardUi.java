package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Card;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class CardUi extends StackPane {

  private Card card;

  private Rectangle cardFrame;

  private ImageView cardSymbolImage;

  private boolean clicked = false;


  public CardUi(Card card, CountryUi countryUi) {
    super();
    this.card = card;
    cardFrame = new Rectangle(250, 450);
    cardFrame.setFill(Color.WHITE);
    cardFrame.setStroke(Color.BLACK);
    cardFrame.setArcHeight(25);
    cardFrame.setArcWidth(25);
    switch (card.getCardSymbol()) {
      case CANNON -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
      }
      case INFANTRY -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
      }
      case CAVALRY -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/cavalryCard.png").toString()));
      }
      case WILDCARD -> {
        cardSymbolImage = new ImageView(
            new Image(getClass().getResource("/com/unima/risk6/pictures/wildCard.png").toString()));
      }
    }
    Label chatLabel = new Label(
        countryUi.getCountry().getCountryName().name().replaceAll("_", " "));
    chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold");
    SVGPath countryPath = countryUi.svgPathClone(countryUi.getCountryPath());
    VBox cardVBox = new VBox();
    cardVBox.setSpacing(10);
    cardVBox.getChildren().addAll(chatLabel, countryPath, cardSymbolImage);
    cardVBox.setAlignment(Pos.CENTER);
    this.getChildren().addAll(cardFrame, cardVBox);
  }

  public CardUi(Card card) {
    super();
    this.card = card;
    cardFrame = new Rectangle(100, 185);
    cardFrame.setArcHeight(25);
    cardFrame.setArcWidth(25);
    switch (card.getCardSymbol()) {
      case CANNON -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
      }
      case INFANTRY -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
      }
      case CAVALRY -> {
        cardSymbolImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/cavalryCard.png").toString()));
      }
      case WILDCARD -> {
        cardSymbolImage = new ImageView(
            new Image(getClass().getResource("/com/unima/risk6/pictures/wildCard.png").toString()));
      }
    }
    this.getChildren().addAll(cardFrame, cardSymbolImage);
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
