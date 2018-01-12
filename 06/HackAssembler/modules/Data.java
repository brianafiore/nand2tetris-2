package modules;

import java.util.ArrayList;
import java.util.Arrays;

public class Data {
	
	private static String[] destMnemonicsList = {
			"null","M","D","MD","A","AM","AD","AMD"};
	private static String[] jumpMnemonicsList = {
			"null","JGT","JEQ","JGE","JLT","JNE","JLE","JMP"};
	private static String[] compMnemonicsList = {
			"0","1","-1","D","A","!D","!A","-D","-A",
			"D+1","A+1","D-1","A-1","D+A","D-A","A-D", "D&A","D|A",
			"M","!M","-M","M+1","M-1","D+M","D-M","M-D","D&M","D|M"};
	
	private static Integer[][] binaryTable3bits = {
			{0,0,0},{0,0,1},{0,1,0},{0,1,1},
			{1,0,0},{1,0,1},{1,1,0},{1,1,1}};
	
	private static Integer[][] compBinaryTable = {
			{0,1,0,1,0,1,0},{0,1,1,1,1,1,1},{0,1,1,1,0,1,0},{0,0,0,1,1,0,0},
			{0,1,1,0,0,0,0},{0,0,0,1,1,0,1},{0,1,1,0,0,0,1},{0,0,0,1,1,1,1},
			{0,1,1,0,0,1,1},{0,0,1,1,1,1,1},{0,1,1,0,1,1,1},{0,0,0,1,1,1,0},
			{0,1,1,0,0,1,0},{0,0,0,0,0,1,0},{0,0,1,0,0,1,1},{0,0,0,0,1,1,1},
			{0,0,0,0,0,0,0},{0,0,1,0,1,0,1},{1,1,1,0,0,0,0},{1,1,1,0,0,0,1},
			{1,1,1,0,0,1,1},{1,1,1,0,1,1,1},{1,1,1,0,0,1,0},{1,0,0,0,0,1,0},
			{1,0,1,0,0,1,1},{1,0,0,0,1,1,1},{1,0,0,0,0,0,0},{1,0,1,0,1,0,1}};

	public static String[] getDestMnemonicsList() {
		return destMnemonicsList;
	}

	public static String[] getJumpMnemonicsList() {
		return jumpMnemonicsList;
	}

	public static String[] getCompMnemonicsList() {
		return compMnemonicsList;
	}

	public static ArrayList<ArrayList<Integer>> getBinaryTable3bits() {
		ArrayList<ArrayList<Integer>> binTable = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < binaryTable3bits.length; i++){
			ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(binaryTable3bits[i]));
			binTable.add(list);
		}
		return binTable;
	}

	public static ArrayList<ArrayList<Integer>> getCompBinaryTable() {
		ArrayList<ArrayList<Integer>> compBinTable = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < compBinaryTable.length; i++){
			ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(compBinaryTable[i]));
			compBinTable.add(list);
		}
		return compBinTable;
	}	
}
