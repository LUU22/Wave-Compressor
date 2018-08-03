import java.io.File;

public class WaveCompressor {
    public static void main(String[] args) {
        HuffmanCompressor hc = new HuffmanCompressor();
        LZWCompressor lzwCompressor = new LZWCompressor();
        try {
            FileSelector fs = new FileSelector();
            String path = fs.getPath();
            if (path != null && path.endsWith(".wav")) {
                hc.read(path);
                long startTime = System.currentTimeMillis();
                hc.compress();
                long endTime = System.currentTimeMillis();
                System.out.println("Compression ratio of huffman compressor: " + hc.computeCompressionRatio());
                System.out.println("Running time: " + (float)(endTime - startTime) / 1000 + "s");

                lzwCompressor.read(path);
                startTime = System.currentTimeMillis();
                lzwCompressor.compress();
                endTime = System.currentTimeMillis();
                System.out.println("Compression ratio of LZW compressor: " + lzwCompressor.computeCompressionRatio());
                System.out.println("Running time: " + (float)(endTime - startTime) / 1000 + "s");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
