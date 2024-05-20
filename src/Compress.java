import java.io.*;
import java.util.*;

public class Compress {
    static class Node implements Comparable<Node>, Serializable {
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

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    private static Node buildTree(Map<Byte, Integer> frequencyMap) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node(left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    private static void generateCodes(Node node, String code, Map<Byte, String> huffmanCodes) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.data, code);
        }
        generateCodes(node.left, code + '0', huffmanCodes);
        generateCodes(node.right, code + '1', huffmanCodes);
    }

    public static void compress(String inputFile, String outputFile) throws IOException {
        FileInputStream fis = new FileInputStream(inputFile);
        byte[] fileBytes = fis.readAllBytes();
        fis.close();

        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : fileBytes) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }

        Node huffmanTree = buildTree(frequencyMap);
        Map<Byte, String> huffmanCodes = new HashMap<>();
        generateCodes(huffmanTree, "", huffmanCodes);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte b : fileBytes) {
            baos.write(huffmanCodes.get(b).getBytes());
        }

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
        oos.writeObject(huffmanTree);
        oos.writeObject(baos.toByteArray());
        oos.close();
    }

    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String outputFile = "compressed.tushka";
        compress(inputFile, outputFile);
        System.out.println("File compressed successfully.");
    }
}
