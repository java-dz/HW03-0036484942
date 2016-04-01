package hr.fer.zemris.java.tecaj.hw3.prob1;

/**
 * Lexer is a program that performs lexical analysis. Lexer is combined with a
 * parser, which together analyze the syntax and extract tokens from the input
 * text.
 * <p>
 * Lexer provides one constructor which accepts an input text to be tokenized.
 * The input text is analyzed and tokens are made depending on the token type.
 * It also provides a method for generating the next token and a method that
 * returns the last generated token.
 * <p>
 * Lexer has two states, {@link LexerState#BASIC basic} and
 * {@link LexerState#EXTENDED extended}. The basic state is the default Lexer
 * state and allows escape characters for words and delimits regularly on
 * whitespaces. The extended state of Lexer does not allow escape characters,
 * whitespaces or the letter-digit transition to delimit the tokens.
 *
 * @author Mario Bobic
 * @see TokenType
 * @see LexerState
 */
public class Lexer {
	
	/** An escape character for escaping numbers into words. */
	private static final Character ESCAPE_CHAR = '\\';
	/** A character for toggling the Lexer state. */
	private static final Character EXTEND_CHAR = '#';
	
	/** Input text for tokenization. */
	private final char[] data;
	/** Current token. */
	private Token token;
	/** Index of the first character to process. */
	private int currentIndex;
	/** Current state of Lexer. */
	private LexerState state;
	
	/**
	 * Constructs an instance of Lexer with the given input text to be
	 * tokenized.
	 * 
	 * @param text text for tokenization
	 * @throws IllegalArgumentException if the input text is null
	 */
	public Lexer(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Text must not be null.");
		}

