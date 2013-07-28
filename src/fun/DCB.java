package fun;

import java.io.ByteArrayInputStream;
//import java.util.Random;

import javax.sound.sampled.AudioInputStream;

/**
 * Ignore this.
 */
public class DCB {

  public static AudioInputStream shuffle(AudioInputStream in, int samplesOverlap, double paramA) {
    return in;
  }

  public static AudioInputStream reverse(AudioInputStream in) {
    byte[] inAudio = null, outAudio = null;

//    inAudio = FileUtility.bytesFromAIS(in);

    outAudio = new byte[inAudio.length];
    for (int i = (int) ((in.getFrameLength() - 1) * in.getFormat().getFrameSize()), j = 0; i >= 0; i -= in.getFormat()
        .getFrameSize()) {
      // iterate across the frame and copy from in to out
      for (int k = 0; k < in.getFormat().getFrameSize(); k++) {
        outAudio[j + k] = inAudio[i + k];
      }
      j += in.getFormat().getFrameSize();
    }

    ByteArrayInputStream audioData = new ByteArrayInputStream(outAudio);

    return new AudioInputStream(audioData, in.getFormat(), outAudio.length / in.getFormat().getFrameSize());
  }

  // test
  public static AudioInputStream test(AudioInputStream in) {
//    byte[] inAudio = FileUtility.bytesFromAIS(in);
    byte[] inAudio = null;

    // process
    // randomised stutter
    Manipulator m = new Manipulator(inAudio, in.getFormat());

    boolean[] allChannels = new boolean[in.getFormat().getChannels()];
    for (int i = 0; i < allChannels.length; i++) {
      allChannels[i] = true;
    }
    m.setSmoothingLen(25);

    reverse(in);

    // m.sSetAudioData(m.getRangeOfAudioData(0, 4999), 2499, allChannels);

    return new AudioInputStream(new ByteArrayInputStream(m.getAudioDataAsSequential()), in.getFormat(),
        m.getAudioDataAsSequential().length / in.getFormat().getFrameSize());
  }
}
