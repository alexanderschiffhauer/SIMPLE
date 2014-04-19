package driver;

import codeGenerator.AbstractCodeGenerator;
import codeGenerator.codeGenerator.CodeGenerator;
import codeGenerator.optimizedCodeGenerator.OptimizedCodeGenerator;
import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import interpreter.Interpreter;
import parser.Parser;
import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.abstractSyntaxTree.printers.AbstractSyntaxTreeDotSyntaxPrinter;
import parser.semanticAnalysis.abstractSyntaxTree.printers.AbstractSyntaxTreePrinter;
import parser.semanticAnalysis.abstractSyntaxTree.printers.IAbstractSyntaxTreePrinter;
import parser.semanticAnalysis.symbolTable.printers.ISymbolTablePrinter;
import parser.semanticAnalysis.symbolTable.printers.SymbolTableDotSyntaxPrinter;
import parser.semanticAnalysis.symbolTable.printers.SymbolTablePrinter;
import parser.syntacticAnalysis.printers.IParseTreePrinter;
import parser.syntacticAnalysis.printers.ParseTreeDotSyntaxPrinter;
import parser.syntacticAnalysis.printers.ParseTreePrinter;
import scanner.Scanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Verifies standard input arguments and runs and orchestrates the modules of the SIMPLE compiler.
 */
public class Driver {

	/**
	 * Represents the instance of the driver.
	 */
	private static Driver m_instance;

	/**
	 * Represents the name of the file, if any, being read.
	 */
	private String m_fileName;

	/**
	 * Constructs the driver.
	 */
	private Driver() {}

	/**
	 * Gets the instance of the driver.
	 */
	public static Driver getInstance() {
		if (m_instance == null) {
			m_instance = new Driver();
		}
		return m_instance;
	}

	/**
	 * Runs the SIMPLE compiler with the supplied arguments.
	 *
	 * @param args The arguments with which to execute the SIMPLE compiler.
	 *             -s will run the scanner and produce a list of recognized tokens.
	 *             -c will run the scanner and parser and produce a concrete syntax tree (stdin).
	 *             -c -g will run the scanner and parser and produce a concrete syntax tree (DOT).
	 *             -t will run the scanner, parser, and symbol table-logic.
	 *             -t -g will run the scanner, parser, and symbol table-logic and produce a symbol table (DOT).
	 *             -a will run the scanner, parser, symbol table and abstract syntax tree-logic.
	 *             -a -g will run the scanner, parser, symbol table and abstract syntax tree-logic and produce an abstract syntax tree (DOT).
	 *             -i will run the scanner, parser, symbol table, abstract syntax tree-logic, and interpreter.
	 *             No arguments will run the scanner, parser, symbol table, abstract syntax tree-logic, and AMD64 code generator.
	 *             -x will run the scanner, parser, symbol table, abstract syntax tree-logic, and optimized AMD64 code generator.
	 */
	public void run(String[] args) {
		List<Arg> argumentList = getArgs(args);
		InputStream inputStream = getInputStream(args);
		executeArgs(argumentList, inputStream);
	}

	/**
	 * Gets the list of argument from its string-based representation.
	 *
	 * @param args The arguments to parse.
	 */
	private List<Arg> getArgs(String[] args) {
		List<Arg> argumentList = new ArrayList<Arg>();
		if (args.length == 0 || args[0].charAt(0) != '-') {
			argumentList.add(Arg.CODE_GENERATOR);
			return argumentList;
		} else {
			for (String parameterizedArg : args) {
				if (parameterizedArg.charAt(0) == '-') {
					boolean isValidArgument = false;
					for (Arg arg : Arg.values()) {
						if (parameterizedArg.equals(arg.toString())) {
							argumentList.add(arg);
							isValidArgument = true;
							break;
						}
					}
					if (!isValidArgument) {
						return ExceptionHandler.getInstance().throwException(Exception.INVALID_ARGS, ExceptionStrength.STRONG);
					}
				}
			}
			if (argumentList.size() > 2) {
				return ExceptionHandler.getInstance().throwException(Exception.NUMBER_OF_ARGS, ExceptionStrength.STRONG);
			} else {
				return argumentList;
			}
		}
	}

	/**
	 * Gets the input stream from which to read code.
	 *
	 * @param args The arguments to parse.
	 */
	private InputStream getInputStream(String[] args) {
		for (String arg : args) {
			if (arg.charAt(0) != '-') {
				try {
					m_fileName = arg;
					return new FileInputStream(arg);
				} catch (FileNotFoundException exception) {
					return ExceptionHandler.getInstance().throwException(Exception.FILE_NOT_FOUND, ExceptionStrength.STRONG);
				}
			}
		}
		return System.in;
	}

