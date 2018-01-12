import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes all comments and white space from the input stream and
 * breaks it into Jack-language tokens, as specified by the Jack grammar.
 * 
 * */

public class JackTokenizer {
	
	//Token types
	private static final int KEYWORD = 0;
	private static final int SYMBOL = 1;
	private static final int IDENTIFIER = 2;
	private static final int INT_CONST = 3;
	private static final int STRING_CONST = 4;
	
	
	private String program;
	private String currentToken;
	private int tokenType;
	
	private Pattern symbolPattern;
	private Pattern stringPattern;
	private Pattern integerPattern;
	private Pattern keywordPattern;
	private Pattern identifierPattern;
	
	public JackTokenizer(String program){
		this.program = program;
		
		this.symbolPattern  = Pattern.compile("^(?:[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\=\\*\\/\\&\\|\\<\\>\\~]){1}");
		this.stringPattern = Pattern.compile("^(?:\".*?\")");
		this.integerPattern = Pattern.compile("^(?:\\d{1,5})");
		this.keywordPattern = Pattern.compile("^(?:class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)");
		this.identifierPattern = Pattern.compile("^(?:[A-Za-z_][A-Za-z0-9]*)");
	}
	
	public Boolean hasMoreTokens(){
		return program.length() > 0;
	}
	
	public void advance(){
		
		Matcher symbolMatcher = symbolPattern.matcher(program);
		Matcher stringMatcher = stringPattern.matcher(program);
		Matcher integerMatcher = integerPattern.matcher(program);
		Matcher keywordMatcher = keywordPattern.matcher(program);
		Matcher identifierMatcher = identifierPattern.matcher(program);
		
		if(integerMatcher.lookingAt()){
			tokenType = INT_CONST;
			currentToken = integerMatcher.group(0);
		} else if(stringMatcher.lookingAt()){
			tokenType = STRING_CONST;
			currentToken = stringMatcher.group(0);
		} else if(keywordMatcher.lookingAt()){
			tokenType = KEYWORD;
			currentToken = keywordMatcher.group(0);
		} else if(symbolMatcher.lookingAt()){
			tokenType = SYMBOL;
			currentToken = symbolMatcher.group(0);
		} else if(identifierMatcher.lookingAt()){
			tokenType = IDENTIFIER;
			currentToken = identifierMatcher.group(0);		
		}
		//remove last token program
		if(hasMoreTokens()){
			program = program.substring(currentToken.length()).trim();
		}
	}
	
	public int tokenType(){
		return tokenType;
	}
	
	public String keyWord(){
		return currentToken;
	}
	
	public char symbol(){		
		return currentToken.charAt(0);
	}
	
	public String identifier(){
		return currentToken;
	}
	
	public int intVal(){
		return Integer.parseInt(currentToken);
	}
	
	public String stringVal(){
		return currentToken.replace('"', ' ');
	}
	
}
