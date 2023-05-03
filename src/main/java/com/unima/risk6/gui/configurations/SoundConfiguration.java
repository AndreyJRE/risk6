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
  private static MediaPlayer titleSound;

  private static MediaPlayer inGameMusic;
  private static AudioClip rollDiceSound;


  public static void playTitleSound() {
    titleSound.play();
  }

  public static void loadSounds() {
    Media media = new Media(
        Objects.requireNonNull(SoundConfiguration.class.getResource(TITLE_SOUND_PATH))
            .toExternalForm());
    titleSound = new MediaPlayer(media);
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
  }

  public static void playInGameMusic() {
    inGameMusic.play();
  }

  public static void pauseTitleSound() {
    titleSound.pause();
  }

  public static void playRollDiceSound() {
    rollDiceSound.play();
  }


}
