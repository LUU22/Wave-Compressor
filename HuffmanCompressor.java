import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class HuffmanCompressor implements Compressor {
    private double compressionRatio;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private int[] samples; //data
    private int sampleCount; //the number of samples
    private int fileSize;
    private HashMap<Integer, Integer> frequencyMap = new HashMap<>();
    private HuffmanNode root;

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
                if(!frequencyMap.containsKey(samples[i])) {
                    frequencyMap.put(samples[i], 1);
                } else {
                    frequencyMap.put(samples[i], frequencyMap.get(samples[i]) + 1);
                }
            }
        } else { //2 bytes per sample
            byte[] temp = new byte[2];
            for(int i = 0; i < sampleCount; i++) {
                bis.read(temp);
                samples[i] = twoBytesToInt(temp, 1);
                if(!frequencyMap.containsKey(samples[i])) {
                    frequencyMap.put(samples[i], 1);
                } else {
                    frequencyMap.put(samples[i], frequencyMap.get(samples[i]) + 1);
                }
            }
        }

        fis.close();
        bis.close();
    }

    @Override
    public void compress() {
        LinkedList<HuffmanNode> nodes = new LinkedList<>();

        for(Map.Entry<Integer, Integer> entry: frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            nodes.add(node);
        }

        Collections.sort(nodes);
        int nodeCount = nodes.size();

        for(int i = 1; i < nodeCount; i++) { //build huffman tree
            HuffmanNode node1 = nodes.removeFirst(); //the node with smallest frequency
            HuffmanNode node2 = nodes.removeFirst(); //the node with 2nd smallest frequency

            //create the parent of these 2 nodes
            HuffmanNode node = new HuffmanNode(Integer.MIN_VALUE, node1.getFrequency() + node2.getFrequency());
            node1.setParent(node);
            node2.setParent(node);
            node.setLeft(node1);
            node.setRight(node2);

            int j;
            for(j = 0; j < nodeCount - i - 1; j++) { //insert the newly created node into the list
                if(node.getFrequency() <= nodes.get(j).getFrequency()) {
                    break;
                }
            }

            nodes.add(j, node);
        }

        root = nodes.getFirst(); // set the root of Huffman tree
    }

    @Override
    public double computeCompressionRatio() {
        if(root == null) {
            return 0.0;
        }

        Queue<HuffmanNode> queue1 = new LinkedList<>();
        Queue<HuffmanNode> queue2 = new LinkedList<>();
        long depth = 0;
        long bitsCounter = 0; //total # of bits needed

        queue1.offer(root);
        while(!queue1.isEmpty() || !queue2.isEmpty()) { // bread-first traverse the huffman tree
            while (!queue1.isEmpty()) {
                HuffmanNode node = queue1.poll();

                if(node.getLeft() == null && node.getRight() == null) {
                    bitsCounter += depth * node.getFrequency();
                } else {
                    queue2.offer(node.getLeft());
                    queue2.offer(node.getRight());
                }
            }

            depth++;

            while (!queue2.isEmpty()) {
                HuffmanNode node = queue2.poll();

                if(node.getLeft() == null && node.getRight() == null) {
                    bitsCounter += depth * node.getFrequency();
                } else {
                    queue1.offer(node.getLeft());
                    queue1.offer(node.getRight());
                }
            }

            depth++;
        }

        compressionRatio = fileSize * 1.0 / (bitsCounter / 8.0 + 44);

        return compressionRatio;
    }
}
