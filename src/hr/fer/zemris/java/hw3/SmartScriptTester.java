package hr.fer.zemris.java.hw3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.lexer.SSTokenType;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.*;

/**
 * This class is a tester class for showing the usage of
 * {@linkplain SmartScriptParser}. Basically it breaks down document given by
 * the path from the command line to nodes in a tree and then it recreates the
 * original textual body from that tree. The body is printed out onto the
 * standard output.
 *
 * @author Mario Bobic
 */
public class SmartScriptTester {

	/**
	 * Program entry point.
	 * 
	 * @param args argument from the command line
	 */
	/* I have put files doc1.txt, doc2.txt, doc3.txt and doc4.txt in this
	 * project, with each being more complex than the previous one. */
	/* Also, for this parser I have prepared a series of JUnit 4 tests.
	 * These can be found in the tests/prob2 source folder. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Expected one argument: path to file");
			System.exit(1);
		}
		
		String docBody = null;
		try {
			docBody = new String(
					Files.readAllBytes(Paths.get(args[0])),
					StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.err.println("Invalid path to text document.");
			System.exit(2);
		}
		
		SmartScriptParser parser = null;
		try {
			parser = new SmartScriptParser(docBody);
		} catch (SmartScriptParserException e) {
			System.err.println("Unable to parse document!");
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (Exception e) {
			System.err.println("If this line ever executes, you will get one grade down!");
			e.printStackTrace();
			System.exit(-1);
		}
		
		DocumentNode document = parser.getDocumentNode();
		String originalDocumentBody = null;
		try {
			originalDocumentBody = createOriginalDocumentBody(document);
			System.out.println(originalDocumentBody);
		} catch (NullPointerException e) {
			// the document body is empty
			System.exit(4);
		}
		
		/* This must be done for the code below. During the parsing, 
		 * escape-sequences \\ and \{ are replaced with \ and {, respectively,
		 * so this must be undone. */
		String originalWithEscapes = originalDocumentBody.replace("\\", "\\\\").replace("{", "\\{");
		
		/* The code below prints out "true" if the recreated document body is
		 * the same as double-recreated document body. */
		SmartScriptParser parser2 = new SmartScriptParser(originalWithEscapes);
		DocumentNode document2 = parser2.getDocumentNode();
		String originalDocumentBody2 = createOriginalDocumentBody(document2);
		System.out.println();
		System.out.println("Equals: " + originalDocumentBody.equals(originalDocumentBody2));
	}

	/**
	 * Recreates the original document body from the given root tree node
	 * <tt>document</tt>. This method just calls the document's
	 * {@link DocumentNode#toString() toString()} method, which internally uses
	 * a {@linkplain StringBuilder} to rebuild a string consisted of
	 * {@link SSTokenType#TEXT TEXT} and {@link SSTokenType#TAG_FOR FOR},
	 * {@link SSTokenType#TAG_END END} and {@link SSTokenType#TAG_ECHO ECHO}
	 * tags.
	 * 
	 * @param document the root tree node to rebuild the document from
	 * @return a string representing the original document body
	 */
	public static String createOriginalDocumentBody(DocumentNode document) {
		return document.toString();
	}	

}
