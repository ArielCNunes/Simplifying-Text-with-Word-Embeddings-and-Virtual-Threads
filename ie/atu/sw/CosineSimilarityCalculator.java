package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class calculates the similarity between 2 words by using the Cosine Similarity algorithm.
 */
public class CosineSimilarityCalculator {
    /**
     * Calculates the cosine similarity between two vectors.
     *
     * <p><b>Big-O Notation is O(n)</b> -> n corresponds to the length of the vectors. The more embeddings, the longer
     * it should take.</p>
     *
     * @param vector1 The first word vector.
     * @param vector2 The second word vector.
     * @return Cosine similarity score ranging from -1 to 1.
     * @throws IllegalArgumentException If the vectors are of different lengths.
     */
    private double getCosineSimilarity(double[] vector1, double[] vector2) {
        // Vectors need to be the same length
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // Compute dot product and norms for vectors
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            normA += Math.pow(vector1[i], 2);
            normB += Math.pow(vector2[i], 2);
        }

        // Return similarity (dot product by the product of magnitudes)
        return (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    /**
     * Finds the closest match for the target vector from the Google-1000 word embeddings.
     *
     * <p>This method iterates through the Google-1000 embeddings and calculates the cosine similarity
     * between the input word vector and each vector in the map. It selects the word with the
     * highest similarity score as the closest match.</p>
     *
     * <p><b>Big-O Notation is O(n * m)</b></p> -> n = Number of words in the Google-1000 list and m = Length of
     * each vector. Processing each word is linear, and for each word we do the calculation which is also linear.
     *
     * @param inputWordEmbeddings The vector of the word being simplified.
     * @param googleEmbeddings    A map containing Google-1000 words and their vectors.
     * @return The closest word from the Google-1000 list based on cosine similarity.
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