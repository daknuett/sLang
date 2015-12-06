package slang.parser.statements.expressionstats;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Variable;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.ExpressionStatement;
import slang.parser.statements.parts.Expression;


public class Assignment implements ExpressionStatement
{
	private Variable target;
	private Expression value;

	private Assignment(Variable target, Expression value)
	{
		this.target = target;
		this.value = value;
	}

	public Variable getTarget()
	{
		return target;
	}

	public Expression getValue()
	{
		return value;
	}

	public static Assignment build(ListIterator<Token> tokens) throws ParseException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.NAME)
			throw new ParseException("name expected", first.getLinePos());

		Variable target = Variable.getFromCurrent(first.getRepresentation());
		if(target == null){ throw new ParseException("variable " + first.getRepresentation() + " not found", first.getLinePos()); }

		try
		{
			return build(tokens, target, true);
		} catch(SyntaxErrorException e)
		{
			// not possible
			return null;
		}
	}

	public static Assignment build(ListIterator<Token> tokens, Variable target, boolean shortAllowed) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		boolean isShortAssign = first.getType() == TokenType.SHORT_ASSIGN_OPERATOR;
		if(!shortAllowed && isShortAssign)
			throw new SyntaxErrorException(first.getRepresentation() + " is no allowed here", first.getLinePos());
		if(first.getType() != TokenType.ASSIGN_OPERATOR && !isShortAssign)
			throw new ParseException("assign operator expected", first.getLinePos());

		Expression value = Expression.build(tokens);

		return new Assignment(target, value);
	}
	
	@Override
	public String toString()
	{
		return target + " = " + value;
	}
}
