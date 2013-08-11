package util;

import javax.sound.sampled.AudioFileFormat.Type;

import data.AudioArray;
import data.AudioSample;


public interface Persistence {

  <T extends AudioSample> AudioArray<T> load(Type format, Class<T> arrayType, String filename) throws PersistenceException;

  <T extends AudioSample> AudioArray<T> load(String filename) throws PersistenceException;

  <T extends AudioSample> void save(AudioArray<T> audio, Type formatFiletype, Class<T> arrayType, String filename) throws PersistenceException;

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
