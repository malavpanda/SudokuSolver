package sudoku;



public class SolutionReader {
    public static StringBuilder reader(String sudokuString, int theN, int theP, int theQ) {
    	
    	StringBuilder sb = new StringBuilder();
    	String s = sudokuString;
    	char c;
    	int nlen = String.valueOf(theN).length();
    	int plen = String.valueOf(theP).length();
    	int qlen = String.valueOf(theQ).length();
    	int sum = nlen + plen + qlen;
    	for (int i = 11 + sum ; i < s.length(); i++) //use the sum to skip over insignificant values to get to the solution
    	{
    		
    	    c = s.charAt(i);
    	    if(Character.isDigit(c))     //get the sudoku solution in specified output format
    	    	sb.append(c + ",");   	//for numbers
    	    if(Character.isAlphabetic(c))
    	    	sb.append(c + ",");     //for Alphabets
    	    
    	} 
    	
    	int len = sb.length();
    	sb.deleteCharAt(len-1); //delete the last comma
    	
    	return sb;
    }
}
