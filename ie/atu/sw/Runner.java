package ie.atu.sw;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Runner {

    public static void main(String[] args) throws Exception {
        Runner runner = new Runner();
        runner.menu();
    }

    /**
     * Displays the main menu and processes user input.
     * Allows the user to specify file paths and execute text simplification.
     *
     * @throws Exception If an error occurs during processing.
     */
    public void menu() throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Variables for file paths
        String embeddingsFile = null;
        String google1000File = null;
        String inputFile = null;
        String outputFile = "out.txt";

        // Menu
        boolean exit = false;
        while (!exit) {
            System.out.println(ConsoleColour.BLUE);
            System.out.println("************************************************************");
            System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
            System.out.println("*                                                          *");
            System.out.println("*             Virtual Threaded Text Simplifier             *");
            System.out.println("************************************************************");
            System.out.println("(1) Specify Embeddings File");
            System.out.println("(2) Specify Google 1000 File");
            System.out.println("(3) Specify Input File");
            System.out.println("(4) Specify an Output File (default: ./out.txt)");
            System.out.println("(5) Execute, Analyse and Report");
            System.out.println("(-1) Quit");

            // Output a menu of options and solicit text from the user
            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
            System.out.print("Select Option [1-5]> ");
            int choice = scanner.nextInt();

            // Get rid of leftover line
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter Path For Embeddings File: ");
                    embeddingsFile = scanner.nextLine();
                    break;
                case 2:
                    System.out.println("Enter Path For Google 1000 File: ");
                    google1000File = scanner.nextLine();
                    break;
                case 3:
                    System.out.print("Enter Path For Input File: ");
                    inputFile = scanner.nextLine();
                    break;
                case 4:
                    System.out.print("Enter Path For Output File: ");
                    outputFile = scanner.nextLine();
                    break;
                case 5:
                    if (embeddingsFile == null || google1000File == null || inputFile == null) {
                        System.out.println(ConsoleColour.RED + "Please specify all required file paths first.");
                        continue;
                    }

                    try {
                        // Load data and process input
                        simplifyWords(embeddingsFile, google1000File, inputFile, outputFile);
                        System.out.println(ConsoleColour.GREEN + "Text simplification complete!");
                        System.out.println(ConsoleColour.CYAN + "Output saved to: " + outputFile);
                    } catch (Exception e) {
                        System.out.println(ConsoleColour.RED + "Error: " + e.getMessage());
                    }
                    break;
                case -1:
                    System.out.println(ConsoleColour.RED + "Exiting...");
                    exit = true;
                    return;
                default:
                    System.out.println(ConsoleColour.RED + "Invalid option!");
            }
        }

        // You may want to include a progress meter in you assignment!
        System.out.print(ConsoleColour.YELLOW);    // Change the colour of the console text
        int size = 100;                            // The size of the meter. 100 equates to 100%
        for (int i = 0; i < size; i++) {        // The loop equates to a sequence of processing steps
            printProgress(i + 1, size);        // After each (some) steps, update the progress meter
            Thread.sleep(10);                    // Slows things down so the animation is visible
        }
    }

    /**
     * Simplifies the text using specified file paths.
     *
     * @param embeddingsFile Path to embeddings file.
     * @param google1000File Path to Google-1000 file.
     * @param inputFile      Path to input file.
     * @param outputFile     Path for the output file.
     * @throws Exception If an error occurs during processing.
     */
    private void simplifyWords(String embeddingsFile, String google1000File, String inputFile, String outputFile) throws IOException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.execute(() -> {
                try {
                    // Load embeddings and google-1000 words (using data loading manager class)
                    DataLoadingManager dataManager = new DataLoadingManager();
                    dataManager.loadWordEmbeddings(embeddingsFile);
                    dataManager.loadGoogle1000Words(google1000File);

                    // Process comparison and save output
                    TextSimplifier processor = new TextSimplifier(dataManager.wordEmbeddings, dataManager.googleWords);
                    processor.simplifyText(inputFile, outputFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /*
     *  Terminal Progress Meter
     *  -----------------------
     *  You might find the progress meter below useful. The progress effect
     *  works best if you call this method from inside a loop and do not call
     *  System.out.println(....) until the progress meter is finished.
     *
     *  Please note the following carefully:
     *
     *  1) The progress meter will NOT work in the Eclipse console, but will
     *     work on Windows (DOS), Mac and Linux terminals.
     *
     *  2) The meter works by using the line feed character "\r" to return to
     *     the start of the current line and writes out the updated progress
     *     over the existing information. If you output any text between
     *     calling this method, i.e. System.out.println(....), then the next
     *     call to the progress meter will output the status to the next line.
     *
     *  3) If the variable size is greater than the terminal width, a new line
     *     escape character "\n" will be automatically added and the meter won't
     *     work properly.
     *
     *
     */
    public static void printProgress(int index, int total) {
        if (index > total) return;    //Out of range
        int size = 50;                //Must be less than console width
        char done = '█';            //Change to whatever you like.
        char todo = '░';            //Change to whatever you like.

        //Compute basic metrics for the meter
        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;

        /*
         * A StringBuilder should be used for string concatenation inside a
         * loop. However, as the number of loop iterations is small, using
         * the "+" operator may be more efficient as the instructions can
         * be optimized by the compiler. Either way, the performance overhead
         * will be marginal.
         */
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append((i < completeLen) ? done : todo);
        }

        /*
         * The line feed escape character "\r" returns the cursor to the
         * start of the current line. Calling print(...) overwrites the
         * existing line and creates the illusion of an animation.
         */
        System.out.print("\r" + sb + "] " + complete + "%");

        //Once the meter reaches its max, move to a new line.
        if (done == total) System.out.println("\n");
    }
}