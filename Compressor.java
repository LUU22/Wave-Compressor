import java.io.IOException;

public interface Compressor {
    int twoBytesToInt(byte[] bytes, int offset);

    int fourBytesToInt(byte[] bytes, int offset);

    void read(String fileName) throws IOException;

    void compress();

    double computeCompressionRatio();
}
