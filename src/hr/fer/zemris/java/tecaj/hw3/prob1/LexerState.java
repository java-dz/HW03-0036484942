package hr.fer.zemris.java.tecaj.hw3.prob1;

/**
 * This enumeration describes the Lexer's state. The extended state of Lexer
 * does not allow escape characters, whitespaces or the letter-digit transition
 * to delimit the tokens. The basic state is the default Lexer state and allows
 * escape characters for words and delimits regularly on whitespaces.
 *
 * @author Mario Bobic
 */
public enum LexerState {

    /**
     * The default Lexer state. Allows escape characters for words and delimits
     * on whitespaces and the letter-digit transition.
     */
    BASIC,

    /**
     * The extended state of Lexer does not allow escape characters, whitespaces
     * or the letter-digit transition to delimit the tokens.
     */
    EXTENDED
}
