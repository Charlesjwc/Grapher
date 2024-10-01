import java.util.*;
/**
 *	Grapher - input f(x) = function, and f(x) is graphed.
 * 	Graph is warped to fit into frame, and can be adjusted.
 * 	Grid rounds doubles down to nearest increment.
 *
 *	@author	Charles Chang
 *	@since	21 January, 2024
 *
 */

public class Grapher {
	//	Field variables
	
	//	The graph, true for white, false for black
	private boolean[][] graph;
	//	Equation
	private String equation;
	//	Window
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	
	//	Default constructor
	public Grapher() {
		graph = new boolean[20][20];
		equation = "";
		xMin = -10;
		xMax = 10;
		yMin = -10;
		yMax = 10;
	}
	
	/**	Prints graph
	 */
	public void print() {
		//	Traverse 2D array
		for (int i = graph.length - 1; i >= 0; i--) {
			for (int j = 0; j < graph[i].length; j++) {
				if (graph[i][j]) System.out.print("X ");
				else System.out.print("  ");
			}
			System.out.print("\n");
		}
	}
	
	/**	Sets graph to all false */
	public void resetGraph() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				graph[i][j] = false;
			}
		}
	}
	
	/**	Creates graph from equation. Tests f(x) in 20 increments from 
	 * 	xMin to xMax.
	 */
	public void createGraph() {
		//	X scale to increment from
		double xInc = (xMax - xMin) / 20;
		//	Scan from left to right
		for (int i = 0; i < 200; i++) {
			//	Calculate y
			double yVal = recurseSolve(equation, ((((double) i / 10) + xMin) 
															* xInc));
			System.out.println(((((double) i / 10) - 10) * xInc) + ", " + yVal);
			double yInc = (yMax - yMin) / 20;
			int yInt = (int)Math.round((yVal - yMin)/yInc);
			//	Set graph if within bounds yVal == yVal to check for NaN
			if (yInt < 20 && yInt >= 0 && yVal == yVal) {
				graph[yInt][i / 10] = true;
			}
		}
	}
	
	/**	Recursively breaks down equation into pregressively smaller parts
	 * 	until a simple expression is reached, following PEMDAS.
	 * 	Same with exponents, but before parenthesis.
	 * 	@param	String	expression
	 * 	@param	Double	x value
	 * 	@return	double	solution
	 */
	public double recurseSolve(String expression, double xVal) {
		//	Trim
		expression = expression.trim();
		//	System.out.println("Expression to solve: " + expression);
		
		//	Base cases
		
		//	If equation is constant, return that constant
		try{
			double constant = Double.parseDouble(expression);
			return constant;
		}
		catch (Exception e) {}
		
		//	Check for x
		if (expression.toLowerCase().equals("x")) return xVal;
		
		
		//	Parenthesis **
		if (expression.lastIndexOf("(") != -1) {
			int insideIndex = highestParen(expression);
			int insideIndex2 = expression.substring(insideIndex).indexOf(')')
				+ (expression.length() -  expression.substring(insideIndex)
				.length());
			double insideExp = recurseSolve(expression.substring(
					insideIndex + 1, insideIndex2), xVal);
			return recurseSolve(expression.substring(0, insideIndex) + 
				insideExp + expression.substring(insideIndex2 
				+ 1), xVal);
		}
		
		/*	Scan through expression, and find all instances of + and -	
		 * 	recurse solve each section between + and -
		 * 	recurse solve those solutions
		 */
		double sum = 0;
		ArrayList<Integer> ASIndices = new ArrayList<>();
		splitAS(ASIndices, expression);
		if (ASIndices.size() != 0) {
			//	Add first section
			sum += recurseSolve(expression.substring(0, ASIndices.
			get(0)), xVal);
			//	Add last fencepost to ASIndices
			ASIndices.add(expression.length());
			//	Calculate the rest of the sections
			for (int i = 0; i < ASIndices.size() - 1; i++) {
				//	Check + or -
				switch (expression.charAt(ASIndices.get(i))) {
					case '+': sum += recurseSolve(expression.substring
					(ASIndices.get(i) + 1, ASIndices.get(i + 1)), xVal);
					break;
					case '-': sum -= recurseSolve(expression.substring
					(ASIndices.get(i) + 1, ASIndices.get(i + 1)), xVal);
				}
			}
			return sum;
		}
		
		/*	Scan through expression, and find all instances of + and -	
		 * 	recurse solve each section between + and -
		 * 	recurse solve those solutions
		 */
		int product = 0;
		ArrayList<Integer> MDIndices = new ArrayList<>();
		splitMD(MDIndices, expression);
		if (MDIndices.size() != 0) {
			//	Add first section
			sum += recurseSolve(expression.substring(0, MDIndices.
			get(0)), xVal);
			//	Add last fencepost to ASIndices
			MDIndices.add(expression.length());
			//	Calculate the rest of the sections
			for (int i = 0; i < MDIndices.size() - 1; i++) {
				//	Check + or -
				switch (expression.charAt(MDIndices.get(i))) {
					case '*': sum *= recurseSolve(expression.substring
					(MDIndices.get(i) + 1, MDIndices.get(i + 1)), xVal);
					break;
					case '/': sum /= recurseSolve(expression.substring
					(MDIndices.get(i) + 1, MDIndices.get(i + 1)), xVal);
				}
			}
			return sum;
		}
		
		//	Exponents
		int index = expression.indexOf("^");
		if (index != -1)
			return Math.pow(recurseSolve(expression.substring(0, index), 
				xVal), recurseSolve(expression.substring(index + 1), xVal));
		return -1;
	}
	
	/**	Find all indices of + and - and add it to given ArrayList
	 * 	@param	ArrayList<Integer>	ArrayList to list indices
	 * 	@param	String				Expression
	 */
	public void splitAS(ArrayList<Integer> indices, String expression) {
		for (int i = 1; i < expression.length(); i++)
			if (expression.charAt(i) == '+' || expression.charAt(i) == '-')
				indices.add(i);
	}
	
	/**	Find all indices of * and / and add it to given ArrayList
	 * 	@param	ArrayList<Integer>	ArrayList to list indices
	 * 	@param	String				Expression
	 */
	public void splitMD(ArrayList<Integer> indices, String expression) {
		for (int i = 0; i < expression.length(); i++)
			if (expression.charAt(i) == '*' || expression.charAt(i) == '/')
				indices.add(i);
	}
	/**	Returns the first index of the "highest level" of parenthesis
	 * 	An index is the "highest level" when it marks the parenthesis
	 * 	that makes the highest ( count - ) count
	 * 	@param	String	expression
	 * 	@return	int		highest index
	 */
	public int highestParen(String expression) {
		//	Keep track of ( count - ) count
		int parenCount = 0;
		int highestCount = 0;
		//	Keep track of index of highest level
		int highestIndex = 0;
		//	Traverse string
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') parenCount ++;
			if (expression.charAt(i) == ')') parenCount --;
			if (parenCount > highestCount) {
				highestCount = parenCount;
				highestIndex = i;
			}
		}
		return highestIndex;
	}
	
	/**	Checks in given string is a constant consisting of only digits, 
	 * 	a "-" or digit as the first char, and 1 or 0 "."
	 * 	@param	String	expression
	 * 	@return	boolean	whether expression is constant
	 */
	public boolean isConstant(String expression) {
		//	Keep track of whether . is the first
		boolean firstDecimal = true;
		//	Check that there is no more than 1 "-", and that it's at the start
		if (expression.lastIndexOf("-") > 0) return false;
		//	Check first
		char first = expression.charAt(0);
		if (first != '-' && !Character.isDigit(first)) return false;
		if (expression.length() == 1) return true;
		//	Check rest
		for (int i = 1; i < expression.length(); i++) {
			if (expression.charAt(i) == '.') {
				if (firstDecimal) firstDecimal = false;
			} else return false;
			if (!Character.isDigit(expression.charAt(i))) return false;
		}
		return true;
	}

	/**	Prompt the user to set the window of the graph */
	public void setWindow() {
		boolean valid = false;
		while (!valid) {
			int choice = Prompt.getInt("Do you want to set a window? (1 for yes, 0 for no)");
			if (choice == 1) valid = true;
			if (choice == 0) return;
		}
		xMin = Prompt.getDouble("Set xMin (enter a double)");
		xMax = Prompt.getDouble("Set xMax (enter a double)");
		yMin = Prompt.getDouble("Set yMin (enter a double)");
		yMax = Prompt.getDouble("Set yMax (enter a double)");
		resetGraph();
		createGraph();
		print();
	}

	public static void main(String[] args) {
		Grapher gr = new Grapher();
		gr.run();
	}
	
	public void run() {
		equation = Prompt.getString("f(x)");
		createGraph();
		print();
		setWindow();
	}
}
