package util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class FileManager {

  public static AudioInputStream getAudioStreamForFileInWD(String filename) throws UnsupportedAudioFileException,
      IOException {
    String path = System.getProperty("user.dir") + "\\" + filename;

    AudioInputStream returnAIS = null;
    returnAIS = AudioSystem.getAudioInputStream(new File(path));

    return returnAIS;
  }

  public static AudioInputStream getAudioStreamForExistingFile(String path) throws UnsupportedAudioFileException,
      IOException {
    AudioInputStream returnAIS = null;
    returnAIS = AudioSystem.getAudioInputStream(new File(path));

    return returnAIS;
  }

  public static File getFilehandleForNewFile(String filename) {
    File f = new File(System.getProperty("user.dir") + "\\" + filename);
    try {
      f.createNewFile();
    }
    catch (IOException e) {
      System.out.println("A weird error occurred: " + e.getMessage());
    }

    return f;
  }

  public static void write(AudioInputStream s, File f) throws IOException, UnsupportedAudioFileException {
    AudioSystem.write(s, AudioFileFormat.Type.WAVE, f);
  }
}
