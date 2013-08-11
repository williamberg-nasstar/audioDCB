package data;


public interface AudioArray<T extends AudioSample> {

  T[] getRange(int start, int end);
  
  void setRange(T[] range, int start);
  
  int getLength();
  
  int getSampleRate();

}
