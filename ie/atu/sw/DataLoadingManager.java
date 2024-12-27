package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * This class handles all operations related to loading external data into our program.
 */
public class DataLoadingManager {
    // Thread-safe hash map to store words and embeddings
    final ConcurrentHashMap<String, double[]> wordEmbeddings = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, double[]> googleWords = new ConcurrentHashMap<>();

    /**
     * This method reads in the words and vectors concurrently from the embeddings.txt file into memory.
     *
     * <p><b>Big-O Notation is O(n)</b> -> The method uses virtual threads to be more efficient but the task itself is still
     * dependent on how big the .txt file is. The more lines the longer it should take. For each line we loop through all
     * embeddings.</p>
     *
     * @param fileName The path to the text file containing word embeddings.
     * @throws IOException          If there is an error reading the file.
     * @throws InterruptedException If thread execution is interrupted during processing.
     */
    public void loadWordEmbeddings(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             // Virtual thread
             var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            String line;
            while ((line = reader.readLine()) != null) {
                final String entry = line;

                // Do this for every line
                executor.submit(() -> {
                    // Separate items by ", "
                    String[] parts = entry.split(", ");

                    // The word is always going to be at index 0
                    String word = parts[0].trim().toLowerCase();

                    // Array to store embeddings
                    double[] embeddings = new double[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        embeddings[i - 1] = Double.parseDouble(parts[i]);
                    }

                    // Add the word and its embeddings to map
                    wordEmbeddings.put(word, embeddings);
                });
            }

            // Wait for all virtual threads to complete
            executor.shutdown();
            executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);

            // Number of lines read in
            System.out.println("Total words loaded: " + wordEmbeddings.size());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    /**
     * Loads words from the Google-1000 word list into memory using virtual threads for concurrency.
     *
     * <p>This method reads each line from the google-1000.txt file, trims and converts the word to lowercase,
     * and checks if an embedding exists for it in the wordEmbeddings map. Words with embeddings are added to
     * the googleWords map along with their vectors.</p>
     *
     * <p><b>Big-O Notation is O(n)</b> -> The method also uses virtual threads to be more efficient but the running time
     * will depend on how many words are int the file. The overall running time ends up being O(n) but the map look up
     * is O(1).</p>
     *
     * @param fileName The path to the file containing the Google-1000 word list.
     * @throws IOException If an error occurs while reading the file.
     */
    public void loadGoogle1000Words(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             // Virtual thread
             var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            String line;
            while ((line = reader.readLine()) != null) {
                final String word = line.trim().toLowerCase();

                // Do this for each line/word
                executor.submit(() -> {
                    // Check if google-1000 word has corresponding embeddings in our map
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