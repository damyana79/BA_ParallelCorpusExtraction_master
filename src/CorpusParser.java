import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * parses the czech and the english xml file
 * creates for each book a Map Object with the format:
 * Map \< correspondenceId: sentence\>
 */

public class CorpusParser {
    private Map<Integer, String> bookSentences = new HashMap<>();

    CorpusParser(String filename) throws DocumentException, IOException {
        File inputFile = new File(filename);
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);

        Element root = document.getRootElement();

        for (Iterator<Element> it1 = root.elementIterator("div"); it1.hasNext(); ) {
            for (Iterator<Element> it2 = it1.next().elementIterator("p"); it2.hasNext(); ) {
                for (Iterator<Element> it3 = it2.next().elementIterator("block"); it3.hasNext(); ) {
                    for (Iterator<Node> it4 = it3.next().elementIterator("s"); it4.hasNext(); ) {
                        Node s = it4.next();

                        String definition = s.valueOf("@id");
                        String temp = definition.substring(definition.lastIndexOf(":") + 1);
                        int sentenceId = Integer.parseInt(temp);
                        String sentence = s.getText();
                        //speichert nur rawSentence in der Map
                        this.bookSentences.put(sentenceId, sentence);

                    }
                }
            }
        }
    }


    public Map<Integer, String> getCorpusData() {
        return this.bookSentences;
    }

//    public static void main(String[] args) throws IOException, DocumentException {
//        String filename = "intercorp_en/brown-sifra.xml";
//        CorpusParser cp = new CorpusParser(filename);
//    }


}
