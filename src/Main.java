import driver.Driver;

/**
 * Executes the SIMPLE compiler.
 */
public class Main {

	/**
	 * Starts the SIMPLE compiler with optional arguments from standard input.
	 *
	 * @param args The arguments that indicate what the SIMPLE compiler will perform.
	 */
	public static void main(String[] args) {
		Driver.getInstance().run(args);
		//try { Driver.getInstance().run(args); } catch (Exception ignored) {}
	}
}