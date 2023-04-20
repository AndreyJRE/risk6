package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.gui.uiModels.ActivePlayerUi;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.gui.uiModels.PlayerUi;
import com.unima.risk6.gui.uiModels.TimeUi;
import com.unima.risk6.gui.uiModels.TroopsCounterUi;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

public class GameScene extends Scene implements Initializable {

//  private GameState gameState;
//
//  private Stack<CardUi> cardUis;

  private BorderPane root;

  private Set<CountryUi> countriesUis;

  private double originalScreenWidth;

  private double originalScreenHeight;

  private ActivePlayerUi ellipseWithRectangle;

  private ArrayList<PlayerUi> PlayerUis;

  private TimeUi TimeUi;

  private BorderPane chatBoxPane = new BorderPane();

  private Set<Line> arrow;

  private CountryName previousCountry;

  private CountryName newCountry;

  private Group countriesGroup;

  public GameScene(
//  GameState gameState,
//  Stack<CardUi> cardUiS
//  this.gameState = gameState;
//  this.cardUiS = cardUiS;
      int width,
      int height,
      Set<CountryUi> countriesUis,
      ActivePlayerUi ellipseWithRectangle,
      ArrayList<PlayerUi> PlayerUis, TimeUi TimeUi) {
    super(new BorderPane(), width, height);
    this.originalScreenWidth = width;
    this.originalScreenHeight = height;
    this.countriesUis = countriesUis;
    this.ellipseWithRectangle = ellipseWithRectangle;
    this.PlayerUis = PlayerUis;
    this.TimeUi = TimeUi;

    this.root = (BorderPane) getRoot();
    this.countriesGroup = new Group();

    this.initializeGameScene();

    this.addListeners();
  }

  private void initializeGameScene() {

    StackPane countriesPane = initializeCountriesPane();
    root.setCenter(countriesPane);
    StackPane playerPane = initializePlayersPane();
    root.setLeft(playerPane);
    StackPane timePane = initializeTimePane();
    root.setRight(timePane);
    StackPane bottomPane = initializeBottomPane();
    root.setBottom(bottomPane);
  }

  private StackPane initializeCountriesPane() {
    double widthRatio = getWidth() / originalScreenWidth;
    double heightRatio = getHeight() / originalScreenHeight;
    double initialScale = Math.min(widthRatio, heightRatio);

    countriesGroup.getChildren().addAll(countriesUis);
    addMouseHandlers(countriesGroup);
    StackPane countriesPane = new StackPane();

    for (CountryUi countryUi : countriesUis) {
      Bounds bounds = countryUi.getCountryPath().getBoundsInParent();
      double ellipseX = bounds.getMinX() + bounds.getWidth() * 0.5;
      double ellipseY = bounds.getMinY() + bounds.getHeight() * 0.5;

      Point2D correctedCoordinates = CountryUi.correctEllipsePlacement(countryUi.getCountry(),
          ellipseX, ellipseY);
      ellipseX = correctedCoordinates.getX();
      ellipseY = correctedCoordinates.getY();

      TroopsCounterUi troopsCounterUi = new TroopsCounterUi(ellipseX, ellipseY);

      countryUi.setTroopsCounterUi(troopsCounterUi);

      countriesGroup.getChildren().add(troopsCounterUi);

    }
    countriesGroup.setScaleX(initialScale);
    countriesGroup.setScaleY(initialScale);

    countriesPane.getChildren().add(countriesGroup);
    return countriesPane;
  }

  private StackPane initializePlayersPane() {
    VBox playersVbox = new VBox();
    playersVbox.setMaxWidth(100);
    playersVbox.getChildren().addAll(PlayerUis);
    playersVbox.setAlignment(Pos.CENTER);
    playersVbox.setSpacing(10);
    StackPane playerPane = new StackPane();
    playerPane.getChildren().add(playersVbox);
    playerPane.setPadding(new Insets(0, 0, 0, 15));
    return playerPane;
  }

  private StackPane initializeTimePane() {
    StackPane timePane = new StackPane();
    timePane.getChildren().add(TimeUi);
    timePane.setAlignment(Pos.CENTER);
    timePane.setPadding(new Insets(0, 15, 0, 0));
    return timePane;
  }

  private StackPane initializeBottomPane() {
    StackPane bottomPane = new StackPane();
    Button chatButton = new Button();
    ImageView chatIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/chatIcon.png").toString()));
    chatIcon.setFitWidth(40);
    chatIcon.setFitHeight(40);
    chatButton.setGraphic(chatIcon);
    chatButton.setStyle("-fx-background-radius: 15px;");
    chatButton.setFocusTraversable(false);

    Label chatLabel = new Label("This is a chat popup.");
    chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

    Button closeButton = new Button();
    closeButton.setPrefSize(20, 20);
    ImageView closeIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeIcon.setFitWidth(40);
    closeIcon.setFitHeight(40);
    closeButton.setGraphic(closeIcon);
    closeButton.setStyle("-fx-background-radius: 15px;");
    closeButton.setFocusTraversable(false);

    chatBoxPane.setTop(closeButton);
    chatBoxPane.setAlignment(closeButton, Pos.TOP_RIGHT);

    VBox chatBox = new VBox();
    chatBox.getChildren().addAll(chatLabel);

    chatBoxPane.setCenter(chatBox);

    chatBoxPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

    Popup chatPopup = new Popup();
    chatPopup.getContent().add(chatBoxPane);

    closeButton.setOnAction(event -> chatPopup.hide());

    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    chatBoxPane.setEffect(dropShadow);

    chatButton.setOnAction(event -> {
      chatBoxPane.setPrefSize(root.getWidth() * 0.70, root.getHeight() * 0.70);

      Bounds rootBounds = root.getBoundsInLocal();

      double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
      double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

      // Get the width and height of the popup
      double popupWidth = chatBoxPane.getPrefWidth();
      double popupHeight = chatBoxPane.getPrefHeight();

      // Set the position of the chat popup to center it in the root Scene
      chatPopup.setX(centerX - popupWidth / 2);
      chatPopup.setY(centerY - popupHeight / 2);
      chatPopup.show(chatButton.getScene().getWindow());
    });

    bottomPane.getChildren().add(ellipseWithRectangle);
    bottomPane.getChildren().add(chatButton);
    bottomPane.setAlignment(Pos.CENTER);
    bottomPane.setAlignment(chatButton, Pos.CENTER_RIGHT);
    bottomPane.setMargin(chatButton, new Insets(0, 10, 0, 0));
    bottomPane.setPadding(new Insets(0, 0, 15, 0));
    return bottomPane;
  }

