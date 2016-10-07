import com.sun.xml.internal.bind.v2.TODO;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * parses the english and czech correspondence files
 * creates a List Object correspondingIDs: [ [en_id, cz_id], [...], ...] pro book
 */
public class CorrespondenceParser {
    private List<List<Integer>> correspondingIDs = new ArrayList<>();

    CorrespondenceParser(String filename) throws IOException {
        File inputFile = new File(filename);
        SAXReader reader = new SAXReader();

        try {
            Document document = reader.read(inputFile);

            List<Node> nodes = document.selectNodes("linkGrp/link");
            if (nodes.isEmpty()) {
                System.err.println("The document does not correspond to the required format.");
                throw new InputMismatchException();
            }
            for (Node node : nodes) {
                String twoSentences = node.valueOf("@xtargets");
                String correspondence = node.valueOf("@type");

                if (correspondence.equals("1-1")) {
                    String[] two = twoSentences.split(";");
                    // list of 2 corresponding id sentences
                    List<Integer> ids = new ArrayList<>();
                    for (String el : two) {
                        int i = el.lastIndexOf(":");
                        el = el.substring(i + 1);
                        ids.add(Integer.parseInt(el));
                    }
                    this.correspondingIDs.add(ids);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
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
