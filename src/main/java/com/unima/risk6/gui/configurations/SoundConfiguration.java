package com.unima.risk6.gui.configurations;

import javafx.scene.media.AudioClip;

public class SoundConfiguration {

  private static final String TITLE_SOUND_PATH = "/com/unima/risk6/sounds/main_menu.mp3";
  private static AudioClip audioClip;


  public static void playTitleSound() {
    audioClip.play();
  }

  public static void loadSounds() {
    audioClip = new AudioClip(
        SoundConfiguration.class.getResource(TITLE_SOUND_PATH).toExternalForm());
    audioClip.setCycleCount(AudioClip.INDEFINITE);

  }


}
