import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sentence: full sentence, list of the verbs contained in the sentence
 */
public class Sentence {
    String fullSentence;
    //infinitives
    List<Verb> verbs;

    /**
     * @param fullSentence
     * @param verbs
     */
    Sentence(String fullSentence, List<Verb> verbs) {
        this.fullSentence = fullSentence;
        this.verbs = verbs;
    }


    @Override
    public String toString() {
        return this.fullSentence + "\nverbs: " + this.verbs.toString();
    }

    /**
     * Builds a sentence from a collected sentence list
     *
     * @param sentenceList
     * @return
     */
    public static String formatSentence(List<String> sentenceList) {
        StringBuilder sentence = new StringBuilder();
        ArrayList<String> punctuation = new ArrayList<String>(Arrays.asList(",", ".", "!", "?", ";"));
        for (String word : sentenceList) {
            if (!punctuation.contains(word)) {
                sentence.append(" ");
            }
            sentence.append(word);
        }
        return sentence.toString();
    }

}
