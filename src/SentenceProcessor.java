import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialises a CorrespondenceParser(filename_en2cs) and CorpusParser(filename_en), CorpusParser(filename_cs)
 * Builds/initializes sentencePair-s
 */
public class SentenceProcessor {
    List<SentencePair> sentencePairs = new ArrayList<SentencePair>();
    CorrespondenceParser correspondenceParser;
    CorpusParser corpusParser_en;
    CorpusParser corpusParser_cs;


    SentenceProcessor(String filename_en2cs, String filename_en, String filename_cs) throws DocumentException, IOException {
        this.correspondenceParser = new CorrespondenceParser(filename_en2cs);
        this.corpusParser_en = new CorpusParser(filename_en);
        this.corpusParser_cs = new CorpusParser(filename_cs);
    }


    /**
     * gets a raw sentence from one of the corpus parsers (en o. cz) and makes a Sentence Object with a field verbList,
     * containing VerbObjects: token and infinitive and a friels String fullSentence
     *
     * @param rawSentence
     * @return Sentence Object
     */
    public Sentence makeSentenceObject(String rawSentence) {
        String[] rawSentenceArray = rawSentence.trim().split("\n"); // Line with: <word \t inf \t POS>
        //System.out.println(Arrays.toString(sentenceArray));
        List<String> sentenceList = new ArrayList<>();
        List<Verb> verbsList = new ArrayList<>();
        for (String line : rawSentenceArray) {
            String[] columns = line.split("\\s+");
            String token = columns[0];
            sentenceList.add(token);
            if (columns[2].startsWith("V")) {
                if (!token.matches("\\W")) { // some punctuation was also tagged as V..: sort out
                    String infinitiv = columns[1];
                    Verb verb = new Verb(token, infinitiv);
                    verbsList.add(verb);
                }
            }
        }
        String sentence_text = Sentence.formatSentence(sentenceList);
        Sentence sentence = new Sentence(sentence_text, verbsList);
        //System.out.println(sentence);

        return sentence;
    }

    /**
     * @return List<SentencePair> per bookName (the Parsers are called for each book)
     */
    public void getSentencePairs(String outputFile) {
        for (List<Integer> correspondence : this.correspondenceParser.getCorrespondences()) {
            Integer en = correspondence.get(0);
            Integer cs = correspondence.get(1);
            Sentence sentence_en = makeSentenceObject(this.corpusParser_en.getCorpusData().get(en));
            Sentence sentence_cs = makeSentenceObject(corpusParser_cs.getCorpusData().get(cs));
            SentencePair sentencePair = new SentencePair(sentence_en, sentence_cs, outputFile);
            //System.out.println("sentence pair" + sentencePair);
            sentencePair.checkVerbCorrespondences();
        }
    }


}
