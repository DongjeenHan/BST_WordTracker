package appDomain;

import implementations.BSTree;
import implementations.BSTreeNode;
import utilities.BSTreeADT;
import utilities.Iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WordTracker {

    private static final String REPO_FILE = "repository.ser";
    private static final Pattern SPLIT_PATTERN = Pattern.compile("[^a-zA-Z0-9']");

    private BSTreeADT<WordData> bst;

    public WordTracker() {
        bst = loadRepository();
    }

    @SuppressWarnings("unchecked")
    private BSTreeADT<WordData> loadRepository() {
        File repo = new File(REPO_FILE);
        if (repo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(repo))) {
                return (BSTreeADT<WordData>) ois.readObject();
            } catch (Exception e) {
                System.err.println("Failed to load repository: " + e.getMessage());
            }
        }
        return new BSTree<WordData>();
    }

    private void saveRepository() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPO_FILE))) {
            oos.writeObject(bst);
        } catch (IOException e) {
            System.err.println("Failed to save repository: " + e.getMessage());
        }
    }

    private void processFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                String[] words = SPLIT_PATTERN.matcher(line.toLowerCase()).replaceAll(" ").split("\\s+");
                for (String w : words) {
                    if (w.isEmpty()) continue;
                    WordData probe = new WordData(w);
                    BSTreeNode<WordData> node = bst.search(probe);
                    if (node == null) {
                        probe.addOccurrence(file.getName(), lineNum);
                        bst.add(probe);
                    } else {
                        node.getElement().addOccurrence(file.getName(), lineNum);
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void printWords(String mode, PrintStream out) {
        List<WordData> words = new ArrayList<WordData>();
        Iterator<WordData> it = bst.inorderIterator();
        while (it.hasNext()) {
            words.add(it.next());
        }
        for (WordData wd : words) {
            if ("-pf".equals(mode)) {
                out.println(wd.getWord() + " : " + wd.getFiles());
            } else if ("-pl".equals(mode)) {
                out.println(wd.getWord() + " : " + wd.getOccurrences());
            } else if ("-po".equals(mode)) {
                out.println(wd.getWord() + " : " + wd.getOccurrencesWithCount());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f<output.txt>]");
            return;
        }

        WordTracker wt = new WordTracker();
        wt.processFile(args[0]);

        PrintStream out = System.out;
        if (args.length >= 3 && args[2].startsWith("-f")) {
            try {
                out = new PrintStream(new FileOutputStream(args[2].substring(2)));
            } catch (FileNotFoundException e) {
                System.err.println("Unable to create output file.");
            }
        }

        wt.printWords(args[1], out);
        wt.saveRepository();
    }
}
