package codeGenerator.optimizedCodeGenerator;

import codeGenerator.AbstractAssemblyCodePrinter;
import codeGenerator.utilities.Instruction;
import codeGenerator.utilities.Register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Manages registers for the optimized code generator.
 */
public class RegisterPool extends AbstractAssemblyCodePrinter {

	/**
	 * Represents the instance of the register pool.
	 */
	private static RegisterPool m_instance;

	/**
	 * Represents the map of registers to the stack of values spilled.
	 */
	private Map<Register, Stack<Integer>> m_spilledRegisters;

	/**
	 * Represents the number of pushes currently done.
	 */
	private int m_spill = 0;

	/**
	 * Gets the instance of the register warden.
	 */
	public static RegisterPool getInstance() {
		if (m_instance == null) {
			m_instance = new RegisterPool();
		}
		return m_instance;
	}

	/**
	 * Constructs the register warden.
	 */
	private RegisterPool() {
		m_spilledRegisters = new HashMap<Register, Stack<Integer>>();
		m_spilledRegisters.put(Register.R9, new Stack<Integer>());
		m_spilledRegisters.put(Register.R10, new Stack<Integer>());
		m_spilledRegisters.put(Register.R11, new Stack<Integer>());
		m_spilledRegisters.put(Register.R12, new Stack<Integer>());
		m_spilledRegisters.put(Register.R13, new Stack<Integer>());
		m_spilledRegisters.put(Register.R14, new Stack<Integer>());
		m_spilledRegisters.put(Register.R15, new Stack<Integer>());
	}

	/**
	 * Requests a new register from the pool.
	 */
	public Register requestNewRegister() {
		int smallestSize = Integer.MAX_VALUE;
		Register mostFreeRegister = null;
		Object[] registers = m_spilledRegisters.keySet().toArray();
		Arrays.sort(registers);
		for (Object register : registers) {
			int size;
			if ((size = m_spilledRegisters.get(register).size()) < smallestSize) {
				smallestSize = size;
				mostFreeRegister = (Register) register;
			}
		}
		// If the register has already been used
		if (smallestSize > 0) {
			print(Instruction.PUSHQ, mostFreeRegister);
			m_spilledRegisters.get(mostFreeRegister).push(m_spill++);
		} else {
			m_spilledRegisters.get(mostFreeRegister).push(-1);
		}

		return mostFreeRegister;
	}

	/**
	 * Frees a register, putting it back in the pool.
	 *
	 * @param register The register to free.
	 */
	public void freeRegister(Register register) {
		if (m_spilledRegisters.get(register).size() == 1) {
			m_spilledRegisters.get(register).clear();
		} else if (m_spilledRegisters.get(register).size() > 1) {
			int index = 8 * m_spilledRegisters.get(register).pop();
			if (index == 0) {
				print(Instruction.MOV, register, "qword ptr[" + Register.RSP + "]");
			} else {
				print(Instruction.MOV, register, "qword ptr[" + Register.RSP + " + " + index + "]");
			}
		}
	}

	/**
	 * Frees a register, putting it back in the pool.
	 *
	 * @param register The register to free.
	 */
	public void freeRegister(String register) {
		if (Register.contains(register)) {
			freeRegister(Register.get(register));
		}
	}

	/**
	 * Resets the stack.
	 */
	public void reset() {
		if (m_spill > 0) {
			int restorePoint = m_spill * 8;
			print(Instruction.ADDQ, Register.RSP, restorePoint);
			m_spill = 0;
		}
	}
}
