import javax.sound.sampled.*;
import java.io.*;

public class WAV_test{

  int frameSize;
  int sampleSizeInBits;
  int channels;
  float sampleRate;
  boolean isStereo;
  boolean isBigEndian;

  public static void main(String[] args) {
     playWavFile("cider.wav");
     WriteWAV();
  }

  public static void playWavFile(String fileName){
    int count =0;
    short [] a1, a2;
    //音声データの配列
    a1 = new short [1280000];
    a2 = new short [1280000];
        try{
           File file=new File(fileName);
           if(file.exists()){
               // Read the sound file using AudioInputStream.
               AudioInputStream stream = AudioSystem.getAudioInputStream(file);
               byte[] buf = new byte[stream.available()];
               stream.read(buf,0,buf.length);

               // Get an AudioFormat object from the stream.
               AudioFormat format = stream.getFormat();
               long nBytesRead = format.getFrameSize()*stream.getFrameLength();

               // Construct a DataLine.Info object from the format.
               DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
               SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);

               // Open and start the line.
               line.open(format);
               line.start();

               // Write the data out to the line.
               int nBytesWritten =  line.write(buf,0,(int)nBytesRead);
               for (int i = 0; i < nBytesRead; i += 2) {
                 short left, right;
                 ++count;
                 left  = (short)(buf[i  ] & 0xff | (buf[i+1] << 8));
                 right = (short)(buf[i+2] & 0xff | (buf[i+3] << 8));
                 a1[count] = left;
                 a2[count] = right;
                 System.out.printf("a count:%s, left:%s%n", count, left);
               }

               // Drain and close the line.
               line.drain();
               line.close();
            }
         }catch(Exception e){e.printStackTrace();}

  }
  public static void WriteWAV(){
    try{
       byte[] wave_data = new byte[44100*2];
       byte[] pcm_data  = new byte[wave_data.length];
       double L1        = 44100.0/440.0;
       double L2        = 44100.0/455.0;
       for(int i=0;i<wave_data.length;i++){
          pcm_data[i] = (byte)(55*Math.sin((i/L1)*Math.PI*2));
          pcm_data[i]+= (byte)(55*Math.sin((i/L2)*Math.PI*2));
       }
       /*引数(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian)
          Constructs an AudioFormat with a linear PCM encoding and the given parameters)*/
       AudioFormat      frmt= new AudioFormat(44100,16,2,true,false);
       //System.out.printf("%s%n", AudioFormat.getChannels());
       AudioInputStream ais = new AudioInputStream(
                  new ByteArrayInputStream(pcm_data)
                 ,frmt
                 ,pcm_data.length);
       AudioSystem.write(
                  ais
                 ,AudioFileFormat.Type.WAVE
                 ,new File("test.wav"));
    }
    catch(Exception e){e.printStackTrace(System.err);}
  }
}
