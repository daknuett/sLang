package slang.parser.statements.structures;

import java.text.ParseException;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Statement;
import slang.parser.StatementBuilder;
import slang.parser.exceptions.SyntaxErrorException;
import slang.parser.statements.Structure;
import slang.parser.statements.parts.Expression;

public class IfStruct extends Structure
{
	private Expression condition;
	private Statement trueBody;
	private Statement falseBody;
	
	private IfStruct(Expression condition, Statement trueStatement, Statement falseStatement)
	{
		this.condition = condition;
		trueBody = trueStatement;
		falseBody = falseStatement;
	}

	public static IfStruct build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.IF)
			throw new ParseException("if expected", first.getLinePos());
		
		Token second = tokens.next();
		if(second.getType() != TokenType.BRACKET_OPEN)
			throw new SyntaxErrorException("( expected", second.getLinePos());
		
		Expression condition = Expression.build(tokens);
		
		Token third = tokens.next();
		if(third.getType() != TokenType.BRACKET_CLOSE)
			throw new SyntaxErrorException(") expected", third.getLinePos());
		
		Statement trueStatement = StatementBuilder.build(tokens);
		
		Statement falseStatement = null;
		
		Token fourth = tokens.next();
		if(fourth.getType() == TokenType.ELSE)
		{
			falseStatement = StatementBuilder.build(tokens);
		}
		else tokens.previous();
		
		return new IfStruct(condition, trueStatement, falseStatement);
	}
	
	public Expression getCondition()
	{
		return condition;
	}
	public Statement getTrueBody()
	{
		return trueBody;
	}
	public Statement getFalseBody()
	{
		return falseBody;
	}
	
	@Override
	public String toString()
	{
		return "if " + condition + " is 0 do\n" + trueBody + "else do\n" + falseBody;
	}
}
