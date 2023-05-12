package com.unima.risk6.gui.configurations;

import com.unima.risk6.gui.controllers.enums.ImageName;
import java.net.URL;
import java.util.HashMap;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class ImageConfiguration {

  private static final String LOG_IN_IMAGE = "/com/unima/risk6/pictures/logInBackground.png";
  private static final String WON_IMAGE = "/com/unima/risk6/pictures/wonBackground.png";
  private static final String SINGLEPLAYER_IMAGE = "/com/unima/risk6/pictures/singlePlayerBackground.png";
  private static final String MULTIPLAYER_IMAGE = "/com/unima/risk6/pictures/multiPlayerBackground.png";
  private static final String CREATE_ACCOUNT_IMAGE = "/com/unima/risk6/pictures/createAccountBackground.png";
  private static final String OPTIONS_IMAGE = "/com/unima/risk6/pictures/optionsBackground.png";
  private static final String STATISTICS_IMAGE = "/com/unima/risk6/pictures/statisticsBackground.png";
  private static final String SELECTED_USER_IMAGE = "/com/unima/risk6/pictures/selectedUser.png";
  private static final String PLAYER_ICON = "/com/unima/risk6/pictures/playerIcon.png";
  private static final String EASYBOT_ICON = "/com/unima/risk6/pictures/easyBot.png";
  private static final String MEDIUMBOT_ICON = "/com/unima/risk6/pictures/mediumBot.png";
  private static final String HARDBOT_ICON = "/com/unima/risk6/pictures/hardBot.png";
  private static final String TUTORIAL_ICON = "/com/unima/risk6/pictures/tutorialIcon.png";


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
    URL mediaUrl = ImageConfiguration.class.getResource(
        "/com/unima/risk6/pictures/backgroundVideo.png");
    String mediaStringUrl = mediaUrl.toExternalForm();
    titleBackgroundVideo = new Media(mediaStringUrl);

  }

  public static Image getBackgroundByName(ImageName imageName) {
    return images.get(imageName);
  }

  public static Image initImage(String path) {
    // Load the image into an ImageView
    Image image = new Image(ImageConfiguration.class.getResource(path).toString());
    return image;
  }

  public static Media getTitleBackgroundVideo() {
    return titleBackgroundVideo;
  }
}
