package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Card;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class CardUi extends StackPane {

    private Card card;

    private Rectangle cardFrame;

    private ImageView cardSymbolImage;

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
                        new Image(getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
            }
            case INFANTRY -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
            }
            case CAVALRY -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/cavalryCard.png").toString()));
            }
            case WILDCARD -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/wildCard.png").toString()));
            }
        }
        Label chatLabel = new Label(countryUi.getCountry().getCountryName().name());
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
        cardFrame = new Rectangle(125, 225);
        cardFrame.setArcHeight(25);
        cardFrame.setArcWidth(25);
        switch (card.getCardSymbol()) {
            case CANNON -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/cannonCard.png").toString()));
            }
            case INFANTRY -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/infantryCard.png").toString()));
            }
            case CAVALRY -> {
                cardSymbolImage = new ImageView(
                        new Image(getClass().getResource("/com/unima/risk6/pictures/cavalryCard.png").toString()));
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
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.DARKRED);
            this.cardFrame.setEffect(dropShadow);
        });
        setOnMouseClicked(event -> {
            System.out.println("clicked");
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.DARKRED);
            this.cardFrame.setEffect(dropShadow);
        });
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
