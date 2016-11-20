import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Container Class
 */
class VallexGlosbeVerb {
    String czVerb;
    String aspect;
    Set<String> enTranslations;

    /**
     * @param czVerb
     * @param aspect
     * @param enTranslations
     */
    VallexGlosbeVerb(String czVerb, String aspect, Set<String> enTranslations) {
        this.czVerb = czVerb;
        this.aspect = aspect;
        this.enTranslations = enTranslations;
    }

    @Override
    public String toString() {
        //String translationRepr = Arrays.toString(this.enTranslations.toArray());
        String translationRepr = this.enTranslations.toString();
        return this.czVerb + "\n" + this.aspect + "\n" + translationRepr + "\n";
    }
}

/**
 * Class containing methods that create a VallexGlosbeDictionary by looking up czech Vallex Verbs in Glosbe and finding List of english translations
 * Once created, an instance of the VallexGlosbeDictionary can be used for further processing.
 */
public class VallexGlosbeDictionary {

    private static VallexGlosbeDictionary instance;

    public static VallexGlosbeDictionary getInstance() {
        return instance;
    }

    public VallexGlosbeVerb get(String verb) {
        return this.vallexGlosbeDictionary.get(verb);
    }


    Map<String, VallexGlosbeVerb> vallexGlosbeDictionary;
    String dictFilename;


    /**
     * @param dictFilename: output file -> vallex/dictionary.csv
     * @throws IOException
     */
    VallexGlosbeDictionary(String dictFilename) throws IOException {
        vallexGlosbeDictionary = readDictionary(dictFilename);
        this.dictFilename = dictFilename;
        instance = this;
    }

    /**
     * Iterates through the Vallex verbs collection, looks a verb up in Glosbe
     * (only these which have aspect value "perf" or "imperf" and don't have homographs)
     * and writes its translations
     * @param inputFilename: vallex verbs -> vallex\vallex_aspectOutput.txt
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, VallexGlosbeVerb> queryEntries(String inputFilename) throws IOException, InterruptedException {
        Path path = Paths.get(inputFilename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                int till = line.indexOf(" ");
                String aspect = line.substring(0, till).trim();
                String verb = line.substring(till + 1).trim();

                if (vallexGlosbeDictionary.containsKey(verb))
                    continue;

                if (aspect.equals("pf") || aspect.equals("impf")) {
                    //for now the homographs (marked in VALLEX with a digit as a last character get disregarded)
                    // as their perfectivity value may be different and their meanings too
                    String[] ve = verb.split(" ");
                    boolean condition = Character.isDigit(ve[0].charAt(ve[0].length() - 1)); //checks for homographs
                    if (!condition) { //if not a homograph
                        //initialize a verb
                        //get en translations
                        String jsonString = lookupVerb(verb);
                        Set<String> translations = getTranslations(jsonString);
//                        if (translations.size() == 0) {
//                        }
                        //TODO: do I want to keep both dicts? if not, translation.size - abfrage kommt hier
                        VallexGlosbeVerb entry = new VallexGlosbeVerb(verb, aspect, translations);
                        writeDictionary(entry);
                    }
                }
            }
        }
        return vallexGlosbeDictionary;
    }

    /**
     * Loads an instance of the already ready VallexGlosbeDictionary
     * @param filename : vallexGlosbeDictionary -> vallex/dictionary.csv
     * @return
     * @throws IOException
     */
    static Map<String, VallexGlosbeVerb> readDictionary(String filename) throws IOException {
        Map<String, VallexGlosbeVerb> dictionary = new HashMap<>();
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '"');
        //Read CSV line by line and use the string array
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String verb = nextLine[0];
            String aspect = nextLine[1];
            String translationString = nextLine[2];
            Set<String> rawTranslations = new HashSet<>(Arrays.asList((translationString.substring(1, translationString.length() - 1)).split(",")));
            Set<String> translations = new HashSet<>();
            for (String translation : rawTranslations) {
                translations.add(translation.trim());
            }
            VallexGlosbeVerb verbObject = new VallexGlosbeVerb(verb, aspect, translations);
            dictionary.put(verb, verbObject);
        }
        return dictionary;
    }

    /**
     * Looks up the czech verbs in Glosbe
     * @param verb
     * @return jsonString
     * @throws IOException
     * @throws InterruptedException
     */
    public static String lookupVerb(String verb) throws
            IOException, InterruptedException {
        //TODO: proper way to deal with exceptions?
        String ve = URLEncoder.encode(verb, "UTF-8");
        String rawURL = "https://glosbe.com/gapi/translate?from=%s&dest=%s&format=json&phrase=%s&pretty=true";
        //String rawURL = "http://wtfismyip.com/text";
        String inputLanguage = "cs";
        String outputLanguage = "en";
        String urlString = String.format(rawURL, inputLanguage, outputLanguage, ve);

        URL url = new URL(urlString);
        String jsonString = null;

        try (InputStream stream = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder buffer = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            jsonString = buffer.toString();
            Thread.sleep(5000);

        }
        return jsonString;
    }



    /**
     * Extracts the translations from the Glosbe jsonString
     * @param jsonString
     * @return
     */
    //jsonString kann nicht null sein, nur "tuc": [] kann leer sein
    public static Set<String> getTranslations(String jsonString) {
        Set<String> translations = new HashSet<>();
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray entry = jsonObject.getAsJsonArray("tuc");
        //System.out.println(entry);
        for (JsonElement dict : entry) {

            JsonObject dictObj = dict.getAsJsonObject();
            if (!dictObj.has("phrase"))
                continue;
            JsonObject phraseObj = dictObj.getAsJsonObject("phrase");
            if (!phraseObj.has("text"))
                continue;
            String translationRaw = phraseObj.get("text").toString();
            String translation = translationRaw.substring(1, translationRaw.length() - 1);
            if (translation.startsWith("to "))
                translation = translation.replace("to", "");
            translations.add(translation.trim());
//            System.out.println(translation);
        }
        //System.out.println(">>>" + translations);
        //System.out.println(translations);
        return translations;
    }

    public void writeDictionary(VallexGlosbeVerb verb) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(dictFilename, true))) {
            String[] record = verb.toString().split("\n");
            writer.writeNext(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String vallex_aspect_path = "vallex\\vallex_aspectOutput.txt";
        String dictionaryFile = "vallex/dictionary.csv";

//        String verb = "analyzovat";
//        //String verb = "rozmýšlet";
//        String jsonString = lookupVerb(verb);
//        //System.out.println(jsonString);
//        System.out.println(getTranslations(jsonString));

        //VallexGlosbeDictionary vallexGlosbeDictionary = new VallexGlosbeDictionary(dictionaryFile);
        //System.out.println(vallexGlosbeDictionary.vallexGlosbeDictionary.size());
        //vallexGlosbeDictionary.queryEntries(vallex_aspect_path);


    }

}
