package data.impl;

import data.AudioSample;

public class StereoAudioSample implements AudioSample {

  private int[] levels;
  
  public StereoAudioSample(int... levels) {
    setLevels(levels);
  }

  @Override
  public int[] getLevels() {
    return levels;
  }

  public void setLevels(int... levels) {
    this.levels = levels;
  }

  @Override
  public String toString() {
    return "left: " + levels[0] + ", right; " + levels[1];
  }

}
