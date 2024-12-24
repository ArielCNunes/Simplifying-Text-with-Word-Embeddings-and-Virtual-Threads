package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EuclideanDistanceCalculator {
    /**
     * This method returns the cosine similarity between the vectors which represent the words.
     *
     * @param vector1 The first word vector.
     * @param vector2 The second word vector.
     * @return Cosine similarity score.
     * @throws IllegalArgumentException if vectors are of different lengths.
     */
    private double getEuclideanDistance(double[] vector1, double[] vector2) {
        // Make sure both words have the same amount of vectors
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("vectors must have the same length");
        }

        // Get sum of squared differences
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }

        return Math.sqrt(sum);
    }

    /**
     * This method finds the closest match for the target vector from the Google-1000 words.
     *
     * @param originalWordVector The vector of the word being simplified.
     * @param googleEmbeddings   Map containing Google-1000 words and their vectors.
     * @return The closest word from the Google-1000 list.
     */
    public String closestWord(double[] originalWordVector, ConcurrentHashMap<String, double[]> googleEmbeddings) {
        String closestWord = null;
        double smallestDistance = Double.MAX_VALUE;

        // Loop through each entry in the Google-1000 map
        for (Map.Entry<String, double[]> entry : googleEmbeddings.entrySet()) {
            // Retrieve the vector for the current word
            double[] vector = entry.getValue();

            // Calculate Euclidean distance between target and current word
            double distance = getEuclideanDistance(originalWordVector, vector);

            // Compare similarities and store closest one
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestWord = entry.getKey();
            }
        }
        return closestWord;
    }
}