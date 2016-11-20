import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Container class
 */
class OutputVerbData {
    Verb verb_en;
    String fullSentence_en;

    String infoStringCzVerb;

    OutputVerbData(Verb verb_en, String fullSentence_en, String infoStringCzVerb) {
        this.verb_en = verb_en;
        this.fullSentence_en = fullSentence_en;
        this.infoStringCzVerb = infoStringCzVerb;
    }

    @Override
    public String toString() {
        return this.verb_en + " " + this.fullSentence_en + "\n";
    }
}


/**
 * Processes the <verb: verbdata, sentence occurance> data from all output files (folder output_sentences) from the processed corpus
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

    //verbs acquired before
    //TODO: methodenaufruf hier oder 2 Konstruktoren???????????
    private static Set<String> oldVerbs = new HashSet<>();

    public Map<String, List<OutputVerbData>> outputVerbDataDictionary;

    /**
     * @param folderName : output_sentences
     * @throws IOException
     */
    OutputVerbDataDictionary(String folderName) throws IOException {
        //String folderName = "output_sentences";
        instance = this;
        List<String> allFileNames = getAllDocumentNames(folderName);
        this.outputVerbDataDictionary = new HashMap<>();
        for (String filename : allFileNames) {
            this.buildOutputProcessor(filename);
        }
    }

    OutputVerbDataDictionary(String folderName, String oldVerbsFile) throws IOException {
        //String folderName = "output_sentences";
        instance = this;
        List<String> allFileNames = getAllDocumentNames(folderName);
        oldVerbs = getOldVerbKeys(oldVerbsFile);
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
            //TODO: best so????????
//            if (oldVerbs.contains(infinitive_en)) {
//                continue;
//            }
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

    /**
     * this method reads from file verbs that were already found in previous dataset
     * (after the whole verb gathering process has already been performed on the smaller data, the bigger data are processed
     * for finding additional verbs)
     *
     * @param filename
     * @return
     */
    public static Set<String> getOldVerbKeys(String filename) {
        Path path = Paths.get(filename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String verb = line.trim();
                //System.out.println(verb);
                oldVerbs.add(verb);
            }
        } catch (IOException e) {
            System.err.printf("The file %s cannot be read", filename);
            e.printStackTrace();
        }
        return oldVerbs;
    }


    // main method not needed; constructor called in WriteProcessedOutput
//    public static void main(String[] args) {
//        String oldVerbsFile = "processed_output/verb_keys.txt";
//        getOldVerbKeys(oldVerbsFile);
//    }

}
