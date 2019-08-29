import java.util.*;
import java.util.stream.Collectors;

public class DictionaryHelper {

    public void filterByStringLength(int length, List<String> list) {
        List<String> newList = list.stream().filter(x-> !(x.length() == length)).collect(Collectors.toList());

        list.removeAll(newList);
    }


    /**
     * iterate on dictionary and keep words where :
     *
     * 1. guessed character is present
     * 2. guessed character is present at specific indices
     *
     * */
    public void filterMatchingCharacter(char c, String wordToBeGuessed, List<String> list) {

        List<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String x = list.get(i);

            if (!x.contains(String.valueOf(c))) {
                newList.add(x);
            }

            if (!positionsMatched(c, x, wordToBeGuessed)) {
                newList.add(x);
            }
        }

        list.removeAll(newList);
    }

    private boolean positionsMatched(char c, String word, String wordToBeGuessed) {

        Set<Integer> positionsOfCharInWordGuessed = new HashSet<>();
        for (int i = 0 ; i<wordToBeGuessed.length() ; i++)
            if (wordToBeGuessed.charAt(i) == c)
                positionsOfCharInWordGuessed.add(i);


        Set<Integer> positionsOfCharInWord = new HashSet<>();
        for (int i = 0 ; i<word.length() ; i++)
            if (word.charAt(i) == c)
                positionsOfCharInWord.add(i);


        if (positionsOfCharInWord.equals(positionsOfCharInWordGuessed)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * iterate on dictionary and remove the words which have a wrong guessed character
     * */
    public void removeUnmatchedCharacter(char c, List<String> list) {

        List<String> newList = list
                .stream()
                .filter(x-> x.contains(String.valueOf(c)))
                .collect(Collectors.toList());

        list.removeAll(newList);
    }


    /**
     *
     * 1. filter dictionary basis wrong and right guess
     * 2. update the guessed word so far
     *
     * */
    public void processGuess(char guess, StringBuilder wordGuessed, String wordToBeGuessed, List<String> list) {

        // case of correct guess
        if (wordToBeGuessed.contains(String.valueOf(guess))) {

            // filter the dictionary to contain only words which have this character
            filterMatchingCharacter(guess, wordToBeGuessed,  list);

            // update the current word guessed
            for (int i = 0; i < wordToBeGuessed.length(); i++) {
                if (wordToBeGuessed.charAt(i) == guess) {
                    wordGuessed.setCharAt(i, guess);
                }
            }

        } else {

            // case of wrong guess -> filter the dictionary having the wrong guess character
            removeUnmatchedCharacter(guess, list);

        }

    }



    /**
     *
     * Main function to make next guess. Creates character wise score. In case of a tie, prefers vowels over consonants.
     * In case of more than one vowels/consonants with same score, sort lexicographically and pick the first char.
     *
     * */
    public char makeNextGuess(List<String> list, Set<Character> characterSetGuessedSoFar) {

        // maintains frequency map of each character to total count in dictionary
        Map<Character, Integer> characterToTotalFrequencyMap = new HashMap<>();

        // maintains freq map of character and number of words it appears
        Map<Character, Integer> characterToWordFrequencyMap = new HashMap<>();


        // calculate the frequency maps
        for (String word : list) {

            Set<Character> currWordUniqueCharSet = new HashSet<>();

            for (int i = 0; i<word.length(); i++) {
                Character currChar = word.charAt(i);

                // do not guess same character over and over again!
                if (characterSetGuessedSoFar.contains(currChar)) {
                    continue;
                }


                currWordUniqueCharSet.add(currChar);

                if (characterToTotalFrequencyMap.containsKey(currChar)) {
                    characterToTotalFrequencyMap.put(currChar, characterToTotalFrequencyMap.get(currChar) + 1);
                } else {
                    characterToTotalFrequencyMap.put(currChar, 1);
                }
            }

            currWordUniqueCharSet.stream().forEach(x-> {
                if (characterToWordFrequencyMap.containsKey(x)) {
                    characterToWordFrequencyMap.put(x, characterToWordFrequencyMap.get(x) + 1);
                } else {
                    characterToWordFrequencyMap.put(x, 1);
                }
            });

        }


        // stores the score of each character that can be guessed next
        Map<Character, Integer> characterToScoreMap = new HashMap<>();

        // calculate the score Map
        calculateScoreMap(characterToTotalFrequencyMap, characterToWordFrequencyMap, characterToScoreMap);


        // make set of max score characters
        Set<Character> maxScoreCharacters = new HashSet<>();
        Integer max_char_score = 0;
        for (Map.Entry<Character, Integer> entry : characterToScoreMap.entrySet()) {
            if (entry.getValue() >= max_char_score) {
                maxScoreCharacters.add(entry.getKey());
                max_char_score = entry.getValue();
            }
        }


        Iterator iterator = maxScoreCharacters.iterator();

        // if set size is 1 return
        if (maxScoreCharacters.size() == 1) {
            Character next = (Character) iterator.next();
            characterSetGuessedSoFar.add(next);
            return next;
        } else {

            /////// tie breaker code //////

            // make list from set of max score characters
            List<Character> sameScoreCharList  = new ArrayList<>();
            while (iterator.hasNext()) {
                sameScoreCharList.add((Character) iterator.next());
            }

            // split into vowels and consonants
            Set<Character> vowelsSet = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
            List<Character> vowelsList = new ArrayList<>();
            List<Character> consonantList = new ArrayList<>();

            for (int i = 0; i < sameScoreCharList.size(); i++) {
                if (vowelsSet.contains(sameScoreCharList.get(i))) {
                    vowelsList.add(sameScoreCharList.get(i));
                } else {
                    consonantList.add(sameScoreCharList.get(i));
                }
            }


            // return sorted vowels first and then sorted consonants
            if (!vowelsList.isEmpty()) {
                Collections.sort(vowelsList);
                characterSetGuessedSoFar.add(vowelsList.get(0));
                return vowelsList.get(0);
            } else {

                Collections.sort(consonantList);
                characterSetGuessedSoFar.add(consonantList.get(0));
                return consonantList.get(0);

            }

        }

    }


    /**
     *
     * calculates the score of each character. Currently returns only the total frequency count as score...
     *
     * */
    private void calculateScoreMap(Map<Character, Integer> characterToTotalFrequencyMap,
                                   Map<Character, Integer> characterToWordFrequencyMap,
                                   Map<Character, Integer> characterToScoreMap) {

        characterToTotalFrequencyMap.forEach( (x,y) -> {
            characterToScoreMap.put(x,y);
        });

    }
}
