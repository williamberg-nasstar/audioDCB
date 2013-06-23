package cli;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import util.FileManager;

import fun.DCB;

public class Main {

  // args
  private static boolean help;
  private static String inputFilename;
  private static int sampleBlendTime;
  private static double randomness;

  public static void main(String... args) {
    parseArgs(args);

    if (help == true) {
      printHelp();
      return;
    }
    if (inputFilename == null) {
      System.out.println("No input filename given! End.");
      return;
    }

    String outputFilename = "processed-" + inputFilename;
    AudioInputStream inAudioStream = null, outputStream = null;

    try {
      inAudioStream = FileManager.getAudioStreamForFileInWD(inputFilename);
    }
    catch (UnsupportedAudioFileException e) {
      System.out.println("Input audio filetype not supported! End.");
      return;
    }
    catch (IOException e) {
      System.out.println(e.getMessage());
      return;
    }

    System.out.println("Processing...");

    outputStream = DCB.shuffle(inAudioStream, sampleBlendTime, randomness);

    File outFilehandle = FileManager.getFilehandleForNewFile(outputFilename);

    try {
      FileManager.write(outputStream, outFilehandle);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Done. Output file: " + outputFilename);
  }

  private static void parseArgs(String... args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--help")) {
        help = true;
      }
      else if (args[i].equals("-i")) {
        inputFilename = args[i + 1];
      }
      else if (args[i].equals("-c")) // number of samples over which to blend
                                     // slices
      {
        sampleBlendTime = Integer.parseInt(args[i + 1]);
      }
      else if (args[i].equals("-r")) {
        randomness = Double.parseDouble(args[i + 1]);
      }
    }
  }

  private static void printHelp() {
    System.out.println("--help: print this");
    System.out.println("-i: input filename");
    System.out.println("-c: number of samples over which to blend slices");
    System.out.println("-r: parameter A");
  }
}
