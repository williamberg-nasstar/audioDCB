package fun;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class Manipulator {

  private byte[][][] audioData;
  private AudioFormat audioFormat;

  int smoothingLen;

  public Manipulator() {}

  public Manipulator(byte[] audioData, AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
    setAudioDataFromSequential(audioData);

    setSmoothingLen(25);
  }

  // truncates when it hits the end of the array
  public byte[][][] getRangeOfAudioData(int start, int end) {
    byte[][][] intermediateArray;
    intermediateArray = getRange(audioData, start, end - 1, false);
    int i = 0;
    for (; i < intermediateArray.length; i++) {
      if (intermediateArray[i] == null)
        break;
    }
    return getRange(intermediateArray, 0, i - 1, false);
  }

  public int getSampleCount(int ms) {
    return ms * (int) (audioFormat.getFrameRate() / 1000);
  }

  public AudioInputStream getAudioInputStream() {
    return new AudioInputStream(new ByteArrayInputStream(getAudioDataAsSequential()), audioFormat, audioData.length
        / audioFormat.getFrameSize());
  }

  public void setAudioInputStream(AudioInputStream ais) {
    audioFormat = ais.getFormat();
    setAudioDataFromSequential(FileUtility.bytesFromAIS(ais));
  }

  // converts audioData into a sequential form, perhaps for file writing
  // purposes
  public byte[] getAudioDataAsSequential() {
    byte[] returnData = new byte[audioData.length * audioData[0].length * audioData[0][0].length];

    for (int i = 0; i < audioData.length; i++) {
      for (int j = 0; j < audioFormat.getChannels(); j++) {
        for (int k = 0; k < audioFormat.getFrameSize() / audioFormat.getChannels(); k++) {
          returnData[i * audioFormat.getFrameSize() + j * audioFormat.getChannels() + k] = audioData[i][j][k];
        }
      }
    }

    return returnData;
  }

  public void setAudioDataFromSequential(byte[] s) {
    audioData = new byte[s.length / audioFormat.getFrameSize()][audioFormat.getChannels()][audioFormat.getFrameSize()
        / audioFormat.getChannels()];

    for (int i = 0; i < s.length / audioFormat.getFrameSize(); i++) {
      for (int j = 0; j < audioFormat.getChannels(); j++) {
        for (int k = 0; k < audioFormat.getFrameSize() / audioFormat.getChannels(); k++) {
          audioData[i][j][k] = s[i * audioFormat.getFrameSize() + j * audioFormat.getChannels() + k];
        }
      }
    }
  }

  private int intFromByteArraySample(byte[] b) {
    int value;

    if (audioFormat.isBigEndian()) {
      if (b[0] < 0) {
        value = -1;
      }
      else {
        value = 0;
      }

      for (int i = 0; i < b.length; i++) {
        value = (value << 8) + (b[i] & 0xff);
      }
    }
    else {
      if (b[b.length - 1] < 0) {
        value = -1;
      }
      else {
        value = 0;
      }

      for (int i = 0; i < b.length; i++) {
        value += (b[i] & 0xff) << (8 * i);
      }
    }

    return value;
  }

  // assumes i is big-endian
  private byte[] byteArraySampleFromInt(int i, int returnLen) {
    byte[] intermediateByteArray = new byte[4];

    if (audioFormat.isBigEndian()) {
      intermediateByteArray[0] = (byte) (i >>> 24);
      intermediateByteArray[1] = (byte) (i >>> 16);
      intermediateByteArray[2] = (byte) (i >>> 8);
      intermediateByteArray[3] = (byte) i;

      return getRange(intermediateByteArray, 4 - returnLen, 3);
    }
    else {
      intermediateByteArray[0] = (byte) i;
      intermediateByteArray[1] = (byte) (i >>> 8);
      intermediateByteArray[2] = (byte) (i >>> 16);
      intermediateByteArray[3] = (byte) (i >>> 24);

      return getRange(intermediateByteArray, 0, returnLen);
    }
  }

  public byte[][][] getAudioData() {
    return audioData;
  }

  public int getSmoothingLen() {
    return smoothingLen;
  }

  public void setSmoothingLen(int i) {
    smoothingLen = i;
  }

  public int getLengthInSamples() {
    return audioData.length;
  }

  public int getLengthInms() {
    return (int) (1000 * audioData.length / audioFormat.getFrameRate());
  }

  public static byte[] getRange(byte[] array, int start, int end) {
    return Arrays.copyOfRange(array, start, end + 1);
  }

  public byte[][][] getRange(byte[][][] array, int start, int end, boolean debug) {
    if (debug) {
      System.out.println("Ouch");
      for (int i = 0; i < array.length; i++) {
        for (int j = 0; j < array[0].length; j++) {
          for (int k = 0; k < array[0][0].length; k++) {
            System.out.print(Integer.toBinaryString(array[i][j][k]) + " ");
          }
          System.out.println("- " + intFromByteArraySample(array[i][j]));
        }
      }
    }

    return Arrays.copyOfRange(array, start, end + 1);
  }
}
