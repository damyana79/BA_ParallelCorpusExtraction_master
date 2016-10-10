import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class WriteProcessedOutput {


    public static void writeSelectedSentences(String outputFilename_1, String outputFilename_2) {
        emptyFile(outputFilename_1);
        emptyFile(outputFilename_2);
        Map<String, List<OutputVerbData>> outputVerbDataDictionary = OutputVerbDataDictionary.getInstance().outputVerbDataDictionary;
        Set<String> verbKeys = outputVerbDataDictionary.keySet();
        Path path_1 = Paths.get(outputFilename_1);
        Path path_2 = Paths.get(outputFilename_2);
        try (BufferedWriter bufferedWriter_1 = Files.newBufferedWriter(path_1,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_1 = new CSVWriter(bufferedWriter_1);

             BufferedWriter bufferedWriter_2 = Files.newBufferedWriter(path_2,
                     StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                     StandardOpenOption.APPEND, StandardOpenOption.WRITE);
             CSVWriter writer_2 = new CSVWriter(bufferedWriter_2);) {

            for (String verb : verbKeys) {
                List<OutputVerbData> outputList = outputVerbDataDictionary.get(verb);
                if (outputList.size() > 0) {
                    OutputVerbData firstOutput = outputList.get(0);
                    Verb firstOutputVerb = firstOutput.verb_en;
                    String[] verbData = new String[]{firstOutputVerb.infinitiv, firstOutputVerb.token, firstOutputVerb.aspect, firstOutput.fullSentence_en};
                    writer_1.writeNext(verbData);
                } if (outputList.size() > 1) {
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

    public static void writeVerbKeys(String verbKeyFilename) {
        emptyFile(verbKeyFilename);
        Map<String, List<OutputVerbData>> outputVerbDataDictionary = OutputVerbDataDictionary.getInstance().outputVerbDataDictionary;
        Set<String> verbKeys = outputVerbDataDictionary.keySet();
        Path path = Paths.get(verbKeyFilename);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {

            for (String verb : verbKeys) {
                writer.append(verb + "\n");
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
            System.err.println(e);
            return;
        }
    }


    public static void main(String[] args) throws IOException {
        //dictionary with verbs and translations
        String dictFolder = "output_sentences";
        OutputVerbDataDictionary outputProcessor = new OutputVerbDataDictionary(dictFolder);

        //filenames for writing
        String writeKeys = "processed_output/verb_keys.txt";
        String writeFullOutput = "processed_output/full_output.txt";
        String writeSelected_1 = "processed_output/selected_1.csv";
        String writeSelected_2 = "processed_output/selected_2.csv";

        //object
        WriteProcessedOutput processedOutput = new WriteProcessedOutput();
        //writeVerbKeys(writeKeys);
        writeSelectedSentences(writeSelected_1, writeSelected_2);


    }

}
