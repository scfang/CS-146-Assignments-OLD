/**
 * Scott Fang
 * Project 2
 * CS 146
 * Dr. Juan Gomez
 *
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;


/*
 * ADT  that follows a binary structure.
 */
public class minMaxHeap 
{
	
	Integer[] heap; //Array to hold units
	boolean levels[]; //Boolean for min or max level spot 
	//true indicates min
	//false indicates max
	int heapSize; //current number of items in heap (not to be confused with size of heap array)

	/*
	 * Main method handles file scanning and commands. Primarily uses a switch-case loop.
	 */
	public static void main(String arg[]) throws FileNotFoundException
	{
		if( arg.length > 0) {
			String s = arg[0];
			File fyle = new File(s);
			Scanner f = new Scanner(fyle);
			minMaxHeap theHeap = new minMaxHeap();
			while (f.hasNextLine())
			{
				String line = f.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(line);
				String command = "";
				if (tokenizer.hasMoreTokens())
					 command = tokenizer.nextToken();

				switch(command)
				{
				case "buildMinMaxHeap:":
				case "buildMinMaxHeap":
					Scanner f2 = new Scanner(line);
					f2.useDelimiter(" |, *");
					ArrayList<Integer> list = new ArrayList<Integer>();
					String str = "";
					while (f2.hasNext())
					{
						str = f2.next();
						if (str.matches((".*\\d+.*")))
							list.add(Integer.parseInt(str));
						
					}
					theHeap.buildMinMaxHeap(list);
					break;
				case "insert":
					str = tokenizer.nextToken();
					theHeap.insert(Integer.parseInt(str));
					break;
				case "peekMin":
					System.out.println(theHeap.peekMin());
					break;
				case "peekMax":
					System.out.println(theHeap.peekMax());
					break;
				case "deleteMin":
					theHeap.deleteMin();
					break;
				case "deleteMax":
					theHeap.deleteMax();
					break;
				case "printMinMaxHeap":
					theHeap.printMinMaxHeap();
					break;
				default:
					break;
				}
				
			}
			return;
		}

	}
	
	/*
	 * Initializes the heap array and boolean array
	 */
	public void buildMinMaxHeap (ArrayList<Integer> num)
	{
		if(num.size() < 10001)
			heap = new Integer[10000]; 
		else
			heap = new Integer[num.size() * 2];
		levels = new boolean [heap.length + 1];
		heapSize = 0;
		
		levels[0] = true;
		for(int i = 1; i < levels.length - 1; i++)
		{
			//System.out.println(i + " " + levels[i]);
			int left, right;
			left = (i * 2) + 1;
			right = (i * 2) + 2;
			int parent = (i % 2 == 0) ? ((left / 2) - 1) : (right / 2); 
			boolean setLevel = !levels[parent];
			if(left < levels.length)
			{
				levels[left] = setLevel;
			}
			if(right < levels.length)
			{
				levels[right] = setLevel;
			}
		}
		
		for(int i = 0; i < num.size(); i++)
		{
			insert(num.get(i));
			
		}

	}
	
	/*
	 * Inserts numbers in min-max order
	 */
	public void insert(int num)
	{
		if (heapSize == 0)
		{
			heap[0] = num;
			heapSize++;
			return;
		}
		
		
		if (heapSize + 1 > heap.length)
		{
			//Seriously?!
			System.exit(0);
		}
	
		heap[heapSize] = num;
			
		if (levels[heapSize] == true) //min level
		{				
			int parentIndex = getParent(heapSize);
			
			if(num > heap[parentIndex])
			{
				heap[heapSize] = heap[parentIndex]; // Swap elements if we see num > parent value
				heap[parentIndex] = num;
				percolate(parentIndex, false); //Max level
				//bubble up
			}
			else
			{
				percolate(heapSize, true);
			}
			
		}
		else //max level
		{
			int parentIndex = getParent(heapSize);
			
			if(num < heap[parentIndex])
			{
				heap[heapSize] = heap[parentIndex]; // Swap elements if we see num < parent value
				heap[parentIndex] = num;
				percolate(parentIndex, true); //Min level
				//bubble up
			}
			else
			{
				percolate(heapSize, false);
			}
		}
			
		heapSize++;
	}
	
	/*
	 * Prints the heap; prints one leverl per line delimited by space.
	 */
	public void printMinMaxHeap()
	{
		//Queue<Integer> q = new LinkedList<Integer>();
		boolean level = true;
		for (int i = 0; i < heapSize; i++)
		{
			if(level != levels[i])
			{
				level = !level;
				System.out.print("\n");
			}
			
			System.out.print(heap[i] + " ");

		}
		System.out.println();
	}
	
	/*
	 * Helper method to assist in moving elements up the structure
	 * 
	 * @param an index to percolate
	 */
	private void percolate(int index, boolean level)
	{
		if(index == 0 || index == 1 || index == 2)
		{
			return;
		}
		//Parent of current index
		int parentIndex = getParent(index);
		int grandParent = getParent(parentIndex);
		if(!level)
		{
			if (heap[index] <= heap[grandParent])
				return;
			else
			{
				int temp = heap[index];
				heap[index] = heap[grandParent];
				heap[grandParent] = temp;
				percolate(grandParent, level);
			}
			return;
		}
	
		if(level)
		{
			if (heap[index] >= heap[grandParent])
				return;
			else
			{
				int temp = heap[index];
				heap[index] = heap[grandParent];
				heap[grandParent] = temp;
				percolate(grandParent, level);
			}
			return;
		}
		
	}
	
