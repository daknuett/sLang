package slang.parser.exceptions;

import slang.parser.statements.VariableDeclaration;
import slang.parser.statements.expressionstats.Assignment;

public class DeclInitException extends SyntaxErrorException
{	
	private static final long serialVersionUID = 1L;
	
	private VariableDeclaration decl;
	private Assignment assign;

	public DeclInitException(int pos, VariableDeclaration decl, Assignment assign)
	{
		super("declaring and initialising a variable in one statement is not allowed here", pos);
		this.decl = decl;
		this.assign = assign;
	}
	
	public VariableDeclaration getDeclaration()
	{
		return decl;
	}
	
	public Assignment getInitilization()
	{
		return assign;
	}

}
