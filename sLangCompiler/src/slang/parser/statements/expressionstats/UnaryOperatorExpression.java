package slang.parser.statements.expressionstats;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Variable;
import slang.parser.statements.ExpressionStatement;
import slang.parser.statements.parts.Expression;


public class UnaryOperatorExpression extends Expression implements ExpressionStatement
{
	private Variable var;
	private UnaryOperator operator;

	private UnaryOperatorExpression(Variable var, UnaryOperator operator)
	{
		this.var = var;
		this.operator = operator;
	}

	public Variable getVariable()
	{
		return var;
	}

	public UnaryOperator getOperator()
	{
		return operator;
	}

	public static UnaryOperatorExpression build(ListIterator<Token> tokens) throws ParseException
	{
		boolean wasPre = false;
		String operator = "";
		String varName = "";
		Token first = tokens.next();
		if(first.getType() == TokenType.UN_OPERATOR)
		{
			wasPre = true;
			operator = first.getRepresentation();
		}
		else if(first.getType() == TokenType.NAME)
			varName = first.getRepresentation();
		else
			throw new ParseException("name or operator expected", first.getLinePos());

		Token second = tokens.next();
		if(!wasPre)
		{
			if(second.getType() == TokenType.UN_OPERATOR)
				operator = second.getRepresentation();
			else
				throw new ParseException("unary operator expected", second.getLinePos());
		}
		else if(wasPre)
		{
			if(second.getType() == TokenType.NAME)
				varName = second.getRepresentation();
			else
				throw new ParseException("name expected", second.getLinePos());
		}

		Variable var = Variable.getFromCurrent(varName);
		if(var == null)
		{
			throw new ParseException("variable " + first.getRepresentation() + " could not be found", first.getLinePos());
		}
		return new UnaryOperatorExpression(var, UnaryOperator.findOperator(second.getRepresentation(), wasPre));
	}

	public enum UnaryOperator
	{
		INCREMENT_PRE, INCREMENT_POST, DECREMENT_PRE, DECREMENT_POST;

		public static UnaryOperator findOperator(String representation, boolean pre)
		{
			switch(representation)
			{
				case "++":
					return pre ? INCREMENT_PRE : INCREMENT_POST;
				case "--":
					return pre ? DECREMENT_PRE : DECREMENT_POST;
				default:
					return null;
			}
		}
	}
	
	@Override
	public String toString()
	{
		return var + " " + operator;
	}
}
