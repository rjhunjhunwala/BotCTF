package botctf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rohan
 */
public class file {
/**
	* Gets an integer array representing a map from the file file name
	* @param fileName is the path or local filename
	* @return an integer array representing the map
	*/
	public static int[][] getMapFromFile(String fileName){
		String[] wordsFromFile=getWordsFromFile(fileName);
		int lengthOfFile=getLengthOfFile(fileName);
		int[][] returnMe=new int[wordsFromFile[0].length()][lengthOfFile];
			for(int j = 0;j<wordsFromFile.length;j++){
		for(int i =0;i<wordsFromFile[j].length();i++){
			try{
				returnMe[i][j]=wordsFromFile[j].charAt(i);
			}catch(Exception ex){
				System.err.printf("i:%s,j:%s1",i,j);
				System.exit(0);
			}
			}
		}
		return returnMe;
	}
	/**
	 *
	 * @param fileName is the path to the file or just the name if it is local
	 * @return the number of lines in fileName
	 */
	public static int getLengthOfFile(String fileName) {
		int length = 0;
		try {
			File textFile = new File(fileName);
			Scanner sc = new Scanner(textFile);
			while (sc.hasNextLine()) {
				sc.nextLine();
				length++;
			}
		} catch (Exception e) {

		}
		return length;
	}

	/**
	 *
	 * @param fileName is the path to the file or just the name if it is local
	 * @return an array of Strings where each string is one line from the file
	 * fileName.
	 */
	public static String[] getWordsFromFile(String fileName) {
		int lengthOfFile = getLengthOfFile(fileName);
		String[] wordBank = new String[lengthOfFile];
		int i = 0;
		try {
			File textFile = new File(fileName);
			Scanner sc = new Scanner(textFile);
			for (i = 0; i < lengthOfFile; i++) {
				wordBank[i] = sc.nextLine();
			}
			return wordBank;
		} catch (Exception e) {
			System.err.println(e);
			System.exit(55);
		}
		return null;
	}



}
