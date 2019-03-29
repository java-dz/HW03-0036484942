package hr.fer.zemris.java.tecaj.hw3.prob1;

/**
 * A token is a lexical unit that groups one or more consecutive characters.
 * One token has its token type and holds its value. This class offers one
 * constructor that accepts these parameters and two getters, one that returns
 * the type of the token and one that returns its value.
 *
 * @author Mario Bobic
 */
public class Token {

    /** Type of the token. */
    private TokenType type;
    /** Value that this token holds. */
    private Object value;

    /**
     * Constructs an instance of Token with the given token type and value.
     *
     * @param type type of the token
     * @param value value of the token
     */
    public Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the type of this token.
     *
     * @return the type of this token
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the value of this token.
     *
     * @return the value of this token
     */
    public Object getValue() {
        return value;
    }

}