		data = text.trim().toCharArray();
		currentIndex = 0;
		state = LexerState.BASIC;
	}

	/**
	 * Generates and returns the next token. Tokens are generated from the input
	 * text and their type depends on the input text. If the input text is
	 * exhausted, a {@linkplain LexerException} is thrown.
	 * 
	 * @return the next token generated from the input text
	 * @throws LexerException
	 *             if there is no next token to generate or if an invalid escape
	 *             sequence is given
	 */
	public Token nextToken() {
		if (token != null && token.getType() == TokenType.EOF) {
			throw new LexerException("No next token after EOF.");
		}

		if (isDataEnd()) {
			token = new Token(TokenType.EOF, null);
		} else {
			Character ch = data[currentIndex];
			
			if (state == LexerState.EXTENDED) {
				String extendedWord = getExtendedWord();
				if (extendedWord.isEmpty()) {
					token = new Token(TokenType.SYMBOL, EXTEND_CHAR);
					currentIndex++;
				} else {
					token = new Token(TokenType.WORD, extendedWord);
				}
			} else if (Character.isLetter(ch) || ch.equals(ESCAPE_CHAR)) {
				String word = getWord();
				token = new Token(TokenType.WORD, word);
			} else if (Character.isDigit(ch)) {
				Long number = getNumber();
				token = new Token(TokenType.NUMBER, number);
			} else {
				Character symbol = getSymbol();
				token = new Token(TokenType.SYMBOL, symbol);
				if (symbol.equals(EXTEND_CHAR)) {
					setState(LexerState.EXTENDED);
				}
			}
			skipSpaces();
		}
		
		return token;
	}

	/**
	 * Returns the last generated token. This method may be called multiple
	 * times because it does not generate the next token.
	 * 
	 * @return the last generated token
	 */
	public Token getToken() {
		return token;
	}
	
	/**
	 * Sets the current state of Lexer. State may be either
	 * {@link LexerState#BASIC basic} or {@link LexerState#EXTENDED extended}.
	 * 
	 * @param state state to be set
	 * @throws IllegalArgumentException if the given state is <tt>null</tt>
	 */
	public void setState(LexerState state) {
		if (state == null) {
			throw new IllegalArgumentException("State must not be null.");
		}
		this.state = state;
	}
	
	/**
	 * Returns a {@link TokenType#WORD word} starting from the
	 * <tt>currentIndex</tt> and ending on the first whitespace character,
	 * number or any other non-letter symbol that is not escaped. This method
	 * increases the currentIndex variable as it goes. This method may throw a
	 * {@linkplain LexerException} if the escape sequence is invalid. This means
	 * that the escape character may not be located at the very end of the input
	 * text with nothing to escape. It also means that the escape character may
	 * not be any character other than a digit or the escape-character itself.
	 * 
	 * @return a word starting from the currentIndex
	 * @throws LexerException if the escape sequence is invalid
	 */
	private String getWord() {
		StringBuilder sb = new StringBuilder();
		boolean wasEscaped = false;
		
		while (true) {
			Character letter = data[currentIndex];
			
			if (letter.equals(ESCAPE_CHAR) && !wasEscaped) {
				currentIndex++;
				if (isDataEnd()) {
					throw new LexerException("Invalid escape ending.");
				} else {
					wasEscaped = true;
					continue;
				}
			}
			
			if (!wasEscaped) {
				if (!Character.isLetter(letter)) {
					break;
				}
			} else {
				wasEscaped = false;
				if (!Character.isDigit(letter) && !letter.equals(ESCAPE_CHAR)) {
					throw new LexerException("Invalid escape sequence: " + letter);
				}
			}
			
			sb.append(data[currentIndex]);
			currentIndex++;
			
			if (isDataEnd()) break;
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns a {@link TokenType#NUMBER number} starting from the
	 * <tt>currentIndex</tt> and ending on the first whitespace character,
	 * letter or any other non-number symbol. This method increases the
	 * currentIndex variable as it goes. This method may throw a
	 * {@linkplain LexerException} if and only if the number is out of range.
	 * A number may be out of range if it goes beyond {@linkplain Long#MAX_VALUE}.
	 * 
	 * @return a number starting from the currentIndex
	 * @throws LexerException if the number is out of range
	 */
	private Long getNumber() {
		StringBuilder sb = new StringBuilder();
		
		while (Character.isDigit(data[currentIndex])) {
			sb.append(data[currentIndex]);
			currentIndex++;
			
			if (isDataEnd()) break;
		}
		
		Long number;
		try {
			number = Long.parseLong(sb.toString());
		} catch (NumberFormatException e) {
			throw new LexerException("Number is too big: " + sb);
		}
		
		return number;
	}
	
	/**
	 * Returns a {@link TokenType#SYMBOL symbol} starting from the
	 * <tt>currentIndex</tt> and ending on <tt>currentIndex+1</tt>.
	 * This method increases the currentIndex variable as it goes.
	 * 
	 * @return a symbol starting from the currentIndex
	 */
	private Character getSymbol() {
		return data[currentIndex++];
	}
	
	/**
	 * Returns a {@link TokenType#WORD word} starting from the
	 * <tt>currentIndex</tt> and ending on the first whitespace character,
	 * {@link Lexer#EXTEND_CHAR extending character} or when it reaches the end
	 * of the input text. This method increases the currentIndex variable as it
	 * goes. This method ignores escape sequences as they are not important to
	 * the extended Lexer state.
	 * 
	 * @return a word starting from the currentIndex
	 */
	private String getExtendedWord() {
		StringBuilder sb = new StringBuilder();
		
		while (!isDataEnd()
				&& !EXTEND_CHAR.equals(data[currentIndex])
				&& !Character.isWhitespace(data[currentIndex])) {
			sb.append(data[currentIndex]);
			currentIndex++;
		}
		
		return sb.toString();
	}
	
	/**
	 * Skips all whitespace characters by moving the <tt>currentIndex</tt>
	 * variable to the next non-whitespace character.
	 */
	private void skipSpaces() {
		int i;
		for (i = currentIndex; !isDataEnd() && Character.isWhitespace(data[i]); i++);
		currentIndex = i;
	}
	
	/**
	 * Returns true if the input text has been exhausted, or more formally if
	 * <tt>currentIndex == data.length</tt>. False otherwise.
	 * 
	 * @return true if the input text has been exhausted
	 */
	private boolean isDataEnd() {
		return currentIndex == data.length;
	}
	
}