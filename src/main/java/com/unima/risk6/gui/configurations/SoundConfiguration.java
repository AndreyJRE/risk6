package com.unima.risk6.gui.configurations;

import java.util.Objects;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundConfiguration {

  private static final String TITLE_SOUND_PATH = "/com/unima/risk6/sounds/main_menu.mp3";

  private static final String ROLL_DICE_SOUND_PATH = "/com/unima/risk6/sounds/dice_roll_1.mp3";

  private static final String IN_GAME_MUSIC_PATH = "/com/unima/risk6/sounds/in_game_music.mp3";

  private static final String YOUR_TURN_SOUND_PATH = "/com/unima/risk6/sounds/your_turn_sound.mp3";

  private static final String ALARM_FOR_START_GAME = "/com/unima/risk6/sounds/alarm_for_game_start"
      + ".mp3";
  private static final String TROOPS_MOVE_SOUND = "/com/unima/risk6/sounds/marching_1.mp3";
  private static MediaPlayer titleSound;

  private static MediaPlayer inGameMusic;
  private static AudioClip rollDiceSound;

  private static AudioClip yourTurnSound;

  private static AudioClip startGameSound;

  private static AudioClip troopsMoveSound;


  public static void playTitleSound() {
    //titleSound.play();
  }

  public static void loadSounds() {
    Media media = new Media(
        Objects.requireNonNull(SoundConfiguration.class.getResource(TITLE_SOUND_PATH))
            .toExternalForm());
    titleSound = new MediaPlayer(media);
    titleSound.setVolume(0.4);
    titleSound.setStartTime(Duration.ZERO);
    titleSound.setStopTime(Duration.seconds(10));
    titleSound.setCycleCount(MediaPlayer.INDEFINITE);
    rollDiceSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(ROLL_DICE_SOUND_PATH))
            .toExternalForm());
    inGameMusic = new MediaPlayer(new Media(
        Objects.requireNonNull(SoundConfiguration.class.getResource(IN_GAME_MUSIC_PATH))
            .toExternalForm()));
    inGameMusic.setCycleCount(MediaPlayer.INDEFINITE);
    inGameMusic.setVolume(0.4);
    yourTurnSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(YOUR_TURN_SOUND_PATH))
            .toExternalForm());
    yourTurnSound.setVolume(0.5);
    startGameSound = new AudioClip(
        Objects.requireNonNull(SoundConfiguration.class.getResource(ALARM_FOR_START_GAME))
            .toExternalForm());
    startGameSound.setVolume(0.3);
    troopsMoveSound = new AudioClip(Objects.requireNonNull(SoundConfiguration.class
        .getResource(TROOPS_MOVE_SOUND)).toExternalForm());
    troopsMoveSound.setVolume(0.4);


  }

  public static void playTroopsMoveSound() {
    troopsMoveSound.play();
  }

  public static void stopTroopsMoveSound() {
    troopsMoveSound.stop();
  }

  public static void playInGameMusic() {
    // inGameMusic.play();
  }

  public static void playYourTurnSound() {
    //yourTurnSound.play();
  }

  public static void playStartGameSound() {
    //startGameSound.play();
  }


  public static void pauseTitleSound() {
    titleSound.pause();
  }

  public static void playRollDiceSound() {
    rollDiceSound.play();
  }


}
