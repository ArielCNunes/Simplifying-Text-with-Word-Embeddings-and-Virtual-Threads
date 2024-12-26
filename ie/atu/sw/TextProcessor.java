package ie.atu.sw;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class TextProcessor {
    // References to our maps and to the distance calculator class
    ConcurrentHashMap<String, double[]> wordEmbeddings;
    ConcurrentHashMap<String, double[]> googleWords;
    CosineSimilarityCalculator similarityCalculator;

    // Constructor
    public TextProcessor(ConcurrentHashMap<String, double[]> wordEmbeddings, ConcurrentHashMap<String, double[]> googleWords) {
        this.wordEmbeddings = wordEmbeddings;
        this.googleWords = googleWords;
        this.similarityCalculator = new CosineSimilarityCalculator();
    }

    /**
     * This method simplifies the input text by replacing each word with its most similar match
     * from the Google-1000 words list based on Euclidean distance.
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
                    for (String word : words) {
                        String newWord;

                        // Replace word if embeddings exist
                        if (wordEmbeddings.containsKey(word)) {
                            double[] vector = wordEmbeddings.get(word);
                            newWord = similarityCalculator.closestWord(vector, googleWords);
                        } else {
                            newWord = word; // Keep original
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
