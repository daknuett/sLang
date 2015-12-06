package slang.parser.literals;

import java.text.ParseException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.statements.parts.Expression;

public class Character extends Expression
{
	private String character;
	
	private Character(String character)
	{
		this.character = character;
	}
	
	public String getCharacter()
	{
		return character;
	}
	
	public static Character build(ListIterator<Token> tokens) throws ParseException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.APOSTROPHE)
			throw new ParseException("' expected", first.getLinePos());
		
		StringBuilder characters = new StringBuilder();
		Token next = tokens.next();
		while(next.getType() != TokenType.APOSTROPHE)
		{
			characters.append(next.getRepresentation());
			try
			{
				next = tokens.next();				
			}
			catch(NoSuchElementException e)
			{
				throw new ParseException("' to close char expected", first.getLinePos());
			}
		}
		
		return new Character(characters.toString());
	}
	
	@Override
	public String toString()
	{
		return "'" + character + "'";
	}
}
