package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * This class handles all operations related to loading external data into our program
 */
public class DataLoadingManager {
    // Thread-safe hash map to store words and embeddings
    final ConcurrentHashMap<String, double[]> wordEmbeddings = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, double[]> googleWords = new ConcurrentHashMap<>();

    /**
     * This method reads in the words and vectors from the embeddings.txt file
     *
     * @param fileName The path to the text file containing word embeddings.
     * @throws IOException If there is an error reading the file.
     */
    public void loadWordEmbeddings(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             // Virtual thread
             var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            String line;
            while ((line = reader.readLine()) != null) {
                final String entry = line;
                executor.submit(() -> {
                    String[] parts = entry.split(", ");
                    String word = parts[0].trim().toLowerCase();
                    double[] embeddings = new double[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        embeddings[i - 1] = Double.parseDouble(parts[i]);
                    }
                    wordEmbeddings.put(word, embeddings); // Add to map
                });
            }
            // Wait for all virtual threads to complete
            executor.shutdown();
            executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);

            // Confirmation
            System.out.println("Total words loaded: " + wordEmbeddings.size());
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }

    /**
     * This method reads in the words from the google-1000.txt file
     *
     * @param fileName The path to the file containing the Google-1000 word list.
     * @throws IOException If there is an error reading the file.
     */
    public void loadGoogle1000Words(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             // Virtual threads
             var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            String line;
            while ((line = reader.readLine()) != null) {
                final String word = line.trim().toLowerCase();
                executor.submit(() -> {
                    // Only add words that have embeddings
                    if (wordEmbeddings.containsKey(word)) {
                        googleWords.put(word, wordEmbeddings.get(word));
                    } else {
                        System.out.println("Word " + word + " not found");
                    }
                });
            }
            // Wait for all virtual threads to complete
            executor.shutdown();
            executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);

            System.out.println("Google-1000 words loaded: " + googleWords.size());
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }
}