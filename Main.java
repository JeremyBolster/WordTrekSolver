package com.company.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {

    static int wordLength;
    static List<String> possibleWords;
    static char wordBlock[][];
    static List<String> dictionary[];


    //This algorithm only works on square blocks of letters

    /**
     * first two numbers are the dimensions of the word square
     * next is the length of the word
     * then the letters in block format, lowercase, spaces written as 'Z'
     * @param args
     */
    public static void main(String[] args) {

        dictionary = new List[26];
        for(int i=0; 26 > i; i++) {
            dictionary[i] = new ArrayList<>();
        }

        Scanner sc = new Scanner(System.in);

        wordBlock = new char
                [sc.nextInt()][sc.nextInt()];

        wordLength = sc.nextInt();
        loadDictionary(wordLength);

        //populate the wordBlock
        for(int i=0; wordBlock.length > i; i++) {
            wordBlock[i]= sc.next().toCharArray();
        }

        possibleWords = new ArrayList<>();

        //begin enumerating possiblities

        long startTime = System.currentTimeMillis();

        for(int i=0; wordBlock.length > i; i++) {
            for(int j=0; wordBlock.length > j; j++) {
                if(wordBlock[i][j] == 'Z') {
                    continue;
                }
                long checkStartTime = System.currentTimeMillis();
                checker(i,j, new char[wordLength], 0, wordBlock);
                System.out.printf("Done search at position: %d,\t Took %d miliseconds\n",
                        (i*wordBlock.length+j), System.currentTimeMillis()-checkStartTime);
            }
        }

        System.out.printf("The time taken to enumerate was: %d ms\n", System.currentTimeMillis()-startTime);
        System.out.printf("The number of possible words is: %d \n", possibleWords.size());
        startTime = System.currentTimeMillis();

        List<String> finalList = new ArrayList<>();
        //check words against dictionary
        for(String str: possibleWords) {
            if(dictionary[str.charAt(0)-'a'].contains(str)) {
                if(!finalList.contains(str)) {
                    finalList.add(str);
                }
            }
        }
        System.out.printf("The time taken to check against dictionary was: %d ms\n", System.currentTimeMillis()-startTime);

        //print out the possible words
        System.out.printf("The number of possible words is: %d \n", finalList.size());
        Collections.sort(finalList);
        for(String str: finalList) {
            System.out.println(str);
        }


    }
    //doesn't yet check if its working back on itself
    public static void checker(int i, int j, char[] currentWord, int depth, char wordBlock[][]) {
        if(wordBlock[i][j]!='Z') {
            char newWordBlock[][] = new char[wordBlock.length][];
            for(int k=0; wordBlock.length > k; k++) {
                newWordBlock[k] = Arrays.copyOf(wordBlock[k], wordBlock.length);
            }
            currentWord[depth] = newWordBlock[i][j];
            newWordBlock[i][j] = 'Z';
            if(depth == wordLength-1) {
                possibleWords.add(new String(currentWord));
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

    public static void loadDictionary(int wordLength) {
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
