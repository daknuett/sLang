package slang.parser.statements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Statement;
import slang.parser.StatementBuilder;
import slang.parser.Utilities;
import slang.parser.Variable;
import slang.parser.exceptions.DeclInitException;
import slang.parser.exceptions.SyntaxErrorException;

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
	
	private Block() 
	{
		blockNumber = maxBlockNumber++;
		Variable.joinBlock(this);
	}
	
	public Block(Block upper)
	{
		this();
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
				paramCount++;
				Token komma = tokens.next();
				if(komma.getType() != TokenType.KOMMA)
					if(komma.getType() == TokenType.BRACKET_CLOSE)
					{
						tokens.previous();
						break;
					}
					else
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
				try
				{
					statements.add(StatementBuilder.build(tokens));					
				}
				catch(DeclInitException e)
				{
					statements.add(e.getDeclaration());
					statements.add(e.getInitilization());
				}
			}
		}
		catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		Token second = tokens.next();
		if(second.getType() != TokenType.BLOCK_CLOSE)
			throw new SyntaxErrorException("} expected", second.getLinePos());
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

	public boolean isVisibleIn(Block currentBlock)	//TODO fill isVisibleIn method
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		str.append(blockNumber + "{\n");
		
		for(Statement s : statements)
		{
			str.append(s + "\n");
		}
		
		str.append("\n" + blockNumber + "}");
		
		return str.toString();
	}

	public void delete()
	{
		maxBlockNumber--;
		leave();
	}
	
	public void leave()
	{		
		Variable.joinBlock(upper);
	}
}
