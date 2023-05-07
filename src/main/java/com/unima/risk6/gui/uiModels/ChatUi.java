package com.unima.risk6.gui.uiModels;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class ChatUi extends BorderPane {
    public ChatUi(Scene gameScene) {

        BorderPane gameRoot = (BorderPane) gameScene.getRoot();
        this.setPrefSize(gameRoot.getWidth() * 0.70, gameRoot.getHeight() * 0.70);
        Bounds rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
        double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
        double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
        double popupWidth = this.getPrefWidth();
        double popupHeight = this.getPrefHeight();

        HBox titleBox = new HBox();
        Label chatLabel = new Label("Chat");
        chatLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold");
        chatLabel.setPadding(new Insets(10, 0, 0, 20));
        Button closeButton = new Button();
        closeButton.setPrefSize(20, 20);
        ImageView closeIcon = new ImageView(new Image(
                getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
        closeIcon.setFitWidth(30);
        closeIcon.setFitHeight(30);
        closeButton.setGraphic(closeIcon);
        closeButton.setStyle("-fx-background-radius: 15px;");
        closeButton.setFocusTraversable(false);
        StackPane stackPane = new StackPane(chatLabel);
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        titleBox.getChildren().addAll(stackPane, closeButton);
        this.setTop(titleBox);

        ScrollPane wholeChat = new ScrollPane();

        VBox chatBox = new VBox();

        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(10, 0, 0, 20));
        messageBox.setSpacing(15);


        VBox userBox = new VBox();
        userBox.setAlignment(Pos.CENTER);
        userBox.setSpacing(5);
        Label userLabel = new Label("Username");
        Circle userCircle = new Circle(30);
        userCircle.setStroke(Color.BLACK);
        //TODO: CHANGE USERICON AGAINST REAL USER IMAGE
        ImageView userIcon = new ImageView(new Image(
                getClass().getResource("/com/unima/risk6/pictures/playerIcon.png").toString()));
        userCircle.setFill(new ImagePattern(userIcon.getImage()));
        userBox.getChildren().addAll(userLabel, userCircle);

        Text text = new Text("Your text here");
        double width = text.getLayoutBounds().getWidth() + 20;
        double height = text.getLayoutBounds().getHeight() + 20;

        Rectangle chatRectangle = new Rectangle(width, height);
        chatRectangle.setArcHeight(10);
        chatRectangle.setArcWidth(10);
        chatRectangle.setFill(Color.LIGHTGREY);
        StackPane textStack = new StackPane(chatRectangle, text);

        messageBox.getChildren().addAll(userBox, textStack);

        chatBox.getChildren().add(messageBox);

        wholeChat.setContent(chatBox);

        this.setCenter(wholeChat);

        TextField sendMessage = new TextField();
        sendMessage.setPromptText("Write your text message here");
        sendMessage.setPrefSize(this.getPrefWidth() * 0.3, 40);
        sendMessage.setStyle("-fx-font-size: 15; -fx-background-radius: 20; -fx-border-radius: 20;");
        sendMessage.setPadding(new Insets(15));
        sendMessage.setFocusTraversable(false);

        Button sendButton = new Button();
        sendButton.setPrefSize(20, 20);
        ImageView sendIcon = new ImageView(new Image(
                getClass().getResource("/com/unima/risk6/pictures/sendIcon.png").toString()));
        sendIcon.setFitWidth(30);
        sendIcon.setFitHeight(30);
        sendButton.setGraphic(sendIcon);
        sendButton.setStyle("-fx-background-radius: 15px;");
        sendButton.setFocusTraversable(false);

        HBox sendMessageBox = new HBox();
        sendMessageBox.setAlignment(Pos.CENTER);
        sendMessageBox.setSpacing(20);
        sendMessageBox.getChildren().addAll(sendMessage, sendButton);

        this.setBottom(sendMessageBox);


//        VBox chatBox = new VBox();
//        chatBox.getChildren().addAll(chatLabel);
//
//        chatBoxPane.setCenter(chatBox);

        this.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

        Popup chatPopup = new Popup();
        closeButton.setOnAction(event -> chatPopup.hide());
        chatPopup.getContent().add(this);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        dropShadow.setRadius(10);
        this.setEffect(dropShadow);

        chatPopup.setX(centerX - popupWidth / 2);
        chatPopup.setY(centerY - popupHeight / 2);
        chatPopup.show(gameScene.getWindow());
    }

    public void addMessage(String message) {
        //TODO: IMPLEMENT GETTING MESSAGE

    }
    //TODO IMPLEMENT SENDING MESSAGE
    //extract String from sendMessage and add to Pane
}
