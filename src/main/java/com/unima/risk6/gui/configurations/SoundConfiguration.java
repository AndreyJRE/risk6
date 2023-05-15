package com.unima.risk6.gui.configurations;

import java.util.Objects;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class holds the configuration for various sounds used in the application. It includes paths
 * to different sound files and provides methods to load, play, and control the sounds.
 *
 * @author astoyano
 */

public class SoundConfiguration {

  private static final String TITLE_SOUND_PATH = "/com/unima/risk6/sounds/main_menu.mp3";

  private static final String ROLL_DICE_SOUND_PATH = "/com/unima/risk6/sounds/dice_roll_1.mp3";

  private static final String IN_GAME_MUSIC_PATH = "/com/unima/risk6/sounds/in_game_music.mp3";

  private static final String YOUR_TURN_SOUND_PATH = "/com/unima/risk6/sounds/your_turn_sound.mp3";

  private static final String ALARM_FOR_START_GAME_PATH =
      "/com/unima/risk6/sounds/alarm_for_game_start" + ".mp3";
  private static final String TROOPS_MOVE_SOUND_PATH = "/com/unima/risk6/sounds/marching_1.mp3";

  private static final String NOTIFICATION_SOUND_PATH = "/com/unima/risk6/sounds/notification.mp3";

  private static final String CLICK_SOUND_PATH = "/com/unima/risk6/sounds/click.mp3";

  private static final String HAND_IN_SOUND_PATH = "/com/unima/risk6/sounds/hand_in.mp3";
  private static MediaPlayer titleSound;

  private static AudioClip clickSound;

  private static AudioClip notificationSound;

  private static MediaPlayer inGameMusic;
  private static AudioClip rollDiceSound;

  private static AudioClip yourTurnSound;

  private static AudioClip startGameSound;

  private static AudioClip troopsMoveSound;

  private static AudioClip handInSound;

  private static final SimpleDoubleProperty VOLUME = new SimpleDoubleProperty(0.1);

  /**
   * Loads the sounds by initializing the media players and audio clips with the corresponding
   * paths. Each sound is loaded using the appropriate class (MediaPlayer or AudioClip) and
   * configured with the volume property.
   */

  public static void loadSounds() {
    Media media = new Media(
        Objects.requireNonNull(SoundConfiguration.class.getResource(TITLE_SOUND_PATH))
            .toExternalForm());
    titleSound = new MediaPlayer(media);
    titleSound.setVolume(VOLUME.get());
    titleSound.setStartTime(Duration.ZERO);
    titleSound.setStopTime(Duration.seconds(10));
    titleSound.setCycleCount(MediaPlayer.INDEFINITE);
    titleSound.volumeProperty().bind(VOLUME);
    rollDiceSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(ROLL_DICE_SOUND_PATH))
            .toExternalForm());
    rollDiceSound.setVolume(VOLUME.get());
    rollDiceSound.volumeProperty().bind(VOLUME);
    inGameMusic = new MediaPlayer(new Media(
        Objects.requireNonNull(SoundConfiguration.class.getResource(IN_GAME_MUSIC_PATH))
            .toExternalForm()));
    inGameMusic.setCycleCount(MediaPlayer.INDEFINITE);
    inGameMusic.setVolume(VOLUME.get());
    VOLUME.addListener((observable, oldValue, newValue) -> {
      inGameMusic.setVolume(newValue.doubleValue() / 3.0);
    });
    yourTurnSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(YOUR_TURN_SOUND_PATH))
            .toExternalForm());
    yourTurnSound.setVolume(VOLUME.get());
    yourTurnSound.volumeProperty().bind(VOLUME);
    startGameSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(ALARM_FOR_START_GAME_PATH))
            .toExternalForm());
    startGameSound.setVolume(VOLUME.get());
    startGameSound.volumeProperty().bind(VOLUME);
    troopsMoveSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(TROOPS_MOVE_SOUND_PATH))
            .toExternalForm());
    troopsMoveSound.setVolume(VOLUME.get());
    troopsMoveSound.volumeProperty().bind(VOLUME);
    handInSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(HAND_IN_SOUND_PATH))
            .toExternalForm());
    handInSound.setVolume(0.6);
    notificationSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(NOTIFICATION_SOUND_PATH))
            .toExternalForm());
    notificationSound.setVolume(1);
    clickSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(CLICK_SOUND_PATH))
            .toExternalForm());
    clickSound.setVolume(0.6);

  }

  /**
   * Plays the title sound.
   */
  public static void playTitleSound() {
    titleSound.play();
  }

  /**
   * Plays the troops move sound.
   */
  public static void playTroopsMoveSound() {
    troopsMoveSound.play();
  }

  /**
   * Stops the troops move sound.
   */
  public static void stopTroopsMoveSound() {
    troopsMoveSound.stop();
  }

  /**
   * Plays the in game music.
   */
  public static void playInGameMusic() {
    inGameMusic.play();
  }

  /**
   * Plays the turn sound.
   */
  public static void playYourTurnSound() {
    yourTurnSound.play();
  }

  /**
   * Plays the start game sound.
   */
  public static void playStartGameSound() {
    startGameSound.play();
  }

  /**
   * Pauses the title sound.
   */
  public static void pauseTitleSound() {
    titleSound.pause();
  }

  /**
   * Plays the roll dice sound.
   */
  public static void playRollDiceSound() {
    rollDiceSound.play();
  }

  public static double getVolume() {
    return VOLUME.get();
  }

  public static void setVolume(double volume) {
    SoundConfiguration.VOLUME.set(volume);
  }

  /**
   * Plays the notification sound.
   */
  public static void playNotificationSound() {
    notificationSound.play();
  }

  /**
   * Plays the click sound.
   */
  public static void playClickSound() {
    clickSound.play();
  }

  /**
   * Stops the in game music.
   */
  public static void stopInGameMusic() {
    inGameMusic.stop();
  }

  /**
   * Plays the hand in sound.
   */
  public static void playHandInSound() {
    handInSound.play();
  }

}
