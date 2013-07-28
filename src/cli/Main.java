package cli;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;

import data.AudioArray;
import data.impl.MonoAudioSample;

import util.Persistence;
import util.Persistence.PersistenceException;
import util.impl.FilesystemPersistence;

public class Main {

  // args
  private static boolean help;
  private static String workingDirectory;
  private static String inputFilename;
  
  private static Persistence persistence;

  public static void main(String... args) {
    parseArgs(args);

    if (help == true) {
      printHelp();
      return;
    }
    if (workingDirectory == null) {
      System.out.println("No working directory given!");
      return;
    }
    if (inputFilename == null) {
      System.out.println("No input filename given!");
      return;
    }
    
    try {
      persistence = new FilesystemPersistence(workingDirectory);
    }
    catch (PersistenceException e) {
      e.printStackTrace();
    }
    
    AudioArray<MonoAudioSample> audioArray = null;
    
    try {
      audioArray = persistence.load(AudioFileFormat.Type.WAVE, MonoAudioSample.class, inputFilename);
    }
    catch (PersistenceException e) {
      e.printStackTrace();
    }

    String outputFilename = "processed-" + inputFilename;

    try {
      persistence.save(audioArray, Type.WAVE, MonoAudioSample.class, outputFilename);
    }
    catch (PersistenceException e) {
      e.printStackTrace();
    }
  }

  private static void parseArgs(String... args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--help")) {
        help = true;
      }
      else if (args[i].equals("-d")) {
        workingDirectory = args[i + 1];
        i++;
      }
      else if (args[i].equals("-i")) {
        inputFilename = args[i + 1];
        i++;
      }
    }
  }

  private static void printHelp() {
    System.out.println("--help: print this");
    System.out.println("-d: input file directory");
    System.out.println("-i: input file path");
  }
}
