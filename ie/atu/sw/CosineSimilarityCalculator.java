package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CosineSimilarityCalculator {
    /**
     * This method returns the cosine similarity between the vectors which represent the words.
     *
     * @param vector1 The first word vector.
     * @param vector2 The second word vector.
     * @return Cosine similarity score.
     * @throws IllegalArgumentException if vectors are of different lengths.
     */
    public double getCosineSimilarity(double[] vector1, double[] vector2) {
        // Make sure both words have the same amount of vectors
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("vectors must have the same length");
        }

        // Store dot product and magnitudes (lengths)
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // Loop through all vectors
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];

            // Add the square root of each element to the variables
            normA += Math.pow(vector1[i], 2);
            normB += Math.pow(vector2[i], 2);
        }

        // Return the dot product divided by the product of magnitudes
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * This method finds the closest match for the target vector from the Google-1000 words.
     *
     * @param targetVector     The vector of the word being simplified.
     * @param googleEmbeddings Map containing Google-1000 words and their vectors.
     * @return The closest word from the Google-1000 list.
     */
    public String closestWord(double[] targetVector, ConcurrentHashMap<String, double[]> googleEmbeddings) {
        String closestWord = null;
        double mostSimilar = -1.0; // lowest possible similarity

        // Loop through each entry in the Google-1000 map
        for (Map.Entry<String, double[]> entry : googleEmbeddings.entrySet()) {
            // Retrieve the vector for the current word
            double[] vector = entry.getValue();

            // Calculate cosine similarity between target and current word
            double similarity = getCosineSimilarity(targetVector, vector);

            // Compare similarities and store closest one
            if (similarity > mostSimilar) {
                mostSimilar = similarity;
                closestWord = entry.getKey();
            }
        }
        return closestWord;
    }
}