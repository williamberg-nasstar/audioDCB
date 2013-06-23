package fun;


public interface AudioSample {
  
  int getLevel(int channel);
  
  void setLevel(int channel, int level);

}
