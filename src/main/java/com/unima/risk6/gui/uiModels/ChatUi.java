package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.ChatObserver;
import java.util.ArrayList;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class ChatUi extends BorderPane implements ChatObserver {

  private Popup chatPopup;
  private HBox sendMessageBox;
  private BorderPane gameRoot;
  private HBox titleBox;
  private Label chatLabel;
  private Button closeButton;
  private StackPane stackPane;
  private ScrollPane wholeChat;
  private VBox chatBox;
  private Text text;
  private TextField sendMessage;
  private Rectangle chatRectangle;
  private Button sendButton;
  private ImageView sendIcon;
  private double centerY;
  private double centerX;
  private double popupHeight;
  private double popupWidth;

  private Bounds rootBounds;

  private Scene gameScene;

  //TODO LEFT Align Messages
  public ChatUi(Scene gameScene) {

    this.gameScene = gameScene;

    gameRoot = (BorderPane) gameScene.getRoot();
        /*
        this.setPrefSize(gameRoot.getWidth() * 0.70, gameRoot.getHeight() * 0.70);
        rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
        centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
        centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
        popupWidth = this.getPrefWidth();
        popupHeight = this.getPrefHeight();
         */

    titleBox = new HBox();
    chatLabel = new Label("Chat");
    chatLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold");
    chatLabel.setPadding(new Insets(10, 0, 0, 20));
    closeButton = new Button();
    closeButton.setPrefSize(20, 20);
    ImageView closeIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeIcon.setFitWidth(30);
    closeIcon.setFitHeight(30);
    closeButton.setGraphic(closeIcon);
    closeButton.setStyle("-fx-background-radius: 15px;");
    closeButton.setFocusTraversable(false);
    stackPane = new StackPane(chatLabel);
    HBox.setHgrow(stackPane, Priority.ALWAYS);
    titleBox.getChildren().addAll(stackPane, closeButton);
    this.setTop(titleBox);

    wholeChat = new ScrollPane();

    chatBox = new VBox();
    chatBox.setAlignment(Pos.TOP_LEFT);

    VBox messageBox = new VBox();
    messageBox.setPadding(new Insets(10, 0, 0, 20));
    messageBox.setSpacing(15);
    messageBox.setAlignment(Pos.TOP_LEFT);

        /*
        userBox = new VBox();
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

        text = new Text("Your text here");
        double chatWidth = text.getLayoutBounds().getWidth() + 20;
        double chatHeight = text.getLayoutBounds().getHeight() + 20;

        chatRectangle = new Rectangle(chatWidth, chatHeight);
        chatRectangle.setArcHeight(10);
        chatRectangle.setArcWidth(10);
        chatRectangle.setFill(Color.LIGHTGREY);
        StackPane textStack = new StackPane(chatRectangle, text);

         */
    text = new Text("Your ingame messages");
    double chatWidth = text.getLayoutBounds().getWidth() + 20;
    double chatHeight = text.getLayoutBounds().getHeight() + 20;

    chatRectangle = new Rectangle(chatWidth, chatHeight);
    chatRectangle.setArcHeight(10);
    chatRectangle.setArcWidth(10);
    chatRectangle.setFill(Color.LIGHTGREY);
    StackPane textStack = new StackPane(chatRectangle, text);
    messageBox.getChildren().addAll(textStack);

    chatBox.getChildren().add(messageBox);

    wholeChat.setContent(chatBox);

    this.setCenter(wholeChat);

    sendMessage = new TextField();
    sendMessage.setStyle("-fx-font-size: 15; -fx-background-radius: 20; -fx-border-radius: 20;");
    sendMessage.setPadding(new Insets(15));
    sendMessage.setFocusTraversable(false);
    sendMessage.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {

        String message = sendMessage.getText();
        sendMessage.clear();
        LobbyConfiguration.sendChatMessage(message);
        //chatArea.appendText(user.getUsername() + ": " + message + "\n");
        event.consume();
      }
    });

    sendButton = new Button();
    sendButton.setPrefSize(20, 20);
    sendIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/sendIcon.png").toString()));
    sendIcon.setFitWidth(30);
    sendIcon.setFitHeight(30);
    sendButton.setGraphic(sendIcon);
    sendButton.setStyle("-fx-background-radius: 15px;");
    sendButton.setFocusTraversable(false);
    sendButton.setOnAction(event -> {
      String message = sendMessage.getText();
      sendMessage.clear();
      LobbyConfiguration.sendChatMessage(message);
      //chatArea.appendText(user.getUsername() + ": " + message + "\n");
      event.consume();
    });

    sendMessageBox = new HBox();
    sendMessageBox.setAlignment(Pos.CENTER);
    sendMessageBox.setSpacing(20);
    sendMessageBox.getChildren().addAll(sendMessage, sendButton);

    this.setBottom(sendMessageBox);

