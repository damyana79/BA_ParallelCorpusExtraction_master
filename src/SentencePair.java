import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SentencePair {
    Sentence english;
    Sentence czech;
    String outputFile;

    SentencePair(Sentence english, Sentence czech, String outputFile) {
        this.english = english;
        this.czech = czech;
        this.outputFile = outputFile;
    }

    @Override
    public String toString() {
        return this.english + "\n" + this.czech;
    }


    public void checkVerbCorrespondences() {
        //TODO: better as a field of the class???????????
        Set<String> tooFrequentToCare_cz = Stream.of("být").collect(Collectors.toCollection(HashSet::new));
        Set<String> tooFrequentToCare_en = Stream.of("be", "have", "know").collect(Collectors.toCollection(HashSet::new));
        //List<List<String>> verbCorrespondences = new ArrayList<>();
        List<Verb> englishCorpusVerbs = this.english.verbs;
        List<Verb> czechCorpusVerbs = this.czech.verbs;
        //System.out.println("verbLists: " + englishCorpusVerbs + "   " + czechCorpusVerbs);


        for (Verb czCorpusVerb : czechCorpusVerbs) { // iterate over Verb list of the sentence from the czech corpus
            //List<String> verbCorrespondence = new ArrayList<>();
            String infinitiv = czCorpusVerb.infinitiv.toLowerCase().trim();

            if (!tooFrequentToCare_cz.contains(infinitiv)) {
                //VallexGlosbeVerb czDictVerb = VallexGlosbeDictionary.getInstance().get(infinitiv);
                VallexGlosbeVerb czDictVerb = lookupVerb(infinitiv); // check each verb in the composed Dictionary
                //was there an entry for the czech verb in Vallex
                if (czDictVerb != null) {
                    Set<String> enTranslations = czDictVerb.enTranslations;
                    //System.out.println("dictTranslations " + infinitiv + " " + enTranslations);
                    //was there an entry for the czech verb in Glosbe
                    if (enTranslations.size() != 0) {
                        for (Verb enCorpusVerb : englishCorpusVerbs) { // iterate over Verbs of the sentence from the english corpus
                            String en = enCorpusVerb.infinitiv.trim().toLowerCase(); // normalize

                            if ((!tooFrequentToCare_en.contains(en)) && enTranslations.contains(en)) {
                                //System.out.println("COMPARISON " + enCorpusVerb);
//                            verbCorrespondence.add(enCorpusVerb.infinitiv);
//                            verbCorrespondence.add(czDictVerb.czVerb);
                                czCorpusVerb.aspect = czDictVerb.aspect;

                                String[] dataToWrite = {enCorpusVerb.token, enCorpusVerb.infinitiv, czCorpusVerb.token, czCorpusVerb.infinitiv, czCorpusVerb.aspect, this.english.fullSentence, this.czech.fullSentence};
                                writeSentences(dataToWrite);
                                //verbCorrespondences.add(verbCorrespondence); // [[v_en, v-cz], [...], ...]
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

    //TODO: expand with lookups for reflexives
    public VallexGlosbeVerb lookupVerb(String infinitiv) {
        VallexGlosbeVerb czDictVerb = VallexGlosbeDictionary.getInstance().get(infinitiv);
        if (czDictVerb == null) {
            if (isMaybeReflexive()) {
                String reflexiveInfinitiv = infinitiv + " " + "se";
                czDictVerb = VallexGlosbeDictionary.getInstance().get(reflexiveInfinitiv);
            }
        }
        return czDictVerb;
    }


    public boolean isMaybeReflexive() {
        return this.czech.fullSentence.contains(" se ");
    }


//    public static void main(String[] args) {
//        String test = " Razhodi se nasam natam";
//        //System.out.println(test.contains(" se "));
//    }

}
