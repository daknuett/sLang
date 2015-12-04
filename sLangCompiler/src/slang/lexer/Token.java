package slang.lexer;

public class Token implements Comparable<Token>
{
	private String representation;
	private TokenType type;
	private int pos;
	
	public Token(String representation, TokenType type, int pos)
	{
		this.representation = representation;
		this.type = type;
		this.pos = pos;
	}
	
	public String getRepresentation()
	{
		return representation;
	}
	
	public TokenType getType()
	{
		return type;
	}
	
	public int getPos()
	{
		return pos;
	}
	
	@Override
	public String toString()
	{
		//TODO delete the next lines
		
		//just for better looking output
		String tempRep = representation;
		if(tempRep.length() == 1)
		{
			switch(representation.charAt(0))
			{
				case '\n': tempRep = "\\n"; break;
				case '\t': tempRep = "\\t"; break;
				case '\r': tempRep = "\\r"; break;
			}
		}
		
		return "[" + type.toString() + "  <" + tempRep + ">  " + pos + "]";
	}

	@Override
	public int compareTo(Token other)
	{
		return this.pos - other.pos;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this.pos == ((Token) o).pos;
	}

	public int getLinePos()	//TODO
	{
		return 0;
	}
}
