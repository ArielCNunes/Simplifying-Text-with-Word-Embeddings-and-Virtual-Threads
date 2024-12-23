package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles all operations related to loading external data into our program
 */
public class DataLoadingManager {
    // Thread-safe hash map to store words and embeddings
    final ConcurrentHashMap<String, double[]> wordEmbeddings = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, double[]> googleWords = new ConcurrentHashMap<>();

    /**
     * This method reads in the words and the embeddings from the .txt file
     */
    public void loadWordEmbeddings(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Loop through .txt file
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");

                // Get words and embeddings
                String word = parts[0];
                double[] embeddings = new double[parts.length - 1];

                // Put embeddings into double array
                for (int i = 1; i < parts.length; i++) {
                    embeddings[i - 1] = Double.parseDouble(parts[i]);
                }

                // Add words and embeddings to map
                wordEmbeddings.put(word, embeddings);
            }

            // Confirmation
            System.out.println("Total words loaded: " + wordEmbeddings.size());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * This method reads in the Google-1000 words
     */
    public void loadGoogle1000Words(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // We only want the words that have embeddings
                if (wordEmbeddings.containsKey(line)) {
                    googleWords.put(line, wordEmbeddings.get(line));
                }
            }
            System.out.println("Google-1000 words loaded: " + googleWords.size());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}