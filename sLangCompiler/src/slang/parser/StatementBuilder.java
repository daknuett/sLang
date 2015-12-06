package slang.parser;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.exceptions.DeclInitException;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.Block;
import slang.parser.statements.ExpressionStatementBuilder;
import slang.parser.statements.Return;
import slang.parser.statements.Structure;
import slang.parser.statements.VariableDeclaration;
import slang.parser.statements.expressionstats.Assignment;


public class StatementBuilder
{
	public static Statement build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Statement statement = null;
		int indexBefore = tokens.nextIndex();

		boolean semicolonNotNecessary = false;

		try
		{
			statement = Structure.build(tokens);
			semicolonNotNecessary = true;
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
			Block block = null;
			try
			{
				block = new Block(Function.getCurrentFunction().getBody());
				block.readBlock(tokens);
				block.leave();
				statement = block;
				semicolonNotNecessary = true;
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, indexBefore);
				block.delete();
			}
		}

		if(statement == null)
		{
			try
			{
				statement = VariableDeclaration.build(tokens);
				int indexBeforeInit = tokens.nextIndex();
				try
				{
					Assignment assign = Assignment.build(tokens, ((VariableDeclaration) statement).getVariable(), false);
					
					Token semicolon = tokens.next();
					if(semicolon.getType() != TokenType.SEMICOLON)
						throw new SyntaxErrorException("; missing", semicolon.getLinePos());
					
					throw new DeclInitException(indexBefore, (VariableDeclaration) statement, assign);
				} catch(ParseException e)
				{
					Utilities.gotoIndex(tokens, indexBeforeInit);
				}
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

		if(!semicolonNotNecessary)
		{
			Token first = tokens.next();
			if(first.getType() != TokenType.SEMICOLON)
				if(statement == null)
					throw new ParseException("statement expected", first.getLinePos());
				else
					throw new SyntaxErrorException("; missing", first.getLinePos());
		}

		return statement;
	}
}
