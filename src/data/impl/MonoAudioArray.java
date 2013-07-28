package data.impl;

import java.util.Arrays;

import data.AudioArray;


public class MonoAudioArray implements AudioArray<MonoAudioSample> {

  private MonoAudioSample[] array;

  public MonoAudioArray(MonoAudioSample[] array) {
    this.array = array;
  }
  
  @Override
  public MonoAudioSample[] getRange(int start, int count) {
    return Arrays.copyOfRange(array, start, start + count);
  }

  @Override
  public void setRange(MonoAudioSample[] range, int start) {
    System.arraycopy(range, 0, array, start, range.length);
  }
  
  @Override
  public int getLength() {
    return array.length;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(MonoAudioSample s : array) {
      sb.append(s.toString());
      sb.append('\n');
    }
    return sb.toString();
  }

}
