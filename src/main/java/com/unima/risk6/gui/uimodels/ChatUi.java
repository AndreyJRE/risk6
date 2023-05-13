package com.unima.risk6.gui.uimodels;

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

/**
 * Represents graphical user interface (UI) of a chat component used in-game for communication
 * between players. The chat UI displays chat messages and handles user input.
 *
 * @author astoyano
 * @author mmeider
 * @author jferch
 */

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

  /**
   * Constructs a ChatUi object with the provided gameScene.
   *
   * @param gameScene the Scene object representing the game scene.
   */

  public ChatUi(Scene gameScene) {

    this.gameScene = gameScene;

    gameRoot = (BorderPane) gameScene.getRoot();

    titleBox = new HBox();
    chatLabel = new Label("Chat");
    chatLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold");
    chatLabel.setPadding(new Insets(10, 0, 0, 20));
    closeButton = new Button();
    closeButton.setPrefSize(15, 15);
    ImageView closeIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeIcon.setFitWidth(30);
    closeIcon.setFitHeight(30);
    closeButton.setGraphic(closeIcon);
    closeButton.setStyle("-fx-background-radius: 15px;");
    closeButton.setFocusTraversable(false);
    closeButton.setStyle("-fx-background-color: transparent;");
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

    text = new Text("Your in-game messages");
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

        String message = sendMessage.getText().trim();
        sendMessage.clear();
        if (message.length() > 0) {
          LobbyConfiguration.sendChatMessage(message);
          event.consume();
        }
      }
    });

    sendButton = new Button();
    sendButton.setPrefSize(20, 20);
    sendButton.setStyle("-fx-background-color: transparent;");
    sendIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/sendIcon.png").toString()));
    sendIcon.setFitWidth(30);
    sendIcon.setFitHeight(30);
    sendButton.setGraphic(sendIcon);
    sendButton.setFocusTraversable(false);
    sendButton.setOnAction(event -> {
      String message = sendMessage.getText();
      sendMessage.clear();
      LobbyConfiguration.sendChatMessage(message);
      event.consume();
    });

    sendMessageBox = new HBox();
    sendMessageBox.setAlignment(Pos.CENTER);
    sendMessageBox.setSpacing(15);
    sendMessageBox.getChildren().addAll(sendMessage, sendButton);
    sendMessageBox.setPadding(new Insets(2));

    this.setBottom(sendMessageBox);

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
    titleBox.setOnMousePressed(event -> {
      rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
      event.consume();
    });
    titleBox.setOnMouseDragged(event -> {
      chatPopup.setX(event.getScreenX());
      chatPopup.setY(event.getScreenY() - 20);
      event.consume();
    });

    wholeChat.setOnMousePressed(event -> {
      rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
      popupWidth = chatPopup.getWidth();
      popupHeight = chatPopup.getHeight();
      event.consume();
    });
    wholeChat.setOnMouseDragged(event -> {
      double width = event.getScreenX() - rootBounds.getMinX() - chatPopup.getX();
      double height = event.getScreenY() - rootBounds.getMinY() - chatPopup.getY() - 20;
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

  /**
   * Shows the chat UI by setting its size, position, and other properties. The chat UI is displayed
   * as a popup window on top of the gameScene.
   */

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
    sendMessage.setPadding(new Insets(3));
    sendMessage.setPrefSize(this.getPrefWidth() * 0.8, 35);
    chatPopup.show(gameScene.getWindow());
  }

  /**
   * Updates the chat UI with the given list of messages. It dynamically adds new chat messages to
   * the chatBox in the UI.
   *
   * @param messages the list of messages to update the chat UI with.
   */

  @Override
  public void updateChat(ArrayList<String> messages) {
    Platform.runLater(() -> {
      double maxWidthOfChat = this.getPrefWidth() - 40;
      text = new Text(messages.get(messages.size() - 1));
      text.setWrappingWidth(maxWidthOfChat - 10);
      double chatHeight = text.getLayoutBounds().getHeight() + 10;
      chatRectangle = new Rectangle(text.getLayoutBounds().getWidth() + 15, chatHeight);
      chatRectangle.setArcHeight(10);
      chatRectangle.setArcWidth(10);
      chatRectangle.setFill(Color.LIGHTGREY);
      StackPane.setAlignment(text, Pos.TOP_LEFT);
      StackPane.setAlignment(chatRectangle, Pos.TOP_LEFT);
      StackPane.setMargin(text, new Insets(4, 0, 0, 8));
      StackPane textStack = new StackPane(chatRectangle, text);
      VBox messageBox = new VBox();
      messageBox.getChildren().addAll(textStack);
      messageBox.setPadding(new Insets(5, 0, 0, 15));
      messageBox.setSpacing(15);
      messageBox.setAlignment(Pos.TOP_LEFT);
      chatBox.getChildren().add(messageBox);
    });
  }

  public Popup getChatPopup() {
    return chatPopup;
  }
}
