package fun;


public interface AudioArray {

  AudioSample[] getRange(int start, int end);
  
  void setRange(AudioSample[] range, int start);
  
}
