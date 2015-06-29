/**
 * Scott Fang
 * Project 1
 * CS 146
 * Dr. Juan Gomez
 *
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/*
 * A class that will parse through a given text-based file and print out the row number and column of a mismatched symbol
 * 
 * A matched symbol is determined if the nearest end symbol for a pair is 
 * 
 */
public class CppJavaParser {
	//A hashmap is made for conveniece of storing a specialized stack and getting for later
	HashMap<String, Stack<ParseSymbol>> map = new HashMap<String, Stack<ParseSymbol>>(); 
	Stack<ParseSymbol> mainStack = new Stack<ParseSymbol>();
	Stack<ParseSymbol> stack = new Stack<ParseSymbol>(); // Stack for {
	Stack<ParseSymbol> stack2 = new Stack<ParseSymbol>(); // stack for [
	Stack<ParseSymbol> stack3 = new Stack<ParseSymbol>(); // Stack for (
	Stack<ParseSymbol> errors = new Stack<ParseSymbol>();// Stack for loose } ] )
	Stack<ParseSymbol> condStack = new Stack<ParseSymbol>();// Stack for quotes
	boolean hasErrors = false; // A boolean to determine the exit status
								// (true:success = 0, false:failure = -1);

	public static void main(String[] args) {

		if (args.length > 0) {
			String file = args[0];
			File f = new File(file);
			if (f.canRead()) {
				CppJavaParser j = new CppJavaParser();
				j.CppJavaParser(f);
			}
		} else {

			Scanner scan = new Scanner(System.in);
			System.out.print("Enter File to parse: ");
			String string = scan.next();
			File f = new File(string);
			CppJavaParser p = new CppJavaParser();

			p.CppJavaParser(f);

		}
	}

	/*
	 * Method that scans the given file for parsing.
	 */
	public void CppJavaParser(File f) {

		// Adds stacks to the hashmap with the appropriate keys
		map.put("{", stack);  map.put("}", stack);
		map.put("[", stack2); map.put("]", stack2);
		map.put("(", stack3); map.put(")", stack3);
		String code = "";
		try {
			Scanner scan = new Scanner(f);

			// If a header comment is in place, the scan will lock down and
			// focus on looking for
			// the end sequence
			boolean isComment = false;
			int row = 1;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				for (int i = 0; i < line.length(); i++) {
					String string = line.substring(i, i + 1);
					ParseSymbol index = new ParseSymbol(string, row, i);

					if (index.getSymbol().equals("/") && !isComment) {
						if ((i + 1) >= line.length()) {
							i = line.length();
							break;
						}

						String nextInd = line.substring(i + 1, i + 2);
						if (nextInd.equals("*")) {
							isComment = true;
							i++;
						} else if (nextInd.equals("/")) // A comment is found in a line; anything written after it is ignored
						{
							i = line.length();
						}

					}

					if (index.getSymbol().equals("*") && isComment) {
						if ((i + 1) >= line.length()) {
							i = line.length();
							break;
						}

						String nextInd = line.substring(i + 1, i + 2);
						if (nextInd.equals("/")) {

							isComment = false;

						}
					}

					if (!isComment) {
						// Checks if index is a quote
						if (index.getSymbol().equals("\"")
								|| index.getSymbol().equals("\'"))
							i = quoteParse(index, line, i);

						// Checks if it's a hashmark for conditional compilation
						if (index.getSymbol().equals("#"))
							condParse(index, line, i);

						parseCheck(index, line); // Parses for braces and
													// parenthesis
					}
				}
				code = code.concat(line + "\n"); // DEBUG
				row++;
			}
			scan.close();

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
			System.exit(-1);
		}

