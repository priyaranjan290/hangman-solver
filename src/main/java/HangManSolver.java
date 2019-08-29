import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class HangManSolver {

    public void solveHangMan() throws FileNotFoundException {


        // initialize dictionary
        Scanner scanner = new Scanner(new File(this.getClass().getResource("dictionary.txt").getPath()));
        List<String> possibleGuesses = new ArrayList<>();
        while (scanner.hasNextLine()) {
            possibleGuesses.add(scanner.nextLine());
        }


        // word to be guessed -> can be taken as user input
        String wordToBeGuessed = "bot";


        // creating blanks to be filled
        StringBuilder wordGuessed  = new StringBuilder();
        for (int i = 0; i< wordToBeGuessed.length(); i++) {
            wordGuessed.append('_');
        }



        /**
         * intializing constraints
         * */
        int steps = 0;
        boolean found = false;
        int triesRemaining = 7;
        Set<Character> characterSetGuessedSoFar = new HashSet<>();


        DictionaryHelper dictionaryHelper = new DictionaryHelper();

        // basic step -> filter out all words of different length
        dictionaryHelper.filterByStringLength(wordToBeGuessed.length(), possibleGuesses);

        // loop through to guess and conclude or terminate!
        while (!wordGuessed.toString().equals(wordToBeGuessed) && !found && triesRemaining > 0 ) {

            // update counter
            triesRemaining = triesRemaining - 1;
            steps = steps + 1;

            // out of words in dictionary
            if (possibleGuesses.size() == 0) {
                System.out.format("cannot guess! dictionary exhausted!, steps processed %d\n", steps);
                exit(0);
            }


            // only one word remains in dictionary
            if (possibleGuesses.size() == 1) {
                if (possibleGuesses.get(0).equals(wordToBeGuessed)) {
                    found = true;
                    break;
                }
            }

            if (possibleGuesses.size() > 1) {
                // make a guess
                char nextGuess = dictionaryHelper.makeNextGuess(possibleGuesses, characterSetGuessedSoFar);

                // process the guess -> eliminate the un-necessary words basis feedback
                dictionaryHelper.processGuess(nextGuess, wordGuessed , wordToBeGuessed,  possibleGuesses);

                System.out.format("current guess: %c, step : %d, currentWord : %s\n", nextGuess, steps, wordGuessed);
            }


            if (wordGuessed.toString().equals(wordToBeGuessed)) {
                found = true;
            }
        }


        if (found) {
            System.out.format("word guessed in %d  tries\n", steps);
        } else {
            System.out.println("could not guess word!\n");
        }
    }



    public static void main(String[] args) throws FileNotFoundException {

        HangManSolver hangManSolver = new HangManSolver();
        hangManSolver.solveHangMan();

    }

}
