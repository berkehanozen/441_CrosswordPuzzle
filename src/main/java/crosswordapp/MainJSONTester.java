package crosswordapp;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.Collator;
import java.util.Locale;

public class MainJSONTester {

    //Gets file from resources folder
    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }

    public static void main(String[] args) {
        File jsonFile = new MainJSONTester().getFileFromResources("words_6000.json");
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
