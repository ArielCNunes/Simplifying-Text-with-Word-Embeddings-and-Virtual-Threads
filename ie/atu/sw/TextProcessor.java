package ie.atu.sw;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class TextProcessor {
    // References to our maps and to the distance calculator class
    ConcurrentHashMap<String, double[]> wordEmbeddings;
    ConcurrentHashMap<String, double[]> googleWords;
    EuclideanDistanceCalculator similarityCalculator;

    // Constructor
    public TextProcessor(ConcurrentHashMap<String, double[]> wordEmbeddings, ConcurrentHashMap<String, double[]> googleWords) {
        this.wordEmbeddings = wordEmbeddings;
        this.googleWords = googleWords;
        this.similarityCalculator = new EuclideanDistanceCalculator();
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

                // Access each word
                for (String word : words) {
                    word = word.trim().toLowerCase(); // for consistency's sake
                    if (wordEmbeddings.containsKey(word)) {
                        // For each word -> get embeddings, send them over to be compared, and get a word in return
                        double[] vector = wordEmbeddings.get(word);
                        String newWord = similarityCalculator.closestWord(vector, googleWords);
                        newLine.append(newWord).append(" ");
                    } else {
                        // Word is unchanged
                        newLine.append(word).append(" ");
                    }
                }

                writer.write(newLine.toString().trim());
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("I/O exception");
        }
    }
}
