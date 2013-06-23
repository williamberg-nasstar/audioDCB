package util.impl;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import util.Persistence;
import fun.AudioArray;
import fun.impl.AudioArrayMono;
import fun.impl.AudioArrayStereo;

public class PersistenceFilesystem implements Persistence {

  private File directory;

  public PersistenceFilesystem(String directoryPath) throws PersistenceException {
    setDirectory(directoryPath);
  }

  public void setDirectory(String path) throws PersistenceException {
    directory = new File(path);
    if (!directory.exists()) {
      directory = null;
      throw new PersistenceException("File at path does not exist");
    }
    if (!directory.isDirectory()) {
      directory = null;
      throw new PersistenceException("Path is not a directory");
    }
  }

  @Override
  public <T extends AudioArray> T load(AudioFileFormat format, String filename, T arrayType) throws PersistenceException {
    File audioFile = new File(directory, filename);
    if (!audioFile.exists()) {
      directory = null;
      throw new PersistenceException("File at path does not exist");
    }
    if (!audioFile.isFile()) {
      directory = null;
      throw new PersistenceException("Path is not a file");
    }

    AudioInputStream ais = null;
    try {
      ais = AudioSystem.getAudioInputStream(audioFile);
    }
    catch (UnsupportedAudioFileException e) {
      throw new PersistenceException("Audio file type not supported by javax", e);
    }
    catch (IOException e) {
      throw new PersistenceException("Filesystem error", e);
    }

    // TODO: mono and stereo load procedures
    if(arrayType instanceof AudioArrayMono) {
      return null;
    }
    else if(arrayType instanceof AudioArrayStereo) {
      return null;
    }
    else {
      throw new PersistenceException("Return format not supported");
    }
    
//    byte[] returnData = new byte[audioData.length * audioData[0].length * audioData[0][0].length];
//
//    for (int i = 0; i < audioData.length; i++) {
//      for (int j = 0; j < audioFormat.getChannels(); j++) {
//        for (int k = 0; k < audioFormat.getFrameSize() / audioFormat.getChannels(); k++) {
//          returnData[i * audioFormat.getFrameSize() + j * audioFormat.getChannels() + k] = audioData[i][j][k];
//        }
//      }
//    }
  }

  @Override
  public void save(AudioArray audio, AudioFileFormat.Type formatFiletype, String filename) throws PersistenceException {
    File audioFile = new File(directory, filename);
    if (!audioFile.exists()) {
      directory = null;
      throw new PersistenceException("File at path does not exist");
    }
    if (!audioFile.isFile()) {
      directory = null;
      throw new PersistenceException("Path is not a file");
    }

    AudioInputStream ais = null;
    
//    AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(getAudioDataAsSequential()), format,
//        audioData.length / audioFormat.getFrameSize());

    try {
      AudioSystem.write(ais, formatFiletype, audioFile);
    }
    catch (IOException e) {
      throw new PersistenceException("Filesystem error", e);
    }
  }

  public byte[] getAudioDataAsSequential() {
//    byte[] returnData = new byte[audioData.length * audioData[0].length * audioData[0][0].length];
//
//    for (int i = 0; i < audioData.length; i++) {
//      for (int j = 0; j < audioFormat.getChannels(); j++) {
//        for (int k = 0; k < audioFormat.getFrameSize() / audioFormat.getChannels(); k++) {
//          returnData[i * audioFormat.getFrameSize() + j * audioFormat.getChannels() + k] = audioData[i][j][k];
//        }
//      }
//    }
//
//    return returnData;
    
    return null;
  }

}
