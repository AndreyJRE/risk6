package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.gui.controllers.GameSceneController;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class CardUi extends StackPane {

  private Card card;

  private Rectangle cardFrame;

  private ImageView cardSymbolImage;

  private List<CardUi> cardUis = new ArrayList<>();

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
        cardSymbolImage = new ImageView(
            new Image(
                getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
      }
      case INFANTRY -> {
        cardSymbolImage = new ImageView(
            new Image(
                getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
      }
      case CAVALRY -> {
        cardSymbolImage = new ImageView(
            new Image(
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
    this.initMouseListener();
  }

  public CardUi(Card card) {
    super();
    this.card = card;
    cardFrame = new Rectangle(100, 185);
    cardFrame.setArcHeight(25);
    cardFrame.setArcWidth(25);
    switch (card.getCardSymbol()) {
      case CANNON -> {
        cardSymbolImage = new ImageView(
            new Image(
                getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
      }
      case INFANTRY -> {
        cardSymbolImage = new ImageView(
            new Image(
                getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
      }
      case CAVALRY -> {
        cardSymbolImage = new ImageView(
            new Image(
                getClass().getResource("/com/unima/risk6/pictures/cavalryCard.png").toString()));
      }
      case WILDCARD -> {
        cardSymbolImage = new ImageView(
            new Image(getClass().getResource("/com/unima/risk6/pictures/wildCard.png").toString()));
      }
    }
    this.getChildren().addAll(cardFrame, cardSymbolImage);
    this.initMouseListener();
  }

  private void initMouseListener() {
    setOnMouseEntered(event -> {
      this.setCursor(Cursor.HAND);
    });
    setOnMouseClicked(event -> {
      if (!this.clicked) {
        this.clicked = true;
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.DARKRED);
        this.cardFrame.setEffect(dropShadow);
        checkAmountOfSelectedCards();
      } else {
        this.cardFrame.setEffect(null);
        this.clicked = false;
        checkAmountOfSelectedCards();
      }
    });
  }

  private void checkAmountOfSelectedCards() {
    HBox cardBox = (HBox) this.getParent();
    HBox newBottomBox = new HBox();
    newBottomBox.setAlignment(Pos.CENTER);
    BorderPane.setMargin(newBottomBox, new Insets(10));
    BorderPane cardPane = (BorderPane) this.getParent().getParent();
    HandController handController = GameSceneController.getPlayerController().getHandController();
    int counter = 0;
    for (Node cardNode : cardBox.getChildren()) {
      if (((CardUi) cardNode).isClicked()) {
        handController.selectCardThroughCard(((CardUi) cardNode).getCard());
      }
    }
    cardPane.setBottom(newBottomBox);

    List<Card> selectedCards = handController.getHand().getSelectedCards();
    if (handController.isExchangeable(selectedCards)) {
      Button handInButton = new Button("Hand in the cards!");
      handInButton.setStyle(
          "-fx-background-radius: 15px; -fx-font-size: 14; -fx-font-weight: bold;");
      handInButton.setFocusTraversable(false);
      handInButton.setOnMouseClicked(event -> handleHandInButton());
      newBottomBox.getChildren().clear();
      newBottomBox.getChildren().add(handInButton);

    } else if (selectedCards.size() > 3) {
      Label removeCardLabel = new Label("You selected too many cards! Only three are allowed!");
      removeCardLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red; -fx-font-weight: bold;");
      newBottomBox.getChildren().clear();
      newBottomBox.getChildren().add(removeCardLabel);
    }
  }

  private void handleHandInButton() {
    HBox cardBox = (HBox) this.getParent();
    cardBox.getChildren().clear();
    GameSceneController.getPlayerController().sendHandIn();
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
}
