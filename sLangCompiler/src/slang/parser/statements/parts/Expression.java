package slang.parser.statements.parts;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.parser.SyntaxErrorException;
import slang.parser.Utilities;
import slang.parser.Variable;
import slang.parser.statements.expressionstats.Functioncall;
import slang.parser.statements.expressionstats.UnaryOperatorExpression;

public abstract class Expression
{
	private BinaryOperator operator;
	private Expression second;
	
	public Expression getSecondExpression()
	{
		return second;
	}

	public BinaryOperator getOperation()
	{
		return operator;
	}
	
	public enum BinaryOperator
	{
		PLUS, MINUS, MULTIPLY, DIVIDE, AND, XOR, OR, GREATER, LESS, EQUALS
	}
	
	public static Expression build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Expression expression = null;
		int indexBefore = tokens.nextIndex();
		
		try
		{
			expression = slang.parser.literals.Character.build(tokens);
		} catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(expression == null)
		{
			try
			{
				expression = Functioncall.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(expression == null)
		{
			try
			{
				expression = slang.parser.literals.Number.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(expression == null)
		{
			try
			{
				expression = UnaryOperatorExpression.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(expression == null)
		{
			try
			{
				expression = Variable.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(expression == null)
		{
			throw new SyntaxErrorException("expression expected", tokens.next().getLinePos());
		}
		
		return expression;
	}
}
