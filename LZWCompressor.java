import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class LZWCompressor implements Compressor {
    private HashMap<HashableArray, Integer> dic;
    private double compressionRatio;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private int[] samples; //data
    private int sampleCount; //the number of samples
    private int fileSize;
    private int compressedFileSize = 0;

    @Override
    public int twoBytesToInt(byte[] bytes, int offset) {
        return ((int)(bytes[offset]) << 8) | (bytes[offset - 1] & 0xFF);
    }

    @Override
    public int fourBytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) | ((bytes[offset - 1] & 0xFF) << 16) |
                ((bytes[offset - 2] & 0xFF) << 8) | (bytes[offset - 3] & 0xFF);
    }

    @Override
    public void read(String fileName) throws IOException {
        fis = new FileInputStream(fileName);
        bis = new BufferedInputStream(fis);

        byte[] header = new byte[44]; //the first 44 bytes are the header
        bis.read(header, 0, 44);

        int sampleSize = twoBytesToInt(header, 35); //the number of bits per sample
        int dataSize = fourBytesToInt(header, 43); //the number of bytes of the data area
        fileSize = dataSize + 44;

        sampleCount = dataSize / (sampleSize / 8);

        samples = new int[sampleCount];

        if(sampleSize == 8) { //1 byte per sample
            for(int i = 0; i < sampleCount; i++) {
                samples[i] = bis.read();
            }
        } else { //2 bytes per sample
            byte[] temp = new byte[2];
            for(int i = 0; i < sampleCount; i++) {
                bis.read(temp);
                samples[i] = twoBytesToInt(temp, 1);
            }
        }

        fis.close();
        bis.close();
    }

    @Override
    public void compress() {
        if(samples.length == 0) {
            compressedFileSize += 44;
            return;
        }

        dic = new HashMap<>();
        int code = Integer.MIN_VALUE;

        for(int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++) {
            dic.put(new HashableArray(i), code);
            code++;
        }

        HashableArray array = new HashableArray(samples[0]);

        for (int i = 1; i < samples.length; i++) { //LZW algorithm
            int sample = samples[i];
            HashableArray temp = array.append(sample);

            if (dic.containsKey(temp)) {
                array.add(sample);
            } else {
                compressedFileSize++; // output the code for array
                dic.put(temp, code);
                code++;
                if (code == Integer.MAX_VALUE) {
                    System.out.println("Warning: code length deficient!");
                }
                array = new HashableArray(sample);
            }
        }
        compressedFileSize++; // output the code for array
        compressedFileSize = compressedFileSize * 4 + 44; // Use int as code type. So the code length is 4 bytes
    }

    @Override
    public double computeCompressionRatio() {
        compressionRatio = fileSize / (compressedFileSize * 1.0);
        return compressionRatio;
    }
}
