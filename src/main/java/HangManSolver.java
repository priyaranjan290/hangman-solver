import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class HangManSolver {

    public void solveHangMan() throws FileNotFoundException {

        String path = "../resources/dictionary.txt";

        // initialize dictionary
        Scanner scanner = new Scanner(new File(path));
        List<String> possibleGuesses = new ArrayList<>();
        while (scanner.hasNextLine()) {
            possibleGuesses.add(scanner.nextLine());
        }


        // word to be guessed -> can be taken as user input
        System.out.println("enter the word to be searched for ...");
        Scanner userInputScanner = new Scanner(System.in);
        String wordToBeGuessed = userInputScanner.nextLine();


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
        WrongTriesCount wrongTries = new WrongTriesCount();
        wrongTries.value = 0;
        //int triesRemaining = 7;
        Set<Character> characterSetGuessedSoFar = new HashSet<>();


        DictionaryHelper dictionaryHelper = new DictionaryHelper();

        // basic step -> filter out all words of different length
        dictionaryHelper.filterByStringLength(wordToBeGuessed.length(), possibleGuesses);

        // loop through to guess and conclude or terminate!
        while (!wordGuessed.toString().equals(wordToBeGuessed) && !found /*&& triesRemaining > 0*/ ) {

            // update counter
            //triesRemaining = triesRemaining - 1;
            steps = steps + 1;

            // out of words in dictionary
            if (possibleGuesses.size() == 0) {
                System.out.format("cannot guess! dictionary exhausted!, steps processed %d\n", steps);
                break;
            }


            // only one word remains in dictionary
            if (possibleGuesses.size() == 1) {
                if (possibleGuesses.get(0).equals(wordToBeGuessed)) {
                    found = true;
                    System.out.format("Only one word remaining in dict - steps %d, guess : %s \n",  steps, possibleGuesses.get(0));
                    break;
                } else {
                    System.out.format("Only one word remaining in dict - steps %d, word: %s, cannot guess further!",  steps, possibleGuesses.get(0));
                }
            }

            if (possibleGuesses.size() > 1) {
                // make a guess
                char nextGuess = dictionaryHelper.makeNextGuess(possibleGuesses, characterSetGuessedSoFar);

                // process the guess -> eliminate the un-necessary words basis feedback
                dictionaryHelper.processGuess(nextGuess, wordGuessed , wordToBeGuessed,  possibleGuesses, wrongTries);

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

        System.out.format("total wrong tries : %d \n", wrongTries.value);
    }



    public static void main(String[] args) throws FileNotFoundException {

        HangManSolver hangManSolver = new HangManSolver();
        hangManSolver.solveHangMan();

    }

}
