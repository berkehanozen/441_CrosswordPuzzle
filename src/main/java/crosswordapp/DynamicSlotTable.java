package crosswordapp;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class DynamicSlotTable {

    public static int HORIZONTAL = 0;
    public static int VERTICAL = 1;

    private ArrayList<Node> table = new ArrayList<>();
    private ArrayList<String> wordList = new ArrayList<>();

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    public DynamicSlotTable(){}

    public DynamicSlotTable(String [][] grid){
        int length = 0;
        int begin = 0;
        boolean didBegin = false;
        int end = 0;
        String word = "";
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid.length; j++){
                if(!grid[i][j].equals("*") && j == grid.length - 1){
                    if(didBegin && length > 1) {
                        length++;
                        table.add(createNode(i, begin, HORIZONTAL, length));
                        if(!grid[i][j].equals(" ")){
                            word = word.concat(grid[i][j]);
                            wordList.add(word);
                        }
                        else{
                            word = word.concat(".");
                            wordList.add(word);
                        }
                    }
                    word = "";
                    didBegin = false;
                    length = 0;
                    begin = 0;
                    end = 0;
                }
                else if(!grid[i][j].equals("*")){
                    if(!didBegin) {
                        didBegin = true;
                        begin = j;
                    }
                    if(!grid[i][j].equals(" ")){
                        word = word.concat(grid[i][j]);
                    }
                    else{
                        word = word.concat(".");
                    }
                    length++;
                }
                else if(grid[i][j].equals("*")){
                    if(didBegin && length > 2) { //Bunu duruma göre büyüktür 2 falan da yapabilirik.
                        end = begin + length - 1;
                        table.add(createNode(i, begin, HORIZONTAL, length));
                        wordList.add(word);
                    }
                    word = "";
                    didBegin = false;
                    length = 0;
                    begin = 0;
                    end = 0;
                }
            }
        }
        for(int j = 0; j < grid.length; j++){
            for(int i = 0; i < grid.length; i++){
                if(!grid[i][j].equals("*") && i == grid.length - 1){
                    if(didBegin && length > 1) {
                        length++;
                        table.add(createNode(begin, j, VERTICAL, length));
                        if(!grid[i][j].equals(" ")){
                            word = word.concat(grid[i][j]);
                            wordList.add(word);
                        }
                        else{
                            word = word.concat(".");
                            wordList.add(word);
                        }
                    }
                    word = "";
                    didBegin = false;
                    length = 0;
                    begin = 0;
                    end = 0;
                }
                else if(!grid[i][j].equals("*")){
                    if(!didBegin) {
                        didBegin = true;
                        begin = i;
                    }
                    if(!grid[i][j].equals(" ")){
                        word = word.concat(grid[i][j]);
                    }
                    else{
                        word = word.concat(".");
                    }
                    length++;
                }
                else if(grid[i][j].equals("*")){
                    if(didBegin && length > 2) { //Bunu duruma göre büyüktür 2 falan da yapabilirik.
                        end = begin + length - 1;
                        table.add(createNode(begin, j, VERTICAL, length));
                        wordList.add(word);
                    }
                    word = "";
                    length = 0;
                    begin = 0;
                    end = 0;
                    didBegin = false;
                }
            }
        }
    }


    public void add(Node n){
        table.add(n);
    }

    public int size(){
        return table.size();
    }

    public Node get(int i){
        return table.get(i);
    }

    public Node remove(int i){
        return table.remove(i);
    }

    public void addAll(DynamicSlotTable dst){
        for (Node n: dst.table) {
            Node t = createNode(n.getRow(), n.getColumn(), n.getOrientation(), n.getWordLength());
            t.setWordSize(n.getWordSize());
            ArrayList<String> words = new ArrayList<>();
            words.addAll(n.getWords());
            t.setWords(words);
            table.add(t);
        }
    }

    public Node createNode(int row, int column, int orientation, int wordLength){
        return new Node(row, column, orientation, wordLength, 0, null);
    }

    class Node{

        private int row;
        private int column;
        private int orientation;
        private int wordLength;
        private int wordSize;
        private ArrayList<String> words;

        public Node(int row, int column, int orientation, int wordLength, int wordSize, ArrayList<String> words) {
            this.row = row;
            this.column = column;
            this.orientation = orientation;
            this.wordLength = wordLength;
            this.wordSize = wordSize;
            this.words = words;
        }

        public int getWordLength() {
            return wordLength;
        }

        public void setWordLength(int wordLength) {
            this.wordLength = wordLength;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getWordSize() {
            return wordSize;
        }

        public void setWordSize(int wordSize) {
            this.wordSize = wordSize;
        }

        public ArrayList<String> getWords() {
            return words;
        }

        public void setWords(ArrayList<String> words) {
            this.words = words;
        }
    }
}

