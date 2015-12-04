package slang.parser.statements;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.parser.Statement;
import slang.parser.SyntaxErrorException;
import slang.parser.Utilities;
import slang.parser.statements.structures.IfStruct;
import slang.parser.statements.structures.WhileStruct;

public class Structure implements Statement
{
	public static Structure build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Structure structure = null;
		int indexBefore = tokens.nextIndex();
		
		try
		{
			structure = IfStruct.build(tokens);
		} catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(structure == null)
		{
			try
			{
				structure = WhileStruct.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(structure == null)
		{
			throw new ParseException("could not create structure", -1);
		}
		
		return null;
	}
}
