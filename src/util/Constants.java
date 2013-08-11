package util;


public class Constants {

  public static byte[] RIFF_TYPE_CHUNK_ID_FIELD = new byte[] {
    // "RIFF"
    // 82, 73, 70, 70
    0x52, 0x49, 0x46, 0x46
  };

  public static byte[] RIFF_TYPE_CHUNK_TYPE_FIELD = new byte[] {
    // "WAVE"
    // 82, 73, 70, 70
    0x57, 0x41, 0x56, 0x45
  };

  public static byte[] FORMAT_CHUNK_NAME_FIELD = new byte[] {
    // "fmt "
    0x66, 0x6D, 0x74, 0x20
  };

  public static byte[] DATA_CHUNK_NAME_FIELD = new byte[] {
    // "data"
    0x64, 0x61, 0x74, 0x61
  };

  // fact, wavl, slnt, cue , plst, list, labl, note, ltxt, smpl, inst

}
