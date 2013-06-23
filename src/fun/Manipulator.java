package fun;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/*
 * William Berg's byte[] audio manipulation suite.
 * 
 * This is a class which deals with the AudioFormat and byte[] audio data 
 * duality going on in AudioInputStream for you, and simultaneously provides
 * an interface. This interface provides a finely-tuned yet highly usable set
 * of operations which enables you to do a lot of interesting things
 * with relative ease - as long as your focus is on the sample level. I
 * suspect that enthusiasts in microsound in particular will find their lives
 * greatly simplifed by this class.
 * 
 * Please feel free to split this into separate user method and data layers,
 * though I suspect this won't ever really need to be done.
 * 
 * 
 * Use:
 * 
 * AudioInputStream myAIS;
 * byte[] myAudioData;
 * 
 * ... assign something to these
 * 
 * BergManipulator m = new BergManipulator(myAudioData, myAIS.getFormat());
 * 
 * m.setSmoothSmpl(100);          // set smoothing length in samples
 * m.sStutterForSmpl(0,256,4096); // smoothly stutters samples 0-256 for 4096 samples
 * m.volumeTween(0,
 *   m.getAudioData().length,
 *   100,
 *   0);                          // fades from full to mute from start to end
 * m.doTheFooBar();
 * m.reverse();
 * m.doTheBergWalk();
 * 
 * AudioInputStream myNewAIS = m.getAudioInputStream();
 * 
 */

public class Manipulator {

  private byte[][][] audioData;
  private AudioFormat audioFormat;

  int smoothingLen;

  private static int hello = 0;

  public Manipulator() {}

  public Manipulator(byte[] audioData, AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
    setAudioDataFromSequential(audioData);

    setSmoothingLen(25);
  }

  // smoothly puts newData onto audioData.
  // bounds of newData must be within audioData.
  public void sSetAudioData(byte[][][] newData, int at, boolean[] channels) throws IllegalArgumentException {
    // TODO: remove this eventually
    boolean isAllChannels = true;
    for (boolean b : channels) {
      if (!b)
        isAllChannels = false;
    }

    if (isAllChannels && at >= 0 && at + newData.length < audioData.length && newData.length >= getSmoothingLen()) {
      // newData trumps at start - no smoothing
      if (at == 0) {
        if (at + newData.length == audioData.length) {
          // trumps at end too
          audioData = newData;
        }
        else {
          // set non-smoothed part
          nSetAudioData(getRange(newData, 0, newData.length - getSmoothingLen(), false), 0, channels);
          // set smoothed part
          nSetAudioData(
              fade(getRange(newData, newData.length - getSmoothingLen(), newData.length, false),
                  getRangeOfAudioData(at + newData.length - getSmoothingLen(), at + newData.length)), at
                  + newData.length - getSmoothingLen(), channels);
        }
      }
      else if (at + newData.length == audioData.length) {
        // trumps at end
        if (at == 0) {
          // trumps at beginning too
          audioData = newData;
        }
        else {
          // set non-smoothed part
          nSetAudioData(getRange(newData, getSmoothingLen(), newData.length, false), at + getSmoothingLen(), channels);
          // set smoothed part
          nSetAudioData(
              fade(getRangeOfAudioData(at, at + getSmoothingLen()), getRange(newData, 0, getSmoothingLen(), false)),
              at, channels);
        }
      }
      else {
        // finally - general case
        // set non-smoothed part
        nSetAudioData(getRange(newData, getSmoothingLen(), newData.length - getSmoothingLen(), false), at
            + getSmoothingLen(), channels);

        fade(getRangeOfAudioData(at, at + getSmoothingLen()), getRange(newData, 0, getSmoothingLen() - 1, true));

        // set smoothed parts
        nSetAudioData(
            fade(getRangeOfAudioData(at, at + getSmoothingLen()), getRange(newData, 0, getSmoothingLen() - 1, false)),
            at, channels);
        nSetAudioData(
            fade(getRange(newData, newData.length - getSmoothingLen(), newData.length - 1, false),
                getRangeOfAudioData(at + newData.length - getSmoothingLen(), at + newData.length)), at + newData.length
                - getSmoothingLen(), channels);
      }
    }
    else {
      throw new IllegalArgumentException("Method only works for all-channel "
          + "in-bounds-set with new audio at least as long as smoothing time");
    }
  }

  public void nSetAudioData(byte[][][] newData, int at, boolean[] channels) throws IllegalArgumentException {
    // TODO: remove this eventually
    boolean isAllChannels = true;
    for (boolean b : channels) {
      if (!b)
        isAllChannels = false;
    }

    if (isAllChannels && at >= 0 && at + newData.length < audioData.length) {
      for (int i = 0; i < newData.length; i++) {
        audioData[at + i] = newData[i];
      }
    }
    else {
      throw new IllegalArgumentException("Method only works for all-channel in-bounds set");
    }
  }

  // returns first tweening to second
  public byte[][][] fade(byte[][][] first, byte[][][] second) throws IllegalArgumentException {
    if (first.length != second.length || first[0].length != second[0].length
        || first[0][0].length != second[0][0].length) {
      throw new IllegalArgumentException("Sound clips must be same dimensions");
    }

    byte[][][] returnAudio = new byte[first.length][first[0].length][first[0][0].length];

    for (int i = 0; i < first.length; i++) {
      for (int j = 0; j < first[0].length; j++) {
        int firstValue = 0, secondValue = 0;
        firstValue = intFromByteArraySample(first[i][j]);
        secondValue = intFromByteArraySample(second[i][j]);

        // System.out.println("Start: " + firstValue);

        // actual tweening law
        // linear
        firstValue *= (double) (first.length - i - 1) / (double) first.length;
        secondValue *= (i + 1) / (double) first.length;

        // firstValue += 5000;
        // secondValue += 5000;

        // System.out.println(i + "/" + j + "|firstValue: " +
        // intFromByteArraySample(first[i][j]) + "->" + firstValue +
        // ", secondValue: " + intFromByteArraySample(second[i][j]) + "->" +
        // secondValue);

        // System.out.println(firstValue + ":\t\t" +
        // String.format("%16s",
        // Integer.toBinaryString(firstValue)).replace(' ', '0') +
        // "|" + String.format("%16s",
        // Integer.toBinaryString(firstValue/100)).replace(' ', '0'));
        // System.out.println(secondValue + ":\t\t" +
        // String.format("%16s",
        // Integer.toBinaryString(secondValue)).replace(' ', '0') +
        // "|" + String.format("%16s",
        // Integer.toBinaryString(secondValue/100)).replace(' ', '0'));

        // System.out.println("Sum: " + (firstValue + secondValue));

        // TODO: figure out if my internal representation is actually correct

        // System.out.println("Times: (" + first.length + " - " + i + " - 1) / "
        // + first.length + " = " + (double) (first.length - i - 1) / (double)
        // first.length);
        System.out.println(intFromByteArraySample(first[i][j]));
        // System.out.println("-");

        returnAudio[i][j] = byteArraySampleFromInt(firstValue + secondValue, first[0][0].length);
        // returnAudio[i][j] = byteArraySampleFromInt((firstValue + secondValue)
        // / 2, first[0][0].length);
      }
    }

    System.out.println("--------------------");
    return returnAudio;
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
            hello++;
          }
          System.out.println("- " + intFromByteArraySample(array[i][j]));
        }
      }
    }

    return Arrays.copyOfRange(array, start, end + 1);
  }
}
