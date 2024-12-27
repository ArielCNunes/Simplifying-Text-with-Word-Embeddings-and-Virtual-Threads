package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class does the cosine similarity calculation
 */
public class CosineSimilarityCalculator {
    /**
     * This method calculates cosine similarity between two vectors.
     *
     * @param vector1 The first word vector.
     * @param vector2 The second word vector.
     * @return Cosine similarity score.
     * @throws IllegalArgumentException if vectors are of different lengths.
     */
    private double getCosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            normA += Math.pow(vector1[i], 2);
            normB += Math.pow(vector2[i], 2);
        }

        return (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    /**
     * This method finds the closest match for the target vector from the Google-1000 words.
     *
     * @param inputWordEmbeddings The vector of the word being simplified.
     * @param googleEmbeddings    Map containing Google-1000 words and their vectors.
     * @return The closest word from the Google-1000 list.
     */
    public String closestWord(double[] inputWordEmbeddings, ConcurrentHashMap<String, double[]> googleEmbeddings) {
        String closestWord = null;
        double highestSimilarity = -1;

        for (Map.Entry<String, double[]> entry : googleEmbeddings.entrySet()) {
            double[] google100Embeddings = entry.getValue();

            // Calculate cosine similarity
            double similarity = getCosineSimilarity(inputWordEmbeddings, google100Embeddings);

            // Update if this word is a better match
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                closestWord = entry.getKey();
            }
        }
        return closestWord;
    }
}