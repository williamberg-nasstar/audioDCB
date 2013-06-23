package fun.impl;

import fun.AudioSample;

public class AudioSampleStereo implements AudioSample {

  private int leftLevel;
  private int rightLevel;

  public AudioSampleStereo(int left, int right) {
    leftLevel = left;
    rightLevel = right;
  }

  @Override
  public int getLevel(int channel) {
    return channel == 0 ? leftLevel : rightLevel;
  }

  public void setLevel(int channel, int level) {
    leftLevel = channel == 0 ? level : leftLevel;
    rightLevel = channel == 1 ? level : rightLevel;
  }

}
