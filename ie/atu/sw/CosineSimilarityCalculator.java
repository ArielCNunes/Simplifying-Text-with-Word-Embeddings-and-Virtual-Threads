package ie.atu.sw;

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
}