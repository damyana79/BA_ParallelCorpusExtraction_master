import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileWriter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


class VallexGlosbeVerb {
    String czVerb;
    String aspect;
    Set<String> enTranslations;

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

    VallexGlosbeDictionary(String dictFilename) throws IOException {
        vallexGlosbeDictionary = readDictionary(dictFilename);
        this.dictFilename = dictFilename;
        instance = this;
    }

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
                    //TODO: for now the homographs (marked in VALLEX with a digit as a last character get disregarded)
                    // as their perfectivity value may be different and their meanings too
                    String[] ve = verb.split(" ");
                    boolean condition = Character.isDigit(ve[0].charAt(ve[0].length() - 1));
                    if (!condition) {
                        //initialize a verb
                        //get en translations
                        //System.out.println(verb);
                        String jsonString = lookupVerb(verb);
                        //System.out.println(jsonString);
                        Set<String> translations = getTranslations(jsonString);
                        if (translations.size() == 0) {
                        }
                        //TODO: do I want to keep both dicts? if not, translation.size - abfrage kommt hier
                        VallexGlosbeVerb entry = new VallexGlosbeVerb(verb, aspect, translations);
                        writeDictionary(entry);

//                        if (translations.size() != 0) {
//                            this.vallexGlosbeDictionary.put(verb, entry);
//                        } else {
//                            this.vallexVerbWithoutTranslation.put(verb, entry);
//                        }
                    }
                }
            }
        }
        return vallexGlosbeDictionary;
    }

    static Map<String, VallexGlosbeVerb> readDictionary(String filename) throws IOException {
        Map<String, VallexGlosbeVerb> dictionary = new HashMap<>();
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '"');
        //Read CSV line by line and use the string array as you want
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String verb = nextLine[0];
            String aspect = nextLine[1];
            String translationString = nextLine[2];
            Set<String> rawTranslations = new HashSet<>(Arrays.asList((translationString.substring(1, translationString.length()-1)).split(",")));
            Set<String> translations= new HashSet<>();
            for (String translation : rawTranslations) {
                translations.add(translation.trim());
            }
            VallexGlosbeVerb verbObject = new VallexGlosbeVerb(verb, aspect, translations);
            dictionary.put(verb, verbObject);
        }
        return dictionary;
    }


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

    //TODO: Annahme: jsonString kann nicht null sein, nur "tuc": [] kann leer sein
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
