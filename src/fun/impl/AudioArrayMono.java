package fun.impl;

import java.util.Arrays;

import fun.AudioArray;
import fun.AudioSample;


public class AudioArrayMono implements AudioArray {

  private AudioSampleMono[] array;

  public AudioArrayMono(AudioSampleMono[] array) {
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