		ArrayList<ParseSymbol> list = sortErrors();
		printErrors(list);
		// System.out.println(code); //Prints the code for debugging
		if (!hasErrors) {
			System.out.println("File parsed successfully; no errors found.");
			System.exit(0);
		}
		System.exit(-1);
	}

	/*
	 * A helper function that handles parsing braces, brackets, and parenthesis
	 * 
	 */
	private void parseCheck(ParseSymbol p, String line) {
		String i = p.getSymbol();
		int row = p.getRow();
		int col = p.getCol();
		Stack<ParseSymbol> symbolStack = map.get(i);
		p.setLineCode(line);

		if (symbolStack == null)
			return;

		if (i.equals("{")) {
			symbolStack.push(p);
			mainStack.push(p);
			return;
		}

		if (i.equals("(")) {
			symbolStack.push(p);
			mainStack.push(p);
			return;
		}

		if (i.equals("[")) {
			symbolStack.push(p);
			mainStack.push(p);
			return;
		}

		if (symbolStack.isEmpty()
				&& (i.equals("}") || i.equals(")") || i.equals("]"))) {
			p.setErrorMessage("Lefthand " + p.getLiteral() + " not found");
			errors.push(p);
			return;
		}

		if (i.equals("}")) {

			if (symbolStack.peek().getSymbol().equals("{")) {

				if (mainStack.peek().getSymbol().equals("{"))
					mainStack.pop();
				else {
					while (!mainStack.peek().getSymbol().equals("{")) {
						String mismatch = mainStack.pop().getSymbol();
						Stack<ParseSymbol> misMatchQ = map.get(mismatch);
						String literal = misMatchQ.peek().getLiteral();
						misMatchQ.peek().setErrorMessage(
								"Righthand " + literal + " not found");
						errors.push(misMatchQ.pop());
					}
					mainStack.pop();
				}

				symbolStack.pop();
				return;
			}

		}

		if (i.equals(")")) {

			if (symbolStack.peek().getSymbol().equals("(")) {

				if (mainStack.peek().getSymbol().equals("("))
					mainStack.pop();
				else {
					while (!mainStack.peek().getSymbol().equals("(")) {
						String mismatch = mainStack.pop().getSymbol();
						Stack<ParseSymbol> misMatchQ = map.get(mismatch);
						String literal = misMatchQ.peek().getLiteral();
						misMatchQ.peek().setErrorMessage(
								"Righthand " + literal + " not found");
						errors.push(misMatchQ.pop());
					}
					mainStack.pop();
				}

				symbolStack.pop();
				return;
			}
		}

		if (i.equals("]")) {

			if (symbolStack.peek().getSymbol().equals("[")) {

				if (mainStack.peek().getSymbol().equals("["))
					mainStack.pop();
				else {
					while (!mainStack.peek().getSymbol().equals("[")) {
						String mismatch = mainStack.pop().getSymbol();
						Stack<ParseSymbol> misMatchQ = map.get(mismatch);
						String literal = misMatchQ.peek().getLiteral();
						misMatchQ.peek().setErrorMessage(
								"Righthand " + literal + " not found");
						errors.push(misMatchQ.pop());
					}
					mainStack.pop();
				}

				symbolStack.pop();
				return;
			}

		}

	}

	/*
	 * Determines whether the given hashmark marks a valid use of conditional
	 * compilation statements
	 */
	private void condParse(ParseSymbol hashmark, String line, int i) {
		StringTokenizer token = new StringTokenizer(line.substring(i));
		String cond = "";
		if (token.hasMoreTokens())
			cond = token.nextToken();

		if (cond.equalsIgnoreCase("#if")) {
			ParseSymbol symbol = new ParseSymbol("#if", hashmark.getRow(), i);
			symbol.setErrorMessage("Unclosed conditional compilation");
			symbol.setLineCode(line);
			condStack.push(symbol);
		}

		if (cond.equalsIgnoreCase("#ifdef")) {
			ParseSymbol symbol = new ParseSymbol("#ifdef", hashmark.getRow(), i);
			symbol.setErrorMessage("Unclosed conditional compilation");
			symbol.setLineCode(line);
			condStack.push(symbol);
		}

		if (cond.equalsIgnoreCase("#ifndef")) {
			ParseSymbol symbol = new ParseSymbol("#ifndef", hashmark.getRow(),
					i);
			symbol.setErrorMessage("Unclosed conditional compilation");
			symbol.setLineCode(line);
			condStack.push(symbol);
		}

		if (cond.equalsIgnoreCase("#else")) {
			ParseSymbol symbol = new ParseSymbol("#else", hashmark.getRow(), i);
			symbol.setErrorMessage("Loose else condition");
			symbol.setLineCode(line);
			if (condStack.isEmpty())
				errors.push(symbol);

			condStack.push(symbol);
		}

		if (cond.equalsIgnoreCase("#elif")) {
			ParseSymbol symbol = new ParseSymbol("#elif", hashmark.getRow(), i);
			symbol.setErrorMessage("Loose else if condition");
			symbol.setLineCode(line);
			if (condStack.isEmpty())
				errors.push(symbol);

			if (condStack.peek().getSymbol().equals("#else"))
				errors.push(symbol);

		}

		if (cond.equalsIgnoreCase("#endif")) {
			ParseSymbol symbol = new ParseSymbol("#endif", hashmark.getRow(), i);
			symbol.setErrorMessage("Loose closing condition");
			symbol.setLineCode(line);

			while (!condStack.empty()) {
				String expr = condStack.peek().getSymbol();
				if (expr.contains("if") && !expr.equals("elif")) {
					condStack.pop();
					return;
				} else {
					condStack.pop();
				}
			}
			errors.push(symbol);
		}
	}

	/*
	 * A helper function that takes the quote ParseSymbol, the current line of
	 * code, and the index. The function will scan for the next quote and jump
	 * to the index of that quote. If it does not find the next quote, the quote
	 * is pushed into the error stack and normal parsing from the next index of
	 * the quote.
	 */
	private int quoteParse(ParseSymbol quote, String line, int i) {
		String q = quote.getSymbol();
		for (int start = quote.getCol() + 1; start < line.length(); start++) {
			String sub = line.substring(start, start + 1);
			
			
			if(sub.equals("\\")){
				if(start+2 > line.length()){
					break;
				}
				
				String sub2 = line.substring(start+1, start+2);
				if(sub2.equals("\"") || sub2.equals("\'"))
					start++;
			}
			if (sub.equals(q)) {
				
				return start;
			}
			
		}
		quote.setErrorMessage("Unclosed quotation");
		quote.setLineCode(line);
		errors.push(quote);
		return i;
	}

	/*
	 * A helper function that gathers each specialized stack and return a sorted
	 * arraylist.
	 */
	private ArrayList<ParseSymbol> sortErrors() {

		ArrayList<ParseSymbol> arr = new ArrayList<ParseSymbol>();

		while (!stack.isEmpty())
			arr.add(stack.pop());
		while (!stack2.isEmpty())
			arr.add(stack2.pop());
		while (!stack3.isEmpty())
			arr.add(stack3.pop());
		while (!errors.isEmpty())
			arr.add(errors.pop());
		while (!condStack.isEmpty())
			arr.add(condStack.pop());

		// Uses a comparator to sort the arraylist
		Collections.sort(arr, new Comparator<ParseSymbol>() {

			@Override
			public int compare(ParseSymbol a, ParseSymbol b) {
				if (a.getRow() == b.getRow()) {
					return a.getCol() - b.getCol();
				}
				return a.getRow() - b.getRow();
			}
		});

		return arr;

	}

	/*
	 * Helper Function that will print out errors as set by the respective
	 * helper function
	 */
	private void printErrors(ArrayList<ParseSymbol> arr) {
		if (arr.size() == 0)
			return;

		else {
			hasErrors = true;
			for (ParseSymbol p : arr)
				System.err.println(p.getRow() + ":" + (p.getCol() + 1) + ":\""
						+ p.getLineCode() + "\":ERROR: " + p.getErrorMessage());
		}

	}

}

