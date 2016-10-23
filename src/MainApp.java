import org.dom4j.DocumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * iterates over all english and czech corpus files, creates a Corpus parser for each file and gets and writes
 * SentencePair one by one to a file
 */
public class MainApp {
    String intercorp_en2cs;
    String intercorp_en;
    String intercorp_cs;
    String output_sentences;

    MainApp(String intercorp_en2cs, String intercorp_en, String intercorp_cs, String output_sentences) {
        this.intercorp_en2cs = intercorp_en2cs;
        this.intercorp_en = intercorp_en;
        this.intercorp_cs = intercorp_cs;
        this.output_sentences = output_sentences;
    }

    public List<String> getAllDocumentNames() throws IOException {
        Set<String> intersect_en2cs = Files.walk(Paths.get(this.intercorp_en2cs))
                .filter(Files::isRegularFile)
                .map(Path::getFileName).map(Path::toString)
                .collect(Collectors.toCollection(HashSet::new));

        Set<String> intercorp_en = Files.walk(Paths.get(this.intercorp_en))
                .filter(Files::isRegularFile)
                .map(Path::getFileName).map(Path::toString)
                .collect(Collectors.toCollection(HashSet::new));

        Set<String> intercorp_cz = Files.walk(Paths.get(this.intercorp_cs))
                .filter(Files::isRegularFile)
                .map(Path::getFileName).map(Path::toString)
                .collect(Collectors.toCollection(HashSet::new));

        intersect_en2cs.retainAll(intercorp_en);
        intersect_en2cs.retainAll(intercorp_cz);
        return intersect_en2cs.stream().collect(Collectors.toList());

    }

    public void parseAllDocuments(List<String> allFileNames) throws IOException, DocumentException {
        //TODO: written Files does not have a function now; can be used to add new files and skip changes in old ones
        Set<String> written_files = Files.walk(Paths.get(this.output_sentences))
                .filter(Files::isRegularFile)
                .map(Path::getFileName).map(Path::toString)
                .collect(Collectors.toCollection(HashSet::new));
        for (String fileName : allFileNames) {
            String bareFilename = fileName.substring(0, fileName.lastIndexOf("."));

            String filename_en2cs = this.intercorp_en2cs + "/" + fileName;
            String filename_en = this.intercorp_en + "/" + fileName;
            String filename_cs = this.intercorp_cs + "/" + fileName;
            String output_filename = this.output_sentences + "/" + bareFilename + ".csv";
//            if (written_files.contains(output_filename)) {
//                System.out.println(output_filename);
//                //emptyFile(output_filename);
//            }
//            for (String filename : written_files) {
//                System.out.println(this.output_sentences + "/" + filename);
//            }
            SentenceProcessor sentenceParser = new SentenceProcessor(filename_en2cs, filename_en, filename_cs);
            emptyFile(output_filename);
            sentenceParser.getSentencePairs(output_filename);
        }
    }

    public static void emptyFile(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Cannot access " + filename);
            e.printStackTrace();
            return;
        }
    }


    public static void main(String[] args) throws IOException, DocumentException {
        String dictionaryFilename = "vallex/dictionary.csv";
        VallexGlosbeDictionary vallexGlosbeDictionary = new VallexGlosbeDictionary(dictionaryFilename);

        String intercorp_en2cs = "correspondences_intercorp_en2cs";
        String intercorp_en = "intercorp_en";
        String intercorp_cs = "intercorp_cs";
        String output_sentences = "output_sentences";

        MainApp mainApp = new MainApp(intercorp_en2cs, intercorp_en, intercorp_cs, output_sentences);
        List<String> allDocumentNames = mainApp.getAllDocumentNames();
        mainApp.parseAllDocuments(allDocumentNames);

    }
}
