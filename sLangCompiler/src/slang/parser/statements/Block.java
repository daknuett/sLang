package slang.parser.statements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Statement;
import slang.parser.StatementBuilder;
import slang.parser.SyntaxErrorException;
import slang.parser.Utilities;

public class Block implements Statement
{
	private static int maxBlockNumber;
	
	public static final Block ROOT;
	
	static
	{
		ROOT = new Block();
		ROOT.upper = ROOT;
	}
	
	private ArrayList<Statement> statements = new ArrayList<Statement>();
	private int blockNumber;
	private Block upper;
	
	private Block() {}
	
	public Block(Block upper)
	{
		blockNumber = maxBlockNumber++;
		this.upper = upper;
	}
	
	/**
	 * @return number of the params read
	 */
	public int readFunctionParameters(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		int paramCount = 0;
		
		int indexBefore = tokens.nextIndex();
		try
		{
			while(true)
			{
				indexBefore = tokens.nextIndex();
				statements.add(VariableDeclaration.build(tokens));
				Token komma = tokens.next();
				if(komma.getType() != TokenType.KOMMA)
					throw new SyntaxErrorException(", expected", komma.getLinePos());
			}
		}
		catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		return paramCount;
	}
	
	public void readBlock(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.BLOCK_OPEN)
			throw new ParseException("{ expected", first.getLinePos());
		
		int indexBefore = tokens.nextIndex();
		try
		{
			while(true)
			{
				indexBefore = tokens.nextIndex();
				statements.add(StatementBuilder.build(tokens));
			}
		}
		catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
	}
	
	public Statement[] getStatements()
	{
		return statements.toArray(new Statement[statements.size()]);
	}
	
	public int getBlockNumber()
	{
		return blockNumber;
	}
	
	public Block getUpper()
	{
		return upper;
	}

	public boolean isVisibleIn(Block currentBlock)	//TODO
	{
		return false;
	}
}