/*
 * An object class that holds the parse symbol as well as row number and col
 * number. Can also hold the line the error appears and an error message for use
 * with the error printer method
 */
class ParseSymbol {
	private String symbol;
	private String literal; // Will be null if the symbol passed in is not a
							// bracket or parenthesis
	private String errorType;
	private String line;
	private int row;
	private int col;

	ParseSymbol(String symbol, int row, int col) {
		this.symbol = symbol;
		this.row = row;
		this.col = col;
		errorType = "";
		setLiteral(symbol);
	}

	ParseSymbol(String symbol, int row, int col, String line) {
		this.symbol = symbol;
		this.row = row;
		this.col = col;
		this.line = line;
		setLiteral(symbol);
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
		setLiteral(symbol);
	}

	public int getRow() {
		return row;
	}

	public void setLine(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getErrorMessage() {
		return errorType;
	}

	public void setErrorMessage(String Message) {
		this.errorType = Message;
	}

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String symbol) {
		if (symbol.equals("{") || symbol.equals("}"))
			literal = "brace";

		if (symbol.equals("(") || symbol.equals(")"))
			literal = "parenthesis";

		if (symbol.equals("[") || symbol.equals("]"))
			literal = "bracket";

	}

	public String getLineCode() {
		return line;
	}

	public void setLineCode(String line) {
		this.line = line;
	}

}
