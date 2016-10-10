import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonWriter;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class PrepareFinalFiles {
    // read .csv file -> write .txt and .json
    String outputFolder;
    String inputFile;

    //TODO: für input auch Folder
    PrepareFinalFiles(String inputFile, String outputFolder) {
        this.outputFolder = outputFolder;
        this.inputFile = inputFile;
    }

    private static int documentCounts = 1;

    public static void raiseDocumentCounts() {
        documentCounts += 1;
    }

    public static int getDocumentCounts() {
        return documentCounts;
    }

    private static String documentBareName;

    public static void setDocumentBareName(String name) {
        documentBareName = name;
    }

    public static String getDocumentBareName() {
        return documentBareName;
    }

    public static final int MAX_LINES = 200;
    private static int lineCounts;

    public static void resetLineCounts() {
        lineCounts = 0;
    }

    public static void raiseLineCounts() {
        lineCounts += 1;
    }

    private static int jsonCharCounter = 0;

    public static void updateJsonCharCounter(int countPreviousLine) {
        jsonCharCounter += countPreviousLine;
    }

    public static int getJsonCharCounter() {
        return jsonCharCounter;
    }

    public static void resetJsonCharCounter() {
        jsonCharCounter = 0;
    }


    public void readAndWriteFile() {
        emptyFiles(this.outputFolder);

        Path path = Paths.get(this.inputFile);

        String bareFile = path.getFileName().toString();
        String bareName = bareFile.substring(0, bareFile.indexOf("."));
        setDocumentBareName(bareName);

        try (BufferedReader bufferedReader = Files.newBufferedReader(path);
             CSVReader csvReader = new CSVReader(bufferedReader)) {
            String[] nextLine;
            OutputStream out;

            List<List<Integer>> jsonSpans = new ArrayList<>();
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length <= 1) {
                    break;
                }
                String verbToken = nextLine[1].trim();
                String fullSentence = nextLine[3].trim();

                String outputFile = getOutputName();

                if (lineCounts < MAX_LINES) {
                    raiseLineCounts();
                } else {
                    //write json file
                    //System.out.println(getOutputName() + " " + jsonSpans);
                    writeJson(getOutputName(), jsonSpans);
                    jsonSpans = new ArrayList<>();
                    resetJsonCharCounter();

                    raiseDocumentCounts(); // also renames the files
                    resetLineCounts();
                }

                //TODO: add json dingsi
                //append json list with the jsonCharCounter for the previous line
                List<Integer> span = Arrays.asList(getSpan(verbToken, fullSentence));
                jsonSpans.add(span);

                //also updates jsonCharCounter
                writeSentence(outputFile, fullSentence);

            }

        } catch (IOException e) {
            System.err.println("Error reading file " + inputFile);
            e.printStackTrace();

        }
    }

    public Integer[] getSpan(String verb, String sentence) {
        int begin = sentence.indexOf(verb);
        int end = begin + verb.length() - 1;
        if ((begin < 0) || (end > sentence.length())) {
            System.err.println("The chosen spans do not match.");
            System.err.println("begin " + begin + " end " + end + " " + verb + " " + sentence);
            throw new IndexOutOfBoundsException();
        }
        //System.out.println(getOutputName());
        //System.out.println(">>>>>>>" + begin + "-" + end + " " + verb + " " + sentence + "<<< " + sentence.length());
        Integer[] spanInfo = new Integer[]{begin + getJsonCharCounter(), end + getJsonCharCounter()};
        //System.out.println(Arrays.toString(spanInfo) + " " + verb + " " + sentence + "\n");
        return spanInfo;
    }

    public String getOutputName() {
        String newName = this.outputFolder + "/" + getDocumentBareName() + "_" + String.valueOf(getDocumentCounts());
        return newName;
    }

    public static void writeSentence(String outputFileName, String sentence) {
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


    public static void writeJson(String outputFileName, List<List<Integer>> spanInfo) {
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

    public static void emptyFile(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Cannot access " + filename);
            System.err.println(e);
            return;
        }
    }

    public static void emptyFiles(String folderName) {
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
        // final_files/selected_1_1.txt

        PrepareFinalFiles finalFilesProcessor = new PrepareFinalFiles(inputFile_1, finalFilesFolder);
        finalFilesProcessor.readAndWriteFile();

    }
}
