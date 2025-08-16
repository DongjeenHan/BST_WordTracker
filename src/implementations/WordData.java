package implementations;

import java.io.Serializable;
import java.util.*;

/**
 * Stores data associated with a word found in text files, including
 * the files and line numbers where it occurs, and its total frequency.
 * Implements Comparable based on the word itself (case-insensitive)
 * and Serializable for storing in the repository.
 */
public class WordData implements Comparable<WordData>, Serializable {

    private static final long serialVersionUID = 20240425L;

    private final String word;
    // Key = filename, Value = sorted set of line numbers
    private final Map<String, Set<Integer>> locations;
    private int frequency;

    /**
     * Constructs WordData for a given word.
     *
     * @param word The word this object represents (converted to lowercase).
     * @throws NullPointerException if the word is null.
     */
    public WordData(String word) {
        if (word == null) {
            throw new NullPointerException("Word cannot be null.");
        }
        this.word = word.toLowerCase();
        this.locations = new HashMap<>();
        this.frequency = 0;
    }

    public String getWord() {
        return word;
    }

    /**
     * Adds an occurrence of the word from a specific file and line number.
     *
     * @param filename   The name of the file where the word occurred.
     * @param lineNumber The line number where the word occurred.
     */
    public void addOccurrence(String filename, int lineNumber) {
        locations.computeIfAbsent(filename, k -> new TreeSet<>()).add(lineNumber);
        frequency++;
    }

    /**
     * Returns an unmodifiable map of filenames to their line numbers.
     */
    public Map<String, Set<Integer>> getLocations() {
        return Collections.unmodifiableMap(locations);
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(WordData other) {
        return this.word.compareToIgnoreCase(other.word);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordData)) return false;
        WordData other = (WordData) o;
        return this.word.equalsIgnoreCase(other.word);
    }

    @Override
    public int hashCode() {
        return word.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return word + " (" + frequency + "): " + locations;
    }
}
