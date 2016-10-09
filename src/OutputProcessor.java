import com.opencsv.CSVReader;
import org.dom4j.DocumentException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class OutputProcessor {
    public Map<String, List<OutputVerbData>> outputVerbDictionary = new HashMap<>();

    public void buildOutputProcessor(String filename) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '"');
        //Read CSV line by line and use the string array as you want
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String token_en = nextLine[0];
            String infinitive_en = nextLine[1];
            String aspect = nextLine[4];
            String info_czVerb = nextLine[3];
            String fullSentence_en = nextLine[5];
            Verb verb = new Verb(token_en, infinitive_en);
            verb.setAspect(aspect);
            OutputVerbData outputVerbData = new OutputVerbData(verb, fullSentence_en, info_czVerb);
            // TODO: Julian zeigen
            try {
                outputVerbData.setSpans();
            } catch (IndexOutOfBoundsException e) {
                continue; // springe ich so zum nächsten verb in der for-Schleife?
            }

            if (outputVerbDictionary.containsKey(infinitive_en)) {
                outputVerbDictionary.get(infinitive_en).add(outputVerbData);
            } else {
                outputVerbDictionary.put(infinitive_en, new ArrayList<OutputVerbData>(Arrays.asList(outputVerbData)));
            }
        }
        System.out.println(outputVerbDictionary);
        System.out.println(outputVerbDictionary.size());
    }

    public static List<String> getAllDocumentNames(String outputFolder) throws IOException {
        List<String> outputFileNames = Files.walk(Paths.get(outputFolder))
                .filter(Files::isRegularFile).map(Path::toString)
                .collect(Collectors.toList());
        return outputFileNames;
    }

//    public void parseAllDocuments(List<String> allFileNames) throws IOException, DocumentException {
//        Set<String> written_files = Files.walk(Paths.get(this.output_sentences))
//                .filter(Files::isRegularFile)
//                .map(Path::getFileName).map(Path::toString)
//                .collect(Collectors.toCollection(HashSet::new));
//        for (String fileName : allFileNames) {
//            String bareFilename = fileName.substring(0, fileName.lastIndexOf("."));
//            if (!written_files.contains(bareFilename + ".csv")) {
//                String filename_en2cs = this.intercorp_en2cs + "/" + fileName;
//                String filename_en = this.intercorp_en + "/" + fileName;
//                String filename_cs = this.intercorp_cs + "/" + fileName;
//                String output_filename = this.output_sentences + "/" + bareFilename + ".csv";
//                SentenceParser sentenceParser = new SentenceParser(filename_en2cs, filename_en, filename_cs);
//                sentenceParser.getSentencePairs(output_filename);
//            }
//        }
//    }


    public static void main(String[] args) throws IOException {
        //String filename = "test_output_sentences/output.csv";
        String folderName = "output_sentences";
        List<String> allFileNames = getAllDocumentNames(folderName);
        //System.out.println(allFileNames);

        OutputProcessor outputProcessor = new OutputProcessor();
        for (String filename : allFileNames) {
            outputProcessor.buildOutputProcessor(filename);
        }


    }
}
