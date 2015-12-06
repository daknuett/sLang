package slang.parser.statements;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.parser.Utilities;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.expressionstats.Assignment;
import slang.parser.statements.expressionstats.Functioncall;
import slang.parser.statements.expressionstats.UnaryOperatorExpression;

public class ExpressionStatementBuilder
{
	public static ExpressionStatement build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		ExpressionStatement statement = null;
		int indexBefore = tokens.nextIndex();
		
		try
		{
			statement = Assignment.build(tokens);
		} catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(statement == null)
		{
			try
			{
				statement = Functioncall.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(statement == null)
		{
			try
			{
				statement = UnaryOperatorExpression.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(statement == null)
		{
			throw new ParseException("could not create expression statement", -1);
		}
		
		return statement;
	}
}
