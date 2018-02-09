package cspSolver;
import java.util.ArrayList; //Added for FC
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import sudoku.Converter;
import sudoku.SudokuFile;

/**
 * Backtracking solver. 
 *
 */
public class BTSolver implements Runnable{

	//===============================================================================
	// Properties
	//===============================================================================

	private ConstraintNetwork network;
	private static Trail trail = Trail.getTrail();
	//private static Trail trail2 = Trail.getTrail(); //for FC
	private static Trail trail3 = Trail.getTrail(); //for LCV
	private static Trail trail4 = Trail.getTrail(); //for LCV
	
	private boolean hasSolution = false;
	private SudokuFile sudokuGrid;
	private boolean hasError = false; //my var
	List<Variable> neighbors = new ArrayList<Variable>();
	List<Variable> neighbors1 = new ArrayList<Variable>();
	List<Constraint> constraints = new ArrayList<Constraint>();
	
	private int numAssignments;
	private int numBacktracks;
	private long startTime;
	private long endTime;
	private long lastChecked;
	Calendar today = new GregorianCalendar();
	
	public enum VariableSelectionHeuristic 	{ None, MinimumRemainingValue, Degree, MRV_Degree };
	public enum ValueSelectionHeuristic 		{ None, LeastConstrainingValue };
	public enum ConsistencyCheck				{ None, ForwardChecking, FC_CP, ArcConsistency };
	
	private VariableSelectionHeuristic varHeuristics;
	private ValueSelectionHeuristic valHeuristics;
	private ConsistencyCheck cChecks;
	//===============================================================================
	// Constructors
	//===============================================================================

	public BTSolver(SudokuFile sf)
	{
		this.network = Converter.SudokuFileToConstraintNetwork(sf);
		this.sudokuGrid = sf;
		numAssignments = 0;
		numBacktracks = 0;
	}

	//===============================================================================
	// Modifiers
	//===============================================================================
	
	public void setVariableSelectionHeuristic(VariableSelectionHeuristic vsh)
	{
		this.varHeuristics = vsh;
	}
	
	public void setValueSelectionHeuristic(ValueSelectionHeuristic vsh)
	{
		this.valHeuristics = vsh;
	}
	
	public void setConsistencyChecks(ConsistencyCheck cc)
	{
		this.cChecks = cc;
	}
	//===============================================================================
	// Accessors
	//===============================================================================

	/** 
	 * @return true if a solution has been found, false otherwise. 
	 */
	public boolean hasSolution()
	{
		return hasSolution;
	}

	/**
	 * @return solution if a solution has been found, otherwise returns the unsolved puzzle.
	 */
	public SudokuFile getSolution()
	{
		return sudokuGrid;
	}

	public void printSolverStats()
	{
		System.out.println("Time taken:" + (endTime-startTime) + " ms");
		System.out.println("Number of assignments: " + numAssignments);
		System.out.println("Number of backtracks: " + numBacktracks);
	}

	/**
	 * 
	 * @return time required for the solver to attain in seconds
	 */
	public long getstartTimeTaken() //my method
	{
		return startTime;
	}
	
	public long getendTimeTaken()  //my method
	{
		return endTime;
	}
	
	public boolean isError()  //my method
	{
		return hasError;
	}
	
	public long getlastChecked()
	{
		return lastChecked;
	}
	
	
	public long getTimeTaken()
	{
		return endTime-startTime;
	}

	public int getNumAssignments()
	{
		return numAssignments;
	}

	public int getNumBacktracks()
	{
		return numBacktracks;
	}

	public ConstraintNetwork getNetwork()
	{
		return network;
	}

	//===============================================================================
	// Helper Methods
	//===============================================================================

	/**
	 * Checks whether the changes from the last time this method was called are consistent. 
	 * @return true if consistent, false otherwise
	 */
	private boolean checkConsistency(Variable v)
	{
		boolean isConsistent = false;
		switch(cChecks)
		{
		case None: 				isConsistent = assignmentsCheck();
		break;
		case ForwardChecking: 	isConsistent = forwardChecking(v); 
		break;
		case FC_CP: 			isConsistent = FC_CP(v);
		break;
		case ArcConsistency: 	isConsistent = arcConsistency();
		break;
		default: 				isConsistent = assignmentsCheck();
		break;
		}
		return isConsistent;
	}
	
