import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;


/**
 * parses the english and czech correspondence files
 * creates a List Object correspondingIDs: [ [en_id, cz_id], [...], ...] pro book
 */
public class CorrespondenceParser {
    private List<List<Integer>> correspondingIDs = new ArrayList<>();

    CorrespondenceParser(String filename) throws IOException, DocumentException {
        File inputFile = new File(filename);
        SAXReader reader = new SAXReader();

        Document document = reader.read(inputFile);

        Element root = document.getRootElement();

        for (Iterator<Element> it1 = root.elementIterator("link"); it1.hasNext(); ) {
            Node node = it1.next();

            String twoSentences = node.valueOf("@xtargets");
            String correspondence = node.valueOf("@type");

            try {
                if (correspondence.equals("1-1")) {
                    String[] two = twoSentences.split(";");
                    if (two.length != 2) {
                        throw new IllegalArgumentException("incorrectly formated input in " + twoSentences);
                    }

                    // list of 2 corresponding id sentences
                    List<Integer> ids = new ArrayList<>();
                    for (String el : two) {
                        int i = el.lastIndexOf(":");
                        el = el.substring(i + 1);
                        ids.add(Integer.parseInt(el));
                    }
                    this.correspondingIDs.add(ids);
                }
            } catch (Exception e) {
                // ignoring badly formatted input
                e.printStackTrace();
            }
        }
    }

    public List<List<Integer>> getCorrespondences() {
        return this.correspondingIDs;
    }


//    public static void main(String[] args) throws IOException, DocumentException {
//        String filename = "correspondences_intercorp_en2cs/brown-andele_demoni.xml";
//        CorrespondenceParser cp = new CorrespondenceParser(filename);
//    }

}
