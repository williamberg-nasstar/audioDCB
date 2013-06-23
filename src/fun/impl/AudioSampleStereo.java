package fun.impl;

import fun.AudioSample;

public class AudioSampleStereo implements AudioSample {

  private int[] levels;
  
  public AudioSampleStereo(int... levels) {
    setLevels(levels);
  }

  @Override
  public int[] getLevels() {
    return levels;
  }

  public void setLevels(int... levels) {
    this.levels = levels;
  }

}
