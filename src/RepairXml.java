import java.io.BufferedWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RepairXml {


    public static void readAndSplit(String inputFilename, String folderName) {
        Path path = Paths.get(inputFilename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            StringBuilder text = new StringBuilder();
            String name = "";
            //System.out.println("hi");
            for (String line = reader.readLine(); ; ) {
                if (line.startsWith("<linkGrp")) {
                    int first = line.indexOf("\'");
                    int second = line.indexOf(".", first + 1);
                    name = line.substring(first + 1, second);
                    //System.out.println(name);
                }

                text.append(line + "\n");

                line = reader.readLine();

                if (line == null || line.startsWith("<?xml")) {
                    String pathName = folderName + "/" + name + ".xml";
                    //System.out.println("PN " + pathName);
                    writeToFile(text.toString(), pathName);
                    if (line == null) break;
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
        String filename_en2cs = "C:/Users/Dana/en2cs/intercorp_en2cs.xml";
        String folderName = "../correspondences_intercorp_en2cs";
        readAndSplit(filename_en2cs, folderName);

    }

}