	/**
	 * Executes the functionality associated with an argument.
	 *
	 * @param args        The arguments to execute.
	 * @param inputStream The input stream from which to read code.
	 */
	private void executeArgs(List<Arg> args, InputStream inputStream) {
		Scanner.getInstance().setInputStream(inputStream);
		switch (args.get(0)) {
			case SCANNER:
				if (isUsingGraphics(args)) {
					ExceptionHandler.getInstance().throwException(Exception.INVALID_ARGS, ExceptionStrength.STRONG);
				}
				Scanner.getInstance().printAllTokens();
				break;
			case CONCRETE_SYNTAX_TREE:
				IParseTreePrinter parseTreePrinter;
				if (isUsingGraphics(args)) {
					parseTreePrinter = ParseTreeDotSyntaxPrinter.getInstance();
					Parser.getInstance().addEventListener(ParseTreeDotSyntaxPrinter.getInstance());
				} else {
					parseTreePrinter = ParseTreePrinter.getInstance();
					Parser.getInstance().addEventListener(ParseTreePrinter.getInstance());
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				SemanticValidator.getInstance().disable();
				if (Parser.getInstance().parseTokens()) {
					parseTreePrinter.printParseTree();
				}
				break;
			case SYMBOL_TABLE:
				ISymbolTablePrinter symbolTablePrinter;
				if (isUsingGraphics(args)) {
					symbolTablePrinter = SymbolTableDotSyntaxPrinter.getInstance();
				} else {
					symbolTablePrinter = SymbolTablePrinter.getInstance();
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				if (Parser.getInstance().parseTokens()) {
					symbolTablePrinter.printSymbolTables();
				}
				break;
			case ABSTRACT_SYNTAX_TREE:
				IAbstractSyntaxTreePrinter abstractSyntaxTreePrinter;
				if (isUsingGraphics(args)) {
					abstractSyntaxTreePrinter = AbstractSyntaxTreeDotSyntaxPrinter.getInstance();
				} else {
					abstractSyntaxTreePrinter = AbstractSyntaxTreePrinter.getInstance();
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				if (Parser.getInstance().parseTokens()) {
					abstractSyntaxTreePrinter.printAbstractSyntaxTree();
				}
				break;
			case INTERPRETER:
				if (isUsingGraphics(args)) {
					ExceptionHandler.getInstance().throwException(Exception.INVALID_ARGS, ExceptionStrength.STRONG);
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				if (Parser.getInstance().parseTokens()) {
					Interpreter.getInstance().interpret(Parser.getInstance().getAbstractSyntaxTree());
				}
				break;
			case CODE_GENERATOR:
				if (isUsingGraphics(args)) {
					ExceptionHandler.getInstance().throwException(Exception.INVALID_ARGS, ExceptionStrength.STRONG);
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				if (Parser.getInstance().parseTokens()) {
					if (inputStream instanceof FileInputStream) {
						AbstractCodeGenerator.setFileName(m_fileName);
					}
					CodeGenerator.getInstance().generate(Parser.getInstance().getAbstractSyntaxTree());
				}
				break;
			case OPTIMIZED_CODE_GENERATOR:
				if (isUsingGraphics(args)) {
					ExceptionHandler.getInstance().throwException(Exception.INVALID_ARGS, ExceptionStrength.STRONG);
				}
				Parser.getInstance().setTokens(Scanner.getInstance().getAllTokens());
				if (Parser.getInstance().parseTokens()) {
					if (inputStream instanceof FileInputStream) {
						AbstractCodeGenerator.setFileName(m_fileName);
					}
					OptimizedCodeGenerator.getInstance().generate(Parser.getInstance().getAbstractSyntaxTree());
				}
				break;
			default:
				ExceptionHandler.getInstance().throwException(Exception.UNSUPPORTED_OPERATION, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Queries if a list of arguments is using graphics.
	 *
	 * @param args The list of arguments for which to test this condition.
	 */
	private boolean isUsingGraphics(List<Arg> args) {
		return args.size() > 1 && args.get(1) == Arg.GRAPHICS;
	}

	/**
	 * Represents the valid types of arguments for the SIMPLE compiler.
	 */
	private enum Arg {
		CODE_GENERATOR {
			/** {@inheritDoc} */
			public String toString() {
				return "";
			}
		}, OPTIMIZED_CODE_GENERATOR {
			/** {@inheritDoc} */
			public String toString() {
				return "-x";
			}
		},
		INTERPRETER {
			/** {@inheritDoc} */
			public String toString() {
				return "-i";
			}
		},
		SCANNER {
			/** {@inheritDoc} */
			public String toString() {
				return "-s";
			}
		},
		ABSTRACT_SYNTAX_TREE {
			/** {@inheritDoc} */
			public String toString() {
				return "-a";
			}
		},
		CONCRETE_SYNTAX_TREE {
			/** {@inheritDoc} */
			public String toString() {
				return "-c";
			}
		},
		SYMBOL_TABLE {
			/** {@inheritDoc} */
			public String toString() {
				return "-t";
			}
		},
		GRAPHICS {
			/** {@inheritDoc} */
			public String toString() {
				return "-g";
			}
		}
	}
}
