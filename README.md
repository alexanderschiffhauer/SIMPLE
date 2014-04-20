# SIMPLE
SIMPLE is the educational programming language used at the [compilers and interpreters course at The Johns Hopkins University](http://gaming.jhu.edu/~phf/2013/spring/cs328/).

## Extended Backus-Naur Form
The langauge's syntax is defined with the following EBNF:

	Program = "PROGRAM" identifier ";" Declarations ["BEGIN" Instructions] "END" identifier "." .
	Declarations = { ConstDecl | TypeDecl | VarDecl | ProcDecl } .
	ConstDecl = "CONST" {identifier "=" Expression ";"} .
	TypeDecl = "TYPE" {identifier "=" Type ";"} .
	VarDecl = "VAR" {IdentifierList ":" Type ";"} .
	ProcDecl = "PROCEDURE" identifier "(" [Formals] ")" [":" Type] ";" { VarDecl } [ "BEGIN" Instructions ] [ "RETURN" Expression ] "END" identifier ";" .
	Type = identifier | "ARRAY" Expression "OF" Type | "RECORD" {IdentifierList ":" Type ";"} "END" .
	Formals = Formal { ";" Formal } .
	Formal = IdentifierList ":" Type .

	Expression = ["+"|"-"] Term {("+"|"-") Term} .
	Term = Factor {("*"|"DIV"|"MOD") Factor} .
	Factor = integer | Designator | "(" Expression ")" | Call .

	Instructions = Instruction {";" Instruction} .
	Instruction = Assign | If | Repeat | While | Read | Write | Call .
	Assign = Designator ":=" Expression .
	If = "IF" Condition "THEN" Instructions ["ELSE" Instructions] "END" .
	Repeat = "REPEAT" Instructions "UNTIL" Condition "END" .
	While = "WHILE" Condition "DO" Instructions "END" .
	Condition = Expression ("="|"#"|"<"|">"|"<="|">=") Expression .
	Write = "WRITE" Expression .
	Read = "READ" Designator .
	Call = identifier "(" [Actuals] ")" .

	Actuals = ExpressionList .
	Designator = identifier Selector .
	Selector = {"[" ExpressionList "]" | "." identifier} .
	IdentifierList = identifier {"," identifier} .
	ExpressionList = Expression {"," Expression} .

	identifier = letter {letter | digit} .
	integer = digit {digit} .
	letter = "a" | "b" | .. | "z" | "A" | "B" | .. | "Z" .
	digit = "0" | "1" | .. | "9" .

## Example

The following example is an implementation of writing a Fibonacci sequence:

	PROGRAM Fibonacci;
		VAR n, first, second, next, c: INTEGER;
		BEGIN 
  		n := 10;
  		c := 0;
  		first := 0;
  		second := 1;
  		REPEAT
    		IF c <= 1 THEN
      			next := c
    		ELSE
      			next := first + second;
      			first := second;
      			second := next
    		END;
    		WRITE next;
    		c := c + 1
  		UNTIL c = n END
	END Fibonacci.


# SIMPLE Compiler
SIMPLE Compiler is written in *Java* 6 without any third-party dependencies and comprises seven major components:

* Lexical Analysis
	* The scanner for SIMPLE reads the source program as a sequence of characters and recognizes "larger" textual units called tokens.
* Syntactic Analysis
	* The parser for SIMPLE reads the source program as a sequence of tokens and recognizes its structure in the form of a parse tree.
* Semantic Analysis
	* The symbol table (ST) keeps track of the declarations made in a SIMPLE program.
	* The abstract syntax (AST) tree will keep track of the instructions, expressions, and conditions in a SIMPLE program. 
* Code Generation
	* The code generator performs storage allocation for all variables in the ST and generates instructions during a post-order-style traversal of the AST (roughly one node at a time).
* Optimized Code Generation
	* The optimized code generator is akin to the code generator, but experimental and significantly faster.
* Interpretation
	* The interpreter traverses the ST to build an environment which tracks the run-time value of all variables in a SIMPLE program; it then performs a post-order-style traversal of the AST and (using the environment as well as an auxiliary stack) executes the program one AST node at a time.
* Compile-Time Recursive Error-Handling
	* Upon encountering a *significant* syntactical error (an error from which the compiler cannot continue analyzing code under normal circumstances), the error handler will attempt to find the next reasonable chunk of code. If found, the compiler will re-compile the program from the start, ignoring the syntactical errors already found, and continue compiling at the new position, producing outstanding and subsequent errors.
		* This works reasonably well; however, there is room for significant improvement.

#### Bells and Whistles

The following features are supported by this compiler, but are not required in the language specification.

* Support for mutual recursion without forward declarations
* Support for shadowing a procedure in a recursive procedure

# Build
	$ make 				# compiles *sc* using the JDK
	$ make clean 		# removes files generated during *sc* compilation
# Run
	$ ./sc [-(s|c|t|a|i|x)] [filename]
		$ /.sc 			# (no arguments) runs the scanner, parser, symbol table, abstract syntax tree-logic, and x64 code generator.
		$ ./sc -s 		# runs the scanner and produce a list of recognized tokens.
		$ ./sc -c 		# runs the scanner and parser and produce a concrete syntax tree (stdin).
		$ ./sc -c -g 	# runs the scanner and parser and produce a concrete syntax tree (DOT).
		$ ./sc -t 		# runs the scanner, parser, and symbol table-logic.
		$ ./sc -t -g 	# runs the scanner, parser, and symbol table-logic and produce a symbol table (DOT).
		$ ./sc -a 		# runs the scanner, parser, symbol table and abstract syntax tree-logic.
	 	$ ./sc -a -g 	# runs the scanner, parser, symbol table and abstract syntax tree-logic and produce an abstract syntax tree (DOT).
		$ ./sc -i 		# runs the scanner, parser, symbol table, abstract syntax tree-logic, and interpreter.
		$ /.sc -x 		# runs the scanner, parser, symbol table, abstract syntax tree-logic, and optimized x64 code generator.

Note: If no filename is specified, *sc* will read in code, line by line, from stdin.

# Compatibility
*x64* executables generated by *sc* are **incompatible** with Mac and Windows.  These executables only work with Linux.  The supplied interpreter is operating system agnostic, however.

The compiler was written and tested using the JDK on Lubuntu 12.10 64-bit.  In the Synaptic Package Manager, the package's name is "default-jdk".

# Code of Ethics
Every computer scientist should write a compiler, and no student should cheat.  Unfortunately, not all computer scientists have written compilers and some students cheat. 

If you are a student writing a compiler, please reference your school's code of ethics and/or relevant professor(s) before viewing this source code.
