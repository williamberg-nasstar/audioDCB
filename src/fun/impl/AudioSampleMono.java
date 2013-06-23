package fun.impl;

import fun.AudioSample;

public class AudioSampleMono implements AudioSample {

  private int level;

  public AudioSampleMono(int level) {
  }

  @Override
  public int getLevel(int channel) {
    return level;
  }

  public void setLevel(int channel, int level) {
    this.level = level;
  }

}
