public class HuffmanNode implements Comparable<HuffmanNode> {
    private int value;
    private int frequency;
    private HuffmanNode parent;
    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(int value, int frequency) {
        this.value = value;
        this.frequency = frequency;
        parent = null;
        left = null;
        right = null;
    }

    public int getValue() {
        return value;
    }

    public int getFrequency() {
        return frequency;
    }

    public HuffmanNode getParent() {
        return parent;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setParent(HuffmanNode parent) {
        this.parent = parent;
    }

    public void setLeft(HuffmanNode left) {
        this.left = left;
    }

    public void setRight(HuffmanNode right) {
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.getFrequency();
    }
}
