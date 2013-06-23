package fun;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

public class FileUtility {

  // returns audio data
  public static byte[] bytesFromAIS(AudioInputStream ais) {
    byte[] buffer = new byte[ais.getFormat().getFrameSize()];
    byte[] audioBytes = new byte[(int) (ais.getFormat().getFrameSize() * ais.getFrameLength())];

    int frameOffset = 0;
    try {
      while (ais.read(buffer) != -1) {
        for (int i = 0; i < buffer.length; i++) {
          audioBytes[(frameOffset * buffer.length) + i] = buffer[i];
        }

        frameOffset++;
      }
    }
    catch (IOException e) {
      System.out.println("A weird error occurred: " + e.getMessage());
    }

    return audioBytes;
  }
}
