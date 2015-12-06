package slang.parser.statements.parts;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.parser.Utilities;
import slang.parser.Variable;
import slang.parser.exceptions.NoExpressionException;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.expressionstats.Functioncall;
import slang.parser.statements.expressionstats.UnaryOperatorExpression;

public abstract class Expression
{

	public enum BinaryOperator
	{
		PLUS("+", 2), MINUS("-", 2), MULTIPLY("*", 3), DIVIDE("/", 3), AND("&", 1), XOR("^", 1), OR("|", 1), GREATER(">", 0), LESS("<", 0), EQUALS("==", 0), BRACKET_OPEN("(", -1), BRACKET_CLOSE(")", -1);

		String representation;
		int orderIndex;
		BinaryOperator(String representation, int orderIndex)
		{
			this.representation = representation;
			this.orderIndex = orderIndex;
		}
		
		public static BinaryOperator findOperator(String representation)
		{
			for(BinaryOperator o : BinaryOperator.values())
			{
				if(o.representation.equals(representation))
					return o;
			}
			return null;
			
		}
		
		public String toString()
		{
			return representation;
		}
	}
	
	public static Expression build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		return ArithmeticExpression.build(tokens);
	}
	
	public static Expression readSingleExpression(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException	//TODO NEUES VERFAHREN mit Arithmetic Exception(höherer Abstractionsgrad)
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
			throw new NoExpressionException(tokens.next().getLinePos());
		}
		
		return expression;
	}
}