  private void addMouseHandlers(Group countriesGroup) {
    // Mouse press event handler
    EventHandler<MouseEvent> mousePressHandler = event -> {
      if (event.getSource() instanceof CountryUi) {
        CountryUi currentCountryUi = (CountryUi) event.getSource();

        Set<Country> adjacentCountries = currentCountryUi.getCountry().getAdjacentCountries();

        for (Country country : adjacentCountries) {
          CountryUi adjecentCountryUi = null;
          for (Node countryUiNode : countriesGroup.getChildren()) {
            if (countryUiNode instanceof CountryUi) {
              CountryUi desiredCountryUI = (CountryUi) countryUiNode;
              if (desiredCountryUI.getCountryId().equals(country.getCountryName())) {
                adjecentCountryUi = desiredCountryUI;
                break;
              }
            }
          }

          Line arrow = new Line();
          arrow.setStroke(Color.RED);
          arrow.setVisible(false);

          int index = 0;
          for (Node troopsCounterUiNode : countriesGroup.getChildren()) {
            if (troopsCounterUiNode instanceof TroopsCounterUi) {
              countriesGroup.getChildren().add(index++, arrow);
              break;
            } else {
              index++;
            }
          }

          Point2D clickPosInScene =
              currentCountryUi.localToScene(
                  currentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterX(),
                  currentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterY());

          Point2D clickPosInSceneToCountry = adjecentCountryUi.localToScene(
              adjecentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterX(),
              adjecentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterY());

          Point2D clickPosInGroup = countriesGroup.sceneToLocal(clickPosInScene);
          Point2D clickPosInGroupToCountry = countriesGroup.sceneToLocal(clickPosInSceneToCountry);

          arrow.setStartX(clickPosInGroup.getX());
          arrow.setStartY(clickPosInGroup.getY());
          arrow.setEndX(clickPosInGroupToCountry.getX());
          arrow.setEndY(clickPosInGroupToCountry.getY());

          // Change the cursor
          setCursor(Cursor.MOVE);

          // Make the arrow visible
          arrow.setVisible(true);
        }
      }
    };

//    HERE IS ACCURATE CODE TO DETERMINE START AND END
//    // Get the click position relative to the scene
//    Point2D clickPosInScene = countryUi.localToScene(event.getX(), event.getY());
//
//    // Set the start and end position of the arrow
//    arrow.setStartX(clickPosInScene.getX());
//    arrow.setStartY(clickPosInScene.getY());
//    arrow.setEndX(clickPosInScene.getX());
//    arrow.setEndY(clickPosInScene.getY());

    //END

//    // Mouse drag event handler
//    EventHandler<MouseEvent> mouseDragHandler = event -> {
//      if (arrow.isVisible()) {
//        arrow.setEndX(event.getSceneX());
//        arrow.setEndY(event.getSceneY());
//      }
//    };

    // Mouse release event handler
//    EventHandler<MouseEvent> mouseReleaseHandler = event -> {
//      if (event.getSource() instanceof CountryUi && arrow.isVisible()) {
//        CountryUi countryUi = (CountryUi) event.getSource();
//        newCountry = countryUi.getCountry().getCountryName();
//
//        // Print the clicked countries
//        System.out.println("Clicked from " + previousCountry + " to " + newCountry);
//
//        // Reset the previous and new countries
//        previousCountry = null;
//        newCountry = null;
//      }
//
//      // Hide the arrow and reset the cursor
//      arrow.setVisible(false);
//      setCursor(Cursor.DEFAULT);
//    };
//
    // Add the mouse event handlers to each CountryUi
    for (CountryUi countryUi : countriesUis) {
      countryUi.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressHandler);
//      countryUi.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
//      countryUi.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleaseHandler);
    }
  }


  private void addListeners() {
    widthProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = newVal.doubleValue() / originalScreenWidth;
      double heightScale = getHeight() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);
      countriesGroup.setScaleX(scale + 0.4);
      countriesGroup.setScaleY(scale + 0.4);
    });

    heightProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = getWidth() / originalScreenWidth;
      double heightScale = newVal.doubleValue() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);
      countriesGroup.setScaleX(scale + 0.3);
      countriesGroup.setScaleY(scale + 0.3);
    });
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}