//        VBox chatBox = new VBox();
//        chatBox.getChildren().addAll(chatLabel);
//
//        chatBoxPane.setCenter(chatBox);

    this.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
    chatPopup = new Popup();
    closeButton.setOnAction(event -> chatPopup.hide());
    chatPopup.getContent().add(this);
    chatPopup.setAutoFix(true);
    chatPopup.setAutoHide(true);
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    this.setEffect(dropShadow);
        /*
        chatPopup.setX(centerX - popupWidth / 2);
        chatPopup.setY(centerY - popupHeight / 2);
        chatPopup.show(gameScene.getWindow());*/
    titleBox.setOnMousePressed(event -> {
      rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
      event.consume();
    });
    titleBox.setOnMouseDragged(event -> {
      chatPopup.setX(event.getScreenX()/* - (rootBounds.getWidth() - popupWidth) / 2*/);
      chatPopup.setY(event.getScreenY() - 20);
      event.consume();
    });

    // Add mouse pressed and drag events to wholeChat for resizing the popup
    wholeChat.setOnMousePressed(event -> {
      rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
      popupWidth = chatPopup.getWidth();
      popupHeight = chatPopup.getHeight();
      event.consume();
    });
    wholeChat.setOnMouseDragged(event -> {
      double width = event.getScreenX() - rootBounds.getMinX() - chatPopup.getX();
      double height = event.getScreenY() - rootBounds.getMinY() - chatPopup.getY() - 20;
      System.out.println("Dragged");
      if (width > 0) {
        chatPopup.setWidth(width);
      }
      if (height > 0) {
        chatPopup.setHeight(height);
      }
      event.consume();
    });

    LobbyConfiguration.addChatObserver(this);
  }

  public void show() {
    this.setPrefSize(gameRoot.getWidth() * 0.25, gameRoot.getHeight() * 0.80);
    rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
    centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
    popupWidth = this.getPrefWidth();
    popupHeight = this.getPrefHeight();
    chatPopup.setX(rootBounds.getMinX() + rootBounds.getWidth());
    chatPopup.setY(centerY - popupHeight / 2);
    sendMessage.setPromptText("Write your text message here");
    sendMessage.setPrefSize(this.getPrefWidth() * 0.8, 40);
    chatPopup.show(gameScene.getWindow());
  }

  @Override
  public void updateChat(ArrayList<String> messages) {
    Platform.runLater(() -> {
      text = new Text(messages.get(messages.size() - 1));
      double chatWidth = text.getLayoutBounds().getWidth() + 20;
      double chatHeight = text.getLayoutBounds().getHeight() + 20;

      chatRectangle = new Rectangle(chatWidth, chatHeight);
      chatRectangle.setArcHeight(10);
      chatRectangle.setArcWidth(10);
      chatRectangle.setFill(Color.LIGHTGREY);
      StackPane textStack = new StackPane(chatRectangle, text);
      //StackPane.setAlignment(text, Pos.TOP_LEFT);
      //StackPane.setMargin(text, new Insets(0, 0, 0, 10));

      VBox messageBox = new VBox();
      messageBox.setPadding(new Insets(10, 0, 0, 20));
      messageBox.setSpacing(15);
      messageBox.setAlignment(Pos.TOP_LEFT);
      messageBox.getChildren().addAll(textStack);

      chatBox.getChildren().add(messageBox);
    });
  }

  public Popup getChatPopup() {
    return chatPopup;
  }

  public VBox getChatBox() {
    return chatBox;
  }
}
