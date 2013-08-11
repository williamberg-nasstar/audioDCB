package util.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import util.Constants;
import util.Persistence;
import data.AudioArray;
import data.AudioSample;
import data.impl.MonoAudioArray;
import data.impl.MonoAudioSample;
import data.impl.StereoAudioArray;
import data.impl.StereoAudioSample;

public class FilesystemPersistence implements Persistence {

  private File directory;

  public FilesystemPersistence(String directoryPath) throws PersistenceException {
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

  /**
   * This method should be fixed to determine the AudioArray type from the file
   * instead of requiring arrayType.
   * 
   * Currently only accepts 16-bit WAVs (mono/stereo). Assumes sample rate is 44,100 samples/s.
   * 
   * Sample amplitudes are not currently scaled.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T extends AudioSample> AudioArray<T> load(AudioFileFormat.Type format, Class<T> arrayType, String filename)
      throws PersistenceException {
    File audioFile = getFile(filename);

    // AudioInputStream
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

    // deserialisation
    byte[] audioBytes = getAudioData(ais);

    if (arrayType.equals(MonoAudioSample.class)) {
      MonoAudioSample[] audioData = new MonoAudioSample[audioBytes.length / 2];

      for (int i = 0; i < audioData.length; i++) {
        int sampleAmplitude = (audioBytes[(i * 2) + 1] << 8) + audioBytes[i * 2];
        audioData[i] = new MonoAudioSample(sampleAmplitude);
      }

      return (AudioArray<T>) new MonoAudioArray(audioData, 44100);
    }
    else if (arrayType.equals(StereoAudioSample.class)) {
      StereoAudioSample[] audioData = new StereoAudioSample[audioBytes.length / 4];

      for (int i = 0; i < audioBytes.length; i += 4) {
        int leftSampleValue = (audioBytes[i + 1] << 8) + audioBytes[i];
        int rightSampleValue = (audioBytes[i + 3] << 8) + audioBytes[i + 2];
        audioData[i / 4] = new StereoAudioSample(leftSampleValue, rightSampleValue);
      }

      return (AudioArray<T>) new StereoAudioArray(audioData, 44100);
    }
    else {
      throw new PersistenceException("Return format not supported");
    }
  }

  /**
   * Deletes the existing file if there is one. Only writes 16-bit WAVs.
   */
  @Override
  public <T extends AudioSample> void save(AudioArray<T> audio, Type formatFiletype, Class<T> arrayType, String filename)
      throws PersistenceException {
    File audioFile = new File(filename);
    audioFile.delete();
    try {
      audioFile.createNewFile();
    }
    catch (IOException e) {
      throw new PersistenceException("Unable to create new file after deleting at specified location", e);
    }

    byte[] audioData = null;
    int channels = 0;

    if (arrayType.equals(MonoAudioSample.class)) {
      channels = 1;
      MonoAudioSample[] sampleArray = (MonoAudioSample[]) audio.getRange(0, audio.getLength());
      audioData = new byte[sampleArray.length * 2];

      for (int i = 0; i < sampleArray.length; i++) {
        byte lsbs = (byte) (sampleArray[i].getLevels()[0] & ((1 << 8) - 1));
        byte msbs = (byte) (sampleArray[i].getLevels()[0] >> 8);

        audioData[i * 2] = lsbs;
        audioData[(i * 2) + 1] = msbs;
      }
    }
    else if (arrayType.equals(StereoAudioSample.class)) {
      channels = 2;
      StereoAudioSample[] sampleArray = (StereoAudioSample[]) audio.getRange(0, audio.getLength());
      audioData = new byte[sampleArray.length * 4];

      for (int i = 0; i < sampleArray.length; i++) {
        byte leftLsbs = (byte) (sampleArray[i].getLevels()[0] & ((1 << 8) - 1));
        byte leftMsbs = (byte) (sampleArray[i].getLevels()[0] >> 8);
        byte rightLsbs = (byte) (sampleArray[i].getLevels()[1] & ((1 << 8) - 1));
        byte rightMsbs = (byte) (sampleArray[i].getLevels()[1] >> 8);

        audioData[i * 4] = leftLsbs;
        audioData[(i * 4) + 1] = leftMsbs;
        audioData[(i * 4) + 2] = rightLsbs;
        audioData[(i * 4) + 3] = rightMsbs;
      }
    }
    else {
      throw new PersistenceException("Save format not supported");
    }

    AudioFormat af = new AudioFormat(44100, 16, channels, true, false);

    // attempting to set arg 3 to something else will probably fail
    AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(audioData), af, -1);

    try {
      AudioSystem.write(ais, formatFiletype, audioFile);
    }
    catch (IOException e) {
      throw new PersistenceException("Filesystem error", e);
    }
  }

  private byte[] getAudioData(AudioInputStream ais) throws PersistenceException {
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
      throw new PersistenceException("Unable to read from file", e);
    }

