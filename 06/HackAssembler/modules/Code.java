package modules;

import java.util.ArrayList;
import java.util.HashMap;

public class Code {

	private HashMap<String, ArrayList<Integer>> destMap = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, ArrayList<Integer>> jumpMap = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, ArrayList<Integer>> compMap = new HashMap<String, ArrayList<Integer>>();
	
	public Code(){
		String[] destMnemonics = Data.getDestMnemonicsList();
		String[] compMnemonics = Data.getCompMnemonicsList();
		String[] jumpMnemonics = Data.getJumpMnemonicsList();
		
		ArrayList<ArrayList<Integer>> binaryTable3bits = Data.getBinaryTable3bits();
		ArrayList<ArrayList<Integer>> compBinaryTable = Data.getCompBinaryTable();
		
		for(int i = 0; i < destMnemonics.length; i++){
			destMap.put(destMnemonics[i], binaryTable3bits.get(i));
		}
		for(int i = 0; i < jumpMnemonics.length; i++){
			jumpMap.put(jumpMnemonics[i], binaryTable3bits.get(i));
		}
		for(int i = 0; i < compMnemonics.length; i++){
			compMap.put(compMnemonics[i], compBinaryTable.get(i));
		}
	}
	
	public ArrayList<Integer> dest(String destMnemonic) {
		ArrayList<Integer> value = destMap.get(destMnemonic);
		return value;
	}

	public ArrayList<Integer> jump(String jumpMnemonic) {
		ArrayList<Integer> value = jumpMap.get(jumpMnemonic);
		return value;
	}

	public ArrayList<Integer> comp(String compMnemonic) {
		ArrayList<Integer> value = compMap.get(compMnemonic);
		return value;
	}
}
