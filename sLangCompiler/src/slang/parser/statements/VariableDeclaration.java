package slang.parser.statements;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Datatype;
import slang.parser.Statement;
import slang.parser.Variable;
import slang.parser.exceptions.SyntaxErrorException;

public class VariableDeclaration implements Statement
{
	private Variable variable;
	
	private VariableDeclaration(Variable variable)
	{
		this.variable = variable;
	}
	
	public Datatype getType()
	{
		return variable.getType();
	}
	public String getName()
	{
		return variable.getName();
	}
	
	public Variable getVariable()
	{
		return variable;
	}
	
	public static VariableDeclaration build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.DATATYPE)
		{
			throw new ParseException("datatype expected", first.getLinePos());			
		}
		
		Token second = tokens.next();
		if(second.getType() != TokenType.NAME)
		{
			throw new ParseException("name expected", second.getLinePos());			
		}
		
		Datatype type = Datatype.findDatatype(first.getRepresentation());
		Variable var;
		try
		{
			var = new Variable(type, second.getRepresentation());
		} catch(SyntaxErrorException e)
		{
			throw new SyntaxErrorException(e.getMessage(), second.getLinePos());
		} catch(ParseException e)
		{
			throw new ParseException(e.getMessage(), first.getLinePos());
		}
		
		return new VariableDeclaration(var);
	}
	
	@Override
	public String toString()
	{
		return variable.getType() + " " + variable.getName();
	}
}
