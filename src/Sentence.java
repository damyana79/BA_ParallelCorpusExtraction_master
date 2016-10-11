import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sentence {
    String fullSentence;
    //infinitives
    List<Verb> verbs;

    Sentence(String fullSentence, List<Verb> verbs) {
        this.fullSentence = fullSentence;
        this.verbs = verbs;
    }

    //TODO: dummy values; implement correctly!
//    private void getVerbs(String fullSentence) {
//        Verb ve = new Verb("placeholder", "no");
//        this.verbs.add(ve);
//    }

    @Override
    public String toString() {
        return this.fullSentence + "\nverbs: " + this.verbs.toString();
    }

    //    public static void main(String[] args) {
//        List<String> test = new ArrayList<String>(Arrays.asList("abc", "xyz"));
//        System.out.println(test);
//    }
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
