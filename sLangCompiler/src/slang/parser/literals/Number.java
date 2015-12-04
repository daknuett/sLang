package slang.parser.literals;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.statements.parts.Expression;

public class Number extends Expression
{
	private int value;
	
	private Number(int parseInt)
	{
		value = parseInt;
	}

	public int getValue()
	{
		return value;
	}
	
	public static Number build(ListIterator<Token> tokens) throws ParseException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.NUMBER)
		{
			throw new ParseException("digits expected", first.getLinePos());
		}
		
		return new Number(Integer.parseInt(first.getRepresentation()));
	}
}
