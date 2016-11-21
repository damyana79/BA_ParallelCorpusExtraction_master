import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * uses instance of OutputVerbDataDictionary to select for each verb occuring in the corpus data the first two sentences of occurance
 */
public class WriteProcessedOutput {

    /**
     * writes 2 selected sentences (for a verb occurance) to two different files
     *
     * @param outputFilename_1
     * @param outputFilename_2
     */
    public static void writeSelectedSentences(String outputFilename_1, String outputFilename_2, String verbkeysAspect) {
        emptyFile(outputFilename_1);
        emptyFile(outputFilename_2);
        emptyFile(verbkeysAspect);
        Map<String, List<OutputVerbData>> outputVerbDataDictionary = OutputVerbDataDictionary.getInstance().outputVerbDataDictionary;
        Set<String> verbKeys = outputVerbDataDictionary.keySet();
        Path path_1 = Paths.get(outputFilename_1);
        Path path_2 = Paths.get(outputFilename_2);
        Path path_3 = Paths.get(verbkeysAspect);

        try (BufferedWriter bufferedWriter_1 = Files.newBufferedWriter(path_1,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_1 = new CSVWriter(bufferedWriter_1);

             BufferedWriter bufferedWriter_2 = Files.newBufferedWriter(path_2,
                     StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                     StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_2 = new CSVWriter(bufferedWriter_2);

             BufferedWriter bufferedWriter_3 = Files.newBufferedWriter(path_3,
                     StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                     StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_3 = new CSVWriter(bufferedWriter_3);) {

            for (String verb : verbKeys) {
                List<OutputVerbData> outputList = outputVerbDataDictionary.get(verb);
                if (outputList.size() > 0) {
                    OutputVerbData firstOutput = outputList.get(0);
                    Verb firstOutputVerb = firstOutput.verb_en;
                    String[] verbData = new String[]{firstOutputVerb.infinitiv, firstOutputVerb.token, firstOutputVerb.aspect, firstOutput.fullSentence_en};
                    writer_1.writeNext(verbData);
                    String[] verbAspect = new String[]{firstOutputVerb.infinitiv, firstOutputVerb.aspect};
                    writer_3.writeNext(verbAspect);
                }
                if (outputList.size() > 1) {
                    OutputVerbData secondOutput = outputList.get(1);
                    Verb secondOutputVerb = secondOutput.verb_en;
                    String[] verbData = new String[]{secondOutputVerb.infinitiv, secondOutputVerb.token, secondOutputVerb.aspect, secondOutput.fullSentence_en};
                    writer_2.writeNext(verbData);
                }

            }
        } catch (IOException e) {
            System.err.println("Error writing file ");
            e.printStackTrace();
        }

    }

    public static void writeVerbKeys(String verbKeyFilename, String verbKeyOccurrenceNumberFilename) {
        emptyFile(verbKeyFilename);
        emptyFile(verbKeyOccurrenceNumberFilename);
        Map<String, List<OutputVerbData>> outputVerbDataDictionary = OutputVerbDataDictionary.getInstance().outputVerbDataDictionary;
        Set<String> verbKeys = outputVerbDataDictionary.keySet();
        Path path = Paths.get(verbKeyFilename);
        Path path_2 = Paths.get(verbKeyOccurrenceNumberFilename);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             BufferedWriter bufferedWriter_2 = Files.newBufferedWriter(path_2,
                     StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                     StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_2 = new CSVWriter(bufferedWriter_2);) {

            for (String verb : verbKeys) {
                writer.append(verb + "\n");
                String[] verbOccurences = new String[]{verb, String.valueOf(outputVerbDataDictionary.get(verb).size())};
                writer_2.writeNext(verbOccurences);

            }
        } catch (IOException e) {
            System.err.println("Error writing file " + path);
            // System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void writeAllSentences(String allSentencesFile) {
        emptyFile(allSentencesFile);
        Map<String, List<OutputVerbData>> outputVerbDataDictionary = OutputVerbDataDictionary.getInstance().outputVerbDataDictionary;
        Set<String> verbKeys = outputVerbDataDictionary.keySet();
        Path path = Paths.get(allSentencesFile);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            for (String verbKey : verbKeys) {
                writer.append(Arrays.toString(outputVerbDataDictionary.get(verbKey).toArray()) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + path);
            // System.out.println(e.getMessage());
            e.printStackTrace();
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


    public static void main(String[] args) throws IOException {
        //dictionary with verbs and translations
        String dictFolder = "output_sentences";
        //String dictFolder = "output_bigCorpus";
        //String oldVerbs = "processed_output/Verb_keys.txt";
        OutputVerbDataDictionary outputProcessor = new OutputVerbDataDictionary(dictFolder);

        //filenames for writing

        String writeKeys = "processed_output/verb_keys.txt";
        String writeKey_occurrenceNumber = "processed_output/verb_keys_occurrenceNumber.csv";
        //String writeKeys = "processed_output_big/verb_keys.txt";
        String writeSelected_1 = "processed_output/selected_1.csv";
        String writeSelected_2 = "processed_output/selected_2.csv";
        String writeVerbAspect = "processed_output/verbKeyAspect.csv";

        //String writeFullOutput = "processed_output_big/full_output.txt"; // too big file, not really necessary
        //String writeSelected_1 = "processed_output_big/selected_3.csv";
        //String writeSelected_2 = "processed_output_big/selected_4.csv";

        //object
        WriteProcessedOutput processedOutput = new WriteProcessedOutput();
        //writeVerbKeys(writeKeys, writeKey_occurrenceNumber);
        writeSelectedSentences(writeSelected_1, writeSelected_2, writeVerbAspect);


    }

}
