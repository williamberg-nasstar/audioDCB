package fun.impl;

import fun.AudioSample;

public class AudioSampleMono implements AudioSample {

  private int level;

  public AudioSampleMono(int level) {}

  @Override
  public int[] getLevels() {
    return new int[] { level };
  }

  public void setLevels(int... levels) {
    level = levels[0];
  }

}
