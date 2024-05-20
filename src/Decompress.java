import java.io.*;
import java.util.*;

public class Decompress {
    static class Node implements Serializable {
        private static final long serialVersionUID = 1L;

        int frequency;
        byte data;
        Node left, right;

        public Node(byte data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }

        public Node(int frequency, Node left, Node right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }

    private static Node buildTreeFromStream(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (Node) ois.readObject();
    }

    private static byte[] decode(byte[] encodedBytes, Node huffmanTree) {
        List<Byte> decodedBytes = new ArrayList<>();
        Node current = huffmanTree;
        for (byte b : encodedBytes) {
            String bits = new String(new byte[]{b});
            for (char bit : bits.toCharArray()) {
                if (bit == '0') {
                    current = current.left;
                } else {
                    current = current.right;
                }
                if (current.left == null && current.right == null) {
                    decodedBytes.add(current.data);
                    current = huffmanTree;
                }
            }
        }
        byte[] result = new byte[decodedBytes.size()];
        for (int i = 0; i < decodedBytes.size(); i++) {
            result[i] = decodedBytes.get(i);
        }
        return result;
    }

    public static void decompress(String inputFile, String outputFile) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile));
        Node huffmanTree = buildTreeFromStream(ois);
        byte[] encodedBytes = (byte[]) ois.readObject();
        ois.close();

        byte[] decodedBytes = decode(encodedBytes, huffmanTree);

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(decodedBytes);
        fos.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String inputFile = "compressed.tushka";
        String outputFile = "decompressed.txt";
        decompress(inputFile, outputFile);
        System.out.println("File decompressed successfully.");
    }
}
