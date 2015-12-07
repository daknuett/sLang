package slang.parser;

import java.util.ListIterator;

public class Utilities
{	
	public static void gotoIndex(ListIterator<?> iterator, int index)
	{
		int steps = index - iterator.nextIndex();
		if(steps < 0)
		{
			steps = 0 - steps;
			
			for(int i = 0; i < steps; i++)
			{
				iterator.previous();
			}
		}
		else
		{
			for(int i = 0; i < steps; i++)
			{
				iterator.next();
			}
		}
	}

	public static boolean checkName(String representation)
	{
		return true;	//TODO check for valid names
	}
}
