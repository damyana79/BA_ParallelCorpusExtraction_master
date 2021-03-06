import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SentencePair contains a Pair of the class Sentence: one czech and a corresponding english sentence
 */
public class SentencePair {
    Sentence english;
    Sentence czech;
    String outputFile;

    /**
     * @param english
     * @param czech
     * @param outputFile
     */
    SentencePair(Sentence english, Sentence czech, String outputFile) {
        this.english = english;
        this.czech = czech;
        this.outputFile = outputFile;
    }

    @Override
    public String toString() {
        return this.english + "\n" + this.czech;
    }

    /**
     * the method is called for each english-czech sentence pair (which has fields full sentence and verb list)
     * the verbs from the czech verb list are looked up in the VallexGlosbe combined dictionary -> aspect and set of meanings in english
     * the list of verbs from the english sentence is compared to the list of english translations for the czech verb
     * when correspondence is found, both verbs (token and infinitiv), the vallex aspect and both sentences are written to a file
     */
    public void checkVerbCorrespondences() {
        //TODO: better as a field of the class???????????
        //defines Sets of too frequent englisch verbs an czech verbs, that are not taken into concideration
        Set<String> tooFrequentToCare_cz = Stream.of("být").collect(Collectors.toCollection(HashSet::new));
        Set<String> tooFrequentToCare_en = Stream.of("be", "have", "know").collect(Collectors.toCollection(HashSet::new));

        List<Verb> englishCorpusVerbs = this.english.verbs;
        List<Verb> czechCorpusVerbs = this.czech.verbs;

        // iterate over Verb list of the sentence from the czech corpus
        for (Verb czCorpusVerb : czechCorpusVerbs) {
            String infinitiv = czCorpusVerb.infinitiv.toLowerCase().trim();

            if (!tooFrequentToCare_cz.contains(infinitiv)) {
                //VallexGlosbeVerb czDictVerb = VallexGlosbeDictionary.getInstance().get(infinitiv);
                VallexGlosbeVerb czDictVerb = lookupVerb(infinitiv); // check each verb in the composed Dictionary
                //was there an entry for the czech verb in Vallex
                if (czDictVerb != null) {
                    Set<String> enTranslations = czDictVerb.enTranslations;
                    //was there an entry for the czech verb in Glosbe
                    if (enTranslations.size() != 0) {
                        for (Verb enCorpusVerb : englishCorpusVerbs) { // iterate over Verbs of the sentence from the english corpus
                            String en = enCorpusVerb.infinitiv.trim().toLowerCase(); // normalize

                            if ((!tooFrequentToCare_en.contains(en)) && enTranslations.contains(en)) {
                                czCorpusVerb.aspect = czDictVerb.aspect;

                                String[] dataToWrite = {enCorpusVerb.token, enCorpusVerb.infinitiv, czCorpusVerb.token, czCorpusVerb.infinitiv, czCorpusVerb.aspect, this.english.fullSentence, this.czech.fullSentence};
                                writeSentences(dataToWrite);
                            }
                        }
                    }
                }
            }
        }
    }

    public void writeSentences(String[] dataToWrite) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(this.outputFile, true))) {
            writer.writeNext(dataToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Looks up the verb in the VallexGlosbeDictionary; expanded with lookups for reflexives (Accusative and Dative)
     *
     * @param infinitiv
     * @return
     */
    public VallexGlosbeVerb lookupVerb(String infinitiv) {
        VallexGlosbeDictionary dict = VallexGlosbeDictionary.getInstance();
        VallexGlosbeVerb czDictVerb = dict.get(infinitiv);
        if (czDictVerb == null) {
            if (isMaybeReflexive()) {
                String reflexiveInfinitiv = infinitiv + " " + "se";
                czDictVerb = dict.get(reflexiveInfinitiv);
            } else if (isMaybeReflexiveDative()) {
                String reflexiveInfinitivDativ = infinitiv + " " + "si";
                czDictVerb = dict.get(reflexiveInfinitivDativ);
            }
        }
        return czDictVerb;
    }


    public boolean isMaybeReflexive() {
        return this.czech.fullSentence.contains(" se ");
    }

    public boolean isMaybeReflexiveDative() {
        return this.czech.fullSentence.contains(" si ");
    }

}
