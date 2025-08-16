/**
 * Group 1
 * Student Names: Alaa Alhaj, Dongjeen Han, Mathew Brown, Mehtab Singh
 * Course: CPRG 304
 * Assignment: Assignment 3
 * Date: Aug 15, 2025
 * 
 * Description: This program reads text files, stores each unique word in a 
 * binary search tree, and tracks the files and lines where the words appear.
 */


package appDomain;


import implementations.BSTree;
import implementations.BSTreeNode;
import utilities.Iterator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class WordTracker {

    private static final String REPO_NAME = "repository.ser";

    private BSTree<WordEntry> tree;

    public static void main(String[] args) {
        WordTracker wt = new WordTracker();
        wt.start(args);
    }

    private void start(String[] args) {
        if (args == null || args.length < 2 || args.length > 3) {
            printUsage("invalid arguments count");
            return;
        }

        String inputPath = args[0];
        String mode = args[1];
        String outFile = null;

        if (!mode.equals("-pf") && !mode.equals("-pl") && !mode.equals("-po")) {
            printUsage("2nd arg must be -pf or -pl or -po");
            return;
        }

        if (args.length == 3) {
            String third = args[2];
            if (!third.startsWith("-f")) {
                printUsage("3rd arg must be -f<output.txt>");
                return;
            }
            outFile = third.substring(2);
            if (outFile.trim().isEmpty()) {
                printUsage("missing output file name after -f");
                return;
            }
        }

        tree = loadRepo();

        try {
            scanFile(inputPath);
        } catch (IOException e) {
            System.err.println("failed to read file: " + e.getMessage());
            return;
        }

        try {
            saveRepo();
        } catch (IOException e) {
            System.err.println("failed to save repo: " + e.getMessage());
        }

        String report = buildReport(mode);

        if (outFile == null) {
            System.out.print(report);
        } else {
            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outFile), StandardCharsets.UTF_8)) {
                bw.write(report);
            } catch (IOException e) {
                System.err.println("failed to write output file: " + e.getMessage());
            }
        }
    }

    private void printUsage(String msg) {
        System.err.println("Error: " + msg);
        System.err.println("Usage: java -jar WordTracker.jar <input.txt> -pf|-pl|-po [-f<output.txt>]");
    }

    @SuppressWarnings("unchecked")
    private BSTree<WordEntry> loadRepo() {
        File f = new File(REPO_NAME);
        if (!f.exists()) {
            return new BSTree<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            return (BSTree<WordEntry>) obj;
        } catch (Exception e) {
            return new BSTree<>();
        }
    }

    private void saveRepo() throws IOException {
        File f = new File(REPO_NAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(tree);
        }
        System.out.println("Saved repo to: " + f.getAbsolutePath());
    }

    private void scanFile(String inputPath) throws IOException {
        String fileNameOnly = new File(inputPath).getName();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(inputPath), StandardCharsets.UTF_8)) {
            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;

                String[] tokens = line.split("\\s+");
                for (String token : tokens) {
                    if (token == null || token.length() == 0) continue;

                    String clean = token.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9']", "");
                    if (clean.length() == 0) continue;

                    addOccurrence(clean, fileNameOnly, lineNum);
                }
            }
        }
    }

    private void addOccurrence(String word, String file, int lineNum) {
        WordEntry look = new WordEntry(word);
        BSTreeNode<WordEntry> node = tree.search(look);

        if (node == null) {
            WordEntry we = new WordEntry(word);
            we.add(file, lineNum);
            tree.add(we);
        } else {
            node.getElement().add(file, lineNum);
        }
    }

    private String buildReport(String mode) {
        StringBuilder sb = new StringBuilder();

        Iterator<WordEntry> it = tree.inorderIterator();
        while (it.hasNext()) {
            WordEntry w = it.next();

            List<String> files = new ArrayList<>(w.map.keySet());
            Collections.sort(files);

            if (mode.equals("-pf")) {
                sb.append(w.word).append(": ");
                for (int i = 0; i < files.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(files.get(i));
                }
                sb.append(System.lineSeparator());
            } else if (mode.equals("-pl")) {
                sb.append(w.word).append(":").append(System.lineSeparator());
                for (String f : files) {
                    List<Integer> lines = new ArrayList<>(w.map.get(f));
                    Collections.sort(lines);
                    sb.append("  ").append(f).append(" [lines: ");
                    for (int i = 0; i < lines.size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(lines.get(i));
                    }
                    sb.append("]").append(System.lineSeparator());
                }
            } else if (mode.equals("-po")) {
                int total = w.total();
                sb.append(w.word).append(" (total: ").append(total).append("):").append(System.lineSeparator());
                for (String f : files) {
                    List<Integer> lines = new ArrayList<>(w.map.get(f));
                    Collections.sort(lines);
                    sb.append("  ").append(f).append(" [lines: ");
                    for (int i = 0; i < lines.size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(lines.get(i));
                    }
                    sb.append("] (freq: ").append(lines.size()).append(")").append(System.lineSeparator());
                }
            }
        }

        return sb.toString();
    }

    public static class WordEntry implements Serializable, Comparable<WordEntry> {
        private static final long serialVersionUID = 2L;

        private String word;
        private Map<String, List<Integer>> map;

        public WordEntry(String w) {
            this.word = w;
            this.map = new HashMap<>();
        }

        public void add(String file, int line) {
            List<Integer> lines = map.get(file);
            if (lines == null) {
                lines = new ArrayList<>();
                map.put(file, lines);
            }
            lines.add(line);
        }

        public int total() {
            int c = 0;
            for (List<Integer> lines : map.values()) {
                c += lines.size();
            }
            return c;
        }

        @Override
        public int compareTo(WordEntry other) {
            if (other == null) return 1;
            return this.word.compareToIgnoreCase(other.word);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WordEntry)) return false;
            WordEntry that = (WordEntry) o;
            return Objects.equals(this.word, that.word);
        }

        @Override
        public int hashCode() {
            return Objects.hash(word);
        }
    }
}
