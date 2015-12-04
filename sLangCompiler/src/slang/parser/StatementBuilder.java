package slang.parser;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.statements.Block;
import slang.parser.statements.ExpressionStatementBuilder;
import slang.parser.statements.Return;
import slang.parser.statements.Structure;
import slang.parser.statements.VariableDeclaration;

public class StatementBuilder
{
	public static Statement build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Statement statement = null;
		int indexBefore = tokens.nextIndex();
		
		try
		{
			statement = Structure.build(tokens);
		} catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		if(statement == null)
		{
			try
			{
				statement = ExpressionStatementBuilder.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(statement == null)
		{
			try
			{
				Block block = new Block(Function.getCurrentFunction().getBody());
				block.readBlock(tokens);
				statement = block;
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(statement == null)
		{
			try
			{
				statement = VariableDeclaration.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		if(statement == null)
		{
			try
			{
				statement = Return.build(tokens);
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
			}
		}
		
		Token first = tokens.next();
		if(first.getType() != TokenType.SEMICOLON)
			throw new SyntaxErrorException("; missing", first.getLinePos());
		
		
		return statement;
	}
}
