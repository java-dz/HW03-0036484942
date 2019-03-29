package hr.fer.zemris.java.tecaj.hw3.prob1;

/**
 * This enumeration describes the type of a token. A token may be a word, a
 * number, a symbol which all contain a value, or <tt>EOF</tt> that indicates
 * the end of data and does not contain a value.
 *
 * @author Mario Bobic
 */
public enum TokenType {

    /**
     * Indicates the end of data and does not contain a value.
     */
    EOF,

    /**
     * Represents a token that consists of 1 or more characters which are
     * primarily letters. If one wishes to construct a word containing digits or
     * other symbols, these must be escaped with an escape character.
     */
    WORD,

    /**
     * Represents a token that consists of digits in range [0,
     * {@linkplain Long#MAX_VALUE}].
     */
    NUMBER,

    /**
     * Represents a token that consists of only one symbol.
     */
    SYMBOL
}
