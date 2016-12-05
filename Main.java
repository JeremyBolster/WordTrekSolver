package com.company.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {

    static int wordLength[];
    static List<String> possibleWords;
    static List<char[][]> possibleBlocks;
    static List<String> dictionary[];
    static char[][] wordBlock;


    //This algorithm only works on square blocks of letters

    /**
     * first two numbers are the dimensions of the word square
     * next is the length of the word
     * then the letters in block format, lowercase, spaces written as 'Z'
     * @param args
     */
    public static void main(String[] args) {

        inputGatherer();



        List<List<String>> wordList = new ArrayList<>();
        List<char[][]> currentBlocks = new ArrayList<>();
        wordList.add(new ArrayList<>());
        currentBlocks.add(wordBlock);

        for(int i=0; i < wordLength.length; i++) {
            long startTime = System.currentTimeMillis();
            loadDictionary(wordLength[i]);

            //begin enumerating possibilities

            List<List<String>> tempWordList = new ArrayList<>();
            List<char[][]> tempCurrentBlocks = new ArrayList<>();

            for(int j=0; j < currentBlocks.size(); j++) {

                possibleWords = new ArrayList<>();
                possibleBlocks = new ArrayList<>();
                enumeratePossibleWords(wordLength[i], currentBlocks.get(j));

                if(possibleBlocks.size() > 0) { //there is actually something to add
                    for(int k = 0; k < possibleBlocks.size(); k++) {

                        tempCurrentBlocks.add(possibleBlocks.get(k)); //add the new block
                        tempWordList.add(new ArrayList<>()); //add a new array to match the block

                        for(int q = 0; q < wordList.get(j).size(); q++) {
                            tempWordList.get(tempWordList.size()-1) //get the new array
                                    .add(wordList.get(j).get(q)); //populate the new array with the old values
                        }
                        tempWordList.get(tempWordList.size()-1)
                                .add(possibleWords.get(k)); //add the word corresponding to the block
                    }
                }
            }
            wordList = tempWordList;
            currentBlocks = tempCurrentBlocks;

            System.out.printf("The time taken to enumerate round %d was: %d ms\n", i+1, System.currentTimeMillis() - startTime);
            System.out.printf("The number of possible words round %d are: %d \n", i+1, currentBlocks.size());
        }
        System.out.printf("\n\nThe final solutions are:\n");
        for(List list: wordList) {
            System.out.println(list.toString());
        }
    }

    public static void enumeratePossibleWords(int wordLength, char[][] wordBlock) {
        for (int i = 0; wordBlock.length > i; i++) {
            for (int j = 0; wordBlock.length > j; j++) {
                if (wordBlock[i][j] == 'Z') {
                    continue;
                }
                checker(i, j, new char[wordLength], 0, wordBlock);
            }
        }
    }

    public static void inputGatherer() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter the block size: ");
        int blockSize = sc.nextInt();
        wordBlock = new char[blockSize][blockSize];

        System.out.println("Please enter the number of words to solve for: ");
        wordLength = new int[sc.nextInt()];

        System.out.println("Please enter the lengths of the words");
        for(int i=0; i < wordLength.length; i++) {
            wordLength[i] = sc.nextInt();
        }

        System.out.println("Please enter the word block, use 'Z' for spaces");
        //populate the wordBlock
        for(int i=0; wordBlock.length > i; i++) {
            wordBlock[i] = sc.next().toCharArray();
        }
    }

    public static void checker(int i, int j, char[] currentWord, int depth, char wordBlock[][]) {
        if(wordBlock[i][j]!='Z') {
            char newWordBlock[][] = new char[wordBlock.length][];
            for(int k=0; wordBlock.length > k; k++) {
                newWordBlock[k] = Arrays.copyOf(wordBlock[k], wordBlock.length);
            }
            currentWord[depth] = newWordBlock[i][j];
            newWordBlock[i][j] = 'Z';
            if(depth == currentWord.length-1) {
                String str = new String(currentWord);
                if(dictionary[str.charAt(0)-'a'].contains(str)) {
                    possibleWords.add(str);
                    letterDropper(newWordBlock);
                    possibleBlocks.add(newWordBlock);
                }
            } else { //begin enumeration checks
                if(j+1 < wordBlock.length)
                    checker(i,j+1, currentWord, depth+1, newWordBlock); //right
                if(i+1 < wordBlock.length)
                    checker(i+1,j,currentWord,depth+1, newWordBlock); //down
                if(j-1 > -1)
                    checker(i, j-1, currentWord, depth+1, newWordBlock); //left
                if(i-1 > -1)
                    checker(i-1, j, currentWord, depth+1, newWordBlock); //up
                if(i-1 > -1 && j+1 < wordBlock.length)
                    checker(i-1, j+1, currentWord, depth+1, newWordBlock); //up-right
                if(i-1 > -1 && j-1 > -1)
                    checker(i-1, j-1, currentWord, depth+1, newWordBlock); //up-left
                if(i+1 < wordBlock.length && j-1 > -1)
                    checker(i+1, j-1, currentWord, depth+1, newWordBlock); //down-left
                if(i+1 < wordBlock.length && j+1 < wordBlock.length)
                    checker(i+1, j+1, currentWord, depth+1, newWordBlock); //down-right
            }
        }
    }

    public static void letterDropper(char[][] block) {
        boolean tryAgain;
        do {
            tryAgain = false;
            for (int i = block.length-1; i >= 1; i--) { //height, no sense trying to swap the top row with nothing
                for (int j = block.length-1; j >= 0; j--) {
                    if(block[i][j]=='Z') {
                        block[i][j] = block[i-1][j];
                        block[i-1][j] = 'Z';
                        if(block[i][j] != 'Z') { //if swap did something
                            tryAgain = true;
                        }
                    }
                }
            }
        } while (tryAgain);
    }

    public static void loadDictionary(int wordLength) {

        dictionary = new List[26];
        for(int i=0; 26 > i; i++) {
            dictionary[i] = new ArrayList<>();
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "/Users/bolster/Documents/Coding Workspace/WordTrekSolver/src/com/company/main/dictionary.txt"));
                String str;
                while ((str = in.readLine()) != null) {
                    if(str.length() != wordLength) {
                        continue;
                    } else {
                        dictionary[str.charAt(0)-'a'].add(str);
                    }
                }
                in.close();
        } catch (IOException e) {
                System.out.print(e.getMessage());
        }
    }
}
