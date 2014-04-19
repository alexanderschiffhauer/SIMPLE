package codeGenerator.utilities;

/**
 * Defines the AMD64 registers.
 */
public enum Register {
	RAX {
		@Override
		public String toString() {
			return "rax";
		}
	},
	RBX {
		@Override
		public String toString() {
			return "rbx";
		}
	},
	RCX {
		@Override
		public String toString() {
			return "rcx";
		}
	},
	RDX {
		@Override
		public String toString() {
			return "rdx";
		}
	},
	RSI {
		@Override
		public String toString() {
			return "rsi";
		}
	},
	RDI {
		@Override
		public String toString() {
			return "rdi";
		}
	},
	RBP {
		@Override
		public String toString() {
			return "rbp";
		}
	},
	RSP {
		@Override
		public String toString() {
			return "rsp";
		}
	},
	R8 {
		@Override
		public String toString() {
			return "r8";
		}
	},
	R9 {
		@Override
		public String toString() {
			return "r9";
		}
	},
	R10 {
		@Override
		public String toString() {
			return "r10";
		}
	},
	R11 {
		@Override
		public String toString() {
			return "r11";
		}
	},
	R12 {
		@Override
		public String toString() {
			return "r12";
		}
	},
	R13 {
		@Override
		public String toString() {
			return "r13";
		}
	},
	R14 {
		@Override
		public String toString() {
			return "r14";
		}
	},
	R15 {
		@Override
		public String toString() {
			return "r15";
		}
	},
	RIP {
		@Override
		public String toString() {
			return "rip";
		}
	};

	/**
	 * Checks if a string is a register.
	 *
	 * @param string The string under question.
	 */
	public static boolean contains(String string) {
		for (Register register : Register.values()) {
			if (string.equals(register.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a string is a register.
	 *
	 * @param string The string under question.
	 */
	public static Register get(String string) {
		for (Register register : Register.values()) {
			if (string.equals(register.toString())) {
				return register;
			}
		}
		return null;
	}
}
