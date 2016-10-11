import com.opencsv.CSVReader;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * processes the <verb: verbdata, sentence occurance> data from all output files from the processed corpus
 * and builds a dictionary for each verb: <verb : [data, all sentence occurances]>
 */
public class OutputVerbDataDictionary {

    private static OutputVerbDataDictionary instance;

    public static OutputVerbDataDictionary getInstance() {
        return instance;
    }

    public List<OutputVerbData> get(String verb) {
        return this.outputVerbDataDictionary.get(verb);
    }

    public Map<String, List<OutputVerbData>> outputVerbDataDictionary;

    OutputVerbDataDictionary(String folderName) throws IOException {
        //String folderName = "output_sentences";
        instance = this;
        List<String> allFileNames = getAllDocumentNames(folderName);
        this.outputVerbDataDictionary = new HashMap<>();
        for (String filename : allFileNames) {
            this.buildOutputProcessor(filename);
        }
    }

    @Override
    public String toString() {
        return this.outputVerbDataDictionary.toString();
    }


    public void buildOutputProcessor(String filename) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '"');
        //Read CSV line by line and use the string array as you want
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String token_en = nextLine[0];
            String infinitive_en = nextLine[1].trim();
            String aspect = nextLine[4];
            String info_czVerb = nextLine[3];
            String fullSentence_en = nextLine[5];
            Verb verb = new Verb(token_en, infinitive_en);
            verb.setAspect(aspect);
            OutputVerbData outputVerbData = new OutputVerbData(verb, fullSentence_en, info_czVerb);
            try {
                //outputVerbData.setSpans();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                continue; // TODO: springe ich so zum nächsten verb in der for-Schleife?
            }

            if (outputVerbDataDictionary.containsKey(infinitive_en)) {
                outputVerbDataDictionary.get(infinitive_en).add(outputVerbData);
            } else {
                outputVerbDataDictionary.put(infinitive_en, new ArrayList<OutputVerbData>(Arrays.asList(outputVerbData)));
            }
        }
        //System.out.println(outputVerbDataDictionary);
        //System.out.println(outputVerbDataDictionary.size());
    }

    public static List<String> getAllDocumentNames(String outputFolder) throws IOException {
        List<String> outputFileNames = Files.walk(Paths.get(outputFolder))
                .filter(Files::isRegularFile).map(Path::toString)
                .collect(Collectors.toList());
        return outputFileNames;
    }


// main method not needed; constructor called in write Processed Output
// public static void main(String[] args) throws IOException {
//        String folderName = "output_sentences";
//        OutputVerbDataDictionary outputProcessor = new OutputVerbDataDictionary(folderName);
//
//        //String filename = "test_output_sentences/output.csv";
////        String folderName = "output_sentences";
////        List<String> allFileNames = getAllDocumentNames(folderName);
////        //System.out.println(allFileNames);
////
////        OutputVerbDataDictionary outputProcessor = new OutputVerbDataDictionary();
////        for (String filename : allFileNames) {
////            outputProcessor.buildOutputProcessor(filename);
////        }
//
//    }

}
