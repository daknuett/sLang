package slang.parser.statements.parts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Utilities;
import slang.parser.exceptions.SyntaxErrorException;

public class ArithmeticExpression extends Expression
{
	private BinaryOperator operator;
	private Expression first;
	private Expression second;
	
	public ArithmeticExpression(Expression first, Expression second, BinaryOperator operator)
	{
		this.first = first;
		this.second = second;
		this.operator = operator;
	}

	public Expression getFirstExpression()
	{
		return first;
	}
	
	public Expression getSecondExpression()
	{
		return second;
	}

	public BinaryOperator getOperation()
	{
		return operator;
	}
	
	public static void main(String... args)
	{
		LinkedList<Token> tokens = new LinkedList<Token>();
		tokens.add(new Token("(", TokenType.BRACKET_OPEN, 0));
		tokens.add(new Token("(", TokenType.BRACKET_OPEN, 1));
		tokens.add(new Token("6", TokenType.NUMBER, 2));
		tokens.add(new Token("+", TokenType.BIN_OPERATOR, 3));
		tokens.add(new Token("3", TokenType.NUMBER, 4));
		tokens.add(new Token("*", TokenType.BIN_OPERATOR, 5));
		tokens.add(new Token("2", TokenType.NUMBER, 6));
		tokens.add(new Token(")", TokenType.BRACKET_CLOSE, 7));
		tokens.add(new Token("/", TokenType.BIN_OPERATOR, 8));
		tokens.add(new Token("2", TokenType.NUMBER, 9));
		tokens.add(new Token(")", TokenType.BRACKET_CLOSE, 10));
		tokens.add(new Token("-", TokenType.BIN_OPERATOR, 11));
		tokens.add(new Token("(", TokenType.BRACKET_OPEN, 12));
		tokens.add(new Token("3", TokenType.NUMBER, 13));
		tokens.add(new Token("+", TokenType.BIN_OPERATOR, 14));
		tokens.add(new Token("1", TokenType.NUMBER, 15));
		tokens.add(new Token(")", TokenType.BRACKET_CLOSE, 16));
		tokens.add(new Token("==", TokenType.BIN_OPERATOR, 16));
		tokens.add(new Token("1", TokenType.NUMBER, 16));
		tokens.add(new Token(")", TokenType.BRACKET_CLOSE, 17));
		
		try
		{
			System.out.println(Expression.build(tokens.listIterator()));
		} catch(SyntaxErrorException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage() + " at " + e.getPos());
		} catch(ParseException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static Expression build(ListIterator<Token> tokens) throws ParseException, SyntaxErrorException
	{
		ArrayList<Expression> expressions = new ArrayList<Expression>();
		ArrayList<Token> operators = new ArrayList<Token>();
		
		int bracketStack = readBrackets(operators, tokens, true, 0);
		
		expressions.add(Expression.readSingleExpression(tokens));
		
		int indexBefore = tokens.nextIndex();
		try
		{
			while(true)
			{
				indexBefore = tokens.nextIndex();
				
				Token next = tokens.next();
				if(next.getType() == TokenType.BIN_OPERATOR)
					operators.add(next);
				else
					throw new ParseException("operator expected", next.getLinePos());
				
				bracketStack += readBrackets(operators, tokens, true, bracketStack);
				
				expressions.add(Expression.readSingleExpression(tokens));
				
				bracketStack += readBrackets(operators, tokens, false, bracketStack);
			}
		} catch(ParseException e)
		{
			Utilities.gotoIndex(tokens, indexBefore);
		}
		
		bracketStack += readBrackets(operators, tokens, false, bracketStack);
		
		ArrayList<BinaryOperator> bOperators = new ArrayList<BinaryOperator>(operators.size());
		for(Token t : operators)
		{
			bOperators.add(BinaryOperator.findOperator(t.getRepresentation()));
		}
		
		return divideInExpressions(expressions, bOperators);
	}
	
	private static Expression divideInExpressions(List<Expression> expressions, List<BinaryOperator> operators)
	{				
		if(expressions.size() == 1) 
			return expressions.get(0);
		
		int bracketCount = 0;
		int bracketLayer = 0;
		
		int minValue = Integer.MAX_VALUE;
		int minIndex = -1;
		int minBracketCount = 0;
		for(int i = 0; i < operators.size(); i++)
		{
			if(operators.get(i) == BinaryOperator.BRACKET_OPEN)
			{
				bracketCount++;
				bracketLayer++;
			}
			else if(operators.get(i) == BinaryOperator.BRACKET_CLOSE)
			{
				bracketCount++;
				bracketLayer--;
			}
			else
			{
				int value = bracketLayer * 100 + operators.get(i).orderIndex;
				if(value < minValue)
				{
					minValue = value;
					minIndex = i;
					minBracketCount = bracketCount;
				}				
			}
		}
		
		int expressionBorder = minIndex - minBracketCount + 1;
		Expression first = divideInExpressions(expressions.subList(0, expressionBorder), operators.subList(0, minIndex));
		Expression second = divideInExpressions(expressions.subList(expressionBorder, expressions.size()), operators.subList(minIndex + 1, operators.size()));
		return new ArithmeticExpression(first, second, operators.get(minIndex));
	}

	private static int readBrackets(ArrayList<Token> buffer, ListIterator<Token> tokens, boolean open, int bracketNum)
	{
		int bracketStack = 0;
		int indexBeforeBracket = tokens.nextIndex();
		Token next = tokens.next();
		while(next.getType() == (open ? TokenType.BRACKET_OPEN : TokenType.BRACKET_CLOSE))
		{
			if(!open && bracketStack == bracketNum)
				break;
			buffer.add(next);
			bracketStack++;
			indexBeforeBracket = tokens.nextIndex();
			next = tokens.next();
		}
		Utilities.gotoIndex(tokens, indexBeforeBracket);
		
		return open ? bracketStack : 0 - bracketStack;
	}
	
	public String toString()
	{
		return "[" + first + " " + operator + " " + second + "]";
	}
}
