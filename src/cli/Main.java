package cli;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import boring.FileManager;
import fun.DCB;

public class Main
{
  // args
  private static boolean help;
  private static String inputFilename;
  private static int sampleBlendTime;
  private static double randomness;
  private static int function;
  
  public static void main(String... args)
  {
    parseArgs(args);

    if(help == true)
    {
      printHelp();
      return;
    }
    if(inputFilename == null)
    {
      System.out.println("No input filename given! End.");
      return;
    }
    if(!(function >= 0 && function <= 3))
    {
      System.out.println("Invalid function! End.");
      return;
    }
    
    String outputFilename = "processed-" + inputFilename;
    AudioInputStream inAudioStream = null, outputStream = null;
    
    try
    {
      inAudioStream = FileManager.getAudioStreamForFileInWD(inputFilename);
    } catch (UnsupportedAudioFileException e)
    {
      System.out.println("Input audio filetype not supported! End.");
      return;
    } catch (IOException e)
    {
      System.out.println(e.getMessage());
      return;
    }
    
    System.out.println("Processing...");
    
    switch(function)
    {
      case 0:
        outputStream = DCB.shuffle(inAudioStream, sampleBlendTime, randomness);
        break;
        
      case 1: // eyy
        outputStream = DCB.fuck(inAudioStream);
        break;
        
      case 2: // bro this is NOT cool,
      //         cops are gonna come knockin on your door and shit
      // she was givin me the eyes earlier bruv, trust blud...
        outputStream = DCB.rape(inAudioStream);
        break;
        
      case 3:
        outputStream = DCB.reverse(inAudioStream);
        break;
        
      case 4:
        outputStream = DCB.test(inAudioStream);
        break;
    }

    File outFilehandle = FileManager.getFilehandleForNewFile(outputFilename);

    try
    {
      FileManager.write(outputStream, outFilehandle);
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    
    System.out.println("Done. Output file: " + outputFilename);
  }
  
  private static void parseArgs(String... args)
  {
    // TODO: Catch parsing exceptions
    
    function = -1;
    
    for(int i=0;i<args.length;i++)
    {
      if(args[i].equals("--help"))
      {
        help = true;
      } else if(args[i].equals("-i"))
      {
        inputFilename = args[i+1];
      } else if(args[i].equals("-f"))
      {
        function = Integer.parseInt(args[i+1]);
      } else if(args[i].equals("-c")) // number of samples over which to blend slices
      {
        sampleBlendTime = Integer.parseInt(args[i+1]);
      } else if(args[i].equals("-r"))
      {
        randomness = Double.parseDouble(args[i+1]);
      }
    }
  }

  private static void printHelp()
  {
    System.out.println("--help: print this");
    System.out.println("-i: input filename");
    System.out.println("-f: function #:");
    System.out.println("\t0: shuffle");
    System.out.println("\t1: fuck");
    System.out.println("\t2: rape");
    System.out.println("\t3: reverse");
    System.out.println("-c: number of samples over which to blend slices");
    System.out.println("-r: parameter A");
  }
}
