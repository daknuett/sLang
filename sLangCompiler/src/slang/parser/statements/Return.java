package slang.parser.statements;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Datatype;
import slang.parser.Function;
import slang.parser.Statement;
import slang.parser.Utilities;
import slang.parser.exceptions.NoExpressionException;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.parts.Expression;

public class Return implements Statement
{
	private Expression retValue;
	
	private Return(Expression expression)
	{
		retValue = expression;
	}

	public Expression getRetValue()
	{
		return retValue;
	}

	public static Return build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.RETURN)
			throw new ParseException("return expected", first.getLinePos());
		
		int indexBefore = tokens.nextIndex();
		Expression expression = null;
		try
		{
			expression = Expression.build(tokens);
		}
		catch(NoExpressionException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(Function.getCurrentFunction().getRetType() != Datatype.VOID)
		{
			if(expression == null)
				throw new SyntaxErrorException("return value expected", first.getLinePos());
		}
		else
		{
			if(expression != null)
				throw new SyntaxErrorException("return type is void", first.getLinePos()); 
		}
		return new Return(expression);
	}
	
	@Override
	public String toString()
	{
		return retValue + "is returned by this function";
	}
}
