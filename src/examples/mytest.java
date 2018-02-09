package examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import cspSolver.BTSolver;
import cspSolver.BTSolver.ConsistencyCheck;
import cspSolver.BTSolver.ValueSelectionHeuristic;
import cspSolver.BTSolver.VariableSelectionHeuristic;

import sudoku.SolutionReader;
import sudoku.SudokuBoardReader;
import sudoku.SudokuFile;

public class mytest {

	public static void main(String[] args)
	{
		long totalStart = System.currentTimeMillis();
		int timeout = Integer.parseInt(args[2]); //time limit input(in seconds)
		timeout *= 1000; //covert timeout into milliseconds
		//int timeout = 1000000;
		double currentTime;
		String token1, token2, token3, token4;  
		
		SudokuFile SudokuFileFromFile = SudokuBoardReader.readFile(args[0]); //input file "ExampleSudokuFiles/PE1.txt" 
		BTSolver solver = new BTSolver(SudokuFileFromFile);
		
		Boolean mrv_used = false;
		//for token1
		try
		{
			token1 = args[3].toString();
			if(token1.equals("FC") || token1.equals("fc"))
			{
				System.out.println("Forward Checking was used");
				solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
			}
			else if(token1.equals("FC_CP") || token1.equals("fc_cp"))
			{
				System.out.println("FC_CP was used");
				solver.setConsistencyChecks(ConsistencyCheck.FC_CP);
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
			}
			else if(token1.equals("MRV") || token1.equals("mrv"))
			{
				System.out.println("MRV was used");
				mrv_used = true;
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
				solver.setConsistencyChecks(ConsistencyCheck.None);
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
			}
			else if(token1.equals("DH") || token1.equals("dh"))
			{
				System.out.println("DH was used");
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.Degree);
				solver.setConsistencyChecks(ConsistencyCheck.None);
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
			}
			else if(token1.equals("LCV") || token1.equals("lcv"))
			{
				System.out.println("LCV was used");
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.LeastConstrainingValue);
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
				solver.setConsistencyChecks(ConsistencyCheck.None);
			}
			else
			{
				System.out.println("Backtracking was used");
				solver.setConsistencyChecks(ConsistencyCheck.None);
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
				
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("BT was used");
			solver.setConsistencyChecks(ConsistencyCheck.None);
			solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
			solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
		}
		
