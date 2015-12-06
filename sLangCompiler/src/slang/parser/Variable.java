package slang.parser;

import java.text.ParseException;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.Block;
import slang.parser.statements.parts.Expression;

public class Variable extends Expression		//TODO finish Variable view
{
	private static Map<String, Variable> variables = new TreeMap<String, Variable>();
	private static Variable lastAdded;
	
	private static Block currentBlock = Block.ROOT;
	
	private String name;
	private Datatype type;
	private Block block;
	
	public Variable(Datatype type, String name) throws ParseException, SyntaxErrorException
	{
		this.type = type;
		this.name = name;
		block = currentBlock;
		
		Variable var = variables.get(name);
		if(var != null && var.block == currentBlock)
		{
			throw new ParseException("variable already exists", -1);			
		}
		else if(!Utilities.checkName(name))
		{
			throw new SyntaxErrorException(name + " is not a valid name", -1);			
		}
		else
		{
			variables.put(name, this);
			lastAdded = this;
		}	
	}

	public String getName()
	{
		return name;
	}
	public Datatype getType()
	{
		return type;
	}
	
	public Block getBlock()
	{
		return block;
	}
	
	public static void joinBlock(Block block)
	{
		currentBlock = block;
	}
	
	public static Block getCurrentBlock()
	{
		return currentBlock;
	}

	public static Variable getLast()
	{
		return lastAdded;
	}

	public static boolean existsInCurrent(String representation)
	{
		Variable var = variables.get(representation);
		return var != null && var.getBlock().isVisibleIn(currentBlock);
	}

	public static Variable getFromCurrent(String representation)
	{
		Variable var = variables.get(representation);
		return var != null ? (var.getBlock().isVisibleIn(currentBlock) ? var : null) : null;
	}
	
	public static Variable build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.NAME)
		{
			throw new ParseException("name expected", first.getLinePos());
		}
		
		Variable var = getFromCurrent(first.getRepresentation());
		if(var == null)
			throw new SyntaxErrorException("variable " + first.getRepresentation() + " could not be found", first.getLinePos());
		
		return var;
	}
	
	public String toString()
	{
		return "var " + name + ":" + type + " (from " + block.getBlockNumber() + ")";
	}
}
