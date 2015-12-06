package slang.parser.exceptions;


public class NoExpressionException extends SyntaxErrorException
{

	private static final long serialVersionUID = 1L;

	public NoExpressionException(int pos)
	{
		super("expression expected", pos);
	}

}
