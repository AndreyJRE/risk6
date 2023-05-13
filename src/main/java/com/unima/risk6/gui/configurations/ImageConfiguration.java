package com.unima.risk6.gui.configurations;

import com.unima.risk6.gui.controllers.enums.ImageName;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class ImageConfiguration {

  private static final String LOG_IN_IMAGE = "/com/unima/risk6/pictures/logInBackground.png";
  private static final String WON_IMAGE = "/com/unima/risk6/pictures/wonBackground.png";
  private static final String SINGLEPLAYER_IMAGE = "/com/unima/risk6/pictures/singlePlayerBackground.png";
  private static final String MULTIPLAYER_IMAGE = "/com/unima/risk6/pictures/multiplayerBackground.png";
  private static final String CREATE_ACCOUNT_IMAGE = "/com/unima/risk6/pictures/createAccountBackground.png";
  private static final String OPTIONS_IMAGE = "/com/unima/risk6/pictures/optionsBackground.png";
  private static final String STATISTICS_IMAGE = "/com/unima/risk6/pictures/statisticsBackground.png";
  private static final String SELECTED_USER_IMAGE = "/com/unima/risk6/pictures/selectedUser.png";
  private static final String SELECT_LOBBY_BACKGROUND = "/com/unima/risk6/pictures/selectLobbyBackground.png";
  private static final String CREATE_LOBBY_BACKGROUND = "/com/unima/risk6/pictures/createLobby.png";
  private static final String PLAYER_ICON = "/com/unima/risk6/pictures/playerIcon.png";
  private static final String EASYBOT_ICON = "/com/unima/risk6/pictures/easyBot.png";
  private static final String MEDIUMBOT_ICON = "/com/unima/risk6/pictures/mediumBot.png";
  private static final String HARDBOT_ICON = "/com/unima/risk6/pictures/hardBot.png";
  private static final String TUTORIAL_ICON = "/com/unima/risk6/pictures/tutorialIcon.png";

  private static final String SWORD_ICON = "/com/unima/risk6/pictures/sword.png";

  private static final String FORTIFY_ICON = "/com/unima/risk6/pictures/fortify.png";

  private static final String REINFORCE_ICON = "/com/unima/risk6/pictures/reinforcement.png";

  private static final String CANNON_ICON = "/com/unima/risk6/pictures/cannonCard.png";
  private static final String INFANTRY_ICON = "/com/unima/risk6/pictures/infantryCard.png";

  private static final String CAVALRY_ICON = "/com/unima/risk6/pictures/cavalryCard.png";

  private static final String WILD_CARD = "/com/unima/risk6/pictures/wildCard.png";

  private static final String PLUS_ICON = "/com/unima/risk6/pictures/plusIcon.png";
  private static final String INFANTRY_RUNNING_GIF = "/com/unima/risk6/pictures/InfantryRunning.gif";

  private static final String ATTACK_DICE_PREVIEW = "/com/unima/risk6/pictures/attackDicePreview.png";

  private static final String DICE_PREVIEW = "/com/unima/risk6/pictures/dicePreview.png";

  private static final String ATTACK_DICE_ROLLING_GIF = "/com/unima/risk6/pictures/attackDiceRollAnimation.gif";

  private static final String DICE_ROLLING_GIF = "/com/unima/risk6/pictures/diceRollAnimation.gif";


  private static HashMap<ImageName, Image> images;
  private static Media titleBackgroundVideo;


  public static void loadImages() {
    images = new HashMap<>();
    Image logInImage = initImage(LOG_IN_IMAGE);
    images.put(ImageName.LOGIN_BACKGROUND, logInImage);
    Image wonImage = initImage(WON_IMAGE);
    images.put(ImageName.WON_BACKGROUND, wonImage);
    Image singlePlayerImage = initImage(SINGLEPLAYER_IMAGE);
    images.put(ImageName.SINGLEPLAYER_BACKGROUND, singlePlayerImage);
    Image multiPlayerImage = initImage(MULTIPLAYER_IMAGE);
    images.put(ImageName.MULTIPLAYER_BACKGROUND, multiPlayerImage);
    Image createAccountImage = initImage(CREATE_ACCOUNT_IMAGE);
    images.put(ImageName.CREATE_ACCOUNT_BACKGROUND, createAccountImage);
    Image optionsImage = initImage(OPTIONS_IMAGE);
    images.put(ImageName.OPTION_BACKGROUND, optionsImage);
    Image statisticsImage = initImage(STATISTICS_IMAGE);
    images.put(ImageName.STATISTICS_BACKGROUND, statisticsImage);
    Image selectedUserImage = initImage(SELECTED_USER_IMAGE);
    images.put(ImageName.SELECTED_USER_BACKGROUND, selectedUserImage);
    Image selectLobbyImage = initImage(SELECT_LOBBY_BACKGROUND);
    images.put(ImageName.SELECT_LOBBY_BACKGROUND, selectLobbyImage);
    Image createLobbyImage = initImage(CREATE_LOBBY_BACKGROUND);
    images.put(ImageName.CREATE_LOBBY_BACKGROUND, createLobbyImage);
    Image playerIcon = initImage(PLAYER_ICON);
    images.put(ImageName.PLAYER_ICON, playerIcon);
    Image easyBotIcon = initImage(EASYBOT_ICON);
    images.put(ImageName.EASYBOT_ICON, easyBotIcon);
    Image mediumBotIcon = initImage(MEDIUMBOT_ICON);
    images.put(ImageName.MEDIUMBOT_ICON, mediumBotIcon);
    Image hardBotIcon = initImage(HARDBOT_ICON);
    images.put(ImageName.HARDBOT_ICON, hardBotIcon);
    Image tutorialBotIcon = initImage(TUTORIAL_ICON);
    images.put(ImageName.TUTORIAL_ICON, tutorialBotIcon);
    Image cannonIcon = initImage(CANNON_ICON);
    images.put(ImageName.CANNON_ICON, cannonIcon);
    Image infantryIcon = initImage(INFANTRY_ICON);
    images.put(ImageName.INFANTRY_ICON, infantryIcon);
    Image cavalryIcon = initImage(CAVALRY_ICON);
    images.put(ImageName.CAVALRY_ICON, cavalryIcon);
    Image wildCardIcon = initImage(WILD_CARD);
    images.put(ImageName.WILDCARD_ICON, wildCardIcon);
    Image plusIcon = initImage(PLUS_ICON);
    images.put(ImageName.PLUS_ICON, plusIcon);
    Image swordIcon = initImage(SWORD_ICON);
    images.put(ImageName.SWORD_ICON, swordIcon);
    Image fortifyIcon = initImage(FORTIFY_ICON);
    images.put(ImageName.FORTIFY_ICON, fortifyIcon);
    Image reinforcementIcon = initImage(REINFORCE_ICON);
    images.put(ImageName.REINFORCE_ICON, reinforcementIcon);
    Image infantryRunningGif = initImage(INFANTRY_RUNNING_GIF);
    images.put(ImageName.INFANTRY_RUNNING_GIF, infantryRunningGif);
    Image attackDicePreview = initImage(ATTACK_DICE_PREVIEW);
    images.put(ImageName.ATTACK_DICE_PREVIEW, attackDicePreview);
    Image dicePreview = initImage(DICE_PREVIEW);
    images.put(ImageName.DICE_PREVIEW, dicePreview);
    Image attackDiceRollingGif = initImage(ATTACK_DICE_ROLLING_GIF);
    images.put(ImageName.ATTACK_DICE_ROLLING, attackDiceRollingGif);
    Image diceRollingGif = initImage(DICE_ROLLING_GIF);
    images.put(ImageName.DICE_ROLLING, diceRollingGif);

    URL mediaUrl = ImageConfiguration.class.getResource(
        "/com/unima/risk6/pictures/backgroundVideo.png");
    String mediaStringUrl = mediaUrl.toExternalForm();
    titleBackgroundVideo = new Media(mediaStringUrl);

  }

  public static Image getImageByName(ImageName imageName) {
    return images.get(imageName);
  }

  public static Image initImage(String path) {
    // Load the image into an ImageView
    return new Image(Objects.requireNonNull(ImageConfiguration.class.getResource(path)).toString());
  }

  public static Media getTitleBackgroundVideo() {
    return titleBackgroundVideo;
  }
}
