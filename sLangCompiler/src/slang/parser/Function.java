package slang.parser;

import java.text.ParseException;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.statements.Block;

public class Function
{
	private static Map<String, Function> functions = new TreeMap<String, Function>();
	
	private static Function currentFunction;
	
	private String name;
	private int paramCount;
	private Datatype retType;
	private Block body;
	
	private Function(String name, int paramCount, Datatype retType, Block body)
	{
		this.name = name;
		this.paramCount = paramCount;
		this.retType = retType;
		this.body = body;
		
		currentFunction = this;
	}
	
	public String getName()
	{
		return name;
	}
	public int getParamCount()
	{
		return paramCount;
	}
	public Datatype getRetType()
	{
		return retType;
	}
	public Block getBody()
	{
		return body;
	}
	
	public static Function build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.FUNCTION)
			throw new ParseException("function expected", first.getLinePos());
		
		Token second = tokens.next();
		if(second.getType() != TokenType.DATATYPE || second.getType() != TokenType.VOID)
			throw new SyntaxErrorException("datatype expected", second.getLinePos());
		
		Token third = tokens.next();
		if(third.getType() != TokenType.NAME)
			throw new SyntaxErrorException("name expected", third.getLinePos());
		
		if(!Utilities.checkName(third.getRepresentation()))
				throw new SyntaxErrorException(third.getRepresentation() + " is no valid function name", third.getLinePos());
		
		Token fourth = tokens.next();
		if(fourth.getType() != TokenType.BRACKET_OPEN)
			throw new SyntaxErrorException("( expected", fourth.getLinePos());
		
		Block block = new Block(Block.ROOT);
		
		int paramCount = 0;
		try
		{
			paramCount = block.readFunctionParameters(tokens);			
		}
		catch(ParseException e)
		{
			throw new SyntaxErrorException(e.getMessage(), e.getErrorOffset());
		}
		
		Token fifth = tokens.next();
		if(fifth.getType() != TokenType.BRACKET_CLOSE)
			throw new SyntaxErrorException(") expected", fifth.getLinePos());
		
		block.readBlock(tokens);
		
		return new Function(third.getRepresentation(), paramCount, Datatype.findDatatype(second.getRepresentation()), block);
	}
	
	public static Function get(String representation)
	{
		return functions.get(representation);
	}
	
	public static Function getCurrentFunction()
	{
		return currentFunction;
	}
}
