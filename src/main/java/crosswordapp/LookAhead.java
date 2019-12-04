package crosswordapp;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class LookAhead {

    public static int HORIZONTAL = 0;
    public static int VERTICAL = 1;

    private DynamicSlotTable table;
    private ArrayList<String> wordList;
    private ArrayList<String> gridWords = new ArrayList<>();

    public LookAhead(String[][] grid) {
        table = new DynamicSlotTable(grid);
        wordList = table.getWordList();
    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }

    public void firstStep() {
        File jsonFile = getFileFromResources("words_6000.json");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(new JSONTokener(new FileInputStream(jsonFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < table.size(); i++) {
            ArrayList<String> words = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                String jThWord = jsonArray.getJSONObject(j).getString("kelime");
                if (jThWord.length() == table.get(i).getWordLength()) {
                    if (Pattern.matches(wordList.get(i), jThWord.toUpperCase()))
                        words.add(jThWord);
                }
            }
            table.get(i).setWords(words);
            table.get(i).setWordSize(words.size());
        }
    }

    public boolean secondStep() {
        boolean remove = false;
        for (int i = 0; i < table.size(); i++) {
            DynamicSlotTable.Node n = table.get(i);
            int orientation = n.getOrientation();
            int begin;
            int beginIndex;
            int end;
            if (orientation == HORIZONTAL) {
                begin = n.getColumn();
                beginIndex = n.getRow();
            } else {
                begin = n.getRow();
                beginIndex = n.getColumn();
            }
            end = begin + n.getWordLength() - 1;
            ArrayList<String> nWords = new ArrayList<>();
            nWords.addAll(n.getWords());
            for (int j = 0; j < n.getWordSize(); j++) {
                String s = n.getWords().get(j);
                for (int k = 0; k < table.size(); k++) {
                    if (k == i)
                        continue;

                    DynamicSlotTable.Node other = table.get(k);
                    if (other.getOrientation() == orientation)
                        continue;

                    ArrayList<String> otherWords = new ArrayList<>();
                    otherWords.addAll(other.getWords());
                    if (otherWords.contains(s))
                        otherWords.remove(s);

                    int beginOther;
                    int otherIndex;
                    int endOther;
                    if (other.getOrientation() == HORIZONTAL) {
                        beginOther = other.getColumn();
                        otherIndex = other.getRow();
                    } else {
                        beginOther = other.getRow();
                        otherIndex = other.getColumn();
                    }
                    endOther = beginOther + other.getWordLength() - 1;
                    if (otherIndex <= end && otherIndex >= begin && beginOther <= beginIndex && endOther >= beginIndex) {
                        /*System.out.println(s);
                        System.out.println(n.getRow()+", "+ n.getColumn());
                        System.out.println(other.getRow()+", "+ other.getColumn());
                        System.out.println("---");*/
                        int index = beginIndex - beginOther;
                        char c = s.charAt(otherIndex - begin);
                        StringBuilder search = new StringBuilder("");
                        for (int z = 0; z < other.getWordLength(); z++) {
                            if (z != index) {
                                search.append(".");
                            } else
                                search.append("" + c);
                        }
                        int count = 0;
                        for (int t = 0; t < otherWords.size(); t++) {
                            if (Pattern.matches(search.toString(), otherWords.get(t))) {
                                count++;
                                break;
                            }
                        }
                        if (count == 0) {
                            nWords.remove(s);
                            remove = true;
                            break;
                        }
                    }
                }
            }
            n.setWords(nWords);
            n.setWordSize(nWords.size());
        }
        return remove;
    }

    public void thirdStep() {
        int i = 0;
        boolean check;
        while (true) {
            check = secondStep();
            System.out.println("This is:" + i);
            i++;
            if (!check)
                break;
        }
        //fourthStep();

    }

    public boolean fourthStep(DynamicSlotTable dst, int beginning) {
        if(beginning == 1)
            dst = table;

        if(dst.size() == 1){
            if(dst.get(0).getWordSize() > 0){
                gridWords.add(dst.get(0).getWords().get(0));
                return true;
            }
        }
        DynamicSlotTable.Node n = dst.get(0);
        int rmIndex = 0;
        for(int k = 0; k < dst.size(); k++){
            if(dst.get(k).getWordSize() < n.getWordSize()) {
                n = dst.get(k);
                rmIndex = k;
            }
        }
        ArrayList<String> wordsOfN = new ArrayList<>();
        wordsOfN.addAll(n.getWords());
        if(n.getWordSize() == 0)
            return false;
        for(int i = 0; i < wordsOfN.size(); i++){
            DynamicSlotTable table2 = new DynamicSlotTable();
            table2.addAll(dst);
            table2.remove(rmIndex);
            String s = wordsOfN.get(0);
            addWord(n, s, table2);
            table = table2;
            thirdStep();
            if(fourthStep(table2, 0)) {
                gridWords.add(s);
                return true;
            }
            wordsOfN.remove(s);
        }
        return false;
    }

    public void gridWord(){
        for(int i = 0; i < gridWords.size(); i++)
            System.out.println(gridWords.get(i));
    }

    public void addWord(DynamicSlotTable.Node n, String s, DynamicSlotTable dst){
        int orientation = n.getOrientation();
        int begin;
        int beginIndex;
        int end;
        if (orientation == HORIZONTAL) {
            begin = n.getColumn();
            beginIndex = n.getRow();
        } else {
            begin = n.getRow();
            beginIndex = n.getColumn();
        }
        end = begin + n.getWordLength() - 1;
        for (int k = 0; k < dst.size(); k++) {
            DynamicSlotTable.Node other = dst.get(k);
            ArrayList<String> otherWords = new ArrayList<>();
            otherWords.addAll(other.getWords());
            if (otherWords.contains(s))
                otherWords.remove(s);

            if (other.getOrientation() == orientation){
                other.setWords(otherWords);
                other.setWordSize(otherWords.size());
                continue;
            }

            int beginOther;
            int otherIndex;
            int endOther;
            if (other.getOrientation() == HORIZONTAL) {
                beginOther = other.getColumn();
                otherIndex = other.getRow();
            } else {
                beginOther = other.getRow();
                otherIndex = other.getColumn();
            }
            endOther = beginOther + other.getWordLength() - 1;
            if (otherIndex <= end && otherIndex >= begin && beginOther <= beginIndex && endOther >= beginIndex) {
                int index = beginIndex - beginOther;
                char c = s.charAt(otherIndex - begin);
                StringBuilder search = new StringBuilder("");
                for (int z = 0; z < other.getWordLength(); z++) {
                    if (z != index) {
                        search.append(".");
                    } else
                        search.append(c);
                }
                for (int t = 0; t < other.getWordSize(); t++) {
                    if (!Pattern.matches(search.toString(), other.getWords().get(t))) {
                        otherWords.remove(other.getWords().get(t));
                    }
                }
            }
            other.setWords(otherWords);
            other.setWordSize(otherWords.size());
        }

    }

    public void display() {
        //Object[] array = table.toArray();
        System.out.println("DİPLAYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYy");
        for (int i = 0; i < table.size(); i++) {
            DynamicSlotTable.Node n = table.get(i);
            String orientation = n.getOrientation() == 0 ? "H" : "V";
            System.out.println(n.getRow() + ", " + n.getColumn() + ", " + orientation + ", " + n.getWordLength() + ", " + wordList.get(i));
            for (int j = 0; j < table.get(i).getWords().size(); j++) {
                System.out.println(table.get(i).getWords().get(j));
            }
            System.out.println("--------------------------------------------------");
        }
    }

}