		//for token2
		try
		{
			token2 = args[4].toString();
			if(token2.equals("FC") || token2.equals("fc"))
			{
				System.out.println("Forward Checking was used");
				solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
			}
			else if(token2.equals("FC_CP") || token2.equals("fc_cp"))
			{
				System.out.println("FC_CP was used");
				solver.setConsistencyChecks(ConsistencyCheck.FC_CP);
			}
			else if(token2.equals("MRV") || token2.equals("mrv"))
			{
				System.out.println("MRV was used");
				mrv_used = true;
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
			}
			else if(token2.equals("DH") || token2.equals("dh"))
			{
				if(mrv_used)
				{
					System.out.println("MRV + DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MRV_Degree);
				}
				else
				{
					System.out.println("DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.Degree);
				}
			}
			else if(token2.equals("LCV") || token2.equals("lcv"))
			{
				System.out.println("LCV was used");
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.LeastConstrainingValue);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//System.out.println("BT was used");
			//solver.setConsistencyChecks(ConsistencyCheck.None);
		}
		
		//for token3
		try
		{
			token3 = args[5].toString();
			if(token3.equals("FC") || token3.equals("fc"))
			{
				System.out.println("Forward Checking was used");
				solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
			}
			else if(token3.equals("FC_CP") || token3.equals("fc_cp"))
			{
				System.out.println("FC_CP was used");
				solver.setConsistencyChecks(ConsistencyCheck.FC_CP);
			}
			else if(token3.equals("MRV") || token3.equals("mrv"))
			{
				System.out.println("MRV was used");
				mrv_used = true;
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
			}
			else if(token3.equals("DH") || token3.equals("dh"))
			{
				if(mrv_used)
				{
					System.out.println("MRV + DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MRV_Degree);
				}
				else
				{
					System.out.println("DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.Degree);
				}
			}
			else if(token3.equals("LCV") || token3.equals("lcv"))
			{
				System.out.println("LCV was used");
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.LeastConstrainingValue);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
		}
		
		//for token4
		try
		{
			token4 = args[6].toString();
			if(token4.equals("FC") || token4.equals("fc"))
			{
				System.out.println("Forward Checking was used");
				solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
			}
			else if(token4.equals("FC_CP") || token4.equals("fc_cp"))
			{
				System.out.println("FC_CP was used");
				solver.setConsistencyChecks(ConsistencyCheck.FC_CP);
			}
			else if(token4.equals("MRV") || token4.equals("mrv"))
			{
				System.out.println("MRV was used");
				mrv_used = true;
				solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
			}
			else if(token4.equals("DH") || token4.equals("dh"))
			{
				if(mrv_used)
				{
					System.out.println("MRV + DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MRV_Degree);
				}
				else
				{
					System.out.println("DH was used");
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.Degree);
				}
			}
			else if(token4.equals("LCV") || token4.equals("lcv"))
			{
				System.out.println("LCV was used");
				solver.setValueSelectionHeuristic(ValueSelectionHeuristic.LeastConstrainingValue);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
		}
		
		Thread t1 = new Thread(solver);
		
		try
		{
			t1.start();
			t1.join(timeout); //parameter is timeout
			if(t1.isAlive())
			{
				t1.interrupt();
			}
		}catch(InterruptedException e)
		{
		}
		
		
		
		File results = new File(args[1]); //output file  
		
		long prepStart=0;
		long prepDone=0;
		double searchStart;
		double searchDone;
		double lastChecked;
		double solutionTime;
		int numAssignments;
		int numBacktracks;
		boolean error;
		
		searchStart = solver.getstartTimeTaken()/1000.0;  //converting from milliseconds to seconds
		searchDone = solver.getendTimeTaken()/1000.0;     
		solutionTime = solver.getTimeTaken()/1000.0;
		lastChecked = solver.getlastChecked()/1000.0;
		numAssignments = solver.getNumAssignments();
		numBacktracks = solver.getNumBacktracks();
		error = solver.isError();
		
		String sep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		sb.append("TOTAL_START=" + totalStart/1000.0);
		sb.append(sep);
		sb.append("PREPROCESSING_START=" + prepStart);
		sb.append(sep);
		sb.append("PREPROCESSING_DONE=" + prepDone);
		sb.append(sep);
		sb.append("SEARCH_START=" + searchStart );
		sb.append(sep);
		
		if(!solver.hasSolution() & !error) //if there is a timeout, checks time it encountered timelimit
			searchDone = lastChecked;
		
		sb.append("SEARCH_DONE=" + searchDone);
		sb.append(sep);
		
		if(!solver.hasSolution())
			solutionTime = 0;
		
		sb.append("SOLUTION_TIME=" + solutionTime );
		sb.append(sep);
		
		if(solver.hasSolution())
		{
			sb.append("STATUS=success");
			sb.append(sep);
		}
		else if(error)
		{
			sb.append("STATUS=error");
			sb.append(sep);
		}
		else
		{
			sb.append("STATUS=timeout");
			sb.append(sep);
		}
		
		int N,p,q;
			N = SudokuFileFromFile.getN();
			p = SudokuFileFromFile.getP();
			q = SudokuFileFromFile.getQ();
			
		if(solver.hasSolution())
		{
			
			try {
				
				FileWriter fw = new FileWriter(results);
				sb.append("SOLUTION=(");
				fw.write(sb.toString());
				System.out.print(solver.getSolution());
				StringBuilder sb2 = new StringBuilder();
				sb2 = SolutionReader.reader(solver.getSolution().toString(), N, p ,q);
				sb2.append(")");
				fw.write(sb2.toString());
				StringBuilder sb1 = new StringBuilder();
				sb1.append(sep);
				sb1.append("COUNT_NODES=" + numAssignments);
				sb1.append(sep);
				sb1.append("COUNT_DEADENDS=" + numBacktracks);
				fw.write(sb1.toString());
				System.out.println("COUNT_NODES=" + numAssignments);
				System.out.println("COUNT_DEADENDS=" + numBacktracks);
				fw.flush();
				fw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		else
		{
			FileWriter fw;
			try
				{
					fw = new FileWriter(results);
					sb.append("SOLUTION=(");
					fw.write(sb.toString());
					currentTime = System.currentTimeMillis();
					
					//System.out.print(solver.getSolution()); //for testing purposes
					
					if(!error & currentTime-totalStart < timeout) //if there was no proven solution found 
					{ //creates 0 NxN tuples
						StringBuilder sb2 = new StringBuilder();
						for (int i = 0 ; i < (N*N)+1 ; i++){
			    	   	sb2.append("0,");
						} 
						sb2.deleteCharAt(sb2.length()-1);
						fw.write(sb2.toString());
					}
					StringBuilder sb1 = new StringBuilder();
					sb1.append(")");
					sb1.append(sep);
					sb1.append("COUNT_NODES=" + numAssignments);
					sb1.append(sep);
					sb1.append("COUNT_DEADENDS=" + numBacktracks);
					fw.write(sb1.toString());
					fw.flush();
					fw.close();
				}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("COUNT_NODES=" + numAssignments);
			System.out.println("COUNT_DEADENDS=" + numBacktracks);
		}
		
		
	}
}