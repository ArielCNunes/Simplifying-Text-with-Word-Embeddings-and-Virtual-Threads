package ie.atu.sw;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * This class is responsible for the actual text simplification
 */
public class TextSimplifier {
    // References to our maps and to the cosine similarity class
    private final ConcurrentHashMap<String, double[]> wordEmbeddings;
    private final ConcurrentHashMap<String, double[]> googleWords;
    private final CosineSimilarityCalculator cosineSimilarityCalculator;

    // Constructor
    public TextSimplifier(ConcurrentHashMap<String, double[]> wordEmbeddings, ConcurrentHashMap<String, double[]> googleWords) {
        this.wordEmbeddings = wordEmbeddings;
        this.googleWords = googleWords;
        this.cosineSimilarityCalculator = new CosineSimilarityCalculator();
    }

    /**
     * This method simplifies the input text by replacing each word with its most similar match the Google-1000 words
     * list based on Cosine Similarity.
     *
     * <p>It reads in the line of text and splits it by " ". Each word is saved in an array 'words'. Then we loop
     * through all the words in the array, check to see if they have embeddings and if so, then calculate similarity
     * and swap (or don't).</p>
     *
     * <p><b>Big-O Notation is O(n * m)</b></p> -> where n = Number of lines in the input file and m = Number of words
     * per line. We loop through each line (array with words) and for each word we search the map (O(1)) and do
     * the swapping.
     *
     * @param inputFile  Path to the input text file containing sentences to simplify.
     * @param outputFile Path to the output text file where the simplified text will be saved to.
     * @throws IOException If an error occurs.
     */
    public void simplifyText(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Get sentence and split it by spaces
                String[] words = line.split(" ");
                StringBuilder newLine = new StringBuilder();

                // Process each word concurrently
                var executor = Executors.newVirtualThreadPerTaskExecutor();
                executor.submit(() -> {
                    for (String currentWord : words) {
                        // Get word embeddings is they exist
                        String newWord;
                        if (wordEmbeddings.containsKey(currentWord)) {
                            // Store embeddings for current word
                            double[] embeddings = wordEmbeddings.get(currentWord);

                            // Get the closest word
                            newWord = cosineSimilarityCalculator.closestWord(embeddings, googleWords);
                        } else {
                            // Keep original
                            newWord = currentWord;
                        }

                        // Thread-safe append
                        synchronized (newLine) {
                            newLine.append(newWord).append(" ");
                        }
                    }
                });

                // Shutdown and await termination
                executor.shutdown();
                executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);

                // Print result to file
                writer.write(newLine.toString().trim());
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("I/O exception");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
