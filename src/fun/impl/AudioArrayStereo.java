package fun.impl;

import java.util.Arrays;

import fun.AudioArray;
import fun.AudioSample;

public class AudioArrayStereo implements AudioArray {

  private AudioSampleStereo[] array;

  public AudioArrayStereo(AudioSampleStereo[] array) {
    this.array = array;
  }
  
  @Override
  public AudioSample[] getRange(int start, int end) {
    return Arrays.copyOfRange(array, start, end + 1);
  }

  @Override
  public void setRange(AudioSample[] range, int start) {
    System.arraycopy(range, 0, array, start, range.length);
  }

}
