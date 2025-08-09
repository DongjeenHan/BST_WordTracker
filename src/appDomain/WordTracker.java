package appDomain;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import utilities.Iterator;

/**
 * WordTracker reads a text file, stores words in a BST, and records
 * their frequency and line numbers.
 */
public class WordTracker {

    private BSTree<Word> wordTree;

    public WordTracker() {
        wordTree = new BSTree<>();
    }

    /**
     * Reads a file and populates the BST with words and their line numbers.
     */
    public void processFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\W+");
                for (String token : tokens) {
                    if (!token.isEmpty()) {
                        addWord(token.toLowerCase(), lineNumber);
                    }
                }
                lineNumber++;
            }
        }
    }

    /**
     * Adds a word to the BST or updates an existing word's data.
     */
    private void addWord(String word, int lineNumber) {
        Word tempWord = new Word(word);
        BSTreeNode<Word> existingNode = wordTree.search(tempWord);

        if (existingNode == null) {
            tempWord.addOccurrence(lineNumber);
            wordTree.add(tempWord);
        } else {
            existingNode.getElement().addOccurrence(lineNumber);
        }
    }

    /**s
     * Writes the contents of the BST to a file in alphabetical order.
     */
    public void writeReport(String outputFilename) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputFilename)) {
            Iterator<Word> it = wordTree.inorderIterator();
            while (it.hasNext()) {
                writer.println(it.next());
            }
        }
    }

    /**
     * Simple inner class to store word data.
     */
    public static class Word implements Comparable<Word> {
        private String text;
        private int count;
        private java.util.TreeSet<Integer> lineNumbers;

        public Word(String text) {
            this.text = text;
            this.count = 0;
            this.lineNumbers = new java.util.TreeSet<>();
        }

        public void addOccurrence(int lineNumber) {
            count++;
            lineNumbers.add(lineNumber);
        }

        @Override
        public int compareTo(Word other) {
            return this.text.compareTo(other.text);
        }

        @Override
        public String toString() {
            return text + " (" + count + "): " + lineNumbers;
        }
    }

    /**
     * Example usage.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java implementations.WordTracker <inputfile> <outputfile>");
            return;
        }

        WordTracker tracker = new WordTracker();
        try {
            tracker.processFile(args[0]);
            tracker.writeReport(args[1]);
            System.out.println("Word report generated: " + args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