	/**
	 * default consistency check. Ensures no two variables are assigned to the same value.
	 * @return true if consistent, false otherwise. 
	 */
	private boolean assignmentsCheck()
	{
		for(Variable v : network.getVariables())
		{
			if(v.isAssigned())
			{
				for(Variable vOther : network.getNeighborsOfVariable(v))
				{
					if (v.getAssignment() == vOther.getAssignment())
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * TODO: Implement forward checking. 
	 */
	private boolean forwardChecking(Variable v)
	{
		for(Variable s : network.getVariables())
		{
			if(s.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(s);
				for(int i = 0; i < neighbors.size(); i++)
					if(neighbors.get(i).isAssigned())
						if(neighbors.get(i).getAssignment() == s.getAssignment())
							return false;
			}
		}
		
	
		neighbors = network.getNeighborsOfVariable(v);		
		for(int i = 0; i < neighbors.size(); i++)
		{
			if(!neighbors.get(i).isAssigned())
				neighbors.get(i).removeValueFromDomain(v.getAssignment());
			if(neighbors.get(i).isDomainEmpty())
				return false;
		}
		/*
		else if(assignmentsCheck())
		{
			if(v.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(v);
				for(int i = 0; i < neighbors.size(); i++)
				{
					if(!neighbors.get(i).isAssigned())
						for(Integer j : getNextValues(neighbors.get(i)))
							{
								trail2.placeBreadCrumb();
								neighbors.get(i).updateDomain(new Domain(j));
								boolean isConsistent = assignmentsCheck();
							
								if(!isConsistent)
								{
									trail2.undo();
									neighbors.get(i).removeValueFromDomain(j);
								}
								if(isConsistent)
									trail2.undo();
							}
				}
			}
		}*/
		return true;
	}
	
	private boolean FC_CP(Variable v)
	{
		//Basically forward checking with constraint propagation
		ConstraintPropagation();
		for(Variable s : network.getVariables())
		{
			if(s.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(s);
				for(int i = 0; i < neighbors.size(); i++)
					if(neighbors.get(i).isAssigned())
						if(neighbors.get(i).getAssignment() == s.getAssignment())
							return false;
			}
		}
		
	
		neighbors = network.getNeighborsOfVariable(v);		
		for(int i = 0; i < neighbors.size(); i++)
		{
			if(!neighbors.get(i).isAssigned())
				neighbors.get(i).removeValueFromDomain(v.getAssignment());
			if(neighbors.get(i).isDomainEmpty())
				return false;
		}
		return true;
	}
	
	/**
	 * TODO: Implement Maintaining Arc Consistency.
	 */
	private boolean arcConsistency()
	{
		
		return false;
	}
	
	private boolean ConstraintPropagation()
	{
		for(Variable v : network.getVariables())
		{
			if(v.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(v);
				for(int i = 0; i < neighbors.size(); i++)
					if(!neighbors.get(i).isAssigned())
						neighbors.get(i).removeValueFromDomain(v.getAssignment());
			}
		}
		return true;
	}
	/**
	 * Selects the next variable to check.
	 * @return next variable to check. null if there are no more variables to check. 
	 */
	private Variable selectNextVariable()
	{
		Variable next = null;
		switch(varHeuristics)
		{
		case None: 					next = getfirstUnassignedVariable();
		break;
		case MinimumRemainingValue: next = getMRV();
		break;
		case Degree:				next = getDegree();
		break;
		case MRV_Degree:			next = getMRV_Degree();
		default:					next = getfirstUnassignedVariable();
		break;
		}
		return next;
	}
	
	/**
	 * default next variable selection heuristic. Selects the first unassigned variable. 
	 * @return first unassigned variable. null if no variables are unassigned. 
	 */
	private Variable getfirstUnassignedVariable()
	{
		for(Variable v : network.getVariables())
		{
			if(!v.isAssigned())
			{
				return v;
			}
		}
		return null;
	}

	/**
	 * TODO: Implement MRV heuristic
	 * @return variable with minimum remaining values that isn't assigned, null if all variables are assigned. 
	 */
	private Variable getMRV()
	{
		//constraint propagation
		/*for(Variable v : network.getVariables())
		{
			if(v.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(v);
				for(int i = 0; i < neighbors.size(); i++)
					if(!neighbors.get(i).isAssigned())
						neighbors.get(i).removeValueFromDomain(v.getAssignment());
			}
		}*/
		
		//check unassigned variables for the smallest domains
		int min = 10;
		Variable m = null;
		for(Variable v : network.getVariables())
		{
			if(!v.isAssigned())
			{
				if(v.size()<min)
				{
					min = v.size();
					m = v;				//variable m holds the variable with the smallest domain checked
				}
			}
		}
		
		if(min != 10)      
			return m;      //return the variable
		else
			return null;   //if all variables assigned
	}
	
	/**
	 * TODO: Implement Degree heuristic
	 * @return variable constrained by the most unassigned variables, null if all variables are assigned.
	 */
	private Variable getMRV_Degree()
	{
		//mostly MRV code
		
		//constraint propagation
		/*for(Variable v : network.getVariables())
		{
			if(v.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(v);
				for(int i = 0; i < neighbors.size(); i++)
					if(!neighbors.get(i).isAssigned())
						neighbors.get(i).removeValueFromDomain(v.getAssignment());
			}
		}*/
		
		int min = 10;
		Variable m = null;
		int count = 0;
		int count2 = 0;
		
		for(Variable v : network.getVariables())
		{
			if(!v.isAssigned())
			{
				if(v.size()<min)
				{
					min = v.size();
					m = v;
				}
				//degree Heuristics
				count = 0;
				count2 = 0;
				
				//when two variables have share the size of the smallest domain
				if(v.size()== min)
				{	//check neighbors of each variable and count the unassigned neighboring variables
					neighbors = network.getNeighborsOfVariable(m);
					for(int i = 0; i < neighbors.size(); i++)
						if(!neighbors.get(i).isAssigned())
							count++;
					neighbors = network.getNeighborsOfVariable(v);
					for(int i = 0; i < neighbors.size(); i++)
					{
						if(!neighbors.get(i).isAssigned())
							count2++;
					}
					if(count2>count)	//variable constrained by most unassigned neighboring variables is assigned to m
						m = v;
				}
			}
		}
		
		if(m != null)      
			return m;      
		else
			return null;   //if all variables assigned
	}
	
	private Variable getDegree()
	{
		
		int max = 0;
		Variable m = null;
		int count = 0;
	
		for(Variable v : network.getVariables())
		{
			if(!v.isAssigned())
			{
				//degree Heuristics
				count = 0;
					//System.out.println("v is:" + v);
					//check neighbors of each variable and count the unassigned neighboring variables
					constraints = network.getConstraintsContainingVariable(v);
					for(int i = 0; i < constraints.size(); i++)
						if(constraints.get(i).equals(0))
							count++;
				
					//System.out.println("Count is" + count);
					if(count>max)	//variable constrained by most unassigned neighboring variables is assigned to m
					{	
						m = v;
						max = count;
						//System.out.println("Max is" + max);
					}
					if(count == 0)
						return v;
					
			}
		}
		
		if(m != null)
		{
			return m;
		}
		else
			return null;   //if the m was never reached in the for loop
	}
	/**
	 * Value Selection Heuristics. Orders the values in the domain of the variable 
	 * passed as a parameter and returns them as a list.
	 * @return List of values in the domain of a variable in a specified order. 
	 */
	public List<Integer> getNextValues(Variable v)
	{
		List<Integer> orderedValues;
		switch(valHeuristics)
		{
		case None: 						orderedValues = getValuesInOrder(v);
		break;
		case LeastConstrainingValue: 	orderedValues = getValuesLCVOrder(v);
		break;
		default:						orderedValues = getValuesInOrder(v);
		break;
		}
		return orderedValues;
	}
	
	/**
	 * Default value ordering. 
	 * @param v Variable whose values need to be ordered
	 * @return values ordered by lowest to highest. 
	 */
	public List<Integer> getValuesInOrder(Variable v)
	{
		List<Integer> values = v.getDomain().getValues();
		
		Comparator<Integer> valueComparator = new Comparator<Integer>(){

			@Override
			public int compare(Integer i1, Integer i2) {
				return i1.compareTo(i2);
			}
		};
		Collections.sort(values, valueComparator);
		return values;
	}
	
	/**
	 * TODO: LCV heuristic
	 */
	public List<Integer> getValuesLCVOrder(Variable v)
	{
		//leaves the maximum flexibility for subsequent variable assignments
		//tries to leave the domain of neighboring variables with maximum flexibility (high domains)
		//what value will leave the most other values for other variables
		//constraint propagation
		/*for(Variable a : network.getVariables())
		{
			if(a.isAssigned())
			{
				neighbors = network.getNeighborsOfVariable(a);
				for(int i = 0; i < neighbors.size(); i++)
					if(!neighbors.get(i).isAssigned())
						neighbors.get(i).removeValueFromDomain(a.getAssignment());
			}
		}*/
	
		int maxval = 0;		//used to store the value which leaves minimum flexibility for neighbors
		
		List<Integer> values = new ArrayList<Integer>();
		
		int originalSize = v.size();   
		
		trail3.placeBreadCrumb();   //to reassign the domain back to the variable
		for(int i = 0; i< originalSize; i++)
		{
			maxval = LCVmax(v);
			
			values.add(0,maxval);  //newer values at the beginning (older values in the back)
			v.removeValueFromDomain(maxval); //temporarily deleted the maxval so it does not repeat
			
		}
	
		trail3.undo();
		
		return values; //returns value in LCV order
	}
	/**
	 * My LCV supplemental function
	 */
	public int LCVmax(Variable v)
	{
		
		int fin = 0;
		int max = 0;
		int count = 0;
		int bef = 0;
		int after = 0;
		
		//for each value, it checks for the value that effects the most unassigned neighbors if it is assigned to the input variable
		for(Integer i : getValuesInOrder(v))
			{
				count = 0;
				neighbors = network.getNeighborsOfVariable(v);
				for(int j = 0; j < neighbors.size(); j++)
				{
					if(!neighbors.get(j).isAssigned())
					{
						bef = neighbors.get(j).size();
						trail4.placeBreadCrumb();
						neighbors.get(j).removeValueFromDomain(i);
						after = neighbors.get(j).size();
						trail4.undo();

						if(after < bef)
						{
							count++;
						}
					}
						
				}
					
				if(count >= max)
				{
					fin = i;      //to store the value that causes most "conflicts" with neighboring variables
					max = count;
				}
					
					
			}
				
		return fin;  //returns it to maxval in getValuesLCVOrder function
	}
	
	
	/**
	 * Called when solver finds a solution
	 */
	private void success()
	{
		hasSolution = true;
		sudokuGrid = Converter.ConstraintNetworkToSudokuFile(network, sudokuGrid.getN(), sudokuGrid.getP(), sudokuGrid.getQ());
	}

	//===============================================================================
	// Solver
	//===============================================================================

	/**
	 * Method to start the solver
	 */
	public void solve()
	{
		startTime = System.currentTimeMillis();
		
		switch(cChecks)
		{
		case None: 				;
		break;
		case ForwardChecking: 	ConstraintPropagation();;// 
		break;
		case FC_CP:             ;
		case ArcConsistency: 	;
		break;
		}
		
		switch(varHeuristics)
		{
		case None: 					;
		break;
		case MinimumRemainingValue: ;
		break;
		case Degree:				;
		break;
		case MRV_Degree:			ConstraintPropagation();
		break;
		}
		try {
			solve(0);
		}catch (VariableSelectionException e)
		{
			//System.out.println("error with variable selection heuristic.");
			hasError = true; //my code. instead of printing a statement, it records an error which would report to the output log file
		}
		endTime = System.currentTimeMillis();
		
		Trail.clearTrail();
	}

	/**
	 * Solver
	 * @param level How deep the solver is in its recursion. 
	 * @throws VariableSelectionException 
	 */


	private void solve(int level) throws VariableSelectionException
	{
		if(!Thread.currentThread().isInterrupted())

		{//Check if assignment is completed
			if(hasSolution)
			{
				return;
			}

			//Select unassigned variable
			Variable v = selectNextVariable();		

			//check if the assignment is complete
			if(v == null)
			{
				for(Variable var : network.getVariables())
				{
					if(!var.isAssigned())
					{
						throw new VariableSelectionException("Something happened with the variable selection heuristic");
					}	
				}
				success();
				return;
			}

			//loop through the values of the variable being checked LCV
			
			for(Integer i : getNextValues(v))
			{
				trail.placeBreadCrumb();

				//check a value
				v.updateDomain(new Domain(i));
				numAssignments++;
				boolean isConsistent = checkConsistency(v);
				
				//move to the next assignment
				if(isConsistent)
				{	
					solve(level + 1);
				}

				//if this assignment failed at any stage, backtrack
				if(!hasSolution)
				{
					trail.undo();
					numBacktracks++;
					lastChecked = System.currentTimeMillis();  //my code, last recorded time encountered incase of timelimit
				}
				
				else
				{
					return;
				}
			}	
		}	
	}
				
	@Override
	public void run() {
		solve();
	}
}
