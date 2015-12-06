package slang.parser.exceptions;

public class SyntaxErrorException extends Exception
{

	private static final long serialVersionUID = 1L;
	
	private int pos;
	
	public SyntaxErrorException(String error, int pos)
	{
		super(error);
		this.pos = pos;
	}
	
	public int getPos()
	{
		return pos;
	}
}
