import com.google.gson.stream.JsonWriter;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Reads selected_n.csv file (in processed_output) -> write .txt and .json
 */
public class PrepareFinalFiles {
    private String outputFolder;
    private String inputFile;

    //TODO: für input auch Folder

    /**
     *
     * @param inputFile: from processed_output folder
     * @param outputFolder: to final_files
     */
    PrepareFinalFiles(String inputFile, String outputFolder) {
        this.outputFolder = outputFolder;
        this.inputFile = inputFile;
    }

    private int documentCounts = 1;
    private String documentBareName;

    private static final int MAX_LINES = 200;
    private int lineCounts;

    private void raiseLineCounts() {
        lineCounts += 1;
    }

    private int jsonCharCounter = 0;

    private void updateJsonCharCounter(int countPreviousLine) {
        jsonCharCounter += countPreviousLine;
    }

    private void startNewDocument() {
        jsonCharCounter = 0;
        lineCounts = 0;
        documentCounts += 1;
    }

    /**
     * reads .csv from processed_output and writes txt and json
     */
    private void readAndWriteFile() {
        emptyFiles(this.outputFolder);

        Path path = Paths.get(this.inputFile);

        String bareFile = path.getFileName().toString();
        String bareName = bareFile.substring(0, bareFile.indexOf("."));
        documentBareName = bareName;

        try (BufferedReader bufferedReader = Files.newBufferedReader(path);
             CSVReader csvReader = new CSVReader(bufferedReader)) {
            String[] nextLine;

            List<List<Integer>> jsonSpans = new ArrayList<>();
            while ((nextLine = csvReader.readNext()) != null) {

                String verbToken = nextLine[1].trim();
                String fullSentence = nextLine[3].trim();

                String outputFile = getOutputName();

                //append json list with the jsonCharCounter for the previous line
                List<Integer> span = Arrays.asList(getSpan(verbToken, fullSentence));
                jsonSpans.add(span);

                //also updates jsonCharCounter
                writeSentence(outputFile, fullSentence);

                if (lineCounts < MAX_LINES) {
                    raiseLineCounts();
                } else {
                    //write json file and start a new document
                    writeJson(getOutputName(), jsonSpans);
                    jsonSpans = new ArrayList<>();
                    startNewDocument();
                }
            }

            if (!jsonSpans.isEmpty())
                writeJson(getOutputName(), jsonSpans);

        } catch (IOException e) {
            System.err.println("Error reading file " + inputFile);
            e.printStackTrace();
        }
    }

    /**
     * finds the begin and end index of a verb in a sentence
     *
     * @param verb
     * @param sentence
     * @return
     */
    private Integer[] getSpan(String verb, String sentence) {
        String temp = String.format("\\b%s\\b", verb);
        Pattern p = Pattern.compile(temp);
        Matcher m = p.matcher(sentence);
        int begin = -2;
        int end = -1;
        if (m.find() == true) {
            begin = m.start();
            end = m.end();
        }

        if ((begin < 0) || (end > sentence.length())) {
            System.err.println("The chosen spans do not match.");
            System.err.println("begin " + begin + " end " + end + " " + verb + " " + sentence);
            throw new IndexOutOfBoundsException();
        }

        return new Integer[]{begin + jsonCharCounter, end + jsonCharCounter};
    }

    private String getOutputName() {
        return this.outputFolder + "/" + documentBareName + "_" + String.valueOf(documentCounts);
    }

    /**
     * writes a line/sentence to a txt file
     *
     * @param outputFileName
     * @param sentence
     */
    private void writeSentence(String outputFileName, String sentence) {
        String outputFile = outputFileName + ".txt";
        Path path = Paths.get(outputFile);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            String data = sentence + "\n" + "\n";
            writer.append(data);
            // update how many chars were already written in the previous line
            updateJsonCharCounter(data.length());


        } catch (IOException e) {
            System.err.println("Error writing file " + outputFile);
            e.printStackTrace();
        }
    }

    /**
     * writes a complete json file
     *
     * @param outputFileName
     * @param spanInfo       - list with spans for the whole document
     */
    private static void writeJson(String outputFileName, List<List<Integer>> spanInfo) {
        String outputFile = outputFileName + ".json";
        try (JsonWriter writer = new JsonWriter(new FileWriter(outputFile))) {
            writer.beginObject();
            writer.name("targets");
            writer.beginArray();
            for (List<Integer> span : spanInfo) {
                writer.beginObject();
                int begin = span.get(0);
                int end = span.get(1);
                writer.name("end").value(end);
                writer.name("type").value("Situation Entity");
                writer.name("begin").value(begin);
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();

        } catch (IOException e) {
            System.err.println("Error writing file " + outputFile);
            e.printStackTrace();
        }


    }

    private static void emptyFile(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Cannot access " + filename);
            e.printStackTrace();
        }
    }

    private static void emptyFiles(String folderName) {
        try {
            List<String> outputFileNames = Files.walk(Paths.get(folderName))
                    .filter(Files::isRegularFile).map(Path::toString)
                    .collect(Collectors.toList());

            for (String file : outputFileNames) {
                emptyFile(file);
            }
        } catch (IOException e) {
            System.err.println("Cannot access " + folderName);
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String inputFile_1 = "processed_output/selected_1.csv";
        String finalFilesFolder = "final_files";

        PrepareFinalFiles finalFilesProcessor = new PrepareFinalFiles(inputFile_1, finalFilesFolder);
        finalFilesProcessor.readAndWriteFile();

    }
}