	/*
	 * Returns the parent index of a given index.
	 */
	private int getParent(int index)
	{
		int number = ((index) % 2 == 0) ? ((index / 2) - 1) : ((index - 1) / 2);
		return number;
	}
	
	/*
	 * Helper function to allow value to move down the structure.
	 */
	private void trickle(int index, boolean level)
	{
		int left = (index * 2) + 1;
		int right = (index * 2) + 2;
		
		int leftLeft = (left * 2) + 1;
		int leftRight = (left * 2) + 2;
		int rightLeft = (right * 2) + 1;
		int rightRight = (right * 2) + 2;
		ArrayList<Integer> list = isChild(left,right,leftLeft,leftRight,rightLeft, rightRight); //list of indexes
		if(list.isEmpty())
		{
			return;
		}
		int small = findSmallest(list); //Index of smallest value
		int large = findLargest(list); //Largest
		
		if(level) //min level
		{
			if ( getParent(small) == index) //smallest value is child of index
			{
				if (heap[small] < heap[index])
				{
					int temp = heap[small];
					heap[small] = heap[index];
					heap[index] = temp;
					return;
				}
				
			}
			else
			{
				if (heap[small] >= heap[index])
				{
					return;
				}
				
				int temp = heap[index];
				heap[index] = heap[small];
				heap[small] = temp;
				
				int parent = getParent(small);
				if(heap[small] > heap[parent])
				{
					int temp2 = heap[small];
					heap[small] = heap[parent];
					heap[parent] = temp2;
				}
				trickle(small, level);
			}
			
		}
		
		if(!level) //max
		{
			if ( getParent(large) == index) //largest value is child of index
			{
				if (heap[large] > heap[index])
				{
					int temp = heap[large];
					heap[large] = heap[index];
					heap[index] = temp;
					return;
				}
				
			}
			else
			{
				if (heap[large] <= heap[index])
				{
					return;
				}
				
				int temp = heap[index];
				heap[index] = heap[large];
				heap[large] = temp;
				
				int parent = getParent(large);
				if(heap[large] < heap[parent])
				{
					int temp2 = heap[large];
					heap[large] = heap[parent];
					heap[parent] = temp2;
				}
				trickle(large, level);
			}
		}
		
	}
	
	


	/*
	 * Helper function that determines whether a given set of indices are valid children/grandchilden of an index
	 */
	private ArrayList<Integer> isChild(int...arr )
	{
		ArrayList<Integer> list = new ArrayList<>();
		for(Integer i : arr)
		{
			if (i < heapSize)
			{
				list.add(i);
			}
		}
		
		return list;
	}
	
	/*
	 * Finds the smalles value in arraylist; used in conjunction with isChild.
	 */
	private int findSmallest(ArrayList<Integer> arr) 
	{
		int smallest = heap[arr.get(0)]; //smallest value in heap
		int k = arr.get(0);
		int i = 0;
		for ( i = 0; i < arr.size(); i++)
		{
			int val = heap[arr.get(i)];
				if (val < smallest)
				{
					smallest = val;
					k = arr.get(i);
				}
		}
		return k;
	}
	/*
	 * Finds the largest value in arraylist; used in conjunction with isChild.
	 */
	private int findLargest(ArrayList<Integer> arr) {
		int largest = heap[arr.get(0)]; //smallest value in heap
		int k = arr.get(0);
		int i = 0;
		for ( i = 0; i < arr.size(); i++)
		{
			int val = heap[arr.get(i)];
				if (val > largest)
				{
					largest = val;
					k = arr.get(i);
				}
		}
		return k;
	}
	
	/*
	 * Returns but does not remove the smallest element in structure.
	 */
	public int peekMin()
	{
		if (heapSize > 0)
			return heap[0];
		else
			return (Integer) null;
	}
	
	/*
	 * Returns but does not remove the largest element in structure.
	 */
	public int peekMax()
	{
		if (heapSize < 1)
		{
			return (Integer) null;
		}
		
		if (heapSize == 1)
		{
			return heap[0];
		}
		else if (heapSize == 2)
		{
			return heap[1];
		}
		else 
		{
			if (heap[1] >= heap[2])
			{
				return heap[1];
			}
			else
				return heap[2];
		}
	}
	
	/*
	 * Returns and removes the smallest element in structure.
	 */
	public int deleteMin()
	{ 
			int num = heap[0];
			heap[0] = null;
			
			if (heapSize == 1)
			{
				heapSize--;
				return num;
			}
			else 
			{
				heapSize--;
				heap[0] = heap[heapSize];
				heap[heapSize] = null;
				trickle(0, true);
				return num;
			}
	}
	
	/*
	 * Returns and removes the largest element in structure.
	 */
	public int deleteMax()
	{
		if (heapSize < 1)
		{
			return (Integer) null;
		}
		else if (heapSize == 1)
		{
			int num = heap[0];
			heap[0] = null;
			heapSize--;
			return num;
		}
		else if (heapSize == 2)
		{
			int num = heap[1];
			heap[1] = null;
			heapSize--;
			return num;
		}
		else
		{
			int max = 0;
			heapSize--;
			if (heap[1] >= heap[2])
			{
				max = heap[1];
				heap[1] = heap[heapSize];
				heap[heapSize] = null;
				trickle(1, false);
				
			}
			else
			{
				max = heap[2];
				heap[2] = heap[heapSize];
				heap[heapSize] = null;
				trickle(2, false);
			}
			return max;
		}
	}
	
}
