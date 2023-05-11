package com.unima.risk6.gui.uiModels;


import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.scenes.GameScene;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javafx.geometry.Bounds;
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
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class HandUi extends BorderPane {

  private Bounds bounds;
  private Popup popup;

  private Hand hand;

  private final Stack<CardUi> cardUis;

  private BorderPane gameRoot;

  private final HBox cardBox;

  private final List<CardUi> selectedCardsUi;

  private final Set<CountryUi> countryUis;

  public HandUi(GameScene gameScene, Set<CountryUi> countryUis) {
    super();
    this.gameRoot = (BorderPane) gameScene.getRoot();
    this.countryUis = countryUis;
    cardBox = new HBox();
    popup = new Popup();
    cardUis = new Stack<>();
    selectedCardsUi = new ArrayList<>();
    Button closeCardsButton = new Button();
    closeCardsButton.setPrefSize(20, 20);
    ImageView closeCardIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeCardIcon.setFitWidth(20);
    closeCardIcon.setFitHeight(20);
    closeCardsButton.setGraphic(closeCardIcon);
    closeCardsButton.setStyle("-fx-background-radius: 15px;");
    closeCardsButton.setFocusTraversable(false);

    this.setTop(closeCardsButton);
    BorderPane.setAlignment(closeCardsButton, Pos.TOP_RIGHT);

    cardBox.setSpacing(30);
    cardBox.setAlignment(Pos.CENTER);

    this.setCenter(cardBox);

    this.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

    popup.getContent().add(this);

    closeCardsButton.setOnAction(event -> {
      popup.hide();
      GameSceneController.getPlayerController().getHandController().deselectAllCards();
      cardUis.forEach(x -> x.setClicked(false));
      cardBox.getChildren().clear();
    });

    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    this.setEffect(dropShadow);
  }

  public void show() {
    this.setPrefSize(gameRoot.getWidth() * 0.65, gameRoot.getHeight() * 0.65);
    Bounds rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
    double popupWidth = this.getPrefWidth();
    double popupHeight = this.getPrefHeight();
    popup.setX(centerX - popupWidth / 2);
    popup.setY(centerY - popupHeight / 2);
    checkAmountOfSelectedCards();
    cardBox.getChildren().addAll(cardUis);
    popup.show(gameRoot.getScene().getWindow());
  }

  private void initMouseListenerCard(CardUi cardUi) {
    cardUi.setOnMouseEntered(event -> {
      cardUi.setCursor(Cursor.HAND);
    });
    cardUi.setOnMouseClicked(event -> {
      if (!cardUi.isClicked()) {
        if (hand.getSelectedCards().size() < 3) {
          cardUi.setClicked(true);
          DropShadow dropShadow = new DropShadow();
          dropShadow.setColor(Color.DARKRED);
          cardUi.getCardFrame().setEffect(dropShadow);
          checkAmountOfSelectedCards();
        }
      } else {
        cardUi.getCardFrame().setEffect(null);
        cardUi.setClicked(false);
        checkAmountOfSelectedCards();
      }
    });
  }

  private void handleHandInButton() {
    cardBox.getChildren().removeAll(selectedCardsUi);
    cardUis.removeAll(selectedCardsUi);
    PlayerController playerController = GameSceneController.getPlayerController();
    playerController.sendHandIn();
    popup.hide();
    GameSceneController.getPlayerController().getHandController().deselectAllCards();
    cardUis.forEach(x -> x.setClicked(false));
    cardBox.getChildren().clear();

  }

  private void checkAmountOfSelectedCards() {
    HBox newBottomBox = new HBox();
    newBottomBox.setAlignment(Pos.CENTER);
    BorderPane.setMargin(newBottomBox, new Insets(10));
    HandController handController = GameSceneController.getPlayerController().getHandController();
    for (Node cardNode : cardBox.getChildren()) {
      if (((CardUi) cardNode).isClicked()) {
        handController.selectCardThroughCard(((CardUi) cardNode).getCard());
      } else {
        handController.deselectCardsThroughCard(((CardUi) cardNode).getCard());
      }
    }
    selectedCardsUi.forEach(x -> System.out.println(x.getCard()));
    this.setBottom(newBottomBox);
    if (handController.isExchangeable(handController.getHand().getSelectedCards())) {
      Button handInButton = new Button("Hand in the cards!");
      handInButton.setStyle(
          "-fx-background-radius: 15px; -fx-font-size: 14; -fx-font-weight: bold;");
      handInButton.setFocusTraversable(false);
      handInButton.setOnMouseClicked(event -> handleHandInButton());
      newBottomBox.getChildren().clear();
      newBottomBox.getChildren().add(handInButton);

    } //TODO der fall wird nie auftreten, da ich in Logic mache,
    // dass er nicht mehr als 3 selecten kann.
    //TODO check through the Anzahl an card UI, die glowhaben ode clicked sind.
    else if (handController.getHand().getSelectedCards().size() > 3) {
      Label removeCardLabel = new Label("You selected too many cards! Only three are allowed!");
      removeCardLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red; -fx-font-weight: bold;");
      newBottomBox.getChildren().clear();
      newBottomBox.getChildren().add(removeCardLabel);
    }


  }

  public void setHand(Hand hand) {
    this.hand = hand;
    cardUis.clear();
    for (Card card : hand.getCards()) {
      CardUi cardUi;
      if (card.hasCountry()) {
        cardUi = new CardUi(card, getCountryUiByCountryName(card.getCountry()));
      } else {
        cardUi = new CardUi(card);
      }
      initMouseListenerCard(cardUi);
      cardUis.push(cardUi);
    }
  }

  public CountryUi getCountryUiByCountryName(CountryName countryName) {
    return countryUis.stream()
        .filter(countryUi -> countryUi.getCountry().getCountryName().equals(countryName))
        .findFirst().get();
  }
}
