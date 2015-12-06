package slang.parser.statements.expressionstats;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Function;
import slang.parser.Utilities;
import slang.parser.exceptions.NoExpressionException;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.ExpressionStatement;
import slang.parser.statements.parts.Expression;

public class Functioncall extends Expression implements ExpressionStatement
{
	private static ArrayList<Functioncall> functioncalls = new ArrayList<Functioncall>();
	
	private Function function;
	private Expression[] params;
	private Token functionname;
	
	private Functioncall(Token functionname, Expression[] params)
	{
		this.functionname = functionname;
		this.params = params;
		
		functioncalls.add(this);
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
				try
				{
					parameters.add(Expression.build(tokens));					
				}
				catch(NoExpressionException e)
				{
					Utilities.gotoIndex(tokens, indexBefore);
				}
				komma = tokens.next();
				if(komma.getType() != TokenType.KOMMA)
					if(komma.getType() == TokenType.BRACKET_CLOSE)
					{						
						break;
					}
					else
						throw new SyntaxErrorException(") expected", komma.getLinePos());
				
			}
		}
		catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		return new Functioncall(first, parameters.toArray(new Expression[parameters.size()]));
	}
	
	public static void checkForFunctionExistance() throws SyntaxErrorException
	{
		for(Functioncall f : functioncalls)
		{
			Function function = Function.get(f.functionname.getRepresentation());
			if(function == null)
				throw new SyntaxErrorException("Could not find function " + f.functionname.getRepresentation(), f.functionname.getLinePos());
			
			if(f.params.length != function.getParamCount())
				throw new SyntaxErrorException("Parameters don't match", f.functionname.getLinePos());
			
			f.function = function;
		}
	}
	
	 @Override
	 public String toString()
	 {
		 StringBuilder str = new StringBuilder();
		 
		 str.append(function.getName() + "(");
		 
		 for(Expression e : params)
			 str.append("<" + e + ">");
		 
		 str.append("):" + function.getRetType());
		 
		 return str.toString();
	 }
}
