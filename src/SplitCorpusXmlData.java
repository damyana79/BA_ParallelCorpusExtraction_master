import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The class is used to split the files with the actual texts - intercorp_en and intercorp_cs into smaller files containing just one book
 */
public class SplitCorpusXmlData {

    /**
     * @param inputFilename: correct path to intercorp_cs.xml/intercorp_en
     * @param folderName:    output folder ->  intercorp_cs/intercorp_en
     */
    public static void readAndSplit(String inputFilename, String folderName) {
        Path path = Paths.get(inputFilename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            StringBuilder text = new StringBuilder();
            String name = "";
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("<doc")) {
                    int first = line.indexOf("\"");
                    int second = line.indexOf("\"", first + 1);
                    name = line.substring(first + 1, second);
                }
                text.append(line + "\n");

                //beginn des nächsten documents
                if (line.startsWith("</doc")) {
                    String pathName = folderName + "/" + name + ".xml";
                    writeToFile(text.toString(), pathName);

                    text = new StringBuilder();
                    System.gc();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading file " + inputFilename);
            e.printStackTrace();
            return;
        }
    }

    public static void writeToFile(String text, String outputFileName) {
        Path path = Paths.get(outputFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {

            writer.append(text);
        } catch (IOException e) {
            System.err.println("Error writing file " + path);
            // System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //String filename_en2cs = "test/test_en2cs.xml";
        String filename = "C:/Users/Dana/en2cs/intercorp_cs.xml";
        String folderName = "intercorp_cs";
        readAndSplit(filename, folderName);

    }

}
