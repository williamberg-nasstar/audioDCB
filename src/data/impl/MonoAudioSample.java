package data.impl;

import data.AudioSample;

public class MonoAudioSample implements AudioSample {

  private int level;

  public MonoAudioSample(int level) {
    this.level = level;
  }

  @Override
  public int[] getLevels() {
    return new int[] { level };
  }

  public void setLevels(int... levels) {
    level = levels[0];
  }

  @Override
  public String toString() {
    return "level: " + level;
  }

}
