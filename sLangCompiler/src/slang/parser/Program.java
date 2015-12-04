package slang.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import slang.lexer.Token;
import slang.lexer.TokenType;
import slang.parser.Function;
import slang.parser.statements.VariableDeclaration;
import slang.parser.statements.expressionstats.Assignment;


public class Program
{
	private ArrayList<VariableDeclaration> decls = new ArrayList<VariableDeclaration>();
	private ArrayList<Assignment> assignments = new ArrayList<Assignment>();
	private ArrayList<Function> functions = new ArrayList<Function>();
	
	private Program(){}
	
	public VariableDeclaration[] getVariableDeclarations()
	{
		return decls.toArray(new VariableDeclaration[decls.size()]);
	}

	public Assignment[] getAssignments()
	{
		return assignments.toArray(new Assignment[assignments.size()]);
	}

	public Function[] getFunctions()
	{
		return functions.toArray(new Function[functions.size()]);
	}

	public static Program build(ListIterator<Token> tokens) throws SyntaxErrorException
	{
		Program p = new Program();
		boolean doneSomething = true;
		int nextIndex;
		while(tokens.hasNext() && doneSomething)
		{
			doneSomething = false;
			nextIndex = tokens.nextIndex();
			
			try
			{
				p.decls.add(VariableDeclaration.build(tokens));
				doneSomething = true;
				int indexBefore = tokens.nextIndex();
				try
				{
					p.assignments.add(Assignment.build(tokens, Variable.getLast(), false));					
				} catch(ParseException e)
				{
					Utilities.gotoIndex(tokens, indexBefore);
				}
				
				Token lineEnd = tokens.next();
				if(lineEnd.getType() != TokenType.SEMICOLON)
					throw new SyntaxErrorException("; expected", lineEnd.getLinePos());
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, nextIndex);
			}
			
			try
			{
				p.functions.add(Function.build(tokens));
				doneSomething = true;
			} catch(ParseException e)
			{
				Utilities.gotoIndex(tokens, nextIndex);
			}
		}
		if(tokens.hasNext())
		{
			throw new SyntaxErrorException("Unexpected ending", tokens.previous().getLinePos());
		}
		return p;
	}
}
