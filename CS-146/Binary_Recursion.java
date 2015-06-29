
/**
 * Scott Fang
 * Assignment 1
 * CS 146
 * Dr. Juan Gomez
 * 
 */
public class Binary_Recursion {

	/*
	 * Main method for testing and debugging
	 */
	public static void main(String[] args) 
	{

		System.out.println(countOnes(555));
		System.out.println(Integer.toBinaryString(555)); //Built-in Java method for converting numbers to binary easily
		
	}
	
	private static int numberOfOnes = 0;
	
	/*
	 * A method that will recursively count the number of "1s" in the binary representation of a number
	 * 
	 * @param the number to count the ones of in its binary representation
	 */
	public static int countOnes(int number)
	{
		
		if (number == 0) //Base case ends function if n = 0
		{
			return numberOfOnes; 
		}
		
		int digitValue = number % 2; //n mod 2 will return a 0 or 1
		if (digitValue == 1)
		{
			numberOfOnes++; // Increments counter if n mod 2 is 1
		}
		return countOnes(number / 2); //recursively calls function
	}

}