    return audioBytes;
  }

  /**
   * Currently only accepts WAVs (mono/stereo) containing uncompressed PCM data.
   * 
   * Sample amplitudes are scaled.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T extends AudioSample> AudioArray<T> load(String filename) throws PersistenceException {
    AudioArray<T> result = null;
    File audioFile = getFile(filename);

    FileInputStream fis = null;
    try {
      fis = new FileInputStream(audioFile);
      
//      byte[] fileChunk = new byte[48];
//      
//      fis.read(fileChunk);
//      
//      for(int i = 0; i < fileChunk.length; i++) {
//        System.out.print(i + ": " + fileChunk[i] + " ");
//      }
      
      // always "RIFF"
      byte[] riffField = new byte[4];
      // 16 + number of bytes specifying further data about the compression format
      // (anything other than uncompressed is a bit weird and not supported)
      byte[] sizeField = new byte[4];
      // always "WAVE"
      byte[] waveField = new byte[4];

      fis.read(riffField);
      fis.read(sizeField);
      fis.read(waveField);
      
      if(riffField.equals(Constants.RIFF_TYPE_CHUNK_ID_FIELD) || waveField.equals(Constants.RIFF_TYPE_CHUNK_TYPE_FIELD)) {
        throw new PersistenceException("RIFF type chunk was malformed");
      }
      // ignore size field
      
      int channels = 0;
      int bitsPerSample = 0;
      int sampleRate = 0;
      AudioSample[] audioSampleArray = null;
      
      byte[] chunkNameField = new byte[4];
      
      while(fis.read(chunkNameField) != -1) {
        if(chunkNameField.equals(Constants.FORMAT_CHUNK_NAME_FIELD)) {
          byte[] fmtSizeField = new byte[4];
          byte[] compressionCodeField = new byte[2];
          byte[] channelsCountField = new byte[2];
          byte[] sampleRateField = new byte[4];
          byte[] averageBytesPerSecondField = new byte[4];
          byte[] blockAlignField = new byte[2];
          byte[] sigBitsPerSampleField = new byte[2];
          byte[] extraFmtBytesField = new byte[2];
          
          fis.read(fmtSizeField);
          fis.read(compressionCodeField);
          fis.read(channelsCountField);
          fis.read(sampleRateField);
          fis.read(averageBytesPerSecondField);
          fis.read(blockAlignField);
          fis.read(sigBitsPerSampleField);
          fis.read(extraFmtBytesField);
          
          if(!(compressionCodeField[0] == 0x01
            && compressionCodeField[1] == 0x00)) {
            throw new PersistenceException("Compression code not supported");
          }
          
          if(channelsCountField[0] == 0x01
            && channelsCountField[1] == 0x00) {
              channels = 1;
          }
          else if(channelsCountField[0] == 0x02
              && channelsCountField[1] == 0x00) {
            channels = 2;
          }
          else {
            throw new PersistenceException("Channel count not supported");
          }
          
          sampleRate = (sampleRateField[1] << 8) + sampleRateField[0];
          
          bitsPerSample = (sigBitsPerSampleField[1] << 8) + sigBitsPerSampleField[0];
        }
        else if(chunkNameField.equals(Constants.DATA_CHUNK_NAME_FIELD)) {
          if(channels == 0 || bitsPerSample == 0 || sampleRate == 0 || audioSampleArray == null) {
            throw new PersistenceException("Format was not specified (before all data chunks)");
          }
          
          byte[] dataSizeField = new byte[4];
          fis.read(dataSizeField);
          int dataBytesCount = (dataSizeField[3] << 24) + (dataSizeField[2] << 16) + (dataSizeField[1] << 8) + dataSizeField[0];

          byte[] audioBytes = new byte[dataBytesCount];
          fis.read(audioBytes);
          
          if (channels == 1) {
            audioSampleArray = new MonoAudioSample[dataBytesCount / (bitsPerSample / 8)];

            for (int i = 0; i < audioSampleArray.length; i++) {
              int sampleAmplitude = (audioBytes[(i * 2) + 1] << 8) + audioBytes[i * 2];
              audioSampleArray[i] = new MonoAudioSample(sampleAmplitude);
            }

            result = (AudioArray<T>) new MonoAudioArray((MonoAudioSample[]) audioSampleArray, 44100);
          }
          else if (channels == 2) {
            audioSampleArray = new StereoAudioSample[(dataBytesCount / (bitsPerSample / 8)) / 2];

            for (int i = 0; i < audioBytes.length; i += 4) {
              int leftSampleValue = (audioBytes[i + 1] << 8) + audioBytes[i];
              int rightSampleValue = (audioBytes[i + 3] << 8) + audioBytes[i + 2];
              audioSampleArray[i / 4] = new StereoAudioSample(leftSampleValue, rightSampleValue);
            }

            result = (AudioArray<T>) new StereoAudioArray((StereoAudioSample[]) audioSampleArray, 44100);
          }
        }
      }
    }
    catch (FileNotFoundException e) {
      throw new PersistenceException("File did not exist", e);
    }
    catch (IOException e) {
      throw new PersistenceException("File was malformed", e);
    }
    finally {
      try {
        fis.close();
      }
      catch (IOException e) {
        throw new PersistenceException("Unable to close read stream for file", e);
      }
    }
    
    return result;
  }
  
  private File getFile(String filename) throws PersistenceException {
    File audioFile = new File(directory, filename);
    if (!audioFile.exists()) {
      throw new PersistenceException("File at path does not exist");
    }
    if (!audioFile.isFile()) {
      throw new PersistenceException("Path is not a file");
    }
    
    return audioFile;
  }
  
}
