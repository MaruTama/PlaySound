//明治大学 理工学部 数学科 一木裕貴
//$ java FFT4gRead 音声ファイル.wav テキストファイル.txt
import java.io.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.sound.sampled.*;
public class wavRead {
  protected static final int EXTERNAL_BUFFER_SIZE = 128000;
  public static void test(String args[]){
    int frameSize;
    int sampleSizeInBits;
    int channels;
    float sampleRate;
    boolean isStereo;
    boolean isBigEndian;
    short [] a1, a2;
    int n, M, count, point, point1;
    double wa, m;
    double [] b;

    //音声データの配列
    a1 = new short [1280000];
    a2 = new short [1280000];
    //2^16
    M = 65536;
    count = 0;
    b = new double [M];
    if (args.length == 0) System.exit(0);
    try {
      File soundFile = new File(args[0]);

      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      AudioFormat audioFormat = audioInputStream.getFormat();

      DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
      SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(audioFormat);
      line.start();

      channels = audioFormat.getChannels();
      isStereo = (channels == 2);
      isBigEndian = audioFormat.isBigEndian();
      frameSize = audioFormat.getFrameSize();
      sampleSizeInBits = audioFormat.getSampleSizeInBits();
      sampleRate = audioFormat.getSampleRate();
      System.out.println("#original file: " + args[0]);
      System.out.println("#number of channels: " + channels);
      System.out.println("#sampling rate: " + sampleRate);
      System.out.println("#number of bits per sample: " + sampleSizeInBits);
      System.out.println("#FrameSize: " + frameSize);
      System.out.println("#isBigEndian: " + isBigEndian);
      if (audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
        System.out.println("#PCM Signed");
      }
      else if (audioFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
        System.out.println("#PCM Unsigned!!!");
        System.exit(0);
      }
      else {
        System.out.println("#NO PCM!!\n");
        System.exit(0);
      }

      int nBytesRead = 0;
      byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
      if (isStereo) {
        if (sampleSizeInBits == 16) {
          while (nBytesRead != -1) {
            //オーディオストリームから指定されたデータの最大バイト数まで読み込み、
            //読み込んだバイトを指定されたバイト配列に格納します。
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              int nBytesWritten = line.write(abData, 0, nBytesRead);
              for (int i = 0; i < nBytesRead; i += 4) {
                short left, right;
                ++count;
                left = (short)(abData[i ] & 0xff | (abData[i+1] << 8));
                right = (short)(abData[i+2] & 0xff | (abData[i+3] << 8));
                a1[count] = left;
                a2[count] = right;
                System.out.printf("a count:%s, left:%s%n", count, left);
              }
            }
          }
        }
        else {
          while (nBytesRead != -1) {
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              int nBytesWritten = line.write(abData, 0, nBytesRead);
              for (int i = 0; i < nBytesRead; i += 2) {
                short left, right;
                ++count;
                left = (short)(abData[i] & 0xff);
                right = (short)(abData[i+1] & 0xff);
                a1[count] = left;
                a2[count] = right;
                System.out.printf("b count:%s, left:%s%n", count, left);
              }
            }
          }
        }
      }
      else {
        if (sampleSizeInBits == 16) {
          while (nBytesRead != -1) {
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              int nBytesWritten = line.write(abData, 0, nBytesRead);
              for (int i = 0; i < nBytesRead; i += 2) {
                short c = (short)(abData[i] &0xff | (abData[i+1] << 8));
                ++count;
                a1[count] = c;
                System.out.printf("c count:%s, left:%s%n", count, c);
              }
              count = 0;
            }
          }
        }
        else {
          while (nBytesRead != -1) {
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              int nBytesWritten = line.write(abData, 0, nBytesRead);
              for (int i = 0; i < nBytesRead; i++) {
                short c;
                ++count;
                c = (short)(abData[i] & 0xff);
                a1[count] = c;
                System.out.printf("d count:%s, left:%s%n", count, c);
              }
              count = 0;
            }
          }
        }
      }
      line.drain();
      line.close();

      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  //窓関数(ハミング窓関数)
  //http://rest-term.com/archives/1872/
  public double[] window_function(double data[], int inv){
    double d = 0.0;
    int len = data.length;
    // hamming window
    for(int i=0; i<len; i++) {
      d = 0.54 - 0.46*Math.cos(2*Math.PI*i/(len - 1));
      if(inv == 1) data[i] *= d;
      else data[i] /= d;
    }
    return data;
  }

  public static void main(String[] args){
    test(args);
  }
}
