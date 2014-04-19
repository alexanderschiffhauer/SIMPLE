package codeGenerator.utilities;

/**
 * Defines the AMD64 instructions.
 */
public enum Instruction {
	MOVQ {
		@Override
		public String toString() {
			return "movq";
		}
	}, MOV {
		@Override
		public String toString() {
			return "mov";
		}
	}, LEA {
		@Override
		public String toString() {
			return "lea";
		}
	}, CMP {
		@Override
		public String toString() {
			return "cmp";
		}
	}, JE {
		@Override
		public String toString() {
			return "je";
		}
	}, JNE {
		@Override
		public String toString() {
			return "jne";
		}
	}, JG {
		@Override
		public String toString() {
			return "jg";
		}
	}, JL {
		@Override
		public String toString() {
			return "jl";
		}
	}, JGE {
		@Override
		public String toString() {
			return "jge";
		}
	}, JLE {
		@Override
		public String toString() {
			return "jle";
		}
	}, CALL {
		@Override
		public String toString() {
			return "call";
		}
	}, RET {
		@Override
		public String toString() {
			return "ret";
		}
	}, XOR {
		@Override
		public String toString() {
			return "xor";
		}
	}, ADDQ {
		@Override
		public String toString() {
			return "addq";
		}
	}, SUB {
		@Override
		public String toString() {
			return "sub";
		}
	}, IMUL {
		@Override
		public String toString() {
			return "imul";
		}
	}, IDIV {
		@Override
		public String toString() {
			return "idiv";
		}
	}, CQO {
		@Override
		public String toString() {
			return "cqo";
		}
	}, POP {
		@Override
		public String toString() {
			return "pop";
		}
	}, PUSH {
		@Override
		public String toString() {
			return "push";
		}
	}, PUSHQ {
		@Override
		public String toString() {
			return "pushq";
		}
	}
}
