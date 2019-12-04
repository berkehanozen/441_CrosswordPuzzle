package crosswordapp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.HashSet;
import java.util.Locale;

public class MainJSONTester {

    //Gets file from resources folder
    //Even if it doesn't exists.
    private static File getFileFromResources(String fileName) {

        ClassLoader classLoader = MainJSONTester.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            try {
                URI resourcePathFile = System.class.getResource("/RESOURCE_PATH").toURI();
                String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
                return new File(new File("").toURI().relativize(new File(resourcePath).toURI())+fileName);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return new File(resource.getFile());
        }

    }

    private static void sampleTheJSONFile(File originalFile, File output){
        JSONArray jsonArray = null;
        JSONArray newArr = new JSONArray();
        try {
            jsonArray = new JSONArray(new JSONTokener(new FileInputStream(originalFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < jsonArray.length(); i++){
            String word = jsonArray.getJSONObject(i).getString("madde");
            if(word.length()>2 && binarySearch(newArr,word) == -1 && Character.isLetter(word.charAt(0)) && !Character.isUpperCase(word.charAt(0)) && Character.isLetter(word.charAt(word.length()-1))
                    && !word.contains("-") && !word.contains(" ") && !word.contains("â") && !word.contains("Â")
                    && !word.contains("û") && !word.contains("Û") && !word.contains("î") && !word.contains("Î")){
                JSONObject obj = new JSONObject();
                obj.put("kelime",word);
                newArr.put(obj);
            }
        }
        try {
            Files.write(output.toPath(),newArr.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File jsonFile = new MainJSONTester().getFileFromResources("words_simplified.json");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(new JSONTokener(new FileInputStream(jsonFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String longest = "";
        String shortest = "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";
        for(int i = 0; i < jsonArray.length(); i++){
            String iThWord = jsonArray.getJSONObject(i).getString("kelime");
            if(binarySearch(jsonArray, iThWord)!= i){   //works fine!
                System.out.println("Binary search guessed wrong!");
                System.out.println("i: " + i + " guessed: " + binarySearch(jsonArray, iThWord));
                System.out.println("word: "+ iThWord);
            }
            if(longest.length() < iThWord.length())
                longest = iThWord;
            if(shortest.length() > iThWord.length())
                shortest = iThWord;
        }
        System.out.println("First word in the corpus is: " + jsonArray.getJSONObject(0).getString("kelime"));
        System.out.println("Shortest word in the corpus is: " + shortest);
        System.out.println("Longest word in the corpus is: " + longest);
        System.out.println("Corpus contains " + jsonArray.length() + " words.");

//        sampleTheJSONFile(getFileFromResources("words.json"),getFileFromResources("words_simplified.json"));
    }

    public static int binarySearch(JSONArray jsonArray,String word){
        Collator collator = Collator.getInstance(new Locale("tr", "TR"));   //turkce sorted oldugu icin bu sekilde compare yapmamiz gerek.
        return binaryRec(jsonArray,word.toLowerCase(),0,jsonArray.length()-1, collator);
    }

    private static int binaryRec(JSONArray arr, String word, int start, int end, Collator coll){
        if(start>end)
            return -1;
        int middle = (start + end) /2;
        String middleWord = arr.getJSONObject(middle).getString("kelime").toLowerCase();
        if(middleWord.equals(word)){
            return middle;
        }else if(coll.compare(middleWord,word) > 0){
            return binaryRec(arr, word, start, middle-1,coll);
        }else{
            return binaryRec(arr, word, middle+1, end, coll);
        }
    }

}
