package appDomain;

import java.io.Serializable;
import java.util.*;

public class WordData implements Comparable<WordData>, Serializable {
    private static final long serialVersionUID = 1L;

    private String word;
    private Map<String, List<Integer>> occurrences;

    public WordData(String word) {
        this.word = word;
        this.occurrences = new TreeMap<>();
    }

    public String getWord() {
        return word;
    }

    public void addOccurrence(String fileName, int lineNumber) {
        List<Integer> lines = occurrences.computeIfAbsent(fileName, k -> new ArrayList<>());
        if (!lines.contains(lineNumber)) {
            lines.add(lineNumber);
        }
    }

    public Set<String> getFiles() {
        return occurrences.keySet();
    }

    public Map<String, List<Integer>> getOccurrences() {
        return occurrences;
    }

    public Map<String, String> getOccurrencesWithCount() {
        Map<String, String> result = new TreeMap<>();
        for (String file : occurrences.keySet()) {
            List<Integer> lines = new ArrayList<>(occurrences.get(file));
            Collections.sort(lines);
            result.put(file, lines + " (count=" + lines.size() + ")");
        }
        return result;
    }

    @Override
    public int compareTo(WordData other) {
        return this.word.compareTo(other.word);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WordData) {
            return this.word.equals(((WordData) obj).word);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return word;
    }
}
