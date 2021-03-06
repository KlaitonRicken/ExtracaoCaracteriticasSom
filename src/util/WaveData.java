package util; 

import java.io.IOException;  
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

	public class WaveData {  
		
		private byte[] entireFileData;
		public String info = "";

	    //SR = sampling rate
	    public double getSR(){
	        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(entireFileData, 24, 28)); // big-endian by default
	        double SR = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
	        return SR;
	    }

	    public WaveData(String filepath, boolean print_info) throws IOException{
	        Path path = Paths.get(filepath);
	        this.entireFileData = Files.readAllBytes(path);

	        if (print_info){

	        //extract format
	        String format = new String(Arrays.copyOfRange(entireFileData, 8, 12), "UTF-8");

	        //extract number of channels
	        int noOfChannels = entireFileData[22];
	        String noOfChannels_str;
	        if (noOfChannels == 2)
	            noOfChannels_str = "2 (stereo)";
	        else if (noOfChannels == 1)
	            noOfChannels_str = "1 (mono)";
	        else
	            noOfChannels_str = noOfChannels + "(more than 2 channels)";

//	        extract sampling rate (SR)
	        int SR = (int) this.getSR();

	        //extract Bit Per Second (BPS/Bit depth)
	        int BPS = entireFileData[34];
	        
	        info += "File path:          " + filepath + "\n" +
	        		"File format:        " + format + "\n" +
					"Number of channels: " + noOfChannels_str + "\n" +
					"Sampling rate:      " + SR + "\n" +
					"Bit depth:          " + BPS + "\n-------------------------------------- \n";
	        }
	    }

	    public double[] getByteArray (){
	        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
	        int totalLength = data_raw.length;

	        //declare double array for mono
	        int new_length = totalLength/4;
	        double[] data_mono = new double[new_length];

	        double left, right;
	        for (int i = 0; 4*i+3 < totalLength; i++){
	        	  left = (short)((data_raw[4*i+1] & 0xff) << 8) | (data_raw[4*i] & 0xff);
	        	  right = (short)((data_raw[4*i+3] & 0xff) << 8) | (data_raw[4*i+2] & 0xff);
	        	  data_mono[i] = (left+right)/2.0;
	        	}       
	        return data_mono;
	    }
}
