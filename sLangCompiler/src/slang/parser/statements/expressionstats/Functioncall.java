package slang.parser.statements.expressionstats;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Function;
import slang.parser.SyntaxErrorException;
import slang.parser.Utilities;
import slang.parser.statements.ExpressionStatement;
import slang.parser.statements.parts.Expression;

public class Functioncall extends Expression implements ExpressionStatement
{
	private Function function;
	private Expression[] params;
	
	public Functioncall(Function function, Expression[] params)
	{
		this.function = function;
		this.params = params;
	}
	
	public Function getFunction()
	{
		return function;
	}
	public Expression[] getParameters()
	{
		return params;
	}
	
	public static Functioncall build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.NAME)
			throw new ParseException("name expected", first.getLinePos());
		
		Function function = Function.get(first.getRepresentation());
		if(function == null)
		{
			throw new SyntaxErrorException("Could not find function " + first.getRepresentation(), first.getLinePos());
		}
		
		Token second = tokens.next();
		if(second.getType() != TokenType.BRACKET_OPEN)
		{
			throw new ParseException("( expected", first.getLinePos());
		}
		
		ArrayList<Expression> parameters = new ArrayList<Expression>();
		int indexBefore = tokens.nextIndex();
		try
		{
			Token komma;
			while(true)
			{
				indexBefore = tokens.nextIndex();
				parameters.add(Expression.build(tokens));
				komma = tokens.next();
				if(komma.getType() != TokenType.KOMMA)
					throw new SyntaxErrorException(", expected", komma.getLinePos());
				
			}
		}
		catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(parameters.size() != function.getParamCount())
			throw new SyntaxErrorException("Parameters don't match", second.getLinePos());
		
		Token third = tokens.next();
		if(third.getType() != TokenType.BRACKET_CLOSE)
		{
			throw new SyntaxErrorException(") expected", third.getLinePos());
		}
		
		return new Functioncall(function, parameters.toArray(new Expression[parameters.size()]));
	}
}
