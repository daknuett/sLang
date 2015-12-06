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

public class WhileStruct extends Structure
{
	private Expression condition;
	private Statement body;
	
	private WhileStruct(Expression condition, Statement body)
	{
		this.condition = condition;
		this.body = body;
	}

	public static WhileStruct build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		Token first = tokens.next();
		if(first.getType() != TokenType.WHILE)
			throw new ParseException("while expected", first.getLinePos());
		
		Token second = tokens.next();
		if(second.getType() != TokenType.BRACKET_OPEN)
			throw new SyntaxErrorException("( expected", second.getLinePos());
		
		Expression condition = Expression.build(tokens);
		
		Token third = tokens.next();
		if(third.getType() != TokenType.BRACKET_CLOSE)
			throw new SyntaxErrorException(") expected", third.getLinePos());
		
		Statement body = StatementBuilder.build(tokens);
		
		return new WhileStruct(condition, body);
	}
	
	public Expression getCondition()
	{
		return condition;
	}
	
	public Statement getBody()
	{
		return body;
	}
	
	@Override
	public String toString()
	{
		return "while " + condition + " is 0 repeat\n" + body;
	}
}
