package util;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;

import fun.AudioArray;

public interface Persistence {

  <T extends AudioArray> T load(AudioFileFormat format, String filename, T arrayType) throws PersistenceException;

  void save(AudioArray audio, Type formatFiletype, String filename) throws PersistenceException;

  public class PersistenceException extends Exception {

    public PersistenceException() {}

    public PersistenceException(String s) {
      super(s);
    }

    public PersistenceException(String s, Throwable t) {
      super(s, t);
    }
  }

}
